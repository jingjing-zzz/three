# 商机域数据库迁移文档

## 概述
本文档记录商机域开发过程中新增的所有增量SQL脚本，供团队成员整合使用。

## 重要注意事项
- 执行MySQL命令时**必须**添加 `--default-character-set=utf8mb4` 参数，否则中文数据会出现乱码
- Docker Compose 的 `init-mysql.sh` 只会在数据库容器**第一次启动**时执行（数据目录为空时），后续重启不会重新执行
- 如果数据库已运行，需要手动执行增量SQL
- SQL文件之间可能存在依赖关系，必须按推荐顺序执行

## 增量SQL文件列表

### 1. 文件分片上传
**文件**: `database/new/new-large-file-upload.sql`

**功能**: 创建文件分片上传任务表
- `infra_file_upload_task` - 文件上传任务表

**执行顺序**: 基础表之后，无依赖

### 2. 菜单国际化
**文件**: `database/new/new-i18n.sql`

**功能**: 创建菜单国际化表
- `system_menu_i18n` - 菜单国际化表

**执行顺序**: 基础表之后，为其他国际化表的依赖

### 3. Mall装修菜单国际化
**文件**: `database/new/new-mall-i18n.sql`

**功能**: 创建Mall装修菜单国际化表
- `promotion_diy_menu_i18n` - 装修菜单国际化表
- 添加 `system_tenant.currency_code` 字段

**执行顺序**: 依赖 `system_menu_i18n` 表存在

### 4. 商品分类国际化
**文件**: `database/new/new-product-category-i18n.sql`

**功能**: 创建商品分类国际化表
- `product_category_i18n` - 商品分类国际化表

**执行顺序**: 基础表之后

### 5. 阿拉伯语国际化数据
**文件**: `database/new/new-i18n-ar.sql`

**功能**: 插入阿拉伯语菜单国际化数据

**执行顺序**: 依赖 `system_menu_i18n` 表存在

### 6. 商机字段扩展
**文件**: `database/new/new-crm-business-fields.sql`

**功能**: 为商机表新增字段
- `source` - 商机来源（字典类型：crm_business_source）
- `competitor` - 竞争对手

**执行顺序**: 必须在基础表创建之后执行

**内容**:
```sql
ALTER TABLE `crm_business`
ADD COLUMN `source` VARCHAR(30) DEFAULT NULL COMMENT '商机来源' AFTER `name`,
ADD COLUMN `competitor` VARCHAR(100) DEFAULT NULL COMMENT '竞争对手' AFTER `source`;
```

### 7. 商机报价表
**文件**: `database/new/new-crm-business-quotation.sql`

**功能**: 创建商机报价相关表
- `crm_business_quotation` - 商机报价主表
- `crm_business_quotation_item` - 报价产品项表

**执行顺序**: 依赖 `crm_business` 表存在

### 8. 商机报价快照表
**文件**: `database/new/new-crm-business-quotation-snapshot.sql`

**功能**: 创建报价快照表，用于记录报价确认时的状态快照

**执行顺序**: 依赖 `crm_business_quotation` 表存在，必须在报价表之后执行

### 9. 商机来源字典数据
**文件**: `database/new/new-crm-business-source-dict.sql`

**功能**: 插入商机来源字典数据
- 主动开发 (value: 1)
- 客户介绍 (value: 2)
- 网络推广 (value: 3)
- 展会 (value: 4)
- 电话营销 (value: 5)
- 其他 (value: 6)

**执行顺序**: 依赖 `system_dict_type` 和 `system_dict_data` 表存在

### 10. 字典中文乱码修复
**文件**: `database/new/fix_dict_chinese.sql`

**功能**: 修复商机来源字典中文乱码问题（当直接执行字典SQL出现中文乱码时使用）

**执行顺序**: 在 `new-crm-business-source-dict.sql` 之后执行

### 11. 租户货币代码修复
**文件**: `database/new/fix_currency_code.sql`

**功能**: 添加租户表货币代码字段（与 new-mall-i18n.sql 中一致）

**执行顺序**: 在 `new-mall-i18n.sql` 之后执行（或单独执行）

## 推荐执行顺序

```bash
# 注意：所有mysql命令必须添加 --default-character-set=utf8mb4 参数

# 1. 先执行基础表（原有初始化）
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/base/quartz.sql
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/base/ruoyi-vue-pro.sql

# 2. 执行其他基础表
for f in database/base/*.sql; do
    if [[ $f != *quartz.sql && $f != *ruoyi-vue-pro.sql ]]; then
        mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < $f
    fi
done

# 3. 执行通用增量SQL（按依赖顺序）
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-i18n.sql
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-large-file-upload.sql
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-mall-i18n.sql
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-product-category-i18n.sql
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-i18n-ar.sql

# 4. 执行商机域增量SQL（按依赖顺序）
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-crm-business-fields.sql
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-crm-business-quotation.sql
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-crm-business-quotation-snapshot.sql
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-crm-business-source-dict.sql

# 5. 如果中文乱码，执行修复脚本
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/fix_dict_chinese.sql

# 6. 如果currency_code字段缺失，执行修复脚本
mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/fix_currency_code.sql
```

