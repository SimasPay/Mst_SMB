package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MoneyClearanceGravedQuery;
import com.mfino.domain.MoneyClearanceGraved;
import com.mfino.fix.CmFinoFIX;
/**
 * @author Satya
 *
 */

public class MoneyClearanceGravedDAO extends BaseDAO<MoneyClearanceGraved>{
	public List<MoneyClearanceGraved> get(MoneyClearanceGravedQuery query){
		Criteria criteria = createCriteria();
		
		if(query.getMdnId() != null){
			criteria.createAlias(CmFinoFIX.CRMoneyClearanceGraved.FieldName_SubscriberMDNByMDNID, "SMDN");
			criteria.add(Restrictions.eq("SMDN." + CmFinoFIX.CRSubscriberMDN.FieldName_RecordID, query.getMdnId()));
		}
		if(query.getPocketId() != null){
			criteria.createAlias(CmFinoFIX.CRMoneyClearanceGraved.FieldName_Pocket, "PKT");
			criteria.add(Restrictions.eq("PKT."+CmFinoFIX.CRPocket.FieldName_RecordID, query.getPocketId()));
		}
		if(query.getSctlId() != null){
			criteria.createAlias(CmFinoFIX.CRMoneyClearanceGraved.FieldName_SctlId, "SCTL");
			criteria.add(Restrictions.eq("SCTL."+CmFinoFIX.CRServiceChargeTransactionLog.FieldName_RecordID, query.getSctlId()));
		}
		if(query.getRefundMdnId() != null){
			criteria.createAlias(CmFinoFIX.CRMoneyClearanceGraved.FieldName_SubscriberMDNByRefundMDNID, "SMDN");
			criteria.add(Restrictions.eq("SMDN."+CmFinoFIX.CRSubscriberMDN.FieldName_RecordID, query.getRefundMdnId()));
		}
		if(query.getRefundPocketId() != null){
			criteria.createAlias(CmFinoFIX.CRMoneyClearanceGraved.FieldName_RefundPocketID, "PKT");
			criteria.add(Restrictions.eq("PKT."+CmFinoFIX.CRPocket.FieldName_RecordID, query.getRefundPocketId()));
		}
		if(query.getRefundSctlId() != null){
			criteria.createAlias(CmFinoFIX.CRMoneyClearanceGraved.FieldName_RefundSctlID, "SCTL");
			criteria.add(Restrictions.eq("SCTL."+CmFinoFIX.CRServiceChargeTransactionLog.FieldName_RecordID, query.getRefundSctlId()));
		}
		 processBaseQuery(query, criteria);

         // Paging
         processPaging(query, criteria);

         if(query.isIDOrdered()) {
           criteria.addOrder(Order.desc(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_RecordID));
         }
         
         //applying Order
         applyOrder(query, criteria);
         @SuppressWarnings("unchecked")
         List<MoneyClearanceGraved> results = criteria.list();

         return results;
	}
}
