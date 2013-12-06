/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SMSCDAO;
import com.mfino.dao.SMSPartnerDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.SMSCQuery;
import com.mfino.dao.query.SMSPartnerQuery;
import com.mfino.domain.Company;
import com.mfino.domain.SMSC;
import com.mfino.domain.SMSPartner;
import com.mfino.domain.User;
import com.mfino.domain.common.KeyTokenPair;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSSMSPartner;
import com.mfino.i18n.MessageText;
import com.mfino.service.TokenService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.security.Authorization;
import com.mfino.uicore.service.UserService;
import com.mfino.uicore.web.WebContextError;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MailUtil;

/**
 *
 * @author Srinu
 */
public class SMSPartnerProcessor extends BaseFixProcessor {

    private void updateEntity(SMSPartner smsPartner, CMJSSMSPartner.CGEntries e) {
        if (e.getPartnerName() != null) {
            smsPartner.setPartnerName(e.getPartnerName());
            smsPartner.getUser().setFirstName(e.getPartnerName());
        }
        if (e.getContactName() != null) {
            smsPartner.setContactName(e.getContactName());
        }
        if (e.getContactPhone() != null) {
            smsPartner.setContactPhone(e.getContactPhone());
        }
        if (e.getContactEmail() != null) {
            smsPartner.setContactEmail(e.getContactEmail());
            smsPartner.getUser().setEmail(e.getContactEmail());
        }
        if (e.getCreatedBy() != null) {
            smsPartner.setCreatedBy(e.getCreatedBy());
        }
        if (e.getUpdatedBy() != null) {
            smsPartner.setUpdatedBy(e.getUpdatedBy());
        }
        if (e.getCreateTime() != null) {
            smsPartner.setCreateTime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            smsPartner.setLastUpdateTime(e.getLastUpdateTime());
        }
        if (e.getServerIP() != null) {
            smsPartner.setServerIP(e.getServerIP());
        }
        if (e.getSendReport() != null) {
            smsPartner.setSendReport(e.getSendReport());
        }
    }

