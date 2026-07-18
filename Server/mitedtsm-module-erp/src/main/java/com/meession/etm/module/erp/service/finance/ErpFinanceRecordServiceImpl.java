package com.meession.etm.module.erp.service.finance;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjUtil;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.meession.etm.module.bpm.api.task.BpmProcessInstanceApi;
import com.meession.etm.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import com.meession.etm.module.bpm.enums.task.BpmProcessInstanceStatusEnum;
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
import com.meession.etm.module.erp.enums.finance.ErpFinanceRecordStatusEnum;
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
    public static final String FINANCE_APPROVE_TASK_ID = "financeApproveTask";

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
        ErpFinanceRecordTypeEnum type = ErpFinanceRecordTypeEnum.of(createReqVO.getType());
        Assert.notNull(type, "财务单据类型不正确");
        validateRelationData(createReqVO, type);
        String no = noRedisDAO.generate(type.getNoPrefix());
        if (financeRecordMapper.selectByNo(no) != null) {
            throw exception(FINANCE_RECORD_NO_EXISTS);
        }
        ErpFinanceRecordDO record = BeanUtils.toBean(createReqVO, ErpFinanceRecordDO.class)
                .setNo(no)
                .setStatus(ErpFinanceRecordStatusEnum.PROCESS.getStatus());
        fillCalculatedFields(record, record.getStatus());
        financeRecordMapper.insert(record);

        if (isBpmType(type)) {
            startApprovalProcess(record, userId, type);
        }
        return record.getId();
    }

    private void startApprovalProcess(ErpFinanceRecordDO record, Long userId, ErpFinanceRecordTypeEnum type) {
        String processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId,
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(getProcessDefinitionKey(type))
                        .setBusinessKey(String.valueOf(record.getId()))
                        .setStartUserSelectAssignees(
                                java.util.Map.of(FINANCE_APPROVE_TASK_ID, List.of(record.getFinanceUserId()))));
        financeRecordMapper.updateById(new ErpFinanceRecordDO()
                .setId(record.getId())
                .setStatus(ErpFinanceRecordStatusEnum.PROCESS.getStatus())
                .setProcessInstanceId(processInstanceId));
    }

    private String getProcessDefinitionKey(ErpFinanceRecordTypeEnum type) {
        return switch (type) {
            case REIMBURSEMENT -> REIMBURSEMENT_PROCESS_DEFINITION_KEY;
            case REFUND -> REFUND_PROCESS_DEFINITION_KEY;
            default -> null;
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFinanceRecord(ErpFinanceRecordSaveReqVO updateReqVO, Long userId) {
        Assert.notNull(updateReqVO.getId(), "财务单据编号不能为空");
        ErpFinanceRecordDO oldRecord = validateFinanceRecordExists(updateReqVO.getId());
        ErpFinanceRecordTypeEnum type = ErpFinanceRecordTypeEnum.of(updateReqVO.getType());
        Assert.notNull(type, "财务单据类型不正确");
        if (!oldRecord.getType().equals(type.getType())) {
            throw exception(FINANCE_RECORD_TYPE_UPDATE_NOT_ALLOWED);
        }
        if (ErpFinanceRecordStatusEnum.APPROVE.getStatus().equals(oldRecord.getStatus())) {
            throw exception(FINANCE_RECORD_UPDATE_FAIL_APPROVE, oldRecord.getNo());
        }
        if (isBpmType(type) && ErpFinanceRecordStatusEnum.PROCESS.getStatus().equals(oldRecord.getStatus())
                && oldRecord.getProcessInstanceId() != null) {
            throw exception(FINANCE_RECORD_UPDATE_FAIL_PROCESS, oldRecord.getNo());
        }
        validateRelationData(updateReqVO, type);
        ErpFinanceRecordDO updateObj = BeanUtils.toBean(updateReqVO, ErpFinanceRecordDO.class);
        fillCalculatedFields(updateObj, oldRecord.getStatus());
        financeRecordMapper.updateById(updateObj);
        if (isBpmType(type) && (ErpFinanceRecordStatusEnum.REJECT.getStatus().equals(oldRecord.getStatus())
                || ErpFinanceRecordStatusEnum.CANCEL.getStatus().equals(oldRecord.getStatus()))) {
            startApprovalProcess(BeanUtils.toBean(updateReqVO, ErpFinanceRecordDO.class), userId, type);
        }
    }

    private void validateRelationData(ErpFinanceRecordSaveReqVO reqVO, ErpFinanceRecordTypeEnum type) {
        if (reqVO.getAccountId() != null) {
            accountService.validateAccount(reqVO.getAccountId());
        }
        if (reqVO.getApplicantUserId() != null) {
            adminUserApi.validateUser(reqVO.getApplicantUserId());
        }
        if (reqVO.getFinanceUserId() != null) {
            adminUserApi.validateUser(reqVO.getFinanceUserId());
        }
        if (isBpmType(type) && reqVO.getFinanceUserId() == null) {
            throw exception(FINANCE_RECORD_FINANCE_USER_REQUIRED);
        }
    }

    private boolean isBpmType(ErpFinanceRecordTypeEnum type) {
        return getProcessDefinitionKey(type) != null;
    }

    private void fillCalculatedFields(ErpFinanceRecordDO record, Integer status) {
        BigDecimal taxAmount = record.getTaxAmount() == null ? BigDecimal.ZERO : record.getTaxAmount();
        record.setTaxAmount(taxAmount);
        record.setTotalAmount(record.getAmount().add(taxAmount));
        record.setOverdue(!ErpFinanceRecordStatusEnum.APPROVE.getStatus().equals(status)
                && record.getDueTime() != null && record.getDueTime().isBefore(LocalDateTime.now()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFinanceRecordStatus(Long id, Integer status) {
        boolean approve = ErpFinanceRecordStatusEnum.APPROVE.getStatus().equals(status);
        if (!approve && !ErpFinanceRecordStatusEnum.PROCESS.getStatus().equals(status)) {
            throw exception(FINANCE_RECORD_PROCESS_FAIL);
        }
        ErpFinanceRecordDO record = validateFinanceRecordExists(id);
        ErpFinanceRecordTypeEnum type = ErpFinanceRecordTypeEnum.of(record.getType());
        if (isBpmType(type)) {
            throw exception(FINANCE_RECORD_BPM_MANUAL_AUDIT_NOT_ALLOWED);
        }
        if (record.getStatus().equals(status)) {
            throw exception(approve ? FINANCE_RECORD_APPROVE_FAIL : FINANCE_RECORD_PROCESS_FAIL);
        }
        int updateCount = financeRecordMapper.updateByIdAndStatus(id, record.getStatus(),
                new ErpFinanceRecordDO().setStatus(status)
                        .setOverdue(!approve && record.getDueTime() != null
                                && record.getDueTime().isBefore(LocalDateTime.now())));
        if (updateCount == 0) {
            throw exception(approve ? FINANCE_RECORD_APPROVE_FAIL : FINANCE_RECORD_PROCESS_FAIL);
        }
    }

    @Override
    public void updateFinanceRecordAuditStatus(Long id, Integer bpmResult) {
        ErpFinanceRecordDO record = validateFinanceRecordExists(id);
        if (ObjUtil.notEqual(record.getStatus(), ErpFinanceRecordStatusEnum.PROCESS.getStatus())) {
            return;
        }
        BpmProcessInstanceStatusEnum bpmStatus = BpmProcessInstanceStatusEnum.valueOf(bpmResult);
        if (bpmStatus == null || bpmStatus == BpmProcessInstanceStatusEnum.RUNNING) {
            return;
        }
        Integer auditStatus = switch (bpmStatus) {
            case APPROVE -> ErpFinanceRecordStatusEnum.APPROVE.getStatus();
            case REJECT -> ErpFinanceRecordStatusEnum.REJECT.getStatus();
            case CANCEL -> ErpFinanceRecordStatusEnum.CANCEL.getStatus();
            default -> null;
        };
        if (auditStatus == null) {
            return;
        }
        financeRecordMapper.updateById(new ErpFinanceRecordDO().setId(id).setStatus(auditStatus)
                .setOverdue(!ErpFinanceRecordStatusEnum.APPROVE.getStatus().equals(auditStatus)
                        && record.getDueTime() != null && record.getDueTime().isBefore(LocalDateTime.now())));
    }

    @Override
    public void deleteFinanceRecord(Collection<Long> ids) {
        List<ErpFinanceRecordDO> records = financeRecordMapper.selectByIds(ids);
        if (CollUtil.isEmpty(records)) {
            return;
        }
        records.forEach(record -> {
            if (ErpFinanceRecordStatusEnum.APPROVE.getStatus().equals(record.getStatus())) {
                throw exception(FINANCE_RECORD_DELETE_FAIL_APPROVE, record.getNo());
            }
            if (record.getProcessInstanceId() != null
                    && ErpFinanceRecordStatusEnum.PROCESS.getStatus().equals(record.getStatus())) {
                throw exception(FINANCE_RECORD_DELETE_FAIL_PROCESS, record.getNo());
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
        List<ErpFinanceRecordDO> approvedRecords = records.stream()
                .filter(record -> ErpFinanceRecordStatusEnum.APPROVE.getStatus().equals(record.getStatus())).toList();
        BigDecimal invoiceAmount = sumByType(approvedRecords, ErpFinanceRecordTypeEnum.INVOICE.getType());
        BigDecimal reimbursementAmount = sumByType(approvedRecords, ErpFinanceRecordTypeEnum.REIMBURSEMENT.getType());
        BigDecimal refundAmount = sumByType(approvedRecords, ErpFinanceRecordTypeEnum.REFUND.getType());
        BigDecimal expenseAmount = sumByType(approvedRecords, ErpFinanceRecordTypeEnum.EXPENSE.getType());
        BigDecimal receiptAmount = getSumValue(financeReceiptMapper.selectList(new LambdaQueryWrapperX<ErpFinanceReceiptDO>()
                        .eq(ErpFinanceReceiptDO::getStatus, ErpAuditStatus.APPROVE.getStatus())
                        .betweenIfPresent(ErpFinanceReceiptDO::getReceiptTime, reqVO.getRecordTime())),
                ErpFinanceReceiptDO::getReceiptPrice, BigDecimal::add, BigDecimal.ZERO);
        BigDecimal paymentAmount = getSumValue(financePaymentMapper.selectList(new LambdaQueryWrapperX<ErpFinancePaymentDO>()
                        .eq(ErpFinancePaymentDO::getStatus, ErpAuditStatus.APPROVE.getStatus())
                        .betweenIfPresent(ErpFinancePaymentDO::getPaymentTime, reqVO.getRecordTime())),
                ErpFinancePaymentDO::getPaymentPrice, BigDecimal::add, BigDecimal.ZERO);
        BigDecimal netAmount = receiptAmount.subtract(paymentAmount).subtract(reimbursementAmount)
                .subtract(refundAmount).subtract(expenseAmount);
        return new ErpFinanceSummaryRespVO()
                .setInvoiceAmount(invoiceAmount)
                .setReimbursementAmount(reimbursementAmount)
                .setRefundAmount(refundAmount)
                .setExpenseAmount(expenseAmount)
                .setReceiptAmount(receiptAmount)
                .setPaymentAmount(paymentAmount)
                .setNetAmount(netAmount)
                .setOverdueCount(records.stream().filter(record -> Boolean.TRUE.equals(record.getOverdue())).count());
    }

    private BigDecimal sumByType(List<ErpFinanceRecordDO> records, Integer type) {
        return getSumValue(records.stream().filter(record -> type.equals(record.getType())).toList(),
                ErpFinanceRecordDO::getTotalAmount, BigDecimal::add, BigDecimal.ZERO);
    }

    @Override
    public int markOverdueRecords() {
        return financeRecordMapper.refreshOverdue(LocalDateTime.now(), ErpFinanceRecordStatusEnum.APPROVE.getStatus());
    }

}
