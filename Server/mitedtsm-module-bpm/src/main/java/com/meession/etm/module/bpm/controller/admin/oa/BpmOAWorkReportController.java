package com.meession.etm.module.bpm.controller.admin.oa;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkReportCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkReportPageReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkReportRespVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOAWorkReportDO;
import com.meession.etm.module.bpm.service.oa.BpmOAWorkReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - OA 工作报告")
@RestController
@RequestMapping("/bpm/oa/work-report")
@Validated
public class BpmOAWorkReportController {

    @Resource
    private BpmOAWorkReportService workReportService;

    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('bpm:oa-work-report:create')")
    @Operation(summary = "创建工作报告")
    public CommonResult<Long> createWorkReport(@Valid @RequestBody BpmOAWorkReportCreateReqVO createReqVO) {
        return success(workReportService.createWorkReport(getLoginUserId(), createReqVO));
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('bpm:oa-work-report:query')")
    @Operation(summary = "获得工作报告")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<BpmOAWorkReportRespVO> getWorkReport(@RequestParam("id") Long id) {
        BpmOAWorkReportDO workReport = workReportService.getWorkReport(id);
        return success(BeanUtils.toBean(workReport, BpmOAWorkReportRespVO.class));
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('bpm:oa-work-report:query')")
    @Operation(summary = "获得工作报告分页")
    public CommonResult<PageResult<BpmOAWorkReportRespVO>> getWorkReportPage(@Valid BpmOAWorkReportPageReqVO pageVO) {
        PageResult<BpmOAWorkReportDO> pageResult = workReportService.getWorkReportPage(getLoginUserId(), pageVO);
        return success(BeanUtils.toBean(pageResult, BpmOAWorkReportRespVO.class));
    }

}