package com.mfino.bsm.uangku.iso8583;

import java.math.BigDecimal;

public class AdditionalAmounts {
	private int	 AccountType;
	private int	 AmountType;
	private int	 CurrencyCode;
	private char	AmountSign;
	private BigDecimal	Amount;

	private AdditionalAmounts() {
	}
	
	public void setCurrencyCode(int currencyCode) {
		CurrencyCode = currencyCode;
	}

	public int getCurrencyCode() {
		return CurrencyCode;
	}

	public void setAccountType(int accountType) {
		AccountType = accountType;
	}

	public int getAccountType() {
		return AccountType;
	}

	public void setAmountType(int amountType) {
		AmountType = amountType;
	}

	public int getAmountType() {
		return AmountType;
	}

	public void setAmountSign(char amountSign) {
		AmountSign = amountSign;
	}

	public char getAmountSign() {
		return AmountSign;
	}

	public void setAmount(BigDecimal amount) {
		Amount = amount;
	}

	public BigDecimal getAmount() {
		return Amount;
	}

	public static AdditionalAmounts parseAdditionalAmounts(String str) throws Exception {
		if (str.length() != 20) {
			throw new Exception("Cannot parse for additionalamounts.Invalid string");
		}
		AdditionalAmounts aa = new AdditionalAmounts();
		String s = str.substring(0, 2);
		aa.setAccountType(Integer.parseInt(s));
		s = str.substring(2, 4);
		aa.setAmountType(Integer.parseInt(s));
		s = str.substring(4, 7);
		aa.setCurrencyCode(Integer.parseInt(s));
		s = str.substring(8, 20);
		aa.setAmount(new BigDecimal(s));
		aa.setAmountSign(str.charAt(7));
		return aa;
	}	
}