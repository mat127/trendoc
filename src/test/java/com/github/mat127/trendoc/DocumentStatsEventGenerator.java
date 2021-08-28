package com.github.mat127.trendoc;

import java.util.Date;
import java.util.UUID;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocumentStatsEventGenerator {

    @Autowired
    private RabbitTemplate rabbit;

    public void generateDisplayedEvent(
        final UUID documentId,
        final Date date
    ) {
        Message message = MessageBuilder.withBody(documentId.toString().getBytes())
            .setHeader("date", date)
            .build();
        this.rabbit.send(
            DocumentStatsMessagingConfig.DOCUMENT_ACTIONS_EXCHANGE_NAME,
            DocumentStatsMessagingConfig.DOCUMENT_DISPLAYED_KEY,
            message
        );
    }

    public void generateDisplayedEvents(
        final UUID documentId,
        final Date date,
        final int count
    ) {
        for(int i=0; i < count; i++) {
            this.generateDisplayedEvent(documentId, date);
        }
    }
}
