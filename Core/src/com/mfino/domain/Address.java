package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Address generated by hbm2java
 */
@Entity
@Table(name = "ADDRESS")
public class Address extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String description;
	private String line1;
	private String line2;
	private String city;
	private String state;
	private String zipcode;
	private String country;
	private String regionname;
	private String rw;
	private String rt;
	private String substate;
	private Set<Merchant> merchantsForFranchiseoutletaddressid = new HashSet<Merchant>(
			0);
	private Set<Partner> partnersForMerchantaddressid = new HashSet<Partner>(0);
	private Set<Subscriber> subscribersForSubscriberaddressktpid = new HashSet<Subscriber>(
			0);
	private Set<CardInfo> cardInfosForOldbillingaddressid = new HashSet<CardInfo>(
			0);
	private Set<AuthPersonDetails> authPersonDetailses = new HashSet<AuthPersonDetails>(
			0);
	private Set<CardInfo> cardInfosForAddressid = new HashSet<CardInfo>(0);
	private Set<CardInfo> cardInfosForBillingaddressid = new HashSet<CardInfo>(
			0);
	private Set<Subscriber> subscribersForSubscriberaddressid = new HashSet<Subscriber>(
			0);
	private Set<Partner> partnersForFranchiseoutletaddressid = new HashSet<Partner>(
			0);
	private Set<Merchant> merchantsForMerchantaddressid = new HashSet<Merchant>(
			0);
	private Set<CardInfo> cardInfosForOldaddressid = new HashSet<CardInfo>(0);

	public Address() {
	}

	@Column(name = "DESCRIPTION", length = 1020)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "LINE1", length = 1020)
	public String getLine1() {
		return this.line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	@Column(name = "LINE2", length = 1020)
	public String getLine2() {
		return this.line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	@Column(name = "CITY", length = 1020)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "STATE", length = 1020)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "ZIPCODE", length = 1020)
	public String getZipcode() {
		return this.zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	@Column(name = "COUNTRY", length = 1020)
	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "REGIONNAME", length = 1020)
	public String getRegionname() {
		return this.regionname;
	}

	public void setRegionname(String regionname) {
		this.regionname = regionname;
	}

	@Column(name = "RW")
	public String getRw() {
		return this.rw;
	}

	public void setRw(String rw) {
		this.rw = rw;
	}

	@Column(name = "RT")
	public String getRt() {
		return this.rt;
	}

	public void setRt(String rt) {
		this.rt = rt;
	}

	@Column(name = "SUBSTATE")
	public String getSubstate() {
		return this.substate;
	}

	public void setSubstate(String substate) {
		this.substate = substate;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "addressByFranchiseoutletaddressid")
	public Set<Merchant> getMerchantsForFranchiseoutletaddressid() {
		return this.merchantsForFranchiseoutletaddressid;
	}

	public void setMerchantsForFranchiseoutletaddressid(
			Set<Merchant> merchantsForFranchiseoutletaddressid) {
		this.merchantsForFranchiseoutletaddressid = merchantsForFranchiseoutletaddressid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "addressByMerchantaddressid")
	public Set<Partner> getPartnersForMerchantaddressid() {
		return this.partnersForMerchantaddressid;
	}

	public void setPartnersForMerchantaddressid(
			Set<Partner> partnersForMerchantaddressid) {
		this.partnersForMerchantaddressid = partnersForMerchantaddressid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "addressBySubscriberaddressktpid")
	public Set<Subscriber> getSubscribersForSubscriberaddressktpid() {
		return this.subscribersForSubscriberaddressktpid;
	}

	public void setSubscribersForSubscriberaddressktpid(
			Set<Subscriber> subscribersForSubscriberaddressktpid) {
		this.subscribersForSubscriberaddressktpid = subscribersForSubscriberaddressktpid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "addressByOldbillingaddressid")
	public Set<CardInfo> getCardInfosForOldbillingaddressid() {
		return this.cardInfosForOldbillingaddressid;
	}

	public void setCardInfosForOldbillingaddressid(
			Set<CardInfo> cardInfosForOldbillingaddressid) {
		this.cardInfosForOldbillingaddressid = cardInfosForOldbillingaddressid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "address")
	public Set<AuthPersonDetails> getAuthPersonDetailses() {
		return this.authPersonDetailses;
	}

	public void setAuthPersonDetailses(
			Set<AuthPersonDetails> authPersonDetailses) {
		this.authPersonDetailses = authPersonDetailses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "addressByAddressid")
	public Set<CardInfo> getCardInfosForAddressid() {
		return this.cardInfosForAddressid;
	}

	public void setCardInfosForAddressid(Set<CardInfo> cardInfosForAddressid) {
		this.cardInfosForAddressid = cardInfosForAddressid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "addressByBillingaddressid")
	public Set<CardInfo> getCardInfosForBillingaddressid() {
		return this.cardInfosForBillingaddressid;
	}

	public void setCardInfosForBillingaddressid(
			Set<CardInfo> cardInfosForBillingaddressid) {
		this.cardInfosForBillingaddressid = cardInfosForBillingaddressid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "addressBySubscriberaddressid")
	public Set<Subscriber> getSubscribersForSubscriberaddressid() {
		return this.subscribersForSubscriberaddressid;
	}

	public void setSubscribersForSubscriberaddressid(
			Set<Subscriber> subscribersForSubscriberaddressid) {
		this.subscribersForSubscriberaddressid = subscribersForSubscriberaddressid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "addressByFranchiseoutletaddressid")
	public Set<Partner> getPartnersForFranchiseoutletaddressid() {
		return this.partnersForFranchiseoutletaddressid;
	}

	public void setPartnersForFranchiseoutletaddressid(
			Set<Partner> partnersForFranchiseoutletaddressid) {
		this.partnersForFranchiseoutletaddressid = partnersForFranchiseoutletaddressid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "addressByMerchantaddressid")
	public Set<Merchant> getMerchantsForMerchantaddressid() {
		return this.merchantsForMerchantaddressid;
	}

	public void setMerchantsForMerchantaddressid(
			Set<Merchant> merchantsForMerchantaddressid) {
		this.merchantsForMerchantaddressid = merchantsForMerchantaddressid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "addressByOldaddressid")
	public Set<CardInfo> getCardInfosForOldaddressid() {
		return this.cardInfosForOldaddressid;
	}

	public void setCardInfosForOldaddressid(
			Set<CardInfo> cardInfosForOldaddressid) {
		this.cardInfosForOldaddressid = cardInfosForOldaddressid;
	}

}
