# Trendoc
A trending documents microservice

### Requirements:
- MySQL - aggregated data persistence
- RabbitMQ - event queue implementation

Edit the [`appplication.properties`](src/main/resources/application.properties) file according to the deploy environment.

### Tests
- require [testcontainers.org](https://testcontainers.org)
- run with `mvn test`
- [`DocumentStatsCollectorTests`](src/test/java/com/github/mat127/trendoc/DocumentStatsCollectorTests.java) - simulates `document.displayed` events on the event queue and verifies their processing (checks the aggregated data contents).
- [`TrendingDocumentsTests`](src/test/java/com/github/mat127/trendoc/TrendingDocumentsTests.java) - demonstrates the query for trending documents (operates on prepared sample data)
