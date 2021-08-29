package com.github.mat127.trendoc;

import java.time.LocalDate;
import java.util.Formatter;
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

    public List<Map<String,Object>> getTrendingDocuments(
        final LocalDate since, LocalDate till,
        final int minDislpayCount,
        final int limit
    ) {
        // skip today if not requested as its trend is not complete yet and
        // can affect the result
        if(till == null)
            till = LocalDate.now().minusDays(1);
        Formatter query = new Formatter()
            // normalize the score to display_count to get a comparable value
            .format("SELECT document_id, AVG(display_trend)/MAX(display_count) AS score ")
            .format(" FROM document_stats")
            .format(" WHERE day<='%tF'", till);
        if(since != null)
            query.format(" AND day>='%tF'", since);
        query.format(" GROUP BY document_id")
            // positive trend only
            .format(" HAVING score>0")
            // only documents have been displayed at least minDispayCount times
            .format(" AND MAX(display_count)>=%d", minDislpayCount);
        query.format(" ORDER BY score DESC") // best first
            .format(" LIMIT %d", limit); // at most limit records
        return this.jdbc.queryForList(query.toString());
    }

    public List<Map<String,Object>> getDocumentStats(
        final UUID documentId,
        final LocalDate since, LocalDate till
    ) {
        // Because of the display_trend column the table document_stats can contain
        // records for tommorow (display_trend in such case accumulates
        // display_count of today). That is why the query is always limited
        // to records before tommorow even the user does not specify the
        // till date.
        if(till == null)
            till = LocalDate.now();
        Formatter query = new Formatter()
            .format("SELECT day as `date`, display_count")
            .format(" FROM document_stats")
            .format(" WHERE document_id='%s'", documentId)
            .format(" AND day<='%tF'", till);
        if(since != null)
            query.format(" AND day>='%tF'", since);
        return this.jdbc.queryForList(query.toString());
    }
}
