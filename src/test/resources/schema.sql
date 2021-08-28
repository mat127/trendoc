CREATE TABLE `document_stats` (
  `document_id` varchar(36) NOT NULL,
  `day` date NOT NULL,
  `display_count` int DEFAULT '0',
  PRIMARY KEY (`document_id`,`day`)
);
