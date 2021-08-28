package com.github.mat127.trendoc;

import java.util.Date;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class DocumentStatsCollector {

    @Autowired
    JdbcTemplate jdbc;

    @RabbitListener(queues=DocumentStatsMessagingConfig.DOCUMENT_DISPLAYED_QUEUE_NAME)
    public void receive(final String documentId, @Header("date")final Date date) {
        jdbc.update(
            "INSERT document_stats (document_id,day,display_count)"
            + " VALUES (?,?,1)"
            + " ON DUPLICATE KEY UPDATE display_count=display_count+1",
            documentId, date
        );
    }
}
