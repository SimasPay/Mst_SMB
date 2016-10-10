/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.MfsDenominations;
import com.mfino.domain.MfsbillerPartnerMap;

/**
 * @author Satya
 *
 */
public class MFSDenominationsDAO extends BaseDAO<MfsDenominations> {
	
	public List<MfsDenominations> get(MFSDenominationsQuery query) {
		Criteria criteria = createCriteria();

		if (query.getMfsID() != null ) {
			criteria.add(Restrictions.eq(MfsDenominations.FieldName_MFSBillerPartnerByMFSID, query.getMfsID()));
		}

		criteria.addOrder(Order.asc(MfsDenominations.FieldName_DenominationAmount));
		
		@SuppressWarnings("unchecked")
			List<MfsDenominations> lst = criteria.list();
			
		return lst;
	}
	
    @Override
    public void save(MfsDenominations md) {
        if (md.getMfsid() == null) {
            MFSBillerPartnerDAO mbpDao = DAOFactory.getInstance().getMFSBillerPartnerDAO();
            MfsbillerPartnerMap mbp = mbpDao.getById(1);
            md.setMfsid(mbp.getId());
        }
        super.save(md);
    }

}