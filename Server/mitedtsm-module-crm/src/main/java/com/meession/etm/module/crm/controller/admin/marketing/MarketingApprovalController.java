package com.meession.etm.module.crm.controller.admin.marketing;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.approval.MarketingApprovalPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.approval.MarketingApprovalRespVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.approval.MarketingApprovalSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingApprovalDO;
import com.meession.etm.module.crm.service.marketing.MarketingApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 群发审批")
@RestController
@RequestMapping("/crm/marketing/approval")
@Validated
public class MarketingApprovalController {

    @Resource
    private MarketingApprovalService approvalService;

    @PostMapping("/create")
    @Operation(summary = "创建群发审批")
    @PreAuthorize("@ss.hasPermission('crm:marketing:approval:create')")
    public CommonResult<Long> createApproval(@Valid @RequestBody MarketingApprovalSaveReqVO createReqVO) {
        return success(approvalService.createApproval(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新群发审批")
    @PreAuthorize("@ss.hasPermission('crm:marketing:approval:update')")
    public CommonResult<Boolean> updateApproval(@Valid @RequestBody MarketingApprovalSaveReqVO updateReqVO) {
        approvalService.updateApproval(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除群发审批")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:marketing:approval:delete')")
    public CommonResult<Boolean> deleteApproval(@RequestParam("id") Long id) {
        approvalService.deleteApproval(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得群发审批")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:marketing:approval:query')")
    public CommonResult<MarketingApprovalRespVO> getApproval(@RequestParam("id") Long id) {
        MarketingApprovalDO approval = approvalService.getApproval(id);
        return success(BeanUtils.toBean(approval, MarketingApprovalRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询群发审批")
    @PreAuthorize("@ss.hasPermission('crm:marketing:approval:query')")
    public CommonResult<PageResult<MarketingApprovalRespVO>> pageApproval(@Valid MarketingApprovalPageReqVO pageReqVO) {
        PageResult<MarketingApprovalDO> pageResult = approvalService.getApprovalPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MarketingApprovalRespVO.class));
    }

    @PutMapping("/approve")
    @Operation(summary = "审批通过")
    @PreAuthorize("@ss.hasPermission('crm:marketing:approval:approve')")
    public CommonResult<Boolean> approve(@Parameter(name = "id", description = "编号", required = true) @RequestParam Long id) {
        approvalService.approve(id, 1, null);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "审批拒绝")
    @PreAuthorize("@ss.hasPermission('crm:marketing:approval:approve')")
    public CommonResult<Boolean> reject(@Parameter(name = "id", description = "编号", required = true) @RequestParam Long id) {
        approvalService.approve(id, 2, null);
        return success(true);
    }

}
