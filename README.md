# Fund Transfer System

## Introduction

Welcome to the Fund Transfer System, a project designed to demonstrate a robust and scalable solution for handling
financial transactions. This document provides an overview of the system's architecture, which has been implemented in
two distinct versions: a traditional **Monolith** and a modern **Distributed** (microservices) system.

We will explore the key design principles used, including **Domain-Driven Design (DDD)** for code organization, a *
*pessimistic locking** strategy for concurrency management, and the use of **RabbitMQ** for reliable asynchronous
communication. The following sections detail these architectural choices and provide comprehensive test scenarios for
both implementations.

## Domain-Driven Design

To maximize code reuse between the monolith and distributed implementations, this system is designed using a
Domain-Driven Design (DDD) approach. The business logic is separated into distinct domains, each responsible for a
specific part of the fund transfer process.

### 1. Accept Domain

**Responsibility:** Initial validation and acceptance of incoming transfer requests.

This domain acts as the first line of defense. Its primary role is to perform initial integrity checks on a transfer
request before it enters the core business logic.

* **Idempotency Check:** Prevents duplicate transactions by verifying the `Idempotency-Key` header.
* **Request Persistence:** All incoming requests are saved. This separation is crucial because a faulty client could
  generate a high volume of invalid requests, and we want to prevent them from propagating further into the system.
* **Outcome:** If a request is formally correct, the domain produces an **`AcceptedTransfer`** event. Otherwise, it
  generates a **`RejectedTransfer`** event.

*(Note: For this demonstration, validation is limited to the idempotency check. However, this domain could easily be
extended to include schema validation, field format checks, and other formal integrity tests.)*

### 2. Registry Domain

**Responsibility:** Core business validation and execution of the fund transfer.

This domain contains the primary business logic for the transfer itself. Unlike the Accept Domain, its validation rules
are tied to the current state of the system (e.g., account balances).

* **Business Validation:** It performs a comprehensive set of checks to ensure the transfer is valid, including:
    * Verifying that the originator and beneficiary are not the same.
    * Confirming that both originator and beneficiary accounts exist.
    * Ensuring an exchange rate is available for the given currency pair.
    * Validating that the transfer amount is a positive value.
    * Checking for sufficient funds in the originator's account.
* **Execution:** If the transfer is valid, the domain executes the transaction by debiting the originator and crediting
  the beneficiary.
* **Auditing:** It produces either a **`SuccessfulTransfer`** or a **`FailedTransfer`** record, both of which are saved
  for auditing purposes.

### 3. Account Domain

**Responsibility:** Manages the lifecycle and state of user accounts.

While the Registry Domain orchestrates the transfer and updates account balances, the Account Domain is the
authoritative source for all account-related operations.

* **Account Management:** It handles creating, deleting, and retrieving account information.
* **Data Integrity:** It ensures that all account data is consistent and valid.

*(Note: For this demonstration, this domain will perform read-only operation. However, this domain could easily be
expanded to include write-operations)*

### Inter-Domain Communication

The communication model between these domains varies depending on the implementation. In the monolith, it involves
direct method calls, while the distributed version will rely on a different pattern (RabbitMQ - asynchronous messaging).
This
will be detailed in a separate section.

---

## Asynchronous Communication with RabbitMQ

For the distributed implementation, **RabbitMQ** was chosen as the message broker for inter-service communication. It
offers several advantages for a financial system, particularly its robust concurrency handling and data safety features.

* **Dead-Letter Queue (DLQ):** RabbitMQ's DLQ capability is crucial for ensuring that no data is lost. If a message
  cannot be processed successfully, it is routed to a DLQ for later analysis and reprocessing, preventing failed
  transfers from disappearing.

The communication topology is designed as follows:

* **Accept -> Registry (Direct Exchange):** When the `accept-distributed` service validates a transfer request, it sends
  a message to a **direct exchange**. This ensures that the message is routed to only **one** instance of the
  `registry-distributed` service. This is vital because all registry instances share a single database, and only one
  should process a given transfer to avoid race conditions.

* **Registry -> Account (Fanout Exchange):** After a transfer is processed, the `registry-distributed` service publishes
  an update message to a **fanout exchange**. This broadcasts the message to **all** instances of the
  `account-distributed` service, allowing each one to update its local data copy and maintain consistency across the
  system.

## Concurrency Management

Concurrency is critical in a fund transfer system to prevent race conditions, such as two transfers overwriting each
other's balance updates. A robust locking strategy is essential to ensure data integrity.

To address this, the system employs **JPA versioned entities** and **pessimistic write locks** at the repository level.
This approach relies on the database to serialize access to account records during a transaction, preventing concurrent
modifications.

