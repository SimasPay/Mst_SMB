package com.mfino.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.AutoReversals;
import com.mfino.fix.CmFinoFIX.CRAutoReversals;

/**
 * 
 * @author Sasi
 *
 */
public class AutoReversalsDao extends BaseDAO<AutoReversals> {

	public AutoReversals getBySctlId(Long sctlId){
		AutoReversals autoReversal = null;
		
		Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(AutoReversals.FieldName_SctlId, sctlId));
        autoReversal = (AutoReversals) criteria.uniqueResult();
		
		return autoReversal;
	}
	
	public Collection<AutoReversals> getAutoReversalsWithStatus(Collection<Integer> statuses){
		log.info("AutoReversalDao : getAutoReversalsWithStatus statuses="+statuses);
		List<AutoReversals> autoReversals = new ArrayList<AutoReversals>();
		
		if((null != statuses) && (statuses.size() > 0)){
			Criteria criteria = createCriteria();
			criteria.add(Restrictions.in(CRAutoReversals.FieldName_AutoRevStatus, statuses));
			autoReversals = criteria.list();
		}
		
		return autoReversals;
	}
}
