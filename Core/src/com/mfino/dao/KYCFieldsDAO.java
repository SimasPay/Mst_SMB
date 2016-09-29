package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.KYCFieldsquery;
import com.mfino.domain.KYCFields;
import com.mfino.domain.KYCLevel;

/**
 *
 * @author sanjeev
 */
public class KYCFieldsDAO extends BaseDAO<KYCFields> {
	
	@SuppressWarnings("unchecked")
	public List<KYCFields> get(KYCFieldsquery query){
	
		Criteria criteria = createCriteria();

        if (query.getkycFieldsLevelID() != null) {
        	criteria.createCriteria(KYCFields.FieldName_KYCLevelByKYCFieldsLevelID).add(Restrictions.le(KYCLevel.FieldName_KYCLevel, query.getkycFieldsLevelID()));
        	}
         processBaseQuery(query, criteria);
         List<KYCFields> results = criteria.list();
         return results;
	}
	
}
