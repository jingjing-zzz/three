package com.meession.etm.module.erp.dal.mysql.finance;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.erp.controller.admin.finance.vo.record.ErpFinanceRecordPageReqVO;
import com.meession.etm.module.erp.dal.dataobject.finance.ErpFinanceRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ErpFinanceRecordMapper extends BaseMapperX<ErpFinanceRecordDO> {

    default PageResult<ErpFinanceRecordDO> selectPage(ErpFinanceRecordPageReqVO reqVO) {
        return selectPage(reqVO, buildQuery(reqVO).orderByDesc(ErpFinanceRecordDO::getId));
    }

    default List<ErpFinanceRecordDO> selectList(ErpFinanceRecordPageReqVO reqVO) {
        return selectList(buildQuery(reqVO).orderByDesc(ErpFinanceRecordDO::getId));
    }

    private LambdaQueryWrapperX<ErpFinanceRecordDO> buildQuery(ErpFinanceRecordPageReqVO reqVO) {
        return new LambdaQueryWrapperX<ErpFinanceRecordDO>()
                .likeIfPresent(ErpFinanceRecordDO::getNo, reqVO.getNo())
                .eqIfPresent(ErpFinanceRecordDO::getType, reqVO.getType())
                .eqIfPresent(ErpFinanceRecordDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ErpFinanceRecordDO::getRecordTime, reqVO.getRecordTime())
                .betweenIfPresent(ErpFinanceRecordDO::getDueTime, reqVO.getDueTime())
                .eqIfPresent(ErpFinanceRecordDO::getApplicantUserId, reqVO.getApplicantUserId())
                .eqIfPresent(ErpFinanceRecordDO::getFinanceUserId, reqVO.getFinanceUserId())
                .eqIfPresent(ErpFinanceRecordDO::getAccountId, reqVO.getAccountId())
                .likeIfPresent(ErpFinanceRecordDO::getSubject, reqVO.getSubject())
                .likeIfPresent(ErpFinanceRecordDO::getCounterparty, reqVO.getCounterparty())
                .likeIfPresent(ErpFinanceRecordDO::getInvoiceNo, reqVO.getInvoiceNo())
                .eqIfPresent(ErpFinanceRecordDO::getOverdue, reqVO.getOverdue());
    }

    default ErpFinanceRecordDO selectByNo(String no) {
        return selectOne(ErpFinanceRecordDO::getNo, no);
    }

    default int updateByIdAndStatus(Long id, Integer status, ErpFinanceRecordDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<ErpFinanceRecordDO>()
                .eq(ErpFinanceRecordDO::getId, id)
                .eq(ErpFinanceRecordDO::getStatus, status));
    }

    default Long selectOverdueCount() {
        return selectCount(new LambdaQueryWrapperX<ErpFinanceRecordDO>()
                .eq(ErpFinanceRecordDO::getOverdue, true));
    }

    default int markOverdue(LocalDateTime now) {
        return update(new ErpFinanceRecordDO().setOverdue(true), new LambdaUpdateWrapper<ErpFinanceRecordDO>()
                .isNotNull(ErpFinanceRecordDO::getDueTime)
                .lt(ErpFinanceRecordDO::getDueTime, now)
                .eq(ErpFinanceRecordDO::getOverdue, false)
                .isNull(ErpFinanceRecordDO::getProcessInstanceId));
    }

}
