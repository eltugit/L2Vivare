SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `character_dressme_enchant_list`
-- ----------------------------
DROP TABLE IF EXISTS `character_dressme_enchant_list`;
CREATE TABLE `character_dressme_enchant_list` (
  `charId` bigint(255) NOT NULL,
  `enchantId` bigint(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

