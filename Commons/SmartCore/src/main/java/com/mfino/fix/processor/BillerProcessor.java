/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;

import com.mfino.dao.BillerDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.DenominationDAO;
import com.mfino.dao.query.BillerQuery;
import com.mfino.dao.query.DenominationQuery;
import com.mfino.domain.Biller;
import com.mfino.domain.Denomination;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBiller;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.security.Authorization;
import com.mfino.uicore.web.WebContextError;

/**
 *
 * @author Srinu
 */
public class BillerProcessor extends BaseFixProcessor {

    public void updateEntity(Biller s, CMJSBiller.CGEntries e) {

        if (e.getID() != null) {
            s.setID(e.getID());
        }
        if (e.getBillerName() != null) {
            s.setBillerName(e.getBillerName());
        }
        if (e.getBillerCode() != null) {
            s.setBillerCode(e.getBillerCode());
        }
        if (e.getBillerType() != null) {
            s.setBillerType(e.getBillerType());
        }
        if (e.getBillRefOffSet() != null) {
            s.setBillRefOffSet(e.getBillRefOffSet());
        }
        if (e.getTransactionFee() != null) {
            s.setTransactionFee(e.getTransactionFee());
        }
        if (e.getBankCodeForRouting() != null) {
            s.setBankCode(e.getBankCodeForRouting());
        }
        if (e.getCreateTime() != null) {
            s.setCreateTime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            s.setLastUpdateTime(e.getLastUpdateTime());
        }
    }

    public void updateMessage(Biller s, CMJSBiller.CGEntries e) {

        if (s.getID() != null) {
            e.setID(s.getID());
        }
        if (s.getBillerName() != null) {
            e.setBillerName(s.getBillerName());
        }
        if (s.getBillerCode() != null) {
            e.setBillerCode(s.getBillerCode());
        }
        if (s.getBillerType() != null) {
            e.setBillerType(s.getBillerType());
        }
        if (s.getBillRefOffSet() != null) {
            e.setBillRefOffSet(s.getBillRefOffSet());
        }
        if (s.getTransactionFee() != null) {
            e.setTransactionFee(s.getTransactionFee());
        }
        if (s.getBankCode() != null) {
            e.setBankCodeForRouting(s.getBankCode());
        }
        if (s.getCreateTime() != null) {
            e.setCreateTime(s.getCreateTime());
        }
        if (s.getLastUpdateTime() != null) {
            e.setLastUpdateTime(s.getLastUpdateTime());
        }
        if (s.getVersion() != null) {
            e.setRecordVersion(s.getVersion());
        }
        if(s.getCreatedBy() != null) {
            e.setCreatedBy(s.getCreatedBy());
        }
        if(s.getUpdatedBy() != null) {
            e.setUpdatedBy(s.getUpdatedBy());
        }
    }

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSBiller realMsg = (CMJSBiller) msg;

        BillerDAO billerdao = DAOFactory.getInstance().getBillerDAO();
        DenominationDAO denominationDao = DAOFactory.getInstance().getDenominationDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSBiller.CGEntries[] entries = realMsg.getEntries();

            for (CMJSBiller.CGEntries e : entries) {
                Biller biller = billerdao.getById(e.getID());

                // Check for Stale Data
                if (!e.getRecordVersion().equals(biller.getVersion())) {
                    handleStaleDataException();
                }
                if (e.getBillerType() != null && !e.getBillerType().equals(biller.getBillerType())) {
                    DenominationQuery query = new DenominationQuery();
                    query.setBillerId(e.getID());

                    List<Denomination> results = denominationDao.get(query);
                    if (results.size() > 0) {
                        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
                        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        error.setErrorDescription(MessageText._("First, please delete all denomiations for this biller"));
                        return error;
                    }
                }
                updateEntity(biller, e);
                try {
                    billerdao.save(biller);
                } catch (ConstraintViolationException ex) {
                    handleUniqueConstraintViolation(ex);
                }
                updateMessage(biller, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            BillerQuery query = new BillerQuery();

            if (StringUtils.isNotBlank(realMsg.getBillerCodeSearch())) {
                query.setBillerCode(Integer.parseInt(realMsg.getBillerCodeSearch()));
            }
            if (StringUtils.isNotBlank(realMsg.getBillerNameSearch())) {
                query.setBillerName(realMsg.getBillerNameSearch());
            }
            if (StringUtils.isNotBlank(realMsg.getBillerTypeSearch())) {
                query.setBillerType(realMsg.getBillerTypeSearch());
            }
            if (realMsg.getStartDateSearch() != null) {
                query.setStartDate(realMsg.getStartDateSearch());
            }
            if (realMsg.getEndDateSearch() != null) {
                query.setEndDate(realMsg.getEndDateSearch());
            }
            List<Biller> results = billerdao.get(query);
            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                Biller biller = results.get(i);
                CMJSBiller.CGEntries entry = new CMJSBiller.CGEntries();
                updateMessage(biller, entry);
                realMsg.getEntries()[i] = entry;
            }
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSBiller.CGEntries[] entries = realMsg.getEntries();

            for (CMJSBiller.CGEntries e : entries) {
                Biller biller = new Biller();
                updateEntity(biller, e);
                try {
                    billerdao.save(biller);
                } catch (ConstraintViolationException ex) {
                    handleUniqueConstraintViolation(ex);
                }
                updateMessage(biller, e);
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSBiller.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_Biller_Delete)) {
                for (CMJSBiller.CGEntries e : entries) {

                    DenominationQuery query = new DenominationQuery();
                    query.setBillerId(e.getID());

                    List<Denomination> results = denominationDao.get(query);
                    // first deleting all denominations belongs to this biller..
                    for (Denomination denomination : results) {
                        denominationDao.deleteById(denomination.getID());
                    }
                    // finally deleting biller record..
                    billerdao.deleteById(e.getID());
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to delete Biller"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSBiller.CGEntries.FieldName_BillerName,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }
        return realMsg;
    }

    private void handleUniqueConstraintViolation(ConstraintViolationException constraintExp) throws ConstraintViolationException {
        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
        String message = MessageText._("BillerName or BillerCode Already Exists for this Bank");
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        error.setErrorDescription(message);
        error.allocateEntries(1);
        error.getEntries()[0] = new CmFinoFIX.CMJSError.CGEntries();
        error.getEntries()[0].setErrorName(CmFinoFIX.CMJSSMSPartner.CGEntries.FieldName_PartnerName);
        error.getEntries()[0].setErrorDescription(message);
        WebContextError.addError(error);
        log.warn(message, constraintExp);
        throw constraintExp;
    }
}
