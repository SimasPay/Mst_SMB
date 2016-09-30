/**
 * 
 */
package com.mfino.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.TransactionCharge;
import com.mfino.domain.TransactionChargeLog;

/**
 * @author Bala Sunku
 *
 */
public class TransactionChargeLogDAO extends BaseDAO<TransactionChargeLog> {
	
    @Override
    public void save(TransactionChargeLog tcl) {
        if (tcl.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            tcl.setMfinoServiceProvider(msp);
        }
        super.save(tcl);
    }
    
    @SuppressWarnings("unchecked")
    public List<TransactionChargeLog> getBySCTLID(long sctlId) {
    	List<TransactionChargeLog> result = null;
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(TransactionChargeLog.FieldName_ServiceChargeTransactionLogID, sctlId));
    	result = criteria.list();
    	return result;
    }
    
    @SuppressWarnings("unchecked")
    public BigDecimal getCharge(long sctlId, long tcId) {
    	BigDecimal charge = BigDecimal.ZERO;
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(TransactionChargeLog.FieldName_ServiceChargeTransactionLogID, sctlId));
    	criteria.createAlias(TransactionChargeLog.FieldName_TransactionCharge, "trxCharge");
    	criteria.add(Restrictions.eq("trxCharge."+TransactionCharge.FieldName_RecordID, tcId));
    	List<TransactionChargeLog> lst = criteria.list();
    	if (CollectionUtils.isNotEmpty(lst)) {
    		charge = lst.get(0).getCalculatedcharge();
    	}
    	return charge;
    }
    
   

	@SuppressWarnings("unchecked")
	public List<TransactionChargeLog> getByServiceChargeTransactionLogIDs(
			Long startSCTLID, Long endSCTLID) {
		if(startSCTLID!=null&&endSCTLID!=null){
		Criteria criteria = createCriteria();
    	criteria.add(Restrictions.between(TransactionChargeLog.FieldName_ServiceChargeTransactionLogID, startSCTLID,endSCTLID));
    	List<TransactionChargeLog> result = criteria.list();
    	return result;
		}
		return null;
	}

}
