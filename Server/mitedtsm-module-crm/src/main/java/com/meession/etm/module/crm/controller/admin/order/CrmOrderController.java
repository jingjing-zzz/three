package com.meession.etm.module.crm.controller.admin.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.meession.etm.framework.apilog.core.annotation.ApiAccessLog;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.collection.MapUtils;
import com.meession.etm.framework.common.util.number.NumberUtils;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.framework.excel.core.util.ExcelUtils;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderPageReqVO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderRespVO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderDO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderProductDO;
import com.meession.etm.module.crm.dal.dataobject.product.CrmProductDO;
import com.meession.etm.module.crm.service.contract.CrmContractService;
import com.meession.etm.module.crm.service.customer.CrmCustomerService;
import com.meession.etm.module.crm.service.order.CrmOrderService;
import com.meession.etm.module.crm.service.product.CrmProductService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.meession.etm.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.common.util.collection.CollectionUtils.*;
import static com.meession.etm.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static java.util.Collections.singletonList;

@Tag(name = "管理后台 - CRM 订单")
@RestController
@RequestMapping("/crm/order")
@Validated
public class CrmOrderController {

    @Resource
    private CrmOrderService orderService;
    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmContractService contractService;
    @Resource
    private CrmProductService productService;

    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    @PreAuthorize("@ss.hasPermission('crm:order:create')")
    public CommonResult<Long> createOrder(@Valid @RequestBody CrmOrderSaveReqVO createReqVO) {
        return success(orderService.createOrder(createReqVO, getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新订单")
    @PreAuthorize("@ss.hasPermission('crm:order:update')")
    public CommonResult<Boolean> updateOrder(@Valid @RequestBody CrmOrderSaveReqVO updateReqVO) {
        orderService.updateOrder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除订单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:order:delete')")
    public CommonResult<Boolean> deleteOrder(@RequestParam("id") Long id) {
        orderService.deleteOrder(id);
        return success(true);
    }

    @PostMapping("/submit")
    @Operation(summary = "提交订单审核")
    @Parameter(name = "id", description = "订单编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:order:approval')")
    public CommonResult<Boolean> submitOrder(@RequestParam("id") Long id) {
        orderService.submitOrder(id);
        return success(true);
    }

    @PostMapping("/approval")
    @Operation(summary = "发起订单审批")
    @Parameter(name = "id", description = "订单编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:order:approval')")
    public CommonResult<Boolean> startApproval(@RequestParam("id") Long id) {
        orderService.startApproval(id, getLoginUserId());
        return success(true);
    }

    @PostMapping("/complete")
    @Operation(summary = "完成订单")
    @Parameter(name = "id", description = "订单编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:order:update')")
    public CommonResult<Boolean> completeOrder(@RequestParam("id") Long id) {
        orderService.completeOrder(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得订单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('crm:order:query')")
    public CommonResult<CrmOrderRespVO> getOrder(@RequestParam("id") Long id) {
        CrmOrderDO order = orderService.getOrder(id);
        return success(buildOrderDetail(order));
    }

    private CrmOrderRespVO buildOrderDetail(CrmOrderDO order) {
        if (order == null) {
            return null;
        }
        CrmOrderRespVO orderVO = buildOrderDetailList(singletonList(order)).get(0);
        List<CrmOrderProductDO> orderProducts = orderService.getOrderProductListByOrderId(orderVO.getId());
        Map<Long, CrmProductDO> productMap = productService.getProductMap(
                convertSet(orderProducts, CrmOrderProductDO::getProductId));
        orderVO.setProducts(BeanUtils.toBean(orderProducts, CrmOrderRespVO.Product.class, productVO ->
                MapUtils.findAndThen(productMap, productVO.getProductId(),
                        product -> productVO.setProductName(product.getName())
                                .setProductNo(product.getNo()).setProductUnit(String.valueOf(product.getUnit())))));
        return orderVO;
    }

    @GetMapping("/page")
    @Operation(summary = "获得订单分页")
    @PreAuthorize("@ss.hasPermission('crm:order:query')")
    public CommonResult<PageResult<CrmOrderRespVO>> getOrderPage(@Valid CrmOrderPageReqVO pageVO) {
        PageResult<CrmOrderDO> pageResult = orderService.getOrderPage(pageVO, getLoginUserId());
        return success(BeanUtils.toBean(pageResult, CrmOrderRespVO.class).setList(buildOrderDetailList(pageResult.getList())));
    }

    @GetMapping("/page-by-customer")
    @Operation(summary = "获得订单分页，基于指定客户")
    public CommonResult<PageResult<CrmOrderRespVO>> getOrderPageByCustomer(@Valid CrmOrderPageReqVO pageVO) {
        Assert.notNull(pageVO.getCustomerId(), "客户编号不能为空");
        PageResult<CrmOrderDO> pageResult = orderService.getOrderPageByCustomerId(pageVO);
        return success(BeanUtils.toBean(pageResult, CrmOrderRespVO.class).setList(buildOrderDetailList(pageResult.getList())));
    }

    @GetMapping("/page-by-contract")
    @Operation(summary = "获得订单分页，基于指定合同")
    public CommonResult<PageResult<CrmOrderRespVO>> getOrderPageByContract(@Valid CrmOrderPageReqVO pageVO) {
        Assert.notNull(pageVO.getContractId(), "合同编号不能为空");
        PageResult<CrmOrderDO> pageResult = orderService.getOrderPageByContractId(pageVO);
        return success(BeanUtils.toBean(pageResult, CrmOrderRespVO.class).setList(buildOrderDetailList(pageResult.getList())));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出订单 Excel")
    @PreAuthorize("@ss.hasPermission('crm:order:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderExcel(@Valid CrmOrderPageReqVO exportReqVO,
                                  HttpServletResponse response) throws IOException {
        PageResult<CrmOrderDO> pageResult = orderService.getOrderPage(exportReqVO, getLoginUserId());
        ExcelUtils.write(response, "订单.xls", "数据", CrmOrderRespVO.class,
                BeanUtils.toBean(pageResult.getList(), CrmOrderRespVO.class));
    }

    private List<CrmOrderRespVO> buildOrderDetailList(List<CrmOrderDO> orderList) {
        if (CollUtil.isEmpty(orderList)) {
            return Collections.emptyList();
        }
        Map<Long, CrmCustomerDO> customerMap = customerService.getCustomerMap(
                convertSet(orderList, CrmOrderDO::getCustomerId));
        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(convertListByFlatMap(orderList,
                order -> Stream.of(NumberUtils.parseLong(order.getCreator()), order.getOwnerUserId())));
        Map<Long, CrmContractDO> contractMap = contractService.getContractMap(
                convertSet(orderList, CrmOrderDO::getContractId));
        return BeanUtils.toBean(orderList, CrmOrderRespVO.class, orderVO -> {
            MapUtils.findAndThen(customerMap, orderVO.getCustomerId(), customer -> orderVO.setCustomerName(customer.getName()));
            MapUtils.findAndThen(userMap, Long.parseLong(orderVO.getCreator()), user -> orderVO.setCreatorName(user.getNickname()));
            MapUtils.findAndThen(userMap, orderVO.getOwnerUserId(), user -> orderVO.setOwnerUserName(user.getNickname()));
            MapUtils.findAndThen(contractMap, orderVO.getContractId(), contract -> orderVO.setContractName(contract.getName()));
        });
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得订单精简列表", description = "只包含的订单，主要用于前端的下拉选项")
    @Parameter(name = "customerId", description = "客户编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:order:query')")
    public CommonResult<List<CrmOrderRespVO>> getOrderSimpleList(@RequestParam("customerId") Long customerId) {
        CrmOrderPageReqVO pageReqVO = new CrmOrderPageReqVO().setCustomerId(customerId);
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<CrmOrderDO> pageResult = orderService.getOrderPageByCustomerId(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(Collections.emptyList());
        }
        return success(convertList(pageResult.getList(), order -> new CrmOrderRespVO()
                .setId(order.getId()).setNo(order.getNo()).setStatus(order.getStatus())
                .setTotalPrice(order.getTotalPrice())));
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得订单统计")
    @PreAuthorize("@ss.hasPermission('crm:order:query')")
    public CommonResult<Map<String, Object>> getOrderStatistics() {
        return success(orderService.getOrderStatistics());
    }

}