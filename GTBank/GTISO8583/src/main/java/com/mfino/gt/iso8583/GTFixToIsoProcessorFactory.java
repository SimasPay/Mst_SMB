package com.mfino.gt.iso8583;

import java.util.Map;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryToBank;
import com.mfino.gt.iso8583.processor.fixtoiso.BalanceInquiryToGTProcessor;
import com.mfino.gt.iso8583.processor.fixtoiso.GetLastTrxnsToGTProcessor;
import com.mfino.gt.iso8583.processor.fixtoiso.MoneyTransferReversalToGTProcessor;
import com.mfino.gt.iso8583.processor.fixtoiso.MoneyTransferToGTProcessor;
import com.mfino.gt.iso8583.processor.fixtoiso.TransferInquiryToGTProcessor;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessorFactory;

public class GTFixToIsoProcessorFactory implements IFixToIsoProcessorFactory {

	private static final GTFixToIsoProcessorFactory factory = new GTFixToIsoProcessorFactory();
	
	private Map<String,String> constantFieldsMap;
	
	public void setConstantFieldsMap(Map<String,String> map){
		this.constantFieldsMap = map;
	}
	
	@Override
	public IFixToIsoProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException {
		IFixToIsoProcessor processor = null;
		if (request instanceof CMTransferInquiryToBank)
			processor = new TransferInquiryToGTProcessor();
		else if (request instanceof CMBalanceInquiryToBank)
			processor = new BalanceInquiryToGTProcessor();
		else if (request instanceof CMMoneyTransferReversalToBank)
			processor = new MoneyTransferReversalToGTProcessor();
		else if (request instanceof CMMoneyTransferToBank)
			processor = new MoneyTransferToGTProcessor();
		else if (request instanceof CMGetLastTransactionsToBank)
			processor = new GetLastTrxnsToGTProcessor();
		else
			throw new ProcessorNotAvailableException();

		processor.setConstantFieldsMap(constantFieldsMap);
		
		return processor;
	}
	
	public static GTFixToIsoProcessorFactory getInstance(){
		return factory;
	}

}
