/**
 * 
 */
package com.mfino.service;

import com.mfino.domain.IntegrationPartnerMapping;

/**
 * @author Sreenath
 *
 */
public interface IntegrationPartnerMappingService {
	/**
	 * Gets IntegrationPartnerMapping record from the given institutionId.If InstitutionId is null returns null
	 * @param institutionId
	 * @return
	 */
	public IntegrationPartnerMapping getByInstitutionID(String institutionId);
	
	/**
	 * Generates AuthenticationKey related to integration
	 * @return
	 */
	public String generateAuthenticationKey();

}
