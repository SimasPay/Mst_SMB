package com.mfino.domain;

// Generated Sep 27, 2016 5:23:21 PM by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
/**
 * ChargeType generated by hbm2java
 */
@Entity
@Table(name = "CHARGE_TYPE", uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
public class ChargeType extends Base implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FieldName_Name = "name";
	
	private MfinoServiceProvider mfinoServiceProvider;
	private String name;
	private String description;
	private Set<ChargeDefinition> chargeDefinitionsForDependantchargetypeid = new HashSet<ChargeDefinition>(
			0);
	private Set<ChargeDefinition> chargeDefinitionsForChargetypeid = new HashSet<ChargeDefinition>(
			0);
	private Set<TransactionCharge> transactionCharges = new HashSet<TransactionCharge>(
			0);

	public ChargeType() {
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

	

	@Column(name = "NAME", unique = true, nullable = false, length = 1020)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION", length = 1020)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "chargeTypeByDependantchargetypeid")
	public Set<ChargeDefinition> getChargeDefinitionsForDependantchargetypeid() {
		return this.chargeDefinitionsForDependantchargetypeid;
	}

	public void setChargeDefinitionsForDependantchargetypeid(
			Set<ChargeDefinition> chargeDefinitionsForDependantchargetypeid) {
		this.chargeDefinitionsForDependantchargetypeid = chargeDefinitionsForDependantchargetypeid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "chargeTypeByChargetypeid")
	public Set<ChargeDefinition> getChargeDefinitionsForChargetypeid() {
		return this.chargeDefinitionsForChargetypeid;
	}

	public void setChargeDefinitionsForChargetypeid(
			Set<ChargeDefinition> chargeDefinitionsForChargetypeid) {
		this.chargeDefinitionsForChargetypeid = chargeDefinitionsForChargetypeid;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "chargeType")
	public Set<TransactionCharge> getTransactionCharges() {
		return this.transactionCharges;
	}

	public void setTransactionCharges(Set<TransactionCharge> transactionCharges) {
		this.transactionCharges = transactionCharges;
	}

}
