package com.github.mat127.trendoc;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrendingDocumentsController {

    @Autowired
    DocumentStatsService stats;

    @GetMapping("/trending")
	public List<UUID> getTrendingDocuments(
        @RequestParam(required = false) Date since,
        @RequestParam(required = false) Date till
    ) {
        return this.stats.getTrendingDoumentIds(since, till);
	}
}
