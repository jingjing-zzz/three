package com.meession.etm.module.crm.service.statistics;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.controller.admin.statistics.vo.forecast.CrmStatisticsForecastByDateRespVO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CRM 销售预测 Service 实现类
 *
 * 预测算法说明（数据驱动版）：
 * 1. 历史阶段转化率：统计所有已结束商机（赢单+输单），按 status_id 分组计算实际转化率
 *    stageConversionRate = 该阶段历史赢单数 / 该阶段历史结束总数
 * 2. 时间衰减因子：距离预计成交日期越近权重越高
 *    timeWeight = max(0.3, 1 - daysToDeal / 180)  半年后权重降至 0.3
 * 3. 已赢单商机：全额计入预测
 * 4. 进行中商机：forecastAmount = totalPrice × stageConversionRate × timeWeight
 * 5. 已流失商机：排除
 * 6. 同比增长率 = (预测金额 - 历史同期成交金额) / 历史同期成交金额 × 100%
 */
@Service
public class CrmStatisticsForecastServiceImpl implements CrmStatisticsForecastService {

    private static final DateTimeFormatter DEAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /** 时间衰减阈值：超过该天数后权重降到地板价 */
    private static final long TIME_DECAY_DAYS = 180L;
    private static final BigDecimal TIME_DECAY_FLOOR = new BigDecimal("0.3");

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
        CrmStatisticsForecastSummaryRespVO resp = new CrmStatisticsForecastSummaryRespVO();
        // 1. 查询当前活跃商机（排除已流失）
        List<CrmBusinessDO> activeList = queryActiveBusiness(reqVO);
        if (CollUtil.isEmpty(activeList)) {
            resp.setBusinessCount(0);
            resp.setTotalAmount(BigDecimal.ZERO);
            resp.setForecastAmount(BigDecimal.ZERO);
            resp.setWonAmount(BigDecimal.ZERO);
            resp.setWonCount(0);
            resp.setAvgWinRate(BigDecimal.ZERO);
            resp.setHistoryAmount(BigDecimal.ZERO);
            resp.setGrowthRate(BigDecimal.ZERO);
            resp.setAchievementRate(null);
            return resp;
        }

        // 2. 查询历史已结束商机，用于计算阶段转化率
        Map<Long, BigDecimal> stageConversionRateMap = calculateStageConversionRates(reqVO);

        // 3. 计算汇总
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal forecastAmount = BigDecimal.ZERO;
        BigDecimal wonAmount = BigDecimal.ZERO;
        BigDecimal totalPercent = BigDecimal.ZERO;
        int percentCount = 0;
        int wonCount = 0;

        // 批量获取阶段信息
        Map<Long, CrmBusinessStatusDO> statusMap = getStatusMap(activeList);
        LocalDateTime now = LocalDateTime.now();

        for (CrmBusinessDO business : activeList) {
            BigDecimal totalPrice = business.getTotalPrice() != null ? business.getTotalPrice() : BigDecimal.ZERO;
            totalAmount = totalAmount.add(totalPrice);

            Integer endStatus = business.getEndStatus();
            if (Integer.valueOf(CrmBusinessEndStatusEnum.WIN.getStatus()).equals(endStatus)) {
                // 已赢单：全额计入
                forecastAmount = forecastAmount.add(totalPrice);
                wonAmount = wonAmount.add(totalPrice);
                wonCount++;
            } else if (!Integer.valueOf(CrmBusinessEndStatusEnum.LOSE.getStatus()).equals(endStatus)) {
                // 进行中：阶段转化率 × 时间衰减
                Long statusId = business.getStatusId();
                BigDecimal stageRate = statusId != null
                        ? stageConversionRateMap.getOrDefault(statusId, new BigDecimal("0.5"))
                        : new BigDecimal("0.5");
                BigDecimal timeWeight = calculateTimeWeight(business.getDealTime(), now);
                BigDecimal weighted = totalPrice.multiply(stageRate).multiply(timeWeight)
                        .setScale(2, RoundingMode.HALF_UP);
                forecastAmount = forecastAmount.add(weighted);

                CrmBusinessStatusDO status = statusId != null ? statusMap.get(statusId) : null;
                if (status != null && status.getPercent() != null) {
                    totalPercent = totalPercent.add(BigDecimal.valueOf(status.getPercent()));
                    percentCount++;
                }
            }
        }

