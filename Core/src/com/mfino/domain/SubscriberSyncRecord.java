/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.domain;

import java.util.Date;

import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author sandeepjs
 */
public class SubscriberSyncRecord {

    private Long id;
    private String mdn;
    private String firstName;
    private String lastName;
    private String email;
    private Integer language;
    private String currency;
    private String activeAccountType;
    private String status;
    private String orgMDN;
    private String newMDN;
    private Date dateOfBirth;
    private String idType;
    private String idNumber;
    private String gender;
    private String address;
    private String addressline2;
    
    private String city;
    private String birthPlace;
    private String imsi;
    private String marketingCategory;
    private String product;
    private Integer serviceType;
    private String balance;
    private String proofofaddress;
    private String creditCheck;
    private String companyName;
    private String certificateofIncorporation;
    private String cardPan;
    private boolean isBulkUploadRecord;
    private Long PocketTemplateID;
    private Integer bankAcType;
    private Date idExpireDate;
    private String aliasName;
    private String nationality;
    private String placeOfBirth;
    private Integer accountStatus;
    private String region;
    private String country;
    private Long referenceACNumber;
    private Integer controlReference;
    private String nextKinName;
    private String nextKinNumber;
    private String authorizFirstName;
    private String authorizLastName;
    private String authorizID;
    private String authorizIDDesc;
    private Date authorizDOB;
    private String streetName;
    private String plotNumber;
    private String oneTimePin;
  	private String misc1;
    private String misc2;
    private Integer accountType;
    private String mobileCompanyName;
    private String applicationId;
    private String groupName;
    
    
        
    public SubscriberSyncRecord() {
    }

    public SubscriberSyncRecord(String mdn, String firstName, String lastName, String email,
            Integer language, String currency,
            String activeAccountType, Date dateOfBirth, String idType,
            String idNumber, String gender, String address, String city, String birthPlace, String imsi,
            String marketingCategory, String product) {
        this.mdn = mdn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.language = language;
        this.currency = currency;
        this.activeAccountType = activeAccountType;
        this.dateOfBirth = dateOfBirth;
        this.idType = idType;
        this.idNumber = idNumber;
        this.gender = gender;
        this.address = address;
        this.city = city;
        this.birthPlace = birthPlace;
        this.imsi = imsi;
        setMarketingCategory(marketingCategory);
        this.product = product;
    }

    public SubscriberSyncRecord(String orgMDN, String newMDN) {
        this.orgMDN = orgMDN;
        this.newMDN = newMDN;
    }

