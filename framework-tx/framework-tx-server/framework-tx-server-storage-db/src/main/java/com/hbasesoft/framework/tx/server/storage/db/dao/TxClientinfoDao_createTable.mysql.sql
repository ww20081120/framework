CREATE TABLE `t_tx_checkinfo` (
  `id` varchar(32) NOT NULL,
  `mark` varchar(32) NOT NULL,
  `result` blob,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`,`mark`)
) ENGINE=MyISAM;

CREATE TABLE `t_tx_clientinfo` (
  `id` varchar(32) NOT NULL,
  `mark` varchar(255) NOT NULL,
  `context` text,
  `args` blob,
  `max_retry_times` tinyint(3) NOT NULL,
  `retry_configs` varchar(32) DEFAULT NULL,
  `next_retry_time` datetime NOT NULL,
  `current_retry_times` tinyint(3) NOT NULL,
  `client_info` varchar(64) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `i_tx_client_next_retry_time` (`next_retry_time`)
) ENGINE=MyISAM