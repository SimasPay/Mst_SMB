package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.FundDefinitionQuery;
import com.mfino.domain.FundDefinition;

public class FundDefinitionDAO extends BaseDAO<FundDefinition> {
	public List<FundDefinition> get(FundDefinitionQuery query) {
		Criteria criteria = createCriteria();
		if (query.getPurposeID() != null) {
			criteria.add(Restrictions.eq(FundDefinition.FieldName_PurposeID, query.getPurposeID()));
		}

		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<FundDefinition> results = criteria.list();
		return results;
	}
}
