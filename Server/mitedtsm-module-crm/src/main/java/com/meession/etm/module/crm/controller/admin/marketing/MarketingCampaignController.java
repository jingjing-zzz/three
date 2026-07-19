package com.meession.etm.module.crm.controller.admin.marketing;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.marketing.vo.campaign.MarketingCampaignPageReqVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.campaign.MarketingCampaignRespVO;
import com.meession.etm.module.crm.controller.admin.marketing.vo.campaign.MarketingCampaignSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.marketing.MarketingCampaignDO;
import com.meession.etm.module.crm.service.marketing.MarketingCampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 营销活动")
@RestController
@RequestMapping("/crm/marketing/campaign")
@Validated
public class MarketingCampaignController {

    @Resource
    private MarketingCampaignService campaignService;

    @PostMapping("/create")
    @Operation(summary = "创建营销活动")
    @PreAuthorize("@ss.hasPermission('crm:marketing:campaign:create')")
    public CommonResult<Long> createCampaign(@Valid @RequestBody MarketingCampaignSaveReqVO createReqVO) {
        return success(campaignService.createCampaign(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新营销活动")
    @PreAuthorize("@ss.hasPermission('crm:marketing:campaign:update')")
    public CommonResult<Boolean> updateCampaign(@Valid @RequestBody MarketingCampaignSaveReqVO updateReqVO) {
        campaignService.updateCampaign(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除营销活动")
    @Parameter(name = "id", description = "活动编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:marketing:campaign:delete')")
    public CommonResult<Boolean> deleteCampaign(@RequestParam("id") Long id) {
        campaignService.deleteCampaign(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取营销活动")
    @Parameter(name = "id", description = "活动编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:marketing:campaign:query')")
    public CommonResult<MarketingCampaignRespVO> getCampaign(@RequestParam("id") Long id) {
        MarketingCampaignDO campaign = campaignService.getCampaign(id);
        return success(BeanUtils.toBean(campaign, MarketingCampaignRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获取营销活动分页")
    @PreAuthorize("@ss.hasPermission('crm:marketing:campaign:query')")
    public CommonResult<PageResult<MarketingCampaignRespVO>> getCampaignPage(
            @Valid MarketingCampaignPageReqVO pageReqVO) {
        PageResult<MarketingCampaignDO> pageResult = campaignService.getCampaignPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MarketingCampaignRespVO.class));
    }

}