/*

 Date: 06/01/2019 13:43:01
*/

SET NAMES utf8mb4;


-- ----------------------------
-- Table structure for User
-- ----------------------------
DROP TABLE IF EXISTS `User`;
CREATE TABLE `User`  (
  `id` bigint(20) NOT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of User
-- ----------------------------
INSERT INTO `User` VALUES (1, 'admin', '$2a$10$rIX9ewyHU2ROytz1ryWEi.YLEtxIhgcQi8WR/7YAcKBl/rZ/4m0jC');
INSERT INTO `User` VALUES (2, '123456', '$2a$10$rIX9ewyHU2ROytz1ryWEi.YLEtxIhgcQi8WR/7YAcKBl/rZ/4m0jC');
INSERT INTO `User` VALUES (221, 'test021', '$2a$10$D6iS9ohqNq338TqMRe8AN.ojrjDHDYIjgLSlUQkfacWrml6VZjuTK');
INSERT INTO `User` VALUES (222, 'test01', '$2a$10$1.4hJD5uefd/u5ARik0xE.pKsgaufAHS4gfLFvMHPkCPMr7hzduFG');



-- ----------------------------
-- Table structure for RoleUser
-- ----------------------------
DROP TABLE IF EXISTS `RoleUser`;
CREATE TABLE `RoleUser`  (
  `id` bigint(20) NOT NULL,
  `userId` bigint(20) NULL DEFAULT NULL,
  `roleId` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of RoleUser
-- ----------------------------
INSERT INTO `RoleUser` VALUES (1, 1, 1);
INSERT INTO `RoleUser` VALUES (2, 2, 2);



SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for Role
-- ----------------------------
DROP TABLE IF EXISTS `Role`;
CREATE TABLE `Role` (
  `id` bigint(20) NOT NULL,
  `rolename` varchar(255) DEFAULT NULL,
  `roledesc` varchar(100) DEFAULT NULL COMMENT '角色描述',
  `createTime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of Role
-- ----------------------------
INSERT INTO `Role` VALUES ('1', 'ROLE_ADMIN', '系统管理员', '2019-01-21 11:02:32');
INSERT INTO `Role` VALUES ('2', 'ROLE_USER', '用户测试001', '2019-01-21 11:02:42');


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for Permission
-- ----------------------------
DROP TABLE IF EXISTS `Permission`;
CREATE TABLE `Permission` (
  `per_id` bigint(11) NOT NULL,
  `per_parent_id` bigint(11) DEFAULT NULL,
  `per_name` varchar(100) DEFAULT NULL COMMENT '权限名称',
  `per_resource` varchar(100) DEFAULT NULL COMMENT '权限资源',
  `per_type` varchar(100) DEFAULT NULL COMMENT '权限类型',
  `per_icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `per_describe` varchar(100) DEFAULT NULL COMMENT '权限描述',
  `per_component` varchar(255) DEFAULT NULL,
  `per_sort` int(11) DEFAULT NULL,
  `per_crtTime` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`per_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of Permission
-- ----------------------------
INSERT INTO `Permission` VALUES ('1101', '0', '权限管理', 'auth', 'menu', 'auth', '权限管理菜单', '', '1', '2019-01-24 10:04:22');
INSERT INTO `Permission` VALUES ('1102', '1101', '角色管理', 'role', 'menu', 'role', '角色管理菜单', 'pre/role/index', '101', '2019-04-18 16:05:03');
INSERT INTO `Permission` VALUES ('1103', '1101', '资源管理', 'per', 'menu', 'resource', '资源管理菜单', 'pre/perm/index', '102', '2019-01-22 10:51:43');
INSERT INTO `Permission` VALUES ('285779921140461568', '1101', '角色权限', 'roleauth', 'menu', 'roleauth', null, 'pre/roleauth/index', '104', '2019-01-24 12:29:52');



SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for RolePermission
-- ----------------------------
DROP TABLE IF EXISTS `RolePermission`;
CREATE TABLE `RolePermission` (
  `rp_id` bigint(20) NOT NULL,
  `rp_role_id` bigint(20) DEFAULT NULL COMMENT '角色id',
  `rp_per_id` bigint(20) DEFAULT NULL COMMENT '权限id',
  PRIMARY KEY (`rp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of RolePermission
-- ----------------------------
INSERT INTO `RolePermission` VALUES ('286859857880166400', '1', '1101');
INSERT INTO `RolePermission` VALUES ('286859857980829696', '1', '1102');
INSERT INTO `RolePermission` VALUES ('286859858081492992', '1', '1103');
INSERT INTO `RolePermission` VALUES ('286859858177961984', '1', '285779921140461568');
INSERT INTO `RolePermission` VALUES ('286904710097809408', '2', '1101');
INSERT INTO `RolePermission` VALUES ('286904710232027136', '2', '1102');
INSERT INTO `RolePermission` VALUES ('286904710332690432', '2', '1103');


