-- 修改 expense 表
ALTER TABLE expense
    MODIFY COLUMN amount DECIMAL(10,2) NOT NULL COMMENT '支出金额',
    MODIFY COLUMN category VARCHAR(50) NOT NULL COMMENT '支出类别',
    MODIFY COLUMN description VARCHAR(255) COMMENT '支出描述',
    MODIFY COLUMN expense_date DATETIME NOT NULL COMMENT '支出日期',
    ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除' AFTER update_time,
    ADD INDEX idx_user_date (user_id, expense_date);

-- 修改 income 表
ALTER TABLE income
    MODIFY COLUMN amount DECIMAL(10,2) NOT NULL COMMENT '收入金额',
    MODIFY COLUMN category VARCHAR(50) NOT NULL COMMENT '收入类别',
    MODIFY COLUMN description VARCHAR(255) COMMENT '收入描述',
    MODIFY COLUMN income_date DATETIME NOT NULL COMMENT '收入日期',
    ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除' AFTER update_time,
    ADD INDEX idx_user_date (user_id, income_date); 