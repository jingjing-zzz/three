package com.meession.etm.module.bpm.controller.admin.oa;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOALoanCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOALoanPageReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOALoanRespVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOALoanDO;
import com.meession.etm.module.bpm.service.oa.BpmOALoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - OA 借款申请")
@RestController
@RequestMapping("/bpm/oa/loan")
@Validated
public class BpmOALoanController {

    @Resource
    private BpmOALoanService loanService;

    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('bpm:oa-loan:create')")
    @Operation(summary = "创建借款申请")
    public CommonResult<Long> createLoan(@Valid @RequestBody BpmOALoanCreateReqVO createReqVO) {
        return success(loanService.createLoan(getLoginUserId(), createReqVO));
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('bpm:oa-loan:query')")
    @Operation(summary = "获得借款申请")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<BpmOALoanRespVO> getLoan(@RequestParam("id") Long id) {
        BpmOALoanDO loan = loanService.getLoan(id);
        return success(BeanUtils.toBean(loan, BpmOALoanRespVO.class));
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('bpm:oa-loan:query')")
    @Operation(summary = "获得借款申请分页")
    public CommonResult<PageResult<BpmOALoanRespVO>> getLoanPage(@Valid BpmOALoanPageReqVO pageVO) {
        PageResult<BpmOALoanDO> pageResult = loanService.getLoanPage(getLoginUserId(), pageVO);
        return success(BeanUtils.toBean(pageResult, BpmOALoanRespVO.class));
    }

}