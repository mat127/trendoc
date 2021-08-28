package com.github.mat127.trendoc;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentStatsMessagingConfig {

    public final static String DOCUMENT_ACTIONS_EXCHANGE_NAME =
        "trendoc.exchange.document.actions";
    public final static String DOCUMENT_DISPLAYED_QUEUE_NAME =
        "trendoc.queue.document.displayed";
    public final static String DOCUMENT_DISPLAYED_KEY =
        "document.displayed";

    @Bean
    public Declarables bindings() {
        Queue queue = new Queue(DOCUMENT_DISPLAYED_QUEUE_NAME, false);

        TopicExchange exchange =
            new TopicExchange(DOCUMENT_ACTIONS_EXCHANGE_NAME, false, false);

        return new Declarables(queue, exchange, BindingBuilder
            .bind(queue)
            .to(exchange)
            .with(DOCUMENT_DISPLAYED_KEY)
        );
    }
}
