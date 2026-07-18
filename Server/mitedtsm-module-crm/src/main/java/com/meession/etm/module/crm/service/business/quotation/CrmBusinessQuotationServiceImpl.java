package com.meession.etm.module.crm.service.business.quotation;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.business.vo.quotation.CrmBusinessQuotationPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.business.*;
import com.meession.etm.module.crm.dal.dataobject.product.CrmProductDO;
import com.meession.etm.module.crm.dal.mysql.business.CrmBusinessQuotationItemMapper;
import com.meession.etm.module.crm.dal.mysql.business.CrmBusinessQuotationMapper;
import com.meession.etm.module.crm.dal.mysql.business.CrmBusinessQuotationSnapshotMapper;
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
    private CrmBusinessQuotationSnapshotMapper businessQuotationSnapshotMapper;
    @Resource
    private CrmBusinessService businessService;
    @Resource
    private CrmProductService productService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQuotationDraft(Long businessId) {
        businessService.validateBusiness(businessId);
        List<CrmBusinessProductDO> businessProducts = businessService.getBusinessProductListByBusinessId(businessId);
        if (CollUtil.isEmpty(businessProducts)) {
            throw exception(BUSINESS_QUOTATION_DRAFT_FAIL_NO_PRODUCT);
        }
        Set<Long> productIds = businessProducts.stream()
                .map(CrmBusinessProductDO::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, CrmProductDO> productMap = productIds.isEmpty()
                ? Collections.emptyMap() : productService.getProductMap(productIds);
        String quotationNo = generateQuotationNo();
        CrmBusinessQuotationDO quotation = CrmBusinessQuotationDO.builder()
                .businessId(businessId)
                .quotationNo(quotationNo)
                .status(0)
                .discountPercent(BigDecimal.ZERO)
                .build();
        businessQuotationMapper.insert(quotation);
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
        quotation.setTotalProductPrice(totalProductPrice.setScale(2, RoundingMode.HALF_UP));
        quotation.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
        businessQuotationMapper.updateById(quotation);
        return quotation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmQuotation(Long quotationId) {
        CrmBusinessQuotationDO quotation = businessQuotationMapper.selectById(quotationId);
        if (quotation == null) {
            throw exception(BUSINESS_QUOTATION_NOT_EXISTS);
        }
        if (!Integer.valueOf(0).equals(quotation.getStatus())) {
            throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_STATUS);
        }
        CrmBusinessDO business = businessService.getBusiness(quotation.getBusinessId());
        if (business.getEndStatus() != null) {
            throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_BUSINESS_END);
        }
        List<CrmBusinessQuotationItemDO> items = businessQuotationItemMapper.selectListByQuotationId(quotationId);
        if (CollUtil.isEmpty(items)) {
            throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_NO_PRODUCT);
        }
        BigDecimal totalProductPrice = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CrmBusinessQuotationItemDO item : items) {
            if (item.getCount() == null || item.getCount().compareTo(BigDecimal.ZERO) <= 0) {
                throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_PRODUCT_COUNT);
            }
            if (item.getActualPrice() != null && item.getActualPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_PRODUCT_PRICE);
            }
            if (item.getTotalPrice() != null && item.getTotalPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw exception(BUSINESS_QUOTATION_CONFIRM_FAIL_PRODUCT_PRICE);
            }
            BigDecimal actualPrice = item.getActualPrice() != null ? item.getActualPrice() : BigDecimal.ZERO;
            BigDecimal itemTotal = actualPrice.multiply(item.getCount()).setScale(2, RoundingMode.HALF_UP);
            item.setTotalPrice(itemTotal);
            businessQuotationItemMapper.updateById(item);
            if (item.getStandardPrice() != null) {
                totalProductPrice = totalProductPrice.add(
                        item.getStandardPrice().multiply(item.getCount()).setScale(2, RoundingMode.HALF_UP));
            }
            totalPrice = totalPrice.add(itemTotal);
        }
        quotation.setStatus(1);
        quotation.setConfirmedBy(getLoginUserId());
        quotation.setConfirmedTime(LocalDateTime.now());
        quotation.setTotalProductPrice(totalProductPrice.setScale(2, RoundingMode.HALF_UP));
        quotation.setTotalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP));
        businessQuotationMapper.updateById(quotation);
        saveSnapshot(quotation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void voidQuotation(Long quotationId) {
        CrmBusinessQuotationDO quotation = businessQuotationMapper.selectById(quotationId);
        if (quotation == null) {
            throw exception(BUSINESS_QUOTATION_NOT_EXISTS);
        }
        if (Integer.valueOf(2).equals(quotation.getStatus())) {
            return;
        }
        quotation.setStatus(2);
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

    private void saveSnapshot(CrmBusinessQuotationDO quotation) {
        CrmBusinessQuotationSnapshotDO snapshot = CrmBusinessQuotationSnapshotDO.fromQuotation(quotation);
        businessQuotationSnapshotMapper.insert(snapshot);
    }

    private String generateQuotationNo() {
        return "QT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%03d", new Random().nextInt(1000));
    }

}