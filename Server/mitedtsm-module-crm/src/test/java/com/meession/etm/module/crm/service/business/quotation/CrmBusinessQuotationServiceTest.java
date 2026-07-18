package com.meession.etm.module.crm.service.business.quotation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CrmBusinessQuotationServiceTest {

  @Mock
  private com.meession.etm.module.crm.dal.mysql.business.CrmBusinessQuotationMapper quotationMapper;
  @Mock
  private com.meession.etm.module.crm.dal.mysql.business.CrmBusinessQuotationItemMapper quotationItemMapper;
  @Mock
  private com.meession.etm.module.crm.dal.mysql.business.CrmBusinessMapper businessMapper;
  @Mock
  private com.meession.etm.module.crm.dal.mysql.business.CrmBusinessProductMapper businessProductMapper;
  @Mock
  private com.meession.etm.module.crm.service.business.CrmBusinessService businessService;
  @Mock
  private com.meession.etm.module.crm.service.product.CrmProductService productService;

  @InjectMocks
  private com.meession.etm.module.crm.service.business.quotation.CrmBusinessQuotationServiceImpl quotationService;

  // 测试1：创建报价草稿-正常流程
  @Test
  public void testCreateQuotationDraft_success() {
    // 准备：商机存在，有产品行
    Long businessId = 1L;
    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO business = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO();
    business.setId(businessId);
    business.setTotalProductPrice(new BigDecimal("1000.00"));
    business.setDiscountPercent(new BigDecimal("10.00"));
    business.setTotalPrice(new BigDecimal("900.00"));
    when(businessService.validateBusiness(businessId)).thenReturn(business);

    List<com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessProductDO> products = Arrays.asList(
        createBusinessProduct(1L, new BigDecimal("100.00"), new BigDecimal("90.00"), new BigDecimal("2"),
            new BigDecimal("180.00")),
        createBusinessProduct(2L, new BigDecimal("200.00"), new BigDecimal("180.00"), new BigDecimal("4"),
            new BigDecimal("720.00")));
    when(businessService.getBusinessProductListByBusinessId(businessId)).thenReturn(products);
    when(productService.getProductMap(anySet())).thenReturn(Collections.emptyMap());
    when(quotationMapper.insert(any(com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO.class))).thenAnswer(invocation -> {
        com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO q = invocation.getArgument(0);
        q.setId(1L);
        return 1;
    });

    // 执行
    Long quotationId = quotationService.createQuotationDraft(businessId);

    // 验证
    assertNotNull(quotationId);
    verify(quotationMapper).insert(any(com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO.class));
    verify(quotationItemMapper, times(2)).insert(any(com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationItemDO.class));
  }

  // 测试2：确认报价-空产品列表
  @Test
  public void testConfirmQuotation_emptyProducts() {
    // 准备：报价存在但没有产品项
    Long quotationId = 1L;
    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO quotation = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO();
    quotation.setId(quotationId);
    quotation.setBusinessId(1L);
    quotation.setStatus(0); // DRAFT
    when(quotationMapper.selectById(quotationId)).thenReturn(quotation);
    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO business = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO();
    business.setId(1L);
    business.setEndStatus(null); // 未成交未流失
    when(businessService.getBusiness(1L)).thenReturn(business);
    when(quotationItemMapper.selectListByQuotationId(quotationId)).thenReturn(Collections.emptyList());

    // 执行和验证：应抛出异常
    assertThrows(RuntimeException.class, () -> quotationService.confirmQuotation(quotationId));
  }

  // 测试3：确认报价-重复确认
  @Test
  public void testConfirmQuotation_alreadyConfirmed() {
    Long quotationId = 1L;
    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO quotation = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO();
    quotation.setId(quotationId);
    quotation.setStatus(1); // CONFIRMED
    when(quotationMapper.selectById(quotationId)).thenReturn(quotation);

    assertThrows(RuntimeException.class, () -> quotationService.confirmQuotation(quotationId));
  }

  // 测试4：确认报价-商机已成交
  @Test
  public void testConfirmQuotation_businessWon() {
    Long quotationId = 1L;
    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO quotation = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO();
    quotation.setId(quotationId);
    quotation.setBusinessId(1L);
    quotation.setStatus(0); // DRAFT
    when(quotationMapper.selectById(quotationId)).thenReturn(quotation);

    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO business = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO();
    business.setId(1L);
    business.setEndStatus(1); // 已成交
    when(businessService.getBusiness(1L)).thenReturn(business);

    assertThrows(RuntimeException.class, () -> quotationService.confirmQuotation(quotationId));
  }

  // 测试5：快照不受产品价格变更影响
  @Test
  public void testQuotationSnapshot_isolated() {
    Long quotationId = 1L;
    com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO quotation = new com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO();
    quotation.setId(quotationId);
    quotation.setBusinessId(1L);
    quotation.setStatus(0);
    quotation.setTotalPrice(new BigDecimal("900.00"));
    when(quotationMapper.selectById(quotationId)).thenReturn(quotation);

    var result = quotationService.getQuotation(quotationId);
    assertNotNull(result);
    assertEquals(new BigDecimal("900.00"), result.getTotalPrice());
  }

  private com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessProductDO createBusinessProduct(
      Long productId, BigDecimal productPrice, BigDecimal businessPrice, BigDecimal count, BigDecimal totalPrice) {
    return com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessProductDO.builder()
        .productId(productId)
        .productPrice(productPrice)
        .businessPrice(businessPrice)
        .count(count)
        .totalPrice(totalPrice)
        .build();
  }

  private com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationItemDO createQuotationItem(
      Long productId, String productName, BigDecimal standardPrice, BigDecimal actualPrice, BigDecimal count) {
    return com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationItemDO.builder()
        .productId(productId)
        .productName(productName)
        .standardPrice(standardPrice)
        .actualPrice(actualPrice)
        .count(count)
        .build();
  }
}