## Docker Compose 初始化

### 启动顺序说明
Docker Compose 部署时，服务启动顺序为：
1. MySQL 数据库容器启动
2. 初始化容器执行 `init-mysql.sh`（**仅第一次启动执行**）
3. 后端服务容器启动

### init-mysql.sh 脚本内容
确保 `docker-compose/init/init-mysql.sh` 脚本包含以下执行步骤：

```bash
#!/bin/bash

set -e

echo "Starting MySQL initialization..."

until mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" -e "SELECT 1"; do
    echo "Waiting for MySQL to be ready..."
    sleep 2
done

echo "MySQL is ready, executing SQL files..."

SQL_BASE_DIR="/docker-init-sql/base"
SQL_NEW_DIR="/docker-init-sql/new"

echo "Executing: quartz.sql"
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_BASE_DIR}/quartz.sql"

echo "Executing: ruoyi-vue-pro.sql"
mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_BASE_DIR}/ruoyi-vue-pro.sql"

for sql_file in "${SQL_BASE_DIR}"/*.sql; do
    filename=$(basename "$sql_file")
    if [[ "$filename" != "quartz.sql" && "$filename" != "ruoyi-vue-pro.sql" ]]; then
        echo "Executing: ${filename}"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "$sql_file"
    fi
done

if [ -d "$SQL_NEW_DIR" ]; then
    # 通用增量SQL（按依赖顺序）
    if [ -f "${SQL_NEW_DIR}/new-i18n.sql" ]; then
        echo "Executing: new-i18n.sql"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_NEW_DIR}/new-i18n.sql"
    fi

    if [ -f "${SQL_NEW_DIR}/new-large-file-upload.sql" ]; then
        echo "Executing: new-large-file-upload.sql"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_NEW_DIR}/new-large-file-upload.sql"
    fi

    if [ -f "${SQL_NEW_DIR}/new-mall-i18n.sql" ]; then
        echo "Executing: new-mall-i18n.sql"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_NEW_DIR}/new-mall-i18n.sql"
    fi

    if [ -f "${SQL_NEW_DIR}/new-product-category-i18n.sql" ]; then
        echo "Executing: new-product-category-i18n.sql"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_NEW_DIR}/new-product-category-i18n.sql"
    fi

    if [ -f "${SQL_NEW_DIR}/new-i18n-ar.sql" ]; then
        echo "Executing: new-i18n-ar.sql"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_NEW_DIR}/new-i18n-ar.sql"
    fi

    # 商机域增量SQL（按依赖顺序）
    if [ -f "${SQL_NEW_DIR}/new-crm-business-fields.sql" ]; then
        echo "Executing: new-crm-business-fields.sql"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_NEW_DIR}/new-crm-business-fields.sql"
    fi

    if [ -f "${SQL_NEW_DIR}/new-crm-business-quotation.sql" ]; then
        echo "Executing: new-crm-business-quotation.sql"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_NEW_DIR}/new-crm-business-quotation.sql"
    fi

    if [ -f "${SQL_NEW_DIR}/new-crm-business-quotation-snapshot.sql" ]; then
        echo "Executing: new-crm-business-quotation-snapshot.sql"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_NEW_DIR}/new-crm-business-quotation-snapshot.sql"
    fi

    if [ -f "${SQL_NEW_DIR}/new-crm-business-source-dict.sql" ]; then
        echo "Executing: new-crm-business-source-dict.sql"
        mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" --default-character-set=utf8mb4 "${MYSQL_DATABASE}" < "${SQL_NEW_DIR}/new-crm-business-source-dict.sql"
    fi
fi

echo "MySQL initialization completed successfully!"
```

## 手动执行增量SQL（针对已运行的数据库）

如果数据库已运行且需要执行增量SQL：

```bash
# 进入WSL
wsl -d Ubuntu-26.04

# 切换到项目目录
cd /home/renmen/product

# 执行通用增量SQL（按依赖顺序）
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-i18n.sql
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-large-file-upload.sql
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-mall-i18n.sql
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-product-category-i18n.sql
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-i18n-ar.sql

# 执行商机域增量SQL（按顺序）
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-crm-business-fields.sql
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-crm-business-quotation.sql
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-crm-business-quotation-snapshot.sql
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-crm-business-source-dict.sql
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-crm-statistics-forecast-menu.sql

# 如果中文乱码，执行修复脚本
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/fix_dict_chinese.sql

# 如果currency_code字段缺失，执行修复脚本
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/fix_currency_code.sql
```

