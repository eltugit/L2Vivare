-- ----------------------------
-- Table structure for `character_achivment_levels`
-- ----------------------------
DROP TABLE IF EXISTS `character_achivment_levels`;
CREATE TABLE `character_achivment_levels` (
  `char_id` int(11) NOT NULL,
  `achivment_levels` varchar(500) NOT NULL DEFAULT '',
  PRIMARY KEY (`char_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of character_achivment_levels
-- ----------------------------
