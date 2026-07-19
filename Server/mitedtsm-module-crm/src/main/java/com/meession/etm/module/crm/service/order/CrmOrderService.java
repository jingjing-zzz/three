package com.meession.etm.module.crm.service.order;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderPageReqVO;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.contract.CrmContractDO;
import com.meession.etm.module.crm.dal.dataobject.customer.CrmCustomerDO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderDO;
import com.meession.etm.module.crm.dal.dataobject.order.CrmOrderProductDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.meession.etm.framework.common.util.collection.CollectionUtils.convertMap;

public interface CrmOrderService {

    Long createOrder(@Valid CrmOrderSaveReqVO createReqVO, Long userId);

    void updateOrder(@Valid CrmOrderSaveReqVO updateReqVO);

    void deleteOrder(Long id);

    CrmOrderDO getOrder(Long id);

    CrmOrderDO validateOrder(Long id);

    List<CrmOrderDO> getOrderList(Collection<Long> ids);

    default Map<Long, CrmOrderDO> getOrderMap(Collection<Long> ids) {
        return convertMap(getOrderList(ids), CrmOrderDO::getId);
    }

    PageResult<CrmOrderDO> getOrderPage(CrmOrderPageReqVO pageReqVO, Long userId);

    PageResult<CrmOrderDO> getOrderPageByCustomerId(CrmOrderPageReqVO pageReqVO);

    PageResult<CrmOrderDO> getOrderPageByContractId(CrmOrderPageReqVO pageReqVO);

    List<CrmOrderProductDO> getOrderProductListByOrderId(Long orderId);

    Long getOrderCountByCustomerId(Long customerId);

    Long getOrderCountByContractId(Long contractId);

    void startApproval(Long orderId, Long userId);

    void submitOrder(Long orderId);

    void completeOrder(Long orderId);

    void updateOrderStatus(Long id, Integer status);

    Map<String, Object> getOrderStatistics();

}