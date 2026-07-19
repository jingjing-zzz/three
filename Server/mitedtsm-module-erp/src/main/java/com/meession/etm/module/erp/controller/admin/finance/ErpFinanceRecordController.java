package com.meession.etm.module.erp.controller.admin.finance;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.apilog.core.annotation.ApiAccessLog;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.collection.MapUtils;
import com.meession.etm.framework.common.util.number.NumberUtils;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.framework.excel.core.util.ExcelUtils;
import com.meession.etm.module.erp.controller.admin.finance.vo.record.ErpFinanceRecordPageReqVO;
import com.meession.etm.module.erp.controller.admin.finance.vo.record.ErpFinanceRecordRespVO;
import com.meession.etm.module.erp.controller.admin.finance.vo.record.ErpFinanceRecordSaveReqVO;
import com.meession.etm.module.erp.controller.admin.finance.vo.record.ErpFinanceSummaryRespVO;
import com.meession.etm.module.erp.dal.dataobject.finance.ErpAccountDO;
import com.meession.etm.module.erp.dal.dataobject.finance.ErpFinanceRecordDO;
import com.meession.etm.module.erp.service.finance.ErpAccountService;
import com.meession.etm.module.erp.service.finance.ErpFinanceRecordService;
import com.meession.etm.module.system.api.user.AdminUserApi;
import com.meession.etm.module.system.api.user.dto.AdminUserRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.meession.etm.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - ERP 财务单据")
@RestController
@RequestMapping("/erp/finance-record")
@Validated
public class ErpFinanceRecordController {

    @Resource
    private ErpFinanceRecordService financeRecordService;
    @Resource
    private ErpAccountService accountService;
    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建财务单据")
    @PreAuthorize("@ss.hasPermission('erp:finance-record:create')")
    public CommonResult<Long> createFinanceRecord(@Valid @RequestBody ErpFinanceRecordSaveReqVO createReqVO) {
        return success(financeRecordService.createFinanceRecord(createReqVO, getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新财务单据")
    @PreAuthorize("@ss.hasPermission('erp:finance-record:update')")
    public CommonResult<Boolean> updateFinanceRecord(@Valid @RequestBody ErpFinanceRecordSaveReqVO updateReqVO) {
        financeRecordService.updateFinanceRecord(updateReqVO, getLoginUserId());
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新财务单据审核状态")
    @PreAuthorize("@ss.hasPermission('erp:finance-record:update-status')")
    public CommonResult<Boolean> updateFinanceRecordStatus(@RequestParam("id") Long id,
                                                           @RequestParam("status") Integer status) {
        financeRecordService.updateFinanceRecordStatus(id, status);
        return success(true);
    }

    @PutMapping("/mark-overdue")
    @Operation(summary = "检测并标记逾期财务单据")
    @PreAuthorize("@ss.hasPermission('erp:finance-record:update-status')")
    public CommonResult<Integer> markOverdueRecords() {
        return success(financeRecordService.markOverdueRecords());
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除财务单据")
    @Parameter(name = "ids", description = "编号数组", required = true)
    @PreAuthorize("@ss.hasPermission('erp:finance-record:delete')")
    public CommonResult<Boolean> deleteFinanceRecord(@RequestParam("ids") List<Long> ids) {
        financeRecordService.deleteFinanceRecord(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得财务单据")
    @PreAuthorize("@ss.hasPermission('erp:finance-record:query')")
    public CommonResult<ErpFinanceRecordRespVO> getFinanceRecord(@RequestParam("id") Long id) {
        ErpFinanceRecordDO record = financeRecordService.getFinanceRecord(id);
        return success(record == null ? null : buildFinanceRecordVO(record));
    }

    @GetMapping("/page")
    @Operation(summary = "获得财务单据分页")
    @PreAuthorize("@ss.hasPermission('erp:finance-record:query')")
    public CommonResult<PageResult<ErpFinanceRecordRespVO>> getFinanceRecordPage(@Valid ErpFinanceRecordPageReqVO pageReqVO) {
        PageResult<ErpFinanceRecordDO> pageResult = financeRecordService.getFinanceRecordPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        return success(BeanUtils.toBean(pageResult, ErpFinanceRecordRespVO.class, this::fillFinanceRecordVO));
    }

    @GetMapping("/summary")
    @Operation(summary = "获得财务数据分析")
    @PreAuthorize("@ss.hasPermission('erp:finance-record:query')")
    public CommonResult<ErpFinanceSummaryRespVO> getFinanceSummary(@Valid ErpFinanceRecordPageReqVO pageReqVO) {
        return success(financeRecordService.getFinanceSummary(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出财务单据 Excel")
    @PreAuthorize("@ss.hasPermission('erp:finance-record:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportFinanceRecordExcel(@Valid ErpFinanceRecordPageReqVO pageReqVO,
                                         HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ErpFinanceRecordRespVO> list = BeanUtils.toBean(financeRecordService.getFinanceRecordList(pageReqVO),
                ErpFinanceRecordRespVO.class, this::fillFinanceRecordVO);
        ExcelUtils.write(response, "财务单据.xls", "数据", ErpFinanceRecordRespVO.class, list);
    }

    private ErpFinanceRecordRespVO buildFinanceRecordVO(ErpFinanceRecordDO record) {
        return BeanUtils.toBean(record, ErpFinanceRecordRespVO.class, this::fillFinanceRecordVO);
    }

    private void fillFinanceRecordVO(ErpFinanceRecordRespVO record) {
        Set<Long> userIds = new HashSet<>();
        addIfNotNull(userIds, NumberUtils.parseLong(record.getCreator()));
        addIfNotNull(userIds, record.getApplicantUserId());
        addIfNotNull(userIds, record.getFinanceUserId());
        Map<Long, AdminUserRespDTO> userMap = userIds.isEmpty() ? new HashMap<>() : adminUserApi.getUserMap(userIds);

        Map<Long, ErpAccountDO> accountMap = new HashMap<>();
        if (record.getAccountId() != null) {
            accountService.getAccountList(Set.of(record.getAccountId()))
                    .forEach(account -> accountMap.put(account.getId(), account));
        }
        MapUtils.findAndThen(userMap, NumberUtils.parseLong(record.getCreator()), user -> record.setCreatorName(user.getNickname()));
        MapUtils.findAndThen(userMap, record.getApplicantUserId(), user -> record.setApplicantUserName(user.getNickname()));
        MapUtils.findAndThen(userMap, record.getFinanceUserId(), user -> record.setFinanceUserName(user.getNickname()));
        MapUtils.findAndThen(accountMap, record.getAccountId(), account -> record.setAccountName(account.getName()));
    }

    private void addIfNotNull(Set<Long> set, Long value) {
        if (value != null) {
            set.add(value);
        }
    }

}
