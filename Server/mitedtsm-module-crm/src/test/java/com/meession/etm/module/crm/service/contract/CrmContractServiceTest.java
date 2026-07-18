package com.meession.etm.module.crm.service.contract;

import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.test.core.ut.BaseMockitoUnitTest;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractDO;
import com.meession.etm.module.crm.dal.mysql.contract.CrmContractMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.meession.etm.module.crm.enums.ErrorCodeConstants.CONTRACT_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CrmContractServiceTest extends BaseMockitoUnitTest {
    @InjectMocks private CrmContractServiceImpl contractService;
    @Mock private CrmContractMapper contractMapper;
    private CrmContractDO contract;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        contract = new CrmContractDO();
        contract.setId(1L);
        contract.setName("测试合同");
    }

    @Test @DisplayName("TC-013 查询合同")
    void getContract_success() {
        when(contractMapper.selectById(1L)).thenReturn(contract);
        assertEquals("测试合同", contractService.getContract(1L).getName());
    }

    @Test @DisplayName("TC-014 查询不存在合同")
    void getContract_notFound() {
        when(contractMapper.selectById(999L)).thenReturn(null);
        assertNull(contractService.getContract(999L));
    }
}
