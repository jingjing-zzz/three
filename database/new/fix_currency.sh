#!/bin/bash
docker exec mitedtsm-mysql mysql -u root -p1234 mitedtsm_database -e "ALTER TABLE system_tenant ADD COLUMN currency_code VARCHAR(10) NOT NULL DEFAULT 'CNY';"
docker exec mitedtsm-mysql mysql -u root -p1234 mitedtsm_database -e "UPDATE system_tenant SET currency_code = 'CNY' WHERE currency_code IS NULL OR currency_code = '';"
echo "Done!"
