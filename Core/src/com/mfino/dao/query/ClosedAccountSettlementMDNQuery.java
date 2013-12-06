package com.mfino.dao.query;

import java.util.Date;

/**
 * @author Satya
 *
 */
public class ClosedAccountSettlementMDNQuery extends BaseQuery{
	private Long mdnId ;
	private String gravedMdn;
	private String FirstName;
	private String LastName;
	private Date dateOfBirth;
	private boolean toBankAccount;
	private String settlementMDN;
	private String settlementAccountNumber;
	private Integer[] multiStatus;
	private boolean restrictionIsEquals = true;
	
	public String getGravedMdn() {
		return gravedMdn;
	}
	public void setGravedMdn(String gravedMdn) {
		this.gravedMdn = gravedMdn;
	}
	public String getFirstName() {
		return FirstName;
	}
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	public String getLastName() {
		return LastName;
	}
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public Long getMdnId() {
		return mdnId;
	}
	public void setMdnId(Long mdnId) {
		this.mdnId = mdnId;
	}
	public boolean isToBankAccount() {
		return toBankAccount;
	}
	public void setToBankAccount(boolean toBankAccount) {
		this.toBankAccount = toBankAccount;
	}
	public String getSettlementMDN() {
		return settlementMDN;
	}
	public void setSettlementMDN(String settlementMDN) {
		this.settlementMDN = settlementMDN;
	}
	public String getSettlementAccountNumber() {
		return settlementAccountNumber;
	}
	public void setSettlementAccountNumber(String settlementAccountNumber) {
		this.settlementAccountNumber = settlementAccountNumber;
	}
	public Integer[] getMultiStatus() {
		return multiStatus;
	}
	public void setMultiStatus(Integer[] multiStatus) {
		this.multiStatus = multiStatus;
	}
	public boolean isRestrictionIsEquals() {
		return restrictionIsEquals;
	}
	public void setRestrictionIsEquals(boolean restrictionIsEquals) {
		this.restrictionIsEquals = restrictionIsEquals;
	}	
}
