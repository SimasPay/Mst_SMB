/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;


/**
 *
 * @author Siddhartha Chinthapally
 */
public class AuditLogQuery extends BaseQuery {
	private String createdBy;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	
  
}
