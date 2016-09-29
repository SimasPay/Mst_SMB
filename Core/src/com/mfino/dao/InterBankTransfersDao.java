package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.InterBankTransfersQuery;
import com.mfino.domain.InterbankTransfers;

/**
 * @author Sasi
 *
 */
public class InterBankTransfersDao extends BaseDAO<InterbankTransfers> {

	public List<InterbankTransfers> get(InterBankTransfersQuery query) {
		log.debug("InterBankTransfersDao :: get() BEGIN");
		
		Criteria criteria = createCriteria();
		
		if(null != query.getSctlId()){
			criteria.add(Restrictions.eq(InterbankTransfers.FieldName_SctlId, query.getSctlId()));
		}

		if(null != query.getTransferId()){
			criteria.add(Restrictions.eq(InterbankTransfers.FieldName_TransferID, query.getTransferId()));
		}
		
		processBaseQuery(query, criteria);
		processPaging(query, criteria);

		List<InterbankTransfers> results = criteria.list();
		
		log.debug("InterBankTransfersDao :: get() END");
		return results;
	}
}
