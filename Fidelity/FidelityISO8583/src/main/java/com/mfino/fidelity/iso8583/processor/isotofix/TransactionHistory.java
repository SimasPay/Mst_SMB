/**
 * 
 */
package com.mfino.fidelity.iso8583.processor.isotofix;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class TransactionHistory {
	private String bankName;
	private String seqNum;
	private String dateTime;
	private String transactionType;
	private String transactionAmount;
	private String fromAccount;
	private String toAccount;
	private String currencyCode;
	private String particulars;
	
	public String getParticulars() {
		return particulars;
	}
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getSeqNum() {
		return seqNum;
	}
	public void setSeqNum(String seqNum) {
		this.seqNum = seqNum;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public String getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}
	public String getToAccount() {
		return toAccount;
	}
	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	private String formatDate(String givenDate) {
		String result = "";
		try {
			if (StringUtils.isNotBlank(givenDate)) {
				SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyyMMdd");
				Date date = sourceDateFormat.parse(givenDate);
				SimpleDateFormat destinationDateFormat = new SimpleDateFormat("dd/MM/yy");
				result = destinationDateFormat.format(date);
			}
		} catch (ParseException e) {
			System.out.println("Error: Date Format Exception --> " + givenDate);
		}
		return result;
	}
	
	private String getActualAmount(String tranAmount) {
		String result = "0";
		BigDecimal HUNDRED = new BigDecimal("100");
		if (StringUtils.isNotBlank(tranAmount)) { 
			BigDecimal amount = new BigDecimal(tranAmount);
			amount = amount.divide(HUNDRED);
			result = amount.toPlainString();
		}
		return result;
	}
	
	private String parseCurrencyCode(String currencyCode) {
		String result = "";
		if ("566".equals(currencyCode)) {
			result = CmFinoFIX.Currency_NGN;
		}
		return result;
	}
	
	private String parseTransactionType(String transactionType) {
		String result = "";
		if (StringUtils.isNotBlank(transactionType)) {
			result = transactionType;
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getBankName() + "|");
		result.append(formatDate(getDateTime()) + "|");
		result.append(parseTransactionType(getTransactionType()) + "|");
		result.append(parseCurrencyCode(getCurrencyCode()) + "|");
		result.append(getTransactionAmount()+ "|");
		result.append(getParticulars());
		return result.toString();
	}
}
