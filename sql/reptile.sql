/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3306
 Source Schema         : reptile

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 29/03/2019 22:39:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article_content
-- ----------------------------
DROP TABLE IF EXISTS `article_content`;
CREATE TABLE `article_content`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` blob NOT NULL COMMENT '文章详细内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_info
-- ----------------------------
DROP TABLE IF EXISTS `article_info`;
CREATE TABLE `article_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `source` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '来源',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '文章标题',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '文章链接',
  `release_time` int(11) NOT NULL COMMENT '发布时间',
  `author` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '作者',
  `create_time` int(11) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `article_info_create_time_index`(`create_time`) USING BTREE,
  INDEX `article_info_release_time_index`(`release_time`) USING BTREE,
  INDEX `article_info_source_index`(`source`) USING BTREE,
  INDEX `article_info_title_index`(`title`) USING BTREE,
  INDEX `article_info_url_index`(`url`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_info
-- ----------------------------
INSERT INTO `article_info` VALUES (1, '游研社', '【油盐晚报0329】救救波兰蠢驴，GOG平台年利润仅7800美元', 'http://www.yystv.cn/p/4845', 1553869728, '社长的实习生', 1553869728);
INSERT INTO `article_info` VALUES (2, '游研社', '终于等到《无主之地3》：PAX EAST 2019发布会汇总', 'http://www.yystv.cn/p/4843', 1553869728, '纨绔乖张', 1553869728);
INSERT INTO `article_info` VALUES (3, '游研社', '只狼，真正的“武侠”', 'http://www.yystv.cn/p/4844', 1553869728, 'Soulframe', 1553869728);
INSERT INTO `article_info` VALUES (4, '游研社', '欧盟新版权法通过了，继续引起了众怒', 'http://www.yystv.cn/p/4842', 1553869728, '空白缠绕', 1553869728);
INSERT INTO `article_info` VALUES (5, '游研社', '《美少女战士》到底影响了多少男人？', 'http://www.yystv.cn/p/4841', 1553869728, '地球人研究报告', 1553869728);
INSERT INTO `article_info` VALUES (6, '游研社', '去年我们推荐的大学生免费游学机会，被19名同学抓住了', 'http://www.yystv.cn/p/4846', 1553869728, 'C9', 1553869728);
INSERT INTO `article_info` VALUES (7, '游研社', '熊孩子们走了，《我的世界》又变酷了', 'http://www.yystv.cn/p/4839', 1553869728, 'kong', 1553869728);
INSERT INTO `article_info` VALUES (8, '游研社', '14岁少年靠玩《堡垒之夜》挣了20万美元', 'http://www.yystv.cn/p/4840', 1553869728, '跳跳', 1553869728);
INSERT INTO `article_info` VALUES (9, '游研社', '【油盐晚报0328】平井一夫将于6月退休，《无主之地》新作或将公布', 'http://www.yystv.cn/p/4837', 1553869728, '社长的实习生', 1553869728);
INSERT INTO `article_info` VALUES (10, '游研社', '人们在《只狼》里受的苦，最后都变成了沙雕图', 'http://www.yystv.cn/p/4836', 1553869728, '偶然轻狂', 1553869728);
INSERT INTO `article_info` VALUES (11, '游研社', '这个“论坛版主模拟器”，是一封写给拨号上网年代的情书', 'http://www.yystv.cn/p/4835', 1553869728, 'kong', 1553869728);
INSERT INTO `article_info` VALUES (12, '游研社', '【油盐晚报0327】EA裁员350人，SQUARE ENIX又有骚操作', 'http://www.yystv.cn/p/4834', 1553869728, '社长的实习生', 1553869728);

-- ----------------------------
-- Table structure for game_appear_recoed
-- ----------------------------
DROP TABLE IF EXISTS `game_appear_recoed`;
CREATE TABLE `game_appear_recoed`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `game_id` int(11) NOT NULL COMMENT '游戏名称ID',
  `release_time` int(11) NOT NULL COMMENT '发布时间',
  `article_id` int(11) NOT NULL COMMENT '文章ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `game_appear_recoed_article_id_index`(`article_id`) USING BTREE,
  INDEX `game_appear_recoed_game_id_index`(`game_id`) USING BTREE,
  INDEX `game_appear_recoed_release_time_index`(`release_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for game_info
-- ----------------------------
DROP TABLE IF EXISTS `game_info`;
CREATE TABLE `game_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `game_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '游戏名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for web_info
-- ----------------------------
DROP TABLE IF EXISTS `web_info`;
CREATE TABLE `web_info`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '网站地址',
  `last_time` int(11) NULL DEFAULT NULL COMMENT '最后爬取时间',
  `web_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '网站名称',
  `article_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '文章获取地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of web_info
-- ----------------------------
INSERT INTO `web_info` VALUES (1, 'http://www.yystv.cn/home/get_home_docs_by_page', NULL, '游研社', 'http://www.yystv.cn/p/');

SET FOREIGN_KEY_CHECKS = 1;
