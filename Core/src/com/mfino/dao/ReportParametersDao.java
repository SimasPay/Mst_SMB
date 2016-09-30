package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.ReportParameters;
import com.mfino.fix.CmFinoFIX;

/**
 * @author sasidhar
 *
 */
public class ReportParametersDao extends BaseDAO<ReportParameters>{
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings("unchecked")
	public ReportParameters getReportParameterByName(String parameterName){
		log.info("ReportParametersDao :: getSystemParameterByName() "+parameterName);
		
		if((null == parameterName) || ("".equals(parameterName))) return null;
		
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(ReportParameters.FieldName_ParameterName, parameterName).ignoreCase());
		
        List<ReportParameters> results = criteria.list();
        
        if((results != null) && (results.size() > 0)) return results.get(0);
        
        return null;
	}
	
}
