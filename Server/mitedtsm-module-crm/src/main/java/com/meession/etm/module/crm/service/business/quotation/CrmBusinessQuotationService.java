package com.meession.etm.module.crm.service.business.quotation;

import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.controller.admin.business.vo.quotation.CrmBusinessQuotationPageReqVO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationDO;
import com.meession.etm.module.crm.dal.dataobject.business.CrmBusinessQuotationItemDO;
import java.util.List;

public interface CrmBusinessQuotationService {
    /** 由当前商机产品生成报价草稿 */
    Long createQuotationDraft(Long businessId);
    /** 确认报价 */
    void confirmQuotation(Long quotationId);
    /** 作废报价 */
    void voidQuotation(Long quotationId);
    /** 获取报价详情（含产品项） */
    CrmBusinessQuotationDO getQuotation(Long id);
    /** 获取报价产品项列表 */
    List<CrmBusinessQuotationItemDO> getQuotationItems(Long quotationId);
    /** 报价分页 */
    PageResult<CrmBusinessQuotationDO> getQuotationPage(CrmBusinessQuotationPageReqVO reqVO);
    /** 获取商机的已确认报价 */
    CrmBusinessQuotationDO getLatestConfirmedQuotation(Long businessId);
}
