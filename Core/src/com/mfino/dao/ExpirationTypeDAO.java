package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ExpirationTypeQuery;
import com.mfino.domain.ExpirationType;
import com.mfino.fix.CmFinoFIX;

public class ExpirationTypeDAO extends BaseDAO<ExpirationType> {

	public List<ExpirationType> get(ExpirationTypeQuery query) {

			Criteria criteria = createCriteria();
			if (query.getExpiryType() != null) {
				criteria.add(Restrictions.eq(
						CmFinoFIX.CRExpirationType.FieldName_ExpiryType,	query.getExpiryType()));
			}
			
			processBaseQuery(query, criteria);
			processPaging(query, criteria);
			@SuppressWarnings("unchecked")
			List<ExpirationType> results = criteria.list();
			return results;
	}

}
