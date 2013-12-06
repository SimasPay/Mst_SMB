/**
 * 
 */
package com.mfino.dao.query;

import java.util.Date;

/**
 * @author Bala Sunku
 *
 */
public class ChargeDefinitionQuery extends BaseQuery {
	
	private String ExactName;
	private String Name;
	private Date startDate;
	private Date endDate;
	private Long chargeTypeId;
	private boolean FundingPartnerAndPocketNotNull;
	
	public String getExactName() {
		return ExactName;
	}
	public void setExactName(String exactName) {
		ExactName = exactName;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
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
	public Long getChargeTypeId() {
		return chargeTypeId;
	}
	public void setChargeTypeId(Long chargeTypeId) {
		this.chargeTypeId = chargeTypeId;
	}
	public void setFundingPartnerAndPocketNotNull(
			boolean fundingPartnerAndPocketNotNull) {
		FundingPartnerAndPocketNotNull = fundingPartnerAndPocketNotNull;
	}
	public boolean isFundingPartnerAndPocketNotNull() {
		return FundingPartnerAndPocketNotNull;
	}
}
