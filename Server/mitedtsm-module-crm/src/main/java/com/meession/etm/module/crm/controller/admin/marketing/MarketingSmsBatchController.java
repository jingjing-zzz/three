package com.meession.etm.module.crm.controller.admin.marketing;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.sms.MarketingSmsBatchPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.sms.MarketingSmsBatchRespVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.sms.MarketingSmsBatchSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSmsBatchDO;
import com.meession.etm.module.crm.service.marketing.MarketingSmsBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 营销短信批次")
@RestController
@RequestMapping("/crm/marketing/sms-batch")
@Validated
public class MarketingSmsBatchController {

    @Resource
    private MarketingSmsBatchService smsBatchService;

    @PostMapping("/create")
    @Operation(summary = "创建短信批次")
    @PreAuthorize("@ss.hasPermission('crm:marketing:sms-batch:create')")
    public CommonResult<Long> createSmsBatch(@Valid @RequestBody MarketingSmsBatchSaveReqVO createReqVO) {
        return success(smsBatchService.createSmsBatch(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新短信批次")
    @PreAuthorize("@ss.hasPermission('crm:marketing:sms-batch:update')")
    public CommonResult<Boolean> updateSmsBatch(@Valid @RequestBody MarketingSmsBatchSaveReqVO updateReqVO) {
        smsBatchService.updateSmsBatch(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除短信批次")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:marketing:sms-batch:delete')")
    public CommonResult<Boolean> deleteSmsBatch(@RequestParam("id") Long id) {
        smsBatchService.deleteSmsBatch(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得短信批次")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:marketing:sms-batch:query')")
    public CommonResult<MarketingSmsBatchRespVO> getSmsBatch(@RequestParam("id") Long id) {
        MarketingSmsBatchDO smsBatch = smsBatchService.getSmsBatch(id);
        return success(BeanUtils.toBean(smsBatch, MarketingSmsBatchRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询短信批次")
    @PreAuthorize("@ss.hasPermission('crm:marketing:sms-batch:query')")
    public CommonResult<PageResult<MarketingSmsBatchRespVO>> pageSmsBatch(@Valid MarketingSmsBatchPageReqVO pageReqVO) {
        PageResult<MarketingSmsBatchDO> pageResult = smsBatchService.getSmsBatchPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MarketingSmsBatchRespVO.class));
    }

}
