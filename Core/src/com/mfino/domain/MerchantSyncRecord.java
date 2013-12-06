/**
 * 
 */
package com.mfino.domain;

import org.apache.commons.lang.StringUtils;

/**
 * @author Deva
 *
 */

/**
 * This is the object which represents each line in the Merchant File that gets uploaded
 */

public class MerchantSyncRecord {
	
	private String mdn = StringUtils.EMPTY; // Mandatory
	
	private String userName = StringUtils.EMPTY; // Mandatory
	
	private String language = StringUtils.EMPTY; // Mandatory
	
	private String timezone = StringUtils.EMPTY; // Mandatory
	
	private String groupId = StringUtils.EMPTY;
	
	private String currency = StringUtils.EMPTY; // Mandatory
	
	private String distChainTemplate = StringUtils.EMPTY;
	
	private String status = StringUtils.EMPTY; //Mandatory
	
	private String parentId = StringUtils.EMPTY; //Mandatory
	
	private String partnerType = StringUtils.EMPTY; //mandatory
	
	private String region = StringUtils.EMPTY; //Mandatory
	
	private String firstName = StringUtils.EMPTY;
	
	private String lastName = StringUtils.EMPTY;
	
	private String tradeName = StringUtils.EMPTY;
	
	private String email = StringUtils.EMPTY;
	
	private String adminComment = StringUtils.EMPTY;
	
	private String notificationMethod = StringUtils.EMPTY;
	
	private String line1 = StringUtils.EMPTY; // Mandatory
	
	private String line2 = StringUtils.EMPTY;
	
	private String city = StringUtils.EMPTY; // Mandatory
	
	private String state = StringUtils.EMPTY;
	
	private String country = StringUtils.EMPTY;
	
	private String zip = StringUtils.EMPTY;  // Mandatory
	
	private String outletType = StringUtils.EMPTY;
	
	private String contactNumber = StringUtils.EMPTY;  // Mandatory
	
	private String faxNumber = StringUtils.EMPTY;
	
	private String orgType = StringUtils.EMPTY;  // Mandatory
	
	private String industryClassification = StringUtils.EMPTY;  // Mandatory
	
	private String websiteURL = StringUtils.EMPTY;
	
	private String outletCnt = StringUtils.EMPTY;
	
	private String yearEstablished = StringUtils.EMPTY;  // Mandatory
	
	private String outletLine1 = StringUtils.EMPTY;
	
	private String outletLine2 = StringUtils.EMPTY;
	
	private String outletCity = StringUtils.EMPTY;
	
	private String outletState = StringUtils.EMPTY;
	
	private String outletCountry = StringUtils.EMPTY;
	
	private String outletZip = StringUtils.EMPTY; // Mandatory
	
	private String representativeName = StringUtils.EMPTY; // Mandatory
	
	private String position = StringUtils.EMPTY;
	
	private String repContactNumber = StringUtils.EMPTY;
	
	private String outletFaxNumber = StringUtils.EMPTY;
	
	private String outletEmail = StringUtils.EMPTY;
	
	private String srcIP = StringUtils.EMPTY;

	/**
	 * @return the mdn
	 */
	public String getMdn() {
		return mdn;
	}

	/**
	 * @param mdn the mdn to set
	 */
	public void setMdn(String mdn) {
		this.mdn = mdn;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the distChainTemplate
	 */
	public String getDistChainTemplate() {
		return distChainTemplate;
	}

	/**
	 * @param distChainTemplate the distChainTemplate to set
	 */
	public void setDistChainTemplate(String distChainTemplate) {
		this.distChainTemplate = distChainTemplate;
	}

	/**
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the tradeName
	 */
	public String getTradeName() {
		return tradeName;
	}

	/**
	 * @param tradeName the tradeName to set
	 */
	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the adminComment
	 */
	public String getAdminComment() {
		return adminComment;
	}

	/**
	 * @param adminComment the adminComment to set
	 */
	public void setAdminComment(String adminComment) {
		this.adminComment = adminComment;
	}

	/**
	 * @return the notificationMethod
	 */
	public String getNotificationMethod() {
		return notificationMethod;
	}

	/**
	 * @param notificationMethod the notificationMethod to set
	 */
	public void setNotificationMethod(String notificationMethod) {
		this.notificationMethod = notificationMethod;
	}


	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}

	/**
	 * @param zip the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * @return the outletType
	 */
	public String getOutletType() {
		return outletType;
	}

	/**
	 * @param outletType the outletType to set
	 */
	public void setOutletType(String outletType) {
		this.outletType = outletType;
	}

	/**
	 * @return the contactNumber
	 */
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * @param contactNumber the contactNumber to set
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	/**
	 * @return the faxNumber
	 */
	public String getFaxNumber() {
		return faxNumber;
	}

	/**
	 * @param faxNumber the faxNumber to set
	 */
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	/**
	 * @return the orgType
	 */
	public String getOrgType() {
		return orgType;
	}

