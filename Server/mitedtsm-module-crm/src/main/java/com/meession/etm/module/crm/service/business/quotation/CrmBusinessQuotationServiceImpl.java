package com.meession.etm.module.crm.service.business.quotation;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.business.vo.quotation.CrmBusinessQuotationPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.business.*;
import com.meession.etm.module.crm.dal.dataobject.product.CrmProductDO;
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

    @Resource
    private CrmBusinessQuotationMapper businessQuotationMapper;
    @Resource
    private CrmBusinessQuotationItemMapper businessQuotationItemMapper;
    @Resource
    private CrmBusinessService businessService;
    @Resource
    private CrmProductService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQuotationDraft(Long businessId) {
        // 1. 校验商机存在
        businessService.validateBusiness(businessId);
        // 2. 获取商机产品
        List<CrmBusinessProductDO> businessProducts = businessService.getBusinessProductListByBusinessId(businessId);
        if (CollUtil.isEmpty(businessProducts)) {
            throw exception(BUSINESS_QUOTATION_DRAFT_FAIL_NO_PRODUCT);
        }
        // 3. 获取产品信息
        Set<Long> productIds = businessProducts.stream()
                .map(CrmBusinessProductDO::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, CrmProductDO> productMap = productIds.isEmpty()
                ? Collections.emptyMap() : productService.getProductMap(productIds);
        // 4. 生成报价编号
        String quotationNo = generateQuotationNo();
        // 5. 创建报价草稿
        CrmBusinessQuotationDO quotation = CrmBusinessQuotationDO.builder()
                .businessId(businessId)
                .quotationNo(quotationNo)
                .status(0) // DRAFT
                .discountPercent(BigDecimal.ZERO)
                .build();
        businessQuotationMapper.insert(quotation);
        // 6. 创建报价产品项并计算金额
        BigDecimal totalProductPrice = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CrmBusinessProductDO bp : businessProducts) {
            CrmProductDO product = productMap.get(bp.getProductId());
            BigDecimal standardPrice = product != null ? product.getPrice() : null;
            BigDecimal actualPrice = bp.getBusinessPrice() != null ? bp.getBusinessPrice() : bp.getProductPrice();
            BigDecimal count = bp.getCount() != null ? bp.getCount() : BigDecimal.ZERO;
            BigDecimal itemTotal = actualPrice != null
                    ? actualPrice.multiply(count).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            if (standardPrice != null) {
                totalProductPrice = totalProductPrice.add(
                        standardPrice.multiply(count).setScale(2, RoundingMode.HALF_UP));
            }
            totalPrice = totalPrice.add(itemTotal);
            CrmBusinessQuotationItemDO item = CrmBusinessQuotationItemDO.builder()
                    .quotationId(quotation.getId())
                    .productId(bp.getProductId())
                    .productName(product != null ? product.getName() : null)
                    .productNo(product != null ? product.getNo() : null)
                    .standardPrice(standardPrice)
                    .actualPrice(actualPrice)
                    .count(count)
                    .discountPercent(BigDecimal.ZERO)
                    .totalPrice(itemTotal)
                    .build();
            businessQuotationItemMapper.insert(item);
        }
        // 7. 更新报价金额
        quotation.setTotalProductPrice(totalProductPrice.setScale(2, RoundingMode.HALF_UP));
        quotation.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
        businessQuotationMapper.updateById(quotation);
        return quotation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmQuotation(Long quotationId) {
        // 1. 校验报价存在
        CrmBusinessQuotationDO quotation = businessQuotationMapper.selectById(quotationId);
        if (quotation == null) {
            throw exception(BUSINESS_QUOTATION_NOT_EXISTS);
        }
        // 2. 校验报价状态为草稿（不能重复确认）
        if (!Integer.valueOf(0).equals(quotation.getStatus())) {
            throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_STATUS);
        }
        // 3. 校验商机未成交未流失
        CrmBusinessDO business = businessService.getBusiness(quotation.getBusinessId());
        if (business.getEndStatus() != null) {
            throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_BUSINESS_END);
        }
        // 4. 校验报价产品项
        List<CrmBusinessQuotationItemDO> items = businessQuotationItemMapper.selectListByQuotationId(quotationId);
        if (CollUtil.isEmpty(items)) {
            throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_NO_PRODUCT);
        }
        // 5. 重新计算金额并校验
        BigDecimal totalProductPrice = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CrmBusinessQuotationItemDO item : items) {
            // 数量必须大于 0
            if (item.getCount() == null || item.getCount().compareTo(BigDecimal.ZERO) <= 0) {
                throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_PRODUCT_COUNT);
            }
            // 金额不能为负数
            if (item.getActualPrice() != null && item.getActualPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_PRODUCT_PRICE);
            }
            if (item.getTotalPrice() != null && item.getTotalPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_PRODUCT_PRICE);
            }
            // 重新计算行金额
            BigDecimal actualPrice = item.getActualPrice() != null ? item.getActualPrice() : BigDecimal.ZERO;
            BigDecimal itemTotal = actualPrice.multiply(item.getCount()).setScale(2, RoundingMode.HALF_UP);
            item.setTotalPrice(itemTotal);
            businessQuotationItemMapper.updateById(item);
            // 累计金额
            if (item.getStandardPrice() != null) {
                totalProductPrice = totalProductPrice.add(
                        item.getStandardPrice().multiply(item.getCount()).setScale(2, RoundingMode.HALF_UP));
            }
            totalPrice = totalPrice.add(itemTotal);
        }
        // 6. 更新报价状态为已确认
        quotation.setStatus(1); // CONFIRMED
        quotation.setConfirmedBy(getLoginUserId());
        quotation.setConfirmedTime(LocalDateTime.now());
        quotation.setTotalProductPrice(totalProductPrice.setScale(2, RoundingMode.HALF_UP));
        quotation.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
        businessQuotationMapper.updateById(quotation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void voidQuotation(Long quotationId) {
        // 1. 校验报价存在
        CrmBusinessQuotationDO quotation = businessQuotationMapper.selectById(quotationId);
        if (quotation == null) {
            throw exception(BUSINESS_QUOTATION_NOT_EXISTS);
        }
        // 2. 已作废直接返回
        if (Integer.valueOf(2).equals(quotation.getStatus())) {
            return;
        }
        // 3. 设置为作废
        quotation.setStatus(2); // VOID
        businessQuotationMapper.updateById(quotation);
    }

    @Override
    public CrmBusinessQuotationDO getQuotation(Long id) {
        return businessQuotationMapper.selectById(id);
    }

    @Override
    public List<CrmBusinessQuotationItemDO> getQuotationItems(Long quotationId) {
        return businessQuotationItemMapper.selectListByQuotationId(quotationId);
    }

    @Override
    public PageResult<CrmBusinessQuotationDO> getQuotationPage(CrmBusinessQuotationPageReqVO reqVO) {
        return businessQuotationMapper.selectPage(reqVO);
    }

    @Override
    public CrmBusinessQuotationDO getLatestConfirmedQuotation(Long businessId) {
        List<CrmBusinessQuotationDO> list = businessQuotationMapper.selectListByBusinessId(businessId);
        return list.stream()
                .filter(q -> Integer.valueOf(1).equals(q.getStatus()))
                .max(Comparator.comparing(CrmBusinessQuotationDO::getId))
                .orElse(null);
    }

    /**
     * 生成报价编号：QT + yyyyMMddHHmmss + 3位随机数
     */
    private String generateQuotationNo() {
        return "QT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%03d", new Random().nextInt(1000));
    }

}
