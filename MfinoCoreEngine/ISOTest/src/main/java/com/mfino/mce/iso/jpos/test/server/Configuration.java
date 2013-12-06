package com.mfino.mce.iso.jpos.test.server;

import java.io.File;

/**
 * @author Sreenath
 */
//a Singleton Design pattern class for storing the path and transactionNumber ,initialBalance ,mockMode
// and using a single instance of  the account details object
public class Configuration{
	private static Configuration configObject;
	int TrxnNo=Constants.NUMBER_OF_TRANSACTIONS_TO_BE_STORED;
	String path ;
	String initialBalance=Constants.DEFAULT_BALANCE;
	String mockMode = Constants.DEFAULT_MOCKMODE;
	public String getMockMode() {
		return mockMode;
	}
	public void setMockMode(String mockMode) {
		this.mockMode = mockMode;
	}
	public String getInitialBalance() {
		return initialBalance;
	}
	public void setInitialBalance(Integer initialBalance) {
		String iBalance = initialBalance.toString();
		while(iBalance.length()<12){
			iBalance = "0"+iBalance;
		}
		this.initialBalance = iBalance;
	}
	private Configuration(){
		//singleton class has private cons
		File dir = new File("CustomerBankData");
		dir.mkdir();
		path = dir.getAbsolutePath();
		System.out.println("default directory path="+path);

	}
	//static method for being able to access with class level
	public static synchronized Configuration getConfigObject(){
		if(configObject == null){
			configObject = new Configuration();
		}
		return configObject;
	}
	public synchronized void setTrxnNo(int trxnNo) {
		TrxnNo = trxnNo;
	}
	public synchronized void setPath(String path) {
		this.path = path;
	}
	public synchronized static AccountDetails getAccountDetailsObject(){
		AccountDetails accDetails = new AccountDetails();
				return accDetails;
	}
	public synchronized int getTrxnNo(){
		
		return TrxnNo;
	}
	public synchronized String getPath(){
		return path;
	}
	public synchronized boolean isMockModeOn(){
		if(mockMode.equals("on")){
			return true;
		}else{
			return false;
		}
	}
	//make it non cloneable
	public Object clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();
	}
}
