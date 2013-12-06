package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.RetiredCardPANInfoQuery;
import com.mfino.domain.RetiredCardPANInfo;
import com.mfino.fix.CmFinoFIX;

/**
*
* @author Satya
*/
public class RetiredCardPANInfoDAO extends BaseDAO<RetiredCardPANInfo>{
	public List<RetiredCardPANInfo> get(RetiredCardPANInfoQuery query){
		Criteria criteria = createCriteria();
		if(query.getRetireCount() != null){
			criteria.add(Restrictions.eq(CmFinoFIX.CRRetiredCardPANInfo.FieldName_RetireCount, query.getRetireCount()));
		}
		if (query.getCardPan() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRRetiredCardPANInfo.FieldName_CardPAN, query.getCardPan()));
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
		List<RetiredCardPANInfo> results = criteria.list();
		return results;
	}
}