This database-centric locking strategy has key implications for the system's architecture:

* **Monolith Implementation:** The database lock effectively manages concurrency within the single application instance.
* **Distributed Implementation:** While the `account-distributed` service can be scaled with independent databases, the
  `registry-distributed` service instances **must share a single database**. This is because the pessimistic lock is
  tied to the database, which acts as the central point of coordination. Similarly, the `accept-distributed` service
  currently relies on a shared database for its idempotency checks.

For advanced scaling scenarios where these services need to operate with independent databases, a more complex
synchronization mechanism, such as a distributed lock manager (e.g., Redis), would be required.

---

## 🧪 Testing the Monolith Implementation

This section outlines the test scenarios executed against the monolith implementation of the fund transfer system.

### Initial Data Setup

For ease of testing, a `data.sql` script is included to clean and populate the database at startup. This script
pre-loads the `account` table with a set of test accounts, ensuring a consistent state for running the scenarios below.

### Running the Application

To test the monolith implementation, run the application using the provided Docker Compose file. From the
`transfer-system` root folder, execute the following command:

    docker-compose -f docker-compose-monolith.yaml up --build

---

## Test Scenarios

The monolith implementation is fully synchronous. When a transfer is initiated, it is executed immediately, and the
final transaction result is returned directly in the response. You can also verify the transfer by fetching the involved
accounts to see their updated balances.

### 1. Retrieve an Account by a Valid Owner ID

* **Scenario:** A `GET` request is made to `/account/{ownerId}` with a valid, existing `ownerId`.
* **Expected Result:** The API should return an `HTTP 200 OK` status and the corresponding account object.

**Request:**

    curl --location 'http://localhost:8080/account/101'

**✅ Expected Response:**

    {
        "ownerId": 101,
        "currency": "EUR",
        "balance": 5000.00
    }

---

### 2. Attempt to Retrieve an Account with an Invalid Owner ID

* **Scenario:** A `GET` request is made to `/account/{ownerId}` with an `ownerId` that does not exist.
* **Expected Result:** The API should return an `HTTP 404 Not Found` status and a descriptive error object.

**Request:**

    curl --location 'http://localhost:8080/account/666'

**✅ Expected Response:**

    {
        "errorCode": "Account not found",
        "message": "Account with ownerId 666 not found",
        "transactionId": null,
        "requestId": null,
        "timestamp": "2025-08-24T13:02:06.458Z",
        "httpStatus": "NOT_FOUND"
    }

---

### 3. Retrieve All Accounts with Default Pagination

* **Scenario:** A `GET` request is made to the `/accounts` endpoint without any pagination parameters.
* **Expected Result:** The API should return an `HTTP 200 OK` status and the first page of account objects.

**Request:**

    curl --location 'http://localhost:8080/accounts'

**✅ Expected Response:**

    {
        "content": [
            { "ownerId": 101, "currency": "EUR", "balance": 5000.00 },
            { "ownerId": 102, "currency": "USD", "balance": 2500.00 },
            { "ownerId": 103, "currency": "GBP", "balance": 10000.00 },
            { "ownerId": 104, "currency": "JPY", "balance": 1500000.00 },
            { "ownerId": 105, "currency": "CAD", "balance": 7500.00 },
            { "ownerId": 106, "currency": "AUD", "balance": 8250.75 },
            { "ownerId": 107, "currency": "CHF", "balance": 12000.00 },
            { "ownerId": 108, "currency": "CNY", "balance": 50000.00 },
            { "ownerId": 109, "currency": "SEK", "balance": 65000.00 },
            { "ownerId": 110, "currency": "NZD", "balance": 9800.50 }
        ],
        "page": {
        "size": 10,
        "number": 0,
        "totalElements": 12,
        "totalPages": 2
        }
    }

---

### 4. Retrieve Accounts with Custom Pagination

* **Scenario:** A `GET` request is made to `/accounts` with specific `page` and `size` query parameters.
* **Expected Result:** The API should return an `HTTP 200 OK` status and a paginated list of accounts corresponding to
  the provided parameters.

**Request:**

    curl --location 'http://localhost:8080/accounts?page=2&size=5'

**✅ Expected Response:**

    {
        "content": [
            { "ownerId": 111, "currency": "XXX", "balance": 1000.00 },
            { "ownerId": 112, "currency": "ZZZ", "balance": 1000.00 }
        ],
        "page": {
        "size": 5,
        "number": 2,
        "totalElements": 12,
        "totalPages": 3
        }
    }

---

### 5. Retrieve Accounts with Invalid Pagination

