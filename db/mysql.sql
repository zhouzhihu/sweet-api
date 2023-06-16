SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for egd_esb_api
-- ----------------------------
DROP TABLE IF EXISTS `egd_esb_api`;
CREATE TABLE `egd_esb_api`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父ID',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型(api:api;folder:文件夹)',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '路径',
  `response_body` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '输出结果',
  `request_body` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '请求体',
  `description` varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接口描述',
  `order_no` int NULL DEFAULT NULL COMMENT '排序号',
  `tenant` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'API接口' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for egd_esb_api_actuator
-- ----------------------------
DROP TABLE IF EXISTS `egd_esb_api_actuator`;
CREATE TABLE `egd_esb_api_actuator`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `api_id` bigint NULL DEFAULT NULL COMMENT '接口ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关键字',
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行用户',
  `config` varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '连接配置(JSON格式)',
  `timeout` int NULL DEFAULT NULL COMMENT '请求超时(单位：秒，0/-1代表不超时)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'API执行器' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for egd_esb_api_field
-- ----------------------------
DROP TABLE IF EXISTS `egd_esb_api_field`;
CREATE TABLE `egd_esb_api_field`  (
  `api_id` bigint NOT NULL COMMENT '接口ID',
  `field_id` bigint NOT NULL COMMENT '字段ID',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'API接口和参数关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for egd_esb_api_script
-- ----------------------------
DROP TABLE IF EXISTS `egd_esb_api_script`;
CREATE TABLE `egd_esb_api_script`  (
  `api_id` bigint NOT NULL COMMENT '接口ID',
  `script` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '脚本',
  PRIMARY KEY (`api_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'Api接口执行脚本' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for egd_esb_connection
-- ----------------------------
DROP TABLE IF EXISTS `egd_esb_connection`;
CREATE TABLE `egd_esb_connection`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `config` varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '连接配置(JSON格式)',
  `extend_config` varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '扩展配置(JSON格式)',
  `timeout` int NULL DEFAULT NULL COMMENT '请求超时(单位：秒，0/-1代表不超时)',
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关键字',
  `tenant` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '数据源管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for egd_esb_field
-- ----------------------------
DROP TABLE IF EXISTS `egd_esb_field`;
CREATE TABLE `egd_esb_field`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段名',
  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段值',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段类型',
  `required` int NULL DEFAULT NULL COMMENT '是否必填(0:非必填/1:必填)',
  `default_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认值',
  `validate_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '验证类型(0:不验证;1:表达式验证;2:正则验证)',
  `expression` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '验证表达式',
  `error` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '验证说明',
  `is_array` int NULL DEFAULT 0 COMMENT '字段是否数组(0:否/1:是)',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'API参数' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for egd_esb_file
-- ----------------------------
DROP TABLE IF EXISTS `egd_esb_file`;
CREATE TABLE `egd_esb_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `unid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'UNID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父ID',
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关键字',
  `file_id` bigint NULL DEFAULT NULL COMMENT '文件ID',
  `file_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  `file_size` decimal(10, 2) NULL DEFAULT NULL COMMENT '文件大小',
  `file_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件路径',
  `local_file_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '本地文件路径',
  `tenant` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文件管理' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
