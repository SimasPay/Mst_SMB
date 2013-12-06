/**
 * 
 */
package com.mfino.dao.query;

import java.util.Date;

/**
 * @author Bala Sunku
 *
 */
public class ChargeTypeQuery extends BaseQuery {
	
	private String ExactName;
	private String Name;
	private Date startDate;
	private Date endDate;
	private Long notEqualId;
	
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
	public Long getNotEqualId() {
		return notEqualId;
	}
	public void setNotEqualId(Long notEqualId) {
		this.notEqualId = notEqualId;
	}

}
