const http = require('http');

const TOKEN = 'f84ea5b2475047b48f8ec4d05ff1be0e';

function makeRequest(options, data) {
    return new Promise((resolve, reject) => {
        const req = http.request(options, (res) => {
            let body = '';
            res.on('data', (chunk) => body += chunk);
            res.on('end', () => {
                try {
                    resolve(JSON.parse(body));
                } catch (e) {
                    resolve(body);
                }
            });
        });
        req.on('error', reject);
        if (data) {
            req.write(JSON.stringify(data));
        }
        req.end();
    });
}

async function createMenu(name, permission, type, sort, parentId, path, icon, component, componentName) {
    const options = {
        hostname: 'localhost',
        port: 8080,
        path: '/admin-api/system/menu/create',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'tenant-id': '1',
            'Authorization': `Bearer ${TOKEN}`
        }
    };
    const data = {
        name,
        permission,
        type,
        sort,
        parentId,
        path,
        icon,
        component,
        componentName,
        status: 0,
        visible: true,
        keepAlive: true,
        alwaysShow: true
    };
    const result = await makeRequest(options, data);
    console.log(`  ${name}: ${result.code === 0 ? '成功 (ID: ' + result.data + ')' : '失败 - ' + result.msg}`);
    return result.data;
}

async function main() {
    console.log('=== 创建营销活动菜单 ===');
    const campaignId = await createMenu('营销活动', '', 2, 70, 2397, 'marketing/campaign', 'ep:promotion', 'crm/marketing/campaign/index', 'MarketingCampaign');
    await createMenu('营销活动查询', 'crm:marketing-campaign:query', 3, 1, campaignId, '', '', '', '');
    await createMenu('营销活动创建', 'crm:marketing-campaign:create', 3, 2, campaignId, '', '', '', '');
    await createMenu('营销活动更新', 'crm:marketing-campaign:update', 3, 3, campaignId, '', '', '', '');
    await createMenu('营销活动删除', 'crm:marketing-campaign:delete', 3, 4, campaignId, '', '', '', '');

    console.log('\n=== 创建短信群发批次菜单 ===');
    const smsBatchId = await createMenu('短信群发批次', '', 2, 71, 2397, 'marketing/sms-batch', 'ep:message', 'crm/marketing/smsBatch/index', 'MarketingSmsBatch');
    await createMenu('短信群发批次查询', 'crm:marketing-sms-batch:query', 3, 1, smsBatchId, '', '', '', '');
    await createMenu('短信群发批次创建', 'crm:marketing-sms-batch:create', 3, 2, smsBatchId, '', '', '', '');
    await createMenu('短信群发批次更新', 'crm:marketing-sms-batch:update', 3, 3, smsBatchId, '', '', '', '');
    await createMenu('短信群发批次删除', 'crm:marketing-sms-batch:delete', 3, 4, smsBatchId, '', '', '', '');

    console.log('\n=== 创建邮件群发批次菜单 ===');
    const emailBatchId = await createMenu('邮件群发批次', '', 2, 72, 2397, 'marketing/email-batch', 'ep:mail', 'crm/marketing/emailBatch/index', 'MarketingEmailBatch');
    await createMenu('邮件群发批次查询', 'crm:marketing-email-batch:query', 3, 1, emailBatchId, '', '', '', '');
    await createMenu('邮件群发批次创建', 'crm:marketing-email-batch:create', 3, 2, emailBatchId, '', '', '', '');
    await createMenu('邮件群发批次更新', 'crm:marketing-email-batch:update', 3, 3, emailBatchId, '', '', '', '');
    await createMenu('邮件群发批次删除', 'crm:marketing-email-batch:delete', 3, 4, emailBatchId, '', '', '', '');

    console.log('\n=== 创建客户关怀规则菜单 ===');
    const customerCareId = await createMenu('客户关怀规则', '', 2, 73, 2397, 'marketing/customer-care', 'ep:heart', 'crm/marketing/customerCare/index', 'MarketingCustomerCare');
    await createMenu('客户关怀规则查询', 'crm:marketing-customer-care:query', 3, 1, customerCareId, '', '', '', '');
    await createMenu('客户关怀规则创建', 'crm:marketing-customer-care:create', 3, 2, customerCareId, '', '', '', '');
    await createMenu('客户关怀规则更新', 'crm:marketing-customer-care:update', 3, 3, customerCareId, '', '', '', '');
    await createMenu('客户关怀规则删除', 'crm:marketing-customer-care:delete', 3, 4, customerCareId, '', '', '', '');

    console.log('\n=== 创建群发审批菜单 ===');
    const approvalId = await createMenu('群发审批', '', 2, 74, 2397, 'marketing/approval', 'ep:check-circle', 'crm/marketing/approval/index', 'MarketingApproval');
    await createMenu('群发审批查询', 'crm:marketing-approval:query', 3, 1, approvalId, '', '', '', '');
    await createMenu('群发审批通过', 'crm:marketing-approval:approve', 3, 2, approvalId, '', '', '', '');
    await createMenu('群发审批拒绝', 'crm:marketing-approval:reject', 3, 3, approvalId, '', '', '', '');
    await createMenu('群发审批删除', 'crm:marketing-approval:delete', 3, 4, approvalId, '', '', '', '');

    console.log('\n=== 创建发送记录菜单 ===');
    const sendRecordId = await createMenu('发送记录', '', 2, 75, 2397, 'marketing/send-record', 'ep:document', 'crm/marketing/sendRecord/index', 'MarketingSendRecord');
    await createMenu('发送记录查询', 'crm:marketing-send-record:query', 3, 1, sendRecordId, '', '', '', '');

    console.log('\n=== 菜单创建完成 ===');
}

main().catch(console.error);