package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MFAAuthenticationQuery;
import com.mfino.domain.MFAAuthentication;
import com.mfino.fix.CmFinoFIX;

public class MFAAuthenticationDAO extends BaseDAO<MFAAuthentication>{
	public List<MFAAuthentication> get(MFAAuthenticationQuery query){
		Criteria criteria = createCriteria();
		if (query.getSctlId() != null ) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRMFAAuthentication.FieldName_SctlId, query.getSctlId()));
		}
		if(query.getMfaMode() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRMFAAuthentication.FieldName_MFAMode, query.getMfaMode()));
		}
		if(query.getMfaValue() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRMFAAuthentication.FieldName_MFAValue, query.getMfaValue()));
		}
		
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		
		if(query.isIDOrdered()) {
            criteria.addOrder(Order.desc(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_RecordID));
          }
          if(query.getSortString()!=null){
        	  criteria.addOrder(Order.asc(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_CreateTime));
          }
          
          //applying Order
          applyOrder(query, criteria);
          
		@SuppressWarnings("unchecked")
		List<MFAAuthentication> results = criteria.list();
		return results;
	}
}
	
