package com.mfino.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.Service;
import com.mfino.domain.ServiceTransaction;
import com.mfino.domain.TransactionType;

/**
 * @author Bala Sunku
 *
 */
public class ServiceTransactionDAO extends BaseDAO<ServiceTransaction> {
	public static final String TRANSACTION_TYPE_TABLE_NAME = "Transaction_type";
	
	public ServiceTransaction getServiceTransaction(Long serviceId, Long transactionTypeId) {
		ServiceTransaction st = null;
		Criteria criteria = createCriteria();
		
		if (serviceId != null) {
			criteria.createAlias(ServiceTransaction.FieldName_Service, "s");
			criteria.add(Restrictions.eq("s."+Service.FieldName_RecordID, serviceId));
			//criteria.add(Restrictions.eq(ServiceTransaction.FieldName_Service, sDAO.getById(serviceId)));
		}
		if (transactionTypeId != null) {
			criteria.createAlias(ServiceTransaction.FieldName_TransactionType, "t");
			criteria.add(Restrictions.eq("t." + TransactionType.FieldName_RecordID, transactionTypeId));
			//criteria.add(Restrictions.eq(ServiceTransaction.FieldName_TransactionType, ttDAO.getById(transactionTypeId)));
		}
		
		@SuppressWarnings("unchecked")
		List<ServiceTransaction> lst = criteria.list();
		if (CollectionUtils.isNotEmpty(lst)) {
			st = lst.get(0);
		}
		
		return st;
	}
	
	public List<ServiceTransaction> getServiceTransactions(Long serviceId, Long transactionTypeId) {
		Criteria criteria = createCriteria();
		
		if (serviceId != null) {
			criteria.createAlias(ServiceTransaction.FieldName_Service, "s");
			criteria.add(Restrictions.eq("s."+Service.FieldName_RecordID, serviceId));
//			criteria.add(Restrictions.eq(ServiceTransaction.FieldName_Service, sDAO.getById(serviceId)));
		}
		if (transactionTypeId != null) {
			criteria.createAlias(ServiceTransaction.FieldName_TransactionType, "t");
			criteria.add(Restrictions.eq("t." + TransactionType.FieldName_RecordID, transactionTypeId));			
//			criteria.add(Restrictions.eq(ServiceTransaction.FieldName_TransactionType, ttDAO.getById(transactionTypeId)));
		}
		
		@SuppressWarnings("unchecked")
		List<ServiceTransaction> lst = criteria.list();
		
		return lst;
	}
	
	public Map<Long, String> getTransactions(Long serviceId) {
		Map<Long, String> result = new HashMap<Long, String>();
		
		Criteria criteria = createCriteria();
		
		if (serviceId != null) {
			criteria.createAlias(ServiceTransaction.FieldName_Service, "s");
			criteria.add(Restrictions.eq("s."+Service.FieldName_RecordID, serviceId));
//			criteria.add(Restrictions.eq(ServiceTransaction.FieldName_Service, sDAO.getById(serviceId)));
			
			@SuppressWarnings("unchecked")
			List<ServiceTransaction> lst = criteria.list();

			if (CollectionUtils.isNotEmpty(lst)) {
				for (ServiceTransaction st: lst) {
					TransactionType tt = st.getTransactionType();
					if (tt != null) {
						result.put(tt.getId().longValue(), tt.getDisplayname());
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the Is Reversed Allowed value for the given service and transaction type.
	 * @param serviceId
	 * @param transactionTypeId
	 * @return
	 */
	public boolean isReverseAllowed(Long serviceId, Long transactionTypeId) {
		boolean result = false;
		ServiceTransaction st = getServiceTransaction(serviceId, transactionTypeId);
		if (st != null && st.getIsreverseallowed() != null) {
			result = st.getIsreverseallowed() != 0;
		}
		return result;
	}

}