    public SubscriberSyncRecord(String mdn) {
        this.mdn = mdn;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getMarketingCategory() {
        return marketingCategory;
    }

    public void setMarketingCategory(String marketingCategory) {
        if (marketingCategory.equalsIgnoreCase("BISSTEL_Prepaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_BISSTEL_Prepaid;
        } else if (marketingCategory.equalsIgnoreCase("BlackBerry_Prepaid_Hebat")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_BlackBerry_Prepaid_Hebat;
        } else if (marketingCategory.equalsIgnoreCase("BlackBerry_Prepaid_Hemat")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_BlackBerry_Prepaid_Hemat;
        } else if (marketingCategory.equalsIgnoreCase("CCS_Postpaid_Guarantee_50K_60K_75K")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_CCS_Postpaid_Guarantee_50K_60K_75K;
        } else if (marketingCategory.equalsIgnoreCase("CCS_Prepaid_180K")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_CCS_Prepaid_180K;
        } else if (marketingCategory.equalsIgnoreCase("CCS_Prepaid_180KPlus")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_CCS_Prepaid_180KPlus;
        } else if (marketingCategory.equalsIgnoreCase("CCS_Prepaid_CB_with_NBG")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_CCS_Prepaid_CB_with_NBG;
        } else if (marketingCategory.equalsIgnoreCase("Chatterbox_190")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Chatterbox_190;
        } else if (marketingCategory.equalsIgnoreCase("Corporate_Artatel")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Corporate_Artatel;
        } else if (marketingCategory.equalsIgnoreCase("Corporate_Postpaid_Fixed_Wireless")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Corporate_Postpaid_Fixed_Wireless;
        } else if (marketingCategory.equalsIgnoreCase("Corporate_Prepaid_Fixed_Wireless")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Corporate_Prepaid_Fixed_Wireless;
        } else if (marketingCategory.equalsIgnoreCase("Dompet_Merchants")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Dompet_Merchants;
        } else if (marketingCategory.equalsIgnoreCase("E_Load_Retailers_Prepaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_E_Load_Retailers_Prepaid;
        } else if (marketingCategory.equalsIgnoreCase("HIPMI_Prepaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_HIPMI_Prepaid;
        } else if (marketingCategory.equalsIgnoreCase("KTNA_Prepaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_KTNA_Prepaid;
        } else if (marketingCategory.equalsIgnoreCase("M_Finance_Retailer_Postpaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_M_Finance_Retailer_Postpaid;
        } else if (marketingCategory.equalsIgnoreCase("M_finance_retailer_Prepaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_M_finance_retailer_Prepaid;
        } else if (marketingCategory.equalsIgnoreCase("Postpaid_Consumer_AYCE_25")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Postpaid_Consumer_AYCE_25;
        } else if (marketingCategory.equalsIgnoreCase("Postpaid_Corporate_Paid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Postpaid_Corporate_Paid;
        } else if (marketingCategory.equalsIgnoreCase("MarketingCategory_Postpaid_EVDO")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Postpaid_EVDO;
        } else if (marketingCategory.equalsIgnoreCase("Postpaid_EvDO_Silver_Banking_Upfront")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Postpaid_EvDO_Silver_Banking_Upfront;
        } else if (marketingCategory.equalsIgnoreCase("Postpaid_Regular_Subscriber")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Postpaid_Regular_Subscriber;
        } else if (marketingCategory.equalsIgnoreCase("Postpaid__Corporate_Guarantee")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Postpaid__Corporate_Guarantee;
        } else if (marketingCategory.equalsIgnoreCase("Prepaid_EVDO")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Prepaid_EVDO;
        } else if (marketingCategory.equalsIgnoreCase("Prepaid_EVDO_Reseller")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Prepaid_EVDO_Reseller;
        } else if (marketingCategory.equalsIgnoreCase("Prepaid_Tarbiyah")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Prepaid_Tarbiyah;
        } else if (marketingCategory.equalsIgnoreCase("REVA_test")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_REVA_test;
        } else if (marketingCategory.equalsIgnoreCase("Regular_Postpaid_Fixed_Wireless")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Regular_Postpaid_Fixed_Wireless;
        } else if (marketingCategory.equalsIgnoreCase("Regular_Prepaid_Fixed_Wireless")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_Regular_Prepaid_Fixed_Wireless;
        } else if (marketingCategory.equalsIgnoreCase("SMART_Employee_Postpaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_SMART_Employee_Postpaid;
        } else if (marketingCategory.equalsIgnoreCase("SMART_Employee_Prepaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_SMART_Employee_Prepaid;
        } else if (marketingCategory.equalsIgnoreCase("SMART_TESTER")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_SMART_TESTER;
        } else if (marketingCategory.equalsIgnoreCase("SMART_TESTER_Operational")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_SMART_TESTER_Operational;
        } else if (marketingCategory.equalsIgnoreCase("SMG_Clients_Postpaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_SMG_Clients_Postpaid;
        } else if (marketingCategory.equalsIgnoreCase("SMG_Corporate_Postpaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_SMG_Corporate_Postpaid;
        } else if (marketingCategory.equalsIgnoreCase("SMG_FnF_subscriber_Postpaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_SMG_FnF_subscriber_Postpaid;
        } else if (marketingCategory.equalsIgnoreCase("SMG_FnF_subscriber_Prepaid")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_SMG_FnF_subscriber_Prepaid;
        } else if (marketingCategory.equalsIgnoreCase("VIP")) {
            this.marketingCategory = CmFinoFIX.MarketingCategory_VIP;
        } else {
            this.marketingCategory = marketingCategory;
        }
    }

    public String getActiveAccountType() {
        return activeAccountType;
    }

    public void setActiveAccountType(String activeAccountType) {
        this.activeAccountType = activeAccountType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLanguage() {
        return language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMdn() {
        return getNormalizedMDN(mdn);
    }

    public String getExactNewMdn() {
        return this.newMDN;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public String getNewMDN() {
        return getNormalizedMDN(newMDN);
    }

    public void setNewMDN(String newMDN) {
        this.newMDN = newMDN;
    }

    public String getOrgMDN() {
        return getNormalizedMDN(orgMDN);
    }

    public void setOrgMDN(String orgMDN) {
        this.orgMDN = orgMDN;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImsi() {
        return imsi;
    }

    private String getNormalizedMDN(String mdn) {
       return mdn;
    }

    /**
     * @return the serviceType
     */
    public Integer getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @return the balance
     */
    public String getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(String balance) {
        this.balance = balance;
    }

    /**
     * @return the isBulkUploadRecord
     */
    public boolean isBulkUploadRecord() {
        return isBulkUploadRecord;
    }

    /**
     * @param isBulkUploadRecord the isBulkUploadRecord to set
     */
    public void setBulkUploadRecord(boolean isBulkUploadRecord) {
        this.isBulkUploadRecord = isBulkUploadRecord;
    }

    /**
     * To String which converts entire parameters as teh string
     */
    public String Serialize() {
        StringBuffer str = new StringBuffer();
        str.append("mdn " + this.mdn);
        str.append(" firstName " + this.firstName);
        str.append(" lastName " + this.lastName);
        str.append(" email " + this.email);
        str.append(" language " + this.language);
        str.append(" currency " + this.currency);
        str.append(" activeAccountType " + this.activeAccountType);
        str.append(" dateOfBirth " + this.dateOfBirth);
        str.append(" idType " + this.idType);
        str.append(" idNumber " + this.idNumber);
        str.append(" gender " + this.gender);
        str.append(" address " + this.address);
        str.append(" city " + this.city);
        str.append(" birthPlace " + this.birthPlace);
        str.append(" imsi " + this.imsi);
        str.append(" marketingCategory " + this.marketingCategory);
        str.append(" product " + this.product);
        str.append(" orgMDN " + this.orgMDN);
        str.append(" newMDN " + this.newMDN);
        return str.toString();
    }

	public void setAddressline2(String addressline2) {
		this.addressline2 = addressline2;
	}

	public String getAddressline2() {
		return addressline2;
	}

	public void setProofofaddress(String proofofaddress) {
		this.proofofaddress = proofofaddress;
	}

	public String getProofofaddress() {
		return proofofaddress;
	}

	public void setCreditCheck(String creditCheck) {
		this.creditCheck = creditCheck;
	}

	public String getCreditCheck() {
		return creditCheck;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCertificateofIncorporation(String certificateofIncorporation) {
		this.certificateofIncorporation = certificateofIncorporation;
	}

	public String getCertificateofIncorporation() {
		return certificateofIncorporation;
	}

	public Integer getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(Integer accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Long getReferenceACNumber() {
		return referenceACNumber;
	}

	public void setReferenceACNumber(Long referenceACNumber) {
		this.referenceACNumber = referenceACNumber;
	}

	public Integer getControlReference() {
		return controlReference;
	}

	public void setControlReference(Integer controlReference) {
		this.controlReference = controlReference;
	}

	public String getNextKinName() {
		return nextKinName;
	}

	public void setNextKinName(String nextKinName) {
		this.nextKinName = nextKinName;
	}

	public String getNextKinNumber() {
		return nextKinNumber;
	}

	public void setNextKinNumber(String nextKinNumber) {
		this.nextKinNumber = nextKinNumber;
	}

	public String getAuthorizFirstName() {
		return authorizFirstName;
	}

	public void setAuthorizFirstName(String authorizFirstName) {
		this.authorizFirstName = authorizFirstName;
	}

	public String getAuthorizLastName() {
		return authorizLastName;
	}

	public void setAuthorizLastName(String authorizLastName) {
		this.authorizLastName = authorizLastName;
	}

	public String getAuthorizID() {
		return authorizID;
	}

	public void setAuthorizID(String authorizID) {
		this.authorizID = authorizID;
	}

	public String getAuthorizIDDesc() {
		return authorizIDDesc;
	}

	public void setAuthorizIDDesc(String authorizIDDesc) {
		this.authorizIDDesc = authorizIDDesc;
	}

	public Date getAuthorizDOB() {
		return authorizDOB;
	}

	public void setAuthorizDOB(Date authorizDOB) {
		this.authorizDOB = authorizDOB;
	}

	public void setCardPan(String cardPan) {
		this.cardPan = cardPan;
	}

	public String getCardPan() {
		return cardPan;
	}

	public void setPocketTemplateID(Long pocketTemplateID) {
		PocketTemplateID = pocketTemplateID;
	}

	public Long getPocketTemplateID() {
		return PocketTemplateID;
	}

	public void setBankAcType(Integer bankAcType) {
		this.bankAcType = bankAcType;
	}

	public Integer getBankAcType() {
		return bankAcType;
	}

	public void setIdExpireDate(Date idExpireDate) {
		this.idExpireDate = idExpireDate;
	}

	public Date getIdExpireDate() {
		return idExpireDate;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getNationality() {
		return nationality;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setPlotNumber(String plotNumber) {
		this.plotNumber = plotNumber;
	}

	public String getPlotNumber() {
		return plotNumber;
	}

	public void setOneTimePin(String oneTimePin) {
		this.oneTimePin = oneTimePin;
	}

	public String getOneTimePin() {
		return oneTimePin;
	}

	public void setMisc1(String misc1) {
		this.misc1 = misc1;
	}

	public String getMisc1() {
		return misc1;
	}

	public void setMisc2(String misc2) {
		this.misc2 = misc2;
	}

	public String getMisc2() {
		return misc2;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setMobileCompanyName(String mobileCompanyName) {
		this.mobileCompanyName = mobileCompanyName;
	}

	public String getMobileCompanyName() {
		return mobileCompanyName;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}
