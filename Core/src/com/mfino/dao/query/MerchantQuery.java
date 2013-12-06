/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

import java.util.Date;

/**
 *
 * @author xchen
 */
public class MerchantQuery extends BaseQuery {
	 //Before Correcting errors reported by Findbugs:
		//A field Long _ID and its setters and getters

	 //After Correcting the errors reported by Findbugs:deleted the _ID field and its setters and getters since it is not used anywhere.

    private String mdn;
    private String firstName;
    private String lastName;
    private String channel;
    private Integer level;
    private Date startRegistrationDate;
    private Date endRegistrationDate;
    private Integer merchantStatus;
    private Integer merchantRestrictions;
    private String userName;
    private Long parentID;
    private String ExactUser;
    private Long parentAndSelfID;
    private String exactMDN;
   // private Integer status;
    private String exactGroupID;
    private Integer[] merchantStatusIn;
    private Date _statusTimeGE;
    private Date _statusTimeLT;

    public Date getStatusTimeGE() {
        return _statusTimeGE;
    }

    public void setStatusTimeGE(Date _statusTimeGE) {
        this._statusTimeGE = _statusTimeGE;
    }

    public Date getStatusTimeLT() {
        return _statusTimeLT;
    }

    public void setStatusTimeLT(Date _statusTimeLT) {
        this._statusTimeLT = _statusTimeLT;
    }

    public String getExactGroupID() {
        return exactGroupID;
    }

    public void setExactGroupID(String exactGroupID) {
        this.exactGroupID = exactGroupID;
    }


    public String getExactMDN() {
        return exactMDN;
    }

    public void setExactMDN(String exactMDN) {
        this.exactMDN = exactMDN;
    }

    public String getExactUser() {
        return ExactUser;
    }

    public void setExactUser(String ExactUser) {
        this.ExactUser = ExactUser;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }

    public Date getEndRegistrationDate() {
        return endRegistrationDate;
    }

    public void setEndRegistrationDate(Date endRegistrationDate) {
        this.endRegistrationDate = endRegistrationDate;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getMerchantRestrictions() {
        return merchantRestrictions;
    }

    public void setMerchantRestrictions(Integer merchantRestrictions) {
        this.merchantRestrictions = merchantRestrictions;
    }

    public Integer getMerchantStatus() {
        return merchantStatus;
    }

    public void setMerchantStatus(Integer merchantStatus) {
        this.merchantStatus = merchantStatus;
    }

    public Date getStartRegistrationDate() {
        return startRegistrationDate;
    }

    public void setStartRegistrationDate(Date startRegistrationDate) {
        this.startRegistrationDate = startRegistrationDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the parentID
     */
    public Long getParentID() {
        return parentID;
    }

    /**
     * @param parentID the parentID to set
     */
    public void setParentID(Long parentID) {
        this.parentID = parentID;
    }

    /**
     * @return the parentAndSelfID
     */
    public Long getParentAndSelfID() {
        return parentAndSelfID;
    }

    /**
     * @param parentAndSelfID the parentAndSelfID to set
     */
    public void setParentAndSelfID(Long parentAndSelfID) {
        this.parentAndSelfID = parentAndSelfID;
    }

    public void setMerchantStatusIn(Integer[] statuses) {
        this.merchantStatusIn = statuses;
    }

    public Integer[] getMerchantStatusIn(){
        return this.merchantStatusIn;
    }

//    public Integer getStatus() {
//        return status;
//    }
//
//    public void setStatus(Integer status) {
//        this.status = status;
//    }
  
}
