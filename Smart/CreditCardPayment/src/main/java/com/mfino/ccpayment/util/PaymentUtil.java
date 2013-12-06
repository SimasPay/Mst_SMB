package com.mfino.ccpayment.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.cc.message.CCPaymentOutput;
import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.CardInfo;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCreditCardPaymentRequest;
import com.mfino.fix.CmFinoFIX.CMCreditCardTopupRequest;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.uicore.service.UserService;
import com.mfino.uicore.smart.processor.CreditCardPaymentProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MailUtil;

/**
 * Utility class for all Credit Card payment related operations
 */

public class PaymentUtil {
	private static Logger log = LoggerFactory.getLogger(PaymentUtil.class);
	private static final int topup =2;
	private static final int postpaid =1;
	public enum status{
		failed, success, couldnotstart
	}

	/**
	 * Method to update the CreditCardTransaction table post payment transaction from Payment Window
	 * @param co
	 */
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public void saveCCTransactionInfo(CCPaymentOutput co) {
		log.info("Saving Credit Card Payment output");
		CreditCardTransactionDAO cDao = DAOFactory.getInstance().getCreditCardTransactionDAO();
		CreditCardTransaction creditTrans = cDao.getById(co.getMerchantTransactionID());
		log.info("Merchant Transaction ID = " + co.getMerchantTransactionID());
		if (creditTrans != null) {
			fillCreditCardTransactionDetails(co, creditTrans);
			log.info(co.toString());
		}
	}