	/**
	 * @param orgType the orgType to set
	 */
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	/**
	 * @return the websiteURL
	 */
	public String getWebsiteURL() {
		return websiteURL;
	}

	/**
	 * @param websiteURL the websiteURL to set
	 */
	public void setWebsiteURL(String websiteURL) {
		this.websiteURL = websiteURL;
	}

	/**
	 * @return the outletCnt
	 */
	public String getOutletCnt() {
		return outletCnt;
	}

	/**
	 * @param outletCnt the outletCnt to set
	 */
	public void setOutletCnt(String outletCnt) {
		this.outletCnt = outletCnt;
	}

	/**
	 * @return the yearEstablished
	 */
	public String getYearEstablished() {
		return yearEstablished;
	}

	/**
	 * @param yearEstablished the yearEstablished to set
	 */
	public void setYearEstablished(String yearEstablished) {
		this.yearEstablished = yearEstablished;
	}


	/**
	 * @return the outletCity
	 */
	public String getOutletCity() {
		return outletCity;
	}

	/**
	 * @param outletCity the outletCity to set
	 */
	public void setOutletCity(String outletCity) {
		this.outletCity = outletCity;
	}

	/**
	 * @return the outletState
	 */
	public String getOutletState() {
		return outletState;
	}

	/**
	 * @param outletState the outletState to set
	 */
	public void setOutletState(String outletState) {
		this.outletState = outletState;
	}

	/**
	 * @return the outletCountry
	 */
	public String getOutletCountry() {
		return outletCountry;
	}

	/**
	 * @param outletCountry the outletCountry to set
	 */
	public void setOutletCountry(String outletCountry) {
		this.outletCountry = outletCountry;
	}

	/**
	 * @return the outletZip
	 */
	public String getOutletZip() {
		return outletZip;
	}

	/**
	 * @param outletZip the outletZip to set
	 */
	public void setOutletZip(String outletZip) {
		this.outletZip = outletZip;
	}

	/**
	 * @return the representativeName
	 */
	public String getRepresentativeName() {
		return representativeName;
	}

	/**
	 * @param representativeName the representativeName to set
	 */
	public void setRepresentativeName(String representativeName) {
		this.representativeName = representativeName;
	}

	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * @return the repContactNumber
	 */
	public String getRepContactNumber() {
		return repContactNumber;
	}

	/**
	 * @param repContactNumber the repContactNumber to set
	 */
	public void setRepContactNumber(String repContactNumber) {
		this.repContactNumber = repContactNumber;
	}

	/**
	 * @return the outletFaxNumber
	 */
	public String getOutletFaxNumber() {
		return outletFaxNumber;
	}

	/**
	 * @param outletFaxNumber the outletFaxNumber to set
	 */
	public void setOutletFaxNumber(String outletFaxNumber) {
		this.outletFaxNumber = outletFaxNumber;
	}

	/**
	 * @return the outletEmail
	 */
	public String getOutletEmail() {
		return outletEmail;
	}

	/**
	 * @param outletEmail the outletEmail to set
	 */
	public void setOutletEmail(String outletEmail) {
		this.outletEmail = outletEmail;
	}

	/**
	 * @return the srcIP
	 */
	public String getSrcIP() {
		return srcIP;
	}

	/**
	 * @param srcIP the srcIP to set
	 */
	public void setSrcIP(String srcIP) {
		this.srcIP = srcIP;
	}

	/**
	 * @return the industryClassification
	 */
	public String getIndustryClassification() {
		return industryClassification;
	}

	/**
	 * @param industryClassification the industryClassification to set
	 */
	public void setIndustryClassification(String industryClassification) {
		this.industryClassification = industryClassification;
	}

	/**
	 * @return the partnerType
	 */
	public String getPartnerType() {
		return partnerType;
	}

	/**
	 * @param partnerType the partnerType to set
	 */
	public void setPartnerType(String partnerType) {
		this.partnerType = partnerType;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return the line1
	 */
	public String getLine1() {
		return line1;
	}

	/**
	 * @param line1 the line1 to set
	 */
	public void setLine1(String line1) {
		this.line1 = line1;
	}

	/**
	 * @return the line2
	 */
	public String getLine2() {
		return line2;
	}

	/**
	 * @param line2 the line2 to set
	 */
	public void setLine2(String line2) {
		this.line2 = line2;
	}

	/**
	 * @return the outletLine1
	 */
	public String getOutletLine1() {
		return outletLine1;
	}

	/**
	 * @param outletLine1 the outletLine1 to set
	 */
	public void setOutletLine1(String outletLine1) {
		this.outletLine1 = outletLine1;
	}

	/**
	 * @return the outletLine2
	 */
	public String getOutletLine2() {
		return outletLine2;
	}

	/**
	 * @param outletLine2 the outletLine2 to set
	 */
	public void setOutletLine2(String outletLine2) {
		this.outletLine2 = outletLine2;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
}
