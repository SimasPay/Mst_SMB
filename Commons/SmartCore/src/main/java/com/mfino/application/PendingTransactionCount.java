/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.application;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.service.SMSService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MailUtil;

/**
 *
 * @author Raju
 */
public class PendingTransactionCount {

    private static final String emailMsgTxt = "Transaction count is above the specified threshold. The threshold is";
    private static final String emailSubjectTxt = "Transaction count is above threshold";
    private static Logger log = LoggerFactory.getLogger(PendingTransactionCount.class);
    public static void main(String args[]) {
        int thresholdCount1 = 0;
        int thresholdCount2 = 0;
        int thresholdCount3 = 0;
        SMSService service = new SMSService();
        String messageToSend= "Transaction count is above threshold: ";
        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            PendingCommodityTransferDAO dao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
            CommodityTransferQuery query = new CommodityTransferQuery();
            Calendar calStartTime = Calendar.getInstance();
            Calendar calEndTime = Calendar.getInstance();
            Date currDate = new Date();
            calEndTime.setTime(currDate);
            calStartTime.setTime(currDate);
            calEndTime.add(calEndTime.MINUTE, -2); //-2 minutes
            boolean setValue = true;
            query.setOperatorActionRequired(setValue);
            query.setStartTimeLT(calEndTime.getTime());
            List results;
            String timePeriod1 = ConfigurationUtil.getPendingTransactionTimePeriod1();
            String timePeriod2 = ConfigurationUtil.getPendingTransactionTimePeriod2();
            String timePeriod3 = ConfigurationUtil.getPendingTransactionTimePeriod3();
            //try {
                if (timePeriod1 != null) {
                    calStartTime.setTime(currDate);
                    calStartTime.add(calStartTime.HOUR, Integer.parseInt(timePeriod1.substring(0, 2)) * -1);
                    calStartTime.add(calStartTime.MINUTE, Integer.parseInt(timePeriod1.substring(3, 5)) * -1);
                    query.setStartTimeGE(calStartTime.getTime());
                    results = dao.getAllPendingTransactionCount(query); 
                    if(results.get(0)!=null){
                    thresholdCount1 = (Integer)results.get(0);
                    }
                }
                if (timePeriod2 != null) {
                    calStartTime.setTime(currDate);
                    calStartTime.add(calStartTime.HOUR, Integer.parseInt(timePeriod2.substring(0, 2)) * -1);
                    calStartTime.add(calStartTime.MINUTE, Integer.parseInt(timePeriod2.substring(3, 5)) * -1);
                    query.setStartTimeGE(calStartTime.getTime());
                    results = dao.getAllPendingTransactionCount(query);
                    if(results.get(0)!=null){
                    thresholdCount2 = (Integer) results.get(0);
                    }
                }
                if (timePeriod3 != null) {
                    calStartTime.setTime(currDate);
                    calStartTime.add(calStartTime.HOUR, Integer.parseInt(timePeriod3.substring(0, 2)) * -1);
                    calStartTime.add(calStartTime.MINUTE, Integer.parseInt(timePeriod3.substring(3, 5)) * -1);
                    query.setStartTimeGE(calStartTime.getTime());
                    results = dao.getAllPendingTransactionCount(query);
                    if(results.get(0)!=null){
                    thresholdCount3 = (Integer)results.get(0);
                    }
                }

                if (thresholdCount1 > Integer.parseInt(ConfigurationUtil.getPendingTransactionThreshold1())) {
                    log.info("Count" + thresholdCount1);
                    log.info("Thresold exceeded sending a mail");
                    try {
                        MailUtil.sendMailMultiX(ConfigurationUtil.getPendingTransactionLimitMail1(), "Hi", emailSubjectTxt, emailMsgTxt + ":" + thresholdCount1);
                        service.setDestinationMDN(ConfigurationUtil.getPendingTransactionLimitSMS1());
                        service.setSourceMDN("62012345");
                        service.setMessage(messageToSend + thresholdCount1);
                        service.send();	
                    } catch (Exception ee) {
                        log.error("Failed to send email", ee);
                    }
                }
                if (thresholdCount2 > Integer.parseInt(ConfigurationUtil.getPendingTransactionThreshold2())) {
                    log.info("Count" + thresholdCount2);
                    log.info("Thresold exceeded sending a mail");
                    try {
                        MailUtil.sendMailMultiX(ConfigurationUtil.getPendingTransactionLimitMail2(), "Hi", emailSubjectTxt, emailMsgTxt + ":" + thresholdCount2);
                        service.setDestinationMDN(ConfigurationUtil.getPendingTransactionLimitSMS2());
                        service.setSourceMDN("62012345");
                        service.setMessage(messageToSend + thresholdCount2);
                        service.send();
                    } catch (Exception ee) {
                        log.error("Failed to send email", ee);
                    }
                }
                if (thresholdCount3 > Integer.parseInt(ConfigurationUtil.getPendingTransactionThreshold3())) {
                    log.info("Count" + thresholdCount3);
                    log.info("Thresold exceeded sending a mail");
                    try {
                        MailUtil.sendMailMultiX(ConfigurationUtil.getPendingTransactionLimitMail3(), "Hi", emailSubjectTxt, emailMsgTxt + ":" + thresholdCount3);
                        service.setDestinationMDN(ConfigurationUtil.getPendingTransactionLimitSMS3());
                        service.setSourceMDN("62012345");
                        service.setMessage(messageToSend + thresholdCount3);
                        service.send();
                    } catch (Exception ee) {
                        log.error("Failed to send email", ee);
                    }
                }                            
        } catch (Throwable throwable) {
            log.error("Exception occured",throwable);
        }finally{
        	HibernateUtil.getCurrentSession().getTransaction().rollback();
        }
    }    
}
