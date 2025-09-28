SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `character_dressme_agathion_list`
-- ----------------------------
DROP TABLE IF EXISTS `character_dressme_agathion_list`;
CREATE TABLE `character_dressme_agathion_list` (
  `charId` bigint(255) NOT NULL,
  `agathionId` bigint(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
