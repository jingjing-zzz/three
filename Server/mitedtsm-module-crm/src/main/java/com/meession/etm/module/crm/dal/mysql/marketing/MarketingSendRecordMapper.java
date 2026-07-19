package com.meession.etm.module.crm.dal.mysql.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.mybatis.core.mapper.BaseMapperX;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.crm.controller.admin.marketing.vo.record.MarketingSendRecordPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSendRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MarketingSendRecordMapper extends BaseMapperX<MarketingSendRecordDO> {

    default PageResult<MarketingSendRecordDO> selectPage(MarketingSendRecordPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<MarketingSendRecordDO>()
                .eqIfPresent(MarketingSendRecordDO::getBatchId, pageReqVO.getBatchId())
                .eqIfPresent(MarketingSendRecordDO::getType, pageReqVO.getTargetType())
                .eqIfPresent(MarketingSendRecordDO::getStatus, pageReqVO.getStatus())
                .orderByDesc(MarketingSendRecordDO::getId));
    }

}
