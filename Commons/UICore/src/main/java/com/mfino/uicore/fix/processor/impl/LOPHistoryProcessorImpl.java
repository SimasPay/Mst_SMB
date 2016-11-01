/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.LOPDAO;
import com.mfino.dao.LOPHistoryDAO;
import com.mfino.dao.query.LOPHistoryQuery;
import com.mfino.domain.LetterOfPurchase;
import com.mfino.domain.LopHistory;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSLOPHistory;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.LOPHistoryProcessor;

@Service("LOPHistoryProcessorImpl")
public class LOPHistoryProcessorImpl extends BaseFixProcessor implements LOPHistoryProcessor{

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private void updateEntity(LopHistory lopHistory, CMJSLOPHistory.CGEntries e) {

        if (lopHistory.getOlddiscount() == null &&  e.getOldDiscount() != null) {
            lopHistory.setOlddiscount(e.getOldDiscount());
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = (auth != null) ? auth.getName() : " ";
        lopHistory.setDiscountchangedby(userName);
        lopHistory.setDiscountchangetime(new Timestamp(new Date()));
        if (e.getCreateTime() != null) {
            lopHistory.setCreatetime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            lopHistory.setLastupdatetime(e.getLastUpdateTime());
        }
        if (e.getUpdatedBy() != null) {
            lopHistory.setUpdatedby(e.getUpdatedBy());
        }
        if (e.getCreatedBy() != null) {
            lopHistory.setCreatedby(e.getCreatedBy());
        }
        if (e.getComments() != null) {
            lopHistory.setComments(e.getComments());
        }
    }

    private void updateMessage(LopHistory lopHistory, CmFinoFIX.CMJSLOPHistory.CGEntries entry) {

        entry.setID(lopHistory.getId().longValue());

        if (lopHistory.getCreatetime() != null) {
            entry.setCreateTime(lopHistory.getCreatetime());
        }
        if (lopHistory.getCreatedby() != null) {
            entry.setCreatedBy(lopHistory.getCreatedby());
        }
        if (lopHistory.getLastupdatetime() != null) {
            entry.setLastUpdateTime(lopHistory.getLastupdatetime());
        }
        if (lopHistory.getUpdatedby() != null) {
            entry.setUpdatedBy(lopHistory.getUpdatedby());
        }
        if (lopHistory.getVersion() != null) {
            entry.setRecordVersion(lopHistory.getVersion());
        }
        if (lopHistory.getComments() != null) {
            entry.setComments(lopHistory.getComments());
        }
        if (lopHistory.getLetterOfPurchase() != null) {
            entry.setLOPID(lopHistory.getLetterOfPurchase().getId().longValue());
        }
        if (lopHistory.getOlddiscount() != null) {
            entry.setOldDiscount(lopHistory.getOlddiscount());
        }
        if (lopHistory.getNewdiscount() != null) {
            entry.setNewDiscount(lopHistory.getNewdiscount());
        }
        if (lopHistory.getDiscountchangedby() != null) {
            entry.setDiscountChangedBy(lopHistory.getDiscountchangedby());
        }
        if (lopHistory.getDiscountchangetime() != null) {
            entry.setDiscountChangeTime(lopHistory.getDiscountchangetime());
        }
    }
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSLOPHistory realMsg = (CMJSLOPHistory) msg;

        LOPHistoryDAO dao = DAOFactory.getInstance().getLOPHistoryDAO();

        if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            LOPHistoryQuery query = new LOPHistoryQuery();
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            query.setLopid(realMsg.getLOPIDSearch());

            List<LopHistory> results = dao.get(query);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                LopHistory s = results.get(i);

                // Here check for the LOPHistory Expiration and if
                // applicable set it.
                CMJSLOPHistory.CGEntries entry =
                        new CMJSLOPHistory.CGEntries();

                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSLOPHistory.CGEntries[] entries = realMsg.getEntries();

            for (CMJSLOPHistory.CGEntries e : entries) {

                LopHistory lopHistory = new LopHistory();
                if (e.getLOPID() != null) {
                    LOPDAO lopDao = DAOFactory.getInstance().getLopDAO();
                    LetterOfPurchase lop = lopDao.getById(e.getLOPID());                    
                    if (!(CmFinoFIX.LOPStatus_Pending.equals(lop.getStatus()))) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("LOP status is not pending. Discount can be changed only for pending LOP"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        errorMsg.setsuccess(CmFinoFIX.Boolean_False);
                        CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                        newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                        newEntries[0].setErrorName(MessageText._("LOP History"));
                        newEntries[0].setErrorDescription(MessageText._("LOP status is not pending. Discount can be changed only for pending LOP."));
                        return errorMsg;
                    }
                    if(lop.getCommission() == null && e.getNewDiscount().equals(e.getOldDiscount())) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("New discount is same as old discount."));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        errorMsg.setsuccess(CmFinoFIX.Boolean_False);
                        CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                        newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                        newEntries[0].setErrorName(MessageText._("LOP History"));
                        newEntries[0].setErrorDescription(MessageText._("New discount is same as old discount."));
                        return errorMsg;
                    }
                    if (e.getNewDiscount() != null && e.getNewDiscount().equals(lop.getCommission())) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("New discount is same as old discount."));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        errorMsg.setsuccess(CmFinoFIX.Boolean_False);
                        CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                        newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                        newEntries[0].setErrorName(MessageText._("LOP History"));
                        newEntries[0].setErrorDescription(MessageText._("New discount is same as old discount."));
                        return errorMsg;
                    }
                    BigDecimal maxCommission = lop.getDistributionChainLvl().getMaxcommission();
                    BigDecimal minCommission = lop.getDistributionChainLvl().getMaxcommission();

                    log.info("Maximum Commission = " + maxCommission);
                    log.info("Minimum Commission = " + minCommission);
                    log.info("New discount is " + e.getNewDiscount());
                    if (maxCommission != null && minCommission != null) {
//                        if (!(e.getNewDiscount() <= maxCommission && e.getNewDiscount() >= minCommission)) {
                    	if (!(e.getNewDiscount().compareTo(maxCommission) <= 0 && e.getNewDiscount().compareTo(minCommission) >= 0)) {                    	
                            CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                            errorMsg.setErrorDescription(MessageText._("Discount should between " + minCommission + " and " + maxCommission));
                            errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                            errorMsg.setsuccess(CmFinoFIX.Boolean_False);
                            CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
                            newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
                            newEntries[0].setErrorName(MessageText._("LOP History"));
                            newEntries[0].setErrorDescription(MessageText._("Discount should between " + minCommission + " and " + maxCommission));
                            return errorMsg;
                        }
                    }
                    lopHistory.setLetterOfPurchase(lop);
                    lopHistory.setOlddiscount(lop.getCommission());
                    lopHistory.setNewdiscount(e.getNewDiscount());
                    BigDecimal amountPaid = lop.getActualamountpaid();
                    BigDecimal amountDistributed = lop.getAmountdistributed();
                    BigDecimal commission = e.getNewDiscount();
//                    amountPaid = (long) (amountDistributed - ((amountDistributed * commission) / 100));
                    amountPaid = amountDistributed.subtract(amountDistributed.multiply(commission).divide(HUNDREAD));                    
                    lop.setActualamountpaid(amountPaid);
                    lop.setCommission(e.getNewDiscount());
                    lop.setIscommissionchanged((short) 1);
                    log.info("Amount Distributed is " + amountDistributed);
                    log.info("Amount paid after new discount is " + amountPaid);
                    lopDao.save(lop);
                }
                updateEntity(lopHistory, e);
                dao.save(lopHistory);
                updateMessage(lopHistory, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }

        return realMsg;
    }
}
