package com.meession.etm.module.crm.service.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Assert;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.number.MoneyUtils;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.bpm.api.task.BpmProcessInstanceApi;
import com.meession.etm.module.bpm.api.task.dto.BpmProcessInstanceCreateReqDTO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderPageReqVO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderDO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderProductDO;
import com.meession.etm.framework.redis.core.generator.NoGeneratorService;
import com.meession.etm.module.crm.dal.mysql.order.CrmOrderMapper;
import com.meession.etm.module.crm.dal.mysql.order.CrmOrderProductMapper;
import com.meession.etm.module.crm.enums.common.CrmBizTypeEnum;
import com.meession.etm.module.crm.enums.order.CrmOrderStatusEnum;
import com.meession.etm.module.crm.enums.permission.CrmPermissionLevelEnum;
import com.meession.etm.module.crm.framework.permission.core.annotations.CrmPermission;
import com.meession.etm.module.crm.service.customer.CrmCustomerService;
import com.meession.etm.module.crm.service.permission.CrmPermissionService;
import com.meession.etm.module.crm.service.permission.bo.CrmPermissionCreateReqBO;
import com.meession.etm.module.crm.service.product.CrmProductService;
import com.meession.etm.module.system.api.user.AdminUserApi;
import java.util.HashMap;
import java.util.Map;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static com.meession.etm.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.meession.etm.framework.common.util.collection.CollectionUtils.*;
import static com.meession.etm.module.crm.enums.ErrorCodeConstants.*;
import static com.meession.etm.module.crm.enums.LogRecordConstants.*;

@Service
@Validated
@Slf4j
public class CrmOrderServiceImpl implements CrmOrderService {

    public static final String PROCESS_KEY = "crm_order";

    @Resource
    private CrmOrderMapper orderMapper;
    @Resource
    private CrmOrderProductMapper orderProductMapper;

    @Resource
    private NoGeneratorService noGeneratorService;

    @Resource
    private BpmProcessInstanceApi processInstanceApi;

