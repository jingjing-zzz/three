package com.meession.etm.module.crm.controller.admin.marketing;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.record.MarketingSendRecordPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.record.MarketingSendRecordRespVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingSendRecordDO;
import com.meession.etm.module.crm.service.marketing.MarketingSendRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 发送记录")
@RestController
@RequestMapping("/crm/marketing/send-record")
@Validated
public class MarketingSendRecordController {

    @Resource
    private MarketingSendRecordService sendRecordService;

    @GetMapping("/get")
    @Operation(summary = "获得发送记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:marketing:send-record:query')")
    public CommonResult<MarketingSendRecordRespVO> getSendRecord(@RequestParam("id") Long id) {
        MarketingSendRecordDO record = sendRecordService.getSendRecord(id);
        return success(BeanUtils.toBean(record, MarketingSendRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询发送记录")
    @PreAuthorize("@ss.hasPermission('crm:marketing:send-record:query')")
    public CommonResult<PageResult<MarketingSendRecordRespVO>> pageSendRecord(@Valid MarketingSendRecordPageReqVO pageReqVO) {
        PageResult<MarketingSendRecordDO> pageResult = sendRecordService.getSendRecordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MarketingSendRecordRespVO.class));
    }

}