    private void updateMessage(SMSPartner smsPartner, CMJSSMSPartner.CGEntries e) {
        e.setID(smsPartner.getID());
        if (smsPartner.getPartnerName() != null) {
            e.setPartnerName(smsPartner.getPartnerName());
        }
        if (smsPartner.getContactName() != null) {
            e.setContactName(smsPartner.getContactName());
        }
        if (smsPartner.getContactPhone() != null) {
            e.setContactPhone(smsPartner.getContactPhone());
        }
        if (smsPartner.getContactEmail() != null) {
            e.setContactEmail(smsPartner.getContactEmail());
        }
        if (smsPartner.getCreatedBy() != null) {
            e.setCreatedBy(smsPartner.getCreatedBy());
        }
        if (smsPartner.getUpdatedBy() != null) {
            e.setUpdatedBy(smsPartner.getUpdatedBy());
        }
        if (smsPartner.getCreateTime() != null) {
            e.setCreateTime(smsPartner.getCreateTime());
        }
        if (smsPartner.getLastUpdateTime() != null) {
            e.setLastUpdateTime(smsPartner.getLastUpdateTime());
        }
        if (smsPartner.getServerIP() != null) {
            e.setServerIP(smsPartner.getServerIP());
        }
        if (smsPartner.getUser() != null) {
            e.setUsername(smsPartner.getUser().getUsername());
            e.setContactEmail(smsPartner.getUser().getEmail());
            e.setPartnerName(smsPartner.getUser().getFirstName());
        }
        if (smsPartner.getVersion() != null) {
            e.setRecordVersion(smsPartner.getVersion());
        }
        if (smsPartner.getSendReport() != null) {
            e.setSendReport(smsPartner.getSendReport());
        }
    }

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSSMSPartner realMsg = (CMJSSMSPartner) msg;
        SMSPartnerDAO dao = DAOFactory.getInstance().getSMSPartnerDAO();
        SMSPartner smsPartner = new SMSPartner();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSMSPartner.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_SMS_Partner_Edit)) {
                for (CMJSSMSPartner.CGEntries e : entries) {
                    SMSPartner smsp = dao.getById(e.getID());
                    // Check for Stale Data
                    if (!e.getRecordVersion().equals(smsp.getVersion())) {
                        handleStaleDataException();
                    } else {
                        updateEntity(smsp, e);
                        dao.save(smsp);
                        updateMessage(smsp, e);
                        realMsg.setsuccess(CmFinoFIX.Boolean_True);
                        realMsg.settotal(entries.length);
                    }
                }
            } else {
                log.info("Not authorized to edit new SMS Partner");
                return getErrorMessage(MessageText._("Not authorized to edit SMS Partner"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSSMSPartner.CGEntries.FieldName_PartnerName,
                        MessageText._("Not allowed"));
            }
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            SMSPartnerQuery query = new SMSPartnerQuery();

            if (realMsg.getPartnerNameSearch() != null && realMsg.getPartnerNameSearch().length() > 0) {
                query.setPartnerName(realMsg.getPartnerNameSearch());
            }
            if (realMsg.getStartDateSearch() != null) {
                query.setStartDate(realMsg.getStartDateSearch());
            }
            if (realMsg.getEndDateSearch() != null) {
                query.setEndDate(realMsg.getEndDateSearch());
            }
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            List<SMSPartner> results = dao.get(query);

            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                SMSPartner s = results.get(i);
                CMJSSMSPartner.CGEntries entry = new CMJSSMSPartner.CGEntries();

                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSMSPartner.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_SMS_Partner_Add)) {
                for (CMJSSMSPartner.CGEntries e : entries) {

                    UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
                    String username = e.getUsername();
                    log.info("User name = " + username);
                    User user = userDAO.getByUserName(username);
                    if (user != null) {
                        // username already in use. So skip adding and report failure
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(String.format(
                                "Username %s not available", username));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        log.info("Username already avaiable in DB " + username);
                        return errorMsg;
                    }

                    User u = new User();
                    if (UserService.getUserCompany() != null) {
                        Company company = UserService.getUserCompany();
                        u.setCompany(company);
                    } else {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(String.format("Company does not exist for the logged in user"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        log.info("Company does not exist for the logged in user");
                        return errorMsg;
                    }

                    u.setUsername(username);
                    u.setEmail(e.getContactEmail());
                    u.setFirstName(e.getPartnerName());
                    u.setLanguage(CmFinoFIX.Language_English);
                    u.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
                    u.setStatus(CmFinoFIX.UserStatus_Active);

                    log.info("Partner name = " + e.getPartnerName());
                    log.info("Email = " + e.getContactEmail());
                    log.info("Server Ip = " + e.getServerIP());
                    // generate a password and hash it
                    String password = e.getPassword();
                    PasswordEncoder encoder = new ShaPasswordEncoder(1);
                    String encPassword = encoder.encodePassword(password, username);
                    u.setPassword(encPassword);

                    userDAO.save(u);
                    smsPartner.setUser(u);
                    log.info("User id = " + u.getID());

                    TokenService ts = TokenService.getInstance();
                    KeyTokenPair ktp = ts.generateToken(username);
                    smsPartner.setAPIKey(ktp.getKey());

                    updateEntity(smsPartner, e);
                    try {
                        dao.save(smsPartner);
                    } catch (ConstraintViolationException t) {
                        handleUniqueConstraintViolation(t);
                    }
                    updateMessage(smsPartner, e);
                    // send mail
                    String emailMsg =
                            String.format(
                            "Dear %s ,\n\tYour username is %s \n\tYour token is %s \n\tYour PartnerID is %s" + ".\n" + ConfigurationUtil.getAdditionalMsg() + "\n" + ConfigurationUtil.getEmailSignature(), e.getPartnerName(),
                            username, ktp.getToken(), smsPartner.getID());
                    String emailSubject = ConfigurationUtil.getUserInsertSubject();
                    try {
                        MailUtil.sendMailMultiX(e.getContactEmail(), e.getPartnerName(), emailSubject, emailMsg);
                    } catch (Exception ee) {
                        log.error("Failed to send User Add information.", ee);
                        realMsg.setsuccess(CmFinoFIX.Boolean_False);
                        realMsg.seterrorCodes("Couldn't send email to " + username);
                        return realMsg;
                    }
                    log.info("Email sent to " + e.getContactEmail());
                }
            } else {
                log.info("Not authorized to add new SMS Partner");
                return getErrorMessage(MessageText._("Not authorized to add new SMS Partner"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSSMSPartner.CGEntries.FieldName_PartnerName,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSSMSPartner.CGEntries[] entries = realMsg.getEntries();
            if (Authorization.isAuthorized(CmFinoFIX.Permission_SMS_Partner_Delete)) {
                for (CMJSSMSPartner.CGEntries e : entries) {

                    SMSCQuery query = new SMSCQuery();
                    query.setPartnerID(e.getID());

                    SMSCDAO smscDao = DAOFactory.getInstance().getSmscDAO();
                    List<SMSC> results = smscDao.get(query);
                    // first deleting all smsc's belongs to this sms partner..
                    for (SMSC smsc : results) {
//                        smscDao.delete(smsc);
                        smscDao.deleteById(smsc.getID());
                    }
                    // finally deleting sms partner record..
                    dao.deleteById(e.getID());
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to delete SMS Partner"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSSMSPartner.CGEntries.FieldName_PartnerName,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }

        return realMsg;
    }

    private void handleUniqueConstraintViolation(ConstraintViolationException cvError) throws ConstraintViolationException {
        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
        String message = MessageText._("Partner Name Already Exists");
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        error.setErrorDescription(message);
        error.allocateEntries(1);
        error.getEntries()[0] = new CmFinoFIX.CMJSError.CGEntries();
        error.getEntries()[0].setErrorName(CmFinoFIX.CMJSSMSPartner.CGEntries.FieldName_PartnerName);
        error.getEntries()[0].setErrorDescription(message);
        WebContextError.addError(error);
        log.warn(message, cvError);
        throw cvError;
    }
}
