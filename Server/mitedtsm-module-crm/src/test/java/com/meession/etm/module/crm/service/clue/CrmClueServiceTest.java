package com.meession.etm.module.crm.service.clue;

import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.test.core.ut.BaseMockitoUnitTest;
import com.meession.etm.module.crm.dal.dataobject.clue.CrmClueDO;
import com.meession.etm.module.crm.dal.mysql.clue.CrmClueMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.meession.etm.module.crm.enums.ErrorCodeConstants.CLUE_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CrmClueServiceTest extends BaseMockitoUnitTest {
    @InjectMocks private CrmClueServiceImpl clueService;
    @Mock private CrmClueMapper clueMapper;
    private CrmClueDO clue;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clue = new CrmClueDO();
        clue.setId(1L);
        clue.setName("测试线索");
    }

    @Test @DisplayName("TC-011 查询线索")
    void getClue_success() {
        when(clueMapper.selectById(1L)).thenReturn(clue);
        assertEquals("测试线索", clueService.getClue(1L).getName());
    }

    @Test @DisplayName("TC-012 查询不存在线索")
    void getClue_notFound() {
        when(clueMapper.selectById(999L)).thenReturn(null);
        assertNull(clueService.getClue(999L));
    }
}
