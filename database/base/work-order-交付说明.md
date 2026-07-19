# 工单域交付说明

## 1. 唯一执行依据

MySQL 初始化以 `docker-compose/init/init-mysql.sh` 为准。本说明只记录该脚本的实际 SQL 执行顺序，不以其他迁移文档或目录中文件的名称推断执行顺序。

- 仅在 MySQL 数据卷为空、容器首次初始化时执行；
- 脚本使用 `set -e`，任一 SQL 失败即停止，后续 SQL 不会继续执行；
- 基础 SQL 第 3 步遍历 `database/base/*.sql`，按**字母顺序**执行（排除已显式执行的 `quartz.sql` 与 `ruoyi-vue-pro.sql`）；
- 增量 SQL 通过 `if [ -f ... ]` 逐个显式调用 `database/new/` 下的 5 个文件，**不存在** `exec_new` 函数；
- 工单相关 SQL（`work-order-2026-07-18.sql`、`work-order-menu-2026-07-18.sql`）当前位于 `database/base/` 目录，会被基础 SQL 第 3 步按字母顺序自动执行，**不在** `database/new/` 增量列表中；
- 当前运行的 MySQL 容器数据卷已非空，`init-mysql.sh` 不会再次触发，工单 SQL 已通过手动 `docker exec` 导入运行中的数据库。

## 2. 基础 SQL 执行顺序

| 顺序 | SQL 文件 | 说明 |
| --- | --- | --- |
| 1 | `database/base/quartz.sql` | 定时任务基础表（显式执行） |
| 2 | `database/base/ruoyi-vue-pro.sql` | 系统基础数据与表，含 `system_menu`（显式执行） |
| 3 | `database/base/ai-2025-08-29.sql` | AI 模块基础表 |
| 4 | `database/base/bpm-2025-10-04.sql` | BPM 模块基础表 |
| 5 | `database/base/crm-2024-09-30.sql` | CRM 基础表与数据，含 CRM 父菜单 id=2397 |
| 6 | `database/base/erp-2024-05-03.sql` | ERP 模块基础表 |
| 7 | `database/base/iot-2026-02-10.sql` | IoT 模块基础表 |
| 8 | `database/base/jimureport.mysql5.7.create.sql` | 报表基础表 |
| 9 | `database/base/mall-2025-05-12.sql` | 商城模块基础表 |
| 10 | `database/base/member-2024-01-18.sql` | 会员模块基础表 |
| 11 | `database/base/mp-2025-12-07.sql` | 公众号模块基础表 |
| 12 | `database/base/pay-2025-07-27.sql` | 支付模块基础表 |
| 13 | `database/base/work-order-2026-07-18.sql` | **工单建表 + 初始数据（本次新增）** |
| 14 | `database/base/work-order-menu-2026-07-18.sql` | **工单菜单 + 权限（本次新增）** |

说明：
- 第 3 至 12 项由脚本遍历 `database/base/*.sql` 按字母顺序执行，并跳过已经在前两步执行过的 `quartz.sql` 与 `ruoyi-vue-pro.sql`。
- 第 13、14 项为本次工单功能新增，因 `w` 字母序排在 `pay` 之后，位于基础 SQL 末尾自动执行。
- **执行顺序符合依赖关系**：工单菜单 SQL（第 14 步）依赖 `system_menu` 表（第 2 步）与 CRM 父菜单 id=2397（第 5 步），均已在前面执行；工单建表 SQL（第 13 步）先于菜单 SQL（第 14 步），满足「先建表后配菜单」的顺序。

## 3. 增量 SQL 执行顺序

| 顺序 | SQL 文件 | 用途 |
| --- | --- | --- |
| 15 | `database/new/new-i18n.sql` | 通用国际化（创建 `system_menu_i18n` 表） |
| 16 | `database/new/new-mall-i18n.sql` | 商城国际化（创建 `promotion_diy_menu_i18n`，修改 `system_tenant` 加 `currency_code`） |
| 17 | `database/new/new-i18n-ar.sql` | 阿拉伯语国际化（依赖 `system_menu_i18n` 表） |
| 18 | `database/new/new-product-category-i18n.sql` | 商品分类国际化 |
| 19 | `database/new/new-large-file-upload.sql` | 大文件上传基础增量 |

说明：
- 增量 SQL 由脚本第 4 步通过 `if [ -f ... ]` 逐个显式执行，顺序固定。
- 工单功能**未在**增量 SQL 列表中，工单 SQL 全部位于基础 SQL（第 13、14 步）中执行。

## 4. 完成标志

初始化日志出现以下内容，表示上述 SQL 均已顺序执行完成：

```text
MySQL initialization completed successfully!
```

若该行之前出现 `ERROR`，表示初始化在该 SQL 处停止，后续 SQL 尚未执行。

---

## 附：工单 SQL 内容说明

### 4.1 `work-order-2026-07-18.sql`（第 13 步）

创建 3 张业务表 + 初始化工单类型数据：

| 表名 | 说明 | 键 |
| --- | --- | --- |
| `work_order` | 工单主表 | 主键 `id`；唯一键 `uk_order_no`；索引 `idx_status`/`idx_type`/`idx_priority`/`idx_reporter_id`/`idx_assignee_id`/`idx_create_time` |
| `work_order_type` | 工单类型配置表 | 主键 `id`；唯一键 `uk_code` |
| `work_order_status_flow` | 工单状态流转记录表 | 主键 `id`；索引 `idx_order_id`/`idx_create_time` |

初始数据：`work_order_type` 插入 4 条类型（故障报修 / 服务请求 / 咨询投诉 / 变更申请）。

三张表均含框架通用字段（`creator`/`create_time`/`updater`/`update_time`/`deleted`/`tenant_id`），采用逻辑关联（无物理外键）。

### 4.2 `work-order-menu-2026-07-18.sql`（第 14 步）

向 `system_menu` 表配置菜单与权限，采用「先 DELETE 后 INSERT」可重复执行模式：

```
CRM 系统 (id=2397)
  └── 工单管理 (id=2770, 菜单, crm/work-order/index)
        ├── 工单查询 (id=2771, crm:work-order:query)
        ├── 工单新增 (id=2772, crm:work-order:create)
        ├── 工单修改 (id=2773, crm:work-order:update)
        ├── 工单删除 (id=2774, crm:work-order:delete)
        └── 工单导出 (id=2777, crm:work-order:export)
```

> 菜单脚本执行后需重新登录或清除 Redis 权限缓存以刷新前端菜单。

---

## 附：手动执行方式（针对已运行的数据库）

当前 MySQL 容器数据卷已非空，`init-mysql.sh` 不会再次执行。如需在已运行的环境中补工单 SQL，手动执行：

```bash
docker exec -i mitedtsm-mysql mysql -uroot -p1234 --default-character-set=utf8mb4 mitedtsm_database < /home/zyf/products/database/base/work-order-2026-07-18.sql
docker exec -i mitedtsm-mysql mysql -uroot -p1234 --default-character-set=utf8mb4 mitedtsm_database < /home/zyf/products/database/base/work-order-menu-2026-07-18.sql
```

> ⚠️ 必须加 `--default-character-set=utf8mb4`，否则中文菜单名乱码。
> ⚠️ 建表脚本含 `DROP TABLE IF EXISTS`，**生产环境已执行后请勿重复执行**（会清空工单数据）。