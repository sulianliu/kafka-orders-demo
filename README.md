# kafka-orders-demo

This repository demonstrates core Apache Kafka delivery semantics using the native Java client,
without Spring or higher-level frameworks.

The goal is to make Kafka behavior explicit and observable rather than abstracted away by frameworks.

---

## What This Project Demonstrates

This project intentionally focuses on *behavior*, not convenience APIs.

It demonstrates:

- Partition-based ordering using message keys (`orderId`)
- At-least-once delivery semantics
- Manual offset management
- Consumer failure and message replay
- Idempotent consumption using `eventId`
- Practical trade-offs between reliability and simplicity

---

## Why Pure Java (No Spring)

This project intentionally avoids Spring Kafka in order to:

- Expose Kafka’s native APIs and lifecycle
- Make offset commit behavior explicit
- Observe failure and replay mechanics directly
- Avoid framework abstractions that hide delivery semantics

This makes the project suitable for learning, experimentation, and architectural discussion.

---

## High-Level Design

- A producer publishes order events keyed by `orderId`
- Kafka uses the key to route events to partitions
- Ordering is guaranteed **per order**, not globally
- A consumer processes events and stores state in memory
- Failures are intentionally injected during processing
- Offsets are committed **only after successful processing**
- Idempotency prevents duplicate side effects during replay

---

## Delivery Semantics

- **Producer**
    - `acks=all`
    - Idempotence enabled
    - Retries enabled

- **Consumer**
    - Auto-commit disabled
    - Offsets committed manually
    - Commit occurs only after successful processing

This results in **at-least-once delivery**, which is the default and most common Kafka usage pattern.
Duplicate delivery is expected and handled via idempotent processing.

---

## Failure and Replay

The consumer intentionally injects failures during processing.

When a failure occurs:

- Offsets are **not committed**
- The consumer continues running
- The same records are re-polled
- Previously processed events are skipped using idempotency

This allows replay to be observed safely and deterministically.

---

## Observed Behaviors

By running the producer and consumer, you can directly observe:

- Events with the same `orderId` always land in the same partition
- Offsets increase monotonically within a partition
- Consumer crashes do not lose data
- Replay occurs after failures
- Idempotency prevents duplicate side effects

These behaviors are visible directly in the application logs.

---

## How to Run

### Prerequisites

- Java 17
- Docker (for running Kafka)

### Start Kafka (Docker, KRaft mode)

```bash
docker compose up -d
