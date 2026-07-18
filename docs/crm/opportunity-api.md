# 商机域增量开发 API 文档

## 1. 新增字段（P0-1）
- crm_business 表新增 source（商机来源 VARCHAR(30)）、competitor（竞争对手 VARCHAR(100)）
- 未跟进天数 daysWithoutFollowUp 由后端动态计算，contactLastTime 优先，无则用 createTime

## 2. 报价确认 API（P0-2）
| 接口 | 方法 | 路径 | 权限 |
|------|------|------|------|
| 创建报价草稿 | POST | /admin-api/crm/business-quotation/create-draft | crm:business-quotation:create |
| 确认报价 | PUT | /admin-api/crm/business-quotation/confirm | crm:business-quotation:confirm |
| 作废报价 | PUT | /admin-api/crm/business-quotation/void | crm:business-quotation:void |
| 报价详情 | GET | /admin-api/crm/business-quotation/get | crm:business-quotation:query |
| 报价分页 | GET | /admin-api/crm/business-quotation/page | crm:business-quotation:query |
| 最新已确认报价 | GET | /admin-api/crm/business-quotation/latest-confirmed | crm:business-quotation:query |

## 3. 销售预测 API（P0-3）
| 接口 | 方法 | 路径 | 权限 |
|------|------|------|------|
| 预测汇总 | GET | /admin-api/crm/statistics-forecast/summary | crm:statistics-forecast:query |
| 预测明细分页 | GET | /admin-api/crm/statistics-forecast/page | crm:statistics-forecast:query |

### 算法说明
- forecastAmount = sum(totalPrice × stagePercent / 100)
- 排除已流失商机（endStatus=2）
- 成交商机（endStatus=1）单独计入 wonAmount

## 4. SQL 迁移
- Server/sql/mysql/optional/crm/商机域字段扩展.sql
- Server/sql/mysql/optional/crm/商机报价表.sql

## 5. 单元测试
- CrmBusinessQuotationServiceTest: 5个测试（草稿创建/空产品/重复确认/商机已成交/快照隔离）
- CrmStatisticsForecastServiceTest: 4个测试（加权计算/流失排除/成交口径/空数据）
- CrmBusinessFieldTest: 4个测试（未跟进天数边界/竞争对手字段校验）

## 6. 已知跨域依赖
- 报价转订单：订单域接口尚未就绪，已确认报价可标记 canConvertToOrder 但不调用订单服务
