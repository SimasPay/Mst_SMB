
package com.mfino.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.IntegrationPartnerMappingQuery;
import com.mfino.domain.IntegrationPartnerMap;
import com.mfino.domain.MfsBiller;
import com.mfino.domain.Partner;


public class IntegrationPartnerMappingDAO extends BaseDAO<IntegrationPartnerMap> {

    public IntegrationPartnerMap getByInstitutionID(String institutionID) {
    	if(institutionID==null)
    		return null;
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(IntegrationPartnerMap.FieldName_InstitutionID, institutionID).ignoreCase());
        return (IntegrationPartnerMap) criteria.uniqueResult();
    }
    
    public IntegrationPartnerMap getByIntegrationName(String integrationName) {
    	IntegrationPartnerMap ipMapping = null;
    	if (StringUtils.isBlank(integrationName)) {
    		return null;
    	}
    	IntegrationPartnerMappingQuery query = new IntegrationPartnerMappingQuery();
    	query.setIntegrationName(integrationName);
    	List<IntegrationPartnerMap> results = get(query);
    	if (CollectionUtils.isNotEmpty(results)) {
    		ipMapping = results.get(0);
    	}
        return ipMapping;
    }
    
        
    public List<IntegrationPartnerMap> get(IntegrationPartnerMappingQuery query) {

        Criteria criteria = createCriteria();

        if (StringUtils.isNotBlank(query.getInstitutionID())) {
            criteria.add(Restrictions.eq(IntegrationPartnerMap.FieldName_InstitutionID, query.getInstitutionID()).ignoreCase());
        }
        if (StringUtils.isNotBlank(query.getIntegrationName())) {
            criteria.add(Restrictions.eq(IntegrationPartnerMap.FieldName_IntegrationName, query.getIntegrationName()).ignoreCase());
        }
        if (query.getPartnerID() != null) {
        	Partner partner = DAOFactory.getInstance().getPartnerDAO().getById(query.getPartnerID());
            criteria.add(Restrictions.eq(IntegrationPartnerMap.FieldName_Partner, partner));
        }
        if (query.getMfsBillerId() != null) {
        	MfsBiller mfsBiller = DAOFactory.getInstance().getMFSBillerDAO().getById(query.getMfsBillerId());
            criteria.add(Restrictions.eq(IntegrationPartnerMap.FieldName_MFSBiller, mfsBiller));
        }
                
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<IntegrationPartnerMap> results = criteria.list();

        return results;
    }
	

}
