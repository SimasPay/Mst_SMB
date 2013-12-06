package com.mfino.dao.query;

/**
 * 
 * @author Amar
 *
 */
public class IPMappingQuery extends BaseQuery{
    
	private Long integrationID;
    private String ipAddress;
    
	public Long getIntegrationID() {
		return integrationID;
	}
	public void setIntegrationID(Long integrationID) {
		this.integrationID = integrationID;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}    
	
}
