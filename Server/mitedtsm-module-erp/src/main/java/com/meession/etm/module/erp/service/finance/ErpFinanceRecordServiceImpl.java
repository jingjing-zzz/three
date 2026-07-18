package com.meession.etm.module.erp.service.finance;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjUtil;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.bpm.api.task.BpmProcessInstanceApi;
import com.meession.etm.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import com.meession.etm.module.bpm.enums.task.BpmTaskStatusEnum;
import com.meession.etm.module.erp.controller.admin.finance.vo.record.ErpFinanceRecordPageReqVO;
import com.meession.etm.module.erp.controller.admin.finance.vo.record.ErpFinanceRecordSaveReqVO;
import com.meession.etm.module.erp.controller.admin.finance.vo.record.ErpFinanceSummaryRespVO;
import com.meession.etm.module.erp.dal.dataobject.finance.ErpFinancePaymentDO;
import com.meession.etm.module.erp.dal.dataobject.finance.ErpFinanceReceiptDO;
import com.meession.etm.module.erp.dal.dataobject.finance.ErpFinanceRecordDO;
import com.meession.etm.module.erp.dal.mysql.finance.ErpFinancePaymentMapper;
import com.meession.etm.module.erp.dal.mysql.finance.ErpFinanceReceiptMapper;
import com.meession.etm.module.erp.dal.mysql.finance.ErpFinanceRecordMapper;
import com.meession.etm.module.erp.dal.redis.no.ErpNoRedisDAO;
import com.meession.etm.module.erp.enums.ErpAuditStatus;
import com.meession.etm.module.erp.enums.finance.ErpFinanceRecordTypeEnum;
import com.meession.etm.module.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.meession.etm.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.meession.etm.framework.common.util.collection.CollectionUtils.getSumValue;
import static com.meession.etm.module.erp.enums.ErrorCodeConstants.*;

@Service
@Validated
public class ErpFinanceRecordServiceImpl implements ErpFinanceRecordService {

    public static final String REIMBURSEMENT_PROCESS_DEFINITION_KEY = "erp-reimbursement-audit";
    public static final String REFUND_PROCESS_DEFINITION_KEY = "erp-refund-audit";

