/**
 * 
 */
package com.mfino.service.impl;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.IntegrationPartnerMappingDAO;
import com.mfino.domain.IntegrationPartnerMapping;
import com.mfino.service.IntegrationPartnerMappingService;
import com.mfino.service.SystemParametersService;

/**
 * @author Sreenath
 *
 */
@Service("IntegrationPartnerMappingServiceImpl")
public class IntegrationPartnerMappingServiceImpl implements IntegrationPartnerMappingService {
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	/**
	 * Gets IntegrationPartnerMapping record from the given institutionId.If InstitutionId is null returns null
	 * @param institutionId
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public IntegrationPartnerMapping getByInstitutionID(String institutionId) {
		IntegrationPartnerMapping ipm = null;
		if(StringUtils.isNotBlank(institutionId)){
			IntegrationPartnerMappingDAO ipmDAO = DAOFactory.getInstance().getIntegrationPartnerMappingDAO();
			ipm = ipmDAO.getByInstitutionID(institutionId);
		}
		return ipm;
		
	}
	
	/**
	 * Generates AuthenticationKey related to integration
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public String generateAuthenticationKey(){
		UUID uuid = UUID.randomUUID();
		Integer keyLength = systemParametersService.getInteger(SystemParameterKeys.INTEGRATION_AUTHENTICATION_KEY_LENGTH);
		return uuid.toString().substring(0, keyLength);
	}

}
