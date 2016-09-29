/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.KtpDetailsQuery;
import com.mfino.domain.KtpDetails;

/**
 * @author Sunil
 *
 */
public class KtpDetailsDAO extends BaseDAO<KtpDetails> {

	@SuppressWarnings("unchecked")
	public List<KtpDetails> get(KtpDetailsQuery query){
	
		Criteria criteria = createCriteria();
		
		if(null != query.getId()) {
			
			criteria.add(Restrictions.eq(KtpDetails.FieldName_RecordID,query.getId()));
		}
		
		List<KtpDetails> results = criteria.list();
        
		return results;
	}
	@SuppressWarnings("unchecked")
	public List<KtpDetails> getByMDN(KtpDetailsQuery query){
	
		Criteria criteria = createCriteria();
		
		if(null != query.getMdn()) {
			
			criteria.add(Restrictions.eq(KtpDetails.FieldName_MDN,query.getMdn()));
		}
		if(query.getOrder()!=null){
			if(query.getOrder().equalsIgnoreCase("desc")){
				criteria.addOrder(Order.desc(KtpDetails.FieldName_RecordID));
			}else if(query.getOrder().equalsIgnoreCase("asc")){
				criteria.addOrder(Order.asc(KtpDetails.FieldName_RecordID));
			}
		}
		
		List<KtpDetails> results = criteria.list();
        
		return results;
	}
}
