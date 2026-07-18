package com.meession.etm.module.crm.service.statistics;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.controller.admin.statistics.vo.report.CrmStatisticsReportOverviewRespVO;
import com.meession.etm.module.crm.controller.admin.statistics.vo.report.CrmStatisticsReportReqVO;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CRM 商机报表 Service 实现类
 *
 * 数据口径说明：
 * 1. 默认排除已流失商机（endStatus != 2），与销售预测口径一致
 * 2. 可通过 includeLost=true 包含已流失商机
 * 3. 预测金额 = sum(商机 totalPrice × 当前阶段 percent / 100)，已赢单全额计入
 * 4. 状态分布：进行中、已赢单、已输单（仅 includeLost=true 时显示）
 * 5. 阶段分布：按 status_id 分组（带阶段名称和概率）
 * 6. 来源分布：按 source 字段分组（使用字典 crm_business_source 翻译）
 * 7. 负责人业绩：按 owner_user_id 分组
 * 8. 客户分布：按 customer_id 分组，取 Top 10
 */
@Service
public class CrmStatisticsReportServiceImpl implements CrmStatisticsReportService {

    @Resource
    private CrmBusinessMapper businessMapper;
    @Resource
    private CrmBusinessStatusService businessStatusService;
    @Resource
    private CrmCustomerService customerService;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public CrmStatisticsReportOverviewRespVO getReportOverview(CrmStatisticsReportReqVO reqVO) {
        CrmStatisticsReportOverviewRespVO resp = new CrmStatisticsReportOverviewRespVO();
        boolean includeLost = reqVO != null && Boolean.TRUE.equals(reqVO.getIncludeLost());

        // 1. 查询符合条件的商机
        LambdaQueryWrapper<CrmBusinessDO> wrapper = buildQueryWrapper(reqVO, includeLost);
        List<CrmBusinessDO> list = businessMapper.selectList(wrapper);
        if (CollUtil.isEmpty(list)) {
            resp.setStatusDistribution(Collections.emptyList());
            resp.setStageDistribution(Collections.emptyList());
            resp.setSourceDistribution(Collections.emptyList());
            resp.setOwnerPerformance(Collections.emptyList());
            resp.setCustomerDistribution(Collections.emptyList());
            resp.setTotalBusinessCount(0);
            resp.setTotalAmount(BigDecimal.ZERO);
            resp.setWonAmount(BigDecimal.ZERO);
            resp.setForecastAmount(BigDecimal.ZERO);
            return resp;
        }

        // 2. 批量获取关联数据
        Map<Long, CrmBusinessStatusDO> statusMap = getStatusMap(list);
        Map<Long, CrmCustomerDO> customerMap = getCustomerMap(list);
        Map<Long, AdminUserRespDTO> userMap = getUserMap(list);

        // 3. 状态分布
        resp.setStatusDistribution(buildStatusDistribution(list, includeLost));

        // 4. 阶段分布
        resp.setStageDistribution(buildStageDistribution(list, statusMap));

        // 5. 来源分布
        resp.setSourceDistribution(buildSourceDistribution(list));

        // 6. 负责人业绩
        resp.setOwnerPerformance(buildOwnerPerformance(list, userMap, statusMap));

        // 7. 客户分布 Top 10
        resp.setCustomerDistribution(buildCustomerDistribution(list, customerMap));

        // 8. 汇总
        resp.setTotalBusinessCount(list.size());
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal wonAmount = BigDecimal.ZERO;
        BigDecimal forecastAmount = BigDecimal.ZERO;
        for (CrmBusinessDO business : list) {
            BigDecimal totalPrice = business.getTotalPrice() != null
                    ? business.getTotalPrice() : BigDecimal.ZERO;
            totalAmount = totalAmount.add(totalPrice);

            Integer endStatus = business.getEndStatus();
            if (Integer.valueOf(CrmBusinessEndStatusEnum.WIN.getStatus()).equals(endStatus)) {
                wonAmount = wonAmount.add(totalPrice);
                forecastAmount = forecastAmount.add(totalPrice);
            } else if (!Integer.valueOf(CrmBusinessEndStatusEnum.LOSE.getStatus()).equals(endStatus)) {
                // 进行中：阶段概率加权
                Long statusId = business.getStatusId();
                CrmBusinessStatusDO status = statusId != null ? statusMap.get(statusId) : null;
                BigDecimal percent = status != null && status.getPercent() != null
                        ? BigDecimal.valueOf(status.getPercent()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
                forecastAmount = forecastAmount.add(totalPrice.multiply(percent));
            }
        }
        resp.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
        resp.setWonAmount(wonAmount.setScale(2, RoundingMode.HALF_UP));
        resp.setForecastAmount(forecastAmount.setScale(2, RoundingMode.HALF_UP));
        return resp;
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<CrmBusinessDO> buildQueryWrapper(CrmStatisticsReportReqVO reqVO, boolean includeLost) {
        LambdaQueryWrapperX<CrmBusinessDO> wrapper = new LambdaQueryWrapperX<CrmBusinessDO>()
                .orderByDesc(CrmBusinessDO::getId);
        if (reqVO == null) {
            // 默认排除已流失
            return wrapper.and(w -> w.isNull(CrmBusinessDO::getEndStatus)
                    .or().ne(CrmBusinessDO::getEndStatus, CrmBusinessEndStatusEnum.LOSE.getStatus()));
        }
        // 负责人筛选
        if (reqVO.getUserId() != null) {
            wrapper.eq(CrmBusinessDO::getOwnerUserId, reqVO.getUserId());
        }
        // 预计成交时间范围筛选
        if (reqVO.getTimes() != null && reqVO.getTimes().length == 2) {
            LocalDateTime startTime = reqVO.getTimes()[0];
            LocalDateTime endTime = reqVO.getTimes()[1];
            if (startTime != null && endTime != null) {
                wrapper.ge(CrmBusinessDO::getDealTime, startTime)
                        .le(CrmBusinessDO::getDealTime, endTime);
            }
        }
        // 是否包含已流失
        if (!includeLost) {
            wrapper.and(w -> w.isNull(CrmBusinessDO::getEndStatus)
                    .or().ne(CrmBusinessDO::getEndStatus, CrmBusinessEndStatusEnum.LOSE.getStatus()));
        }
        return wrapper;
    }

    /**
     * 商机状态分布：进行中、已赢单、已输单
     */
    private List<CrmStatisticsReportOverviewRespVO.Item> buildStatusDistribution(List<CrmBusinessDO> list, boolean includeLost) {
        Map<String, List<CrmBusinessDO>> grouped = new LinkedHashMap<>();
        // 保证顺序：进行中、已赢单、已输单（仅 includeLost=true 时显示）
        grouped.put("进行中", new ArrayList<>());
        grouped.put("已赢单", new ArrayList<>());
        if (includeLost) {
            grouped.put("已输单", new ArrayList<>());
        }
        for (CrmBusinessDO business : list) {
            Integer endStatus = business.getEndStatus();
            if (Integer.valueOf(CrmBusinessEndStatusEnum.WIN.getStatus()).equals(endStatus)) {
                grouped.get("已赢单").add(business);
            } else if (Integer.valueOf(CrmBusinessEndStatusEnum.LOSE.getStatus()).equals(endStatus)) {
                if (includeLost) {
                    grouped.get("已输单").add(business);
                }
            } else {
                grouped.get("进行中").add(business);
            }
        }
        List<CrmStatisticsReportOverviewRespVO.Item> result = new ArrayList<>(grouped.size());
        for (Map.Entry<String, List<CrmBusinessDO>> entry : grouped.entrySet()) {
            CrmStatisticsReportOverviewRespVO.Item item = new CrmStatisticsReportOverviewRespVO.Item();
            item.setName(entry.getKey());
            item.setValue(entry.getValue().size());
            BigDecimal amount = entry.getValue().stream()
                    .map(b -> b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            item.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
            result.add(item);
        }
        return result;
    }

    /**
     * 商机阶段分布：按 status_id 分组
     */
    private List<CrmStatisticsReportOverviewRespVO.StageItem> buildStageDistribution(
            List<CrmBusinessDO> list, Map<Long, CrmBusinessStatusDO> statusMap) {
        Map<Long, List<CrmBusinessDO>> grouped = new LinkedHashMap<>();
        for (CrmBusinessDO business : list) {
            Long statusId = business.getStatusId();
            if (statusId == null) {
                continue;
            }
            grouped.computeIfAbsent(statusId, k -> new ArrayList<>()).add(business);
        }
        List<CrmStatisticsReportOverviewRespVO.StageItem> result = new ArrayList<>(grouped.size());
        for (Map.Entry<Long, List<CrmBusinessDO>> entry : grouped.entrySet()) {
            CrmBusinessStatusDO status = statusMap.get(entry.getKey());
            if (status == null) {
                continue;
            }
            CrmStatisticsReportOverviewRespVO.StageItem item = new CrmStatisticsReportOverviewRespVO.StageItem();
            item.setName(status.getName());
            item.setPercent(BigDecimal.valueOf(status.getPercent() != null ? status.getPercent() : 0));
            item.setValue(entry.getValue().size());
            BigDecimal amount = entry.getValue().stream()
                    .map(b -> b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            item.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
            result.add(item);
        }
        // 按 sort 排序
        Map<String, Integer> nameToSort = statusMap.values().stream()
                .collect(Collectors.toMap(CrmBusinessStatusDO::getName,
                        s -> s.getSort() != null ? s.getSort() : Integer.MAX_VALUE,
                        (a, b) -> a));
        result.sort(Comparator.comparingInt(a -> nameToSort.getOrDefault(a.getName(), Integer.MAX_VALUE)));
        return result;
    }

    /**
     * 商机来源分布：按 source 字段分组
     */
    private List<CrmStatisticsReportOverviewRespVO.Item> buildSourceDistribution(List<CrmBusinessDO> list) {
        Map<String, List<CrmBusinessDO>> grouped = new LinkedHashMap<>();
        for (CrmBusinessDO business : list) {
            String source = business.getSource();
            String key = (source == null || source.trim().isEmpty()) ? "未填写" : source;
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(business);
        }
        List<CrmStatisticsReportOverviewRespVO.Item> result = new ArrayList<>(grouped.size());
        for (Map.Entry<String, List<CrmBusinessDO>> entry : grouped.entrySet()) {
            CrmStatisticsReportOverviewRespVO.Item item = new CrmStatisticsReportOverviewRespVO.Item();
            item.setName(entry.getKey());
            item.setValue(entry.getValue().size());
            BigDecimal amount = entry.getValue().stream()
                    .map(b -> b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            item.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
            result.add(item);
        }
        result.sort(Comparator.comparingInt(CrmStatisticsReportOverviewRespVO.Item::getValue).reversed());
        return result;
    }

    /**
     * 负责人业绩：按 owner_user_id 分组
     */
    private List<CrmStatisticsReportOverviewRespVO.OwnerItem> buildOwnerPerformance(
            List<CrmBusinessDO> list, Map<Long, AdminUserRespDTO> userMap,
            Map<Long, CrmBusinessStatusDO> statusMap) {
        Map<Long, List<CrmBusinessDO>> grouped = new LinkedHashMap<>();
        for (CrmBusinessDO business : list) {
            Long ownerId = business.getOwnerUserId();
            if (ownerId == null) {
                continue;
            }
            grouped.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(business);
        }
        List<CrmStatisticsReportOverviewRespVO.OwnerItem> result = new ArrayList<>(grouped.size());
        for (Map.Entry<Long, List<CrmBusinessDO>> entry : grouped.entrySet()) {
            Long ownerId = entry.getKey();
            AdminUserRespDTO user = userMap.get(ownerId);
            List<CrmBusinessDO> businessList = entry.getValue();

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal wonAmount = BigDecimal.ZERO;
            BigDecimal forecastAmount = BigDecimal.ZERO;
            for (CrmBusinessDO business : businessList) {
                BigDecimal totalPrice = business.getTotalPrice() != null
                        ? business.getTotalPrice() : BigDecimal.ZERO;
                totalAmount = totalAmount.add(totalPrice);

                Integer endStatus = business.getEndStatus();
                if (Integer.valueOf(CrmBusinessEndStatusEnum.WIN.getStatus()).equals(endStatus)) {
                    wonAmount = wonAmount.add(totalPrice);
                    forecastAmount = forecastAmount.add(totalPrice);
                } else if (!Integer.valueOf(CrmBusinessEndStatusEnum.LOSE.getStatus()).equals(endStatus)) {
                    Long statusId = business.getStatusId();
                    CrmBusinessStatusDO status = statusId != null ? statusMap.get(statusId) : null;
                    BigDecimal percent = status != null && status.getPercent() != null
                            ? BigDecimal.valueOf(status.getPercent()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    forecastAmount = forecastAmount.add(totalPrice.multiply(percent));
                }
            }
            CrmStatisticsReportOverviewRespVO.OwnerItem item = new CrmStatisticsReportOverviewRespVO.OwnerItem();
            item.setUserId(ownerId);
            item.setUserName(user != null ? user.getNickname() : ("用户" + ownerId));
            item.setBusinessCount(businessList.size());
            item.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
            item.setWonAmount(wonAmount.setScale(2, RoundingMode.HALF_UP));
            item.setForecastAmount(forecastAmount.setScale(2, RoundingMode.HALF_UP));
            result.add(item);
        }
        result.sort(Comparator.comparing(CrmStatisticsReportOverviewRespVO.OwnerItem::getTotalAmount).reversed());
        return result;
    }

    /**
     * 客户分布 Top 10：按 customer_id 分组，按商机总金额降序
     */
    private List<CrmStatisticsReportOverviewRespVO.CustomerItem> buildCustomerDistribution(
            List<CrmBusinessDO> list, Map<Long, CrmCustomerDO> customerMap) {
        Map<Long, List<CrmBusinessDO>> grouped = new LinkedHashMap<>();
        for (CrmBusinessDO business : list) {
            Long customerId = business.getCustomerId();
            if (customerId == null) {
                continue;
            }
            grouped.computeIfAbsent(customerId, k -> new ArrayList<>()).add(business);
        }
        List<CrmStatisticsReportOverviewRespVO.CustomerItem> result = new ArrayList<>(grouped.size());
        for (Map.Entry<Long, List<CrmBusinessDO>> entry : grouped.entrySet()) {
            Long customerId = entry.getKey();
            CrmCustomerDO customer = customerMap.get(customerId);
            List<CrmBusinessDO> businessList = entry.getValue();
            BigDecimal totalAmount = businessList.stream()
                    .map(b -> b.getTotalPrice() != null ? b.getTotalPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            CrmStatisticsReportOverviewRespVO.CustomerItem item = new CrmStatisticsReportOverviewRespVO.CustomerItem();
            item.setCustomerId(customerId);
            item.setCustomerName(customer != null ? customer.getName() : ("客户" + customerId));
            item.setBusinessCount(businessList.size());
            item.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));
            result.add(item);
        }
        result.sort(Comparator.comparing(CrmStatisticsReportOverviewRespVO.CustomerItem::getTotalAmount).reversed());
        if (result.size() > 10) {
            return new ArrayList<>(result.subList(0, 10));
        }
        return result;
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
}
