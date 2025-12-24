package com.example.kafka.producer;

import com.example.kafka.model.OrderEvent;
import com.example.kafka.serde.JsonSerde;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class OrderProducerApp {

    private static final String TOPIC = "orders.events.v1";

    public static void main(String[] args) {

        Properties props = getProperties();

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {

            // Produce a small batch of events with repeating orderIds
            // Events with the same orderId will always go to the same partition
            for (int i = 1; i <= 10; i++) {
                String orderId = "ORDER-" + (i % 3);

                OrderEvent event = OrderEvent.create(
                        "OrderCreated",
                        orderId,
                        "{ \"amount\": 100 }"
                );

                ProducerRecord<String, String> record =
                        new ProducerRecord<>(
                                TOPIC,
                                orderId, // key used for partitioning and ordering
                                JsonSerde.serialize(event)
                        );

                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        System.out.printf(
                                "Produced eventId=%s orderId=%s partition=%d offset=%d%n",
                                event.getEventId(),
                                orderId,
                                metadata.partition(),
                                metadata.offset()
                        );
                    }
                });
            }
        }
    }

    private static Properties getProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Use String serializers; the message value is JSON
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Reliability-focused producer configuration
        // - acks=all ensures data is replicated before success is returned
        // - idempotence prevents duplicate records during retries
        // - retries enabled to handle transient failures
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        return props;
    }
}
