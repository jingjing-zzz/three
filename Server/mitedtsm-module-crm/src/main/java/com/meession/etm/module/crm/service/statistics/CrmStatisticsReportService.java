package com.meession.etm.module.crm.service.statistics;

import com.meession.etm.module.crm.controller.admin.statistics.vo.report.CrmStatisticsReportOverviewRespVO;
import com.meession.etm.module.crm.controller.admin.statistics.vo.report.CrmStatisticsReportReqVO;

/**
 * CRM 商机报表 Service 接口
 */
public interface CrmStatisticsReportService {

    /**
     * 获取商机报表总览数据
     *
     * @param reqVO 查询参数（负责人、时间范围、是否含流失）
     */
    CrmStatisticsReportOverviewRespVO getReportOverview(CrmStatisticsReportReqVO reqVO);
}
