package com.github.mat127.trendoc;

import java.time.LocalDate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocumentStatsCollector {

    @Autowired
    JdbcTemplate jdbc;

    /**
     * Listens to the queue where the "document.displayed" events (as messages)
     * arrive and updates the aggregate table document_stats that is used by
     * the DocumentStatsService (see the explained table structure there).
     * 
     * @see DocumentStatsMessagingConfig
     * @see DocumentStatsService
     * 
     * @param documentId document that was displayed
     * @param date       date when it was displayed
     */
    @RabbitListener(queues=DocumentStatsMessagingConfig.DOCUMENT_DISPLAYED_QUEUE_NAME)
    public void receive(
        final String documentId,
        final Message message
    ) {
        // @Header annotation did not work because of missing converter
        // String <-> LocalDate, failed to add custom converter that is why:
        LocalDate date = parseDateFrom(message);
        jdbc.update(
            "INSERT document_stats (document_id,day,display_count,display_trend)"
            + " VALUES (?,?,1,1)"
            + " ON DUPLICATE KEY UPDATE display_count=display_count+1,"
            + "  display_trend=display_trend+1",
            documentId, date
        );

        // do not forget to decrement the display_trend for the day after the date
        jdbc.update(
            "INSERT document_stats (document_id,day,display_count,display_trend)"
            + " VALUES (?,DATE_ADD(?, INTERVAL 1 DAY),0,-1)"
            + " ON DUPLICATE KEY UPDATE display_trend=display_trend-1",
            documentId, date
        );
    }

    private static LocalDate parseDateFrom(final Message message) {
        String str = message.getMessageProperties().getHeader("date");
        return LocalDate.parse(str);
    }
}