* **Scenario:** A `GET` request is made to `/accounts` with invalid `page` and `size` query parameters.
* **Expected Result:** The API should return an `HTTP 400 BAD REQUEST` status and a descriptive error object.

**Request:**

    curl --location 'http://localhost:8080/accounts?page=-2&size=5'

**✅ Expected Response:**

    {
        "errorCode": "Invalid request",
        "message": "Page number must be non-negative and page size must be positive.",
        "transactionId": null,
        "requestId": null,
        "timestamp": "2025-08-24T13:22:35.516568635Z",
        "httpStatus": "BAD_REQUEST"
    }

### 6. Perform Fund transfer from strong currency to weak currency

* **Scenario:** A `POST` request is made to `/transfer` with a valid payload from a strong currency to a weak one.
* **Expected Result:** The API should return an `HTTP 200 OK` status and a description of the performed transfer.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 2a9b813f-8e9e-4c2f-8b2a-3d18c81e3a5f' \
    --data '{
        "originatorId": 101,
        "beneficiaryId": 102,
        "amount": 500.00
    }'

**✅ Expected Response:**

    {
        "transferId": "2c6b8104-fc43-4ebc-b51a-f0d722f8aa14",
        "createdAt": "2025-08-24T13:36:21.676204925Z",
        "transferAmount": 500.00,
        "originator": {
            "ownerId": 101,
            "currency": "EUR",
            "balance": 4569.2650000
        },
        "beneficiary": {
            "ownerId": 102,
            "currency": "USD",
            "balance": 3000.00
        },
        "status": "SUCCESS",
        "processedAt": "2025-08-24T13:36:23.063895516Z",
        "exchangeRate": 0.86147,
        "debit": 430.7350000,
        "credit": 500.00,
        "errorCode": null
    }

### 6. Perform Fund transfer from weak currency to strong currency

* **Scenario:** A `POST` request is made to `/transfer` with a valid payload from a weak currency to a strong one.
* **Expected Result:** The API should return an `HTTP 200 OK` status and a description of the performed transfer.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 4b645392-d3ce-46dd-a9bd-f32019c19e2d' \
    --data '{
        "originatorId": 104,
        "beneficiaryId": 105,
        "amount": 100.00
    }'

**✅ Expected Response:**

    {
        "transferId": "25c1e378-fa62-43f6-905f-d9956f4c98d3",
        "createdAt": "2025-08-24T13:40:26.08426563Z",
        "transferAmount": 100.00,
        "originator": {
            "ownerId": 104,
            "currency": "JPY",
            "balance": 1489313.0000
            },
        "beneficiary": {
            "ownerId": 105,
            "currency": "CAD",
            "balance": 7600.00
            },
        "status": "SUCCESS",
        "processedAt": "2025-08-24T13:40:27.182784768Z",
        "exchangeRate": 106.87,
        "debit": 10687.0000,
        "credit": 100.00,
        "errorCode": null
    }

### 7. Duplicated fund transfer

* **Scenario:** A `POST` request is made to `/transfer` with a duplicated payload.
* **Expected Result:** The API should return an `HTTP 409 CONFLICT` status and a descriptive error object.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 4b645392-d3ce-46dd-a9bd-f32019c19e2d' \
    --data '{
        "originatorId": 104,
        "beneficiaryId": 105,
        "amount": 100.00
    }'

**✅ Expected Response:**

    {
        "errorCode": "Duplicated request",
        "message": "Transfer with requestId 4b645392-d3ce-46dd-a9bd-f32019c19e2d is duplicated",
        "transactionId": "25c1e378-fa62-43f6-905f-d9956f4c98d3",
        "requestId": "4b645392-d3ce-46dd-a9bd-f32019c19e2d",
        "timestamp": "2025-08-24T13:44:33.047550306Z",
        "httpStatus": "CONFLICT"
    }

### 8. Same owner and beneficiary

* **Scenario:** A `POST` request is made to `/transfer` with a same owner and beneficiary.
* **Expected Result:** The API should return an `HTTP 400 BAD REQUEST` status and a failed transfer object.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 0e910757-0444-4200-9fc5-bdc4d183aa8c' \
    --data '{
        "originatorId": 101,
        "beneficiaryId": 101,
        "amount": 100.00
    }'

**✅ Expected Response:**

    {
        "transferId": "20b092f5-f36b-4299-9259-1de992c8db90",
        "createdAt": "2025-08-24T13:54:22.569784474Z",
        "transferAmount": null,
        "originator": null,
        "beneficiary": null,
        "status": "FAILED",
        "processedAt": "2025-08-24T13:54:22.569790415Z",
        "exchangeRate": null,
        "debit": null,
        "credit": null,
        "errorCode": "Invalid beneficiary"
    }

