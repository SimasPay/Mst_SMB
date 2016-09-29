/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.MFSDenominations;
import com.mfino.domain.MfsbillerPartnerMap;

/**
 * @author Satya
 *
 */
public class MFSDenominationsDAO extends BaseDAO<MFSDenominations> {
	
	public List<MFSDenominations> get(MFSDenominationsQuery query) {
		Criteria criteria = createCriteria();

		if (query.getMfsID() != null ) {
			criteria.add(Restrictions.eq(MFSDenominations.FieldName_MFSBillerPartnerByMFSID, query.getMfsID()));
		}

		criteria.addOrder(Order.asc(MFSDenominations.FieldName_DenominationAmount));
		
		@SuppressWarnings("unchecked")
			List<MFSDenominations> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(MFSDenominations md) {
        if (md.getMfsid() == null) {
            MFSBillerPartnerDAO mbpDao = DAOFactory.getInstance().getMFSBillerPartnerDAO();
            MfsbillerPartnerMap mbp = mbpDao.getById(1);
            md.setMfsid(mbp.getId());
        }
        super.save(md);
    }

}