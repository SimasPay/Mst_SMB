/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.TransactionTypeQuery;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class TransactionTypeDAO extends BaseDAO<TransactionType>{
	
	public List<TransactionType> get(TransactionTypeQuery query) {
		
		Criteria transactionCriteria = createCriteria();
		  //Before Correcting errors reported by Findbugs:
			//if((null != query.getServiceId()) && !("".equals(query.getServiceId()))){
		
		  //After Correcting the errors reported by Findbugs:only null check needed
		if(null != query.getServiceId()){
			Criteria serviceTransactionFromTransactionTypeCriteria = transactionCriteria.createCriteria("ServiceTransactionFromTransactionTypeID");
			Criteria serviceCriteria = serviceTransactionFromTransactionTypeCriteria.createCriteria("Service");
			serviceCriteria.add(Restrictions.eq(CmFinoFIX.CRService.FieldName_RecordID, query.getServiceId()));
		}
		
		if (StringUtils.isNotBlank(query.getTransactionName())) {
			transactionCriteria.add(Restrictions.eq(CmFinoFIX.CRTransactionType.FieldName_TransactionName, query.getTransactionName()).ignoreCase());
		}
		
        processBaseQuery(query, transactionCriteria);
        processPaging(query, transactionCriteria);
        
        transactionCriteria.addOrder(Order.desc(CmFinoFIX.CRService.FieldName_RecordID));
//        applyOrder(query, serviceCriteria);
        
		List<TransactionType> results = transactionCriteria.list(); 
		return results;
	}
	
	/**
	 * Returns the TransactionType by name
	 * @param transactionName
	 * @return
	 */
	public TransactionType getTransactionTypeByName(String transactionName) {
		TransactionType tt = null;
		if (StringUtils.isNotBlank(transactionName)) {
			TransactionTypeQuery query = new TransactionTypeQuery();
			query.setTransactionName(transactionName);
			List<TransactionType> lst = get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				tt = lst.get(0);
			}
		}
		return tt;
	}
}
