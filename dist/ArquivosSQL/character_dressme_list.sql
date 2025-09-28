/*
Navicat MySQL Data Transfer

Source Server         : LocalHost
Source Server Version : 50525
Source Host           : localhost:3306
Source Database       :

Target Server Type    : MYSQL
Target Server Version : 50525
File Encoding         : 65001

Date: 2020-04-30 03:30:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `character_dressme_armor_list`
-- ----------------------------
DROP TABLE IF EXISTS `character_dressme_armor_list`;
CREATE TABLE `character_dressme_armor_list` (
  `charId` varchar(255) NOT NULL,
  `dressId` bigint(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of character_dressme_armor_list
-- ----------------------------

-- ----------------------------
-- Table structure for `character_dressme_cloak_list`
-- ----------------------------
DROP TABLE IF EXISTS `character_dressme_cloak_list`;
CREATE TABLE `character_dressme_cloak_list` (
  `charId` bigint(255) NOT NULL,
  `cloakDressId` bigint(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of character_dressme_cloak_list
-- ----------------------------

-- ----------------------------
-- Table structure for `character_dressme_hat_list`
-- ----------------------------
DROP TABLE IF EXISTS `character_dressme_hat_list`;
CREATE TABLE `character_dressme_hat_list` (
  `charId` bigint(255) NOT NULL,
  `hatDressId` bigint(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of character_dressme_hat_list
-- ----------------------------

-- ----------------------------
-- Table structure for `character_dressme_shield_list`
-- ----------------------------
DROP TABLE IF EXISTS `character_dressme_shield_list`;
CREATE TABLE `character_dressme_shield_list` (
  `charId` bigint(255) NOT NULL,
  `shieldDressId` bigint(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of character_dressme_shield_list
-- ----------------------------

-- ----------------------------
-- Table structure for `character_dressme_weapon_list`
-- ----------------------------
DROP TABLE IF EXISTS `character_dressme_weapon_list`;
CREATE TABLE `character_dressme_weapon_list` (
  `charId` bigint(255) NOT NULL,
  `weaponDressId` bigint(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of character_dressme_weapon_list
-- ----------------------------
