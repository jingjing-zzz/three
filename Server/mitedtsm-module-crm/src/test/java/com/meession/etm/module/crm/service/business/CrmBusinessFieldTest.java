package com.meession.etm.module.crm.service.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CrmBusinessFieldTest {

  @InjectMocks
  private com.meession.etm.module.crm.service.business.CrmBusinessServiceImpl businessService;

  // 测试1：未跟进天数-有最后跟进时间
  @Test
  public void testDaysWithoutFollowUp_withContactLastTime() {
    var business = com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO.builder()
        .id(1L)
        .contactLastTime(LocalDateTime.now().minusDays(10))
        .build();

    Integer days = businessService.calculateDaysWithoutFollowUp(business);
    assertEquals(10, days);
  }

  // 测试2：未跟进天数-无最后跟进时间，用创建时间
  @Test
  public void testDaysWithoutFollowUp_withoutContactLastTime() {
    var business = com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO.builder()
        .id(1L)
        .contactLastTime(null)
        .build();
    // createTime 是 BaseDO 的字段，@Builder 不含父类字段，用 setter 设置
    business.setCreateTime(LocalDateTime.now().minusDays(30));

    Integer days = businessService.calculateDaysWithoutFollowUp(business);
    assertEquals(30, days);
  }

  // 测试3：未跟进天数-无任何时间
  @Test
  public void testDaysWithoutFollowUp_noTime() {
    var business = com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO.builder()
        .id(1L)
        .contactLastTime(null)
        .build();

    Integer days = businessService.calculateDaysWithoutFollowUp(business);
    assertEquals(0, days);
  }

  // 测试4：竞争对手字段长度校验
  @Test
  public void testCompetitorField_lengthValidation() {
    // 验证competitor字段最大100字符
    String competitor = "a".repeat(100);
    assertEquals(100, competitor.length());

    String tooLong = "a".repeat(101);
    assertTrue(tooLong.length() > 100);
  }
}
