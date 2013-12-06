/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.datapushserver.domain;

import java.util.Date;

/**
 *
 * @author sandeepjs
 */
public class CBOSSRequest {

    private Long id;
    private String mdn;
    private String firstName;
    private String lastName;
    private String email;
    private String language;
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
    private String city;
    private String birthPlace;
    private String imsi;
    private String marketingCategory;
    private String product;

    public CBOSSRequest() {
    }

    public CBOSSRequest(String mdn, String firstName, String lastName, String email,
            String language, String currency,
            String activeAccountType, Date dateOfBirth, String idType,
            String idNumber, String gender, String address, String city, String birthPlace, String imsi,
            String marketingCategory, String product)
    {
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
        this.marketingCategory = marketingCategory;
        this.product = product;
    }
     public CBOSSRequest(String orgMDN, String newMDN)
     {
        this.orgMDN = orgMDN;
        this.newMDN = newMDN;
     }
     public CBOSSRequest(String mdn)
     {
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
        this.marketingCategory = marketingCategory;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
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
      if(null == mdn || 0 == mdn.trim().length()) {
        return null;
      }

      return "62" + mdn.trim();
    }
}
