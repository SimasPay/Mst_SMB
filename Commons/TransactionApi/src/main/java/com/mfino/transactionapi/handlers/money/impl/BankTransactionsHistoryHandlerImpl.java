package com.mfino.transactionapi.handlers.money.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetBankAccountTransactions;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank.CGEntries;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.service.EnumTextService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.money.BankTransactionsHistoryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.LastNTxnsXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/*
 * @author Karthik
 * 
 */
@Service("BankTransactionsHistoryHandlerImpl")
public class BankTransactionsHistoryHandlerImpl extends FIXMessageHandler implements BankTransactionsHistoryHandler{
	private static Logger log = LoggerFactory.getLogger(BankTransactionsHistoryHandlerImpl.class);

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;

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
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	// FIXME have to integrate with bankid that is added to the notification by
	// Vishal and billpayments
	// messages

	public Result handle(TransactionDetails transactionDetails) {
		
		int maxCount = systemParametersService.getInteger(SystemParameterKeys.MAX_TXN_COUNT_IN_HISTORY);
		if (maxCount == -1) {
			maxCount = 3;
		}
		ChannelCode cc = transactionDetails.getCc();
		String PocketCode = transactionDetails.getSourcePocketCode();

		CMGetBankAccountTransactions transactionsHistory = new CMGetBankAccountTransactions();
		transactionsHistory.setSourceMDN(transactionDetails.getSourceMDN());
		transactionsHistory.setPin(transactionDetails.getSourcePIN());
		transactionsHistory.setMaxCount(maxCount);
		transactionsHistory.setSourceApplication((int)cc.getChannelsourceapplication());
		transactionsHistory.setChannelCode(cc.getChannelcode());
		transactionsHistory.setServletPath(CmFinoFIX.ServletPath_BankAccount);
		transactionsHistory.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		transactionsHistory.setCardPAN(transactionDetails.getCardPAN());
		transactionsHistory.setCardAlias(transactionDetails.getCardAlias());
		
		log.info("Handling bank last n transactions history webapi request for MDN --> " + transactionsHistory.getSourceMDN());
		LastNTxnsXMLResult result = new LastNTxnsXMLResult();
		result.setEnumTextService(enumTextService);

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_GetTransactions, transactionsHistory.DumpFields());
		transactionsHistory.setTransactionID(transactionsLog.getID());
		
		result.setSourceMessage(transactionsHistory);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setSourceMDN(transactionsHistory.getSourceMDN());
		result.setTransactionID(transactionsLog.getID());

