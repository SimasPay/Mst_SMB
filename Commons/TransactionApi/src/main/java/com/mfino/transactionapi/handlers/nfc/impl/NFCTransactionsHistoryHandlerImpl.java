package com.mfino.transactionapi.handlers.nfc.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.lowagie.text.DocumentException;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetBankAccountTransactions;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank.CGEntries;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.service.MailService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.nfc.NFCTransactionsHistoryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.nfc.NFCTransactionHistoryXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.util.LanguageTranslator;
import com.mfino.transactionapi.util.PDFDocument;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Amar
 *
 */
@Service("NFCTransactionsHistoryHandlerImpl")
public class NFCTransactionsHistoryHandlerImpl extends FIXMessageHandler implements NFCTransactionsHistoryHandler{
	private static Logger log = LoggerFactory.getLogger(NFCTransactionsHistoryHandlerImpl.class);

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
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;
		
	private static final int MAX_DURATION_TO_FETCH_HISTORY = 90;
	private static final char TRANSACTION_FLAG_CREDIT = 'C';
	private static final int DEFAULT_PAGE_NO = 1;
	private static final int LIMIT_ON_NO_OF_RECORDS = 56;
	
	public Result handle(TransactionDetails transactionDetails) {
		
		int maxCount = systemParametersService.getInteger(SystemParameterKeys.MAX_TXN_COUNT_IN_HISTORY);
		if (maxCount == -1) {
			maxCount = 3;
		}
		ChannelCode cc = transactionDetails.getCc();

		CMGetBankAccountTransactions transactionsHistory = new CMGetBankAccountTransactions();
		transactionsHistory.setSourceMDN(transactionDetails.getSourceMDN());
		transactionsHistory.setPin(transactionDetails.getSourcePIN());
		transactionsHistory.setSourceApplication((int)cc.getChannelsourceapplication());
		transactionsHistory.setChannelCode(cc.getChannelcode());
		transactionsHistory.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transactionsHistory.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		transactionsHistory.setCardPAN(transactionDetails.getCardPAN());
		transactionsHistory.setCardAlias(transactionDetails.getCardAlias());
		
		log.info("Handling last n NFC transactions history webapi request for MDN --> " + transactionsHistory.getSourceMDN());
		NFCTransactionHistoryXMLResult result = new NFCTransactionHistoryXMLResult();
		
		if(transactionDetails.getFromDate() != null && transactionDetails.getToDate() !=  null)
		{
			int diffInDays = (int)( (transactionDetails.getToDate().getTime() - transactionDetails.getFromDate().getTime()) / (1000 * 60 * 60 * 24) );
			if(transactionDetails.getToDate().before(transactionDetails.getFromDate()) || diffInDays > MAX_DURATION_TO_FETCH_HISTORY)
			{
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidDateRange);
				return result;
			}
			
			transactionsHistory.setFromDate(new Timestamp(transactionDetails.getFromDate()));
			transactionsHistory.setToDate(new Timestamp(transactionDetails.getToDate()));			
		}
		
		if(NumberUtils.isDigits(transactionDetails.getNumRecords())){
			int nofRecords = Integer.parseInt(transactionDetails.getNumRecords());
			if(nofRecords > LIMIT_ON_NO_OF_RECORDS)
			{
				log.error("No of records exceeded the limit given by NFC ISO Provider");
				result.setNotificationCode(CmFinoFIX.NotificationCode_NofRecordsExceededMaxLimit);
				return result;
			}
			transactionsHistory.setMaxCount(nofRecords);
		}
		else
		{
			if(ServiceAndTransactionConstants.TRANSACTION_HISTORY.equals(transactionDetails.getTransactionName()) || 
					ServiceAndTransactionConstants.TRANSACTION_HISTORY_DETAILED_STATEMENT.equals(transactionDetails.getTransactionName()))
			{
				transactionsHistory.setMaxCount(maxCount);
			}
			//For email an download nofRecords is not mentioned and hence is set to Max limit provided by ISO.
			else
			{
				transactionsHistory.setMaxCount(LIMIT_ON_NO_OF_RECORDS);
			}
			
		}
		
		if(NumberUtils.isDigits(transactionDetails.getPageNumber())){
			transactionsHistory.setPageNumber(Integer.parseInt(transactionDetails.getPageNumber()) + 1);
		}
		else
		{
			transactionsHistory.setPageNumber(DEFAULT_PAGE_NO);
		}
		
		TransactionLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_GetTransactions, transactionsHistory.DumpFields());
		transactionsHistory.setTransactionID(transactionsLog.getId().longValue());
		
		result.setSourceMessage(transactionsHistory);
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setSourceMDN(transactionsHistory.getSourceMDN());
		result.setTransactionID(transactionsLog.getId().longValue());
		
		SubscriberMdn sourceMDN= subscriberMdnService.getByMDN(transactionsHistory.getSourceMDN());

		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(sourceMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+sourceMDN.getMdn()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}
		validationResult=transactionApiValidationService.validatePin(sourceMDN, transactionsHistory.getPin());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Pin validation failed for mdn: "+sourceMDN.getMdn());
			result.setNumberOfTriesLeft((int)(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - sourceMDN.getWrongpincount()));
			result.setNotificationCode(validationResult);
			return result;
		}

		addCompanyANDLanguageToResult(sourceMDN, result);
		Pocket sourcePocket = null;

		if(StringUtils.isNotBlank(transactionsHistory.getCardPAN()))
		{
			sourcePocket = pocketService.getByCardPan(transactionsHistory.getCardPAN());
		}
		else
		{
			sourcePocket = pocketService.getByCardAlias(transactionsHistory.getCardAlias());
			if(sourcePocket != null) 
				transactionsHistory.setCardPAN(sourcePocket.getCardpan());
		}

		if (sourcePocket == null) 
		{
			result.setNotificationCode(CmFinoFIX.NotificationCode_NoPocketWithGivenCardPAN);
			return result;
		}
		result.setCardPan(sourcePocket.getCardpan());
		result.setCardAlias(sourcePocket.getCardalias());
		result.setLanguage((int)sourceMDN.getSubscriber().getLanguage());
		result.setBankCode(sourcePocket.getPocketTemplate().getBankcode().intValue());
		if(!(sourcePocket.getPocketTemplate().getType()==(CmFinoFIX.PocketType_NFC)))
		{
			result.setNotificationCode(CmFinoFIX.NotificationCode_NotNFCAccount);
			return result;
		}

		validationResult = transactionApiValidationService.validateSourcePocket(sourcePocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(sourcePocket!=null? sourcePocket.getId():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}		

		log.info("Pocket Type = " + sourcePocket.getPocketTemplate().getType());
		result.setPocketDescription(sourcePocket.getPocketTemplate().getDescription());

		ServiceChargeTxnLog sctl;
		Transaction transaction = null;
		if(transactionDetails.getSctlId() != null)
		{
			sctl = sctlService.getBySCTLID(transactionDetails.getSctlId());
			if(sctl == null)
			{
				log.error("Invalid sctl ID " + transactionDetails.getSctlId());
				result.setSctlID(transactionDetails.getSctlId());
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidSctlId);
				return result;
			}
		}
		else
		{
			//Transaction transaction = null;
			ServiceCharge sc = new ServiceCharge();
			sc.setSourceMDN(transactionsHistory.getSourceMDN());
			sc.setDestMDN(null);
			sc.setChannelCodeId(cc.getId().longValue());
			sc.setServiceName(transactionDetails.getServiceName());
			sc.setTransactionTypeName(transactionDetails.getTransactionName());
			sc.setTransactionAmount(BigDecimal.ZERO);
			sc.setTransactionLogId(transactionsLog.getId().longValue());
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
			sctl = transaction.getServiceChargeTransactionLog();
		}
		
		transactionsHistory.setPocketID(sourcePocket.getId().longValue());
		transactionsHistory.setBankCode(sourcePocket.getPocketTemplate().getBankcode().intValue());
		transactionsHistory.setServiceChargeTransactionLogID(sctl.getId().longValue());
				
		try {
			boolean moreRecordsAvailable = false;
			List<CMGetLastTransactionsFromBank.CGEntries> nfcTransactionHistory = new ArrayList<CMGetLastTransactionsFromBank.CGEntries>();
			do{
				CFIXMsg response = super.process(transactionsHistory);

				if (response instanceof CmFinoFIX.CMGetLastTransactionsFromBank) {
					log.info("Got Transaction Histroy from NFC");
					if(transaction != null)	{
						//sctl.setCalculatedCharge(BigDecimal.ZERO);
						sctl.setCalculatedcharge(transaction.getAmountTowardsCharges());
						transactionChargingService.completeTheTransaction(sctl);				
					}
					CmFinoFIX.CMGetLastTransactionsFromBank bankResponse = (CmFinoFIX.CMGetLastTransactionsFromBank) response;
					transactionDetails.setAmount(bankResponse.getAmount());
					result.setNotificationCode(CmFinoFIX.NotificationCode_NFCTransactionDetails);
					nfcTransactionHistory.addAll(constructLastTransactionHistoryFromBank(bankResponse.getEntries()));
					if( (ServiceAndTransactionConstants.TRANSACTION_DOWNLOAD_HISTORY_AS_PDF.equals(transactionDetails.getTransactionName()) || 
							ServiceAndTransactionConstants.TRANSACTION_EMAIL_HISTORY_AS_PDF.equals(transactionDetails.getTransactionName()) ) &&
								bankResponse.getMoreRecordsAvailable() != null ){
						moreRecordsAvailable = bankResponse.getMoreRecordsAvailable();
						transactionsHistory.setPageNumber(transactionsHistory.getPageNumber() + 1);
					}
				}
				else {
					log.info("Error: While gettiong the NFC Transactions history");
					result.setNotificationCode(CmFinoFIX.NotificationCode_BankAccountGetTransactionsFailed);
					if(sctl!=null){
						transactionChargingService.failTheTransaction(sctl, MessageText._("Error: While gettiong the Bank Transactions history"));
					}
				}
				
			}
			while(moreRecordsAvailable);
			
			if (CollectionUtils.isNotEmpty(nfcTransactionHistory)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
				String dateString = sdf.format(new Date());
				String fileName =  sourcePocket.getCardpan() + "_" + dateString + ".pdf";
				String filePath = "../webapps" + File.separatorChar + "webapi" +  File.separatorChar + "NFC_Txn_History" + File.separatorChar + fileName;
				if(ServiceAndTransactionConstants.TRANSACTION_EMAIL_HISTORY_AS_PDF.equals(transactionDetails.getTransactionName()))
				{
					createPDFAndSendEmail(transactionDetails, sourceMDN, sourcePocket, nfcTransactionHistory, filePath,sctl.getId().longValue());
					result.setNotificationCode(CmFinoFIX.NotificationCode_TransactionHistoryEmailWasSent);
				}
				else if(ServiceAndTransactionConstants.TRANSACTION_DOWNLOAD_HISTORY_AS_PDF.equals(transactionDetails.getTransactionName()))
				{
					result.setFilePath(filePath);
					createPDF(transactionDetails, sourceMDN, sourcePocket, nfcTransactionHistory, filePath, sctl.getId().longValue());
					result.setNotificationCode(CmFinoFIX.NotificationCode_TransactionHistoryDownloadSuccessful);
					String downloadURL = "NFC_Txn_History" + File.separatorChar + fileName;
					result.setDownloadURL(downloadURL);
				}
				else
				{
					if (nfcTransactionHistory.size() > transactionsHistory.getMaxCount()) {
						result.setNfcTransactionHistory(nfcTransactionHistory.subList(0, transactionsHistory.getMaxCount()));
					} else {
						result.setNfcTransactionHistory(nfcTransactionHistory);
					}
					result.setMoreRecordsAvailable(moreRecordsAvailable);
				}
			} else {
				result.setNotificationCode(CmFinoFIX.NotificationCode_NoCompletedTransactionsWereFound);
			}
		}
		catch (Exception ex) {
			log.error("Exception occured while getting NFC transactions history", ex);
			result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
			transactionChargingService.failTheTransaction(sctl, MessageText._("Exception occured while getting transactions history"));
			return result;
		}
		
		result.setSctlID(sctl.getId().longValue());
 		return result;
	}

	private void createPDFAndSendEmail(TransactionDetails transactionDetails, SubscriberMdn subscriberMDN, Pocket pocket,
			List<CGEntries> nfcTransactionHistory, String filePath, Long sctlId) throws IOException, DocumentException, ParseException 
	{
		Subscriber subscriber = subscriberMDN.getSubscriber();
		String email = transactionDetails.getEmail();
		String to = subscriber.getFirstname() + subscriber.getLastname();

		createPDF(transactionDetails, subscriberMDN, pocket, nfcTransactionHistory, filePath, sctlId);
		String subject = "Smartfren Uangku: DIMO Electronic Statement";
		String body = "Thank you for using DIMO E-Statements Services. Please find your requested DIMO Transaction History for your selected time period.";
		mailService.asyncSendEmailWithAttachment(email, to, subject, body, filePath);		
	}

	private void createPDF(TransactionDetails transactionDetails, SubscriberMdn subscriberMDN, Pocket pocket,	List<CGEntries> nfcTransactionHistory, String filePath, Long id) 
			throws IOException, DocumentException, ParseException
	{
		File file = new File(filePath);
		PDFDocument pdfDocument = new PDFDocument(file, transactionDetails.getSourcePIN());
		String headerRow = "Tanggal | Transaksi | Jumlah";
		pdfDocument.addLogo();
		pdfDocument.addSubscriberDetailsTable(transactionDetails, subscriberMDN, pocket);
		pdfDocument.addHeaderRow(headerRow);
		
		SimpleDateFormat printDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat parsedateFormat = new SimpleDateFormat("dd/MM/yy hh:mm");
		Iterator<CGEntries> it = nfcTransactionHistory.iterator();
		while(it.hasNext())
		{
			CGEntries entry = it.next();
			//String txnType = getTxnType(entry, pocket);
			String rowContent = printDateFormat.format(parsedateFormat.parse(entry.getBankTransactionDate()))
								//+ "|"+ txnType
								//+ "|" + getTxnType(entry.getBankTransactionCode())
								+ "|" + LanguageTranslator.translate((int)subscriberMDN.getSubscriber().getLanguage(), entry.getBankTransactionCode())
								+ "|" + "Rp. " + MfinoUtil.getNumberFormat().format(entry.getAmount())  + (TRANSACTION_FLAG_CREDIT == entry.getBankTransactionFlag()?"(+)":"(-)");
			pdfDocument.addRowContent(rowContent,false);
		}
		pdfDocument.closePdfReport();		
		
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
		if(entries != null)
		{
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
		}
		return result;
	}	
	
	public String getTxnType(String transactionType)
	{
		if(transactionType.equals("RCHG"))
			return "Isi Ulang ";
		else if(transactionType.equals("SALE"))
			return "Pembayaran "; 
		return transactionType;
	}
}
