/*
Navicat MySQL Data Transfer

Source Server         : LocalHost
Source Server Version : 50525
Source Host           : localhost:3306
Source Database       : l2mythras

Target Server Type    : MYSQL
Target Server Version : 50525
File Encoding         : 65001

Date: 2020-05-03 03:50:43
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `clan_requiements`
-- ----------------------------
DROP TABLE IF EXISTS `clan_requiements`;
CREATE TABLE `clan_requiements` (
  `clan_id` int(11) NOT NULL,
  `recruting` tinyint(2) DEFAULT NULL,
  `classes` text,
  `question1` text,
  `question2` text,
  `question3` text,
  `question4` text,
  `question5` text,
  `question6` text,
  `question7` text,
  `question8` text,
  PRIMARY KEY (`clan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of clan_requiements
-- ----------------------------
INSERT INTO `clan_requiements` VALUES ('268486820', '0', '88,90,92,94,96,98,100,102,104,106,108,110,112,114,116,118,89,91,93,95,97,99,101,103,105,107,109,111,113,115,117', ' dsa ', ' dsa ', ' dsa ', ' dsa ', '', '', '', '');