		SubscriberMdn sourceMDN= subscriberMdnService.getByMDN(transactionsHistory.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+sourceMDN.getMdn()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		addCompanyANDLanguageToResult(sourceMDN, result);
		Pocket sourcePocket = null;

		sourcePocket = pocketService.getDefaultPocket(sourceMDN, PocketCode);

		if(!systemParametersService.getBankServiceStatus())	{
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
		}

		if (sourcePocket.getCardpan() == null) {
			result.setNotificationCode(CmFinoFIX.NotificationCode_BankAccountCardPANMissing);
			return result;
		}

		result.setBankCode(sourcePocket.getPocketTemplate().getBankcode().intValue());
		if(!(sourcePocket.getPocketTemplate().getType()==(CmFinoFIX.PocketType_BankAccount)))
		{
			result.setNotificationCode(CmFinoFIX.NotificationCode_NotBankAccount);
			return result;
		}

		validationResult = transactionApiValidationService.validateSourcePocket(sourcePocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(sourcePocket!=null? sourcePocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		
		if (sourcePocket != null) {
			log.info("Pocket Type = " + sourcePocket.getPocketTemplate().getType());
			result.setPocketDescription(sourcePocket.getPocketTemplate().getDescription());
		}

		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(transactionsHistory.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(transactionDetails.getServiceName());
		sc.setTransactionTypeName(transactionDetails.getTransactionName());
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(transactionsLog.getID());
		sc.setTransactionIdentifier(transactionsHistory.getTransactionIdentifier());

		try{
			transaction =transactionChargingService.getCharge(sc);
		}catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
 			return result;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
 			return result;
		}
		ServiceChargeTransactionLog sctl = transaction.getServiceChargeTransactionLog();

		transactionsHistory.setPocketID(sourcePocket.getId().longValue());
		CMGetBankAccountTransactions bankTransactionsReq = new CMGetBankAccountTransactions();
		bankTransactionsReq.setSourceMDN(transactionsHistory.getSourceMDN());
		bankTransactionsReq.setPin(transactionsHistory.getPin());
		bankTransactionsReq.setSourceApplication(transactionsHistory.getSourceApplication());
		bankTransactionsReq.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		bankTransactionsReq.setBankCode(sourcePocket.getPocketTemplate().getBankcode().intValue());
		bankTransactionsReq.setPocketID(sourcePocket.getId().longValue());
		bankTransactionsReq.setTransactionID(transactionsLog.getID());
		bankTransactionsReq.setServiceChargeTransactionLogID(sctl.getID());
		bankTransactionsReq.setMaxCount(transactionsHistory.getMaxCount());
		
		CFIXMsg response = super.process(bankTransactionsReq);
		if (response instanceof CmFinoFIX.CMGetLastTransactionsFromBank) {
			log.info("Got the Bank Transaction Histroy from Bank.");
			if (sctl != null) {
				sctl.setCalculatedCharge(BigDecimal.ZERO);
				transactionChargingService.completeTheTransaction(sctl);				
			}
			CmFinoFIX.CMGetLastTransactionsFromBank bankResponse = (CmFinoFIX.CMGetLastTransactionsFromBank) response;
			result.setNotificationCode(CmFinoFIX.NotificationCode_BankAccountTransactionDetails);
			List<CMGetLastTransactionsFromBank.CGEntries> lstTxnHistory = constructLastTransactionHistoryFromBank(bankResponse);
			if(CollectionUtils.isEmpty(lstTxnHistory) && bankResponse.getEntries() != null){
				lstTxnHistory = constructLastTransactionHistoryFromBank(bankResponse.getEntries());
			}
			if (CollectionUtils.isNotEmpty(lstTxnHistory)) {
				if (lstTxnHistory.size() > bankTransactionsReq.getMaxCount()) {
					result.setLastBankTrxnList(lstTxnHistory.subList(0, bankTransactionsReq.getMaxCount()));
				} else {
					result.setLastBankTrxnList(lstTxnHistory);
				}
			} else {
				result.setNotificationCode(CmFinoFIX.NotificationCode_NoCompletedTransactionsWereFound);
			}

		}
		else {
			log.info("Error: While gettiong the Bank Transactions history");
			result.setNotificationCode(CmFinoFIX.NotificationCode_BankAccountGetTransactionsFailed);
			if(sctl!=null){
				transactionChargingService.failTheTransaction(sctl, MessageText._("Error: While gettiong the Bank Transactions history"));
			}
		}
		result.setSctlID(sctl.getID());
 		return result;
	}

	private List<CGEntries> constructLastTransactionHistoryFromBank(
			CGEntries[] entries) {
		List<CMGetLastTransactionsFromBank.CGEntries> result = new ArrayList<CMGetLastTransactionsFromBank.CGEntries>();
		Integer transactionHistoryOrderIsAscending;
		try{
			transactionHistoryOrderIsAscending = systemParametersService.getInteger(SystemParameterKeys.BANK_TRANSACTIONS_HISTORY_RECORD_ORDER_IS_ASCENDING);
		}
		catch(Exception e){
			log.error("Exception occured while getting the system parameter: "+SystemParameterKeys.BANK_TRANSACTIONS_HISTORY_RECORD_ORDER_IS_ASCENDING+"  "+e.getMessage());
			transactionHistoryOrderIsAscending = 1;			
		}
		//create the transaction list as it is from first to last as sent by bank
		if (transactionHistoryOrderIsAscending == 1){
			log.info("sorting list in ascending order");
			for(int i=0; i < entries.length; i++){
				if(entries[i] != null){
					result.add(entries[i]);
				}
			}
		}
		//create the transaction list in reverse order from last to first as sent by bank
		else if(transactionHistoryOrderIsAscending == 0){
			log.info("sorting list in descending order");
			for(int i=entries.length-1; i >=0; i--){
				if(entries[i] != null){
					result.add(entries[i]);
				}
			}
		}
		return result;
	}

	private List<CMGetLastTransactionsFromBank.CGEntries> constructLastTransactionHistoryFromBank(CMGetLastTransactionsFromBank fromBank) {
		log.info("Constructing the List for Transaction History from the Bank Reasponse.");
		final String LINE_SEPERATOR = "~";
		final String FIELD_SEPERATOR = "|";
		List<CMGetLastTransactionsFromBank.CGEntries> result = new ArrayList<CMGetLastTransactionsFromBank.CGEntries>();
		String transactions = fromBank.getTransactionHistory();
		if (StringUtils.isBlank(transactions)) {
			return result;
		}
		transactions = transactions.replace(FIELD_SEPERATOR, " "+FIELD_SEPERATOR+" ");
		Map<Integer, String> lineMap = new HashMap<Integer, String>();
		StringTokenizer lines = new StringTokenizer(transactions, LINE_SEPERATOR);
		if (lines.hasMoreTokens()) {
			String line = lines.nextToken();
			log.debug("Transaction History --> " + line);
			StringTokenizer fields = new StringTokenizer(line, FIELD_SEPERATOR);
			for (int i = 0; fields.hasMoreTokens(); i++) {
				lineMap.put(i, fields.nextToken().trim());
			}
		}
		while (lines.hasMoreTokens()) {
			String line = lines.nextToken();
			log.debug("Transaction History --> " + line);
			StringTokenizer fields = new StringTokenizer(line, FIELD_SEPERATOR);
			CMGetLastTransactionsFromBank.CGEntries entry = new CMGetLastTransactionsFromBank.CGEntries();
			for (int i = 0; fields.hasMoreTokens(); i++) {
				String field = fields.nextToken();
				field = field.trim();
				if (lineMap.get(i).equals("TRAN_DATE"))
					entry.setBankTransactionDate(field);
				else if (lineMap.get(i).equals("TRAN_TYPE"))
					entry.setTransactionType(field);
				else if (lineMap.get(i).equals("ACC_ID1"))
					entry.setSourceCardPAN(field);
				else if (lineMap.get(i).equals("ACC_ID2"))
					entry.setDestCardPAN(field);
				else if (lineMap.get(i).equals("TRAN_AMOUNT"))
					entry.setAmount(new BigDecimal(field));
				else if (lineMap.get(i).equals("CURR_CODE"))
					entry.setCurrency(field);
				else if (lineMap.get(i).equals("BANK_NAME"))
					entry.setBankName(field);
			}
			result.add(entry);
		}
		log.info("Got " + result.size() + " transactions history from bank");
		return result;
	}
	
}
