-- 商机域字段扩展：添加来源和竞争对手字段
ALTER TABLE `crm_business`
ADD COLUMN `source` VARCHAR(30) DEFAULT NULL COMMENT '商机来源' AFTER `name`,
ADD COLUMN `competitor` VARCHAR(100) DEFAULT NULL COMMENT '竞争对手' AFTER `source`;
