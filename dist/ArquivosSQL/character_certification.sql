SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `character_certification`
-- ----------------------------
DROP TABLE IF EXISTS `character_certification`;
CREATE TABLE `character_certification` (
  `object_id` int(11) NOT NULL DEFAULT '0',
  `index` tinyint(4) NOT NULL DEFAULT '0',
  `emergent1` tinyint(4) NOT NULL DEFAULT '0',
  `emergent2` tinyint(4) NOT NULL DEFAULT '0',
  `emergent3` tinyint(4) NOT NULL DEFAULT '0',
  `emergent4` tinyint(4) NOT NULL DEFAULT '0',
  `skill1` smallint(6) NOT NULL DEFAULT '0',
  `skill2` smallint(6) NOT NULL DEFAULT '0',
  `skill3` smallint(6) NOT NULL DEFAULT '0',
  `transform1` smallint(6) NOT NULL DEFAULT '0',
  `transform2` smallint(6) NOT NULL DEFAULT '0',
  `transform3` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`object_id`,`index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
