package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.KYCFieldsquery;
import com.mfino.domain.KycFields;
import com.mfino.domain.KycLevel;

/**
 *
 * @author sanjeev
 */
public class KYCFieldsDAO extends BaseDAO<KycFields> {
	
	@SuppressWarnings("unchecked")
	public List<KycFields> get(KYCFieldsquery query){
	
		Criteria criteria = createCriteria();

        if (query.getkycFieldsLevelID() != null) {
        	criteria.createCriteria(KycFields.FieldName_KYCLevelByKYCFieldsLevelID).add(Restrictions.le(KycLevel.FieldName_KYCLevel, query.getkycFieldsLevelID()));
        	}
         processBaseQuery(query, criteria);
         List<KycFields> results = criteria.list();
         return results;
	}
	
}
