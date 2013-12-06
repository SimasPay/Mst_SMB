/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.fix.processor;

import java.math.BigDecimal;
import java.util.Calendar;

import com.mfino.dao.ActivitiesLogDAO;
import com.mfino.dao.BulkLOPDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.DistributionChainLevelDAO;
import com.mfino.dao.DistributionChainTemplateDAO;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.query.DistributionChainLevelQuery;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.BulkLOP;
import com.mfino.domain.DistributionChainLevel;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.Merchant;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSGenerateBulkLOP;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationMessageParser;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.MerchantService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.validators.DefaultSVAPocketValidator;
import com.mfino.validators.SubscriberValidator;

/**
 *
 * @author admin
 */
public class GenerateBulkLOPProcessor extends BaseFixProcessor{

    public CFIXMsg process(CFIXMsg msg)  throws Exception {
        CMJSGenerateBulkLOP realMsg = (CMJSGenerateBulkLOP) msg;

        MerchantDAO mDao = DAOFactory.getInstance().getMerchantDAO();
        Merchant m;
        m = mDao.getById(realMsg.getMerchantID());
        SubscriberMDN mdn = (SubscriberMDN)m.getSubscriber().getSubscriberMDNFromSubscriberID().toArray()[0];
        ActivitiesLogDAO aDao = DAOFactory.getInstance().getActivitiesLogDAO();
        ActivitiesLog activityRec = new ActivitiesLog();

    	NotificationWrapper notificationMsg = new NotificationWrapper();
        SubscriberValidator subscriberValidator = new SubscriberValidator(mdn.getMDN());
    	Integer validationResult = subscriberValidator.validate();

    	if ( ! CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
    		notificationMsg.setCode(validationResult);
	}
        long[] dctAndLevel = MerchantService.getDCTIDAndLevel(m.getID());

        if(dctAndLevel[0]<1 && dctAndLevel[1] <1)
        {
            notificationMsg.setCode(CmFinoFIX.NotificationCode_NotAllowedToGenerateLOP);
        }
        DistributionChainTemplateDAO dctDAO = DAOFactory.getInstance().getDistributionChainTemplateDAO();
        DistributionChainTemplate dct = dctDAO.getById(dctAndLevel[0]);
        DistributionChainLevelDAO dclDao = DAOFactory.getInstance().getDistributionChainLevelDAO();
        DistributionChainLevelQuery dclQuery = new DistributionChainLevelQuery();
        dclQuery.setDistributionChainTemplateID(dct.getID());
        dclQuery.setLevel((int)dctAndLevel[1]);
        DistributionChainLevel dcl = dclDao.get(dclQuery).get(0);
        Calendar cal = Calendar.getInstance();
        int currentDay = cal.get(Calendar.DAY_OF_WEEK);
        int lastLopDay = 0;
        if(m.getLastLOPTime() != null)
        {
            cal.setTimeInMillis(m.getLastLOPTime().getTime());
            lastLopDay = cal.get(Calendar.DAY_OF_WEEK);
        }
        if((dcl.getPermissions()& CmFinoFIX.DistributionPermissions_LOP )==1)
        {
            if(m.getLastLOPTime()	==	null	||
                    currentDay < lastLopDay ||
                   System.currentTimeMillis()	>	m.getLastLOPTime().getTime()	+	1000*60*60*24*7)
            {
                    m.setCurrentWeeklyPurchaseAmount(ZERO);
            }

            DefaultSVAPocketValidator sourcePocketValidator = new DefaultSVAPocketValidator(mdn);
            validationResult = sourcePocketValidator.validate();
            if ( ! CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
                notificationMsg.setCode(validationResult);
            }
            else
            {
                BulkLOP bulkLop = new BulkLOP();
                bulkLop.setActualAmountPaid(realMsg.getLOPActualAmountPaid());
                if(realMsg.getComment() != null)
                    bulkLop.setComment(realMsg.getComment());
                bulkLop.setSubscriberMDNByMDNID(mdn);
                bulkLop.setStatus(CmFinoFIX.LOPStatus_Pending);
                bulkLop.setTransferDate(realMsg.getLOPTransferDate());
                bulkLop.setGiroRefID(realMsg.getLOPGiroRefID());
                bulkLop.setFileData(realMsg.getFileData());
                BigDecimal DistributedAmount;
                if(dcl.getCommission()!=null	&&	dcl.getCommission().compareTo(ZERO)	>	0)
                {
                    DistributedAmount = realMsg.getLOPActualAmountPaid().divide(HUNDREAD.
                    		subtract(dcl.getCommission()).divide(HUNDREAD)).add(new BigDecimal(0.5)); 	
//                    	(realMsg.getLOPActualAmountPaid()	/	((100.0 - dcl.getCommission())/100.0))	+	0.5;
                    bulkLop.setAmountDistributed(DistributedAmount);
                }
                else
                {
                    bulkLop.setAmountDistributed(realMsg.getLOPActualAmountPaid());
                }
                m.setLastLOPTime(new Timestamp());
//                m.setCurrentWeeklyPurchaseAmount(m.getCurrentWeeklyPurchaseAmount()	+	bulkLop.getAmountDistributed());
                m.setCurrentWeeklyPurchaseAmount(m.getCurrentWeeklyPurchaseAmount().add(bulkLop.getAmountDistributed()));
//                if(dcl.getMaxLOPAmount()!=null	&&	dcl.getMaxLOPAmount()	<	bulkLop.getAmountDistributed())
                if(dcl.getMaxLOPAmount()!=null && dcl.getMaxLOPAmount().compareTo(bulkLop.getAmountDistributed()) == -1)
                {
                        notificationMsg.setDistributionChainLevel(dcl);
                        notificationMsg.setCode(CmFinoFIX.NotificationCode_LOPAmountAboveMaximumAllowed);
//                }	else	if(dcl.getMaxWeeklyLOPAmount()!= null	&&	dcl.getMaxWeeklyLOPAmount()	<	m.getCurrentWeeklyPurchaseAmount())
                } else if(dcl.getMaxWeeklyLOPAmount()!= null &&	dcl.getMaxWeeklyLOPAmount().compareTo(m.getCurrentWeeklyPurchaseAmount()) == -1)
                {
                        notificationMsg.setCode(CmFinoFIX.NotificationCode_AboveWeeklyExpenditureLimit);
                }
                else
                {
                    bulkLop.setActualAmountPaid(realMsg.getLOPActualAmountPaid());
                    bulkLop.setCompany(m.getSubscriber().getCompany());
                    bulkLop.setLevelPermissions(dcl.getPermissions());
                    bulkLop.setSourceApplication(CmFinoFIX.SourceApplication_Web);
                    bulkLop.setDistributionChainLevelByDCTLevelID(dcl);
                    bulkLop.setDistributionChainTemplateByDCTID(dct);
                    BulkLOPDAO bDao = DAOFactory.getInstance().getBulkLOPDAO();
                    bDao.save(bulkLop);
                    mDao.save(m);
                    activityRec.setLOPID(bulkLop.getID());
                    notificationMsg.setBulkLOP(bulkLop);
                    notificationMsg.setCode(CmFinoFIX.NotificationCode_H2HGenerateLOPCompleted);
                }
            }
        }
        else
        {
            notificationMsg.setCode(CmFinoFIX.NotificationCode_NotAllowedToGenerateLOP);
        }
	activityRec.setCompany(m.getSubscriber().getCompany());
	activityRec.setMsgType(CmFinoFIX.MsgType_JSGenerateBulkLOP);
	activityRec.setSourceApplication(CmFinoFIX.SourceApplication_Web);
	activityRec.setSourceMDN(mdn.getMDN());
	activityRec.setNotificationCode(notificationMsg.getCode());
	activityRec.setSourceMDNID(mdn.getID());
	activityRec.setSourceSubscriberID(m.getSubscriber().getID());
        aDao.save(activityRec);

        NotificationMessageParser bnm = new NotificationMessageParser(notificationMsg);
        String Msg = bnm.buildMessage();
       
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        if(notificationMsg.getCode() == CmFinoFIX.NotificationCode_H2HGenerateLOPCompleted)
        {
            errorMsg.setErrorCode( CmFinoFIX.ErrorCode_NoError);
        }
        else
        {
            errorMsg.setErrorCode(notificationMsg.getCode());
        }
        errorMsg.setErrorDescription(Msg);
        return errorMsg;
    }

    
}
