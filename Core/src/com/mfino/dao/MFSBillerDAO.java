/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MFSBillerQuery;
import com.mfino.domain.MFSBiller;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class MFSBillerDAO extends BaseDAO<MFSBiller> {
	
	public List<MFSBiller> get(MFSBillerQuery query) {
		Criteria criteria = createCriteria();
		
		if (StringUtils.isNotBlank(query.getExactBillerName())) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRMFSBiller.FieldName_MFSBillerName, query.getExactBillerName()).ignoreCase());
		}
		if (StringUtils.isNotBlank(query.getBillerName())) {
			addLikeStartRestriction(criteria, CmFinoFIX.CRMFSBiller.FieldName_MFSBillerName, query.getBillerName());
		}
		
		if (StringUtils.isNotBlank(query.getBillerCode())) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRMFSBiller.FieldName_MFSBillerCode, query.getBillerCode()).ignoreCase());
		}
		
		if (StringUtils.isNotBlank(query.getBillerType())) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRMFSBiller.FieldName_MFSBillerType, query.getBillerType()).ignoreCase());
		}
		// Paging
		processPaging(query, criteria);

		@SuppressWarnings("unchecked")
			List<MFSBiller> lst = criteria.list();
			
		return lst;
	}
	
	public MFSBiller getByBillerCode(String billerCode)
	{
		Criteria criteria = createCriteria();
		
		if (StringUtils.isNotBlank(billerCode)) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRMFSBiller.FieldName_MFSBillerCode, billerCode).ignoreCase());
		}
		@SuppressWarnings("unchecked")
		List<MFSBiller> lst = criteria.list();
		if(lst.size() > 0)
		{
			return lst.get(0);
		}
		return null;
	}
	
    @Override
    public void save(MFSBiller mb) {
        if (mb.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            mb.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(mb);
    }

}
