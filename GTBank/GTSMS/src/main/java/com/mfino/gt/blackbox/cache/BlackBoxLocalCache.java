package com.mfino.gt.blackbox.cache;

public interface BlackBoxLocalCache 
{
	String getConvertedAccNum(String accountNumber);
	String addAccNum(String accountNumber, String ConvertedAccountNumber);
}
