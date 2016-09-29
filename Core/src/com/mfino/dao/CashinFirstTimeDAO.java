package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.criterion.Restrictions;

import com.mfino.domain.CashinFirstTime;

public class CashinFirstTimeDAO extends BaseDAO<CashinFirstTime>{

    public CashinFirstTime getByMDN(String MDN, LockMode lockMode) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(CashinFirstTime.FieldName_MDN, MDN));
    	
        if(lockMode != null){
            criteria.setLockMode(lockMode);
        }
        
    	List<CashinFirstTime> cftList = criteria.list();
    	
    	if((null != cftList) && (cftList.size() > 0)){
    		return cftList.get(0);
    	}
    	
    	return null;
    }
    
    public CashinFirstTime getByMDN(String MDN) {
    	return getByMDN(MDN, null);
    }
}
