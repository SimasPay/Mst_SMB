package com.mfino.bsim.iso8583;

import com.mfino.bsim.iso8583.processor.fixtoiso.BillPaymentToBankProcessor;
import com.mfino.bsim.iso8583.processor.fixtoiso.NewSubscriberActivationToBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.AdviceInquiryFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.BalanceInquiryFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.BillPaymentAmountInquiryFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.BillPaymentInquiryFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.BillPaymentFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.BillPaymentReversalFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.ExistingSubscriberReActivationFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.GetLastTrxnsFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.GetSubscriberDetailsFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.InterBankMoneyTransferFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.InterBankTransferInquiryFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.MoneyTransferFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.MoneyTransferReversalFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.NewSubscriberActivationFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.QRPaymentFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.QRPaymentInquiryFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.QRPaymentReversalFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.TransferInquiryFromBankProcessor;
import com.mfino.bsim.iso8583.processor.isotofix.networkmanagement.EchoTest;
import com.mfino.bsim.iso8583.processor.isotofix.networkmanagement.EchoTestResponse;
import com.mfino.bsim.iso8583.processor.isotofix.networkmanagement.SignOff;
import com.mfino.bsim.iso8583.processor.isotofix.networkmanagement.SignOffResponse;
import com.mfino.bsim.iso8583.processor.isotofix.networkmanagement.SignOn;
import com.mfino.bsim.iso8583.processor.isotofix.networkmanagement.SignOnResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMAdviceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentReversalToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountToBiller;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMEchoTestResponseToBank;
import com.mfino.fix.CmFinoFIX.CMEchoTestToBank;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivationToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationFromBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationToBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentReversalToBank;
import com.mfino.fix.CmFinoFIX.CMQRPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMSignOffResponseToBank;
import com.mfino.fix.CmFinoFIX.CMSignOffToBank;
import com.mfino.fix.CmFinoFIX.CMSignOnResponseToBank;
import com.mfino.fix.CmFinoFIX.CMSignOnToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessorFactory;

public class BSIMIsoToFixProcessorFactory implements IIsoToFixProcessorFactory {

	private static final BSIMIsoToFixProcessorFactory factory = new BSIMIsoToFixProcessorFactory();
	
	@Override
	public IIsoToFixProcessor getProcessor(CFIXMsg requestFixMsg) throws ProcessorNotAvailableException {

		IIsoToFixProcessor processor = null;
		
		if (requestFixMsg instanceof CMInterBankTransferInquiryToBank){
			processor = new  InterBankTransferInquiryFromBankProcessor();
		}
		else if (requestFixMsg instanceof CMInterBankMoneyTransferToBank){
			processor = new InterBankMoneyTransferFromBankProcessor();
		}
		else if (requestFixMsg instanceof CMQRPaymentInquiryToBank)
			processor = new QRPaymentInquiryFromBankProcessor();
		else if (requestFixMsg instanceof CMQRPaymentToBank){
			processor = new QRPaymentFromBankProcessor();
		}
		else if (requestFixMsg instanceof CMQRPaymentReversalToBank)
			processor = new QRPaymentReversalFromBankProcessor();
		else if (requestFixMsg instanceof CMBSIMBillPaymentReversalToBank)
			processor = new BillPaymentReversalFromBankProcessor();
		else if(requestFixMsg instanceof CMBSIMGetAmountToBiller)
			processor = new BillPaymentAmountInquiryFromBankProcessor();
	    else if (requestFixMsg instanceof CMBSIMBillPaymentInquiryToBank)
			processor = new BillPaymentInquiryFromBankProcessor();
		else if (requestFixMsg instanceof CMBSIMBillPaymentToBank)
			processor = new BillPaymentFromBankProcessor();
			
		else if(requestFixMsg instanceof CMBalanceInquiryToBank){
			processor = new BalanceInquiryFromBankProcessor();
		}
		else if(requestFixMsg instanceof CMAdviceInquiryToBank)
			processor = new AdviceInquiryFromBankProcessor();
		else if(requestFixMsg instanceof CMMoneyTransferReversalToBank)
			processor = new MoneyTransferReversalFromBankProcessor();
		else if (requestFixMsg instanceof CMMoneyTransferToBank) 
			processor = new MoneyTransferFromBankProcessor();
		else if (requestFixMsg instanceof CMTransferInquiryToBank)
			processor = new TransferInquiryFromBankProcessor();
		else if (requestFixMsg instanceof CMGetLastTransactionsToBank)
			processor = new GetLastTrxnsFromBankProcessor();
		else if (requestFixMsg instanceof CMSignOnToBank)
			processor = new SignOn();
		else if (requestFixMsg instanceof CMSignOffToBank)
			processor = new SignOff();
		else if (requestFixMsg instanceof CMSignOnResponseToBank)
			processor = new SignOnResponse();
		else if (requestFixMsg instanceof CMSignOffResponseToBank)
			processor = new SignOffResponse();
		else if (requestFixMsg instanceof CMEchoTestToBank)
			processor = new EchoTest();
		else if (requestFixMsg instanceof CMEchoTestResponseToBank)
			processor = new EchoTestResponse();
		else if (requestFixMsg instanceof CMNewSubscriberActivationToBank)
			processor = new NewSubscriberActivationFromBankProcessor();
		else if (requestFixMsg instanceof CMExistingSubscriberReactivationToBank)
			processor = new ExistingSubscriberReActivationFromBankProcessor();
		else if (requestFixMsg instanceof CMGetSubscriberDetailsToBank)
			processor = new GetSubscriberDetailsFromBankProcessor();
		else throw new ProcessorNotAvailableException();
           return processor;
	}
	
	public static BSIMIsoToFixProcessorFactory getInstance(){
		return factory;
	}

}
