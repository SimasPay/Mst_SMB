package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.FundDefinitionQuery;
import com.mfino.domain.FundDefinition;
import com.mfino.fix.CmFinoFIX;

public class FundDefinitionDAO extends BaseDAO<FundDefinition> {
	public List<FundDefinition> get(FundDefinitionQuery query) {
		Criteria criteria = createCriteria();
		if (query.getPurposeID() != null) {
			criteria.createAlias(CmFinoFIX.CRFundDefinition.FieldName_Purpose, "p");
			criteria.add(Restrictions.eq("p."+CmFinoFIX.CRPurpose.FieldName_RecordID,query.getPurposeID()));
		}

		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<FundDefinition> results = criteria.list();
		return results;
	}
}
