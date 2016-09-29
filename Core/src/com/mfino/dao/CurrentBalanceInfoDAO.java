package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;

import com.mfino.domain.CurrentBalanceInfo;

public class CurrentBalanceInfoDAO extends BaseDAO<CurrentBalanceInfo> {
	public List<CurrentBalanceInfo> get(){
		Criteria criteria = createCriteria();
		/*if (query.getServiceId() != null ) {
			criteria.createAlias(CmFinoFIX.CRMFATransactionInfo.FieldName_Service, "s");
			criteria.add(Restrictions.eq("s." + CmFinoFIX.CRService.FieldName_RecordID, query.getServiceId()));
		}		
		if (query.getTransactionTypeId() != null) {
			criteria.createAlias(CmFinoFIX.CRMFATransactionInfo.FieldName_TransactionType, "tt");
			criteria.add(Restrictions.eq("tt."+CmFinoFIX.CRTransactionType.FieldName_RecordID, query.getTransactionTypeId()));
		}
		if (query.getChannelCodeId() != null) {
			criteria.createAlias(CmFinoFIX.CRMFATransactionInfo.FieldName_ChannelCode, "cc");
			criteria.add(Restrictions.eq("cc."+CmFinoFIX.CRChannelCode.FieldName_RecordID, query.getChannelCodeId()));
		}
		if(query.getMfaMode()!=null){
			criteria.add(Restrictions.eq(CmFinoFIX.CRMFATransactionInfo.FieldName_MFAMode, query.getMfaMode()));
		}
		*/
		
		
		@SuppressWarnings("unchecked")
		List<CurrentBalanceInfo> results = criteria.list();
		return results;
	}
}
