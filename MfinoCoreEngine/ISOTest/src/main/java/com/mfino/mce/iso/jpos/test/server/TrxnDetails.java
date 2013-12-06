package com.mfino.mce.iso.jpos.test.server;
/**
 * 
 * @author Sreenath
 *
 */
//TrxnDetails class stores the data of the transaction that happened so that the object can be used for passing to other methods

public class TrxnDetails {

    private String initBalance;
    private String id;
    private String date;
    private String credit;
    private String debit;

    private String stan;
    private String trxnType;
    private int trxnNo;

    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	public String getDebit() {
		return debit;
	}
	public void setDebit(String debit) {
		this.debit = debit;
	}
	public String getInitBalance() {

		return initBalance;
	}
	public void setInitBalance(String initBalance) {
		this.initBalance = initBalance;
	}
	public String getStan() {
		return stan;
	}
	public void setStan(String stan) {
		this.stan = stan;
	}
	public String getTrxnType() {
		return trxnType;
	}
	public void setTrxnType(String trxnType) {
		this.trxnType = trxnType;
	}
	public int getTrxnNo() {
		return trxnNo;
	}
	public void setTrxnNo(int trxnNo) {
		this.trxnNo = trxnNo;
	}

    }
    

