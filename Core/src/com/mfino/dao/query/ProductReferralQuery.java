/**
 * 
 */
package com.mfino.dao.query;

import java.util.Date;

/**
 * @author Harihara
 *
 */
public class ProductReferralQuery extends BaseQuery {

	private Date startDate;
	private Date endDate;
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	private String agentMDN;
	private String subscriberMDN;
	private String fullName;
	private String email;
	private String productDesired;
	private String others;
	public String getAgentMDN() {
		return agentMDN;
	}
	public void setAgentMDN(String agentMDN) {
		this.agentMDN = agentMDN;
	}
	public String getSubscriberMDN() {
		return subscriberMDN;
	}
	public void setSubscriberMDN(String subscriberMDN) {
		this.subscriberMDN = subscriberMDN;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProductDesired() {
		return productDesired;
	}
	public void setProductDesired(String productDesired) {
		this.productDesired = productDesired;
	}
	public String getOthers() {
		return others;
	}
	public void setOthers(String others) {
		this.others = others;
	}
	
		
	
	

	

	

}
