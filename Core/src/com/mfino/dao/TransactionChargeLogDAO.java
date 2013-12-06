/**
 * 
 */
package com.mfino.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.TransactionChargeLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class TransactionChargeLogDAO extends BaseDAO<TransactionChargeLog> {
	
    @Override
    public void save(TransactionChargeLog tcl) {
        if (tcl.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            tcl.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(tcl);
    }
    
    @SuppressWarnings("unchecked")
    public List<TransactionChargeLog> getBySCTLID(long sctlId) {
    	List<TransactionChargeLog> result = null;
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionChargeLog.FieldName_ServiceChargeTransactionLogID, sctlId));
    	result = criteria.list();
    	return result;
    }
    
    @SuppressWarnings("unchecked")
    public BigDecimal getCharge(long sctlId, long tcId) {
    	BigDecimal charge = BigDecimal.ZERO;
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionChargeLog.FieldName_ServiceChargeTransactionLogID, sctlId));
    	criteria.add(Restrictions.eq(CmFinoFIX.CRTransactionChargeLog.FieldName_TransactionChargeID, tcId));
    	List<TransactionChargeLog> lst = criteria.list();
    	if (CollectionUtils.isNotEmpty(lst)) {
    		charge = lst.get(0).getCalculatedCharge();
    	}
    	return charge;
    }
    
   

	@SuppressWarnings("unchecked")
	public List<TransactionChargeLog> getByServiceChargeTransactionLogIDs(
			Long startSCTLID, Long endSCTLID) {
		if(startSCTLID!=null&&endSCTLID!=null){
		Criteria criteria = createCriteria();
    	criteria.add(Restrictions.between(CmFinoFIX.CRTransactionChargeLog.FieldName_ServiceChargeTransactionLogID, startSCTLID,endSCTLID));
    	List<TransactionChargeLog> result = criteria.list();
    	return result;
		}
		return null;
	}

}
