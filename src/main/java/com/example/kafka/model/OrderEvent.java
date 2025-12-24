package com.example.kafka.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable-style event representing a change in order state.
 * Used as the message payload sent through Kafka.
 */
public class OrderEvent {

    private String eventId;
    private String eventType;
    private String orderId;
    private long timestamp;
    private String payload;

    // Required by Jackson
    public OrderEvent() {
    }

    /**
     * Factory method for creating a new order event.
     */
    public static OrderEvent create(String eventType, String orderId, String payload) {
        OrderEvent event = new OrderEvent();
        event.eventId = UUID.randomUUID().toString();
        event.eventType = eventType;
        event.orderId = orderId;
        event.timestamp = Instant.now().toEpochMilli();
        event.payload = payload;
        return event;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getOrderId() {
        return orderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPayload() {
        return payload;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