    @Resource
    private CrmPermissionService crmPermissionService;
    @Resource
    private CrmProductService productService;
    @Resource
    private CrmCustomerService customerService;
    @Resource
    private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_ORDER_TYPE, subType = CRM_ORDER_CREATE_SUB_TYPE, bizNo = "{{#order.id}}",
            success = CRM_ORDER_CREATE_SUCCESS)
    public Long createOrder(CrmOrderSaveReqVO createReqVO, Long userId) {
        List<CrmOrderProductDO> orderProducts = validateOrderProducts(createReqVO.getProducts());
        validateRelationDataExists(createReqVO);
        String no = noGeneratorService.generateOrderNo();
        if (orderMapper.selectByNo(no) != null) {
            throw exception(ORDER_NO_EXISTS);
        }

        CrmOrderDO order = BeanUtils.toBean(createReqVO, CrmOrderDO.class).setNo(no).setStatus(CrmOrderStatusEnum.DRAFT.getStatus());
        calculateTotalPrice(order, orderProducts);
        orderMapper.insert(order);
        if (CollUtil.isNotEmpty(orderProducts)) {
            orderProducts.forEach(item -> item.setOrderId(order.getId()));
            orderProductMapper.insertBatch(orderProducts);
        }

        crmPermissionService.createPermission(new CrmPermissionCreateReqBO().setUserId(order.getOwnerUserId())
                .setBizType(CrmBizTypeEnum.CRM_ORDER.getType()).setBizId(order.getId())
                .setLevel(CrmPermissionLevelEnum.OWNER.getLevel()));

        LogRecordContext.putVariable("order", order);
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_ORDER_TYPE, subType = CRM_ORDER_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = CRM_ORDER_UPDATE_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ORDER, bizId = "#updateReqVO.id", level = CrmPermissionLevelEnum.WRITE)
    public void updateOrder(CrmOrderSaveReqVO updateReqVO) {
        Assert.notNull(updateReqVO.getId(), "订单编号不能为空");
        updateReqVO.setOwnerUserId(null);
        CrmOrderDO oldOrder = validateOrderExists(updateReqVO.getId());

        List<CrmOrderProductDO> orderProducts = validateOrderProducts(updateReqVO.getProducts());
        validateRelationDataExists(updateReqVO);

        CrmOrderDO updateObj = BeanUtils.toBean(updateReqVO, CrmOrderDO.class);
        calculateTotalPrice(updateObj, orderProducts);
        orderMapper.updateById(updateObj);
        updateOrderProduct(updateReqVO.getId(), orderProducts);

        updateReqVO.setOwnerUserId(oldOrder.getOwnerUserId());
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldOrder, CrmOrderSaveReqVO.class));
        LogRecordContext.putVariable("orderNo", oldOrder.getNo());
    }

    private void updateOrderProduct(Long id, List<CrmOrderProductDO> newList) {
        List<CrmOrderProductDO> oldList = orderProductMapper.selectListByOrderId(id);
        List<List<CrmOrderProductDO>> diffList = diffList(oldList, newList,
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            diffList.get(0).forEach(o -> o.setOrderId(id));
            orderProductMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            orderProductMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            orderProductMapper.deleteByIds(convertSet(diffList.get(2), CrmOrderProductDO::getId));
        }
    }

    private void validateRelationDataExists(CrmOrderSaveReqVO reqVO) {
        if (reqVO.getCustomerId() != null) {
            customerService.validateCustomer(reqVO.getCustomerId());
        }
        if (reqVO.getOwnerUserId() != null) {
            adminUserApi.validateUser(reqVO.getOwnerUserId());
        }
    }

    private List<CrmOrderProductDO> validateOrderProducts(List<CrmOrderSaveReqVO.Product> list) {
        if (CollUtil.isEmpty(list)) {
            return ListUtil.empty();
        }
        productService.validProductList(convertSet(list, CrmOrderSaveReqVO.Product::getProductId));
        return convertList(list, o -> BeanUtils.toBean(o, CrmOrderProductDO.class,
                item -> {
                    item.setTotalPrice(MoneyUtils.priceMultiply(item.getProductPrice(), item.getCount()));
                    if (item.getTaxPercent() != null && item.getTaxPercent().compareTo(BigDecimal.ZERO) > 0) {
                        item.setTaxPrice(MoneyUtils.priceMultiply(item.getTotalPrice(), item.getTaxPercent().divide(new BigDecimal("100"))));
                    } else {
                        item.setTaxPrice(BigDecimal.ZERO);
                    }
                }));
    }

    private void calculateTotalPrice(CrmOrderDO order, List<CrmOrderProductDO> orderProducts) {
        order.setTotalProductPrice(getSumValue(orderProducts, CrmOrderProductDO::getTotalPrice, BigDecimal::add, BigDecimal.ZERO));
        BigDecimal discountPrice = MoneyUtils.priceMultiplyPercent(order.getTotalProductPrice(), order.getDiscountPercent());
        order.setTotalPrice(order.getTotalProductPrice().subtract(discountPrice));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = CRM_ORDER_TYPE, subType = CRM_ORDER_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = CRM_ORDER_DELETE_SUCCESS)
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ORDER, bizId = "#id", level = CrmPermissionLevelEnum.OWNER)
    public void deleteOrder(Long id) {
        CrmOrderDO order = validateOrderExists(id);

        orderMapper.deleteById(id);
        orderProductMapper.deleteByOrderId(id);
        crmPermissionService.deletePermission(CrmBizTypeEnum.CRM_ORDER.getType(), id);

        LogRecordContext.putVariable("orderNo", order.getNo());
    }

    private CrmOrderDO validateOrderExists(Long id) {
        CrmOrderDO order = orderMapper.selectById(id);
        if (order == null) {
            throw exception(ORDER_NOT_EXISTS);
        }
        return order;
    }

    @Override
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_ORDER, bizId = "#id", level = CrmPermissionLevelEnum.READ)
    public CrmOrderDO getOrder(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public CrmOrderDO validateOrder(Long id) {
        return validateOrderExists(id);
    }

    @Override
    public List<CrmOrderDO> getOrderList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return ListUtil.empty();
        }
        return orderMapper.selectByIds(ids);
    }

    @Override
    public PageResult<CrmOrderDO> getOrderPage(CrmOrderPageReqVO pageReqVO, Long userId) {
        return orderMapper.selectPage(pageReqVO, userId);
    }

    @Override
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_CUSTOMER, bizId = "#pageReqVO.customerId", level = CrmPermissionLevelEnum.READ)
    public PageResult<CrmOrderDO> getOrderPageByCustomerId(CrmOrderPageReqVO pageReqVO) {
        return orderMapper.selectPageByCustomerId(pageReqVO);
    }

    @Override
    @CrmPermission(bizType = CrmBizTypeEnum.CRM_CONTRACT, bizId = "#pageReqVO.contractId", level = CrmPermissionLevelEnum.READ)
    public PageResult<CrmOrderDO> getOrderPageByContractId(CrmOrderPageReqVO pageReqVO) {
        return orderMapper.selectPageByContractId(pageReqVO);
    }

    @Override
    public List<CrmOrderProductDO> getOrderProductListByOrderId(Long orderId) {
        return orderProductMapper.selectListByOrderId(orderId);
    }

    @Override
    public Long getOrderCountByCustomerId(Long customerId) {
        return orderMapper.selectCount(CrmOrderDO::getCustomerId, customerId);
    }

    @Override
    public Long getOrderCountByContractId(Long contractId) {
        return orderMapper.selectCount(CrmOrderDO::getContractId, contractId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(Long orderId) {
        CrmOrderDO order = validateOrderExists(orderId);
        CrmOrderStatusEnum currentStatus = CrmOrderStatusEnum.valueOf(order.getStatus());
        if (!currentStatus.canSubmit()) {
            throw exception(ORDER_STATUS_CANNOT_APPROVE);
        }
        orderMapper.updateById(new CrmOrderDO().setId(orderId).setStatus(CrmOrderStatusEnum.PENDING_AUDIT.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startApproval(Long orderId, Long userId) {
        CrmOrderDO order = validateOrderExists(orderId);
        CrmOrderStatusEnum currentStatus = CrmOrderStatusEnum.valueOf(order.getStatus());
        if (!currentStatus.canApprove()) {
            throw exception(ORDER_STATUS_CANNOT_APPROVE);
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("totalPrice", order.getTotalPrice());
        variables.put("customerId", order.getCustomerId());
        String processInstanceId = processInstanceApi.createProcessInstance(userId,
                new BpmProcessInstanceCreateReqDTO()
                        .setProcessDefinitionKey(PROCESS_KEY)
                        .setVariables(variables)
                        .setBusinessKey(String.valueOf(orderId)));
        orderMapper.updateById(new CrmOrderDO().setId(orderId).setProcessInstanceId(processInstanceId).setStatus(CrmOrderStatusEnum.AUDITING.getStatus()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long orderId) {
        CrmOrderDO order = validateOrderExists(orderId);
        CrmOrderStatusEnum currentStatus = CrmOrderStatusEnum.valueOf(order.getStatus());
        if (!currentStatus.canComplete()) {
            throw exception(ORDER_STATUS_TRANSITION_INVALID);
        }
        orderMapper.updateById(new CrmOrderDO().setId(orderId).setStatus(CrmOrderStatusEnum.COMPLETED.getStatus()));
    }

    @Override
    public void updateOrderStatus(Long id, Integer status) {
        CrmOrderDO order = validateOrderExists(id);
        CrmOrderStatusEnum currentStatus = CrmOrderStatusEnum.valueOf(order.getStatus());
        CrmOrderStatusEnum targetStatus = CrmOrderStatusEnum.valueOf(status);
        if (!isValidStatusTransition(currentStatus, targetStatus)) {
            throw exception(ORDER_STATUS_TRANSITION_INVALID);
        }
        orderMapper.updateById(new CrmOrderDO().setId(id).setStatus(status));
    }

    private boolean isValidStatusTransition(CrmOrderStatusEnum current, CrmOrderStatusEnum target) {
        if (current == target) {
            return true;
        }
        switch (current) {
            case DRAFT:
                return target == CrmOrderStatusEnum.PENDING_AUDIT || target == CrmOrderStatusEnum.CANCELLED;
            case PENDING_AUDIT:
                return target == CrmOrderStatusEnum.AUDITING || target == CrmOrderStatusEnum.REJECTED || target == CrmOrderStatusEnum.CANCELLED;
            case AUDITING:
                return target == CrmOrderStatusEnum.APPROVED || target == CrmOrderStatusEnum.REJECTED || target == CrmOrderStatusEnum.CANCELLED;
            case APPROVED:
                return target == CrmOrderStatusEnum.COMPLETED || target == CrmOrderStatusEnum.CANCELLED;
            case REJECTED:
                return target == CrmOrderStatusEnum.DRAFT || target == CrmOrderStatusEnum.CANCELLED;
            case COMPLETED:
                return false;
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }

    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        Long totalCount = orderMapper.selectCount(null);
        statistics.put("totalCount", totalCount);
        List<CrmOrderDO> allOrders = orderMapper.selectList();
        BigDecimal totalAmount = allOrders.stream()
                .map(CrmOrderDO::getTotalPrice)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalAmount", totalAmount);
        Long draftCount = orderMapper.selectCount(CrmOrderDO::getStatus, CrmOrderStatusEnum.DRAFT.getStatus());
        statistics.put("draftCount", draftCount);
        Long pendingAuditCount = orderMapper.selectCount(CrmOrderDO::getStatus, CrmOrderStatusEnum.PENDING_AUDIT.getStatus());
        statistics.put("pendingAuditCount", pendingAuditCount);
        Long auditingCount = orderMapper.selectCount(CrmOrderDO::getStatus, CrmOrderStatusEnum.AUDITING.getStatus());
        statistics.put("auditingCount", auditingCount);
        Long approvedCount = orderMapper.selectCount(CrmOrderDO::getStatus, CrmOrderStatusEnum.APPROVED.getStatus());
        statistics.put("approvedCount", approvedCount);
        Long rejectedCount = orderMapper.selectCount(CrmOrderDO::getStatus, CrmOrderStatusEnum.REJECTED.getStatus());
        statistics.put("rejectedCount", rejectedCount);
        Long completedCount = orderMapper.selectCount(CrmOrderDO::getStatus, CrmOrderStatusEnum.COMPLETED.getStatus());
        statistics.put("completedCount", completedCount);
        Long cancelledCount = orderMapper.selectCount(CrmOrderDO::getStatus, CrmOrderStatusEnum.CANCELLED.getStatus());
        statistics.put("cancelledCount", cancelledCount);
        return statistics;
    }

}