package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import org.apache.commons.lang.StringUtils;

import com.mfino.result.XMLResult;

public class SubscriberKTPValidationXMLResult extends XMLResult {

	private String addressLine;
	private String rt;
	private String rw;
	private String subDistrict;
	private String district;
	private String city;
	private String province;
	private String postalCode;
	private String mothersMaidenName;
	private String name;
	private String dob;
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the dob
	 */
	public String getDob() {
		return dob;
	}

	/**
	 * @param dob the dob to set
	 */
	public void setDob(String dob) {
		this.dob = dob;
	}

	/**
	 * @return the mothersMaidenName
	 */
	public String getMothersMaidenName() {
		return mothersMaidenName;
	}

	/**
	 * @param mothersMaidenName the mothersMaidenName to set
	 */
	public void setMothersMaidenName(String mothersMaidenName) {
		this.mothersMaidenName = mothersMaidenName;
	}

	/**
	 * @return the addressLine
	 */
	public String getAddressLine() {
		return addressLine;
	}

	/**
	 * @param addressLine the addressLine to set
	 */
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}

	/**
	 * @return the rt
	 */
	public String getRt() {
		return rt;
	}

	/**
	 * @param rt the rt to set
	 */
	public void setRt(String rt) {
		this.rt = rt;
	}

	/**
	 * @return the rw
	 */
	public String getRw() {
		return rw;
	}

	/**
	 * @param rw the rw to set
	 */
	public void setRw(String rw) {
		this.rw = rw;
	}

	/**
	 * @return the subDistrict
	 */
	public String getSubDistrict() {
		return subDistrict;
	}

	/**
	 * @param subDistrict the subDistrict to set
	 */
	public void setSubDistrict(String subDistrict) {
		this.subDistrict = subDistrict;
	}

	/**
	 * @return the district
	 */
	public String getDistrict() {
		return district;
	}

	/**
	 * @param district the district to set
	 */
	public void setDistrict(String district) {
		this.district = district;
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
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public SubscriberKTPValidationXMLResult() {
		super();
	}

	public void render() throws Exception {
		
		writeStartOfDocument();
		super.render();
		
		if(getTransactionID() > 0) {
			
			getXmlWriter().writeStartElement("transactionId");
			getXmlWriter().writeCharacters(String.valueOf(getTransactionID()), false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getName())) {
			
			getXmlWriter().writeStartElement("name");
			getXmlWriter().writeCharacters(getName(), false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getDob())) {
			
			getXmlWriter().writeStartElement("dob");
			getXmlWriter().writeCharacters(getDob(), false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getMothersMaidenName())) {
			
			getXmlWriter().writeStartElement("mothersMaidenName");
			getXmlWriter().writeCharacters(getMothersMaidenName(), false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getAddressLine())) {
			
			getXmlWriter().writeStartElement("addressLine");
			getXmlWriter().writeCharacters(getAddressLine(), false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getRt())) {
			
			getXmlWriter().writeStartElement("rt");
			getXmlWriter().writeCharacters(getRt(), false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getRw())) {
			
			getXmlWriter().writeStartElement("rw");
			getXmlWriter().writeCharacters(getRw(), false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getCity())) {
			
			getXmlWriter().writeStartElement("city");
			getXmlWriter().writeCharacters(getCity(), false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getSubDistrict())) {
			
			getXmlWriter().writeStartElement("subDistrict");
			getXmlWriter().writeCharacters(getSubDistrict(), false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getDistrict())) {
			
			getXmlWriter().writeStartElement("district");
			getXmlWriter().writeCharacters(getDistrict(), false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getProvince())) {
			
			getXmlWriter().writeStartElement("province");
			getXmlWriter().writeCharacters(getProvince(), false);
			getXmlWriter().writeEndElement();
		}
		
		if(StringUtils.isNotBlank(getPostalCode())) {
			
			getXmlWriter().writeStartElement("postalCode");
			getXmlWriter().writeCharacters(getPostalCode(), false);
			getXmlWriter().writeEndElement();
		}
		
		writeEndOfDocument();
	}
}
