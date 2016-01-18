/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.KtpDetailsQuery;
import com.mfino.domain.KtpDetails;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Sunil
 *
 */
public class KtpDetailsDAO extends BaseDAO<KtpDetails> {

	@SuppressWarnings("unchecked")
	public List<KtpDetails> get(KtpDetailsQuery query){
	
		Criteria criteria = createCriteria();
		
		if(null != query.getId()) {
			
			criteria.add(Restrictions.eq(CmFinoFIX.CRKtpDetails.FieldName_RecordID,query.getId()));
		}
		
		List<KtpDetails> results = criteria.list();
        
		return results;
	}
}
