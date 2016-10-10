package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.KYCLevelQuery;
import com.mfino.domain.KycLevel;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author sanjeev
 */
public class KYCLevelDAO extends BaseDAO<KycLevel> {
	
	@SuppressWarnings("unchecked")
	public List<KycLevel> get (KYCLevelQuery query){
	
		Criteria criteria = createCriteria();

        if (query.getKycLevel() != null) {
            criteria.add(Restrictions.eq(KycLevel.FieldName_KYCLevel, query.getKycLevel()));
        }
        
        if (query.getKycLevelName() != null) {
        	
            criteria.add(Restrictions.eq(KycLevel.FieldName_KYCLevelName, query.getKycLevelName()));
        }
        
        processBaseQuery(query, criteria);
        List<KycLevel> results = criteria.list();
        
        return results;
	}

	public KycLevel getByKycLevel(Long kyclevel) {
		Criteria criteria = createCriteria();
		 if (kyclevel == null) {
		 return null;
		 }
	  criteria.add(Restrictions.eq(KycLevel.FieldName_KYCLevel, kyclevel));
	  List<KycLevel> results = criteria.list();
	  if(results.size()>0){
		  return results.get(0);
	  }
      return null;     
		
	}
	
}
