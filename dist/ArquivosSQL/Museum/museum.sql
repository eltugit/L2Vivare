-- ----------------------------
-- Table structure for museum_last_statistics
-- ----------------------------
DROP TABLE IF EXISTS `museum_last_statistics`;
CREATE TABLE `museum_last_statistics` (
  `objectId` int(10) NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL,
  `category` varchar(50) NOT NULL DEFAULT '',
  `timer` varchar(15) NOT NULL,
  `count` bigint(13) NOT NULL DEFAULT '0',
  PRIMARY KEY (`objectId`,`category`,`timer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for museum_statistics
-- ----------------------------
DROP TABLE IF EXISTS `museum_statistics`;
CREATE TABLE `museum_statistics` (
  `objectId` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL,
  `category` varchar(30) NOT NULL,
  `monthly_count` bigint(13) NOT NULL DEFAULT '0',
  `weekly_count` bigint(13) NOT NULL DEFAULT '0',
  `daily_count` bigint(13) NOT NULL DEFAULT '0',
  `total_count` bigint(13) NOT NULL DEFAULT '0',
  `hasReward` smallint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`objectId`,`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;