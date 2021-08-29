package com.github.mat127.trendoc;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TrendocApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {DocumentStatsCollectorTests.Initializer.class})
public class DocumentStatsCollectorTests {

    @ClassRule
    public static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.0.25")
        .withDatabaseName("integration-tests-db")
        .withUsername("sa")
        .withPassword("sa")
        .withInitScript("schema.sql");

    @ClassRule
    public static GenericContainer<?> activeMQContainer = new GenericContainer<>("rmohr/activemq:latest")
        .withExposedPorts(61616);

    @ClassRule
    public static GenericContainer<?> rabbitMQContainer = new GenericContainer<>("rabbitmq:management")
        .withExposedPorts(5672);

    static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
            public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
                TestPropertyValues.of(
                    // mysql
                    "spring.datasource.url=" + mySQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + mySQLContainer.getUsername(),
                    "spring.datasource.password=" + mySQLContainer.getPassword(),
                    // activemq
                    "spring.activemq.broker-url=tcp://localhost:" + activeMQContainer.getMappedPort(61616),
                    // rabbitmq
                    "spring.rabbitmq.port=" + rabbitMQContainer.getMappedPort(5672)
                ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    private DocumentStatsEventGenerator statsGenerator;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FormattingConversionService formatter;

    @Test
    public void testStatsCollector() throws Exception {
        DocumentStatsRecord record =
            new DocumentStatsRecord(5).generate();
        Thread.sleep(1000); // TODO: use better synchronization!
        record.verify();
    }

    private class DocumentStatsRecord {

        private final UUID documentId;
        private final LocalDate date;
        private final int displayCount;

        public DocumentStatsRecord(final int displayCount) {
            this.documentId = UUID.randomUUID();
            this.date = LocalDate.now();
            this.displayCount = displayCount;
        }

        public DocumentStatsRecord generate() {
            statsGenerator.generateDisplayedEvents(
                this.documentId, this.date, this.displayCount
            );
            return this;
        }
        
        public void verify() throws Exception {
            String dateStr = formatter.convert(this.date, String.class);
            mvc.perform(
                get("/document/{id}/stats", documentId)
                .queryParam("since", dateStr)
                .queryParam("till", dateStr)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].display_count").value(this.displayCount));
        }
    }
}
