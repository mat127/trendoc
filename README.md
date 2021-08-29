# Trendoc
A trending documents microservice

### Requirements:
- MySQL - aggregated data persistence
- RabbitMQ - event queue implementation

Edit the [`appplication.properties`](src/main/resources/application.properties) file according to the deploy environment.

### REST API
- `GET /document/trending?since={yyyy-mm-dd}&till={yyyy-mm-dd}&min_disp_count={n}&limit={n}`
  - `since` - begin date of the inspected time interval, optional, if ommited since the beginning of known history
  - `till` - end date of the inspected time interval, optional, if ommited till the end of known history
  - in case both `since` and `till` are omitted last 7 days are used
  - `min_disp_count` - minimal display count documents must have at least one day during the inspected period to be included in the results, optional, default value is 1000
  - `limit` - optional, maximum number of trending documents returned
  - result structure
  ```
  [
    {"document_id":"5c910dde-0838-11ec-ad75-6c02e0970836","score":0.09065088},
    {"document_id":"5c90fef7-0838-11ec-ad75-6c02e0970836","score":0.06497158},
    ...
    {"document_id":"5c910628-0838-11ec-ad75-6c02e0970836","score":0.01396002}
  ]
  ```
  - 0 < `score` <= 1, higher value means "more trending" document, results are ordered in the descending order

- `GET /document/{id}/stats?since={yyyy-mm-dd}&till={yyyy-mm-dd}`
  - `id` - document id (UUID)
  - `since` - optional, begin date of the inspected time interval, if ommited since the beginning of known history
  - `till` - optional, end date of the inspected time interval, if ommited till the end of known history
  - result structure
  ```
  [
    {"date":"2021-08-29","display_count":5},
    ...
  ]
  ```

### Tests
- require [testcontainers.org](https://testcontainers.org)
- run with `mvn test`
- [`DocumentStatsCollectorTests`](src/test/java/com/github/mat127/trendoc/DocumentStatsCollectorTests.java) - simulates `document.displayed` events on the event queue and verifies their processing by the [`DocumentStatsCollector`](src/main/java/com/github/mat127/trendoc/DocumentStatsCollector.java).
- [`TrendingDocumentsTests`](src/test/java/com/github/mat127/trendoc/TrendingDocumentsTests.java) - demonstrates the query for trending documents (operates on prepared [sample data](src/test/resources/schema-and-data.sql))
