package com.meession.etm.module.crm.service.workorder;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.workorder.vo.CrmWorkOrderPageReqVO;
import com.meession.etm.module.crm.controller.admin.workorder.vo.CrmWorkOrderSaveReqVO;
import com.meession.etm.module.crm.controller.admin.workorder.vo.CrmWorkOrderStatisticsRespVO;
import com.meession.etm.module.crm.controller.admin.workorder.vo.CrmWorkOrderUpdateStatusReqVO;
import com.meession.etm.module.crm.dal.dataobject.workorder.CrmWorkOrderDO;

import java.util.List;

public interface CrmWorkOrderService {

    Long createWorkOrder(CrmWorkOrderSaveReqVO createReqVO, Long userId);

    void updateWorkOrder(CrmWorkOrderSaveReqVO updateReqVO);

    void updateWorkOrderStatus(CrmWorkOrderUpdateStatusReqVO reqVO, Long userId);

    void deleteWorkOrder(Long id);

    CrmWorkOrderDO getWorkOrder(Long id);

    PageResult<CrmWorkOrderDO> getWorkOrderPage(CrmWorkOrderPageReqVO pageReqVO);

    List<CrmWorkOrderDO> getWorkOrderListByCustomerId(Long customerId);

    CrmWorkOrderStatisticsRespVO getWorkOrderStatistics();

}