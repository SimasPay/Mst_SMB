/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.MFSBillerQuery;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.MfsBiller;

/**
 * @author Bala Sunku
 *
 */
public class MFSBillerDAO extends BaseDAO<MfsBiller> {
	
	public List<MfsBiller> get(MFSBillerQuery query) {
		Criteria criteria = createCriteria();
		
		if (StringUtils.isNotBlank(query.getExactBillerName())) {
			criteria.add(Restrictions.eq(MfsBiller.FieldName_MFSBillerName, query.getExactBillerName()).ignoreCase());
		}
		if (StringUtils.isNotBlank(query.getBillerName())) {
			addLikeStartRestriction(criteria, MfsBiller.FieldName_MFSBillerName, query.getBillerName());
		}
		
		if (StringUtils.isNotBlank(query.getBillerCode())) {
			criteria.add(Restrictions.eq(MfsBiller.FieldName_MFSBillerCode, query.getBillerCode()).ignoreCase());
		}
		
		if (StringUtils.isNotBlank(query.getBillerType())) {
			criteria.add(Restrictions.eq(MfsBiller.FieldName_MFSBillerType, query.getBillerType()).ignoreCase());
		}
		if (query.getStartRegistrationDate() != null) {
            criteria.add(Restrictions.gt(MfsBiller.FieldName_CreateTime, query.getStartRegistrationDate()));
         }
       if (query.getEndRegistrationDate() != null) {
            criteria.add(Restrictions.lt(MfsBiller.FieldName_CreateTime, query.getEndRegistrationDate()));
      }
		// Paging
		processPaging(query, criteria);

		@SuppressWarnings("unchecked")
			List<MfsBiller> lst = criteria.list();
			
		return lst;
	}
	
	public MfsBiller getByBillerCode(String billerCode)
	{
		Criteria criteria = createCriteria();
		
		if (StringUtils.isNotBlank(billerCode)) {
			criteria.add(Restrictions.eq(MfsBiller.FieldName_MFSBillerCode, billerCode).ignoreCase());
		}
		@SuppressWarnings("unchecked")
		List<MfsBiller> lst = criteria.list();
		if(lst.size() > 0)
		{
			return lst.get(0);
		}
		return null;
	}
	
    @Override
    public void save(MfsBiller mb) {
        if (mb.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            mb.setMfinoServiceProvider(msp);
        }
        super.save(mb);
    }

}
