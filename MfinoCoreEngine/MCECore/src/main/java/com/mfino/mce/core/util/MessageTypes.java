package com.mfino.mce.core.util;

import java.util.HashMap;
import java.util.Map;

import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankAccountBalanceInquiry;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMCashIn;
import com.mfino.fix.CmFinoFIX.CMCashInInquiry;
import com.mfino.fix.CmFinoFIX.CMCashOut;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiry;
import com.mfino.fix.CmFinoFIX.CMChargeDistribution;
import com.mfino.fix.CmFinoFIX.CMFIXResponse;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMPurchaseInquiry;
import com.mfino.fix.CmFinoFIX.CMSettlementOfCharge;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistrationCashIn;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;

/**
 * 
 * @author sasidhar
 *
 */
public enum MessageTypes {
	
	
	CMBankAccountBalanceInquiry(1009, CMBankAccountBalanceInquiry.class),
	CMFIXResponse(1009, CMFIXResponse.class),
	CMBalanceInquiryToBank(1018, CMBalanceInquiryToBank.class),
	CMBalanceInquiryFromBank(1019, CMBalanceInquiryFromBank.class),
	CMBankAccountToBankAccount(1008 ,CMBankAccountToBankAccount.class),
	CMTransferInquiryFromBank(1021 ,CMTransferInquiryFromBank.class),
	CMTransferInquiryToBank(1020, CMTransferInquiryToBank.class),
	CMBankAccountToBankAccountConfirmation(1032 , CMBankAccountToBankAccountConfirmation.class),
	CMMoneyTransferFromBank(1023, CMMoneyTransferFromBank.class),
	CMMoneyTransferToBank(1022, CMMoneyTransferToBank.class),
	CMSubscriberRegistrationCashIn(1268, CMSubscriberRegistrationCashIn.class),
	CMChargeDistribution(1269, CMChargeDistribution.class),
	CMSettlementOfCharge(1270, CMSettlementOfCharge.class),
	CMCashInInquiry(1271, CMCashInInquiry.class),
	CMCashIn(1272, CMCashIn.class),
	CMCashOutInquiry(1273, CMCashOutInquiry.class),
	CMCashOut(1274, CMCashOut.class),
	CMPurchaseInquiry(1285, CMPurchaseInquiry.class),
	CMPurchase(1286, com.mfino.fix.CmFinoFIX.CMPurchase.class),
	DSTVPaymentInquiry(1292, com.mfino.fix.CmFinoFIX.CMDSTVPaymentInquiry.class),
	DSTVPayment(1293, com.mfino.fix.CmFinoFIX.CMDSTVPayment.class),
	DSTVTransferInquiryToBank(1294, com.mfino.fix.CmFinoFIX.CMDSTVTransferInquiryToBank.class),
	DSTVTransferInquiryFromBank(1295, com.mfino.fix.CmFinoFIX.CMDSTVTransferInquiryFromBank.class),
	DSTVMoneyTransferToBank(1296, com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferToBank.class),
	DSTVMoneyTransferFromBank(1297, com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferFromBank.class),
	AirtimePurchaseInquiry(1302, com.mfino.fix.CmFinoFIX.CMAirtimePurchaseInquiry.class),
	AirtimePurchase(1303, com.mfino.fix.CmFinoFIX.CMAirtimePurchase.class),
	VisafoneAirtimePurchaseInquiry(1304, com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePurchaseInquiry.class),
	VisafoneAirtimePurchase(1305, com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePurchase.class),
	VisafoneAirtimeTransferInquiryToBank(1306, com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryToBank.class),
	VisafoneAirtimeTransferInquiryFromBank(1307, com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryFromBank.class),
	VisafoneAirtimeMoneyTransferToBank(1308, com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferToBank.class),
	VisafoneAirtimeMoneyTransferFromBank(1309, com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferFromBank.class),
	VisafoneAirtimeMoneyTransferReversalToBank(1310, com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferReversalToBank.class),
	VisafoneAirtimeMoneyTransferReversalFromBank(1311, com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferReversalFromBank.class),
	BankTellerCashIn(1316,com.mfino.fix.CmFinoFIX.CMBankTellerCashIn.class),
	BankTellerCashInConfirm(1317,com.mfino.fix.CmFinoFIX.CMBankTellerCashInConfirm.class),
	BankTellerTransferInquiryToBank(1312, com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryToBank.class),
	BankTellerTransferInquiryFromBank(1313, com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryFromBank.class),
	BankTellerMoneyTransferToBank(1314, com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank.class),
	BankTellerMoneyTransferFromBank(1315, com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferFromBank.class),
	BankTellerCashOut(1322,com.mfino.fix.CmFinoFIX.CMBankTellerCashOut.class),
	BankTellerCashOutConfirm(1323,com.mfino.fix.CmFinoFIX.CMBankTellerCashOutConfirm.class),
	CMReverseTransactionInquiry(1329, com.mfino.fix.CmFinoFIX.CMReverseTransactionInquiry.class),
	CMReverseTransaction(1330, com.mfino.fix.CmFinoFIX.CMReverseTransaction.class),
	VisafoneAirtimePendingCommodityTransferRequest(1334, com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePendingCommodityTransferRequest.class),
	InterBankFundsTransferInquiry(1331, com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferInquiry.class),
	InterBankFundsTransfer(1332, com.mfino.fix.CmFinoFIX.CMInterBankFundsTransfer.class),
	InterBankFundsTransferStatus(1333, com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferStatus.class),
	TransferInquiryToNonRegistered(1337, com.mfino.fix.CmFinoFIX.CMTransferInquiryToNonRegistered.class),
	TransferToNonRegistered(1338, com.mfino.fix.CmFinoFIX.CMTransferToNonRegistered.class),
	TransactionReversal(1349, com.mfino.fix.CmFinoFIX.CMTransactionReversal.class),
	CashOutAtATMInquiry(1372, com.mfino.fix.CmFinoFIX.CMCashOutAtATMInquiry.class),
	CashOutAtATM(1373, com.mfino.fix.CmFinoFIX.CMCashOutAtATM.class),
	WithdrawFromATMInquiry(1375, com.mfino.fix.CmFinoFIX.CMWithdrawFromATMInquiry.class),
	WithdrawFromATM(1376, com.mfino.fix.CmFinoFIX.CMWithdrawFromATM.class),
	PinLessInquiryLessTransfer(1377, com.mfino.fix.CmFinoFIX.CMPinLessInquiryLessTransfer.class),
	FundAllocationInquiry(1392,com.mfino.fix.CmFinoFIX.CMFundAllocationInquiry.class),
	FundAllocationConfirm(1393,com.mfino.fix.CmFinoFIX.CMFundAllocationConfirm.class),
	FundWithdrawalInquiry(1394,com.mfino.fix.CmFinoFIX.CMFundWithdrawalInquiry.class),
	FundWithdrawalConfirm(1395,com.mfino.fix.CmFinoFIX.CMFundWithdrawalConfirm.class);
	
