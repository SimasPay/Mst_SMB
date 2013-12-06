package com.mfino.dao.query;

public class BusinessPartnerQuery extends BaseQuery{
    
	private String businessPartnerId;
    private String partnerId;
    private String tradeName;
    private String authorizedEmail;
    private String partnerCode;
    
	public String getPartnerId() {
		return partnerId;
	}
	
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	
	public String getTradeName() {
		return tradeName;
	}
	
	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}
	
	public String getAuthorizedEmail() {
		return authorizedEmail;
	}
	
	public void setAuthorizedEmail(String authorizedEmail) {
		this.authorizedEmail = authorizedEmail;
	}

	public String getPartnerCode() {
		return partnerCode;
	}

	public void setPartnerCode(String partnerCode) {
		this.partnerCode = partnerCode;
	}

	public String getBusinessPartnerId() {
		return businessPartnerId;
	}

	public void setBusinessPartnerId(String businessPartnerId) {
		this.businessPartnerId = businessPartnerId;
	}
}
