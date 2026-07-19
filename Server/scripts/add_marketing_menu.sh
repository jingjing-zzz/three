TOKEN="f84ea5b2475047b48f8ec4d05ff1be0e"
BASE_URL="http://localhost:8080/admin-api/system/menu"

create_menu() {
    NAME=$1
    PERMISSION=$2
    TYPE=$3
    SORT=$4
    PARENT_ID=$5
    PATH=$6
    ICON=$7
    COMPONENT=$8
    COMPONENT_NAME=$9
    
    curl -s -X POST "$BASE_URL/create" \
        -H "Content-Type: application/json" \
        -H "tenant-id: 1" \
        -H "Authorization: Bearer $TOKEN" \
        -d "{
            \"name\": \"$NAME\",
            \"permission\": \"$PERMISSION\",
            \"type\": $TYPE,
            \"sort\": $SORT,
            \"parentId\": $PARENT_ID,
            \"path\": \"$PATH\",
            \"icon\": \"$ICON\",
            \"component\": \"$COMPONENT\",
            \"componentName\": \"$COMPONENT_NAME\",
            \"status\": 0,
            \"visible\": true,
            \"keepAlive\": true,
            \"alwaysShow\": true
        }" | python3 -m json.tool
}

echo "=== 创建营销活动菜单 ==="
create_menu "营销活动" "" 2 70 2397 "marketing/campaign" "ep:promotion" "crm/marketing/campaign/index" "MarketingCampaign"
sleep 0.5
create_menu "营销活动查询" "crm:marketing-campaign:query" 3 1 5047 "" "" "" ""
sleep 0.5
create_menu "营销活动创建" "crm:marketing-campaign:create" 3 2 5047 "" "" "" ""
sleep 0.5
create_menu "营销活动更新" "crm:marketing-campaign:update" 3 3 5047 "" "" "" ""
sleep 0.5
create_menu "营销活动删除" "crm:marketing-campaign:delete" 3 4 5047 "" "" "" ""

echo ""
echo "=== 创建短信群发批次菜单 ==="
create_menu "短信群发批次" "" 2 71 2397 "marketing/sms-batch" "ep:message" "crm/marketing/smsBatch/index" "MarketingSmsBatch"
sleep 0.5
create_menu "短信群发批次查询" "crm:marketing-sms-batch:query" 3 1 5052 "" "" "" ""
sleep 0.5
create_menu "短信群发批次创建" "crm:marketing-sms-batch:create" 3 2 5052 "" "" "" ""
sleep 0.5
create_menu "短信群发批次更新" "crm:marketing-sms-batch:update" 3 3 5052 "" "" "" ""
sleep 0.5
create_menu "短信群发批次删除" "crm:marketing-sms-batch:delete" 3 4 5052 "" "" "" ""

echo ""
echo "=== 创建邮件群发批次菜单 ==="
create_menu "邮件群发批次" "" 2 72 2397 "marketing/email-batch" "ep:mail" "crm/marketing/emailBatch/index" "MarketingEmailBatch"
sleep 0.5
create_menu "邮件群发批次查询" "crm:marketing-email-batch:query" 3 1 5057 "" "" "" ""
sleep 0.5
create_menu "邮件群发批次创建" "crm:marketing-email-batch:create" 3 2 5057 "" "" "" ""
sleep 0.5
create_menu "邮件群发批次更新" "crm:marketing-email-batch:update" 3 3 5057 "" "" "" ""
sleep 0.5
create_menu "邮件群发批次删除" "crm:marketing-email-batch:delete" 3 4 5057 "" "" "" ""

echo ""
echo "=== 创建客户关怀规则菜单 ==="
create_menu "客户关怀规则" "" 2 73 2397 "marketing/customer-care" "ep:heart" "crm/marketing/customerCare/index" "MarketingCustomerCare"
sleep 0.5
create_menu "客户关怀规则查询" "crm:marketing-customer-care:query" 3 1 5062 "" "" "" ""
sleep 0.5
create_menu "客户关怀规则创建" "crm:marketing-customer-care:create" 3 2 5062 "" "" "" ""
sleep 0.5
create_menu "客户关怀规则更新" "crm:marketing-customer-care:update" 3 3 5062 "" "" "" ""
sleep 0.5
create_menu "客户关怀规则删除" "crm:marketing-customer-care:delete" 3 4 5062 "" "" "" ""

echo ""
echo "=== 创建群发审批菜单 ==="
create_menu "群发审批" "" 2 74 2397 "marketing/approval" "ep:check-circle" "crm/marketing/approval/index" "MarketingApproval"
sleep 0.5
create_menu "群发审批查询" "crm:marketing-approval:query" 3 1 5067 "" "" "" ""
sleep 0.5
create_menu "群发审批通过" "crm:marketing-approval:approve" 3 2 5067 "" "" "" ""
sleep 0.5
create_menu "群发审批拒绝" "crm:marketing-approval:reject" 3 3 5067 "" "" "" ""
sleep 0.5
create_menu "群发审批删除" "crm:marketing-approval:delete" 3 4 5067 "" "" "" ""

echo ""
echo "=== 创建发送记录菜单 ==="
create_menu "发送记录" "" 2 75 2397 "marketing/send-record" "ep:document" "crm/marketing/sendRecord/index" "MarketingSendRecord"
sleep 0.5
create_menu "发送记录查询" "crm:marketing-send-record:query" 3 1 5072 "" "" "" ""

echo ""
echo "=== 菜单创建完成 ==="