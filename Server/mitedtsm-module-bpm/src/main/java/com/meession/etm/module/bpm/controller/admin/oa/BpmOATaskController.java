package com.meession.etm.module.bpm.controller.admin.oa;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOATaskCreateReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOATaskPageReqVO;
import com.meession.etm.module.bpm.controller.admin.oa.vo.BpmOATaskRespVO;
import com.meession.etm.module.bpm.dal.dataobject.oa.BpmOATaskDO;
import com.meession.etm.module.bpm.service.oa.BpmOATaskService;
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

@Tag(name = "管理后台 - OA 任务管理")
@RestController
@RequestMapping("/bpm/oa/task")
@Validated
public class BpmOATaskController {

    @Resource
    private BpmOATaskService taskService;

    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('bpm:oa-task:create')")
    @Operation(summary = "创建任务")
    public CommonResult<Long> createTask(@Valid @RequestBody BpmOATaskCreateReqVO createReqVO) {
        return success(taskService.createTask(getLoginUserId(), createReqVO));
    }

    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermission('bpm:oa-task:update')")
    @Operation(summary = "更新任务")
    public CommonResult<Boolean> updateTask(@Valid @RequestBody BpmOATaskDO updateReqVO) {
        taskService.updateTask(updateReqVO.getId(), updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@ss.hasPermission('bpm:oa-task:delete')")
    @Operation(summary = "删除任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<Boolean> deleteTask(@RequestParam("id") Long id) {
        taskService.deleteTask(id);
        return success(true);
    }

    @GetMapping("/get")
    @PreAuthorize("@ss.hasPermission('bpm:oa-task:query')")
    @Operation(summary = "获得任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<BpmOATaskRespVO> getTask(@RequestParam("id") Long id) {
        BpmOATaskDO task = taskService.getTask(id);
        return success(BeanUtils.toBean(task, BpmOATaskRespVO.class));
    }

    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('bpm:oa-task:query')")
    @Operation(summary = "获得任务分页")
    public CommonResult<PageResult<BpmOATaskRespVO>> getTaskPage(@Valid BpmOATaskPageReqVO pageVO) {
        PageResult<BpmOATaskDO> pageResult = taskService.getTaskPage(getLoginUserId(), pageVO);
        return success(BeanUtils.toBean(pageResult, BpmOATaskRespVO.class));
    }

}