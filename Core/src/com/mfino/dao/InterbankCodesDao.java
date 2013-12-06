package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.InterBankCodesQuery;
import com.mfino.domain.InterBankCode;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Sasi
 * 
 */
public class InterbankCodesDao extends BaseDAO<InterBankCode> {

	public List<InterBankCode> get(InterBankCodesQuery query) {
		log.debug("InterBankCodesDao :: get() BEGIN");
		
		Criteria criteria = createCriteria();
		
		if((null != query.getBankCode()) && !("".equals(query.getBankCode()))) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRInterBankCode.FieldName_BankCode, query.getBankCode()).ignoreCase());
		}

		processBaseQuery(query, criteria);
		processPaging(query, criteria);

		List<InterBankCode> results = criteria.list();
		
		log.debug("InterBankCodesDao :: get() END");
		return results;
	}
}
