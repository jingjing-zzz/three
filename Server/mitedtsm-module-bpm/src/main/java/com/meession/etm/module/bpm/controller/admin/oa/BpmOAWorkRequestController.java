package com.meession.etm.module.bpm.controller.admin.oa;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkRequestCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkRequestPageReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAWorkRequestRespVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOAWorkRequestDO;
import com.meession.etm.module.bpm.service.oa.BpmOAWorkRequestService;
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

@Tag(name = "管理后台 - OA 请示审批")
@RestController
@RequestMapping("/bpm/oa/work-request")
@Validated
public class BpmOAWorkRequestController {

    @Resource
    private BpmOAWorkRequestService workRequestService;

    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('bpm:oa-work-request:create')")
    @Operation(summary = "创建请示审批")
    public CommonResult<Long> createWorkRequest(@Valid @RequestBody BpmOAWorkRequestCreateReqVO createReqVO) {
        return success(workRequestService.createWorkRequest(getLoginUserId(), createReqVO));
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('bpm:oa-work-request:query')")
    @Operation(summary = "获得请示审批")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<BpmOAWorkRequestRespVO> getWorkRequest(@RequestParam("id") Long id) {
        BpmOAWorkRequestDO workRequest = workRequestService.getWorkRequest(id);
        return success(BeanUtils.toBean(workRequest, BpmOAWorkRequestRespVO.class));
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('bpm:oa-work-request:query')")
    @Operation(summary = "获得请示审批分页")
    public CommonResult<PageResult<BpmOAWorkRequestRespVO>> getWorkRequestPage(@Valid BpmOAWorkRequestPageReqVO pageVO) {
        PageResult<BpmOAWorkRequestDO> pageResult = workRequestService.getWorkRequestPage(getLoginUserId(), pageVO);
        return success(BeanUtils.toBean(pageResult, BpmOAWorkRequestRespVO.class));
    }

}