### 9. Non-existing owner

* **Scenario:** A `POST` request is made to `/transfer` with a non-existing owner.
* **Expected Result:** The API should return an `HTTP 404 NOT FOUND` status and a failed transfer object.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 8c041771-e9f5-4ff2-b59b-30ec71d798f9' \
    --data '{
        "originatorId": 666,
        "beneficiaryId": 101,
        "amount": 100.00
    }'

**✅ Expected Response:**

    {
        "transferId": "0d8c7f92-3bb7-4c3a-b342-d815609798a2",
        "createdAt": "2025-08-24T13:56:31.241629118Z",
        "transferAmount": null,
        "originator": null,
        "beneficiary": null,
        "status": "FAILED",
        "processedAt": "2025-08-24T13:56:31.241640329Z",
        "exchangeRate": null,
        "debit": null,
        "credit": null,
        "errorCode": "Account not found"
    }

### 10. Non-existing beneficiary

* **Scenario:** A `POST` request is made to `/transfer` with a non-existing beneficiary.
* **Expected Result:** The API should return an `HTTP 404 NOT FOUND` status and a failed transfer object.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: bd9f585c-c5ab-4d54-a136-5b27d16a1a2d' \
    --data '{
        "originatorId": 101,
        "beneficiaryId": 666,
        "amount": 100.00
    }'

**✅ Expected Response:**

    {
        "transferId": "018d1416-3549-47de-b51e-e9126a972096",
        "createdAt": "2025-08-24T14:03:29.966662379Z",
        "transferAmount": null,
        "originator": null,
        "beneficiary": null,
        "status": "FAILED",
        "processedAt": "2025-08-24T14:03:29.966668921Z",
        "exchangeRate": null,
        "debit": null,
        "credit": null,
        "errorCode": "Account not found"
    }

### 10. Non-existing originator exchange-rate

* **Scenario:** A `POST` request is made to `/transfer` with a non-existing originator exchange-rate.
* **Expected Result:** The API should return an `HTTP 500 INTERNAL_SERVER_ERROR` status and a failed transfer object.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 2f901abb-054e-489a-b8ab-67411e9a5727' \
    --data '{
        "originatorId": 111,
        "beneficiaryId": 101,
        "amount": 100.00
    }'

**✅ Expected Response:**

    {
        "transferId": "74ff3e4b-51f8-43b9-8821-7a33239d9759",
        "createdAt": "2025-08-24T22:15:36.875875079Z",
        "transferAmount": null,
        "originator": null,
        "beneficiary": null,
        "status": "FAILED",
        "processedAt": "2025-08-24T22:15:36.875881621Z",
        "exchangeRate": null,
        "debit": null,
        "credit": null,
        "errorCode": "Invalid currency"
    }

### 11. Non-existing beneficiary exchange-rate

* **Scenario:** A `POST` request is made to `/transfer` with a non-existing beneficiary exchange-rate.
* **Expected Result:** The API should return an `HTTP 500 INTERNAL_SERVER_ERROR` status and a failed transfer object.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 4899bb33-2ebd-49a4-9d98-db35facc053d' \
    --data '{
        "originatorId": 101,
        "beneficiaryId": 111,
        "amount": 100.00
    }'

**✅ Expected Response:**

    {
        "transferId": "ed311033-438d-427a-92a9-58e1573e9845",
        "createdAt": "2025-08-24T22:14:39.477067697Z",
        "transferAmount": null,
        "originator": null,
        "beneficiary": null,
        "status": "FAILED",
        "processedAt": "2025-08-24T22:14:39.477079479Z",
        "exchangeRate": null,
        "debit": null,
        "credit": null,
        "errorCode": "Invalid currency"
    }

### 12. Not sufficient balance

* **Scenario:** A `POST` request is made to `/transfer` with a non-sufficient balance.
* **Expected Result:** The API should return an `HTTP 400 BAD REQUEST` status and a failed transfer object.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 9b476013-47dc-43dc-b523-8c9eccf138f9' \
    --data '{
        "originatorId": 101,
        "beneficiaryId": 102,
        "amount": 100000.00
    }'

**✅ Expected Response:**

    {
        "transferId": "882b6a97-0ca3-4478-b220-10a52a127e3a",
        "createdAt": "2025-08-24T14:14:40.468811292Z",
        "transferAmount": null,
        "originator": null,
        "beneficiary": null,
        "status": "FAILED",
        "processedAt": "2025-08-24T14:14:40.468817384Z",
        "exchangeRate": null,
        "debit": null,
        "credit": null,
        "errorCode": "Insufficient balance"
    }