## 销售预测菜单与销售漏斗图表联动（2026-07-17）

### 销售预测菜单

增量脚本：`database/new/new-crm-statistics-forecast-menu.sql`。它以“销售漏斗”的父级为父菜单，创建数据库动态路由 `/crm/statistics/forecast`，写入中英文菜单名，并授权 `super_admin`、`crm_admin`；脚本可重复执行。

已运行数据库执行：

```bash
docker exec mitedtsm-mysql mysql -u root -p1234 --default-character-set=utf8mb4 mitedtsm_database < database/new/new-crm-statistics-forecast-menu.sql
```

执行后重新登录。本项目为数据库动态路由，**不要**在 `Web/src/router/modules/remaining.ts` 添加静态路由。

### 销售漏斗图表点击联动

商机转化率属于 `/crm/statistics/funnel` 的第三个标签页，不是销售预测页。图表点击后，前端使用接口返回的 `startTime`、`endTime`（格式 `yyyy-MM-dd HH:mm:ss`）作为 `times` 参数，请求下方商机明细。不要恢复 `clickDate` 精确匹配：按周、月、季度统计时它不能代表完整周期。

### 整合与验收

1. 合并源码和 `new-crm-statistics-forecast-menu.sql`。
2. 已存在数据库按上面命令执行一次；新环境请将该脚本加入初始化 SQL 顺序。
3. 发布后重新登录：漏斗页第三个标签点击有数据柱子/折线点，表格仅显示该周期商机；“数据统计”下可进入销售预测，且负责人传 `userIds`、预计成交日期传 `dealTimeStart` / `dealTimeEnd`。

## 新增表结构说明

### infra_file_upload_task（文件上传任务表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 任务编号 |
| file_id | VARCHAR(64) | 文件唯一标识(UUID) |
| filename | VARCHAR(255) | 文件名 |
| file_size | BIGINT | 文件大小(字节) |

### system_menu_i18n（菜单国际化表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| menu_id | BIGINT | 菜单ID |
| language | VARCHAR(10) | 语言代码(zh-CN, en等) |
| name | VARCHAR(100) | 菜单名称 |

### promotion_diy_menu_i18n（装修菜单国际化表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| menu_key | VARCHAR(200) | 菜单标识(url路径) |
| language | VARCHAR(10) | 语言代码 |
| position | VARCHAR(20) | 菜单位置 |
| name | VARCHAR(100) | 菜单名称翻译 |

### product_category_i18n（商品分类国际化表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| category_id | BIGINT | 分类ID |
| language | VARCHAR(20) | 语言代码 |
| name | VARCHAR(255) | 分类名称翻译 |

### crm_business（商机表扩展字段）

| 字段 | 类型 | 说明 |
|------|------|------|
| source | VARCHAR(30) | 商机来源（字典类型：crm_business_source） |
| competitor | VARCHAR(100) | 竞争对手 |

### crm_business_quotation（商机报价主表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| business_id | BIGINT | 商机编号 |
| quotation_no | VARCHAR(64) | 报价编号 |
| status | TINYINT | 状态（0-草稿，1-已确认，2-已作废） |
| total_product_price | DECIMAL(20,2) | 产品原价合计 |
| discount_percent | DECIMAL(20,2) | 整单折扣 |
| total_price | DECIMAL(20,2) | 报价总额 |
| confirmed_by | BIGINT | 确认人 |
| confirmed_time | DATETIME | 确认时间 |
| remark | VARCHAR(500) | 备注 |
| tenant_id | BIGINT | 租户编号 |
| creator | VARCHAR(64) | 创建者 |
| create_time | DATETIME | 创建时间 |
| updater | VARCHAR(64) | 更新者 |
| update_time | DATETIME | 更新时间 |
| deleted | BIT(1) | 是否删除 |

### crm_business_quotation_item（报价产品项表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| quotation_id | BIGINT | 报价编号 |
| product_id | BIGINT | 产品编号 |
| product_name | VARCHAR(200) | 产品名称（快照） |
| product_no | VARCHAR(100) | 产品编码（快照） |
| standard_price | DECIMAL(20,2) | 标准价 |
| actual_price | DECIMAL(20,2) | 实际售价 |
| count | DECIMAL(20,2) | 数量 |
| discount_percent | DECIMAL(20,2) | 行折扣 |
| total_price | DECIMAL(20,2) | 行总价 |
| gift | VARCHAR(500) | 礼品 |
| remark | VARCHAR(500) | 备注 |
| tenant_id | BIGINT | 租户编号 |

