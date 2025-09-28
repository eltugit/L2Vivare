/*
Navicat MySQL Data Transfer

Source Server         : LocalHost
Source Server Version : 50525
Source Host           : localhost:3306
Source Database       : l2wardenfreya@159786

Target Server Type    : MYSQL
Target Server Version : 50525
File Encoding         : 65001

Date: 2020-05-02 00:39:57
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `character_custom_hero`
-- ----------------------------
DROP TABLE IF EXISTS `character_custom_hero`;
CREATE TABLE `character_custom_hero` (
  `charId` int(10) NOT NULL,
  `hero` int(8) NOT NULL DEFAULT '1',
  `hero_reg_time` bigint(40) NOT NULL DEFAULT '0',
  `hero_time` bigint(40) NOT NULL DEFAULT '0',
  PRIMARY KEY (`charId`),
  KEY `charId` (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of character_custom_hero
-- ----------------------------