### 12. Not sufficient balance because originator currency too weak

* **Scenario:** A `POST` request is made to `/transfer` with a non-sufficient balance because originator currency is too
  weak.
* **Expected Result:** The API should return an `HTTP 400 BAD REQUEST` status and a failed transfer object.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 3fbaef94-959a-4b02-ac02-b2b3567d056d' \
    --data '{
        "originatorId": 104,
        "beneficiaryId": 102,
        "amount": 20000.00
    }'

**✅ Expected Response:**

    {
        "transferId": "10996a0e-78b7-4cb6-b0f1-c6c9f90862d7",
        "createdAt": "2025-08-24T14:20:35.221847709Z",
        "transferAmount": null,
        "originator": null,
        "beneficiary": null,
        "status": "FAILED",
        "processedAt": "2025-08-24T14:20:35.221860143Z",
        "exchangeRate": null,
        "debit": null,
        "credit": null,
        "errorCode": "Insufficient balance"
    }

### 13. Zero amount

* **Scenario:** A `POST` request is made to `/transfer` with a zero amount.
* **Expected Result:** The API should return an `HTTP 400 BAD REQUEST` status and a failed transfer object.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 2190495f-0cbe-4d74-8d49-f532f477e44b' \
    --data '{
        "originatorId": 104,
        "beneficiaryId": 102,
        "amount": 0
    }'

**✅ Expected Response:**

    {
        "transferId": "98246588-77f9-4cd9-a978-42bde3072333",
        "createdAt": "2025-08-24T14:22:40.995015732Z",
        "transferAmount": null,
        "originator": null,
        "beneficiary": null,
        "status": "FAILED",
        "processedAt": "2025-08-24T14:22:40.99502025Z",
        "exchangeRate": null,
        "debit": null,
        "credit": null,
        "errorCode": "Negative amount"
    }

### 13. Negative amount

* **Scenario:** A `POST` request is made to `/transfer` with a negative amount.
* **Expected Result:** The API should return an `HTTP 400 BAD REQUEST` status and a failed transfer object.

**Request:**

    curl --location --request POST 'http://localhost:8080/transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 2c570f86-549d-40cb-852c-ade9159a390a' \
    --data '{
        "originatorId": 104,
        "beneficiaryId": 102,
        "amount": -100.00
    }'

**✅ Expected Response:**

    {
        "transferId": "7f60bc5a-ac57-4bb7-9450-23887a82c235",
        "createdAt": "2025-08-24T14:24:22.838456472Z",
        "transferAmount": null,
        "originator": null,
        "beneficiary": null,
        "status": "FAILED",
        "processedAt": "2025-08-24T14:24:22.838462573Z",
        "exchangeRate": null,
        "debit": null,
        "credit": null,
        "errorCode": "Negative amount"
    }

---

## 🧪 Testing the Distributed Implementation

This section outlines the test scenarios executed against the monolith implementation of the fund transfer system.

### Initial Data Setup

For ease of testing, a `data.sql` script is included to clean and populate the database at startup. This script
pre-loads the `account` table with a set of test accounts, ensuring a consistent state for running the scenarios below.

### Running the Application

To test the distributed implementation, run the application using the provided Docker Compose file. From the
`transfer-system` root folder, execute the following command:

    docker-compose -f docker-compose-distributed.yaml up --build

## Test Scenarios

The test scenarios for the distributed system are the same as the monolith's. However, due to its asynchronous design,
the API immediately returns a response with a transferId instead of the final transaction result. To confirm the
transfer was successful, you can either use the transferId to fetch the transaction's status or check the updated
balances of the involved accounts.

### ### Perform Fund transfer

* **Scenario:** A `POST` request is made to `/send-request-transfer`.
* **Expected Result:** The API should return an `HTTP 200 OK` status and a transfer request object.

*(Note: Before start testing the distributed implementation, give a bit of time to the eureka server to discover all the
services.)*

**Request:**

    curl --location --request POST 'http://localhost:8080/send-request-transfer' \
    --header 'Content-Type: application/json' \
    --header 'Idempotency-Key: 62c9881b-e247-4656-b530-06ca76e1688a' \
    --data '{
        "originatorId": 101,
        "beneficiaryId": 102,
        "amount": 100.00
    }'

**✅ Expected Response:**

    {
        "transferId": "aa06788c-6413-4043-b66b-f7633a66ddbe",
        "requestId": "62c9881b-e247-4656-b530-06ca76e1688a",
        "createdAt": "2025-08-24T14:42:54.612436742Z"
    }