	MessageTypes(Integer messageCode, Class klass){
		this.messageCode = messageCode;
		this.klass = klass;
	}
	
	private Integer messageCode;
	private Class klass;
	private static Map<Class, MessageTypes> messageTypesMap = null;
	/*Code before using find bug tool
	  public static Integer getMessageCode(CMBase base){
		Integer messageCode = -1;
		
		if(messageTypesMap == null){
			messageTypesMap = new HashMap<Class, MessageTypes>();
			
			MessageTypes msgTypes[] = MessageTypes.values();
			
			for (int i = 0; i < msgTypes.length; i++) {
				messageTypesMap.put(msgTypes[i].getKlass(), msgTypes[i]);
			}
		}
		
		MessageTypes msgTypes = messageTypesMap.get(base.getClass());
		
		if(msgTypes != null){
			messageCode = msgTypes.getMessageCode();
		}
		
		return messageCode;
	}
	 */
	
	static{
		messageTypesMap = new HashMap<Class, MessageTypes>();
		
		MessageTypes msgTypes[] = MessageTypes.values();
		
		for (int i = 0; i < msgTypes.length; i++) {
			messageTypesMap.put(msgTypes[i].getKlass(), msgTypes[i]);
		}
	}
	
	public static Integer getMessageCode(CMBase base){
		Integer messageCode = -1;
		
		/*if(messageTypesMap == null){
			messageTypesMap = new HashMap<Class, MessageTypes>();
			
			MessageTypes msgTypes[] = MessageTypes.values();
			
			for (int i = 0; i < msgTypes.length; i++) {
				messageTypesMap.put(msgTypes[i].getKlass(), msgTypes[i]);
			}
		}*/
		
		MessageTypes msgTypes = messageTypesMap.get(base.getClass());
		
		if(msgTypes != null){
			messageCode = msgTypes.getMessageCode();
		}
		
		return messageCode;
	}

	public Integer getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(Integer messageCode) {
		this.messageCode = messageCode;
	}

	public Class getKlass() {
		return klass;
	}

	public void setKlass(Class klass) {
		this.klass = klass;
	}
	
	@Override
	public String toString() {
		return "Class="+getKlass() + ": messageCode="+getMessageCode();
	}
}
