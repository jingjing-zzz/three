package com.meession.etm.module.crm.mq.message.business;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrmBusinessConvertOrderMessage {

    @NotNull(message = "商机编号不能为空")
    private Long businessId;

    @NotNull(message = "操作用户编号不能为空")
    private Long userId;

    private Long contractId;

}