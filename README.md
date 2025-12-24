# kafka-orders-demo

This repository demonstrates core Apache Kafka delivery semantics using the native Java client,
without Spring or higher-level frameworks.

The goal is to make Kafka behavior explicit and observable, rather than abstracted away.

## What This Project Demonstrates

- Partition-based ordering using message keys (orderId)
- At-least-once delivery semantics
- Manual offset management
- Consumer failure and message replay
- Idempotent consumption using eventId
- Practical trade-offs between reliability and simplicity

## Why Pure Java (No Spring)

This project intentionally avoids Spring Kafka to:
- Expose Kafka’s native APIs and lifecycle
- Make offset commit behavior explicit
- Observe failure and replay mechanics directly
- Remove framework-level abstractions that hide semantics

## High-Level Design

- Producer publishes order events keyed by orderId
- Kafka ensures per-order ordering via partitions
- Consumer processes events and stores state in memory
- Failures are intentionally injected to demonstrate replay
- Idempotency prevents duplicate side effects

## How to Run

(Will be documented step by step)

## Observations

This project is intended as an exploratory and learning tool.
Behavior is best understood by running the consumer, observing logs,
and restarting after simulated failures.
