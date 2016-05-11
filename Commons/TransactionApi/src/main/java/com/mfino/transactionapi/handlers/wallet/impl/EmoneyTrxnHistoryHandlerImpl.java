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
import com.mfino.domain.BillPayments;
import com.mfino.domain.BookingDatedBalance;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
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
import com.mfino.result.Result;
import com.mfino.service.BillPaymentsService;
import com.mfino.service.BookingDateBalanceService;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.EnumTextService;
import com.mfino.service.IBTService;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
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
import com.mfino.util.ConfigurationUtil;
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
	
	@Autowired
	@Qualifier("BillPaymentsServiceImpl")
	private BillPaymentsService billPaymentsService;	
	
	@Autowired
	@Qualifier("SCTLServiceImpl")
	private SCTLService sctlService;

	@Autowired
	@Qualifier("IBTServiceImpl")
	private IBTService ibtService;
	
	private static final int DEFAULT_PAGE_NO = 0;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(ConfigurationUtil.getPdfHistoryDateFormat());	
	Integer language = 0;	// Default language set as Bahasa.
	
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
		int maxDurationToFetchTxnHistory = systemParametersService.getInteger(SystemParameterKeys.MAX_DURATION_TO_FETCH_TXN_HISTORY);
		if(transactionDetails.getFromDate() != null && transactionDetails.getToDate() != null)
		{
			int diffInDays = (int)( (transactionDetails.getToDate().getTime() - transactionDetails.getFromDate().getTime()) / (1000 * 60 * 60 * 24) );
			if(transactionDetails.getToDate().before(transactionDetails.getFromDate()) || diffInDays > maxDurationToFetchTxnHistory)
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
		else if(ServiceAndTransactionConstants.TRANSACTION_HISTORY.equals(transactionDetails.getTransactionName()) ||
				ServiceAndTransactionConstants.TRANSACTION_HISTORY_DETAILED_STATEMENT.equals(transactionDetails.getTransactionName()) )
		{
			int nofRecords = systemParametersService.getInteger(SystemParameterKeys.MAX_TXN_COUNT_IN_HISTORY);
			log.info("The system parameter 'max.txn.count.in.history' is set to: " + nofRecords);
			if (nofRecords != -1) {
				transactionsHistory.setNumRecords(nofRecords);
			}
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

		
		ServiceChargeTransactionLog sctl;
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
			// Calculate the Service Charge
			log.info("creating the serviceCharge object....");
			ServiceCharge sc = new ServiceCharge();
			sc.setSourceMDN(transactionsHistory.getSourceMDN());
			sc.setDestMDN(null);
			sc.setChannelCodeId(cc.getID());
			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
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
			sctl = transaction.getServiceChargeTransactionLog();
		}
		
		transactionsHistory.setServiceChargeTransactionLogID(sctl.getID());
		List<CommodityTransfer> transactionHistoryList = new ArrayList<CommodityTransfer>();
		try {
			transactionHistoryList.addAll(commodityTransferService.getTranscationsHistory(srcPocket, srcSubscriberMDN,transactionsHistory));
			if(transaction != null)	{
				//sctl.setCalculatedCharge(BigDecimal.ZERO);
				sctl.setCalculatedCharge(transaction.getAmountTowardsCharges());
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
			language = srcSubscriberMDN.getSubscriber().getLanguage();
			if(ServiceAndTransactionConstants.TRANSACTION_HISTORY.equals(transactionDetails.getTransactionName()) ||
					ServiceAndTransactionConstants.TRANSACTION_HISTORY_DETAILED_STATEMENT.equals(transactionDetails.getTransactionName()) )
			{
				result.setTransactionList(transactionHistoryList);
			}
			for(CommodityTransfer ct : transactionHistoryList){
				if(ct.getUICategory().equals(CmFinoFIX.TransactionUICategory_NFC_Pocket_Topup) && ct.getDestCardPAN() == null){
					Pocket dtPk = pocketService.getById(ct.getDestPocketID());
					ct.setDestCardPAN(dtPk.getCardPAN());
				}
				ct.setGeneratedTxnDescription(getTxnType(ct, srcPocket, language));
			}
			result.setNotificationCode(CmFinoFIX.NotificationCode_CommodityTransaferDetails);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String dateString = sdf.format(new Date());
			
			String fileName =  srcSubscriberMDN.getMDN() + "_" + dateString + ".pdf";
			String filePath = "../webapps" + File.separatorChar + "webapi" +  File.separatorChar + "Emoney_Txn_History" + File.separatorChar + fileName;
			
			if(ServiceAndTransactionConstants.TRANSACTION_EMAIL_HISTORY_AS_PDF.equals(transactionDetails.getTransactionName())) {
				
				createPDFAndSendEmail(transactionDetails, srcSubscriberMDN, srcPocket, transactionHistoryList, filePath,sctl.getID());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransactionHistoryEmailWasSent);
			
			} else if(ServiceAndTransactionConstants.TRANSACTION_DOWNLOAD_HISTORY_AS_PDF.equals(transactionDetails.getTransactionName())) {
				
				result.setFilePath(filePath);
				createPDF(transactionDetails, srcSubscriberMDN, srcPocket, transactionHistoryList, filePath, sctl.getID());
				result.setNotificationCode(CmFinoFIX.NotificationCode_TransactionHistoryDownloadSuccessful);
				String downloadURL = "Emoney_Txn_History" + File.separatorChar + fileName;
				result.setDownloadURL(downloadURL);
			
			} else {
				
				Long txnCount = commodityTransferService.getTranscationsCount(srcPocket, srcSubscriberMDN,transactionsHistory);
				result.setTotalTxnCount(txnCount);
				if(txnCount > (transactionsHistory.getPageNumber() + 1) * transactionsHistory.getNumRecords()){
					result.setMoreRecordsAvailable(true);
				} else{
					result.setMoreRecordsAvailable(false);
				}
			}		
			
//			if(ServiceAndTransactionConstants.TRANSACTION_HISTORY.equals(transactionDetails.getTransactionName()))
//			{
//				sendSms(srcSubscriberMDN, transactionHistoryList, sctl.getID());
//			}
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
		String to = StringUtils.EMPTY;
		if (subscriber.getKYCLevelByKYCLevel().getKYCLevel() != null && 
				CmFinoFIX.SubscriberKYCLevel_NoKyc.intValue() == (subscriber.getKYCLevelByKYCLevel().getKYCLevel().intValue())) {
			to = subscriber.getNickname();
		}
		else {
			String firstName = subscriber.getFirstName();
			String lastName = subscriber.getLastName();
			to = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
		}

		createPDF(txnDetails, subscriberMDN, srcPocket, transactionHistoryList, filepath, sctlId);
		String subject = ConfigurationUtil.getEmailPdfHistorySubject();
		subject = subject.replace("$(FromDate)", dateFormat.format(txnDetails.getFromDate()));
		subject = subject.replace("$(ToDate)", dateFormat.format(txnDetails.getToDate()));
		String body = ConfigurationUtil.getEmailPdfHistoryBody();
		mailService.asyncSendEmailWithAttachment(email, to, subject, body, filepath);
	}
	
	private void createPDF(TransactionDetails txnDetails, SubscriberMDN subscriberMDN,	Pocket pocket, List<CommodityTransfer> transactionHistoryList, String filePath, Long sctlId) throws IOException, DocumentException 
	{
		 
		 BookingDateBalanceService balanceService = new BookingDateBalanceService();
		 BookingDatedBalance bookingFromDatedBalance = balanceService.getBookingDatedBalances(pocket, txnDetails.getFromDate());
		 BigDecimal openingBalance= new BigDecimal(0);
		 if(bookingFromDatedBalance!=null){
			 openingBalance=bookingFromDatedBalance.getOpeningBalance();
		 }
		 BookingDatedBalance bookingToDatedBalance = null;
		 BigDecimal endingBalance = new BigDecimal(0);
		 
		 
		 Calendar cal = Calendar.getInstance();
		    cal.set(Calendar.HOUR_OF_DAY, 0);
		    cal.set(Calendar.MINUTE, 0);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.MILLISECOND, 0);
		    Date currentDate = cal.getTime();
		 if(txnDetails.getToDate().compareTo(currentDate)==0){
			 endingBalance=pocket.getCurrentBalance();
		
		 }else{
			 bookingToDatedBalance = balanceService.getBookingDatedBalances(pocket, txnDetails.getToDate());
			 if(bookingToDatedBalance!=null){
				 endingBalance= bookingToDatedBalance.getClosingBalance();
			 }
		 }
		 
		File file = new File(filePath);
		PDFDocument pdfDocument = new PDFDocument(file, txnDetails.getSourcePIN());
		
		//String headerRow = "Tanggal | Transaksi | Jumlah";
		String headerRow = LanguageTranslator.translate(language, "Date") + " | " + LanguageTranslator.translate(language, "Transaction type") + " | " + LanguageTranslator.translate(language, "Amount(dalam Rp)");
		pdfDocument.addLogo();
		
		pdfDocument.addHeaderRow(headerRow);
		
		Iterator<CommodityTransfer> it = transactionHistoryList.iterator();
		BigDecimal totalCreditAmount=new BigDecimal(0);
		BigDecimal totalDetbitAmount=new BigDecimal(0);
		int rowCount=0;
		boolean isLastRow=false;
		while(it.hasNext())
		{
			CommodityTransfer ct = it.next();
			rowCount++;
			if(rowCount == transactionHistoryList.size()){
				isLastRow=true;
			}
			String txnType = ct.getGeneratedTxnDescription();
			boolean isCredit ;
			if (ct.getPocketBySourcePocketID().getID().equals(pocket.getID())) {
				isCredit = false;
			}
			else{
				isCredit = true;
			}
			BigDecimal txnAmount = ct.getAmount();
			if (!isCredit) {
				txnAmount = txnAmount.add(ct.getCharges());
				totalDetbitAmount=totalDetbitAmount.add(txnAmount);
			}else{
				totalCreditAmount=totalCreditAmount.add(txnAmount);
			}
			String rowContent = dateFormat.format(ct.getStartTime())
								+ "|"+ txnType
								+ "|"+ (! isCredit ? "-" : "+")+ MfinoUtil.getNumberFormat().format(txnAmount) ;
			pdfDocument.addRowContent(rowContent,isLastRow);
			
		}
		pdfDocument.addSubscriberDetailsTable(txnDetails, subscriberMDN, pocket,totalCreditAmount,totalDetbitAmount,openingBalance,endingBalance);
		
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
		}else if(sourceMsg.equalsIgnoreCase("from BSM")){
			txnType = LanguageTranslator.translate(language, "from BSM");
		}else if(sourceMsg.equalsIgnoreCase("Purchase")){
			txnType = LanguageTranslator.translate(language, "Purchase") + ct.getSourceSubscriberName();
		}else if(ServiceAndTransactionConstants.MESSAGE_BILL_PAY.equalsIgnoreCase(sourceMsg)){
			BillPayments bp = billPaymentsService.getBySctlId(ct.getSctlId());
			txnType = LanguageTranslator.translate(language, "Bill Pay") + ((bp != null) ? bp.getInvoiceNumber() : "");
		}else if(ServiceAndTransactionConstants.MESSAGE_INTERBANK_TRANSFER.equalsIgnoreCase(sourceMsg)){
			/*BillPayments bp = billPaymentsService.getBySctlId(ct.getSctlId());
			String acctNum = null;
			String acctName = null;
			if (bp != null) {
				acctName = bp.getInfo3();
				acctNum = bp.getInvoiceNumber();
			}*/
			txnType = LanguageTranslator.translate(language, "InterBank Transfer") + ct.getDestCardPAN(); 
		}else if(ServiceAndTransactionConstants.MESSAGE_AIRTIME_PURCHASE.equalsIgnoreCase(sourceMsg)){
			BillPayments bp = billPaymentsService.getBySctlId(ct.getSctlId());
			txnType = LanguageTranslator.translate(language, "Airtime Purchase") + ((bp != null) ? bp.getInvoiceNumber() : "");
		}else if(ServiceAndTransactionConstants.MESSAGE_QR_PAYMENT.equalsIgnoreCase(sourceMsg)){
			BillPayments bp = billPaymentsService.getBySctlId(ct.getSctlId());
			txnType = LanguageTranslator.translate(language, "QR Payment") + ((bp != null) ? bp.getInfo1() : "");
		}else if(ServiceAndTransactionConstants.MESSAGE_DONATION.equalsIgnoreCase(sourceMsg)){
			txnType = LanguageTranslator.translate(language, ServiceAndTransactionConstants.MESSAGE_DONATION);
		}
		else {
			txnType = LanguageTranslator.translate(language, sourceMsg);
		}
		return txnType;
	}

	/*
	 * sends emoney transaction history as sms
	 */
