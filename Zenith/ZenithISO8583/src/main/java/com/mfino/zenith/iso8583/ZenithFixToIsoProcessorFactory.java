package com.mfino.zenith.iso8583;

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
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessorFactory;
import com.mfino.zenith.iso8583.processor.fixtoiso.BalanceInquiryToZenithProcessor;
import com.mfino.zenith.iso8583.processor.fixtoiso.DSTVPaymentToZenithProcessor;
import com.mfino.zenith.iso8583.processor.fixtoiso.DSTVTransferInquiryToZenithProcessor;
import com.mfino.zenith.iso8583.processor.fixtoiso.GetLastTrxnsToZenithProcessor;
import com.mfino.zenith.iso8583.processor.fixtoiso.MoneyTransferReversalToZenithProcessor;
import com.mfino.zenith.iso8583.processor.fixtoiso.MoneyTransferToZenithProcessor;
import com.mfino.zenith.iso8583.processor.fixtoiso.TransferInquiryToZenithProcessor;
import com.mfino.zenith.iso8583.processor.fixtoiso.VisafoneAirtimePurchaseToZenithProcessor;
import com.mfino.zenith.iso8583.processor.fixtoiso.VisafoneAirtimeTransferInquiryToZenithProcessor;

public class ZenithFixToIsoProcessorFactory implements IFixToIsoProcessorFactory {

	private static final ZenithFixToIsoProcessorFactory factory = new ZenithFixToIsoProcessorFactory();
	
	private Map<String,String> constantFieldsMap;
	
	public void setConstantFieldsMap(Map<String,String> map){
		this.constantFieldsMap = map;
	}
	
	@Override
	public IFixToIsoProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException {
		IFixToIsoProcessor processor = null;
		if (request instanceof CMVisafoneAirtimeTransferInquiryToBank)
			processor = new VisafoneAirtimeTransferInquiryToZenithProcessor();
		else if (request instanceof CMVisafoneAirtimeMoneyTransferToBank)
			processor = new VisafoneAirtimePurchaseToZenithProcessor();
		if (request instanceof CMDSTVTransferInquiryToBank)
			processor = new DSTVTransferInquiryToZenithProcessor();
		else if (request instanceof CMDSTVMoneyTransferToBank)
			processor = new DSTVPaymentToZenithProcessor();
		else if (request instanceof CMTransferInquiryToBank)
			processor = new TransferInquiryToZenithProcessor();
		else if (request instanceof CMBalanceInquiryToBank)
			processor = new BalanceInquiryToZenithProcessor();
		else if (request instanceof CMMoneyTransferReversalToBank)
			processor = new MoneyTransferReversalToZenithProcessor();
		else if (request instanceof CMMoneyTransferToBank)
			processor = new MoneyTransferToZenithProcessor();
		else if (request instanceof CMGetLastTransactionsToBank)
			processor = new GetLastTrxnsToZenithProcessor();
		else
			throw new ProcessorNotAvailableException();
		
		processor.setConstantFieldsMap(constantFieldsMap);
		
		return processor;
	}
	
	public static ZenithFixToIsoProcessorFactory getInstance(){
		return factory;
	}

}
