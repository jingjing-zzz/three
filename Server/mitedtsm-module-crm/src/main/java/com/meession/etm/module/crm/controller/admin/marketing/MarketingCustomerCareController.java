package com.meession.etm.module.crm.controller.admin.marketing;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.care.MarketingCustomerCarePageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.care.MarketingCustomerCareRespVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.care.MarketingCustomerCareSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingCustomerCareDO;
import com.meession.etm.module.crm.service.marketing.MarketingCustomerCareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 客户关怀规则")
@RestController
@RequestMapping("/crm/marketing/customer-care")
@Validated
public class MarketingCustomerCareController {

    @Resource
    private MarketingCustomerCareService customerCareService;

    @PostMapping("/create")
    @Operation(summary = "创建关怀规则")
    @PreAuthorize("@ss.hasPermission('crm:marketing:customer-care:create')")
    public CommonResult<Long> createCustomerCare(@Valid @RequestBody MarketingCustomerCareSaveReqVO createReqVO) {
        return success(customerCareService.createCustomerCare(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新关怀规则")
    @PreAuthorize("@ss.hasPermission('crm:marketing:customer-care:update')")
    public CommonResult<Boolean> updateCustomerCare(@Valid @RequestBody MarketingCustomerCareSaveReqVO updateReqVO) {
        customerCareService.updateCustomerCare(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除关怀规则")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:marketing:customer-care:delete')")
    public CommonResult<Boolean> deleteCustomerCare(@RequestParam("id") Long id) {
        customerCareService.deleteCustomerCare(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得关怀规则")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:marketing:customer-care:query')")
    public CommonResult<MarketingCustomerCareRespVO> getCustomerCare(@RequestParam("id") Long id) {
        MarketingCustomerCareDO customerCare = customerCareService.getCustomerCare(id);
        return success(BeanUtils.toBean(customerCare, MarketingCustomerCareRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询关怀规则")
    @PreAuthorize("@ss.hasPermission('crm:marketing:customer-care:query')")
    public CommonResult<PageResult<MarketingCustomerCareRespVO>> pageCustomerCare(@Valid MarketingCustomerCarePageReqVO pageReqVO) {
        PageResult<MarketingCustomerCareDO> pageResult = customerCareService.getCustomerCarePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MarketingCustomerCareRespVO.class));
    }

}
