# Spring Boot ERP Backend

A production-style Java / Spring Boot enterprise backend that models **master data**, **MM (Materials Management)**, **inventory**, and **SD / Order-to-Cash** business processes.

The application is built as a controlled backend for later **SAP CPI integration learning**, without modifying or depending on any S/4HANA system.

---

## Table of Contents

- [Project Goals](#project-goals)
- [Business Scope](#business-scope)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Business Domains](#business-domains)
- [Core Entity Model](#core-entity-model)
- [JPA / Hibernate Design](#jpa--hibernate-design)
- [DTO and Mapping](#dto-and-mapping)
- [Validation](#validation)
- [Exception Handling](#exception-handling)
- [Transaction Management](#transaction-management)
- [Database and Flyway](#database-and-flyway)
- [REST API Surface](#rest-api-surface)
- [Testing Strategy (TDD)](#testing-strategy-tdd)
- [Security (Later Phase)](#security-later-phase)
- [Observability](#observability)
- [Docker](#docker)
- [Development Roadmap](#development-roadmap)
- [Target End-to-End Flow](#target-end-to-end-flow)

---

## Project Goals

This project revises Spring Boot, Java backend, JPA/Hibernate, SQL, REST, testing, transactions, and enterprise application design, while building a realistic backend that can later be integrated with SAP CPI.

**Key principles**

- TDD (Test-Driven Development)
- Clean layered architecture
- RESTful APIs
- JPA / Hibernate
- Transactions
- Integration testing
- Docker
- CPI integration

> **Project philosophy:** Build the backend incrementally and test each business capability before moving to the next. The Java application remains a controlled learning ERP backend; SAP CPI will later consume its APIs as the integration/middleware layer.

---

## Business Scope

This is a **simplified enterprise ERP backend**. It does not recreate all of SAP S/4HANA. It models only the business objects needed for meaningful MM and SD/O2C integration scenarios.

```
                        MINI ERP
                           |
        +------------------+------------------+
        |                  |                  |
   MASTER DATA            MM                 SD
        |                  |                  |
 Business Partner      Supplier           Customer
 Material              Purchase Order     Sales Order
 Plant                 Goods Receipt      Delivery
 Storage Location      Inventory          Goods Issue
                                          Billing
```

---

## Technology Stack

| Area | Technology |
|---|---|
| Language | Java 17 or Java 21 |
| Framework | Spring Boot 3.x |
| Web | Spring Web / Spring MVC |
| API | RESTful APIs |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL |
| Database migration | Flyway |
| Validation | Jakarta Bean Validation |
| Unit testing | JUnit 5 + Mockito |
| Web/API testing | Spring Boot Test + MockMvc |
| Integration testing | Testcontainers + MySQL |
| API documentation | OpenAPI / Swagger |
| Build | Maven |
| Logging | SLF4J + Logback |
| API testing tool | Postman |
| Version control | Git + GitHub |
| Containerization | Docker / Docker Compose |
| CI/CD | GitHub Actions (later) |
| Security | Spring Security + JWT (later) |
| Observability | Spring Boot Actuator |

---

## Architecture

### High-level view

```
 External Applications / Postman
              |
              | REST
              v
      +----------------+
      |  Spring Boot   |
      |      ERP       |
      +-------+--------+
              |
     +--------+---------+
     |        |         |
     v        v         v
  Master     MM         SD
   Data
     |        |         |
     +--------+---------+
              |
              v
            MySQL

 Later: External System -> SAP CPI -> Spring Boot ERP
```

### Architecture decision

Use **one Spring Boot application with a modular package structure** initially. Do **not** split into microservices at the beginning. This keeps the focus on Spring Boot fundamentals and business integration.

---

## Project Structure

### Logical modules

```
com.enterprise.erp
 |
 +-- businesspartner
 +-- material
 +-- plant
 +-- inventory
 +-- supplier
 +-- purchasing
 +-- sales
 +-- delivery
 +-- billing
 +-- common
```

### Standard module structure

Example: the `sales` module.

```
sales/
 |
 +-- controller/
 |     +-- SalesOrderController.java
 |
 +-- service/
 |     +-- SalesOrderService.java
 |     +-- SalesOrderServiceImpl.java
 |
 +-- repository/
 |     +-- SalesOrderRepository.java
 |
 +-- entity/
 |     +-- SalesOrder.java
 |     +-- SalesOrderItem.java
 |
 +-- dto/
 |     +-- SalesOrderRequest.java
 |     +-- SalesOrderItemRequest.java
 |     +-- SalesOrderResponse.java
 |
 +-- mapper/
 |     +-- SalesOrderMapper.java
 |
 +-- validator/
 |     +-- SalesOrderValidator.java
 |
 +-- exception/
       +-- SalesOrderException.java
```

---

## Business Domains

### Master data

- **Business Partner** - represents parties such as customers and suppliers. A simplified role model supports `CUSTOMER`, `SUPPLIER`, or `BOTH`.
- **Material / Product** - goods handled by the enterprise, with material number, description, material type, unit, status, and price.
- **Organizational data** - plant and storage location provide the location context used by inventory and logistics transactions.

### MM - Materials Management

Manages procurement and stock replenishment.

```
Supplier -> Purchase Order -> Goods Receipt -> Inventory increases
```

Example: if `MAT1001` has 10 units and a goods receipt adds 100 units, the available quantity becomes 110.

Purchase requisitions and more detailed procurement rules can be added later.

### SD / Order-to-Cash

Handles selling and delivering materials.

```
Customer -> Sales Order -> Availability Check -> Delivery -> Goods Issue -> Billing
```

### How the domains relate

```
        Business Partner
         |            |
      Customer      Supplier
         |            |
         v            v
    Sales Order   Purchase Order
         |            |
         v            v
      Delivery    Goods Receipt
         |            |
     Goods Issue      |
         |            |
         +--> Inventory <--+
                  ^
                  |
              Material
                  |
        Plant / Storage Location
```

Master data is the foundation reused by transactions. MM supports obtaining and managing materials, SD supports selling and delivering them, and inventory connects the two.

---

## Core Entity Model

```
BusinessPartner
 |-- Customer role
 |-- Supplier role

Material
 |-- Plant
 |-- Storage Location
      |-- Inventory

Supplier -> PurchaseOrder -> PurchaseOrderItem -> GoodsReceipt -> Inventory

Customer -> SalesOrder -> SalesOrderItem -> Delivery -> GoodsIssue -> Billing
```

---

## JPA / Hibernate Design

JPA entities use relationships that reflect the business model. The project deliberately practices JPA concepts commonly asked in Java backend interviews.

| Concept | Where it is practiced |
|---|---|
| `@Entity` / `@Id` | All persistent business objects |
| `@OneToMany` | SalesOrder -> SalesOrderItem |
| `@ManyToOne` | SalesOrderItem -> Material |
| `@JoinColumn` | Foreign-key relationships |
| `mappedBy` | Bidirectional relationships where appropriate |
| Cascade | Aggregate operations such as order -> items |
| `FetchType.LAZY` | Related master/transaction data |
| JPQL | Business-specific queries |
| Derived queries | Simple repository searches |
| `@Transactional` | Order, goods receipt, goods issue operations |

---

## DTO and Mapping

JPA entities are **not** exposed directly as API contracts. DTOs keep the external API independent from the persistence model and make the later CPI integration cleaner.

```
Request path:
HTTP Request -> Request DTO -> Controller -> Service -> Entity -> Repository -> Database

Response path:
Database -> Entity -> Mapper -> Response DTO -> JSON Response
```

---

## Validation

**Structural validation** uses Jakarta Bean Validation:

`@NotNull` `@NotBlank` `@Positive` `@Size` `@Email`

**Business validation** stays in the service/domain layer, where it depends on data and business rules. Examples:

- Customer is active
- Material is active
- Plant / storage location is valid
- Inventory is sufficient

---

## Exception Handling

```
Business Exception -> @RestControllerAdvice -> Standard Error Response
```

Example error response:

```json
{
  "timestamp": "...",
  "status": 400,
  "errorCode": "INSUFFICIENT_STOCK",
  "message": "Only 10 units are available",
  "path": "/api/sales-orders"
}
```

---

## Transaction Management

Operations that change multiple records must be atomic. For example, creating a sales order can validate the customer, validate the material, check inventory, create the order, and create the order items, all within one transaction. Goods receipt and goods issue also use transaction boundaries.

```java
@Transactional
public SalesOrderResponse createSalesOrder(...) {
    // validate
    // check stock
    // create order
    // create items
}
```

If a required operation fails, the transaction rolls back instead of leaving partial business data.

---

## Database and Flyway

MySQL is the main database. Flyway manages schema evolution instead of manually created tables.

```
db/migration/
 |
 +-- V1__create_business_partner.sql
 +-- V2__create_material.sql
 +-- V3__create_inventory.sql
 +-- V4__create_purchase_order.sql
 +-- V5__create_sales_order.sql
 +-- ...
```

---

## REST API Surface

| Domain | Example endpoints |
|---|---|
| Business Partner | `GET/POST/PUT /api/business-partners` |
| Material | `GET/POST/PUT /api/materials` |
| Plant | `GET /api/plants`, `GET /api/plants/{id}` |
| Storage Location | `GET /api/storage-locations` |
| Inventory | `GET /api/inventory/{materialId}` |
| Supplier | `GET /api/suppliers/{id}` |
| Purchase Order | `POST/GET /api/purchase-orders` |
| Goods Receipt | `POST /api/goods-receipts` |
| Sales Order | `POST/GET /api/sales-orders` |
| Delivery | `POST/GET /api/deliveries` |
| Goods Issue | `POST /api/goods-issues` |
| Billing | `POST/GET /api/billing-documents` |

---

## Testing Strategy (TDD)

```
RED       Write a test for the required behavior. Confirm it fails.
GREEN     Write the minimum implementation. Confirm the test passes.
REFACTOR  Improve the design without changing behavior.
Repeat.
```

### Testing layers

| Layer | Scope |
|---|---|
| Unit tests | Services, business rules, validators, mappers |
| Integration tests | Controller + Spring context, JPA + database, Testcontainers + MySQL |
| API tests | Postman / Swagger |

Testcontainers with MySQL is preferred for database integration tests, so tests exercise behavior close to the real database rather than relying only on H2.

---

## Security (Later Phase)

```
Postman / Client -> Spring Security -> JWT Authentication -> Role-based authorization
```

Planned roles: `ADMIN`, `SALES_USER`, `WAREHOUSE_USER`, `PURCHASING_USER`.

Security is deliberately deferred until the core business and integration flows work. This avoids adding complexity before the fundamental architecture is stable.

---

## Observability

SLF4J/Logback for application logging and Spring Boot Actuator for operational endpoints:

- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`

---

## Docker

```
      Docker Compose
            |
      +-----+------+
      |            |
      v            v
 Spring Boot     MySQL
  container     container
```

Once the application works locally, containerize the Spring Boot service and MySQL. This also prepares the application for CPI connectivity in a controlled environment.

---

## Development Roadmap

- [ ] **Phase 1** - Project setup, Maven, configuration, MySQL, Flyway
- [ ] **Phase 2** - Business Partner master data
- [ ] **Phase 3** - Material, Plant, Storage Location
- [ ] **Phase 4** - Inventory
- [ ] **Phase 5** - Supplier + Purchase Order
- [ ] **Phase 6** - Goods Receipt + stock update
- [ ] **Phase 7** - Customer / Sales Order
- [ ] **Phase 8** - Availability check
- [ ] **Phase 9** - Delivery + Goods Issue
- [ ] **Phase 10** - Billing
- [ ] **Phase 11** - TDD / unit tests + integration tests
- [ ] **Phase 12** - Swagger, Actuator, logging
- [ ] **Phase 13** - Docker
- [ ] **Phase 14** - Security / JWT
- [ ] **Phase 15** - SAP CPI integration

---

## Target End-to-End Flow

**MM / Procurement**

```
Supplier -> Purchase Order -> Goods Receipt -> Inventory + quantity
```

**SD / Order-to-Cash**

```
Customer -> Sales Order -> Availability Check -> Delivery -> Goods Issue -> Billing
```

**Integration (later)**

```
External Application -> SAP CPI -> Spring Boot ERP -> Business processing -> Response
```

### Final technical architecture

```
+----------------------------------------------------------+
|                    SPRING BOOT ERP                       |
|                                                          |
|  Java 17/21                                              |
|  Spring Boot 3.x                                         |
|  Spring MVC / REST                                       |
|  Spring Data JPA / Hibernate                             |
|  Jakarta Validation                                      |
|                                                          |
|  Modules:                                                |
|  Business Partner | Material | Plant | Inventory         |
|  Supplier | Purchasing | Sales | Delivery | Billing      |
|                                                          |
|  MySQL + Flyway                                          |
|  JUnit 5 + Mockito + MockMvc + Testcontainers            |
|  Swagger/OpenAPI                                         |
|  SLF4J/Logback + Actuator                                |
|  Docker                                                  |
|  Spring Security/JWT (later)                             |
+----------------------------------------------------------+
                           |
                           v
                     SAP CPI (later)
                           |
                           v
                   External Systems
```
