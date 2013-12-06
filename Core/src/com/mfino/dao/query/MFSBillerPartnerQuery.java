/**
 * 
 */
package com.mfino.dao.query;

/**
 * @author Bala Sunku
 *
 */
public class MFSBillerPartnerQuery extends BaseQuery {
	
	private Long mfsBillerId;
	private String billerType;
	public String getBillerType() {
		return billerType;
	}

	public void setBillerType(String billerType) {
		this.billerType = billerType;
	}

	public String getIntegrationCode() {
		return integrationCode;
	}

	public void setIntegrationCode(String integrationCode) {
		this.integrationCode = integrationCode;
	}

	private String integrationCode;

	public Long getMfsBillerId() {
		return mfsBillerId;
	}

	public void setMfsBillerId(Long mfsBillerId) {
		this.mfsBillerId = mfsBillerId;
	}
	
	private String billerCode;
	public String getBillerCode() {
		return billerCode;
	}

	public void setBillerCode(String billerCode) {
		this.billerCode = billerCode;
	}
	
	private Boolean ChargesIncluded;
		public boolean getChargesIncluded(){
			return ChargesIncluded;
		}
		
		public void setChargesIncluded(Boolean ChargesIncluded){
			this.ChargesIncluded=ChargesIncluded;
		}
	
}
