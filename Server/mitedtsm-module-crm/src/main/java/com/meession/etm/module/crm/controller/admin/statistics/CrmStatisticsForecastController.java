package com.meession.etm.module.crm.controller.admin.statistics;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.*;
import com.meession.etm.module.crm.service.statistics.CrmStatisticsForecastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - CRM 销售预测")
@RestController
@RequestMapping("/crm/statistics-forecast")
@Validated
public class CrmStatisticsForecastController {

  @Resource
  private CrmStatisticsForecastService forecastService;

  @GetMapping("/summary")
  @Operation(summary = "获取销售预测汇总")
  @PreAuthorize("@ss.hasPermission('crm:statistics-forecast:query')")
  public CommonResult<CrmStatisticsForecastSummaryRespVO> getForecastSummary(@Valid CrmStatisticsForecastReqVO reqVO) {
    return success(forecastService.getForecastSummary(reqVO));
  }

  @GetMapping("/page")
  @Operation(summary = "获取销售预测明细分页")
  @PreAuthorize("@ss.hasPermission('crm:statistics-forecast:query')")
  public CommonResult<PageResult<CrmStatisticsForecastDetailRespVO>> getForecastPage(
      @Valid CrmStatisticsForecastReqVO reqVO) {
    return success(forecastService.getForecastPage(reqVO));
  }

  @GetMapping("/get-forecast-by-date")
  @Operation(summary = "获取按日聚合的销售预测数据")
  @PreAuthorize("@ss.hasPermission('crm:statistics-forecast:query')")
  public CommonResult<List<CrmStatisticsForecastByDateRespVO>> getForecastByDate(
      @Valid CrmStatisticsForecastReqVO reqVO) {
    return success(forecastService.getForecastByDate(reqVO));
  }
}
