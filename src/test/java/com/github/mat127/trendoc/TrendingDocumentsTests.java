package com.github.mat127.trendoc;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TrendocApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {TrendingDocumentsTests.Initializer.class})
public class TrendingDocumentsTests {

    @ClassRule
    public static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.0.25")
        .withDatabaseName("integration-tests-db")
        .withUsername("sa")
        .withPassword("sa")
        .withInitScript("schema-and-data.sql");

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
    private MockMvc mvc;

    @Autowired
    private FormattingConversionService formatter;

    private Logger logger = LoggerFactory.getLogger(TrendingDocumentsTests.class);

    @Test
    public void testTrendingDocuments() throws Exception {
        MvcResult result = mvc.perform(get("/document/trending")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        logger.info("\n\nTrending documents received:\n\t {}\n",
            result.getResponse().getContentAsString()
        );
    }
}
