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
import com.mfino.domain.LOP;
import com.mfino.domain.LOPHistory;
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

    private void updateEntity(LOPHistory lopHistory, CMJSLOPHistory.CGEntries e) {

        if (lopHistory.getOldDiscount() == null &&  e.getOldDiscount() != null) {
            lopHistory.setOldDiscount(e.getOldDiscount());
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = (auth != null) ? auth.getName() : " ";
        lopHistory.setDiscountChangedBy(userName);
        lopHistory.setDiscountChangeTime(new Timestamp(new Date()));
        if (e.getCreateTime() != null) {
            lopHistory.setCreateTime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            lopHistory.setLastUpdateTime(e.getLastUpdateTime());
        }
        if (e.getUpdatedBy() != null) {
            lopHistory.setUpdatedBy(e.getUpdatedBy());
        }
        if (e.getCreatedBy() != null) {
            lopHistory.setCreatedBy(e.getCreatedBy());
        }
        if (e.getComments() != null) {
            lopHistory.setComments(e.getComments());
        }
    }

    private void updateMessage(LOPHistory lopHistory, CmFinoFIX.CMJSLOPHistory.CGEntries entry) {

        entry.setID(lopHistory.getID());

        if (lopHistory.getCreateTime() != null) {
            entry.setCreateTime(lopHistory.getCreateTime());
        }
        if (lopHistory.getCreatedBy() != null) {
            entry.setCreatedBy(lopHistory.getCreatedBy());
        }
        if (lopHistory.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(lopHistory.getLastUpdateTime());
        }
        if (lopHistory.getUpdatedBy() != null) {
            entry.setUpdatedBy(lopHistory.getUpdatedBy());
        }
        if (lopHistory.getVersion() != null) {
            entry.setRecordVersion(lopHistory.getVersion());
        }
        if (lopHistory.getComments() != null) {
            entry.setComments(lopHistory.getComments());
        }
        if (lopHistory.getLOP() != null) {
            entry.setLOPID(lopHistory.getLOP().getID());
        }
        if (lopHistory.getOldDiscount() != null) {
            entry.setOldDiscount(lopHistory.getOldDiscount());
        }
        if (lopHistory.getNewDiscount() != null) {
            entry.setNewDiscount(lopHistory.getNewDiscount());
        }
        if (lopHistory.getDiscountChangedBy() != null) {
            entry.setDiscountChangedBy(lopHistory.getDiscountChangedBy());
        }
        if (lopHistory.getDiscountChangeTime() != null) {
            entry.setDiscountChangeTime(lopHistory.getDiscountChangeTime());
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

            List<LOPHistory> results = dao.get(query);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                LOPHistory s = results.get(i);

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

                LOPHistory lopHistory = new LOPHistory();
                if (e.getLOPID() != null) {
                    LOPDAO lopDao = DAOFactory.getInstance().getLopDAO();
                    LOP lop = lopDao.getById(e.getLOPID());                    
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
                    BigDecimal maxCommission = lop.getDistributionChainLevelByDCTLevelID().getMaxCommission();
                    BigDecimal minCommission = lop.getDistributionChainLevelByDCTLevelID().getMinCommission();

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
                    lopHistory.setLOP(lop);
                    lopHistory.setOldDiscount(lop.getCommission());
                    lopHistory.setNewDiscount(e.getNewDiscount());
                    BigDecimal amountPaid = lop.getActualAmountPaid();
                    BigDecimal amountDistributed = lop.getAmountDistributed();
                    BigDecimal commission = e.getNewDiscount();
//                    amountPaid = (long) (amountDistributed - ((amountDistributed * commission) / 100));
                    amountPaid = amountDistributed.subtract(amountDistributed.multiply(commission).divide(HUNDREAD));                    
                    lop.setActualAmountPaid(amountPaid);
                    lop.setCommission(e.getNewDiscount());
                    lop.setIsCommissionChanged(Boolean.TRUE);
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
