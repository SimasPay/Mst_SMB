/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantPrefixCodeDAO;
import com.mfino.dao.query.MerchantPrefixCodeQuery;
import com.mfino.domain.MerchantPrefixCode;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSMerchantPrefixCode;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.security.Authorization;

/**
 *
 * @author ADMIN
 */
public class MerchantPrefixCodeProcessor extends BaseFixProcessor {

    public CMJSError handleMerchantPrefixCodes(ConstraintViolationException error) {
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        String message = MessageText._(error.getCause().getMessage() +" Please enter different one");
        errorMsg.setErrorDescription(message);
        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        log.warn(message, error);
        return errorMsg;
    }

    private void updateEntity(MerchantPrefixCode merchantPrefixCode, CmFinoFIX.CMJSMerchantPrefixCode.CGEntries e) {

        if (e.getBillerName() != null) {
            merchantPrefixCode.setBillerName(e.getBillerName());
        }
        if (e.getMerchantPrefixCode() != null) {
            merchantPrefixCode.setMerchantPrefixCode(e.getMerchantPrefixCode());
        }
//        if (e.getCompanyID() != null) {
//            CompanyDAO dao = new CompanyDAO();
//            Company company = dao.getById(e.getCompanyID());
//            merchantPrefixCode.setCompany(company);
//        }
        if(e.getVAServiceName() != null) {
        	merchantPrefixCode.setVAServiceName(e.getVAServiceName());
        }
    }

    private void updateMessage(MerchantPrefixCode merchantPrefixCode, CMJSMerchantPrefixCode.CGEntries entry) {

        entry.setID(merchantPrefixCode.getID());

        if (merchantPrefixCode.getBillerName() != null) {
            entry.setBillerName(merchantPrefixCode.getBillerName());
        }
        if (merchantPrefixCode.getMerchantPrefixCode() != null) {
            entry.setMerchantPrefixCode(merchantPrefixCode.getMerchantPrefixCode());
        }
        if(merchantPrefixCode.getVAServiceName() != null) {
        	entry.setVAServiceName(merchantPrefixCode.getVAServiceName());
        }
        if (merchantPrefixCode.getCreateTime() != null) {
            entry.setCreateTime(merchantPrefixCode.getCreateTime());
        }
        if (merchantPrefixCode.getCreatedBy() != null) {
            entry.setCreatedBy(merchantPrefixCode.getCreatedBy());
        }
        if (merchantPrefixCode.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(merchantPrefixCode.getLastUpdateTime());
        }
        if (merchantPrefixCode.getUpdatedBy() != null) {
            entry.setUpdatedBy(merchantPrefixCode.getUpdatedBy());
        }
        if (merchantPrefixCode.getVersion() != null) {
            entry.setRecordVersion(merchantPrefixCode.getVersion());
        }
    }

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSMerchantPrefixCode realMsg = (CMJSMerchantPrefixCode) msg;

        MerchantPrefixCodeDAO dao = DAOFactory.getInstance().getMerchantPrefixCodeDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMerchantPrefixCode.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_Merchant_PrefixCodes_Edit)) {
                for (CMJSMerchantPrefixCode.CGEntries e : entries) {
                    MerchantPrefixCode merchantPrefixCode = dao.getById(e.getID());

                    // Check for Stale Data
                    if (!e.getRecordVersion().equals(merchantPrefixCode.getVersion())) {
                        handleStaleDataException();
                    }

                    updateEntity(merchantPrefixCode, e);
                    try {
                        dao.save(merchantPrefixCode);
                    } catch (ConstraintViolationException error) {
                        return handleMerchantPrefixCodes(error);
                    }
                    updateMessage(merchantPrefixCode, e);
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to edit Merchant Prefix Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSMerchantPrefixCode.CGEntries.FieldName_MerchantPrefixCode,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);

        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            MerchantPrefixCodeQuery query = new MerchantPrefixCodeQuery();

//            if (UserService.getUserCompany() != null) {
//                query.setCompany(UserService.getUserCompany());
//            }
            if (StringUtils.isNotBlank(realMsg.getBillerNameSearch())) {
                query.setBillerName(realMsg.getBillerNameSearch());
            }
            if (StringUtils.isNotBlank(realMsg.getMerchantPrefixCodeSearch())) {
                query.setMerchantPrefixCode(Integer.parseInt(realMsg.getMerchantPrefixCodeSearch()));
            }
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());


            List<MerchantPrefixCode> results = dao.get(query);

            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                MerchantPrefixCode merchantPrefixCode = results.get(i);

                CMJSMerchantPrefixCode.CGEntries entry = new CMJSMerchantPrefixCode.CGEntries();
                updateMessage(merchantPrefixCode, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMerchantPrefixCode.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_Merchant_PrefixCodes_Add)) {
                for (CMJSMerchantPrefixCode.CGEntries e : entries) {
                    MerchantPrefixCode merchantPrefixCode = new MerchantPrefixCode();
                    updateEntity(merchantPrefixCode, e);
                    try {
                        dao.save(merchantPrefixCode);
                    } catch (ConstraintViolationException error) {
                        return handleMerchantPrefixCodes(error);
                    }
                    updateMessage(merchantPrefixCode, e);
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to add new Merchant Prefix Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSMerchantCode.CGEntries.FieldName_MerchantCode,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMerchantPrefixCode.CGEntries[] entries = realMsg.getEntries();
//            if (Authorization.isAuthorized(CmFinoFIX.Permission_Merchant_PrefixCodes_Delete)) {
//                for (CMJSMerchantPrefixCode.CGEntries e : entries) {
//                    dao.deleteById(e.getID());
//                }
//            } else {
//                return getErrorMessage(MessageText._("Not authorized to delete Merchant Prefix Code"),
//                        CmFinoFIX.ErrorCode_Generic,
//                        CmFinoFIX.CMJSMerchantCode.CGEntries.FieldName_MerchantCode,
//                        MessageText._("Not allowed"));
//            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }
        return realMsg;
    }
}
