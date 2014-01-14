package com.mfino.transactionapi.handlers.wallet.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.lowagie.text.DocumentException;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetTransactions;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.Result;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.EnumTextService;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.wallet.EmoneyTrxnHistoryHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.LastNTxnsXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.util.LanguageTranslator;
import com.mfino.transactionapi.util.PDFDocument;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.MfinoUtil;

/*
 * @author Maruthi
 * 
 */
@Service("EmoneyTrxnHistoryHandlerImpl")
public class EmoneyTrxnHistoryHandlerImpl extends FIXMessageHandler implements EmoneyTrxnHistoryHandler{
	private static Logger log = LoggerFactory.getLogger(EmoneyTrxnHistoryHandlerImpl.class);
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;

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
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	
	private static final int DEFAULT_PAGE_NO = 0;
	private static final int MAX_DURATION_TO_FETCH_HISTORY = 90;
	
	public Result handle(TransactionDetails transactionDetails) {
		log.info("Extracting data from transactionDetails in EmoneyTrxnHistoryHandlerImpl from sourceMDN: "+transactionDetails.getSourceMDN());
		String pocketCode= transactionDetails.getSourcePocketCode();
		ChannelCode cc = transactionDetails.getCc();
		CMGetTransactions transactionsHistory = new CMGetTransactions();
		transactionsHistory.setSourceMDN(transactionDetails.getSourceMDN());
		transactionsHistory.setPin(transactionDetails.getSourcePIN());
		transactionsHistory.setSourceApplication(cc.getChannelSourceApplication());
		transactionsHistory.setChannelCode(cc.getChannelCode());
		transactionsHistory.setTransactionIdentifier(transactionDetails.getTransactionIdentifier());
		
		LastNTxnsXMLResult result = new LastNTxnsXMLResult();
		result.setEnumTextService(enumTextService);
		
		if(transactionDetails.getFromDate() != null && transactionDetails.getToDate() != null)
		{
			int diffInDays = (int)( (transactionDetails.getToDate().getTime() - transactionDetails.getFromDate().getTime()) / (1000 * 60 * 60 * 24) );
			if(transactionDetails.getToDate().before(transactionDetails.getFromDate()) || diffInDays > MAX_DURATION_TO_FETCH_HISTORY)
			{
				result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidDateRange);
				return result;
			}
			
			transactionsHistory.setFromDate(new Timestamp(transactionDetails.getFromDate()));
			
			/*
			 * Adding a day to ToDate to ensure that all the transactions happening during that day are considered.
			 */
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(transactionDetails.getToDate());
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			transactionsHistory.setToDate(new Timestamp(calendar.getTime()));
		}
		if(NumberUtils.isDigits(transactionDetails.getPageNumber())){
			transactionsHistory.setPageNumber(Integer.parseInt(transactionDetails.getPageNumber()));
		}
		else
		{
			transactionsHistory.setPageNumber(DEFAULT_PAGE_NO);
		}
		if(NumberUtils.isDigits(transactionDetails.getNumRecords())){
			transactionsHistory.setNumRecords(Integer.parseInt(transactionDetails.getNumRecords()));
		}
		log.info("Handling emoney transactions history webapi request");
		
		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_GetTransactions, transactionsHistory.DumpFields());
		transactionsHistory.setTransactionID(transactionsLog.getID());

		result.setSourceMessage(transactionsHistory);
		result.setTransactionTime(transactionsLog.getTransactionTime());
		result.setTransactionID(transactionsLog.getID());

		SubscriberMDN srcSubscriberMDN = subscriberMdnService.getByMDN(transactionsHistory.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateSubscriberAsSource(srcSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Source subscriber with mdn : "+transactionsHistory.getSourceMDN()+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}	
		
		validationResult = transactionApiValidationService.validatePin(srcSubscriberMDN, transactionsHistory.getPin());
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Pin validation failed for mdn: "+transactionsHistory.getSourceMDN());
			result.setNumberOfTriesLeft(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT) - srcSubscriberMDN.getWrongPINCount());
			result.setNotificationCode(validationResult);
			return result;
		}
		
		addCompanyANDLanguageToResult(srcSubscriberMDN,result);

		Pocket srcPocket = pocketService.getDefaultPocket(srcSubscriberMDN, pocketCode);
		validationResult = transactionApiValidationService.validateSourcePocket(srcPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(srcPocket!=null? srcPocket.getID():null)+" has failed validations");
			result.setNotificationCode(validationResult);
			return result;
		}

		// Calculate the Service Charge

		log.info("creating the serviceCharge object....");
		Transaction transaction = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(transactionsHistory.getSourceMDN());
		sc.setDestMDN(null);
		sc.setChannelCodeId(cc.getID());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_HISTORY);
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

		transactionsHistory.setServiceChargeTransactionLogID(sctl.getID());
		List<CommodityTransfer> transactionHistoryList = new ArrayList<CommodityTransfer>();
		try {
			transactionHistoryList.addAll(commodityTransferService.getTranscationsHistory(srcPocket, srcSubscriberMDN,transactionsHistory));
			if (sctl != null) {
				sctl.setCalculatedCharge(BigDecimal.ZERO);
				transactionChargingService.completeTheTransaction(sctl);
			}
			if (transactionHistoryList.size() == 0) {
				result.setNotificationCode(CmFinoFIX.NotificationCode_NoCompletedTransactionsWereFound);
				return result;
			}

			Collections.sort(transactionHistoryList, new Comparator<CommodityTransfer>() {
				@Override
				public int compare(CommodityTransfer ct1, CommodityTransfer ct2) {
					return ((int) (ct2.getID() - ct1.getID()));
				}
			});

			if(ServiceAndTransactionConstants.TRANSACTION_HISTORY.equals(transactionDetails.getTransactionName()))
			{
				result.setTransactionList(transactionHistoryList);
			}
			for(CommodityTransfer ct : transactionHistoryList){
				if(ct.getUICategory().equals(CmFinoFIX.TransactionUICategory_NFC_Pocket_Topup) && ct.getDestCardPAN() == null){
					Pocket dtPk = pocketService.getById(ct.getDestPocketID());
					ct.setDestCardPAN(dtPk.getCardPAN());
				}
			}
			result.setNotificationCode(CmFinoFIX.NotificationCode_CommodityTransaferDetails);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
			String dateString = sdf.format(new Date());
			String fileName =  srcSubscriberMDN.getMDN() + "_" + dateString + ".pdf";
			String filePath = "../webapps" + File.separatorChar + "webapi" +  File.separatorChar + "Emoney_Txn_History" + File.separatorChar + fileName;
			if(ServiceAndTransactionConstants.TRANSACTION_EMAIL_HISTORY_AS_PDF.equals(transactionDetails.getTransactionName()))
			{
				createPDFAndSendEmail(transactionDetails, srcSubscriberMDN, srcPocket, transactionHistoryList, filePath,sctl.getID());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransactionHistoryEmailWasSent);
			}
			else if(ServiceAndTransactionConstants.TRANSACTION_DOWNLOAD_HISTORY_AS_PDF.equals(transactionDetails.getTransactionName()))
			{
				result.setFilePath(filePath);
				createPDF(transactionDetails, srcSubscriberMDN, srcPocket, transactionHistoryList, filePath, sctl.getID());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransactionHistoryDownloadSuccessful);
				String downloadURL = "Emoney_Txn_History" + File.separatorChar + fileName;
				result.setDownloadURL(downloadURL);
			}
			else
			{	
				sendSms(srcSubscriberMDN, transactionHistoryList, sctl.getID());
			}
		}
		catch (Exception ex) {
			log.error("Exception occured while getting transactions history", ex);
			result.setNotificationCode(CmFinoFIX.NotificationCode_Failure);
			transactionChargingService.failTheTransaction(sctl, MessageText._("Exception occured while getting transactions history"));
			return result;
		}
				
		result.setSctlID(sctl.getID());
		result.setSourceMessage(transactionsHistory);
		result.setSourcePocket(srcPocket);
		return result;
	}
	
	
	private void createPDFAndSendEmail(TransactionDetails txnDetails, SubscriberMDN subscriberMDN, Pocket srcPocket, List<CommodityTransfer> transactionHistoryList, String filepath, Long sctlId) throws IOException, DocumentException
	{
		Subscriber subscriber = subscriberMDN.getSubscriber();
		String email = txnDetails.getEmail();
		String to = subscriber.getFirstName() + subscriber.getLastName();

		createPDF(txnDetails, subscriberMDN, srcPocket, transactionHistoryList, filepath, sctlId);
		String subject = "Smartfren Uangku Electronic Statement";
		String body = "Thank you for using Uangku E-Statements Services. Please find your requested Uangku Transaction History for your selected time period. Enter your Uangku PIN to view the document."; 
		mailService.asyncSendEmailWithAttachment(email, to, subject, body, filepath);
	}
	
	private void createPDF(TransactionDetails txnDetails, SubscriberMDN subscriberMDN,	Pocket pocket, List<CommodityTransfer> transactionHistoryList, String filePath, Long sctlId) throws IOException, DocumentException 
	{
		File file = new File(filePath);
		PDFDocument pdfDocument = new PDFDocument(file, txnDetails.getSourcePIN());
		String headerRow = "Tanggal | Transaksi | Jumlah";
		pdfDocument.addLogo();
		pdfDocument.addSubscriberDetailsTable(txnDetails, subscriberMDN, pocket);
		pdfDocument.addHeaderRow(headerRow);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Iterator<CommodityTransfer> it = transactionHistoryList.iterator();
		while(it.hasNext())
		{
			CommodityTransfer ct = it.next();
			String txnType = getTxnType(ct, pocket, subscriberMDN.getSubscriber().getLanguage());
			String rowContent = dateFormat.format(ct.getStartTime())
								+ "|"+ txnType
								+ "|"+ "Rp. " + MfinoUtil.getNumberFormat().format(ct.getAmount())  + (ct.getPocketBySourcePocketID().getID().equals(pocket.getID())?"(-)":"(+)");
			pdfDocument.addRowContent(rowContent);
		}
		pdfDocument.closePdfReport();		
	}
	
	public String getTxnType(CommodityTransfer ct, Pocket pk, Integer language){
		String sourceMsg = ct.getSourceMessage();
		boolean isCredit = !(ct.getPocketBySourcePocketID().getID().equals(pk.getID()));
		String txnType = null;
		if(sourceMsg.equalsIgnoreCase("Mobile Transfer")){
			if (isCredit) {
				//txnType = "Transfer dari "+ ct.getSourceMDN();
				txnType = LanguageTranslator.translate(language, "Transfer From") +  ct.getSourceMDN();
			}else{
				//txnType = "Transfer ke "+ ct.getDestMDN();
				txnType = LanguageTranslator.translate(language, "Transfer To") +  ct.getDestMDN();
			}
		}else if(sourceMsg.equalsIgnoreCase("UnRegistered Transfer")){
			if (isCredit) {
				//txnType = "Transfer dari "+ ct.getSourceMDN();
				txnType = LanguageTranslator.translate(language, "Transfer From") +  ct.getSourceMDN();
			}else{
				//txnType = "Transfer ke "+ ct.getDestMDN();
				txnType = LanguageTranslator.translate(language, "Transfer To") +  ct.getDestMDN();
			}
		}else if(sourceMsg.equalsIgnoreCase("NFC Pocket Topup") || sourceMsg.equalsIgnoreCase("NFC Pocket Topup Inquiry")){
			String data=ct.getDestCardPAN();
			String separtedData = null;
			try{
				separtedData=data.substring(0, 4)+"-"+data.substring(4, 8)+"-"+data.substring(8, 12)+"-"+data.substring(12, 16);
			}catch(Exception e){
				separtedData=data;
			}
			//txnType = "Isi ulang ke NFC "+ separtedData;
			txnType = LanguageTranslator.translate(language, "NFC Pocket Topup") +  separtedData;
		}else if(sourceMsg.equalsIgnoreCase("Auto Reverse")){
			//txnType = "Pengembalian ";
			txnType = LanguageTranslator.translate(language, "Auto Reverse");
		}else if(sourceMsg.equalsIgnoreCase("Cash Out")){
			txnType = LanguageTranslator.translate(language, "Cash Out")+ct.getDestSubscriberName();
		}else if(sourceMsg.equalsIgnoreCase("Cash In")){
			txnType = LanguageTranslator.translate(language, "Cash In")+ct.getSourceSubscriberName();
		}else {
			txnType = LanguageTranslator.translate(language, sourceMsg);
		}
		return txnType;
	}


	/*
	 * sends emoney transaction history as sms
	 */
	private void sendSms(SubscriberMDN subscriberMDN, List<CommodityTransfer> transactionHistoryList, Long sctlId) {
		Integer language = subscriberMDN.getSubscriber().getLanguage();
		NotificationQuery query = new NotificationQuery();
		query.setNotificationCode(CmFinoFIX.NotificationCode_CommodityTransaferDetails);
		query.setLanguage(language);
		query.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		List<Notification> notifications = notificationService.getLanguageBasedNotificationsByQuery(query);

		if(CollectionUtils.isNotEmpty(notifications) && (!notifications.get(0).getIsActive()) ){
			log.info("SMS notification is not active, so not sending the SMS for the E-money history transaction.") ;
		} 
		else {
			NotificationWrapper notificationWrapper = new NotificationWrapper(notifications.get(0));
			notificationWrapper.setCode(CmFinoFIX.NotificationCode_CommodityTransaferDetails);
			notificationWrapper.setFirstName(subscriberMDN.getSubscriber().getFirstName());
			notificationWrapper.setLastName(subscriberMDN.getSubscriber().getLastName());
			notificationWrapper.setLanguage(language);
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			StringBuilder messageBuilder = new StringBuilder();
			for (CommodityTransfer commodityTransfer : transactionHistoryList) {
				notificationWrapper.setCommodityTransfer(commodityTransfer);
				messageBuilder.append(notificationMessageParserService.buildMessage(notificationWrapper,false));
				messageBuilder.append("\r\n");
			}
			smsService.setDestinationMDN(subscriberMDN.getMDN());
			smsService.setMessage(messageBuilder.toString());
			smsService.setNotificationCode(notificationWrapper.getCode());
			smsService.setSctlId(sctlId);
			smsService.asyncSendSMS();
		}
	}
}
