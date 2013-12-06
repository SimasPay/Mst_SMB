package com.mfino.transactionapi.handlers.nfc.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMNFCCardStatus;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.service.KYCLevelService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.nfc.NFCCardStatusHandler;
import com.mfino.transactionapi.service.TransactionApiValidationService;

@Service("NFCCardStatusHandlerImpl")
public class NFCCardStatusHandlerImpl extends FIXMessageHandler implements NFCCardStatusHandler{

	private static Logger log = LoggerFactory.getLogger(NFCCardLinkHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("KYCLevelServiceImpl")
	private KYCLevelService kycLevelService;
	
	@Override
	public CFIXMsg handle(CMNFCCardStatus nfcCardStatus) {		
		CFIXMsg response = super.process(nfcCardStatus);	
		return response;
	}

}
