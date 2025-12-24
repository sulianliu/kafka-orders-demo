package com.example.kafka.store;

import com.example.kafka.model.OrderEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simulates a persistent store with idempotent writes.
 * Tracks processed eventIds to prevent duplicate side effects.
 */
public class InMemoryOrderStore {

    private final Map<String, String> orderStateById = new HashMap<>();
    private final Set<String> processedEventIds = new HashSet<>();

    /**
     * Applies an event if it has not been processed before.
     *
     * @return true if the event was applied, false if it was a duplicate
     */
    public boolean applyEvent(OrderEvent event) {

        if (processedEventIds.contains(event.getEventId())) {
            return false;
        }

        orderStateById.put(event.getOrderId(), event.getEventType());
        processedEventIds.add(event.getEventId());
        return true;
    }
}
