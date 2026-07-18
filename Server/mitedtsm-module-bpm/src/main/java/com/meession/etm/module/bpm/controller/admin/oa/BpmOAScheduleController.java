package com.meession.etm.module.bpm.controller.admin.oa;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAScheduleCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOASchedulePageReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOAScheduleRespVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOAScheduleDO;
import com.meession.etm.module.bpm.service.oa.BpmOAScheduleService;
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

@Tag(name = "管理后台 - OA 日程管理")
@RestController
@RequestMapping("/bpm/oa/schedule")
@Validated
public class BpmOAScheduleController {

    @Resource
    private BpmOAScheduleService scheduleService;

    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('bpm:oa-schedule:create')")
    @Operation(summary = "创建日程")
    public CommonResult<Long> createSchedule(@Valid @RequestBody BpmOAScheduleCreateReqVO createReqVO) {
        return success(scheduleService.createSchedule(getLoginUserId(), createReqVO));
    }

    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermission('bpm:oa-schedule:update')")
    @Operation(summary = "更新日程")
    public CommonResult<Boolean> updateSchedule(@Valid @RequestBody BpmOAScheduleDO updateReqVO) {
        scheduleService.updateSchedule(updateReqVO.getId(), updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@ss.hasPermission('bpm:oa-schedule:delete')")
    @Operation(summary = "删除日程")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<Boolean> deleteSchedule(@RequestParam("id") Long id) {
        scheduleService.deleteSchedule(id);
        return success(true);
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('bpm:oa-schedule:query')")
    @Operation(summary = "获得日程")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<BpmOAScheduleRespVO> getSchedule(@RequestParam("id") Long id) {
        BpmOAScheduleDO schedule = scheduleService.getSchedule(id);
        return success(BeanUtils.toBean(schedule, BpmOAScheduleRespVO.class));
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('bpm:oa-schedule:query')")
    @Operation(summary = "获得日程分页")
    public CommonResult<PageResult<BpmOAScheduleRespVO>> getSchedulePage(@Valid BpmOASchedulePageReqVO pageVO) {
        PageResult<BpmOAScheduleDO> pageResult = scheduleService.getSchedulePage(getLoginUserId(), pageVO);
        return success(BeanUtils.toBean(pageResult, BpmOAScheduleRespVO.class));
    }

}