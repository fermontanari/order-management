# order-management

# Microservices for Shopkeepers and Order Management to interact

The Shopkeeper and Order Management microservices are two Spring Boot applications developed to allow shopkeepers and Order Management do do interactions. The ideal is to have a microservice to control orders, this microservice could interact in the future with other sevices, as Number Management. The shopkeeper is a microservice to comunicate with OM, creating orders and representing a starting point of the system. 
The codes in this repository are just a proof of concept and they were derived from a pratical task of the discipline of **Backend Architecture and Microservices**, in the Distributed Software Architecture postgraduate.
This work is a POC for the conclusion paper for the same course.

#### Author
- [Fernanda Montanari](https://github.com/fermontanari)

### To test the application

You can test this application by follow the postman collection inside the folder `postman`. The sequence of saved requests there represents all the business flow between the shopkeeper and the order management, according to the statement of the discipline final work.

### Instructions to run the applications with Docker
1. Clone this repository
2. Go to the root project and type `./mvnw package docker:build` to build the both microservices and the Docker images. At this moment, it works just with Maven.
3. To run the Shopkeeper microservice, type: `docker run -p 8090:8090 -t asd/ashopkeeper`
4. So, to run the Order Management microservice, type: `docker run -p 13080:13080 -t asd/order-management-app`
5. You can now visit http://localhost:8090/shopkeeper/v1/ and http://localhost:13080/ordermanagement/v1/ to see the APIs initial resources

### Instructions to run the applications without Docker (i.e. in the host OS)
1. Clone this repository
2. Go to the root project and type `./mvnw clean package` (if you prefer maven) or `./gradlew build` (if you prefer gradle). You will build the both microservices.
3. To run the Shopkeeper microservice, type:
    1. `java -jar shopkeeper/target/shopkeeper-0.1.0.jar` (if you prefer maven)
    2. `java -jar shopkeeper/build/libs/shopkeeper-0.1.0.jar` (if you prefer gradle)
    3. You can now visit http://localhost:8090/shopkeeper/v1/ to see the API initial resources
4. To run the Order Management microservice, type:
    1. `java -jar order-management-app/target/order-management-app-0.1.0.jar` (if you prefer maven)
    2. `java -jar order-management-app/build/libs/order-management-app-0.1.0.jar` (if you prefer gradle)
    3. You can now visit http://localhost:13080/ordermanagement/v1/ to see the API initial resources

## Shopkeeper microservice

Shopkeeper is a microservice for shopkeepers to request products and interact with order management on proposals.

Exposed API for Shopkeeper:

* **GET** /orchestration/order/{id} - Gets the order {id}.
* **GET** /orchestration/orders - Gets all orders.
* **POST** /orchestration/order - Creates a new order.
* **POST** /orchestration/proposal/{id}/accept - Accepts the proposal {id}.
* **POST** /orchestration/proposal/{id}/reject - Rejects the proposal {id}.

Exposed API for other microservices: All other APIs under / are exposed for other microservices. There will be a authorization framework to only allow other microservices to access them.

**Complete documentation:** The complete documentation can be found at: http://localhost:8090/shopkeeper/v1/swagger-ui.html

## Order Management microservice
Order Management is a microservice for controlling receive of products requests and interact with shopkeepers on proposals and order tracking.

Exposed API for Order Management:

* **GET** /orchestration/order/{id} - Gets the order {id}.
* **GET** /orchestration/orders - Gets all orders.
* **POST** /orchestration/proposal - Creates a new proposal.
* **POST** /orchestration/order/{id}/manufactoring - Changes the order status.
* **POST** /orchestration/order/{id}/dispatch - Changes the order status.
* **POST** /orchestration/order/{id}/close - Changes the order status.

Exposed API for other Microservices: All other APIs under / are exposed for other microservices. There will be a authorization framework to only allow other microservices to access them.

**Complete documentation:** The complete documentation can be found at: http://localhost:13080/ordermanagement/v1/swagger-ui.html
