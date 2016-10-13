package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.mfino.hibernate.Timestamp;

/**
 * AuthPersonDetails generated by hbm2java
 */
@Entity
@Table(name = "AUTH_PERSON_DETAILS")
public class AuthPersonDetails extends Base implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Address address;
	private MfinoServiceProvider mfinoServiceProvider;
	private String firstname;
	private String lastname;
	private String iddesc;
	private String idnumber;
	private Timestamp dateofbirth;
	private Set<Subscriber> subscribers = new HashSet<Subscriber>(0);

	public AuthPersonDetails() {
	}

		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ADDRESSID")
	public Address getAddress() {
		return this.address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MSPID", nullable = false)
	public MfinoServiceProvider getMfinoServiceProvider() {
		return this.mfinoServiceProvider;
	}

	public void setMfinoServiceProvider(
			MfinoServiceProvider mfinoServiceProvider) {
		this.mfinoServiceProvider = mfinoServiceProvider;
	}

	
	@Column(name = "FIRSTNAME", length = 1020)
	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	@Column(name = "LASTNAME", length = 1020)
	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Column(name = "IDDESC", length = 1020)
	public String getIddesc() {
		return this.iddesc;
	}

	public void setIddesc(String iddesc) {
		this.iddesc = iddesc;
	}

	@Column(name = "IDNUMBER", length = 1020)
	public String getIdnumber() {
		return this.idnumber;
	}

	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}

	@Column(name = "DATEOFBIRTH")
	public Timestamp getDateofbirth() {
		return this.dateofbirth;
	}

	public void setDateofbirth(Timestamp dateofbirth) {
		this.dateofbirth = dateofbirth;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "authPersonDetails")
	public Set<Subscriber> getSubscribers() {
		return this.subscribers;
	}

	public void setSubscribers(Set<Subscriber> subscribers) {
		this.subscribers = subscribers;
	}

}
