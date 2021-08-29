package com.github.mat127.trendoc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentStatsController {

    @Autowired
    DocumentStatsService stats;

    @GetMapping("/document/{document_id}/stats")
    public List<Map<String,Object>> getDocumentStats(
        @PathVariable("document_id") final UUID documentId,
        @RequestParam(required = false) final LocalDate since,
        @RequestParam(required = false) final LocalDate till
    ) {
        return this.stats.getDocumentStats(documentId, since, till);
    }

    @GetMapping("/document/trending")
    public List<Map<String, Object>> getTrendingDocuments(
        @RequestParam(required = false) LocalDate since,
        @RequestParam(required = false) final LocalDate till,
        @RequestParam(value = "min_disp_count", defaultValue = "1000") final int minDisplayCount,
        @RequestParam(defaultValue = "10") final int limit
    ) {
        // if no dates specified get last 7 days ("week") documents
        if(since == null && till == null)
            since = LocalDate.now().minusDays(7);
        return this.stats.getTrendingDocuments(since, till, minDisplayCount, limit);
    }
}
