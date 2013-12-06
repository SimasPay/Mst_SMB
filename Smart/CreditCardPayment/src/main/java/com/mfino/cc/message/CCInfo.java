/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.cc.message;

/**
 *
 * @author admin
 */
public class CCInfo {
    private String cCNumberF6;
    private String cCNumberL4;
    private String nameOnCard;
    private String address1;
    private String addressLine2;
    private String city;
    private String state;
    private String region;
    private String zipCode;
    private String issuerName;
    private Long pocketId;
    private Integer cardInfoVersion;
    private Long cardId;
    private String billingAddress;
    private String billingaddressLine2;
    private String billingcity;
    private String billingstate;
    private String billingregion;
    private String billingzipCode;
    private Boolean isConfirmationRequired;

    

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getBillingaddressLine2() {
		return billingaddressLine2;
	}

	public void setBillingaddressLine2(String billingaddressLine2) {
		this.billingaddressLine2 = billingaddressLine2;
	}

	public String getBillingcity() {
		return billingcity;
	}

	public void setBillingcity(String billingcity) {
		this.billingcity = billingcity;
	}

	public String getBillingstate() {
		return billingstate;
	}

	public void setBillingstate(String billingstate) {
		this.billingstate = billingstate;
	}

	public String getBillingregion() {
		return billingregion;
	}

	public void setBillingregion(String billingregion) {
		this.billingregion = billingregion;
	}

	public String getBillingzipCode() {
		return billingzipCode;
	}

	public void setBillingzipCode(String billingzipCode) {
		this.billingzipCode = billingzipCode;
	}

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingaddress) {
        this.billingAddress = billingaddress;
    }

    public String getCCNumberF6() {
        return cCNumberF6;
    }

    public void setCCNumberF6(String cCNumberF6) {
        this.cCNumberF6 = cCNumberF6;
    }

    public String getCCNumberL4() {
        return cCNumberL4;
    }

    public void setCCNumberL4(String cCNumberL4) {
        this.cCNumberL4 = cCNumberL4;
    }

    public Integer getCardInfoVersion() {
        return cardInfoVersion;
    }

    public void setCardInfoVersion(Integer cardInfoVersion) {
        this.cardInfoVersion = cardInfoVersion;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public Long getPocketId() {
        return pocketId;
    }

    public void setPocketId(Long pocketId) {
        this.pocketId = pocketId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

	@Override
	public String toString() {
		return "CCInfo [cCNumberF6=" + cCNumberF6 + ", cCNumberL3="
				+ cCNumberL4 + ", nameOnCard=" + nameOnCard
				+ ", Address=" + address1 + ", addressLine2="
				+ addressLine2 + ", city=" + city + ", state=" + state
				+ ", region=" + region + ", zipCode=" + zipCode+", billingAddress=" + billingAddress + ",  billingaddressLine2="
				+  billingaddressLine2 + ",  billingcity=" +  billingcity + ",  billingstate=" +  billingstate
				+ ",  billingregion=" +  billingregion + ",  billingzipCode=" +  billingzipCode
                                + ", issuerName=" + issuerName + ", pocketId="
				+ pocketId + ", cardInfoVersion=" + cardInfoVersion
				+ ", cardId=" + cardId + "]";
	}

	public void setIsConfirmationRequired(Boolean isConfirmationRequired) {
		this.isConfirmationRequired = isConfirmationRequired;
}

	public Boolean getIsConfirmationRequired() {
		return isConfirmationRequired;
	}
}
