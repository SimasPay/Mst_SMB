package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.query.IPMappingQuery;
import com.mfino.domain.IPMapping;
import com.mfino.domain.IntegrationPartnerMapping;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Amar
 *
 */
public class IPMappingDAO extends BaseDAO<IPMapping>{

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public List<IPMapping> getByIntegrationName(String integrationName){
		log.info("IPMappingDAO :: getByIntegrationName() "+integrationName);

		if((null == integrationName) || ("".equals(integrationName))) return null;
		IntegrationPartnerMapping integrationPartnerMapping = DAOFactory.getInstance().getIntegrationPartnerMappingDAO().getByIntegrationName(integrationName);		
		Criteria criteria = createCriteria();
		if(integrationPartnerMapping != null)
		{
			criteria.add(Restrictions.eq(CmFinoFIX.CRIPMapping.FieldName_IntegrationPartnerMappingByIntegrationID, integrationPartnerMapping));
		}
		@SuppressWarnings("unchecked")
		List<IPMapping> results = criteria.list();
		return results;
	}

	public List<IPMapping> get(IPMappingQuery query) {

		Criteria criteria = createCriteria();
		if (query.getIntegrationID() != null) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRIPMapping.FieldName_IntegrationPartnerMappingByIntegrationID, 
					DAOFactory.getInstance().getIntegrationPartnerMappingDAO().getById(query.getIntegrationID())));
		}
		if (StringUtils.isNotBlank(query.getIpAddress())) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRIPMapping.FieldName_IPAddress, query.getIpAddress()));
		}

		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<IPMapping> results = criteria.list();

		return results;
	}

}