	/**
	 * Method to update the Billing system after post paid payment transaction
	 * with Credit Card
	 * 
	 * @param ccResponse
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public String processPostPaid(CCPaymentOutput ccResponse) {
		status success = status.failed;
		String transactionID =StringUtils.EMPTY;
		Integer language = CmFinoFIX.Language_Bahasa;
		try {
			log.info("Starting post paid transaction");
			CMCreditCardPaymentRequest postPaidRequest = new CMCreditCardPaymentRequest();

			postPaidRequest.setSourceMDN(ccResponse.getSourceMdn());
			postPaidRequest.setPocketID(ccResponse.getPocketid());
			PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
			Pocket pocket = pocketDao.getById(ccResponse.getPocketid());
			Set<CardInfo> cards = pocket.getCardInfoFromPocketID();
			Iterator<CardInfo> cardIterator = cards.iterator();
			if ( cardIterator.hasNext()) {
				CardInfo card = cardIterator.next();
				postPaidRequest.setF6L3(card.getCardF6() + "XX XXXX X"
						+ card.getCardL4());
				postPaidRequest.setIssuerName(card.getIssuerName());
				language = card.getSubscriber().getLanguage();
			}

			postPaidRequest.setAmount(ccResponse.getAmount());
			postPaidRequest.setDestMDN(ccResponse.getMdn());
			log.info("Source MDN = " + ccResponse.getSourceMdn() + ", Amount = "
					+ ccResponse.getAmount() + ", Destination MDN = " + ccResponse.getMdn());
			postPaidRequest.setSourceApplication(CmFinoFIX.SourceApplication_Web);
			postPaidRequest.setCreditCardTransactionID(ccResponse.getMerchantTransactionID());
			log.info("Merchant transaction ID = " + ccResponse.getMerchantTransactionID());
			postPaidRequest.setAuthID(ccResponse.getAuthId());

			if (StringUtils.isNotBlank(ccResponse.getBillReferenceNumber())) {
				try {
					Long billReferenceNumber = Long.parseLong(ccResponse.getBillReferenceNumber());
					postPaidRequest.setBillReferenceNumber(billReferenceNumber);
					log.info("Bill Reference Number = " + billReferenceNumber);
				} catch (Exception e) {
					log.error("Error in Bill Reference Number ", e);
				}
			} else {
				log.info("Bill Reference Number is null. Could not set bill reference number");
				return "Could not set bill reference number";
			}

			CreditCardPaymentProcessor amountProcessor = new CreditCardPaymentProcessor();
			CFIXMsg returnMsg = amountProcessor.process(postPaidRequest);
			CMJSError msg = (CMJSError) returnMsg;
			if (msg.getErrorCode() == 0) {
				success = status.success;
				transactionID = getTransactionIDFromMessage(msg.getErrorDescription(), language);
			} else {
				success = status.failed;
				transactionID = getTransactionIDFromMessage(msg
						.getErrorDescription(), language);
				if (StringUtils.isEmpty(transactionID)) {
					success = status.couldnotstart;
				}
			}
			return msg.getErrorDescription();
		} catch (Exception e) {
			success = status.couldnotstart;
			log.error("Rolling back of Transaction ", e);
		} finally {
			sendTransferMail(UserService.getCurrentUser().getEmail(),ccResponse, success, postpaid, transactionID);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Method to update the Billing System after top up payment transaction
	 * @param ccResponse
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public String processTopUp(CCPaymentOutput ccResponse) {
		status success = status.failed;
		String transactionID =StringUtils.EMPTY;
		Integer language = CmFinoFIX.Language_Bahasa;
		try {
			log.info("Starting Topup/Recharge transaction");
			CMCreditCardTopupRequest topUpRequest = new CMCreditCardTopupRequest();

			topUpRequest.setSourceMDN(ccResponse.getSourceMdn());
			topUpRequest.setPocketID(ccResponse.getPocketid());
			PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
			Pocket pocket = pocketDao.getById(ccResponse.getPocketid());
			Set<CardInfo> cards = pocket.getCardInfoFromPocketID();
			Iterator<CardInfo> cardIterator=cards.iterator();
			if(cardIterator.hasNext())
			{
				CardInfo card = cardIterator.next();
				topUpRequest.setF6L3(card.getCardF6() + "XX XXXX X" +card.getCardL4());
				topUpRequest.setIssuerName(card.getIssuerName());
				language = card.getSubscriber().getLanguage();
			}
			topUpRequest.setAmount(ccResponse.getAmount());
			topUpRequest.setDestMDN(ccResponse.getMdn());
			log.info("Pre Paid Source MDN = "
					+ ConfigurationUtil.getPrepaidSourceMDN() + ", Amount = "
					+ ccResponse.getAmount() + ",Destination MDN = "
					+ ccResponse.getMdn());
			topUpRequest.setSourceApplication(CmFinoFIX.SourceApplication_Web);
			topUpRequest.setCreditCardTransactionID(ccResponse
					.getMerchantTransactionID());
			log.info("Merchant transaction ID = "
					+ ccResponse.getMerchantTransactionID());
			topUpRequest.setAuthID(ccResponse.getAuthId());

			CreditCardPaymentProcessor amountProcessor = new CreditCardPaymentProcessor();
			CFIXMsg returnMsg = amountProcessor.process(topUpRequest);
			CMJSError msg = (CMJSError) returnMsg;
			if (msg.getErrorCode() == 0) {
				success = status.success;
				transactionID = getTransactionIDFromMessage(msg
						.getErrorDescription(), language);
			} else {
				success = status.failed;
				transactionID = getTransactionIDFromMessage(msg
						.getErrorDescription(), language);
				if (StringUtils.isEmpty(transactionID)) {
					success = status.couldnotstart;
				}
			}
			return msg.getErrorDescription();
		} catch (Exception e) {
			success = status.couldnotstart;
			log.error("Rolling back of Transaction ", e);
		}
		finally
		{
			sendTransferMail(UserService.getCurrentUser().getEmail(),ccResponse, success, topup, transactionID);
		}
		return "";
	}

	private void fillCreditCardTransactionDetails(CCPaymentOutput co, CreditCardTransaction creditTrans) {
		creditTrans.setAcquirerBank(co.getAcquirerBank());
		creditTrans.setAmount(co.getAmount());
		creditTrans.setAuthID(co.getAuthId());
		creditTrans.setBankReference(co.getBankreference());
		creditTrans.setBankResCode(co.getBankResCode());
		creditTrans.setBankResMsg(co.getBankResMsg());
		creditTrans.setCardName(co.getCardName());
		creditTrans.setCardNoPartial(co.getCardNoPartial());
		creditTrans.setCardType(co.getCardType());
		creditTrans.setCurrCode(co.getCurrencyCode());
		creditTrans.setEUI(co.getEUI());
		creditTrans.setErrCode(co.getErrorCode());
		creditTrans.setExceedHighRisk(co.getExceedHighRisk());
		creditTrans.setFraudRiskLevel(co.getFraudRiskLevel());
		creditTrans.setFraudRiskScore(co.getFraudRisksCore());
		creditTrans.setIsBlackListed(co.getIsBlackListed());
		creditTrans.setPaymentMethod(co.getPaymentMethod());
		creditTrans.setTransStatus(co.getTxnStatus());
		creditTrans.setTransType(co.getTransType());
		creditTrans.setTransactionDate(co.getTransDate());
		creditTrans.setTransactionID(co.getTransactionID());
		creditTrans.setUserCode(co.getUsrCode());
		creditTrans.setWhiteListCard(co.getWhiteListCard());
		creditTrans.setCompany(UserService.getUserCompany());
		co.setOperation(creditTrans.getOperation());
		co.setMdn(creditTrans.getMDN());
		if(null != creditTrans.getBillReferenceNumber())
			co.setBillReferenceNumber(creditTrans.getBillReferenceNumber().toString());
		SubscriberMDN mdn = (SubscriberMDN)creditTrans.getSubscriber().getSubscriberMDNFromSubscriberID().toArray()[0];
		co.setSourceMdn(mdn.getMDN());
		co.setPocketid(creditTrans.getPocket().getID());
		co.setDescription(creditTrans.getDescription());
	}
	
	public void sendTransferMail(String email,CCPaymentOutput paymentResponse, status success, int operation, String transactionId)
	{
		try
		{
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			DateFormat tf = new SimpleDateFormat("HH:mm:ss");
			Date today = Calendar.getInstance().getTime();
			String date = df.format(today);
			String time = tf.format(today);
			if(email == null){
				email = UserService.getCurrentUser().getEmail(); 
			}                
			String emailSubject = ConfigurationUtil.getCCPaymentNotificationSubject();
			String emailMsg = ConfigurationUtil.getCCPaymentNotificationBody();
			emailMsg = StringUtils.replace(emailMsg, "$(Destination)", paymentResponse.getMdn());
			emailMsg = StringUtils.replace(emailMsg, "$(Date)", date);
			if(operation == postpaid)
			{
				emailMsg = StringUtils.replace(emailMsg, "$(TransactionType)", "Credit Card Postpaid Payment");
			}
			else
			{
				emailMsg = StringUtils.replace(emailMsg, "$(TransactionType)", "Credit Card Recharge Payment");
			}
			emailMsg = StringUtils.replace(emailMsg, "$(Time)", time);
			emailMsg = StringUtils.replace(emailMsg, "$(Amount)", paymentResponse.getAmount().toString());
			if(success == status.success)
			{
				emailMsg = StringUtils.replace(emailMsg, "$(Status)", "Success");
			}
			else if(success == status.failed)
			{
				emailMsg = StringUtils.replace(emailMsg, "$(Status)", "Failure");
			}
			else if(success == status.couldnotstart)
			{
				emailMsg = StringUtils.replace(emailMsg, "$(Status)", "Could not start transaction");
			}
			if(null == transactionId) {
				transactionId = StringUtils.EMPTY;
			}
			emailMsg = StringUtils.replace(emailMsg, "$(TransactionID)", transactionId);

			emailMsg = StringUtils.replace(emailMsg, "$(CreditCardTransactionID)", paymentResponse.getMerchantTransactionID().toString());

			String description = StringUtils.EMPTY;
			if(StringUtils.isNotEmpty(paymentResponse.getDescription())){
				description = paymentResponse.getDescription();
			}
			emailMsg = StringUtils.replace(emailMsg,"$(Description)",description);
			//todo shud fill the first name and last name
			try{
				log.info("Sending email to the user");
				MailUtil.sendMail(email, "", emailSubject, emailMsg);
			}catch(Exception e){
				log.info("Exception Occured when sending email");
			}


		}
		catch(Exception e)
		{
			log.info("error while sending email", e);
		}
	}


	/*
	 * This function would extract the transactionID from the message
	 */

	private String getTransactionIDFromMessage(String notifiMsg, Integer language) {
		String[] tokens = notifiMsg.split("REF:");
		if (tokens.length > 1)
			return tokens[1].trim();
		return StringUtils.EMPTY;
	}

}