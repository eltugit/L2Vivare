/*
Navicat MySQL Data Transfer

Source Server         : LocalHost
Source Server Version : 50525
Source Host           : localhost:3306
Source Database       : l2mythras

Target Server Type    : MYSQL
Target Server Version : 50525
File Encoding         : 65001

Date: 2020-05-23 15:59:22
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `auctions`
-- ----------------------------
DROP TABLE IF EXISTS `auctions`;
CREATE TABLE `auctions` (
  `auction_id` bigint(20) NOT NULL,
  `seller_object_id` int(11) DEFAULT NULL,
  `seller_name` varchar(35) DEFAULT NULL,
  `item_object_id` int(11) DEFAULT NULL,
  `price_per_item` bigint(13) DEFAULT NULL,
  PRIMARY KEY (`auction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of auctions
-- ----------------------------
