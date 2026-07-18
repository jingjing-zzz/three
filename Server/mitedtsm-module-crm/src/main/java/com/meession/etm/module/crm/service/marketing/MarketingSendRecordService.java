package com.meession.etm.module.crm.service.marketing;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.marketing.vo.record.MarketingSendRecordPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSendRecordDO;

public interface MarketingSendRecordService {

    MarketingSendRecordDO getSendRecord(Long id);

    MarketingSendRecordDO validateSendRecord(Long id);

    PageResult<MarketingSendRecordDO> getSendRecordPage(MarketingSendRecordPageReqVO pageReqVO);

}
