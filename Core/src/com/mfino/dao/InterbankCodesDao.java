package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.InterBankCodesQuery;
import com.mfino.domain.InterbankCodes;

/**
 * @author Sasi
 * 
 */
public class InterbankCodesDao extends BaseDAO<InterbankCodes> {

	public List<InterbankCodes> get(InterBankCodesQuery query) {
		log.debug("InterBankCodesDao :: get() BEGIN");
		
		Criteria criteria = createCriteria();
		
		if((null != query.getBankCode()) && !("".equals(query.getBankCode()))) {
			criteria.add(Restrictions.eq(InterbankCodes.FieldName_BankCode, query.getBankCode()).ignoreCase());
		}

		processBaseQuery(query, criteria);
		processPaging(query, criteria);

		List<InterbankCodes> results = criteria.list();
		
		log.debug("InterBankCodesDao :: get() END");
		return results;
	}
}
