/*
Navicat MySQL Data Transfer

Source Server         : LocalHost
Source Server Version : 50525
Source Host           : localhost:3306
Source Database       : l2mythras

Target Server Type    : MYSQL
Target Server Version : 50525
File Encoding         : 65001

Date: 2020-05-03 03:50:48
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `clan_petitions`
-- ----------------------------
DROP TABLE IF EXISTS `clan_petitions`;
CREATE TABLE `clan_petitions` (
  `sender_id` int(11) NOT NULL,
  `clan_id` int(11) NOT NULL,
  `answer1` text,
  `answer2` text,
  `answer3` text,
  `answer4` text,
  `answer5` text,
  `answer6` text,
  `answer7` text,
  `answer8` text,
  `comment` text,
  PRIMARY KEY (`sender_id`,`clan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of clan_petitions
-- ----------------------------
