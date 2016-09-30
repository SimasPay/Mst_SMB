/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SharePartnerQuery;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.SharePartner;
import com.mfino.domain.TransactionCharge;

/**
 * @author Bala Sunku
 *
 */
public class SharePartnerDAO extends BaseDAO<SharePartner> {
	
	public List<SharePartner> get(SharePartnerQuery query) {
		Criteria criteria = createCriteria();

		if (query.getTransactionChargeId() != null ) {
			criteria.createAlias(SharePartner.FieldName_TransactionCharge, "tc");
			criteria.add(Restrictions.eq("tc." + TransactionCharge.FieldName_RecordID, query.getTransactionChargeId()));
		}

		criteria.addOrder(Order.asc(SharePartner.FieldName_RecordID));
		
		@SuppressWarnings("unchecked")
			List<SharePartner> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(SharePartner sp) {
        if (sp.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            sp.setMfinoServiceProvider(msp);
        }
        super.save(sp);
    }

}
