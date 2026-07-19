package com.meession.etm.module.crm.dal.mysql.order;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderDO;
import com.meession.etm.module.crm.enums.common.CrmBizTypeEnum;
import com.meession.etm.module.crm.enums.common.CrmSceneTypeEnum;
import com.meession.etm.module.crm.util.CrmPermissionUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CrmOrderMapper extends BaseMapperX<CrmOrderDO> {

    default CrmOrderDO selectByNo(String no) {
        return selectOne(CrmOrderDO::getNo, no);
    }

    default List<CrmOrderDO> selectListByCustomerId(Long customerId) {
        return selectList(CrmOrderDO::getCustomerId, customerId);
    }

    default List<CrmOrderDO> selectListByContractId(Long contractId) {
        return selectList(CrmOrderDO::getContractId, contractId);
    }

    default List<CrmOrderDO> selectListByOwnerUserId(Long ownerUserId) {
        return selectList(CrmOrderDO::getOwnerUserId, ownerUserId);
    }

    default int deleteByContractId(Long contractId) {
        return delete(CrmOrderDO::getContractId, contractId);
    }

    default PageResult<CrmOrderDO> selectPage(CrmOrderPageReqVO pageReqVO, Long userId) {
        MPJLambdaWrapperX<CrmOrderDO> query = new MPJLambdaWrapperX<>();
        CrmPermissionUtils.appendPermissionCondition(query, CrmBizTypeEnum.CRM_ORDER.getType(),
                CrmOrderDO::getId, userId, pageReqVO.getSceneType());
        query.selectAll(CrmOrderDO.class)
                .likeIfPresent(CrmOrderDO::getNo, pageReqVO.getNo())
                .eqIfPresent(CrmOrderDO::getCustomerId, pageReqVO.getCustomerId())
                .eqIfPresent(CrmOrderDO::getContractId, pageReqVO.getContractId())
                .eqIfPresent(CrmOrderDO::getStatus, pageReqVO.getStatus())
                .eqIfPresent(CrmOrderDO::getOwnerUserId, pageReqVO.getOwnerUserId())
                .betweenIfPresent(CrmOrderDO::getOrderTime, pageReqVO.getOrderTimeRange())
                .betweenIfPresent(CrmOrderDO::getCreateTime, pageReqVO.getCreateTimeRange())
                .orderByDesc(CrmOrderDO::getId);
        return selectJoinPage(pageReqVO, CrmOrderDO.class, query);
    }

    default PageResult<CrmOrderDO> selectPageByCustomerId(CrmOrderPageReqVO pageReqVO) {
        return selectJoinPage(pageReqVO, CrmOrderDO.class, new MPJLambdaWrapperX<CrmOrderDO>()
                .selectAll(CrmOrderDO.class)
                .eq(CrmOrderDO::getCustomerId, pageReqVO.getCustomerId())
                .likeIfPresent(CrmOrderDO::getNo, pageReqVO.getNo())
                .eqIfPresent(CrmOrderDO::getStatus, pageReqVO.getStatus())
                .orderByDesc(CrmOrderDO::getId));
    }

    default PageResult<CrmOrderDO> selectPageByContractId(CrmOrderPageReqVO pageReqVO) {
        return selectJoinPage(pageReqVO, CrmOrderDO.class, new MPJLambdaWrapperX<CrmOrderDO>()
                .selectAll(CrmOrderDO.class)
                .eq(CrmOrderDO::getContractId, pageReqVO.getContractId())
                .likeIfPresent(CrmOrderDO::getNo, pageReqVO.getNo())
                .eqIfPresent(CrmOrderDO::getStatus, pageReqVO.getStatus())
                .orderByDesc(CrmOrderDO::getId));
    }

}