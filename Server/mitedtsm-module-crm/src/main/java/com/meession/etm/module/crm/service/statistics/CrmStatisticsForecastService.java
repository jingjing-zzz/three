package com.meession.etm.module.crm.service.statistics;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastByDateRespVO;
import com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastReqVO;
import com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastSummaryRespVO;
import com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastDetailRespVO;

import java.util.List;

public interface CrmStatisticsForecastService {
  /** 获取预测汇总 */
  CrmStatisticsForecastSummaryRespVO getForecastSummary(CrmStatisticsForecastReqVO reqVO);

  /** 获取预测明细分页 */
  PageResult<CrmStatisticsForecastDetailRespVO> getForecastPage(CrmStatisticsForecastReqVO reqVO);

  /** 获取按日聚合的预测数据，用于图表展示 */
  List<CrmStatisticsForecastByDateRespVO> getForecastByDate(CrmStatisticsForecastReqVO reqVO);
}
