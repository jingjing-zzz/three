package com.meession.etm.module.crm.service.marketing.impl;

import com.meession.etm.framework.common.exception.ServiceException;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.marketing.vo.record.MarketingSendRecordPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSendRecordDO;
import com.meession.etm.module.crm.dal.mysql.marketing.MarketingSendRecordMapper;
import com.meession.etm.module.crm.enums.ErrorCodeConstants;
import com.meession.etm.module.crm.service.marketing.MarketingSendRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MarketingSendRecordServiceImpl implements MarketingSendRecordService {

    @Resource
    private MarketingSendRecordMapper sendRecordMapper;

    @Override
    public MarketingSendRecordDO getSendRecord(Long id) {
        return sendRecordMapper.selectById(id);
    }

    @Override
    public MarketingSendRecordDO validateSendRecord(Long id) {
        MarketingSendRecordDO record = getSendRecord(id);
        if (record == null) {
            throw new ServiceException(ErrorCodeConstants.MARKETING_SEND_RECORD_NOT_EXISTS);
        }
        return record;
    }

    @Override
    public PageResult<MarketingSendRecordDO> getSendRecordPage(MarketingSendRecordPageReqVO pageReqVO) {
        return sendRecordMapper.selectPage(pageReqVO);
    }

}
