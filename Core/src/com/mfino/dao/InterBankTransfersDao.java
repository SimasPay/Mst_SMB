package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.InterBankTransfersQuery;
import com.mfino.domain.InterbankTransfer;

/**
 * @author Sasi
 *
 */
public class InterBankTransfersDao extends BaseDAO<InterbankTransfer> {

	public List<InterbankTransfer> get(InterBankTransfersQuery query) {
		log.debug("InterBankTransfersDao :: get() BEGIN");
		
		Criteria criteria = createCriteria();
		
		if(null != query.getSctlId()){
			criteria.add(Restrictions.eq(InterbankTransfer.FieldName_SctlId, query.getSctlId()));
		}

		if(null != query.getTransferId()){
			criteria.add(Restrictions.eq(InterbankTransfer.FieldName_TransferID, query.getTransferId()));
		}
		
		processBaseQuery(query, criteria);
		processPaging(query, criteria);

		List<InterbankTransfer> results = criteria.list();
		
		log.debug("InterBankTransfersDao :: get() END");
		return results;
	}
}
