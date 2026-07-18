package com.meession.etm.module.crm.controller.admin.marketing;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchRespVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.email.MarketingEmailBatchSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingEmailBatchDO;
import com.meession.etm.module.crm.service.marketing.MarketingEmailBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 营销邮件批次")
@RestController
@RequestMapping("/crm/marketing/email-batch")
@Validated
public class MarketingEmailBatchController {

    @Resource
    private MarketingEmailBatchService emailBatchService;

    @PostMapping("/create")
    @Operation(summary = "创建邮件批次")
    @PreAuthorize("@ss.hasPermission('crm:marketing:email-batch:create')")
    public CommonResult<Long> createEmailBatch(@Valid @RequestBody MarketingEmailBatchSaveReqVO createReqVO) {
        return success(emailBatchService.createEmailBatch(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新邮件批次")
    @PreAuthorize("@ss.hasPermission('crm:marketing:email-batch:update')")
    public CommonResult<Boolean> updateEmailBatch(@Valid @RequestBody MarketingEmailBatchSaveReqVO updateReqVO) {
        emailBatchService.updateEmailBatch(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除邮件批次")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:marketing:email-batch:delete')")
    public CommonResult<Boolean> deleteEmailBatch(@RequestParam("id") Long id) {
        emailBatchService.deleteEmailBatch(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得邮件批次")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:marketing:email-batch:query')")
    public CommonResult<MarketingEmailBatchRespVO> getEmailBatch(@RequestParam("id") Long id) {
        MarketingEmailBatchDO emailBatch = emailBatchService.getEmailBatch(id);
        return success(BeanUtils.toBean(emailBatch, MarketingEmailBatchRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询邮件批次")
    @PreAuthorize("@ss.hasPermission('crm:marketing:email-batch:query')")
    public CommonResult<PageResult<MarketingEmailBatchRespVO>> pageEmailBatch(@Valid MarketingEmailBatchPageReqVO pageReqVO) {
        PageResult<MarketingEmailBatchDO> pageResult = emailBatchService.getEmailBatchPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MarketingEmailBatchRespVO.class));
    }

}
