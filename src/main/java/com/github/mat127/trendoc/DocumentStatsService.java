package com.github.mat127.trendoc;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DocumentStatsService {

    public List<UUID> getTrendingDoumentIds(
        final Date since, final Date till
    ) {
        return Collections.singletonList(UUID.randomUUID());
    }
}
