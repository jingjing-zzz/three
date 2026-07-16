package com.meession.etm.module.crm.controller.admin.business;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.business.vo.quotation.CrmBusinessQuotationPageReqVO;
import com.meession.etm.module.crm.controller.admin.business.vo.quotation.CrmBusinessQuotationRespVO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationItemDO;
import com.meession.etm.module.crm.service.business.quotation.CrmBusinessQuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - CRM 商机报价")
@RestController
@RequestMapping("/crm/business-quotation")
@Validated
public class CrmBusinessQuotationController {

    @Resource
    private CrmBusinessQuotationService businessQuotationService;

    @PostMapping("/create-draft")
    @Operation(summary = "创建报价草稿")
    @Parameter(name = "businessId", description = "商机编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:business-quotation:create')")
    public CommonResult<Long> createQuotationDraft(@RequestParam("businessId") Long businessId) {
        return success(businessQuotationService.createQuotationDraft(businessId));
    }

    @PutMapping("/confirm")
    @Operation(summary = "确认报价")
    @Parameter(name = "quotationId", description = "报价编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:business-quotation:confirm')")
    public CommonResult<Boolean> confirmQuotation(@RequestParam("quotationId") Long quotationId) {
        businessQuotationService.confirmQuotation(quotationId);
        return success(true);
    }

    @PutMapping("/void")
    @Operation(summary = "作废报价")
    @Parameter(name = "quotationId", description = "报价编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:business-quotation:void')")
    public CommonResult<Boolean> voidQuotation(@RequestParam("quotationId") Long quotationId) {
        businessQuotationService.voidQuotation(quotationId);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取报价详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:business-quotation:query')")
    public CommonResult<CrmBusinessQuotationRespVO> getQuotation(@RequestParam("id") Long id) {
        CrmBusinessQuotationDO quotation = businessQuotationService.getQuotation(id);
        if (quotation == null) {
            return success(null);
        }
        CrmBusinessQuotationRespVO respVO = BeanUtils.toBean(quotation, CrmBusinessQuotationRespVO.class);
        respVO.setStatusName(getStatusName(quotation.getStatus()));
        List<CrmBusinessQuotationItemDO> items = businessQuotationService.getQuotationItems(id);
        respVO.setItems(BeanUtils.toBean(items, CrmBusinessQuotationRespVO.Item.class));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "报价分页")
    @PreAuthorize("@ss.hasPermission('crm:business-quotation:query')")
    public CommonResult<PageResult<CrmBusinessQuotationRespVO>> getQuotationPage(@Valid CrmBusinessQuotationPageReqVO reqVO) {
        PageResult<CrmBusinessQuotationDO> pageResult = businessQuotationService.getQuotationPage(reqVO);
        List<CrmBusinessQuotationRespVO> respVOList = buildQuotationList(pageResult.getList());
        return success(new PageResult<>(respVOList, pageResult.getTotal()));
    }

    @GetMapping("/latest-confirmed")
    @Operation(summary = "获取商机最新已确认报价")
    @Parameter(name = "businessId", description = "商机编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:business-quotation:query')")
    public CommonResult<CrmBusinessQuotationRespVO> getLatestConfirmedQuotation(@RequestParam("businessId") Long businessId) {
        CrmBusinessQuotationDO quotation = businessQuotationService.getLatestConfirmedQuotation(businessId);
        if (quotation == null) {
            return success(null);
        }
        CrmBusinessQuotationRespVO respVO = BeanUtils.toBean(quotation, CrmBusinessQuotationRespVO.class);
        respVO.setStatusName(getStatusName(quotation.getStatus()));
        return success(respVO);
    }

    private List<CrmBusinessQuotationRespVO> buildQuotationList(List<CrmBusinessQuotationDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<CrmBusinessQuotationRespVO> respVOList = BeanUtils.toBean(list, CrmBusinessQuotationRespVO.class);
        for (int i = 0; i < respVOList.size(); i++) {
            CrmBusinessQuotationRespVO respVO = respVOList.get(i);
            respVO.setStatusName(getStatusName(list.get(i).getStatus()));
            List<CrmBusinessQuotationItemDO> items = businessQuotationService.getQuotationItems(list.get(i).getId());
            respVO.setItems(BeanUtils.toBean(items, CrmBusinessQuotationRespVO.Item.class));
        }
        return respVOList;
    }

    private String getStatusName(Integer status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case 0:
                return "草稿";
            case 1:
                return "已确认";
            case 2:
                return "已作废";
            default:
                return null;
        }
    }

}