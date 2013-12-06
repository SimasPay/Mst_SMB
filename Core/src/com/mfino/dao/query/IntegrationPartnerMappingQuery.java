package com.mfino.dao.query;

/**
 * 
 * @author Amar
 *
 */
public class IntegrationPartnerMappingQuery extends BaseQuery{
    
	private String institutionID;
    private String integrationName;
    private Long partnerID;
    private Long mfsBillerId;
    
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
	public Long getPartnerID() {
		return partnerID;
	}
	public void setPartnerID(Long partnerID) {
		this.partnerID = partnerID;
	}
	public Long getMfsBillerId() {
		return mfsBillerId;
	}
	public void setMfsBillerId(Long mfsBillerId) {
		this.mfsBillerId = mfsBillerId;
	}
       
	
}
