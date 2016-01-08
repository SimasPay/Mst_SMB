package com.mfino.dao.query;

/**
 * 
 * @author srikanth
 */
public class BranchCodeQuery extends BaseQuery {

	private int branchCode;
	public int getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(int branchCode) {
		this.branchCode = branchCode;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	private String branchName;
	

}
