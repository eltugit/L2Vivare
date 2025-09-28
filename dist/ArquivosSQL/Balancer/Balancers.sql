/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1_3306
Source Server Version : 50622
Source Host           : 127.0.0.1:3306
Source Database       : l2j

Target Server Type    : MYSQL
Target Server Version : 50622
File Encoding         : 65001

Date: 2015-02-20 02:05:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for class_balance
-- ----------------------------
DROP TABLE IF EXISTS `class_balance`;
CREATE TABLE `class_balance` (
  `key` int(10) NOT NULL,
  `forOlympiad` tinyint(1) NOT NULL DEFAULT '0',
  `normal` double(10,2) NOT NULL DEFAULT '1.00',
  `normalCrit` double(10,2) NOT NULL DEFAULT '1.00',
  `magic` double(10,2) NOT NULL DEFAULT '1.00',
  `magicCrit` double(10,2) NOT NULL DEFAULT '1.00',
  `blow` double(10,2) NOT NULL DEFAULT '1.00',
  `physSkill` double(10,2) NOT NULL DEFAULT '1.00',
  `physSkillCrit` double(10,2) NOT NULL DEFAULT '1.00',
  `classId` int(3) NOT NULL DEFAULT '-1',
  `targetClassId` int(3) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`key`,`forOlympiad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for skills_balance
-- ----------------------------
DROP TABLE IF EXISTS `skills_balance`;
CREATE TABLE `skills_balance` (
  `key` int(10) NOT NULL,
  `forOlympiad` tinyint(1) NOT NULL DEFAULT '0',
  `power` double(10,2) NOT NULL DEFAULT '1.00',
  `chance` double(10,2) NOT NULL DEFAULT '1.00',
  `skillId` int(6) NOT NULL DEFAULT '-1',
  `targetClassId` int(3) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`key`,`forOlympiad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
