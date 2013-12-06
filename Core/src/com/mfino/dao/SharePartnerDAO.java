/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SharePartnerQuery;
import com.mfino.domain.SharePartner;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class SharePartnerDAO extends BaseDAO<SharePartner> {
	
	public List<SharePartner> get(SharePartnerQuery query) {
		Criteria criteria = createCriteria();

		if (query.getTransactionChargeId() != null ) {
			criteria.createAlias(CmFinoFIX.CRSharePartner.FieldName_TransactionCharge, "tc");
			criteria.add(Restrictions.eq("tc." + CmFinoFIX.CRTransactionCharge.FieldName_RecordID, query.getTransactionChargeId()));
		}

		criteria.addOrder(Order.asc(CmFinoFIX.CRSharePartner.FieldName_RecordID));
		
		@SuppressWarnings("unchecked")
			List<SharePartner> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(SharePartner sp) {
        if (sp.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            sp.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(sp);
    }

}
