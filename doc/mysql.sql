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

