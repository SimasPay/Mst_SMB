/**
 * 
 */
package com.mfino.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.TransactionAmountDistributionQuery;
import com.mfino.domain.TransactionAmountDistributionLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class TransactionAmountDistributionLogDAO extends BaseDAO<TransactionAmountDistributionLog> {
	
    @Override
    public void save(TransactionAmountDistributionLog tadl) {
        if (tadl.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            tadl.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(tadl);
    }
    
    /**
     * Returns the List of Log entries based on the Service Charge Transaction Log Id.
     * @param sctlId
     * @return
     */
    public List<TransactionAmountDistributionLog> getLogEntriesBySCTLID(long sctlId) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionAmountDistributionLog.FieldName_ServiceChargeTransactionLogID, sctlId));
    	
    	@SuppressWarnings("unchecked")
    	List<TransactionAmountDistributionLog> lst = criteria.list();
    	
    	return lst;
    }

	@SuppressWarnings("unchecked")
	public List<TransactionAmountDistributionLog> get(TransactionAmountDistributionQuery query) {
		Criteria criteria = createCriteria();
		if(query.getPartner()!=null){
		criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionAmountDistributionLog.FieldName_Partner, query.getPartner()));
		}
		if(query.getServiceChargeTransactionLogID()!=null){
			criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionAmountDistributionLog.FieldName_ServiceChargeTransactionLogID, query.getServiceChargeTransactionLogID()));
		}
		if(query.getStatus()!=null){
			criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionAmountDistributionLog.FieldName_TADLStatus, query.getStatus()));
		}
		List<TransactionAmountDistributionLog> result = criteria.list();
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<TransactionAmountDistributionLog> getTransactionAmountDistributionLogBySCTLIds(
			Set<Long> sctlids) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.in(CmFinoFIX.CRTransactionAmountDistributionLog.FieldName_ServiceChargeTransactionLogID, sctlids));
		List<TransactionAmountDistributionLog> result = criteria.list();
		return result;
	}

}
