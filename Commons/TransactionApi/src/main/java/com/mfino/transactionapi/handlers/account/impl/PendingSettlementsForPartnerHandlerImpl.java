package com.mfino.transactionapi.handlers.account.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.SCTLSettlementMapQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Partner;
import com.mfino.domain.SctlSettlementMap;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetPendingSettlementsForPartner;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.Result;
import com.mfino.service.EnumTextService;
import com.mfino.service.PartnerService;
import com.mfino.service.SCTLSettlementMapService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.account.PendingSettlementsForPartnerHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.LastNTxnsXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Amar
 * 
 */
@Service("PendingSettlementsForPartnerHandlerImpl")
public class PendingSettlementsForPartnerHandlerImpl extends FIXMessageHandler implements PendingSettlementsForPartnerHandler{
	private static Logger log = LoggerFactory.getLogger(PendingSettlementsForPartnerHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SCTLSettlementMapServiceImpl")
	private SCTLSettlementMapService sctlSettlementMapService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	public Result handle(TransactionDetails transactionDetails) {
		
		ChannelCode	cc = transactionDetails.getCc();
		
		CMGetPendingSettlementsForPartner	pendingSettlements = new CMGetPendingSettlementsForPartner();
		pendingSettlements.setSourceMDN(transactionDetails.getSourceMDN());
		pendingSettlements.setPin(transactionDetails.getSourcePIN());
		pendingSettlements.setPartnerCode(transactionDetails.getPartnerCode());
		pendingSettlements.setSourceApplication(new Integer(String.valueOf(cc.getChannelsourceapplication())));
		pendingSettlements.setChannelCode(cc.getChannelcode());
		pendingSettlements.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		
		log.info("Handling Pending Settlements for Partner webapi request");
		
		LastNTxnsXMLResult result = new LastNTxnsXMLResult();
		result.setEnumTextService(enumTextService);
		SubscriberMdn sourceMDN = subscriberMdnService.getByMDN(pendingSettlements.getSourceMDN());
		

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_GetPendingSettlementsForPartner, pendingSettlements.DumpFields());
		
		pendingSettlements.setTransactionID(transactionsLog.getID());

		result.setSourceMessage(pendingSettlements);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());


		Integer validationResult =transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+pendingSettlements.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
	
		validationResult=transactionApiValidationService.validatePin(sourceMDN, pendingSettlements.getPin());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Pin validation failed for mdn: "+pendingSettlements.getSourceMDN());
			result.setNumberOfTriesLeft(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - sourceMDN.getWrongPINCount());
			result.setNotificationCode(validationResult);
			return result;
		}
		
		Partner partner = partnerService.getPartner(sourceMDN);
		if(partner == null){
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidParnter);
 			return result;
		}

		SCTLSettlementMapQuery query = new SCTLSettlementMapQuery();
		query.setPartnerID(partner.getId().longValue());
		query.setSettlementStatus(CmFinoFIX.SettlementStatus_Initiated);
		List<SctlSettlementMap> pendingSettlementsList = sctlSettlementMapService.get(query);
		
		if(pendingSettlementsList.size() == 0){
			result.setNotificationCode(CmFinoFIX.NotificationCode_NoPendingsettlementsWereFound);
			return result;
		}
		
		Collections.sort(pendingSettlementsList, new Comparator<SctlSettlementMap>() {
			public int compare(SctlSettlementMap ps1, SctlSettlementMap ps2) {
				return ((int) (ps2.getId().intValue() - ps1.getId().intValue()));
			}
		});
		
		result.setPendingSettlements(pendingSettlementsList);
		result.setNotificationCode(CmFinoFIX.NotificationCode_PendingSettlementDetails);
		return result;

	}
	
}
