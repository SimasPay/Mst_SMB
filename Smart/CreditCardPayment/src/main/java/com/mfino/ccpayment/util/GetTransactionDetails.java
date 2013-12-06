/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.ccpayment.util;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.cc.message.CCPaymentInput;
import com.mfino.dao.CardInfoDAO;
import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.CardInfoQuery;
import com.mfino.domain.CardInfo;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.util.ConfigurationUtil;


/**
 *
 * @author admin
 */
public class GetTransactionDetails {

	private static Logger log = LoggerFactory.getLogger(GetTransactionDetails.class);

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public boolean process(CCPaymentInput paymentInput) {
		log.info("Credit Card Payment Transaction started at " + new Date());
		SubscriberDAO subsDao = DAOFactory.getInstance().getSubscriberDAO();
		Subscriber subs = subsDao.getById(paymentInput.getSubscriberid());
		PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
		CreditCardTransaction creditTrans = new CreditCardTransaction();
		creditTrans.setSourceIP(paymentInput.getSourceIP());
		creditTrans.setDescription(paymentInput.getDescription());
		creditTrans.setAmount(paymentInput.getAmount());
		creditTrans.setOperation(paymentInput.getOperation());
		creditTrans.setMDN(paymentInput.getMdn());
		creditTrans.setSubscriber(subs);
		creditTrans.setCompany(subs.getCompany());
		creditTrans.setPocket(pocketDao.getById(paymentInput.getPocketid()));
		creditTrans.setTransStatus(CmFinoFIX.TransStatus_Requested);
		creditTrans.setCurrCode(paymentInput.getCurrencyCode());
		if(paymentInput.getOperation().equals("3"))
			creditTrans.setProductIndicatorCode( paymentInput.getPICode()!=null ? paymentInput.getPICode() : "");
		if("NSIAPAY".equalsIgnoreCase(ConfigurationUtil.getCreditcardGatewayName())){
			CardInfoDAO dao = DAOFactory.getInstance().getCardInfoDAO();
			CardInfoQuery query = new CardInfoQuery();
			query.setSubscriber(subs);
			query.setCardStatus(CmFinoFIX.UserStatus_Active);
			List<CardInfo> results = dao.get(query);                
			creditTrans.setCardNoPartial(results.size()>0?results.get(0).getCardF6()+"XXXXXX"+results.get(0).getCardL4():null);
			creditTrans.setCurrCode(paymentInput.getNsiaCurrency());
			creditTrans.setTransactionDate(new Timestamp() + ""); //setting the transaction date at which transaction started.
			creditTrans.setSessionID(paymentInput.getSessionID());
			creditTrans.setCCBucketType(paymentInput.getCCBucketType());
		} 
		Long billReferenceNumber = null;
		if(StringUtils.isNotBlank(paymentInput.getBillReferenceNumber())) {
			try {
				billReferenceNumber = Long.parseLong(paymentInput.getBillReferenceNumber());
				creditTrans.setBillReferenceNumber(billReferenceNumber);
				log.info("Bill Reference Number " + billReferenceNumber);
			} catch (Exception e) {
				log.info("Bill reference is not a number" , e);
			}
		}
		CreditCardTransactionDAO cDao = DAOFactory.getInstance().getCreditCardTransactionDAO();
		cDao.save(creditTrans);
		log.info("Customer details : MDN = " + paymentInput.getMdn() + ",Amount = " + 
				paymentInput.getAmount() + ",Description = " + paymentInput.getDescription() +
				",Operation = " + paymentInput.getOperation() +" Product Indicator Code:  " + paymentInput.getPICode());
		paymentInput.setMerchantTransactionID(creditTrans.getID().toString());
		log.info("Merchant Transaction ID = " + creditTrans.getID());
		return true;
	}
}
