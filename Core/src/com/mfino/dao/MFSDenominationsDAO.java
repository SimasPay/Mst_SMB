/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ChargePricingQuery;
import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.ChargePricing;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.MFSDenominations;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Satya
 *
 */
public class MFSDenominationsDAO extends BaseDAO<MFSDenominations> {
	
	public List<MFSDenominations> get(MFSDenominationsQuery query) {
		Criteria criteria = createCriteria();

		if (query.getMfsID() != null ) {
			criteria.createAlias(CmFinoFIX.CRMFSDenominations.FieldName_MFSBillerPartnerByMFSID, "mfsid");
			criteria.add(Restrictions.eq("mfsid." + CmFinoFIX.CRMFSBillerPartner.FieldName_RecordID, query.getMfsID()));
		}

		criteria.addOrder(Order.asc(CmFinoFIX.CRMFSDenominations.FieldName_DenominationAmount));
		
		@SuppressWarnings("unchecked")
			List<MFSDenominations> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(MFSDenominations md) {
        if (md.getMFSBillerPartnerByMFSID() == null) {
            MFSBillerPartnerDAO mbpDao = DAOFactory.getInstance().getMFSBillerPartnerDAO();
            MFSBillerPartner mbp = mbpDao.getById(1);
            md.setMFSBillerPartnerByMFSID(mbp);
        }
        super.save(md);
    }

}