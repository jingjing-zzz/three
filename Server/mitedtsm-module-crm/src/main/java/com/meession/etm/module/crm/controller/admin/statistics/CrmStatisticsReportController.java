package com.meession.etm.module.crm.controller.admin.statistics;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.module.crm.controller.admin.statistics.vo.report.CrmStatisticsReportOverviewRespVO;
import com.meession.etm.module.crm.controller.admin.statistics.vo.report.CrmStatisticsReportReqVO;
import com.meession.etm.module.crm.service.statistics.CrmStatisticsReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

/**
 * CRM 商机报表 Controller
 */
@Tag(name = "管理后台 - CRM 商机报表")
@RestController
@RequestMapping("/crm/statistics-report")
@Validated
public class CrmStatisticsReportController {

    @Resource
    private CrmStatisticsReportService reportService;

    @GetMapping("/overview")
    @Operation(summary = "获取商机报表总览数据（支持按负责人、预计成交时间筛选，默认排除已流失）")
    @PreAuthorize("@ss.hasPermission('crm:statistics-forecast:query')")
    public CommonResult<CrmStatisticsReportOverviewRespVO> getReportOverview(@Validated CrmStatisticsReportReqVO reqVO) {
        return success(reportService.getReportOverview(reqVO));
    }
}
