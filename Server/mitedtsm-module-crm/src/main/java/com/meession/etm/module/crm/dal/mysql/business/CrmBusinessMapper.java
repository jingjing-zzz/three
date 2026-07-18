package com.meession.etm.module.crm.dal.mysql.business;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.meession.etm.module.crm.controller.admin.business.vo.business.CrmBusinessPageReqVO;
import com.meession.etm.module.crm.controller.admin.statistics.vo.funnel.CrmStatisticsFunnelReqVO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO;
import com.meession.etm.module.crm.enums.common.CrmBizTypeEnum;
import com.meession.etm.module.crm.util.CrmPermissionUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CrmBusinessMapper extends BaseMapperX<CrmBusinessDO> {

    default int updateOwnerUserIdById(Long id, Long ownerUserId) {
        return update(new LambdaUpdateWrapper<CrmBusinessDO>()
                .eq(CrmBusinessDO::getId, id)
                .set(CrmBusinessDO::getOwnerUserId, ownerUserId));
    }

    default PageResult<CrmBusinessDO> selectPageByCustomerId(CrmBusinessPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<CrmBusinessDO>()
                .eq(CrmBusinessDO::getCustomerId, pageReqVO.getCustomerId())
                .likeIfPresent(CrmBusinessDO::getName, pageReqVO.getName())
                .orderByDesc(CrmBusinessDO::getId));
    }

    default PageResult<CrmBusinessDO> selectPageByContactId(CrmBusinessPageReqVO pageReqVO, Collection<Long> businessIds) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<CrmBusinessDO>()
                .in(CrmBusinessDO::getId, businessIds)
                .likeIfPresent(CrmBusinessDO::getName, pageReqVO.getName())
                .orderByDesc(CrmBusinessDO::getId));
    }

    default PageResult<CrmBusinessDO> selectPage(CrmBusinessPageReqVO pageReqVO, Long userId) {
        MPJLambdaWrapperX<CrmBusinessDO> query = new MPJLambdaWrapperX<>();
        CrmPermissionUtils.appendPermissionCondition(query, CrmBizTypeEnum.CRM_BUSINESS.getType(),
                CrmBusinessDO::getId, userId, pageReqVO.getSceneType());
        query.selectAll(CrmBusinessDO.class)
                .likeIfPresent(CrmBusinessDO::getName, pageReqVO.getName())
                .orderByDesc(CrmBusinessDO::getId);
        return selectJoinPage(pageReqVO, CrmBusinessDO.class, query);
    }

    default Long selectCountByStatusTypeId(Long statusTypeId) {
        return selectCount(CrmBusinessDO::getStatusTypeId, statusTypeId);
    }

    default List<CrmBusinessDO> selectListByCustomerIdOwnerUserId(Long customerId, Long ownerUserId) {
        return selectList(new LambdaQueryWrapperX<CrmBusinessDO>()
                .eq(CrmBusinessDO::getCustomerId, customerId)
                .eq(CrmBusinessDO::getOwnerUserId, ownerUserId));
    }

    default PageResult<CrmBusinessDO> selectPage(CrmStatisticsFunnelReqVO pageVO) {
        return selectPage(pageVO, new LambdaQueryWrapperX<CrmBusinessDO>()
                .in(CrmBusinessDO::getOwnerUserId, pageVO.getUserIds())
                .betweenIfPresent(CrmBusinessDO::getCreateTime, pageVO.getTimes())
                .eqIfPresent(CrmBusinessDO::getCreateTime, pageVO.getClickDate()));
    }

}
