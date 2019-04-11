CREATE TABLE IF NOT EXISTS `t_msg_delaymsg` (
  `id` varchar(32) NOT NULL,
  `channel` varchar(32) NOT NULL,
  `create_time` datetime NOT NULL,
  `delay_seconds` int(10) NOT NULL,
  `memery_flag` char(1) NOT NULL DEFAULT 'N',
  `content` text,
  `expire_time` datetime NOT NULL,
  `shard_info` varchar(128) NOT NULL,
  PRIMARY KEY (`id`)
)
