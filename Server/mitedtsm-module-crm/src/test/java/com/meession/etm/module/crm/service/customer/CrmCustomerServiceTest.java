package com.meession.etm.module.crm.service.customer;

import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.test.core.ut.BaseMockitoUnitTest;
import com.meession.etm.module.crm.controller.admin.customer.vo.customer.CrmCustomerPageReqVO;
import com.meession.etm.module.crm.controller.admin.customer.vo.customer.CrmCustomerSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.mysql.customer.CrmCustomerMapper;
import com.meession.etm.module.crm.service.business.CrmBusinessService;
import com.meession.etm.module.crm.service.contact.CrmContactService;
import com.meession.etm.module.crm.service.contract.CrmContractService;
import com.meession.etm.module.crm.service.permission.CrmPermissionService;
import com.meession.etm.module.crm.service.permission.bo.CrmPermissionCreateReqBO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static com.meession.etm.module.crm.enums.ErrorCodeConstants.CUSTOMER_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CrmCustomerServiceTest extends BaseMockitoUnitTest {
    @InjectMocks private CrmCustomerServiceImpl customerService;
    @Mock private CrmCustomerMapper customerMapper;
    @Mock private CrmPermissionService permissionService;
    @Mock private CrmCustomerLimitConfigService customerLimitConfigService;
    @Mock private CrmContactService contactService;
    @Mock private CrmBusinessService businessService;
    @Mock private CrmContractService contractService;
    private CrmCustomerDO customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new CrmCustomerDO();
        customer.setId(1L);
        customer.setName("测试客户");
        customer.setOwnerUserId(100L);
        customer.setDealStatus(false);
    }

    @Test @DisplayName("TC-001 创建客户")
    void createCustomer_success() {
        CrmCustomerSaveReqVO reqVO = new CrmCustomerSaveReqVO();
        reqVO.setName("新客户");
        reqVO.setOwnerUserId(100L);
        when(customerLimitConfigService.getCustomerLimitConfigListByUserId(anyInt(), eq(100L))).thenReturn(Collections.emptyList());
        when(customerMapper.insert(any(CrmCustomerDO.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, CrmCustomerDO.class).setId(2L);
            return 1;
        });
        when(permissionService.createPermission(any(CrmPermissionCreateReqBO.class))).thenReturn(1L);

        assertEquals(2L, customerService.createCustomer(reqVO, 100L));
        verify(customerMapper).insert(any(CrmCustomerDO.class));
        verify(permissionService).createPermission(any(CrmPermissionCreateReqBO.class));
    }

    @Test @DisplayName("TC-002 查询客户")
    void getCustomer_success() {
        when(customerMapper.selectById(1L)).thenReturn(customer);
        assertEquals("测试客户", customerService.getCustomer(1L).getName());
    }

    @Test @DisplayName("TC-003 查询不存在客户")
    void validateCustomer_notFound() {
        when(customerMapper.selectById(999L)).thenReturn(null);
        ServiceException exception = assertThrows(ServiceException.class, () -> customerService.validateCustomer(999L));
        assertEquals(CUSTOMER_NOT_EXISTS.getCode(), exception.getCode());
    }

    @Test @DisplayName("TC-004 更新客户")
    void updateCustomer_success() {
        CrmCustomerSaveReqVO reqVO = new CrmCustomerSaveReqVO();
        reqVO.setId(1L);
        reqVO.setName("更新客户");
        when(customerMapper.selectById(1L)).thenReturn(customer);
        when(customerMapper.updateById(any(CrmCustomerDO.class))).thenReturn(1);

        assertDoesNotThrow(() -> customerService.updateCustomer(reqVO));

        verify(customerMapper).updateById(any(CrmCustomerDO.class));
        assertEquals(100L, reqVO.getOwnerUserId());
    }

    @Test @DisplayName("TC-005 更新成交状态")
    void updateCustomerDealStatus_success() {
        when(customerMapper.selectById(1L)).thenReturn(customer);
        when(customerMapper.updateById(any(CrmCustomerDO.class))).thenReturn(1);

        assertDoesNotThrow(() -> customerService.updateCustomerDealStatus(1L, true));

        verify(customerMapper).updateById(any(CrmCustomerDO.class));
    }

    @Test @DisplayName("TC-006 重复更新成交状态")
    void updateCustomerDealStatus_duplicate() {
        customer.setDealStatus(true);
        when(customerMapper.selectById(1L)).thenReturn(customer);

        assertThrows(ServiceException.class, () -> customerService.updateCustomerDealStatus(1L, true));

        verify(customerMapper, never()).updateById(any(CrmCustomerDO.class));
    }

    @Test @DisplayName("TC-007 删除无引用客户")
    void deleteCustomer_success() {
        when(customerMapper.selectById(1L)).thenReturn(customer);
        when(contactService.getContactCountByCustomerId(1L)).thenReturn(0L);
        when(businessService.getBusinessCountByCustomerId(1L)).thenReturn(0L);
        when(contractService.getContractCountByCustomerId(1L)).thenReturn(0L);
        when(customerMapper.deleteById(1L)).thenReturn(1);
        doNothing().when(permissionService).deletePermission(anyInt(), anyLong());

        assertDoesNotThrow(() -> customerService.deleteCustomer(1L));

        verify(customerMapper).deleteById(1L);
        verify(permissionService).deletePermission(anyInt(), eq(1L));
    }

    @Test @DisplayName("TC-008 客户分页查询")
    void getCustomerPage_success() {
        CrmCustomerPageReqVO reqVO = new CrmCustomerPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        PageResult<CrmCustomerDO> page = new PageResult<>();
        page.setList(Collections.singletonList(customer));
        page.setTotal(1L);
        when(customerMapper.selectPage(any(CrmCustomerPageReqVO.class), eq(100L))).thenReturn(page);

        PageResult<CrmCustomerDO> result = customerService.getCustomerPage(reqVO, 100L);

        assertEquals(1L, result.getTotal());
        assertEquals("测试客户", result.getList().get(0).getName());
    }
}
