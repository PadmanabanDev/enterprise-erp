Spring Boot ERP Backend — Technical Design Page 1
Spring Boot ERP Backend
Technical Design & Project Structure
Purpose: A production-style Java/Spring Boot enterprise backend that models master data, MM, inventory, and
SD/O2C business processes. The application will later serve as a controlled backend for SAP CPI integration
learning, without modifying or depending on the user's S/4HANA system.
1. Project Goals
The project is designed to revise Spring Boot, Java backend, JPA/Hibernate, SQL, REST, testing, transactions, and
enterprise application design while simultaneously creating a realistic backend that can later be integrated with SAP
CPI.
Key principles
TDD ® Clean layered architecture ® REST APIs ® JPA/Hibernate
® Transactions ® Integration testing ® Docker ® CPI integration
2. Business Scope
The application is a simplified enterprise ERP backend. It is not intended to recreate all of SAP S/4HANA; it models
the business objects needed for meaningful MM and SD/O2C integration scenarios.
MINI ERP
|
+-------------------+-------------------+
| | |
MASTER DATA MM SD
| | |
Business Partner Supplier Customer
Material Purchase Order Sales Order
Plant Goods Receipt Delivery
Storage Location Inventory Goods Issue
Billing
3. Technology Stack
Area Technology
Language Java 17 or Java 21
Framework Spring Boot 3.x
Web Spring Web / Spring MVC
API RESTful APIs
ORM Spring Data JPA + Hibernate
Database MySQL
Database migration Flyway
Validation Jakarta Bean Validation
Unit testing JUnit 5 + Mockito
Web/API testing Spring Boot Test + MockMvc
Integration testing Testcontainers + MySQL
API documentation OpenAPI / Swagger
Build Maven
Logging SLF4J + Logback
Spring Boot ERP Backend — Technical Design Page 2
Area Technology
API testing tool Postman
Version control Git + GitHub
Containerization Docker / Docker Compose
CI/CD GitHub Actions (later)
Security Spring Security + JWT (later)
Observability Spring Boot Actuator
4. High-Level Architecture
External Applications / Postman
|
| REST
v
+----------------+
| Spring Boot |
| ERP |
+-------+--------+
|
+--------+---------+
| | |
v v v
Master MM SD
Data | |
| | |
+--------+----------+
|
v
MySQL
Later:
External System ® SAP CPI ® Spring Boot ERP
Architecture decision: Use one Spring Boot application with a modular package structure initially. Do not split the
project into microservices at the beginning. This keeps the focus on Spring Boot fundamentals and business
integration.
5. Logical Modules
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
6. Standard Module Structure
sales/
|
+-- controller/
| +-- SalesOrderController.java
|
+-- service/
| +-- SalesOrderService.java
| +-- SalesOrderServiceImpl.java
|
+-- repository/
| +-- SalesOrderRepository.java
|
+-- entity/
Spring Boot ERP Backend — Technical Design Page 3
| +-- SalesOrder.java
| +-- SalesOrderItem.java
|
+-- dto/
| +-- SalesOrderRequest.java
| +-- SalesOrderItemRequest.java
| +-- SalesOrderResponse.java
|
+-- mapper/
| +-- SalesOrderMapper.java
|
+-- validator/
| +-- SalesOrderValidator.java
|
+-- exception/
+-- SalesOrderException.java
7. Master Data Domain
Business Partner: Represents parties such as customers and suppliers. A simplified role model can support
CUSTOMER, SUPPLIER, or BOTH.
Material/Product: Represents goods handled by the enterprise, with attributes such as material number, description,
material type, unit, status, and price.
Organizational data: Plant and storage location provide the basic location context used by inventory and logistics
transactions.
8. MM — Materials Management
The simplified MM flow manages procurement and stock replenishment:
Supplier
|
v
Purchase Order
|
v
Goods Receipt
|
v
Inventory increases
Example: if MAT1001 has 10 units and a goods receipt adds 100 units, the available quantity becomes 110. The
project can later add purchase requisitions and more detailed procurement rules if needed.
9. SD / Order-to-Cash
The simplified SD flow handles selling and delivering materials:
Customer
|
v
Sales Order
|
v
Availability Check
|
v
Delivery
|
v
Goods Issue
|
v
Billing
10. Relationship Between Master Data, MM and SD
Business Partner
| Customer Supplier
| |
Spring Boot ERP Backend — Technical Design Page 4
v v
Sales Order Purchase Order
| |
v v
Delivery Goods Receipt
| |
Goods Issue v
| Inventory
+---------> Inventory <---------+
^
|
Material
|
Plant / Storage Location
Master data is the foundation reused by transactions. MM primarily supports obtaining and managing materials, while
SD supports selling and delivering them. Both areas use shared master and organizational information, and inventory
connects the processes.
11. Core Entity Model
BusinessPartner
|
+-- Customer role
+-- Supplier role
Material
|
+-- Plant
+-- Storage Location
|
+-- Inventory
Supplier
|
v
PurchaseOrder
|
v
PurchaseOrderItem
|
v
GoodsReceipt
|
v
Inventory
Customer
|
v
SalesOrder
|
v
SalesOrderItem
|
v
Delivery
|
v
GoodsIssue
|
v
Billing
12. JPA / Hibernate Design
The application will use JPA entities with relationships that reflect the business model. The project will deliberately
practice the JPA concepts commonly asked in Java backend interviews.
Concept Where it will be practiced
@Entity / @Id All persistent business objects
Spring Boot ERP Backend — Technical Design Page 5
Concept Where it will be practiced
@OneToMany SalesOrder ® SalesOrderItem
@ManyToOne SalesOrderItem ® Material
@JoinColumn Foreign-key relationships
mappedBy Bidirectional relationships where appropriate
Cascade Aggregate operations such as order ® items
FetchType.LAZY Related master/transaction data
JPQL Business-specific queries
Derived queries Simple repository searches
@Transactional Order, goods receipt, goods issue operations
13. DTO and Mapping Architecture
HTTP Request
|
v
Request DTO
|
v
Controller
|
v
Service
|
v
Entity
|
v
Repository
|
v
Database
Database
|
v
Entity
|
v
Mapper
|
v
Response DTO
|
v
JSON Response
JPA entities should not be exposed directly as API contracts. DTOs keep the external API independent from the
persistence model and make the later CPI integration cleaner.
14. Validation
Structural validation will use Jakarta Bean Validation:
@NotNull
@NotBlank
@Positive
@Size
@Email
Business validation will remain in the service/domain layer where it depends on data and business rules, for example:
active customer, active material, valid plant/storage location, and sufficient inventory.
Spring Boot ERP Backend — Technical Design Page 6
15. Exception Handling
Business Exception
|
v
@RestControllerAdvice
|
v
Standard Error Response
{
"timestamp": "...",
"status": 400,
"errorCode": "INSUFFICIENT_STOCK",
"message": "Only 10 units are available",
"path": "/api/sales-orders"
}
16. Transaction Management
Operations that change multiple records should be atomic. For example, creating a sales order can validate the
customer, validate material, check inventory, create the order, and create order items within one transaction. Goods
receipt and goods issue will also use transaction boundaries.
@Transactional
public SalesOrderResponse createSalesOrder(...) {
// validate
// check stock
// create order
// create items
}
If a required operation fails, the transaction should roll back rather than leaving partial business data.
17. Database and Flyway
MySQL will be the main database. Flyway will manage schema evolution rather than relying on manually created
tables.
db/migration/
|
+-- V1__create_business_partner.sql
+-- V2__create_material.sql
+-- V3__create_inventory.sql
+-- V4__create_purchase_order.sql
+-- V5__create_sales_order.sql
+-- ...
18. REST API Surface
Domain Example endpoints
Business Partner GET/POST/PUT /api/business-partners
Material GET/POST/PUT /api/materials
Plant GET /api/plants, GET /api/plants/{id}
Storage Location GET /api/storage-locations
Inventory GET /api/inventory/{materialId}
Supplier GET /api/suppliers/{id}
Purchase Order POST/GET /api/purchase-orders
Goods Receipt POST /api/goods-receipts
Sales Order POST/GET /api/sales-orders
Delivery POST/GET /api/deliveries
Spring Boot ERP Backend — Technical Design Page 7
Domain Example endpoints
Goods Issue POST /api/goods-issues
Billing POST/GET /api/billing-documents
19. TDD Strategy
RED
Write a test for the required behavior.
Confirm it fails.
GREEN
Write the minimum implementation.
Confirm the test passes.
REFACTOR
Improve design without changing behavior.
Repeat.
Testing layers
Unit tests
- Service
- Business rules
- Validators
- Mappers
Integration tests
- Controller + Spring context
- JPA + database
- Testcontainers + MySQL
API tests
- Postman / Swagger
Testcontainers with MySQL is preferred for database integration tests so the tests exercise behavior close to the real
database rather than relying only on H2.
20. Security — Later Phase
Postman / Client
|
v
Spring Security
|
v
JWT Authentication
|
v
Role-based authorization
ADMIN
SALES_USER
WAREHOUSE_USER
PURCHASING_USER
Security is deliberately deferred until the core business and integration flows are working. This avoids adding
complexity before the fundamental architecture is stable.
21. Observability
Use SLF4J/Logback for application logging and Spring Boot Actuator for health and operational endpoints.
/actuator/health
/actuator/info
/actuator/metrics
22. Docker
Docker Compose
Spring Boot ERP Backend — Technical Design Page 8
|
+---+-----------+
| |
v v
Spring Boot MySQL
container container
Once the application works locally, containerize the Spring Boot service and MySQL. This also prepares the
application for CPI connectivity in a controlled environment.
23. Development Roadmap
Phase Scope
1 Project setup, Maven, configuration, MySQL, Flyway
2 Business Partner master data
3 Material, Plant, Storage Location
4 Inventory
5 Supplier + Purchase Order
6 Goods Receipt + stock update
7 Customer/Sales Order
8 Availability check
9 Delivery + Goods Issue
10 Billing
11 TDD/unit tests + integration tests
12 Swagger, Actuator, logging
13 Docker
14 Security/JWT
15 SAP CPI integration
24. Target End-to-End Business Flow
MM / Procurement:
Supplier
-> Purchase Order
-> Goods Receipt
-> Inventory + quantity
SD / Order-to-Cash:
Customer
-> Sales Order
-> Availability Check
-> Delivery
-> Goods Issue
-> Billing
Integration later:
External Application
-> SAP CPI
-> Spring Boot ERP
-> Business processing
-> Response
Spring Boot ERP Backend — Technical Design Page 9
25. Final Technical Architecture
+----------------------------------------------------------+
| SPRING BOOT ERP |
| |
| Java 17/21 |
| Spring Boot 3.x |
| Spring MVC / REST |
| Spring Data JPA / Hibernate |
| Jakarta Validation |
| |
| Modules: |
| Business Partner | Material | Plant | Inventory |
| Supplier | Purchasing | Sales | Delivery | Billing |
| |
| MySQL + Flyway |
| JUnit 5 + Mockito + MockMvc + Testcontainers |
| Swagger/OpenAPI |
| SLF4J/Logback + Actuator |
| Docker |
| Spring Security/JWT (later) |
+----------------------------------------------------------+
|
v
SAP CPI (later)
|
v
External Systems
Project philosophy: Build the backend incrementally and test each business capability before moving to the next.
The Java application remains a controlled learning ERP backend; SAP CPI will later consume its APIs as the
integration/middleware layer.
