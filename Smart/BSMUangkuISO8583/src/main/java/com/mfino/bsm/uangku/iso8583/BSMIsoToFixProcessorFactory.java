package com.mfino.bsm.uangku.iso8583;

import com.mfino.bsm.uangku.iso8583.processor.isotofix.BalanceInquiryFromBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.BillPaymentAmountInquiryFromBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.BillPaymentFromBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.GetLastTrxnsFromBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.InterBankMoneyTransferFromBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.InterBankTransferInquiryFromBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.MoneyTransferFromBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.MoneyTransferReversalFromBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.TransferInquiryFromBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.networkmanagement.EchoTest;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.networkmanagement.EchoTestResponse;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.networkmanagement.SignOff;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.networkmanagement.SignOffResponse;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.networkmanagement.SignOn;
import com.mfino.bsm.uangku.iso8583.processor.isotofix.networkmanagement.SignOnResponse;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMEchoTestResponseToBank;
import com.mfino.fix.CmFinoFIX.CMEchoTestToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountToBiller;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMSignOffResponseToBank;
import com.mfino.fix.CmFinoFIX.CMSignOffToBank;
import com.mfino.fix.CmFinoFIX.CMSignOnResponseToBank;
import com.mfino.fix.CmFinoFIX.CMSignOnToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessorFactory;

public class BSMIsoToFixProcessorFactory implements IIsoToFixProcessorFactory {

	private static final BSMIsoToFixProcessorFactory factory = new BSMIsoToFixProcessorFactory();
	
	@Override
	public IIsoToFixProcessor getProcessor(CFIXMsg requestFixMsg) throws ProcessorNotAvailableException {

		IIsoToFixProcessor processor = null;
		

		if(requestFixMsg instanceof CMBalanceInquiryToBank)
			processor = new BalanceInquiryFromBankProcessor();
		if (requestFixMsg instanceof CMBSIMGetAmountToBiller)
			processor = new BillPaymentAmountInquiryFromBankProcessor();
		else if (requestFixMsg instanceof CMBSIMBillPaymentToBank)
			processor = new BillPaymentFromBankProcessor();
		else if (requestFixMsg instanceof CMInterBankTransferInquiryToBank)
			processor = new  InterBankTransferInquiryFromBankProcessor();
		else if (requestFixMsg instanceof CMInterBankMoneyTransferToBank)
			processor = new InterBankMoneyTransferFromBankProcessor();
		else if(requestFixMsg instanceof CMMoneyTransferReversalToBank)
			processor = new MoneyTransferReversalFromBankProcessor();
		else if (requestFixMsg instanceof CMMoneyTransferToBank) 
			processor = new MoneyTransferFromBankProcessor();
		else if (requestFixMsg instanceof CMInterBankTransferInquiryToBank)
			processor = new TransferInquiryFromBankProcessor();
		else if (requestFixMsg instanceof CMInterBankMoneyTransferToBank) 
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
		else throw new ProcessorNotAvailableException();

		return processor;
	}
	
	public static BSMIsoToFixProcessorFactory getInstance(){
		return factory;
	}

}
