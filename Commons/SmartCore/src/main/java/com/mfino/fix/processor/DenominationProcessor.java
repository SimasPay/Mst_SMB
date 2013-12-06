/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;

import com.mfino.dao.BankDAO;
import com.mfino.dao.BillerDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.DenominationDAO;
import com.mfino.dao.query.DenominationQuery;
import com.mfino.domain.Denomination;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSDenomination;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.security.Authorization;

/**
 *
 * @author Srinu
 */
public class DenominationProcessor extends BaseFixProcessor {

    BillerDAO billerDao = DAOFactory.getInstance().getBillerDAO();
    BankDAO bankDao = DAOFactory.getInstance().getBankDao();

    public void updateEntity(Denomination s, CMJSDenomination.CGEntries e) {

        if (e.getID() != null) {
            s.setID(e.getID());
        }
        if (e.getDenominationAmount() != null) {
            s.setDenominationAmount(e.getDenominationAmount());
        }
        if (e.getBillerID() != null) {
            s.setBiller(billerDao.getById(e.getBillerID()));
        }
//        if (e.getBankID() != null) {
//           s.setBank(new Bank());
//        }
        if (e.getCreateTime() != null) {
            s.setCreateTime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            s.setLastUpdateTime(e.getLastUpdateTime());
        }
    }

    public void updateMessage(Denomination d, CMJSDenomination.CGEntries e) {

        if (d.getID() != null) {
            e.setID(d.getID());
        }
//        if (s.getBank() != null) {
//            e.setBankID(s.getBank().getID());
//        }
        if (d.getBiller() != null) {
            e.setBillerID(d.getBiller().getID());
        }
        if (d.getDenominationAmount() != null) {
            e.setDenominationAmount(d.getDenominationAmount());
        }
        if (d.getCreateTime() != null) {
            e.setCreateTime(d.getCreateTime());
        }
        if (d.getLastUpdateTime() != null) {
            e.setLastUpdateTime(d.getLastUpdateTime());
        }
        if (d.getVersion() != null) {
            e.setRecordVersion(d.getVersion());
        }
        if(d.getCreatedBy() != null) {
            e.setCreatedBy(d.getCreatedBy());
        }
        if(d.getUpdatedBy() != null) {
            e.setUpdatedBy(d.getUpdatedBy());
        }
    }

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CmFinoFIX.CMJSDenomination realMsg = (CmFinoFIX.CMJSDenomination) msg;
        DenominationDAO dao = DAOFactory.getInstance().getDenominationDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSDenomination.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_Denomination_Edit)) {
                for (CMJSDenomination.CGEntries entry : entries) {
                    Denomination denomination = dao.getById(entry.getID());

                    // Check for Stale Data
                    if (!entry.getRecordVersion().equals(denomination.getVersion())) {
                        handleStaleDataException();
                    } else {
                        updateEntity(denomination, entry);
                        try {
                            dao.save(denomination);
                        } catch (ConstraintViolationException ex) {
                        	log.warn("Denomination uniqueness failed: ", ex);
//                            handleUniqueConstraintViolation(ex);
                        }
                        updateMessage(denomination, entry);
                    }
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to edit SMSC"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSDenomination.CGEntries.FieldName_DenominationID,
                        MessageText._("Not allowed"));
            }
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            DenominationQuery query = new DenominationQuery();

            if (StringUtils.isNotBlank(realMsg.getBillerIDSearch())) {
                query.setBillerId(Long.parseLong(realMsg.getBillerIDSearch()));
            }
            List<Denomination> results = dao.get(query);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                Denomination denomination = results.get(i);

                CMJSDenomination.CGEntries entry = new CMJSDenomination.CGEntries();

                updateMessage(denomination, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSDenomination.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_Denomination_Add)) {
                for (CMJSDenomination.CGEntries e : entries) {
                    DenominationQuery query = new DenominationQuery();
                    query.setBillerId(e.getBillerID());
                    query.setAmount(e.getDenominationAmount());

                    List<Denomination> results = dao.get(query);
                    if (results != null && results.size() > 0) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("Denomination already exists for this biller."));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        return errorMsg;
                    }
                    Denomination denomination = new Denomination();
                    updateEntity(denomination, e);
                    try {
                        dao.save(denomination);
                    } catch (ConstraintViolationException ex) {
                    	log.warn("Denomination uniqueness failed: ", ex);
//                        handleUniqueConstraintViolation(ex);
                    }
                    updateMessage(denomination, e);
                }
            } else {
                log.info("Not authorized to add new Denomination");
                return getErrorMessage(MessageText._("Not authorized to add new Denomination"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSDenomination.CGEntries.FieldName_DenominationID,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSDenomination.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_Denomination_Delete)) {
                for (CMJSDenomination.CGEntries e : entries) {
                    dao.deleteById(e.getID());
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to delete Denomination"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSDenomination.CGEntries.FieldName_BillerID,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }

        return realMsg;
    }
}
