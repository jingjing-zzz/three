package com.meession.etm.module.bpm.controller.admin.oa;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOACustomerVisitCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOACustomerVisitPageReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOACustomerVisitRespVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOACustomerVisitDO;
import com.meession.etm.module.bpm.service.oa.BpmOACustomerVisitService;
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

@Tag(name = "管理后台 - OA 客户拜访申请")
@RestController
@RequestMapping("/bpm/oa/customer-visit")
@Validated
public class BpmOACustomerVisitController {

    @Resource
    private BpmOACustomerVisitService customerVisitService;

    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('bpm:oa-customer-visit:create')")
    @Operation(summary = "创建客户拜访申请")
    public CommonResult<Long> createCustomerVisit(@Valid @RequestBody BpmOACustomerVisitCreateReqVO createReqVO) {
        return success(customerVisitService.createCustomerVisit(getLoginUserId(), createReqVO));
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('bpm:oa-customer-visit:query')")
    @Operation(summary = "获得客户拜访申请")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<BpmOACustomerVisitRespVO> getCustomerVisit(@RequestParam("id") Long id) {
        BpmOACustomerVisitDO customerVisit = customerVisitService.getCustomerVisit(id);
        return success(BeanUtils.toBean(customerVisit, BpmOACustomerVisitRespVO.class));
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('bpm:oa-customer-visit:query')")
    @Operation(summary = "获得客户拜访申请分页")
    public CommonResult<PageResult<BpmOACustomerVisitRespVO>> getCustomerVisitPage(@Valid BpmOACustomerVisitPageReqVO pageVO) {
        PageResult<BpmOACustomerVisitDO> pageResult = customerVisitService.getCustomerVisitPage(getLoginUserId(), pageVO);
        return success(BeanUtils.toBean(pageResult, BpmOACustomerVisitRespVO.class));
    }

}