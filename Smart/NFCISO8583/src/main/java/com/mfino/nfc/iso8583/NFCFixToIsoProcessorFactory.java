package com.mfino.nfc.iso8583;

import java.util.Map;
import java.util.Set;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMNFCCardLinkReversalToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardLinkToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardStatusToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlinkReversalToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlinkToCMS;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessorFactory;
import com.mfino.nfc.iso8583.processor.fixtoiso.BalanceInquiryToBankProcessor;
import com.mfino.nfc.iso8583.processor.fixtoiso.GetLastTrxnsToBankProcessor;
import com.mfino.nfc.iso8583.processor.fixtoiso.NFCCardLinkReversalToCMSProcessor;
import com.mfino.nfc.iso8583.processor.fixtoiso.NFCCardLinkToCMSProcessor;
import com.mfino.nfc.iso8583.processor.fixtoiso.NFCCardStatusToCMSProcessor;
import com.mfino.nfc.iso8583.processor.fixtoiso.NFCCardUnlinkReversalToCMSProcessor;
import com.mfino.nfc.iso8583.processor.fixtoiso.NFCCardUnlinkToCMSProcessor;

public class NFCFixToIsoProcessorFactory implements IFixToIsoProcessorFactory {

	private static final NFCFixToIsoProcessorFactory factory = new NFCFixToIsoProcessorFactory();
	
	private Map<String,String> constantFieldsMap;
	
	private Set<String> offlineBillers;
	
	public void setConstantFieldsMap(Map<String,String> map){
		this.constantFieldsMap = map;
	}

	@Override
	public IFixToIsoProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException {
		IFixToIsoProcessor processor = null;
//		if (request instanceof CMInterBankTransferInquiryToBank)
//			processor = new InterBankTransferInquiryToBankProcessor();
//		else if(request instanceof CMInterBankMoneyTransferToBank){
//			processor = new InterBankMoneyTransferToBankProcessor();
//		}		
//		else if(request instanceof CMBSIMGetAmountToBiller)
//			processor = new BillPaymentAmountInquiryToBankProcessor();
//		else if (request instanceof CMBSIMBillPaymentReversalToBank)
//			processor = new BillPaymentReversalToBankProcessor();
//		else if (request instanceof CMBSIMBillPaymentInquiryToBank){
//			BillPaymentInquiryToBankProcessor billPaymentInquiryToBankProcessor = new BillPaymentInquiryToBankProcessor();
//			billPaymentInquiryToBankProcessor.setOfflineBillers(offlineBillers);
//			processor = billPaymentInquiryToBankProcessor;
//		}
//		else if (request instanceof CMBSIMBillPaymentToBank)
//			processor = new BillPaymentToBankProcessor();
//		else if (request instanceof CMTransferInquiryToBank)
//			processor = new TransferInquiryToBankProcessor();
//		else if (request instanceof CMBalanceInquiryToBank)
//			processor = new BalanceInquiryToBankProcessor();
//		else if (request instanceof CMMoneyTransferReversalToBank)
//			processor = new MoneyTransferReversalToBankProcessor();
//		else if (request instanceof CMMoneyTransferToBank)
//			processor = new MoneyTransferToBankProcessor();
//		else 
			if (request instanceof CMGetLastTransactionsToBank)
			processor = new GetLastTrxnsToBankProcessor();
			else if (request instanceof CMNFCCardLinkReversalToCMS)
				processor = new NFCCardLinkReversalToCMSProcessor();
			else if (request instanceof CMNFCCardLinkToCMS)
				processor = new NFCCardLinkToCMSProcessor();
			else if (request instanceof CMNFCCardUnlinkToCMS)
				processor = new NFCCardUnlinkToCMSProcessor();
			else if (request instanceof CMNFCCardUnlinkReversalToCMS)
				processor = new NFCCardUnlinkReversalToCMSProcessor();
			else if (request instanceof CMBalanceInquiryToBank)
				processor = new BalanceInquiryToBankProcessor();
			else if (request instanceof CMNFCCardStatusToCMS)
				processor = new NFCCardStatusToCMSProcessor();
//		else if (request instanceof CMNewSubscriberActivationToBank)
//			processor = new NewSubscriberActivationToBankProcessor();
//		else if (request instanceof CMExistingSubscriberReactivationToBank)
//			processor = new ExistingSubscriberReActivationToBankProcessor();
		else
			throw new ProcessorNotAvailableException();

		processor.setConstantFieldsMap(constantFieldsMap);
		return processor;
	}
	
	public static NFCFixToIsoProcessorFactory getInstance(){
		return factory;
	}

	public Set<String> getOfflineBillers() {
		return offlineBillers;
	}

	public void setOfflineBillers(Set<String> offlineBillers) {
		this.offlineBillers = offlineBillers;
	}
}
