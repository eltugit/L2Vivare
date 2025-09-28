-- ----------------------------
-- Table structure for `character_counters`
-- ----------------------------
DROP TABLE IF EXISTS `character_counters`;
CREATE TABLE `character_counters` (
  `char_id` int(11) NOT NULL,
  `highest_karma` int(11) NOT NULL DEFAULT '0',
  `times_died` int(11) NOT NULL DEFAULT '0',
  `mobs_killed` int(11) NOT NULL DEFAULT '0',
  `raids_killed` int(11) NOT NULL DEFAULT '0',
  `summons_killed` int(11) NOT NULL DEFAULT '0',
  `recipes_crafted` int(11) NOT NULL DEFAULT '0',
  `recipes_failed` int(11) NOT NULL DEFAULT '0',
  `longest_spree` int(11) NOT NULL DEFAULT '0',
  `sprees_ended` int(11) NOT NULL DEFAULT '0',
  `crits_done` int(11) NOT NULL DEFAULT '0',
  `mscrits_done` int(11) NOT NULL DEFAULT '0',
  `sieges_won` int(11) NOT NULL DEFAULT '0',
  `fortresses_won` int(11) NOT NULL DEFAULT '0',
  `most_adena` bigint(22) NOT NULL DEFAULT '0',
  `achievments_done` int(2) NOT NULL DEFAULT '0',
  `duels_won` int(11) NOT NULL DEFAULT '0',
  `players_ressurected` int(11) NOT NULL DEFAULT '0',
  `seeds_extracted` int(2) NOT NULL DEFAULT '0',
  `treasure_box_opened` int(11) NOT NULL DEFAULT '0',
  `fish_catched` int(11) NOT NULL DEFAULT '0',
  `antharasKilled` int(11) NOT NULL DEFAULT '0',
  `baiumKilled` int(11) NOT NULL DEFAULT '0',
  `valakasKilled` int(11) NOT NULL DEFAULT '0',
  `orfenKilled` int(11) NOT NULL DEFAULT '0',
  `antQueenKilled` int(11) NOT NULL DEFAULT '0',
  `coreKilled` int(11) NOT NULL DEFAULT '0',
  `belethKilled` int(11) NOT NULL DEFAULT '0',
  `sailrenKilled` int(11) NOT NULL DEFAULT '0',
  `baylorKilled` int(11) NOT NULL DEFAULT '0',
  `zakenKilled` int(11) NOT NULL DEFAULT '0',
  `tiatKilled` int(11) NOT NULL DEFAULT '0',
  `freyaKilled` int(11) NOT NULL DEFAULT '0',
  `frintezzaKilled` int(11) NOT NULL DEFAULT '0',
  `highestEnchant` int(11) NOT NULL DEFAULT '0',
  `enchantBlessedSucceeded` int(11) NOT NULL DEFAULT '0',
  `enchantNormalSucceeded` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`char_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of character_counters
-- ----------------------------
