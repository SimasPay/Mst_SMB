/**
 * 
 */
package com.mfino.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.TransactionAmountDistributionQuery;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.TxnAmountDstrbLog;

/**
 * @author Bala Sunku
 *
 */
public class TransactionAmountDistributionLogDAO extends BaseDAO<TxnAmountDstrbLog> {
	
    @Override
    public void save(TxnAmountDstrbLog tadl) {
        if (tadl.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            tadl.setMfinoServiceProvider(msp);
        }
        super.save(tadl);
    }
    
    /**
     * Returns the List of Log entries based on the Service Charge Transaction Log Id.
     * @param sctlId
     * @return
     */
    public List<TxnAmountDstrbLog> getLogEntriesBySCTLID(long sctlId) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(TxnAmountDstrbLog.FieldName_ServiceChargeTransactionLogID, sctlId));
    	
    	@SuppressWarnings("unchecked")
    	List<TxnAmountDstrbLog> lst = criteria.list();
    	
    	return lst;
    }

	@SuppressWarnings("unchecked")
	public List<TxnAmountDstrbLog> get(TransactionAmountDistributionQuery query) {
		Criteria criteria = createCriteria();
		if(query.getPartner()!=null){
		criteria.add(Restrictions.eq(TxnAmountDstrbLog.FieldName_Partner, query.getPartner()));
		}
		if(query.getServiceChargeTransactionLogID()!=null){
			criteria.add(Restrictions.eq(TxnAmountDstrbLog.FieldName_ServiceChargeTransactionLogID, query.getServiceChargeTransactionLogID()));
		}
		if(query.getStatus()!=null){
			criteria.add(Restrictions.eq(TxnAmountDstrbLog.FieldName_TADLStatus, query.getStatus()));
		}
		List<TxnAmountDstrbLog> result = criteria.list();
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<TxnAmountDstrbLog> getTransactionAmountDistributionLogBySCTLIds(
			Set<Long> sctlids) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.in(TxnAmountDstrbLog.FieldName_ServiceChargeTransactionLogID, sctlids));
		List<TxnAmountDstrbLog> result = criteria.list();
		return result;
	}

}
