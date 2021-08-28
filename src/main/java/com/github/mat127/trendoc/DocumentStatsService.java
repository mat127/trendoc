package com.github.mat127.trendoc;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocumentStatsService {

    @Autowired
    JdbcTemplate jdbc;

    public List<UUID> getTrendingDoumentIds(
        final Date since, final Date till
    ) {
        throw new UnsupportedOperationException();
    }

    List<Map<String,Object>> getDocumentStats(
        final UUID documentId,
        final Date since, final Date till
    ) {
        StringBuilder query = new StringBuilder()
            .append("SELECT day as `date`, display_count")
            .append(" FROM document_stats")
            .append(" WHERE document_id='").append(documentId).append("'");
        if(since != null)
            query.append(MessageFormat.format(" AND day>=''{0,date,yyyy-MM-dd}''", since));
        if(till != null)
            query.append(MessageFormat.format(" AND day<=''{0,date,yyyy-MM-dd}''", till));
        return this.jdbc.queryForList(query.toString());
    }
}