### crm_business_quotation_snapshot（报价快照表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| business_id | BIGINT | 商机编号 |
| quotation_id | BIGINT | 报价编号 |
| quotation_no | VARCHAR(64) | 报价编号 |
| status | TINYINT | 状态（0-草稿，1-已确认，2-已作废） |
| total_product_price | DECIMAL(14,2) | 产品总价 |
| discount_percent | DECIMAL(5,2) | 折扣（%） |
| total_price | DECIMAL(14,2) | 报价总金额 |
| confirmed_by | BIGINT | 确认人 |
| confirmed_time | DATETIME | 确认时间 |
| remark | VARCHAR(512) | 备注 |
| creator | VARCHAR(64) | 创建者 |
| create_time | DATETIME | 创建时间 |

### system_tenant（租户表扩展字段）

| 字段 | 类型 | 说明 |
|------|------|------|
| currency_code | VARCHAR(10) | 货币代码（CNY/USD/SAR等），默认CNY |

### system_dict_type / system_dict_data（商机来源字典）

**字典类型**: `crm_business_source` (CRM 商机来源)

**字典数据**:

| value | label | 说明 |
|-------|-------|------|
| 1 | 主动开发 | 主动开发客户 |
| 2 | 客户介绍 | 客户转介绍 |
| 3 | 网络推广 | 网络渠道推广 |
| 4 | 展会 | 展会获取 |
| 5 | 电话营销 | 电话营销 |
| 6 | 其他 | 其他来源 |

## 更新记录

| 日期 | 更新内容 | 操作人 |
|------|----------|--------|
| 2026-07-14 | 新增商机字段扩展SQL | traebot |
| 2026-07-14 | 新增商机报价表SQL | traebot |
| 2026-07-14 | 新增商机来源字典SQL | traebot |
| 2026-07-16 | 新增报价快照表SQL | traebot |
| 2026-07-16 | 更新init-mysql.sh添加快照表执行 | traebot |
| 2026-07-16 | 添加字符集注意事项（--default-character-set=utf8mb4） | traebot |
| 2026-07-16 | 新增字典中文乱码修复SQL | traebot |
| 2026-07-16 | 更新整合文档，补充表结构说明和手动执行方法 | traebot |
| 2026-07-16 | 添加通用增量SQL（i18n、large-file-upload等）到整合文档 | traebot |
| 2026-07-16 | 添加租户currency_code字段修复SQL | traebot |
| 2026-07-16 | 更新推荐执行顺序，包含所有增量SQL文件 | traebot |
| 2026-07-17 | 修正销售预测菜单 SQL、动态路由说明及销售漏斗图表点击联动说明 | Codex |

## Git提交记录

| 提交 | 日期 | 说明 |
|------|------|------|
| 8bfeece | 2026-07-14 | fix(crm): fix quotation API URL syntax with template literals |
| a55404b | 2026-07-14 | fix(crm): fix test compilation and add jacoco coverage report |
| a41ee03 | 2026-07-14 | fix(crm): repair forecast VO, lombok proc config and query wrapper type |
| 45b432e | 2026-07-14 | fix(crm): repair quotation service impl and add source dictionary for deployment |
| 569efe3 | 2026-07-14 | docs(crm): document opportunity api and verification results |
| 743771d | 2026-07-14 | test(crm): cover opportunity quotation and forecast flows |
| 91df248 | 2026-07-14 | feat(crm): add stage-weighted sales forecast report |
| 7eaa3fe | 2026-07-14 | feat(crm): add quotation confirmation and snapshot records |
| 0714e7b | 2026-07-14 | feat(crm): extend opportunity profile fields and follow-up warning |
| 8e1c3f0 | 2026-07-14 | initial commit |

## 待提交的文件

| 文件 | 类型 | 说明 |
|------|------|------|
| Server/mitedtsm-module-crm/src/main/java/com/meession/etm/module/crm/dal/dataobject/business/CrmBusinessQuotationSnapshotDO.java | 新增 | 报价快照DO |
| Server/mitedtsm-module-crm/src/main/java/com/meession/etm/module/crm/dal/mysql/business/CrmBusinessQuotationSnapshotMapper.java | 新增 | 报价快照Mapper |
| Server/mitedtsm-module-crm/src/test/java/com/meession/etm/module/crm/service/business/CrmBusinessServiceTest.java | 新增 | 商机服务测试 |
| database/new/new-crm-business-quotation-snapshot.sql | 新增 | 报价快照表SQL |
| database/new/fix_dict_chinese.sql | 新增 | 字典中文乱码修复 |
| database/new/fix_currency_code.sql | 新增 | 租户货币代码修复 |
| docker-compose/init/init-mysql.sh | 修改 | 添加所有增量SQL执行 |
| database-migration.md | 修改 | 完整整合文档 |
