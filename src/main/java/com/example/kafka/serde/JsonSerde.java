package com.example.kafka.serde;

import com.example.kafka.model.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple JSON serialization helper.
 * Intentionally avoids Kafka Serializer SPI to keep behavior explicit.
 */
public class JsonSerde {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String serialize(OrderEvent event) {
        try {
            return MAPPER.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize OrderEvent", e);
        }
    }

    public static OrderEvent deserialize(String json) {
        try {
            return MAPPER.readValue(json, OrderEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize OrderEvent", e);
        }
    }
}
