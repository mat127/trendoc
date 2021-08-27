CREATE TABLE `doc_stats` (
  `doc_id` varchar(36) DEFAULT NULL,
  `day` date DEFAULT NULL,
  `display_count` int DEFAULT '0'
);

INSERT INTO doc_stats (doc_id) SELECT UUID();
INSERT INTO doc_stats (doc_id) SELECT UUID();
INSERT INTO doc_stats (doc_id) SELECT UUID();
