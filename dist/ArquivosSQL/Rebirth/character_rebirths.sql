-- ----------------------------
-- Table structure for `character_rebirths`
-- ----------------------------
DROP TABLE IF EXISTS `character_rebirths`;
CREATE TABLE `character_rebirths` (
  `playerId` int(20) NOT NULL,
  `rebirthCount` int(2) NOT NULL,
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of character_rebirths
-- ----------------------------
