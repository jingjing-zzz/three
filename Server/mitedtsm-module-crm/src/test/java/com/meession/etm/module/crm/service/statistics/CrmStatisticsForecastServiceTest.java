package com.meession.etm.module.crm.service.statistics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CrmStatisticsForecastServiceTest {

  @Mock
  private com.meession.etm.module.crm.dal.mysql.business.CrmBusinessMapper businessMapper;
  @Mock
  private com.meession.etm.module.crm.service.business.CrmBusinessStatusService businessStatusService;
  @Mock
  private com.meession.etm.module.crm.service.customer.CrmCustomerService customerService;
  @Mock
  private com.meession.etm.module.system.api.user.AdminUserApi adminUserApi;

  @InjectMocks
  private com.meession.etm.module.crm.service.statistics.CrmStatisticsForecastServiceImpl forecastService;

  // 测试1：阶段概率加权金额计算
  @Test
  public void testForecastAmount_weightedCalculation() {
    // 准备：两个活跃商机
    List<com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO> businesses = Arrays.asList(
        createBusiness(1L, new BigDecimal("10000.00"), 1L, null), // 阶段1, percent=20
        createBusiness(2L, new BigDecimal("20000.00"), 2L, null) // 阶段2, percent=50
    );
    when(businessMapper.selectList(any())).thenReturn(businesses);

    // 阶段配置
    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessStatusDO status1 = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessStatusDO();
    status1.setId(1L);
    status1.setPercent(20);
    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessStatusDO status2 = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessStatusDO();
    status2.setId(2L);
    status2.setPercent(50);
    when(businessStatusService.getBusinessStatusMap(any())).thenReturn(
        java.util.Map.of(1L, status1, 2L, status2));

    var reqVO = new com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastReqVO();
    reqVO.setPageSize(-1); // 不分页

    // 执行
    var summary = forecastService.getForecastSummary(reqVO);

    // 验证：forecastAmount = 10000*0.2 + 20000*0.5 = 2000 + 10000 = 12000
    assertEquals(new BigDecimal("12000.00"), summary.getForecastAmount());
    assertEquals(2, summary.getBusinessCount());
    assertEquals(new BigDecimal("30000.00"), summary.getTotalAmount());
  }

  // 测试2：流失商机排除
  @Test
  public void testForecastAmount_excludeLostBusinesses() {
    List<com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO> businesses = Arrays.asList(
        createBusiness(1L, new BigDecimal("10000.00"), 1L, null), // 活跃
        createBusiness(2L, new BigDecimal("20000.00"), 2L, 2) // 流失(endStatus=2)
    );
    when(businessMapper.selectList(any())).thenReturn(businesses);

    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessStatusDO status1 = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessStatusDO();
    status1.setId(1L);
    status1.setPercent(20);
    when(businessStatusService.getBusinessStatusMap(any())).thenReturn(java.util.Map.of(1L, status1));

    var reqVO = new com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastReqVO();
    reqVO.setPageSize(-1);

    var summary = forecastService.getForecastSummary(reqVO);

    // 只计入活跃商机
    assertEquals(1, summary.getBusinessCount());
    assertEquals(new BigDecimal("2000.00"), summary.getForecastAmount());
  }

  // 测试3：成交商机口径
  @Test
  public void testForecastSummary_wonAmount() {
    List<com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO> businesses = Arrays.asList(
        createBusiness(1L, new BigDecimal("10000.00"), 1L, null), // 活跃
        createBusiness(2L, new BigDecimal("20000.00"), 2L, 1) // 成交(endStatus=1)
    );
    when(businessMapper.selectList(any())).thenReturn(businesses);

    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessStatusDO status1 = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessStatusDO();
    status1.setId(1L);
    status1.setPercent(20);
    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessStatusDO status2 = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessStatusDO();
    status2.setId(2L);
    status2.setPercent(100); // 成交阶段100%
    when(businessStatusService.getBusinessStatusMap(any())).thenReturn(java.util.Map.of(1L, status1, 2L, status2));

    var reqVO = new com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastReqVO();
    reqVO.setPageSize(-1);

    var summary = forecastService.getForecastSummary(reqVO);

    // wonAmount = 20000
    assertEquals(new BigDecimal("20000.00"), summary.getWonAmount());
    assertEquals(1, summary.getWonCount());
  }

  // 测试4：空数据
  @Test
  public void testForecastSummary_emptyData() {
    when(businessMapper.selectList(any())).thenReturn(Collections.emptyList());

    var reqVO = new com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastReqVO();
    reqVO.setPageSize(-1);

    var summary = forecastService.getForecastSummary(reqVO);

    assertEquals(0, summary.getBusinessCount());
    assertEquals(BigDecimal.ZERO, summary.getForecastAmount());
  }

  private com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO createBusiness(
      Long id, BigDecimal totalPrice, Long statusId, Integer endStatus) {
    return com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO.builder()
        .id(id)
        .totalPrice(totalPrice)
        .statusId(statusId)
        .endStatus(endStatus)
        .build();
  }
}
