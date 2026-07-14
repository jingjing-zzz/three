package com.meession.etm.module.crm.service.statistics;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastDetailRespVO;
import com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastReqVO;
import com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastSummaryRespVO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessStatusDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.mysql.business.CrmBusinessMapper;
import com.meession.etm.module.crm.enums.business.CrmBusinessEndStatusEnum;
import com.meession.etm.module.crm.service.business.CrmBusinessStatusService;
import com.meession.etm.module.crm.service.customer.CrmCustomerService;
import com.meession.etm.module.system.api.user.AdminUserApi;
import com.meession.etm.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CRM 销售预测 Service 实现类
 *
 * 算法说明（阶段概率加权预测）：
 * 1. forecastAmount = sum(商机 totalPrice × 当前阶段 percent / 100)
 * 2. 排除已流失商机（endStatus = 2）
 * 3. 成交商机（endStatus = 1）单独计入成交金额
 */
@Service
public class CrmStatisticsForecastServiceImpl implements CrmStatisticsForecastService {

  private static final DateTimeFormatter DEAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Resource
  private CrmBusinessMapper businessMapper;
  @Resource
  private CrmBusinessStatusService businessStatusService;
  @Resource
  private CrmCustomerService customerService;
  @Resource
  private AdminUserApi adminUserApi;

  @Override
  public CrmStatisticsForecastSummaryRespVO getForecastSummary(CrmStatisticsForecastReqVO reqVO) {
    // 1. 查询非流失商机（endStatus IS NULL 或 endStatus != 2），按负责人、预计成交时间、状态组筛选
    List<CrmBusinessDO> list = businessMapper.selectList(buildQueryWrapper(reqVO));
    // 在 Java 层再次过滤流失商机，确保即使数据库查询未过滤也不会计入
    list = list.stream()
        .filter(b -> !Integer.valueOf(CrmBusinessEndStatusEnum.LOSE.getStatus()).equals(b.getEndStatus()))
        .collect(Collectors.toList());
    CrmStatisticsForecastSummaryRespVO resp = new CrmStatisticsForecastSummaryRespVO();
    if (CollUtil.isEmpty(list)) {
      resp.setBusinessCount(0);
      resp.setTotalAmount(BigDecimal.ZERO);
      resp.setForecastAmount(BigDecimal.ZERO);
      resp.setWonAmount(BigDecimal.ZERO);
      resp.setWonCount(0);
      resp.setAvgWinRate(BigDecimal.ZERO);
      return resp;
    }

    // 2. 批量获取商机状态（percent 来自 CrmBusinessStatusDO，不硬编码）
    Set<Long> statusIds = list.stream().map(CrmBusinessDO::getStatusId)
        .filter(Objects::nonNull).collect(Collectors.toSet());
    Map<Long, CrmBusinessStatusDO> statusMap = CollUtil.isEmpty(statusIds)
        ? Collections.emptyMap()
        : businessStatusService.getBusinessStatusMap(statusIds);

    // 3. 计算汇总
    BigDecimal totalAmount = BigDecimal.ZERO;
    BigDecimal forecastAmount = BigDecimal.ZERO;
    BigDecimal wonAmount = BigDecimal.ZERO;
    BigDecimal totalPercent = BigDecimal.ZERO;
    int wonCount = 0;
    int percentCount = 0;

    for (CrmBusinessDO business : list) {
      BigDecimal totalPrice = business.getTotalPrice() != null
          ? business.getTotalPrice()
          : BigDecimal.ZERO;
      totalAmount = totalAmount.add(totalPrice);

      // 加权预测金额 = totalPrice × percent / 100
      CrmBusinessStatusDO status = business.getStatusId() != null
          ? statusMap.get(business.getStatusId())
          : null;
      Integer percent = status != null ? status.getPercent() : null;
      if (percent != null) {
        BigDecimal weighted = totalPrice.multiply(BigDecimal.valueOf(percent))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        forecastAmount = forecastAmount.add(weighted);
        totalPercent = totalPercent.add(BigDecimal.valueOf(percent));
        percentCount++;
      }

      // 成交金额（endStatus = 1）
      if (Integer.valueOf(CrmBusinessEndStatusEnum.WIN.getStatus()).equals(business.getEndStatus())) {
        wonAmount = wonAmount.add(totalPrice);
        wonCount++;
      }
    }

    BigDecimal avgWinRate = percentCount > 0
        ? totalPercent.divide(BigDecimal.valueOf(percentCount), 2, RoundingMode.HALF_UP)
        : BigDecimal.ZERO;

    resp.setBusinessCount(list.size());
    resp.setTotalAmount(totalAmount);
    resp.setForecastAmount(forecastAmount);
    resp.setWonAmount(wonAmount);
    resp.setWonCount(wonCount);
    resp.setAvgWinRate(avgWinRate);
    return resp;
  }

