# notification-queue

## Description
The `notification-queue` repository is a microservice designed to process user notifications. These notifications are triggered by the contact form on my personal portfolio page. This service ensures that user messages are efficiently handled and processed.

## Features
- Processes notifications triggered by the contact form.
- Ensures messages are delivered reliably.
- Integrated with a Kafka messaging queue for message handling.

## Technologies Used
- Java
- Spring Boot
- Kafka
- MongoDB

## Setup and Installation

### Prerequisites
- Java 11 or higher
- Apache Kafka
- MongoDB

### Clone the Repository
```bash
git clone https://github.com/yourusername/notification-queue.git
cd notification-queue
```

### Configure the Application
- Update the application.properties file with your Kafka and MongoDB configurations.
```properties
spring.kafka.bootstrap-servers=your-kafka-server:9092
spring.data.mongodb.uri=mongodb://localhost:27017/yourdatabase
```

### Configure the Application
```bash
./mvnw clean install
./mvnw spring-boot:run
```
