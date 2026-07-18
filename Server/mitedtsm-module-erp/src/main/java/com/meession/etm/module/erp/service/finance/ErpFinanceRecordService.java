package com.meession.etm.module.erp.service.finance;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.erp.controller.admin.finance.vo.record.ErpFinanceRecordPageReqVO;
import com.meession.etm.module.erp.controller.admin.finance.vo.record.ErpFinanceRecordSaveReqVO;
import com.meession.etm.module.erp.controller.admin.finance.vo.record.ErpFinanceSummaryRespVO;
import com.meession.etm.module.erp.dal.dataobject.finance.ErpFinanceRecordDO;

import java.util.Collection;
import java.util.List;

public interface ErpFinanceRecordService {

    Long createFinanceRecord(ErpFinanceRecordSaveReqVO createReqVO, Long userId);

    void updateFinanceRecord(ErpFinanceRecordSaveReqVO updateReqVO);

    void updateFinanceRecordStatus(Long id, Integer status);

    void updateFinanceRecordAuditStatus(Long id, Integer bpmResult);

    void deleteFinanceRecord(Collection<Long> ids);

    ErpFinanceRecordDO getFinanceRecord(Long id);

    PageResult<ErpFinanceRecordDO> getFinanceRecordPage(ErpFinanceRecordPageReqVO pageReqVO);

    List<ErpFinanceRecordDO> getFinanceRecordList(ErpFinanceRecordPageReqVO pageReqVO);

    ErpFinanceSummaryRespVO getFinanceSummary(ErpFinanceRecordPageReqVO reqVO);

    int markOverdueRecords();

}
