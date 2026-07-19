package com.meession.etm.module.crm.mq.consumer.business;

import cn.hutool.core.collection.CollUtil;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.module.crm.controller.admin.order.vo.order.CrmOrderSaveReqVO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessDO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessProductDO;
import com.meession.etm.module.crm.mq.message.business.CrmBusinessConvertOrderMessage;
import com.meession.etm.module.crm.service.business.CrmBusinessService;
import com.meession.etm.module.crm.service.order.CrmOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.meession.etm.framework.common.util.collection.CollectionUtils.convertList;

@Component
@Slf4j
public class CrmBusinessConvertOrderConsumer {

    @Resource
    private CrmBusinessService businessService;

    @Resource
    private CrmOrderService orderService;

    @EventListener
    public void onMessage(CrmBusinessConvertOrderMessage message) {
        log.info("[onMessage][商机转订单消息内容({})]", message);

        CrmBusinessDO business = businessService.getBusiness(message.getBusinessId());
        if (business == null) {
            log.warn("[onMessage][商机不存在，businessId={}]", message.getBusinessId());
            return;
        }

        List<CrmBusinessProductDO> businessProducts = businessService.getBusinessProductListByBusinessId(message.getBusinessId());

        CrmOrderSaveReqVO orderSaveReqVO = new CrmOrderSaveReqVO();
        orderSaveReqVO.setCustomerId(business.getCustomerId());
        orderSaveReqVO.setOwnerUserId(business.getOwnerUserId());
        orderSaveReqVO.setOrderTime(java.time.LocalDateTime.now());
        orderSaveReqVO.setDiscountPercent(business.getDiscountPercent());
        orderSaveReqVO.setRemark("从商机转化: " + business.getName());

        if (message.getContractId() != null) {
            orderSaveReqVO.setContractId(message.getContractId());
        }

        if (CollUtil.isNotEmpty(businessProducts)) {
            List<CrmOrderSaveReqVO.Product> products = convertList(businessProducts, o -> {
                CrmOrderSaveReqVO.Product product = BeanUtils.toBean(o, CrmOrderSaveReqVO.Product.class);
                if (o.getBusinessPrice() != null) {
                    product.setProductPrice(o.getBusinessPrice());
                }
                return product;
            });
            orderSaveReqVO.setProducts(products);
        }

        orderService.createOrder(orderSaveReqVO, message.getUserId());

        log.info("[onMessage][商机转订单成功，businessId={}]", message.getBusinessId());
    }

}