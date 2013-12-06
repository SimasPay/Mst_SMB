package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.query.SystemParametersQuery;
import com.mfino.domain.SystemParameters;
import com.mfino.fix.CmFinoFIX;

/**
 * @author sasidhar
 *
 */
public class SystemParametersDao extends BaseDAO<SystemParameters>{
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public SystemParameters getSystemParameterByName(String parameterName){
		log.info("SystemParametersDao :: getSystemParameterByName() "+parameterName);
		
		if((null == parameterName) || ("".equals(parameterName))) return null;
		
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRSystemParameters.FieldName_ParameterName, parameterName).ignoreCase());
		
        List<SystemParameters> results = criteria.list();
        
        if((results != null) && (results.size() > 0)) return results.get(0);
        
        return null;
	}
	
	
	public List<SystemParameters> get(SystemParametersQuery query) {

        Criteria criteria = createCriteria();

        if (StringUtils.isNotBlank(query.getParameterName())) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSystemParameters.FieldName_ParameterName, query.getParameterName()).ignoreCase());
        }
        if (StringUtils.isNotBlank(query.getParemeterValue())) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSystemParameters.FieldName_ParameterValue, query.getParemeterValue()).ignoreCase());
        }
        if (StringUtils.isNotBlank(query.getDescription())) {
            criteria.add(Restrictions.like(CmFinoFIX.CRSystemParameters.FieldName_Description, query.getDescription()).ignoreCase());
        }
        
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<SystemParameters> results = criteria.list();

        return results;
    }
	
}
