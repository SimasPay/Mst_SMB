package com.mfino.bsm.uangku.iso8583;

import java.util.Map;

import com.mfino.bsm.uangku.iso8583.processor.fixtoiso.BalanceInquiryToBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.fixtoiso.BillPaymentAmountInquiryToBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.fixtoiso.BillPaymentToBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.fixtoiso.GetLastTrxnsToBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.fixtoiso.InterBankMoneyTransferToBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.fixtoiso.InterBankTransferInquiryToBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.fixtoiso.MoneyTransferReversalToBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.fixtoiso.MoneyTransferToBankProcessor;
import com.mfino.bsm.uangku.iso8583.processor.fixtoiso.TransferInquiryToBankProcessor;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMBillPaymentToBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMBSIMGetAmountToBiller;
import com.mfino.fix.CmFinoFIX.CMInterBankMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMInterBankTransferInquiryToBank;
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
		if (request instanceof CMBSIMGetAmountToBiller)
			processor = new BillPaymentAmountInquiryToBankProcessor();
		else if (request instanceof CMBSIMBillPaymentToBank)
			processor = new BillPaymentToBankProcessor();
		else if (request instanceof CMInterBankTransferInquiryToBank)
			processor = new InterBankTransferInquiryToBankProcessor();
		else if(request instanceof CMInterBankMoneyTransferToBank)
			processor = new InterBankMoneyTransferToBankProcessor();	
		else if (request instanceof CMTransferInquiryToBank)
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