  @Override
  public PageResult<CrmStatisticsForecastDetailRespVO> getForecastPage(CrmStatisticsForecastReqVO reqVO) {
    // 1. 分页查询非流失商机
    PageResult<CrmBusinessDO> pageResult = businessMapper.selectPage(reqVO, buildQueryWrapper(reqVO));
    // 在 Java 层再次过滤流失商机
    List<CrmBusinessDO> filteredList = pageResult.getList().stream()
        .filter(b -> !Integer.valueOf(CrmBusinessEndStatusEnum.LOSE.getStatus()).equals(b.getEndStatus()))
        .collect(Collectors.toList());
    pageResult = new PageResult<>(filteredList, (long) filteredList.size());
    if (CollUtil.isEmpty(pageResult.getList())) {
      return new PageResult<>(Collections.emptyList(), pageResult.getTotal());
    }

    List<CrmBusinessDO> list = pageResult.getList();

    // 2. 批量获取关联数据：阶段状态、客户、负责人
    Set<Long> statusIds = list.stream().map(CrmBusinessDO::getStatusId)
        .filter(Objects::nonNull).collect(Collectors.toSet());
    Map<Long, CrmBusinessStatusDO> statusMap = CollUtil.isEmpty(statusIds)
        ? Collections.emptyMap()
        : businessStatusService.getBusinessStatusMap(statusIds);

    Set<Long> customerIds = list.stream().map(CrmBusinessDO::getCustomerId)
        .filter(Objects::nonNull).collect(Collectors.toSet());
    Map<Long, CrmCustomerDO> customerMap = CollUtil.isEmpty(customerIds)
        ? Collections.emptyMap()
        : customerService.getCustomerMap(customerIds);

    Set<Long> userIds = list.stream().map(CrmBusinessDO::getOwnerUserId)
        .filter(Objects::nonNull).collect(Collectors.toSet());
    Map<Long, AdminUserRespDTO> userMap = CollUtil.isEmpty(userIds)
        ? Collections.emptyMap()
        : adminUserApi.getUserMap(userIds);

    // 3. 拼接明细
    List<CrmStatisticsForecastDetailRespVO> detailList = new ArrayList<>(list.size());
    for (CrmBusinessDO business : list) {
      CrmStatisticsForecastDetailRespVO detail = new CrmStatisticsForecastDetailRespVO();
      detail.setBusinessId(business.getId());
      detail.setBusinessName(business.getName());

      // 负责人名称
      AdminUserRespDTO user = business.getOwnerUserId() != null
          ? userMap.get(business.getOwnerUserId())
          : null;
      detail.setOwnerUserName(user != null ? user.getNickname() : null);

      // 客户名称
      CrmCustomerDO customer = business.getCustomerId() != null
          ? customerMap.get(business.getCustomerId())
          : null;
      detail.setCustomerName(customer != null ? customer.getName() : null);

      // 阶段名称和赢单率（percent 来自 CrmBusinessStatusDO）
      CrmBusinessStatusDO status = business.getStatusId() != null
          ? statusMap.get(business.getStatusId())
          : null;
      detail.setStatusName(status != null ? status.getName() : null);
      detail.setStagePercent(status != null ? status.getPercent() : null);

      // 金额
      BigDecimal totalPrice = business.getTotalPrice() != null
          ? business.getTotalPrice()
          : BigDecimal.ZERO;
      detail.setTotalPrice(totalPrice);
      Integer percent = status != null ? status.getPercent() : null;
      if (percent != null) {
        detail.setForecastAmount(totalPrice.multiply(BigDecimal.valueOf(percent))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
      } else {
        detail.setForecastAmount(BigDecimal.ZERO);
      }

      // 预计成交日期
      detail.setDealTime(business.getDealTime() != null
          ? business.getDealTime().format(DEAL_TIME_FORMATTER)
          : null);
      detail.setEndStatus(business.getEndStatus());

      detailList.add(detail);
    }
    return new PageResult<>(detailList, pageResult.getTotal());
  }

  /**
   * 构建查询条件：排除已流失商机（endStatus != 2），按负责人、预计成交时间、状态组筛选
   */
  private LambdaQueryWrapper<CrmBusinessDO> buildQueryWrapper(CrmStatisticsForecastReqVO reqVO) {
    return new LambdaQueryWrapperX<CrmBusinessDO>()
        .inIfPresent(CrmBusinessDO::getOwnerUserId, reqVO.getUserIds())
        .eqIfPresent(CrmBusinessDO::getStatusTypeId, reqVO.getStatusTypeId())
        .geIfPresent(CrmBusinessDO::getDealTime, reqVO.getDealTimeStart())
        .leIfPresent(CrmBusinessDO::getDealTime, reqVO.getDealTimeEnd())
        .and(w -> w.isNull(CrmBusinessDO::getEndStatus)
            .or().ne(CrmBusinessDO::getEndStatus,
                CrmBusinessEndStatusEnum.LOSE.getStatus()))
        .orderByDesc(CrmBusinessDO::getId);
  }

}
