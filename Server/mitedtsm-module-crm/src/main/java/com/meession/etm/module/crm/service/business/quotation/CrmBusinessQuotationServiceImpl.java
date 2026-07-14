package com.meession.etm.module.crm.service.business.quotation;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.business.vo.quotation.CrmBusinessQuotationPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.business.*;
import com.meession.etm.module.crm.dal.dataobject.product.CrmProductDO;
import com.meession.etm.module.crm.dal.mysql.business.CrmBusinessProductMapper;
import com.meession.etm.module.crm.dal.mysql.business.CrmBusinessQuotationItemMapper;
import com.meession.etm.module.crm.dal.mysql.business.CrmBusinessQuotationMapper;
import com.meession.etm.module.crm.service.business.CrmBusinessService;
import com.meession.etm.module.crm.service.product.CrmProductService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.meession.etm.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.meession.etm.module.crm.enums.ErrorCodeConstants.*;

/**
 * CRM 商机报价 Service 实现类
 *
 * @author traebot
 */
@Service
@Validated
public class CrmBusinessQuotationServiceImpl implements CrmBusinessQuotationService {


  private C
  @Resource
  private C
  @Resource
  private C
  @Resource
  private C
  @Resource
  private C


  @Transact
  public Long createQuotationDraft(Long busines
      // 1. 校验商机存在
    businessServ
    // 2. 获取商机产品
    List<CrmBusi
    if (CollUtil.isEmpty(businessProducts)) {
        throw exception(BUSINESS_QUOTATION_DR

    /
    Set<Long> prod
            .map(CrmBusinessProductDO::getProductId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet
        , CrmProductDO> productMap =
    // 4. 生成报价编号
    String quota
    // 5. 创建报价草稿
    CrmBusinessQ
            .businessId(businessId)
        .quotationNo(quotationN
        .status(0) // DRAFT
        .discountPercent(Bi
        .build();
        Quotation
    // 6. 创建报价产品项并计算金额
    BigDecimal totalPr
    BigDecimal totalPrice = BigDecimal.ZERO;
    for (CrmBusinessProductDO bp : businessP
        CrmProductDO product = productMap.get(bp.getPr
      BigDecimal standardPrice = product != null ? product.getP
      BigDecimal actualPrice = bp.getBusinessPrice() != null ? bp.getBusinessPrice() : bp.get
      BigDecimal count = bp.getCount() != null ? bp.getCount() : BigDecimal.ZERO;
      BigDecimal itemTotal = actualPrice.multiply(count).setScale(2, RoundingMode
      if (standardPrice != null) {
          totalProductPrice = tota

      t
      CrmBusinessQuotationItemDO item = CrmBu
              .quotationId(quotation.getId())
          .productId(bp.getProductId())
          .productName(product != null
          .productNo(product != null ? product.getNo() : null)
          .standardPrice(standardPrice)
          .actualPrice(actualPrice)
          .count(count)
          .discountPerc
          .totalPrice(itemTotal)
          .build();
          Quotation

    /
    quotation.setTotalPr
    quotation.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
    businessQuotationMapper.updateById(quotation);
    return quotation.getId();



  @Transact
  public void confirmQuotation(Long quotationId
      // 1. 校验报价存在
    CrmBusinessQ
    // 2. 校验报价状态为草稿（不能重复确认）
    if (!Integer.valueOf(0)
        throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_STAT

    /
    CrmBusinessDO busine
    if (business.getEndStatus() != null) {
        throw exception(BUSINESS_QUOTATION

    /
    List<CrmBusin
    if (CollUtil.isEmpty(items)) {
        throw exception(BUSINESS_Q

    /
    BigDecimal totalProductPrice =
    BigDecimal totalPrice = BigDecimal.ZERO;
    for (CrmBusinessQuotationItemDO item : i
        // 数量必须大于 0
      if (item.ge
          throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_PRODUCT_COUNT);

      /
      if (item.
          throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_PRODUCT_PRICE);

      i
          throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_PRODUCT_PRICE);

      /
      BigDecimal
      BigDecimal itemTotal = actualPrice.multiply(item.getCount()).setScale(2, RoundingMode.HALF_UP);
      item.setTotalPrice(itemTotal);
      businessQuotationItemMapper.up
      // 累计金额
      if (ite
          totalProductPrice = totalProductPr

      t

    /
    quotation.setSta
    quotation.setConfirmedBy(getLoginUse
    quotation.setConfirmedTime(LocalDateTime.no
    quotation.setTotalProductPrice(totalProductPrice
    quotation.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
    businessQuotationMapper.updateById(quotation);



  @Transact
  public void voidQuotation(Long quotationId) {
      // 1. 校验报价存在
    CrmBusinessQ
    // 2. 已作废直接返回
    if (Integer.v
        return;

    /
    quotation.setS
    businessQuotationMapper.updateB



  public Cr
      return businessQuotationMapper.selectById(id);



  public Li
      return businessQuotationItemMapper.selectListByQuotationId(quotationId);



  public Pa
      return businessQuotationMapper.selectPage(reqVO);



  public Cr
      List<CrmBusinessQuotationDO> list = businessQuotationMapper.selectListBy
    return list.stream()
            .filter(q ->
        .max(Comparator.comparing(CrmBusinessQuotationDO::getI
        .orElse(null);



      CrmBusinessQuotationDO quotation = businessQuotationMapper.se
    if (quotation == null) {
        throw exception(BUSI

    r



   *
   */
   ri
      return "QT" + LocalDateTime.now().
            + String.format("%03d", new Random().nextInt(1000));


}
