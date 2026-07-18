ALTER TABLE system_tenant ADD COLUMN currency_code VARCHAR(10) NOT NULL DEFAULT 'CNY';
UPDATE system_tenant SET currency_code = 'CNY' WHERE currency_code IS NULL OR currency_code = '';
