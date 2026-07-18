package com.meession.etm.module.crm.service.business;

import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.test.core.ut.BaseMockitoUnitTest;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO;
import com.meession.etm.module.crm.dal.mysql.business.CrmBusinessMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.meession.etm.module.crm.enums.ErrorCodeConstants.BUSINESS_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CrmBusinessServiceTest extends BaseMockitoUnitTest {
    @InjectMocks private CrmBusinessServiceImpl businessService;
    @Mock private CrmBusinessMapper businessMapper;
    private CrmBusinessDO business;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        business = new CrmBusinessDO();
        business.setId(1L);
        business.setName("测试商机");
    }

    @Test @DisplayName("TC-009 查询商机")
    void getBusiness_success() {
        when(businessMapper.selectById(1L)).thenReturn(business);
        assertEquals("测试商机", businessService.getBusiness(1L).getName());
    }

    @Test @DisplayName("TC-010 查询不存在商机")
    void getBusiness_notFound() {
        when(businessMapper.selectById(999L)).thenReturn(null);
        assertNull(businessService.getBusiness(999L));
    }
}
