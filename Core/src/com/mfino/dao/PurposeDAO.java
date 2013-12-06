package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.mfino.dao.query.PurposeQuery;
import com.mfino.domain.Purpose;
import com.mfino.fix.CmFinoFIX;

public class PurposeDAO extends BaseDAO<Purpose> {
	public List<Purpose> get(PurposeQuery query) {
		Criteria criteria = createCriteria();
		if (query.getCode() != null) {
			criteria.add(Restrictions.eq(
					CmFinoFIX.CRPurpose.FieldName_Code,
					query.getCode()));
		}
		if(query.getMultiCode()!=null){
			Disjunction finalDisjunction = Restrictions.disjunction();
			for(int i=0;i<query.getMultiCode().length;i++){
				SimpleExpression se = Restrictions.eq(CmFinoFIX.CRPurpose.FieldName_Code, query.getMultiCode()[i]).ignoreCase();    
				finalDisjunction.add(se);  
			}
			criteria.add(finalDisjunction);
		}
		
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<Purpose> results = criteria.list();
		return results;
	}
}
