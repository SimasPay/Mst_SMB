
package com.mfino.integrations.vo;

/**
 * 
 * @author Amar
 *
 */
public class IntegrationDetails {
	
	private String institutionID;
	
	private String integrationName;
	
	private String partnerCode;	
	
	private String mfsBillerCode;
	
	private String authenticationKey;
	
	private String ipAddress;

	public String getInstitutionID() {
		return institutionID;
	}

	public void setInstitutionID(String institutionID) {
		this.institutionID = institutionID;
	}

	public String getIntegrationName() {
		return integrationName;
	}

	public void setIntegrationName(String integrationName) {
		this.integrationName = integrationName;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public String getMfsBillerCode() {
		return mfsBillerCode;
	}

	public void setMfsBillerCode(String mfsBillerCode) {
		this.mfsBillerCode = mfsBillerCode;
	}

	public String getAuthenticationKey() {
		return authenticationKey;
	}

	public void setAuthenticationKey(String authenticationKey) {
		this.authenticationKey = authenticationKey;
	}

	public String getIPAddress() {
		return ipAddress;
	}

	public void setIPAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}	
	
}