        BigDecimal avgWinRate = percentCount > 0
                ? totalPercent.divide(BigDecimal.valueOf(percentCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 4. 历史同期成交金额
        BigDecimal historyAmount = calculateHistoryAmount(reqVO);

        // 5. 同比增长率
        BigDecimal growthRate = BigDecimal.ZERO;
        if (historyAmount != null && historyAmount.compareTo(BigDecimal.ZERO) > 0) {
            growthRate = forecastAmount.subtract(historyAmount)
                    .divide(historyAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        resp.setBusinessCount(activeList.size());
        resp.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
        resp.setForecastAmount(forecastAmount.setScale(2, RoundingMode.HALF_UP));
        resp.setWonAmount(wonAmount.setScale(2, RoundingMode.HALF_UP));
        resp.setWonCount(wonCount);
        resp.setAvgWinRate(avgWinRate);
        resp.setHistoryAmount(historyAmount != null ? historyAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        resp.setGrowthRate(growthRate);
        resp.setAchievementRate(null);
        return resp;
    }

    @Override
    public PageResult<CrmStatisticsForecastDetailRespVO> getForecastPage(CrmStatisticsForecastReqVO reqVO) {
        // 1. 分页查询非流失商机
        PageResult<CrmBusinessDO> pageResult = businessMapper.selectPage(reqVO, buildQueryWrapper(reqVO));
        List<CrmBusinessDO> filteredList = pageResult.getList().stream()
                .filter(b -> !Integer.valueOf(CrmBusinessEndStatusEnum.LOSE.getStatus()).equals(b.getEndStatus()))
                .collect(Collectors.toList());
        pageResult = new PageResult<>(filteredList, (long) filteredList.size());
        if (CollUtil.isEmpty(pageResult.getList())) {
            return new PageResult<>(Collections.emptyList(), pageResult.getTotal());
        }

        List<CrmBusinessDO> list = pageResult.getList();

        // 2. 批量获取关联数据
        Map<Long, CrmBusinessStatusDO> statusMap = getStatusMap(list);
        Map<Long, CrmCustomerDO> customerMap = getCustomerMap(list);
        Map<Long, AdminUserRespDTO> userMap = getUserMap(list);

        // 3. 历史阶段转化率
        Map<Long, BigDecimal> stageConversionRateMap = calculateStageConversionRates(reqVO);
        LocalDateTime now = LocalDateTime.now();

        // 4. 拼接明细
        List<CrmStatisticsForecastDetailRespVO> detailList = new ArrayList<>(list.size());
        for (CrmBusinessDO business : list) {
            CrmStatisticsForecastDetailRespVO detail = new CrmStatisticsForecastDetailRespVO();
            detail.setBusinessId(business.getId());
            detail.setBusinessName(business.getName());

            AdminUserRespDTO user = business.getOwnerUserId() != null ? userMap.get(business.getOwnerUserId()) : null;
            detail.setOwnerUserName(user != null ? user.getNickname() : null);

            CrmCustomerDO customer = business.getCustomerId() != null ? customerMap.get(business.getCustomerId()) : null;
            detail.setCustomerName(customer != null ? customer.getName() : null);

            CrmBusinessStatusDO status = business.getStatusId() != null ? statusMap.get(business.getStatusId()) : null;
            detail.setStatusName(status != null ? status.getName() : null);
            detail.setStagePercent(status != null ? status.getPercent() : null);

            BigDecimal totalPrice = business.getTotalPrice() != null ? business.getTotalPrice() : BigDecimal.ZERO;
            detail.setTotalPrice(totalPrice);

            Integer endStatus = business.getEndStatus();
            if (Integer.valueOf(CrmBusinessEndStatusEnum.WIN.getStatus()).equals(endStatus)) {
                detail.setForecastAmount(totalPrice.setScale(2, RoundingMode.HALF_UP));
            } else if (!Integer.valueOf(CrmBusinessEndStatusEnum.LOSE.getStatus()).equals(endStatus)) {
                Long statusId = business.getStatusId();
                BigDecimal stageRate = statusId != null
                        ? stageConversionRateMap.getOrDefault(statusId, new BigDecimal("0.5"))
                        : new BigDecimal("0.5");
                BigDecimal timeWeight = calculateTimeWeight(business.getDealTime(), now);
                BigDecimal weighted = totalPrice.multiply(stageRate).multiply(timeWeight)
                        .setScale(2, RoundingMode.HALF_UP);
                detail.setForecastAmount(weighted);
            } else {
                detail.setForecastAmount(BigDecimal.ZERO);
            }

            detail.setDealTime(business.getDealTime() != null
                    ? business.getDealTime().format(DEAL_TIME_FORMATTER) : null);
            detail.setEndStatus(endStatus);
            detailList.add(detail);
        }
        return new PageResult<>(detailList, pageResult.getTotal());
    }

    @Override
    public List<CrmStatisticsForecastByDateRespVO> getForecastByDate(CrmStatisticsForecastReqVO reqVO) {
        // 1. 查询当前活跃商机
        List<CrmBusinessDO> activeList = queryActiveBusiness(reqVO);
        // 2. 历史阶段转化率
        Map<Long, BigDecimal> stageConversionRateMap = calculateStageConversionRates(reqVO);
        Map<Long, CrmBusinessStatusDO> statusMap = getStatusMap(activeList);
        LocalDateTime now = LocalDateTime.now();

        // 3. 按预计成交日期分组聚合
        Map<LocalDate, List<CrmBusinessDO>> groupedByDate = activeList.stream()
                .filter(b -> b.getDealTime() != null)
                .collect(Collectors.groupingBy(b -> b.getDealTime().toLocalDate()));

        // 4. 历史同期各日成交金额（去年同期 N 天）
        Map<LocalDate, BigDecimal> historyDailyMap = calculateHistoryDailyMap(reqVO);

        // 5. 生成日期序列
        List<LocalDate> dateList = buildDateRange(reqVO);
        List<CrmStatisticsForecastByDateRespVO> result = new ArrayList<>(dateList.size());
        for (LocalDate date : dateList) {
            String dateStr = date.format(DATE_FORMATTER);
            String startStr = date.atStartOfDay().format(DEAL_TIME_FORMATTER);
            String endStr = date.atTime(23, 59, 59).format(DEAL_TIME_FORMATTER);

            List<CrmBusinessDO> dayList = groupedByDate.getOrDefault(date, Collections.emptyList());
            BigDecimal businessAmount = BigDecimal.ZERO;
            BigDecimal forecastAmount = BigDecimal.ZERO;
            for (CrmBusinessDO business : dayList) {
                BigDecimal totalPrice = business.getTotalPrice() != null ? business.getTotalPrice() : BigDecimal.ZERO;
                businessAmount = businessAmount.add(totalPrice);

                Integer endStatus = business.getEndStatus();
                if (Integer.valueOf(CrmBusinessEndStatusEnum.WIN.getStatus()).equals(endStatus)) {
                    forecastAmount = forecastAmount.add(totalPrice);
                } else if (!Integer.valueOf(CrmBusinessEndStatusEnum.LOSE.getStatus()).equals(endStatus)) {
                    Long statusId = business.getStatusId();
                    BigDecimal stageRate = statusId != null
                            ? stageConversionRateMap.getOrDefault(statusId, new BigDecimal("0.5"))
                            : new BigDecimal("0.5");
                    BigDecimal timeWeight = calculateTimeWeight(business.getDealTime(), now);
                    forecastAmount = forecastAmount.add(totalPrice.multiply(stageRate).multiply(timeWeight));
                }
            }

            BigDecimal historyAmount = historyDailyMap.getOrDefault(date, BigDecimal.ZERO);

            CrmStatisticsForecastByDateRespVO vo = new CrmStatisticsForecastByDateRespVO();
            vo.setTime(dateStr);
            vo.setStartTime(startStr);
            vo.setEndTime(endStr);
            vo.setBusinessAmount(businessAmount.setScale(2, RoundingMode.HALF_UP));
            vo.setForecastAmount(forecastAmount.setScale(2, RoundingMode.HALF_UP));
            vo.setHistoryAmount(historyAmount.setScale(2, RoundingMode.HALF_UP));
            result.add(vo);
        }
        return result;
    }

    // ==================== 私有辅助方法 ====================

    private List<CrmBusinessDO> queryActiveBusiness(CrmStatisticsForecastReqVO reqVO) {
        List<CrmBusinessDO> list = businessMapper.selectList(buildQueryWrapper(reqVO));
        return list.stream()
                .filter(b -> !Integer.valueOf(CrmBusinessEndStatusEnum.LOSE.getStatus()).equals(b.getEndStatus()))
                .collect(Collectors.toList());
    }

    private Map<Long, BigDecimal> calculateStageConversionRates(CrmStatisticsForecastReqVO reqVO) {
        // 查询所有历史已结束商机（不限时间，保证样本量）
        LambdaQueryWrapper<CrmBusinessDO> wrapper = new LambdaQueryWrapperX<CrmBusinessDO>()
                .inIfPresent(CrmBusinessDO::getOwnerUserId, reqVO.getUserIds())
                .in(CrmBusinessDO::getEndStatus,
                        CrmBusinessEndStatusEnum.WIN.getStatus(),
                        CrmBusinessEndStatusEnum.LOSE.getStatus());
        List<CrmBusinessDO> closedList = businessMapper.selectList(wrapper);
        if (CollUtil.isEmpty(closedList)) {
            return Collections.emptyMap();
        }
        Map<Long, List<CrmBusinessDO>> grouped = closedList.stream()
                .filter(b -> b.getStatusId() != null)
                .collect(Collectors.groupingBy(CrmBusinessDO::getStatusId));
        Map<Long, BigDecimal> result = new HashMap<>();
        for (Map.Entry<Long, List<CrmBusinessDO>> entry : grouped.entrySet()) {
            List<CrmBusinessDO> stageList = entry.getValue();
            long won = stageList.stream()
                    .filter(b -> Integer.valueOf(CrmBusinessEndStatusEnum.WIN.getStatus()).equals(b.getEndStatus()))
                    .count();
            if (!stageList.isEmpty()) {
                result.put(entry.getKey(),
                        BigDecimal.valueOf(won).divide(BigDecimal.valueOf(stageList.size()), 4, RoundingMode.HALF_UP));
            }
        }
        return result;
    }

    private BigDecimal calculateTimeWeight(LocalDateTime dealTime, LocalDateTime now) {
        if (dealTime == null) {
            return BigDecimal.ONE;
        }
        long days = ChronoUnit.DAYS.between(now.toLocalDate(), dealTime.toLocalDate());
        if (days <= 0) {
            // 已过期但未结束的商机，权重降至地板值
            return TIME_DECAY_FLOOR;
        }
        if (days >= TIME_DECAY_DAYS) {
            return TIME_DECAY_FLOOR;
        }
        // 线性衰减：1 - (days / 180) × (1 - 0.3)
        BigDecimal decay = BigDecimal.valueOf(1)
                .subtract(BigDecimal.valueOf(days)
                        .divide(BigDecimal.valueOf(TIME_DECAY_DAYS), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.ONE.subtract(TIME_DECAY_FLOOR)));
        return decay.min(BigDecimal.ONE).max(TIME_DECAY_FLOOR);
    }

    private BigDecimal calculateHistoryAmount(CrmStatisticsForecastReqVO reqVO) {
        if (reqVO.getDealTimeStart() == null || reqVO.getDealTimeEnd() == null) {
            return BigDecimal.ZERO;
        }
        // 去年同期：开始和结束各往前推一年
        LocalDateTime histStart = reqVO.getDealTimeStart().minusYears(1);
        LocalDateTime histEnd = reqVO.getDealTimeEnd().minusYears(1);
        LambdaQueryWrapper<CrmBusinessDO> wrapper = new LambdaQueryWrapperX<CrmBusinessDO>()
                .inIfPresent(CrmBusinessDO::getOwnerUserId, reqVO.getUserIds())
                .eq(CrmBusinessDO::getEndStatus, CrmBusinessEndStatusEnum.WIN.getStatus())
                .ge(CrmBusinessDO::getDealTime, histStart)
                .le(CrmBusinessDO::getDealTime, histEnd);
        List<CrmBusinessDO> list = businessMapper.selectList(wrapper);
        if (CollUtil.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.stream()
                .map(b -> b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<LocalDate, BigDecimal> calculateHistoryDailyMap(CrmStatisticsForecastReqVO reqVO) {
        if (reqVO.getDealTimeStart() == null || reqVO.getDealTimeEnd() == null) {
            return Collections.emptyMap();
        }
        LocalDateTime histStart = reqVO.getDealTimeStart().minusYears(1);
        LocalDateTime histEnd = reqVO.getDealTimeEnd().minusYears(1);
        LambdaQueryWrapper<CrmBusinessDO> wrapper = new LambdaQueryWrapperX<CrmBusinessDO>()
                .inIfPresent(CrmBusinessDO::getOwnerUserId, reqVO.getUserIds())
                .eq(CrmBusinessDO::getEndStatus, CrmBusinessEndStatusEnum.WIN.getStatus())
                .ge(CrmBusinessDO::getDealTime, histStart)
                .le(CrmBusinessDO::getDealTime, histEnd);
        List<CrmBusinessDO> list = businessMapper.selectList(wrapper);
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }
        // 历史日期映射回当前年度，方便对比
        return list.stream()
                .filter(b -> b.getDealTime() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getDealTime().toLocalDate().plusYears(1),
                        Collectors.mapping(
                                b -> b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
    }

    private List<LocalDate> buildDateRange(CrmStatisticsForecastReqVO reqVO) {
        if (reqVO.getDealTimeStart() == null || reqVO.getDealTimeEnd() == null) {
            // 默认近30天到未来90天
            LocalDate end = LocalDate.now().plusDays(90);
            return buildRange(LocalDate.now().minusDays(30), end);
        }
        return buildRange(reqVO.getDealTimeStart().toLocalDate(), reqVO.getDealTimeEnd().toLocalDate());
    }

    private List<LocalDate> buildRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            return Collections.singletonList(start);
        }
        List<LocalDate> dates = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            dates.add(cursor);
            cursor = cursor.plusDays(1);
        }
        return dates;
    }

    private Map<Long, CrmBusinessStatusDO> getStatusMap(List<CrmBusinessDO> list) {
        Set<Long> statusIds = list.stream().map(CrmBusinessDO::getStatusId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        return CollUtil.isEmpty(statusIds)
                ? Collections.emptyMap()
                : businessStatusService.getBusinessStatusMap(statusIds);
    }

    private Map<Long, CrmCustomerDO> getCustomerMap(List<CrmBusinessDO> list) {
        Set<Long> customerIds = list.stream().map(CrmBusinessDO::getCustomerId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        return CollUtil.isEmpty(customerIds)
                ? Collections.emptyMap()
                : customerService.getCustomerMap(customerIds);
    }

    private Map<Long, AdminUserRespDTO> getUserMap(List<CrmBusinessDO> list) {
        Set<Long> userIds = list.stream().map(CrmBusinessDO::getOwnerUserId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        return CollUtil.isEmpty(userIds)
                ? Collections.emptyMap()
                : adminUserApi.getUserMap(userIds);
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
