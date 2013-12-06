package com.mfino.webapi.utilities;

import javax.servlet.http.HttpServletRequest;

import com.mfino.integrations.vo.IntegrationDetails;
import com.mfino.transactionapi.constants.ApiConstants;


/**
 * @author Amar
 */
public class IntegrationDetailsExtractor {

	public static IntegrationDetails getIntegrationDetails(HttpServletRequest request)
	{
		IntegrationDetails integrationDetails = new IntegrationDetails();

		String institutionID = request.getParameter(ApiConstants.PARAMETER_INSTITUTION_ID);

		String integrationName = request.getParameter(ApiConstants.PARAMETER_INTEGRATION_NAME);

		String partnerCode = request.getParameter(ApiConstants.PARAMETER_PARTNER_CODE);

		String mfsBillerCode = request.getParameter(ApiConstants.PARAMETER_MFS_BILLER_CODE);

		//String ipAddress = request.getParameter(ApiConstants.PARAMETER_IPADRESS);
		String ipAddress = request.getRemoteAddr();

		String authenticationKey = request.getParameter(ApiConstants.PARAMETER_AUTHENTICATION_KEY);

		if(institutionID != null)
		{
			integrationDetails.setInstitutionID(institutionID);
		}
		if(integrationName != null)
		{
			integrationDetails.setIntegrationName(integrationName);
		}
		if(partnerCode != null)
		{
			integrationDetails.setPartnerCode(partnerCode);
		}
		if(mfsBillerCode != null)
		{
			integrationDetails.setMfsBillerCode(mfsBillerCode);
		}
		if(authenticationKey != null)
		{
			integrationDetails.setAuthenticationKey(authenticationKey);
		}
		if(ipAddress != null)
		{
			integrationDetails.setIPAddress(ipAddress);
		}
		return integrationDetails;
	}
	
}