//	private void sendSms(SubscriberMDN subscriberMDN, List<CommodityTransfer> transactionHistoryList, Long sctlId) {
//		Integer language = subscriberMDN.getSubscriber().getLanguage();
//		NotificationQuery query = new NotificationQuery();
//		query.setNotificationCode(CmFinoFIX.NotificationCode_CommodityTransaferDetails);
//		query.setLanguage(language);
//		query.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
//		List<Notification> notifications = notificationService.getLanguageBasedNotificationsByQuery(query);
//
//		if(CollectionUtils.isNotEmpty(notifications) && (!notifications.get(0).getIsActive()) ){
//			log.info("SMS notification is not active, so not sending the SMS for the E-money history transaction.") ;
//		} 
//		else {
//			NotificationWrapper notificationWrapper = new NotificationWrapper(notifications.get(0));
//			notificationWrapper.setCode(CmFinoFIX.NotificationCode_CommodityTransaferDetails);
//			notificationWrapper.setFirstName(subscriberMDN.getSubscriber().getFirstName());
//			notificationWrapper.setLastName(subscriberMDN.getSubscriber().getLastName());
//			notificationWrapper.setLanguage(language);
//			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
//			StringBuilder messageBuilder = new StringBuilder();
//			for (CommodityTransfer commodityTransfer : transactionHistoryList) {
//				notificationWrapper.setCommodityTransfer(commodityTransfer);
//				messageBuilder.append(notificationMessageParserService.buildMessage(notificationWrapper,false));
//				messageBuilder.append("\r\n");
//			}
//			smsService.setDestinationMDN(subscriberMDN.getMDN());
//			smsService.setMessage(messageBuilder.toString());
//			smsService.setNotificationCode(notificationWrapper.getCode());
//			smsService.setSctlId(sctlId);
//			smsService.asyncSendSMS();
//		}
//	}
	
	
}
