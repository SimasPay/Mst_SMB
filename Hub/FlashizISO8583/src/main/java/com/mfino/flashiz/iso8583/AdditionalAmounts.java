package com.mfino.flashiz.iso8583;

import java.math.BigDecimal;

import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.ISO87APackager;

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
		if (str.length() < 26) {
			throw new Exception("Cannot parse for additionalamounts.Invalid string");
		}
		AdditionalAmounts aa = new AdditionalAmounts();
		String s = str.substring(0, 2);
		aa.setAccountType(Integer.parseInt(s));
		s = str.substring(2, 4);
		aa.setAmountType(Integer.parseInt(s));
		s = str.substring(4, 7);
		aa.setCurrencyCode(Integer.parseInt(s));
		s = str.substring(8, 26);
		BigDecimal amount = new BigDecimal(s);
		aa.setAmount(amount.divide(new BigDecimal(100)));
		aa.setAmountSign(str.charAt(7));
		return aa;
	}	
	
	public static void main (String[] args) throws Exception {
		ISOChannel channel = new ASCIIChannel (
		"localhost", 31532, new ISO87APackager()
		);
		channel.connect ();
		ISOMsg m = new ISOMsg ();
		m.setMTI ("0800");
		m.set (3, "000000");
		m.set (41, "00000001");
		m.set (70, "301");
		channel.send (m);
		ISOMsg r = channel.receive ();
		channel.disconnect ();
		}
	
	
	
	
	
	
}