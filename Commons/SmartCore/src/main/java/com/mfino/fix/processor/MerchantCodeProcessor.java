/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;

import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantCodeDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.MerchantCodeQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Merchant;
import com.mfino.domain.MerchantCode;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSMerchantCode;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.security.Authorization;
import com.mfino.uicore.service.UserService;

/**
 *
 * @author ADMIN
 */
public class MerchantCodeProcessor extends BaseFixProcessor {

    public CMJSError handleMerchantCodes(ConstraintViolationException error) {
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        String message = MessageText._(error.getCause().getMessage());
        errorMsg.setErrorDescription(message);
        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        log.warn(message, error);
        return errorMsg;
    }

    private void updateEntity(MerchantCode merchantCode, CmFinoFIX.CMJSMerchantCode.CGEntries e) {

        if (e.getMDN() != null) {
            merchantCode.setMDN(e.getMDN());
        }
        if (e.getMerchantCode() != null) {
            merchantCode.setMerchantCode(e.getMerchantCode());
        }
        if (e.getCompanyID() != null) {
            CompanyDAO dao = DAOFactory.getInstance().getCompanyDAO();
            Company company = dao.getById(e.getCompanyID());
            merchantCode.setCompany(company);
        }
    }

    private void updateMessage(MerchantCode merchantCode, CMJSMerchantCode.CGEntries entry) {

        entry.setID(merchantCode.getID());

        if (merchantCode.getMDN() != null) {
            entry.setMDN(merchantCode.getMDN());
        }
        if (merchantCode.getMerchantCode() != null) {
            entry.setMerchantCode(merchantCode.getMerchantCode());
        }
        if (merchantCode.getCreateTime() != null) {
            entry.setCreateTime(merchantCode.getCreateTime());
        }
        if (merchantCode.getCreatedBy() != null) {
            entry.setCreatedBy(merchantCode.getCreatedBy());
        }
        if (merchantCode.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(merchantCode.getLastUpdateTime());
        }
        if (merchantCode.getUpdatedBy() != null) {
            entry.setUpdatedBy(merchantCode.getUpdatedBy());
        }
        if (merchantCode.getVersion() != null) {
            entry.setRecordVersion(merchantCode.getVersion());
        }
    }

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSMerchantCode realMsg = (CMJSMerchantCode) msg;

        MerchantCodeDAO dao = DAOFactory.getInstance().getMerchantCodeDAO();
        SubscriberMDNDAO mdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMerchantCode.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_Merchant_Codes_Edit)) {
                for (CMJSMerchantCode.CGEntries e : entries) {
                    MerchantCode merchantCode = dao.getById(e.getID());

                    // Check for Stale Data
                    if (!e.getRecordVersion().equals(merchantCode.getVersion())) {
                        handleStaleDataException();
                    }
                    Merchant merchant = null;
                    if (e.getMDN() != null) {
                        SubscriberMDN mdn = mdnDao.getByMDN(e.getMDN());
                        if (mdn != null && mdn.getSubscriber() != null && mdn.getSubscriber().getMerchant() != null) {
                            merchant = mdn.getSubscriber().getMerchant();
                        }
                    }
                    if (merchant == null) {
                        CmFinoFIX.CMJSError error = new CMJSError();
                        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        error.setErrorDescription(MessageText._("Invalid Merchant MDN"));
                        return error;
                    }
                    updateEntity(merchantCode, e);
                    try {
                        dao.save(merchantCode);
                    } catch (ConstraintViolationException error) {
                        return handleMerchantCodes(error);
                    }
                    updateMessage(merchantCode, e);
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to edit Merchant Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSMerchantCode.CGEntries.FieldName_MerchantCode,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);

        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            MerchantCodeQuery query = new MerchantCodeQuery();

            if (UserService.getUserCompany() != null) {
                query.setCompany(UserService.getUserCompany());
            }
            if (StringUtils.isNotBlank(realMsg.getMDNSearch())) {
                query.setMdn(realMsg.getMDNSearch());
            }
            if (StringUtils.isNotBlank(realMsg.getMerchantCodeSearch())) {
                query.setMerchantCode(realMsg.getMerchantCodeSearch());
            }
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());


            List<MerchantCode> results = dao.get(query);

            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                MerchantCode merchantCode = results.get(i);

                CMJSMerchantCode.CGEntries entry = new CMJSMerchantCode.CGEntries();
                updateMessage(merchantCode, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMerchantCode.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_Merchant_Codes_Add)) {
                for (CMJSMerchantCode.CGEntries e : entries) {
                    MerchantCode merchantCode = new MerchantCode();
                    Merchant merchant = null;
                    if (e.getMDN() != null) {
                        SubscriberMDN mdn = mdnDao.getByMDN(e.getMDN());
                        if (mdn != null && mdn.getSubscriber() != null && mdn.getSubscriber().getMerchant() != null) {
                            merchant = mdn.getSubscriber().getMerchant();
                        }
                    }
                    if (merchant == null) {
                        CmFinoFIX.CMJSError error = new CMJSError();
                        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        error.setErrorDescription(MessageText._("Invalid Merchant MDN"));
                        return error;
                    }
                    updateEntity(merchantCode, e);
                    try {
                        dao.save(merchantCode);
                    } catch (ConstraintViolationException error) {
                        return handleMerchantCodes(error);
                    }
                    updateMessage(merchantCode, e);
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to add new Merchant Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSMerchantCode.CGEntries.FieldName_MerchantCode,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSMerchantCode.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_Merchant_Codes_Delete)) {
                for (CMJSMerchantCode.CGEntries e : entries) {
                    dao.deleteById(e.getID());
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to delete Merchant Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSMerchantCode.CGEntries.FieldName_MerchantCode,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }
        return realMsg;
    }
}
