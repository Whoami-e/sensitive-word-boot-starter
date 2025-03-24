CREATE TABLE `sw_sensitive_word_dtl_log` (
                                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                             `busi_year` int NOT NULL COMMENT '年度',
                                             `word_log_id` bigint NOT NULL COMMENT '敏感词日志ID;sw_sensitive_word_log.id',
                                             `word_id` bigint NOT NULL COMMENT '敏感词ID;sw_sensitve_word.id',
                                             `trigger_location` int NOT NULL COMMENT '触发位置;1-正文；2-附件',
                                             `file_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件名',
                                             `file_ext` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '扩展名',
                                             `file_content` mediumtext COLLATE utf8mb4_general_ci COMMENT '文件内容',
                                             `trigger_cnt` int NOT NULL COMMENT '触发次数',
                                             `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
                                             `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='敏感词详情日志'

CREATE TABLE `sw_sensitive_word_log` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `busi_year` int NOT NULL COMMENT '年度',
                                         `req_method` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求方式;POST，GET等，ALL表示所有',
                                         `req_url` varchar(2048) COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求URL',
                                         `req_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求说明',
                                         `check_conf` varchar(2048) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '检查配置;JSON格式',
                                         `req_body` mediumtext COLLATE utf8mb4_general_ci NOT NULL COMMENT '请求报文',
                                         `trigger_type` int NOT NULL DEFAULT '1' COMMENT '触发类型 1-禁止性；2-提示性',
                                         `ipaddr` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '登录IP地址',
                                         `msg` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '提示消息',
                                         `login_location` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '登录地点',
                                         `browser` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '浏览器类型',
                                         `os` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作系统',
                                         `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
                                         `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='敏感词日志'

CREATE TABLE `sw_sensitve_word` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `word_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '敏感词内容',
                                    `word_type` int NOT NULL COMMENT '敏感词分类;1.暴恐；2.涉政；3.反动；4.违禁；10-特殊字符；99.其他',
                                    `trigger_type` int NOT NULL DEFAULT '1' COMMENT '触发类型;1-禁止性；2-提示信',
                                    `status` int NOT NULL DEFAULT '0' COMMENT '状态;0-正常；1-停用',
                                    `del_flag` int NOT NULL DEFAULT '1' COMMENT '删除标志;1-正常；0-删除',
                                    `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
                                    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                    PRIMARY KEY (`id`),
                                    KEY `sw_sensitve_word__inx_word_name` (`word_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3511 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='敏感词信息表'

