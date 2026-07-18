const http = require('http');

let TOKEN = '';

async function makeRequest(options, data) {
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

async function login() {
    const options = {
        hostname: 'localhost',
        port: 8080,
        path: '/admin-api/system/auth/login',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'tenant-id': '1'
        }
    };
    const result = await makeRequest(options, { username: 'admin', password: 'admin123' });
    TOKEN = result.data.accessToken;
    console.log('登录成功, Token:', TOKEN);
}

async function deleteMenu(id) {
    const options = {
        hostname: 'localhost',
        port: 8080,
        path: `/admin-api/system/menu/delete?id=${id}`,
        method: 'DELETE',
        headers: {
            'tenant-id': '1',
            'Authorization': `Bearer ${TOKEN}`
        }
    };
    const result = await makeRequest(options);
    console.log(`  删除菜单 ${id}: ${result.code === 0 ? '成功' : '失败 - ' + result.msg}`);
    return result.code === 0;
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

async function updateMenu(id, name, permission, type, sort, parentId, path, icon, component, componentName) {
    const options = {
        hostname: 'localhost',
        port: 8080,
        path: '/admin-api/system/menu/update',
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'tenant-id': '1',
            'Authorization': `Bearer ${TOKEN}`
        }
    };
    const data = {
        id,
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
    console.log(`  更新菜单 ${id}: ${result.code === 0 ? '成功' : '失败 - ' + result.msg}`);
    return result.code === 0;
}

async function main() {
    console.log('=== 登录系统 ===');
    await login();

    console.log('\n=== 第一阶段：删除残留的旧菜单（先删子菜单，再删父菜单）===');
    const childIds = [6005, 6006, 6007, 6008, 6010, 6011, 6012, 6013, 6015, 6016, 6017, 6018, 6020, 6021, 6022, 6023, 6025, 6026, 6027, 6028, 6030];
    for (const id of childIds) {
        await deleteMenu(id);
    }
    
    const parentIds = [6004, 6009, 6014, 6019, 6024, 6029];
    for (const id of parentIds) {
        await deleteMenu(id);
    }

    console.log('\n=== 删除之前创建的营销管理菜单 ===');
    await deleteMenu(6031);

    console.log('\n=== 创建营销管理菜单（二级菜单，type=1）===');
    const marketingId = await createMenu('营销管理', '', 1, 70, 2397, 'marketing', 'ep:promotion', '', '');

    console.log('\n=== 创建营销活动子菜单（三级菜单，type=2）===');
    const campaignId = await createMenu('营销活动', '', 2, 1, marketingId, 'campaign', '', 'crm/marketing/campaign/index', 'MarketingCampaign');
    await createMenu('营销活动查询', 'crm:marketing-campaign:query', 3, 1, campaignId, '', '', '', '');
    await createMenu('营销活动创建', 'crm:marketing-campaign:create', 3, 2, campaignId, '', '', '', '');
    await createMenu('营销活动更新', 'crm:marketing-campaign:update', 3, 3, campaignId, '', '', '', '');
    await createMenu('营销活动删除', 'crm:marketing-campaign:delete', 3, 4, campaignId, '', '', '', '');

    console.log('\n=== 创建短信群发批次子菜单 ===');
    const smsBatchId = await createMenu('短信群发批次', '', 2, 2, marketingId, 'sms-batch', '', 'crm/marketing/smsBatch/index', 'MarketingSmsBatch');
    await createMenu('短信群发批次查询', 'crm:marketing-sms-batch:query', 3, 1, smsBatchId, '', '', '', '');
    await createMenu('短信群发批次创建', 'crm:marketing-sms-batch:create', 3, 2, smsBatchId, '', '', '', '');
    await createMenu('短信群发批次更新', 'crm:marketing-sms-batch:update', 3, 3, smsBatchId, '', '', '', '');
    await createMenu('短信群发批次删除', 'crm:marketing-sms-batch:delete', 3, 4, smsBatchId, '', '', '', '');

    console.log('\n=== 创建邮件群发批次子菜单 ===');
    const emailBatchId = await createMenu('邮件群发批次', '', 2, 3, marketingId, 'email-batch', '', 'crm/marketing/emailBatch/index', 'MarketingEmailBatch');
    await createMenu('邮件群发批次查询', 'crm:marketing-email-batch:query', 3, 1, emailBatchId, '', '', '', '');
    await createMenu('邮件群发批次创建', 'crm:marketing-email-batch:create', 3, 2, emailBatchId, '', '', '', '');
    await createMenu('邮件群发批次更新', 'crm:marketing-email-batch:update', 3, 3, emailBatchId, '', '', '', '');
    await createMenu('邮件群发批次删除', 'crm:marketing-email-batch:delete', 3, 4, emailBatchId, '', '', '', '');

    console.log('\n=== 创建客户关怀规则子菜单 ===');
    const customerCareId = await createMenu('客户关怀规则', '', 2, 4, marketingId, 'customer-care', '', 'crm/marketing/customerCare/index', 'MarketingCustomerCare');
    await createMenu('客户关怀规则查询', 'crm:marketing-customer-care:query', 3, 1, customerCareId, '', '', '', '');
    await createMenu('客户关怀规则创建', 'crm:marketing-customer-care:create', 3, 2, customerCareId, '', '', '', '');
    await createMenu('客户关怀规则更新', 'crm:marketing-customer-care:update', 3, 3, customerCareId, '', '', '', '');
    await createMenu('客户关怀规则删除', 'crm:marketing-customer-care:delete', 3, 4, customerCareId, '', '', '', '');

    console.log('\n=== 创建群发审批子菜单 ===');
    const approvalId = await createMenu('群发审批', '', 2, 5, marketingId, 'approval', '', 'crm/marketing/approval/index', 'MarketingApproval');
    await createMenu('群发审批查询', 'crm:marketing-approval:query', 3, 1, approvalId, '', '', '', '');
    await createMenu('群发审批通过', 'crm:marketing-approval:approve', 3, 2, approvalId, '', '', '', '');
    await createMenu('群发审批拒绝', 'crm:marketing-approval:reject', 3, 3, approvalId, '', '', '', '');
    await createMenu('群发审批删除', 'crm:marketing-approval:delete', 3, 4, approvalId, '', '', '', '');

    console.log('\n=== 创建发送记录子菜单 ===');
    const sendRecordId = await createMenu('发送记录', '', 2, 6, marketingId, 'send-record', '', 'crm/marketing/sendRecord/index', 'MarketingSendRecord');
    await createMenu('发送记录查询', 'crm:marketing-send-record:query', 3, 1, sendRecordId, '', '', '', '');

    console.log('\n=== 菜单结构调整完成 ===');
    console.log('\n菜单结构如下：');
    console.log('CRM 系统');
    console.log('  └── 营销管理');
    console.log('      ├── 营销活动');
    console.log('      ├── 短信群发批次');
    console.log('      ├── 邮件群发批次');
    console.log('      ├── 客户关怀规则');
    console.log('      ├── 群发审批');
    console.log('      └── 发送记录');
}

main().catch(console.error);