/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

/**
 *
 * @author xchen
 */
public class SubscriberQuery extends BaseQuery {

  private Boolean isDompetMerchant;
  private long subscriberUserID;
  private Integer registrationMedium;
  private Integer status;
  
  public Integer getStatus() {
	return status;
  }

  public void setStatus(Integer status) {
	this.status = status;
  }

	public Integer getRegistrationMedium() {
    	return registrationMedium;
    }

    public void setRegistrationMedium(Integer registrationMedium) {
    	this.registrationMedium = registrationMedium;
    }

	public long getSubscriberUserID() {
        return subscriberUserID;
    }

    public void setSubscriberUserID(long subscriberUserID) {
        this.subscriberUserID = subscriberUserID;
    }
    public Boolean getIsDompetMerchant() {
        return isDompetMerchant;
    }

    public void setIsDompetMerchant(boolean dompetMerchant) {
        this.isDompetMerchant = dompetMerchant;
    }
}
