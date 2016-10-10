package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.query.IPMappingQuery;
import com.mfino.domain.IpMapping;
import com.mfino.domain.IntegrationPartnerMap;

/**
 * @author Amar
 *
 */
public class IPMappingDAO extends BaseDAO<IpMapping>{

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public List<IpMapping> getByIntegrationName(String integrationName){
		log.info("IPMappingDAO :: getByIntegrationName() "+integrationName);

		if((null == integrationName) || ("".equals(integrationName))) return null;
		IntegrationPartnerMap integrationPartnerMapping = DAOFactory.getInstance().getIntegrationPartnerMappingDAO().getByIntegrationName(integrationName);		
		Criteria criteria = createCriteria();
		if(integrationPartnerMapping != null)
		{
			criteria.add(Restrictions.eq(IpMapping.FieldName_IntegrationPartnerMappingByIntegrationID, integrationPartnerMapping));
		}
		@SuppressWarnings("unchecked")
		List<IpMapping> results = criteria.list();
		return results;
	}

	public List<IpMapping> get(IPMappingQuery query) {

		Criteria criteria = createCriteria();
		if (query.getIntegrationID() != null) {
			criteria.add(Restrictions.eq(IpMapping.FieldName_IntegrationPartnerMappingByIntegrationID, 
					DAOFactory.getInstance().getIntegrationPartnerMappingDAO().getById(query.getIntegrationID())));
		}
		if (StringUtils.isNotBlank(query.getIpAddress())) {
			criteria.add(Restrictions.eq(IpMapping.FieldName_IPAddress, query.getIpAddress()));
		}

		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<IpMapping> results = criteria.list();

		return results;
	}

}
