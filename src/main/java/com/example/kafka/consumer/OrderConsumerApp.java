package com.example.kafka.consumer;

import com.example.kafka.model.OrderEvent;
import com.example.kafka.serde.JsonSerde;
import com.example.kafka.store.InMemoryOrderStore;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderConsumerApp {

    private static final String TOPIC = "orders.events.v1";

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "orders-consumer-group-demo-");

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        // Disable auto-commit so offset management is fully explicit
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // For demo purposes: if no offset exists, start from the beginning
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList(TOPIC));

        System.out.println("Waiting for partition assignment...");

        // Partition assignment happens lazily during poll()
        while (consumer.assignment().isEmpty()) {
            consumer.poll(Duration.ofMillis(100));
        }

        Set<TopicPartition> assignedPartitions = consumer.assignment();
        System.out.println("Assigned partitions: " + assignedPartitions);

        // Force a deterministic starting position for this demo
        consumer.seekToBeginning(assignedPartitions);
        System.out.println("Seeked to beginning of assigned partitions");

        System.out.println("Consumer started, polling for records...");

        InMemoryOrderStore store = new InMemoryOrderStore();
        AtomicInteger processedCount = new AtomicInteger();

        try {
            while (true) {
                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofSeconds(1));

                if (records.isEmpty()) {
                    continue;
                }

                try {
                    for (ConsumerRecord<String, String> record : records) {

                        OrderEvent event = JsonSerde.deserialize(record.value());
                        boolean applied = store.applyEvent(event);

                        System.out.printf(
                                "Consumed partition=%d offset=%d orderId=%s eventId=%s action=%s%n",
                                record.partition(),
                                record.offset(),
                                record.key(),
                                event.getEventId(),
                                applied ? "APPLY" : "SKIP_DUPLICATE"
                        );

                        // Inject a deterministic failure every 7 records
                        if (processedCount.incrementAndGet() % 7 == 0) {
                            throw new RuntimeException("Simulated failure");
                        }
                    }

                    // Commit offsets only after successful processing
                    consumer.commitSync();
                    processedCount.set(0);
                    System.out.println("Offsets committed");

                } catch (Exception e) {
                    // Failure path:
                    // - offsets are NOT committed
                    // - records will be replayed
                    System.err.println(
                            "Processing failed, offsets not committed: " + e.getMessage()
                    );

                    // Backoff to avoid tight failure loops
                    Thread.sleep(1000);
                }
            }
        } finally {
            consumer.close();
        }
    }
}
