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

import com.mfino.cc.message.CCPaymentInput;
import com.mfino.dao.CardInfoDAO;
import com.mfino.dao.CreditCardTransactionDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.CardInfoQuery;
import com.mfino.domain.CardInfo;
import com.mfino.domain.CreditCardTransaction;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;


/**
 *
 * @author admin
 */
public class GetTransactionDetails {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public boolean process(CCPaymentInput paymentInput) {
        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            log.info("Credit Card Payment Transaction started at " + new Date());
            SubscriberDAO subsDao = new SubscriberDAO();
            Subscriber subs = subsDao.getById(paymentInput.getSubscriberid());
            PocketDAO pocketDao = new PocketDAO();
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
            if("NSIAPAY".equalsIgnoreCase(ConfigurationUtil.getCreditcardGatewayName())){
            	CardInfoDAO dao = new CardInfoDAO();
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
                    log.error("Bill reference is not a number" , e);
                }
            }
            CreditCardTransactionDAO cDao = new CreditCardTransactionDAO();
            cDao.save(creditTrans);
            log.info("Customer details : MDN = " + paymentInput.getMdn() + ",Amount = " + 
                    paymentInput.getAmount() + ",Description = " + paymentInput.getDescription() +
                    ",Operation = " + paymentInput.getOperation());
            paymentInput.setMerchantTransactionID(creditTrans.getID().toString());
            log.info("Merchant Transaction ID = " + creditTrans.getID());
            HibernateUtil.getCurrentTransaction().commit();
            return true;
        } catch (Exception e) {
            log.error("RollBack of Transaction", e);            
            HibernateUtil.getCurrentTransaction().rollback();
        }
        return false;
    }
}
