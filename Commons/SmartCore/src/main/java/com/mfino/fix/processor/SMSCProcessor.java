/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SMSCDAO;
import com.mfino.dao.SMSPartnerDAO;
import com.mfino.dao.query.SMSCQuery;
import com.mfino.domain.SMSC;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSSMSC;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.security.Authorization;
import com.mfino.uicore.web.WebContextError;

/**
 * 
 * @author Maruthi
 */
public class SMSCProcessor extends BaseFixProcessor {

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CmFinoFIX.CMJSSMSC realMsg = (CmFinoFIX.CMJSSMSC) msg;
        SMSCDAO dao = DAOFactory.getInstance().getSmscDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSMSC.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_SMSC_Edit)) {
                for (CMJSSMSC.CGEntries entry : entries) {
                    SMSC smsc = dao.getById(entry.getID());

                    // Check for Stale Data
                    if (!entry.getRecordVersion().equals(smsc.getVersion())) {
                        handleStaleDataException();
                    } else {
                        updateEntity(smsc, entry);
                        try {
                            dao.save(smsc);
                        } catch (ConstraintViolationException ex) {
                            handleUniqueConstraintViolation(ex);
                        }
                        updateMessage(smsc, entry);
                    }
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to edit SMSC"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSSMSC.CGEntries.FieldName_SMSCID,
                        MessageText._("Not allowed"));
            }
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            SMSCQuery query = new SMSCQuery();

            if (StringUtils.isNotBlank(realMsg.getPartnerIDSearch())) {
                query.setPartnerID(Long.parseLong(realMsg.getPartnerIDSearch()));
            }
            List<SMSC> results = dao.get(query);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                SMSC smsc = results.get(i);

                CMJSSMSC.CGEntries entry = new CMJSSMSC.CGEntries();

                updateMessage(smsc, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSMSC.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_SMSC_Add)) {
                for (CMJSSMSC.CGEntries e : entries) {
                    SMSCQuery query = new SMSCQuery();
                    query.setPartnerID(e.getPartnerID());
                    query.setShortCode(e.getShortCode());

                    List<SMSC> results = dao.get(query);
                    if (results != null && results.size() > 0) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("ShortCode already exists for this partner."));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        return errorMsg;
                    }
                    SMSC smsc = new SMSC();
                    updateEntity(smsc, e);
                    try {
                        dao.save(smsc);
                    } catch (ConstraintViolationException ex) {
                        handleUniqueConstraintViolation(ex);
                    }
                    updateMessage(smsc, e);
                }
            } else {
                log.info("Not authorized to add new SMSC");
                return getErrorMessage(MessageText._("Not authorized to add new SMSC"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSSMSC.CGEntries.FieldName_ShortCode,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSMSC.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_SMSC_Edit)) {  // need to change this permission..
                for (CMJSSMSC.CGEntries e : entries) {
                    dao.deleteById(e.getID());
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to delete SMSC"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSSMSC.CGEntries.FieldName_ShortCode,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }

        return realMsg;
    }

    private void updateEntity(SMSC smsc, CmFinoFIX.CMJSSMSC.CGEntries e) {
        SMSPartnerDAO smspartner = DAOFactory.getInstance().getSMSPartnerDAO();

        if (e.getPartnerID() != null) {
            smsc.setSMSPartnerByPartnerID(smspartner.getById(e.getPartnerID()));
        }
        if (e.getShortCode() != null) {
            smsc.setShortCode(e.getShortCode());
        }
        if (e.getLongNumber() != null) {
            smsc.setLongNumber(e.getLongNumber());
        }
        if (e.getSmartfrenSMSCID() != null) {
            smsc.setSmartfrenSMSCID(e.getSmartfrenSMSCID());
        }
        if (e.getCharging() != null) {
            smsc.setCharging(e.getCharging());
        }
        if (e.getOtherLocalOperatorSMSCID() != null) {
            smsc.setOtherLocalOperatorSMSCID(e.getOtherLocalOperatorSMSCID());
        }
        if (e.getHeader() != null) {
            smsc.setHeader(e.getHeader());
        }
        if (e.getFooter() != null) {
            smsc.setFooter(e.getFooter());
        }
        if (e.getLastUpdateTime() != null) {
            smsc.setLastUpdateTime(e.getLastUpdateTime());
        }
        if (e.getUpdatedBy() != null) {
            smsc.setUpdatedBy(e.getUpdatedBy());
        }
        if (e.getCreatedBy() != null) {
            smsc.setCreatedBy(e.getCreatedBy());
        }
        if (e.getCreateTime() != null) {
            smsc.setCreateTime(e.getCreateTime());
        }
    }

    private void updateMessage(SMSC smsc, CmFinoFIX.CMJSSMSC.CGEntries e) {
        e.setID(smsc.getID());
        if (smsc.getSMSPartnerByPartnerID() != null) {
            e.setPartnerID(smsc.getSMSPartnerByPartnerID().getID());
        }
        if (smsc.getShortCode() != null) {
            e.setShortCode(smsc.getShortCode());
        }
        if (smsc.getLongNumber() != null) {
            e.setLongNumber(smsc.getLongNumber());
        }
        if (smsc.getSmartfrenSMSCID() != null) {
            e.setSmartfrenSMSCID(smsc.getSmartfrenSMSCID());
        }
        if (smsc.getOtherLocalOperatorSMSCID() != null) {
            e.setOtherLocalOperatorSMSCID(smsc.getOtherLocalOperatorSMSCID());
        }
        if (smsc.getCharging() != null) {
            e.setCharging(smsc.getCharging());
        }
        if (smsc.getHeader() != null) {
            e.setHeader(smsc.getHeader());
        }
        if (smsc.getFooter() != null) {
            e.setFooter(smsc.getFooter());
        }
        if (smsc.getLastUpdateTime() != null) {
            e.setLastUpdateTime(smsc.getLastUpdateTime());
        }
        if (smsc.getUpdatedBy() != null) {
            e.setUpdatedBy(smsc.getUpdatedBy());
        }
        if (smsc.getCreatedBy() != null) {
            e.setCreatedBy(smsc.getCreatedBy());
        }
        if (smsc.getCreateTime() != null) {
            e.setCreateTime(smsc.getCreateTime());
        }
        if (smsc.getVersion() != null) {
            e.setRecordVersion(smsc.getVersion());
        }
    }
    private void handleUniqueConstraintViolation(ConstraintViolationException cvError) throws ConstraintViolationException {
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        String message = MessageText._("Same SMSC for partner already exist.");
        errorMsg.setErrorDescription(message);
        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        errorMsg.allocateEntries(1);
        errorMsg.getEntries()[0] = new CmFinoFIX.CMJSError.CGEntries();
        errorMsg.getEntries()[0].setErrorName(CmFinoFIX.CMJSSMSPartner.CGEntries.FieldName_PartnerName);
        errorMsg.getEntries()[0].setErrorDescription(MessageText._("Short Code Already Exists"));
        WebContextError.addError(errorMsg);
        log.warn(message, cvError);
        throw cvError;
    }
}
  
