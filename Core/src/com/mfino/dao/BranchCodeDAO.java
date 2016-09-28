package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.QueryConstants;
import com.mfino.dao.query.BranchCodeQuery;
import com.mfino.domain.BranchCodes;
import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author srikanth
 */
public class BranchCodeDAO extends BaseDAO<BranchCodes> {

	public List<BranchCodes> get(BranchCodeQuery query) {
		Criteria criteria = createCriteria();

		
		
		if (query.getBranchName() != null) {
			criteria.add(Restrictions.eq(
					BranchCodes.FieldName_BranchName,
					query.getBranchName()).ignoreCase());
		}

		addOrder(QueryConstants.ASC_STRING, BranchCodes.FieldName_BranchCode, criteria);		
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<BranchCodes> results = criteria.list();

		return results;
	}

}
