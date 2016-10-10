package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MFAAuthenticationQuery;
import com.mfino.domain.MfaAuthentication;
import com.mfino.domain.UnregisteredTxnInfo;

public class MFAAuthenticationDAO extends BaseDAO<MfaAuthentication>{
	public List<MfaAuthentication> get(MFAAuthenticationQuery query){
		Criteria criteria = createCriteria();
		if (query.getSctlId() != null ) {
			criteria.add(Restrictions.eq(MfaAuthentication.FieldName_SctlId, query.getSctlId()));
		}
		if(query.getMfaMode() != null) {
			criteria.add(Restrictions.eq(MfaAuthentication.FieldName_MFAMode, query.getMfaMode()));
		}
		if(query.getMfaValue() != null) {
			criteria.add(Restrictions.eq(MfaAuthentication.FieldName_MFAValue, query.getMfaValue()));
		}
		
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		
		if(query.isIDOrdered()) {
            criteria.addOrder(Order.desc(UnregisteredTxnInfo.FieldName_RecordID));
          }
          if(query.getSortString()!=null){
        	  criteria.addOrder(Order.asc(UnregisteredTxnInfo.FieldName_CreateTime));
          }
          
          //applying Order
          applyOrder(query, criteria);
          
		@SuppressWarnings("unchecked")
		List<MfaAuthentication> results = criteria.list();
		return results;
	}
}
	
