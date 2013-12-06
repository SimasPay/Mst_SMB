/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author Maruthi
 */
public class BillPaymentTransactionQuery extends BaseQuery  {
	private Long transactionID;
	private String billerName; 

	public void setTransactionID(Long transactionID) {
		this.transactionID = transactionID;
	}

	public Long getTransactionID() {
		return transactionID;
	}

	public void setBillerName(String billerName) {
		this.billerName = billerName;
	}

	public String getBillerName() {
		return billerName;
	}
	

}
