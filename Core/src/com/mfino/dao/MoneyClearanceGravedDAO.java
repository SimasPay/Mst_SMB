package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MoneyClearanceGravedQuery;
import com.mfino.domain.MoneyClearanceGraved;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.UnregisteredTxnInfo;
/**
 * @author Satya
 *
 */

public class MoneyClearanceGravedDAO extends BaseDAO<MoneyClearanceGraved>{
	public List<MoneyClearanceGraved> get(MoneyClearanceGravedQuery query){
		Criteria criteria = createCriteria();
		
		if(query.getMdnId() != null){
			criteria.createAlias(MoneyClearanceGraved.FieldName_SubscriberMDNByMDNID, "SMDN");
			criteria.add(Restrictions.eq("SMDN." + SubscriberMdn.FieldName_RecordID, query.getMdnId()));
		}
		if(query.getPocketId() != null){
			criteria.createAlias(MoneyClearanceGraved.FieldName_Pocket, "PKT");
			criteria.add(Restrictions.eq("PKT."+Pocket.FieldName_RecordID, query.getPocketId()));
		}
		if(query.getSctlId() != null){
			criteria.createAlias(MoneyClearanceGraved.FieldName_SctlId, "SCTL");
			criteria.add(Restrictions.eq("SCTL."+ServiceChargeTxnLog.FieldName_RecordID, query.getSctlId()));
		}
		if(query.getRefundMdnId() != null){
			criteria.createAlias(MoneyClearanceGraved.FieldName_SubscriberMDNByRefundMDNID, "SMDN");
			criteria.add(Restrictions.eq("SMDN."+SubscriberMdn.FieldName_RecordID, query.getRefundMdnId()));
		}
		if(query.getRefundPocketId() != null){
			criteria.createAlias(MoneyClearanceGraved.FieldName_RefundPocketID, "PKT");
			criteria.add(Restrictions.eq("PKT."+Pocket.FieldName_RecordID, query.getRefundPocketId()));
		}
		if(query.getRefundSctlId() != null){
			criteria.createAlias(MoneyClearanceGraved.FieldName_RefundSctlID, "SCTL");
			criteria.add(Restrictions.eq("SCTL."+ServiceChargeTxnLog.FieldName_RecordID, query.getRefundSctlId()));
		}
		 processBaseQuery(query, criteria);

         // Paging
         processPaging(query, criteria);

         if(query.isIDOrdered()) {
           criteria.addOrder(Order.desc(UnregisteredTxnInfo.FieldName_RecordID));
         }
         
         //applying Order
         applyOrder(query, criteria);
         @SuppressWarnings("unchecked")
         List<MoneyClearanceGraved> results = criteria.list();

         return results;
	}
}
