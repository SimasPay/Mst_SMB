package com.mfino.bsm.iso8583;

import java.util.Map;

import com.mfino.bsm.iso8583.processor.fixtoiso.BalanceInquiryToBankProcessor;
import com.mfino.bsm.iso8583.processor.fixtoiso.GetLastTrxnsToBankProcessor;
import com.mfino.bsm.iso8583.processor.fixtoiso.MoneyTransferReversalToBankProcessor;
import com.mfino.bsm.iso8583.processor.fixtoiso.MoneyTransferToBankProcessor;
import com.mfino.bsm.iso8583.processor.fixtoiso.TransferInquiryToBankProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessorFactory;

public class BSMFixToIsoProcessorFactory implements IFixToIsoProcessorFactory {

	private static final BSMFixToIsoProcessorFactory factory = new BSMFixToIsoProcessorFactory();
	
	private Map<String,String> constantFieldsMap;

	public void setConstantFieldsMap(Map<String,String> map){
		this.constantFieldsMap = map;
	}
	
	@Override
	public IFixToIsoProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException {
		IFixToIsoProcessor processor = null;
		if (request instanceof CMTransferInquiryToBank)
			processor = new TransferInquiryToBankProcessor();
		else if (request instanceof CMBalanceInquiryToBank)
			processor = new BalanceInquiryToBankProcessor();
		else if (request instanceof CMMoneyTransferReversalToBank)
			processor = new MoneyTransferReversalToBankProcessor();
		else if (request instanceof CMMoneyTransferToBank)
			processor = new MoneyTransferToBankProcessor();
		else if (request instanceof CMGetLastTransactionsToBank)
			processor = new GetLastTrxnsToBankProcessor();
		else
			throw new ProcessorNotAvailableException();

		processor.setConstantFieldsMap(constantFieldsMap);
		return processor;
	}
	
	public static BSMFixToIsoProcessorFactory getInstance(){
		return factory;
	}

}
