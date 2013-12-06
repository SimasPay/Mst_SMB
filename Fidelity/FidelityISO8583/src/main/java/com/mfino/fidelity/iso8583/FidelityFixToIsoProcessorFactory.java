package com.mfino.fidelity.iso8583;

import java.util.Map;

import com.mfino.fidelity.iso8583.processor.fixtoiso.BalanceInquiryToFidelityProcessor;
import com.mfino.fidelity.iso8583.processor.fixtoiso.FidelityISORequestProcessor;
import com.mfino.fidelity.iso8583.processor.fixtoiso.FixToISOService;
import com.mfino.fidelity.iso8583.processor.fixtoiso.GetLastTrxnsToFidelityProcessor;
import com.mfino.fidelity.iso8583.processor.fixtoiso.MoneyTransferReversalToFidelityProcessor;
import com.mfino.fidelity.iso8583.processor.fixtoiso.MoneyTransferToFidelityProcessor;
import com.mfino.fidelity.iso8583.processor.fixtoiso.TransferInquiryToFidelityProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.iso8583.definitions.exceptions.ProcessorNotAvailableException;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessor;
import com.mfino.iso8583.definitions.fixtoiso.IFixToIsoProcessorFactory;

public class FidelityFixToIsoProcessorFactory implements IFixToIsoProcessorFactory {

	private static final FidelityFixToIsoProcessorFactory factory = new FidelityFixToIsoProcessorFactory();
	
	private FixToISOService fixToISOService;
	
	private Map<String,String> constantFieldsMap;
	

	public void setFixToISOService(FixToISOService fixToISOService) {
		this.fixToISOService = fixToISOService;
	}
	
	public void setConstantFieldsMap(Map<String,String> map){
		this.constantFieldsMap = map;
	}
	
	@Override
	public IFixToIsoProcessor getProcessor(CFIXMsg request) throws ProcessorNotAvailableException {
		IFixToIsoProcessor processor = null;
		FidelityISORequestProcessor fidelityProcessor = null;
		if (request instanceof CMTransferInquiryToBank)
			fidelityProcessor = new TransferInquiryToFidelityProcessor();
		else if (request instanceof CMBalanceInquiryToBank)
			fidelityProcessor = new BalanceInquiryToFidelityProcessor();
		else if (request instanceof CMMoneyTransferToBank)
			fidelityProcessor = new MoneyTransferToFidelityProcessor();
		else if (request instanceof CMGetLastTransactionsToBank)
			fidelityProcessor = new GetLastTrxnsToFidelityProcessor();
		else if(request instanceof CMMoneyTransferReversalToBank)
			fidelityProcessor = new MoneyTransferReversalToFidelityProcessor();
		else
			throw new ProcessorNotAvailableException();

		fidelityProcessor.setConstantFieldsMap(constantFieldsMap);
		fidelityProcessor.setFixToISOService(fixToISOService);
		processor = fidelityProcessor;
		return processor;
	}
	
	public static FidelityFixToIsoProcessorFactory getInstance(){
		return factory;
	}	

}
