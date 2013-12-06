package com.mfino.nfc.iso8583;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMEchoTestResponseToBank;
import com.mfino.fix.CmFinoFIX.CMEchoTestToBank;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMNFCCardLinkToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardStatusToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlinkReversalToCMS;
import com.mfino.fix.CmFinoFIX.CMNFCCardUnlinkToCMS;
import com.mfino.fix.CmFinoFIX.CMSignOffResponseToBank;
import com.mfino.fix.CmFinoFIX.CMSignOffToBank;
import com.mfino.fix.CmFinoFIX.CMSignOnResponseToBank;
import com.mfino.fix.CmFinoFIX.CMSignOnToBank;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessor;
import com.mfino.iso8583.definitions.isotofix.IIsoToFixProcessorFactory;
import com.mfino.nfc.iso8583.processor.isotofix.BalanceInquiryFromBankProcessor;
import com.mfino.nfc.iso8583.processor.isotofix.GetLastTrxnsFromBankProcessor;
import com.mfino.nfc.iso8583.processor.isotofix.NFCCardLinkFromCMSProcessor;
import com.mfino.nfc.iso8583.processor.isotofix.NFCCardStatusFromCMSProcessor;
import com.mfino.nfc.iso8583.processor.isotofix.NFCCardUnlinkFromCMSProcessor;
import com.mfino.nfc.iso8583.processor.isotofix.NFCCardUnlinkReversalFromCMSProcessor;
import com.mfino.nfc.iso8583.processor.isotofix.networkmanagement.EchoTest;
import com.mfino.nfc.iso8583.processor.isotofix.networkmanagement.EchoTestResponse;
import com.mfino.nfc.iso8583.processor.isotofix.networkmanagement.SignOff;
import com.mfino.nfc.iso8583.processor.isotofix.networkmanagement.SignOffResponse;
import com.mfino.nfc.iso8583.processor.isotofix.networkmanagement.SignOn;
import com.mfino.nfc.iso8583.processor.isotofix.networkmanagement.SignOnResponse;

public class NFCIsoToFixProcessorFactory implements IIsoToFixProcessorFactory {

	private static final NFCIsoToFixProcessorFactory factory = new NFCIsoToFixProcessorFactory();
	
	@Override
	public IIsoToFixProcessor getProcessor(CFIXMsg requestFixMsg) throws ProcessorNotAvailableException {

		IIsoToFixProcessor processor = null;
		
		if (requestFixMsg instanceof CMSignOnToBank)
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
		else if (requestFixMsg instanceof CMGetLastTransactionsToBank)
			processor = new GetLastTrxnsFromBankProcessor();
		else if  (requestFixMsg instanceof CMNFCCardLinkToCMS)
			processor = new NFCCardLinkFromCMSProcessor();
		else if  (requestFixMsg instanceof CMNFCCardUnlinkToCMS)
			processor = new NFCCardUnlinkFromCMSProcessor();
		else if  (requestFixMsg instanceof CMNFCCardUnlinkReversalToCMS)
			processor = new NFCCardUnlinkReversalFromCMSProcessor();
		else if (requestFixMsg instanceof CMBalanceInquiryToBank)
			processor = new BalanceInquiryFromBankProcessor();
		else if (requestFixMsg instanceof CMNFCCardStatusToCMS)
			processor = new NFCCardStatusFromCMSProcessor();
		else throw new ProcessorNotAvailableException();
           return processor;
	}
	
	public static NFCIsoToFixProcessorFactory getInstance(){
		return factory;
	}

}