    @Resource
    private ErpFinanceRecordMapper financeRecordMapper;
    @Resource
    private ErpFinanceReceiptMapper financeReceiptMapper;
    @Resource
    private ErpFinancePaymentMapper financePaymentMapper;
    @Resource
    private ErpNoRedisDAO noRedisDAO;
    @Resource
    private ErpAccountService accountService;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFinanceRecord(ErpFinanceRecordSaveReqVO createReqVO, Long userId) {
        validateRelationData(createReqVO);
        ErpFinanceRecordTypeEnum type = ErpFinanceRecordTypeEnum.of(createReqVO.getType());
        Assert.notNull(type, "财务单据类型不正确");
        String no = noRedisDAO.generate(type.getNoPrefix());
        if (financeRecordMapper.selectByNo(no) != null) {
            throw exception(FINANCE_RECORD_NO_EXISTS);
        }
        ErpFinanceRecordDO record = BeanUtils.toBean(createReqVO, ErpFinanceRecordDO.class)
                .setNo(no)
                .setStatus(ErpAuditStatus.PROCESS.getStatus());
        fillCalculatedFields(record);
        financeRecordMapper.insert(record);

        String processDefinitionKey = getProcessDefinitionKey(type);
        if (processDefinitionKey != null) {
            String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId,
                    new BpmProcessInstanceCreateReqDTO()
                            .setProcessDefinitionKey(processDefinitionKey)
                            .setBusinessKey(String.valueOf(record.getId())));
            financeRecordMapper.updateById(new ErpFinanceRecordDO()
                    .setId(record.getId())
                    .setProcessInstanceId(processInstanceId));
        }
        return record.getId();
    }

    private String getProcessDefinitionKey(ErpFinanceRecordTypeEnum type) {
        return switch (type) {
            case REIMBURSEMENT -> REIMBURSEMENT_PROCESS_DEFINITION_KEY;
            case REFUND -> REFUND_PROCESS_DEFINITION_KEY;
            default -> null;
        };
    }

    @Override
    public void updateFinanceRecord(ErpFinanceRecordSaveReqVO updateReqVO) {
        ErpFinanceRecordDO oldRecord = validateFinanceRecordExists(updateReqVO.getId());
        if (ErpAuditStatus.APPROVE.getStatus().equals(oldRecord.getStatus())) {
            throw exception(FINANCE_RECORD_UPDATE_FAIL_APPROVE, oldRecord.getNo());
        }
        validateRelationData(updateReqVO);
        ErpFinanceRecordDO updateObj = BeanUtils.toBean(updateReqVO, ErpFinanceRecordDO.class);
        fillCalculatedFields(updateObj);
        financeRecordMapper.updateById(updateObj);
    }

    private void validateRelationData(ErpFinanceRecordSaveReqVO reqVO) {
        if (reqVO.getAccountId() != null) {
            accountService.validateAccount(reqVO.getAccountId());
        }
        if (reqVO.getApplicantUserId() != null) {
            adminUserApi.validateUser(reqVO.getApplicantUserId());
        }
        if (reqVO.getFinanceUserId() != null) {
            adminUserApi.validateUser(reqVO.getFinanceUserId());
        }
    }

    private void fillCalculatedFields(ErpFinanceRecordDO record) {
        BigDecimal taxAmount = record.getTaxAmount() == null ? BigDecimal.ZERO : record.getTaxAmount();
        record.setTaxAmount(taxAmount);
        record.setTotalAmount(record.getAmount().add(taxAmount));
        record.setOverdue(record.getDueTime() != null && record.getDueTime().isBefore(LocalDateTime.now()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFinanceRecordStatus(Long id, Integer status) {
        boolean approve = ErpAuditStatus.APPROVE.getStatus().equals(status);
        ErpFinanceRecordDO record = validateFinanceRecordExists(id);
        if (record.getStatus().equals(status)) {
            throw exception(approve ? FINANCE_RECORD_APPROVE_FAIL : FINANCE_RECORD_PROCESS_FAIL);
        }
        int updateCount = financeRecordMapper.updateByIdAndStatus(id, record.getStatus(),
                new ErpFinanceRecordDO().setStatus(status));
        if (updateCount == 0) {
            throw exception(approve ? FINANCE_RECORD_APPROVE_FAIL : FINANCE_RECORD_PROCESS_FAIL);
        }
    }

    @Override
    public void updateFinanceRecordAuditStatus(Long id, Integer bpmResult) {
        ErpFinanceRecordDO record = validateFinanceRecordExists(id);
        if (ObjUtil.notEqual(record.getStatus(), ErpAuditStatus.PROCESS.getStatus())) {
            return;
        }
        Integer auditStatus = BpmTaskStatusEnum.APPROVE.getStatus().equals(bpmResult)
                ? ErpAuditStatus.APPROVE.getStatus() : ErpAuditStatus.PROCESS.getStatus();
        financeRecordMapper.updateById(new ErpFinanceRecordDO().setId(id).setStatus(auditStatus));
    }

    @Override
    public void deleteFinanceRecord(Collection<Long> ids) {
        List<ErpFinanceRecordDO> records = financeRecordMapper.selectByIds(ids);
        if (CollUtil.isEmpty(records)) {
            return;
        }
        records.forEach(record -> {
            if (ErpAuditStatus.APPROVE.getStatus().equals(record.getStatus())) {
                throw exception(FINANCE_RECORD_DELETE_FAIL_APPROVE, record.getNo());
            }
        });
        financeRecordMapper.deleteByIds(ids);
    }

    private ErpFinanceRecordDO validateFinanceRecordExists(Long id) {
        ErpFinanceRecordDO record = financeRecordMapper.selectById(id);
        if (record == null) {
            throw exception(FINANCE_RECORD_NOT_EXISTS);
        }
        return record;
    }

    @Override
    public ErpFinanceRecordDO getFinanceRecord(Long id) {
        return financeRecordMapper.selectById(id);
    }

    @Override
    public PageResult<ErpFinanceRecordDO> getFinanceRecordPage(ErpFinanceRecordPageReqVO pageReqVO) {
        return financeRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ErpFinanceRecordDO> getFinanceRecordList(ErpFinanceRecordPageReqVO pageReqVO) {
        return financeRecordMapper.selectList(pageReqVO);
    }

    @Override
    public ErpFinanceSummaryRespVO getFinanceSummary(ErpFinanceRecordPageReqVO reqVO) {
        List<ErpFinanceRecordDO> records = financeRecordMapper.selectList(reqVO);
        BigDecimal invoiceAmount = sumByType(records, ErpFinanceRecordTypeEnum.INVOICE.getType());
        BigDecimal reimbursementAmount = sumByType(records, ErpFinanceRecordTypeEnum.REIMBURSEMENT.getType());
        BigDecimal refundAmount = sumByType(records, ErpFinanceRecordTypeEnum.REFUND.getType());
        BigDecimal expenseAmount = sumByType(records, ErpFinanceRecordTypeEnum.EXPENSE.getType());
        BigDecimal receiptAmount = getSumValue(financeReceiptMapper.selectList(new LambdaQueryWrapperX<ErpFinanceReceiptDO>()
                        .betweenIfPresent(ErpFinanceReceiptDO::getReceiptTime, reqVO.getRecordTime())),
                ErpFinanceReceiptDO::getReceiptPrice, BigDecimal::add, BigDecimal.ZERO);
        BigDecimal paymentAmount = getSumValue(financePaymentMapper.selectList(new LambdaQueryWrapperX<ErpFinancePaymentDO>()
                        .betweenIfPresent(ErpFinancePaymentDO::getPaymentTime, reqVO.getRecordTime())),
                ErpFinancePaymentDO::getPaymentPrice, BigDecimal::add, BigDecimal.ZERO);
        BigDecimal netAmount = receiptAmount.add(invoiceAmount).subtract(paymentAmount).subtract(reimbursementAmount).subtract(refundAmount).subtract(expenseAmount);
        return new ErpFinanceSummaryRespVO()
                .setInvoiceAmount(invoiceAmount)
                .setReimbursementAmount(reimbursementAmount)
                .setRefundAmount(refundAmount)
                .setExpenseAmount(expenseAmount)
                .setReceiptAmount(receiptAmount)
                .setPaymentAmount(paymentAmount)
                .setNetAmount(netAmount)
                .setOverdueCount(financeRecordMapper.selectOverdueCount());
    }

    private BigDecimal sumByType(List<ErpFinanceRecordDO> records, Integer type) {
        return getSumValue(records.stream().filter(record -> type.equals(record.getType())).toList(),
                ErpFinanceRecordDO::getTotalAmount, BigDecimal::add, BigDecimal.ZERO);
    }

    @Override
    public int markOverdueRecords() {
        return financeRecordMapper.markOverdue(LocalDateTime.now());
    }

}
