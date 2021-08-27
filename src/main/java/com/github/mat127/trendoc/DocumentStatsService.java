package com.github.mat127.trendoc;

import java.util.Date;
import java.util.List;
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
        return this.jdbc.query(
            "SELECT doc_id FROM doc_stats",
            (rs, row) -> UUID.fromString(rs.getString("doc_id"))
        );
    }
}
