/*
Navicat MySQL Data Transfer

Source Server         : LocalHost
Source Server Version : 50525
Source Host           : localhost:3306
Source Database       : l2mythras

Target Server Type    : MYSQL
Target Server Version : 50525
File Encoding         : 65001

Date: 2020-05-03 02:31:05
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `bbs_clannotice`
-- ----------------------------
DROP TABLE IF EXISTS `bbs_clannotice`;
CREATE TABLE `bbs_clannotice` (
  `clan_id` int(10) unsigned NOT NULL,
  `type` smallint(6) NOT NULL DEFAULT '0',
  `notice` text NOT NULL,
  PRIMARY KEY (`clan_id`,`type`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of bbs_clannotice
-- ----------------------------
