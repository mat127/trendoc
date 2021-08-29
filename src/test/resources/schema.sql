DROP TABLE IF EXISTS `document_stats`;
CREATE TABLE `document_stats` (
  `document_id` varchar(36) NOT NULL,
  `day` date NOT NULL,
  `display_count` int DEFAULT '0',
  `display_trend` int DEFAULT '0',
  PRIMARY KEY (`day`,`document_id`)
);
