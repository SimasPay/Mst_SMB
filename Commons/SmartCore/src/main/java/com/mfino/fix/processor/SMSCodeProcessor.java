/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;

import com.mfino.dao.BrandDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SMSCodeDAO;
import com.mfino.dao.query.SMSCodeQuery;
import com.mfino.domain.Brand;
import com.mfino.domain.SMSCode;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSSMSCode;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.security.Authorization;

/**
 *
 * @author ADMIN
 */
public class SMSCodeProcessor extends BaseFixProcessor {

    public CMJSError handleSMSCodes(ConstraintViolationException error) {
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        String message = MessageText._("SMS Code already exists. Please enter different code");
        errorMsg.setErrorDescription(message);
        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        log.warn(message, error);
        return errorMsg;
    }

    private void updateEntity(SMSCode smsCode, CmFinoFIX.CMJSSMSCode.CGEntries e) {
        if (e.getBrandID() != null) {
            BrandDAO bdao = DAOFactory.getInstance().getBrandDAO();
            Brand brand = bdao.getById(e.getBrandID());
            smsCode.setBrand(brand);
        }
        if (e.getDescription() != null) {
            smsCode.setDescription(e.getDescription());
        }
        if (e.getSMSCodeStatus() != null) {
            smsCode.setSMSCodeStatus(e.getSMSCodeStatus());
        }
        if (e.getSMSCodeText() != null) {
            smsCode.setSMSCodeText(e.getSMSCodeText().toUpperCase());
        }
        if (e.getServiceName() != null) {
            smsCode.setServiceName(e.getServiceName());
        }
        if (StringUtils.isNotBlank(e.getShortCodes())) {
            smsCode.setShortCodes(e.getShortCodes());
        }
    }

    private void updateMessage(SMSCode smsCode, CMJSSMSCode.CGEntries entry) {

        entry.setID(smsCode.getID());

        if (smsCode.getBrand() != null) {
            entry.setBrandID(smsCode.getBrand().getID());
            entry.setBrandName(smsCode.getBrand().getBrandName());
        }
        if (smsCode.getServiceName() != null) {
            entry.setServiceName(smsCode.getServiceName());
        }
        if (smsCode.getDescription() != null) {
            entry.setDescription(smsCode.getDescription());
        }
        if (smsCode.getSMSCodeStatus() != null) {
            entry.setSMSCodeStatus(smsCode.getSMSCodeStatus());
            entry.setSMSCodeStatusText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SMSCodeStatus, null, entry.getSMSCodeStatus()));
        }
        if (smsCode.getSMSCodeText() != null) {
            entry.setSMSCodeText(smsCode.getSMSCodeText());
        }
        if (smsCode.getCreateTime() != null) {
            entry.setCreateTime(smsCode.getCreateTime());
        }
        if (smsCode.getCreatedBy() != null) {
            entry.setCreatedBy(smsCode.getCreatedBy());
        }
        if (smsCode.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(smsCode.getLastUpdateTime());
        }
        if (smsCode.getUpdatedBy() != null) {
            entry.setUpdatedBy(smsCode.getUpdatedBy());
        }
        if (smsCode.getVersion() != null) {
            entry.setRecordVersion(smsCode.getVersion());
        }
        if (smsCode.getShortCodes() != null) {
            entry.setShortCodes(smsCode.getShortCodes());
        }
    }

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSSMSCode realMsg = (CMJSSMSCode) msg;

        SMSCodeDAO dao = DAOFactory.getInstance().getSMSCodeDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSMSCode.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_SMS_Codes_Edit)) {
                for (CMJSSMSCode.CGEntries e : entries) {
                    SMSCode smsCode = dao.getById(e.getID());

                    // Check for Stale Data
                    if (!e.getRecordVersion().equals(smsCode.getVersion())) {
                        handleStaleDataException();
                    }

                    updateEntity(smsCode, e);
                    try {
                        dao.save(smsCode);
                    } catch (ConstraintViolationException error) {
                        return handleSMSCodes(error);
                    }
                    updateMessage(smsCode, e);
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to edit SMS Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSSMSCode.CGEntries.FieldName_SMSCodeText,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);

        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            SMSCodeQuery query = new SMSCodeQuery();

            if (StringUtils.isNotBlank(realMsg.getSMSCodeSearch())) {
                query.setSmsCode(realMsg.getSMSCodeSearch());
            }
            if (StringUtils.isNotBlank(realMsg.getSMSCodeStatusSearch())) {
                query.setSmsStatus(Integer.parseInt(realMsg.getSMSCodeStatusSearch()));
            }
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            query.setId(realMsg.getIDSearch());

            List<SMSCode> results = dao.get(query);

            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                SMSCode smsCode = results.get(i);

                CMJSSMSCode.CGEntries entry = new CMJSSMSCode.CGEntries();
                updateMessage(smsCode, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSMSCode.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_SMS_Codes_Add)) {
                for (CMJSSMSCode.CGEntries e : entries) {
                    SMSCode smsCode = new SMSCode();
                    updateEntity(smsCode, e);
                    try {
                        dao.save(smsCode);
                    } catch (ConstraintViolationException error) {
                        return handleSMSCodes(error);
                    }
                    updateMessage(smsCode, e);
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to add new SMS Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSSMSCode.CGEntries.FieldName_SMSCodeText,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSMSCode.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_SMS_Codes_Delete)) {
                for (CMJSSMSCode.CGEntries e : entries) {
                    dao.deleteById(e.getID());
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to delete SMS Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSSMSCode.CGEntries.FieldName_SMSCodeText,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }
        return realMsg;
    }
}
