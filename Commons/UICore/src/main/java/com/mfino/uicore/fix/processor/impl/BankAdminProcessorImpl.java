/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BankAdminDAO;
import com.mfino.dao.BankDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Bank;
import com.mfino.domain.BankAdmin;
import com.mfino.domain.Company;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBankAdmin;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSUsernameCheck;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.service.MailService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BankAdminProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.UsernameCheckProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.PasswordGenUtil;

/**
 *
 * @author deva
 */
@Service("BankAdminProcessorImpl")
public class BankAdminProcessorImpl extends BaseFixProcessor implements BankAdminProcessor{
    private String action = null;
    
    @Autowired
    @Qualifier("UsernameCheckProcessorImpl")
    private UsernameCheckProcessor usernameCheckProcessor;
    
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;

    private boolean validateUsername(CMJSBankAdmin.CGEntries e) throws Exception
    {
        CMJSUsernameCheck chkUsername = new CmFinoFIX.CMJSUsernameCheck();
        chkUsername.setUsername(e.getUsername());
        chkUsername.setCheckIfExists(true);
        CMJSError err = (CMJSError)usernameCheckProcessor.process(chkUsername);
        if(CmFinoFIX.ErrorCode_NoError.equals(err.getErrorCode())){
            return true;
        }
        return false;
    }
    private void initializeUserObject(User user, CMJSBankAdmin.CGEntries e) {
        if (e.getUsername() != null) {
            user.setUsername(e.getUsername());
        }
        Company company = userService.getUserCompany();
        user.setCompany(company);
        
        if (e.getEmail() != null) {
            user.setEmail(e.getEmail());
        }

        if (e.getFirstName() != null) {
            user.setFirstName(e.getFirstName());
        }

        if (e.getLastName() != null) {
            user.setLastName(e.getLastName());
        }

        if (e.getCreatedBy() != null) {
            user.setCreatedBy(e.getCreatedBy());
        }

        if (e.getLanguage() != null) {
            user.setLanguage(e.getLanguage());
        }

        if (e.getTimezone() != null) {
            user.setTimezone(e.getTimezone());
        }
         MfinoServiceProviderDAO mspdao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
         user.setmFinoServiceProviderByMSPID(mspdao.getById(1l));
        
          
        user.setStatus(CmFinoFIX.UserStatus_Active);
        
        if(e.getBankRoles()!=null){
        user.setRole(e.getBankRoles());
        }
        if(e.getAdminComment()!=null){
        user.setAdminComment(e.getAdminComment());
        }
        user.setStatusTime(new Timestamp());

//        if (e.getUpdatedBy() != null) {
//            user.setUpdatedBy(e.getUpdatedBy());
//        }
    }

 private void updateEntity(BankAdmin bankAdmin, CmFinoFIX.CMJSBankAdmin.CGEntries e) {
      if(e.getBankID()!=null){
          BankDAO dao = DAOFactory.getInstance().getBankDao();
          Bank bank = dao.getById(e.getBankID());
          bankAdmin.setBank(bank);
      }
     UserDAO dao = DAOFactory.getInstance().getUserDAO();
     User  user=null;
     if(bankAdmin.getUser().getID()!=null && CmFinoFIX.JSaction_Update.equalsIgnoreCase(action) ){
        user = dao.getById(bankAdmin.getUser().getID());
        if(e.getFirstName()!=null){
            user.setFirstName(e.getFirstName());
        }
        if(e.getLastName()!=null){
            user.setLastName(e.getLastName());
        }
        if(e.getEmail()!=null){
            user.setEmail(e.getEmail());
        }
        if(e.getBankRoles()!=null){
            user.setRole(e.getBankRoles());
        }
        if(e.getAdminComment()!=null){
            user.setAdminComment(e.getAdminComment());
        }
        if(e.getTimezone()!=null){
            user.setTimezone(e.getTimezone());
        }
        if(e.getLanguage()!=null){
            user.setLanguage(e.getLanguage());
        }
       dao.save(user);
     }     
    }

    private void updateMessage(BankAdmin bankAdmin, CmFinoFIX.CMJSBankAdmin.CGEntries e) {
        User user = bankAdmin.getUser();
        e.setUserID(user.getID());
        if (user.getUsername() != null) {
            e.setUsername(bankAdmin.getUser().getUsername());
        }
        if (bankAdmin.getID() != null) {
            e.setID(bankAdmin.getID());
        }
        if(user.getSecurityAnswer() !=null)
        {
            e.setSecurityAnswer(user.getSecurityAnswer());
        }

        if(user.getSecurityQuestion() !=null)
        {
            e.setSecurityQuestion(user.getSecurityQuestion());
        }
        if(user.getConfirmationTime() !=null)
        {
            e.setConfirmationTime(user.getConfirmationTime());
        }
        if(user.getUserActivationTime() !=null)
        {
            e.setUserActivationTime(user.getUserActivationTime());
        }
        if(user.getRejectionTime() !=null)
        {
            e.setRejectionTime(user.getRejectionTime());
        }
        if(user.getDateOfBirth() != null)
        {
            e.setDateOfBirth(user.getDateOfBirth());
        }
        if(user.getExpirationTime() !=null)
        {
            e.setExpirationTime(user.getExpirationTime());
        }
        if(user.getConfirmationCode() !=null)
        {
            e.setConfirmationCode(user.getConfirmationCode());
        }
        // Don't send the password back
        e.setPassword(null);

        if (user.getFirstTimeLogin() != null) {
            e.setFirstTimeLogin(user.getFirstTimeLogin());
        }

        if (bankAdmin.getCreateTime() != null) {
            e.setCreateTime(bankAdmin.getCreateTime());
        }

        if (bankAdmin.getCreatedBy() != null) {
            e.setCreatedBy(bankAdmin.getCreatedBy());
        }

        if (user.getEmail() != null) {
            e.setEmail(user.getEmail());
        }

        if (user.getFirstName() != null) {
            e.setFirstName(user.getFirstName());
        }

        if (user.getLastName() != null) {
            e.setLastName(user.getLastName());
        }

        if (user.getLanguage() != null) {
            e.setLanguage(user.getLanguage());
        }

        if (user.getLastLoginTime() != null) {
            e.setLastLoginTime(user.getLastLoginTime());
        }

        if (bankAdmin.getLastUpdateTime() != null) {
            e.setLastUpdateTime(bankAdmin.getLastUpdateTime());
        }
        if(bankAdmin.getBank()!=null){
            e.setBankID(bankAdmin.getBank().getID());
        }
        Integer restr = user.getRestrictions();
        if (restr != null) {
            e.setUserRestrictions(restr);
            if ((restr & CmFinoFIX.SubscriberRestrictions_Suspended) > 0) {
                e.setUserSuspended('Y');
            } else {
                e.setUserSuspended('N');
            }
            if ((restr & CmFinoFIX.SubscriberRestrictions_SecurityLocked) > 0) {
                e.setUserSecurityLocked('Y');
            } else {
                e.setUserSecurityLocked('N');
            }
        }

        Integer status = user.getStatus();
        if (status != null) {
            e.setUserStatus(status);
            e.setUserStatusText(enumTextService.getEnumTextValue(
                    CmFinoFIX.TagID_UserStatus, user.getLanguage(), user.getStatus().toString()));
        }

        if (user.getStatusTime() != null) {
            e.setStatusTime(user.getStatusTime());
        }

        if (user.getTimezone() != null) {
            e.setTimezone(user.getTimezone());
        }

        if (bankAdmin.getUpdatedBy() != null) {
            e.setUpdatedBy(bankAdmin.getUpdatedBy());
        }

        e.setBankRoles(user.getRole());
        e.setRoleText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BankRoles, user.getLanguage(), user.getRole()));

        e.setLanguageText(enumTextService.getEnumTextValue(
                CmFinoFIX.TagID_Language, user.getLanguage(), user.getLanguage()));

        if (user.getAdminComment() != null) {
            e.setAdminComment(user.getAdminComment());
        }
        if(user.getDateOfBirth()!=null){
            e.setDateOfBirth(user.getDateOfBirth());
        }
		if (user.getUserActivationTime() != null) {
			e.setUserActivationTime(user.getUserActivationTime());
		}
		if (user.getRejectionTime() != null) {
			e.setRejectionTime(user.getRejectionTime());
		}
		if (user.getExpirationTime() != null) {
			e.setExpirationTime(user.getExpirationTime());
		}
		if(user.getConfirmationTime()!=null){
			e.setConfirmationTime(user.getConfirmationTime());
		}
                if(user.getRestrictions()!=null){
                    //e.set
                }
                e.setRecordVersion(bankAdmin.getVersion());
    }
    
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSBankAdmin realMsg = (CMJSBankAdmin) msg;
        action = realMsg.getaction();
        BankAdminDAO bankAdmindao = DAOFactory.getInstance().getBankAdminDAO();
        if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            UserQuery query = new UserQuery();
            if(StringUtils.isNotBlank(realMsg.getUsernameSearch())){
                query.setUserName(realMsg.getUsernameSearch());
            }
            if(StringUtils.isNotBlank(realMsg.getFirstNameSearch())){
                query.setFirstNameLike(realMsg.getFirstNameSearch());
            }
            if(StringUtils.isNotBlank(realMsg.getLastNameSearch())){
                query.setLastNameLike(realMsg.getLastNameSearch());
            }
            if(StringUtils.isNotBlank(realMsg.getRoleSearch())){
                query.setRole(Integer.parseInt(realMsg.getRoleSearch()));
            }
            if(StringUtils.isNotBlank(realMsg.getRestrictionsSearch())){
                query.setRestrictions(Integer.parseInt(realMsg.getRestrictionsSearch()));
            }
            String status = realMsg.getStatusSearch();
            if (StringUtils.isNotBlank(status)) {
                query.setStatus(Integer.parseInt(status));
            }
            query.setCompany(userService.getUserCompany());
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            List<BankAdmin> results = bankAdmindao.get(query);

            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                BankAdmin u = results.get(i);
                CMJSBankAdmin.CGEntries entry = new CMJSBankAdmin.CGEntries();

                updateMessage(u, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
             
        } else if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
              CMJSBankAdmin.CGEntries[] entries = realMsg.getEntries();

            for (CMJSBankAdmin.CGEntries entry : entries) {
                BankAdmin bankadminObj = bankAdmindao.getById(entry.getID());
                // Check for Stale Data
                if (!entry.getRecordVersion().equals(bankadminObj.getVersion())) {
                    handleStaleDataException();
                }
                updateEntity(bankadminObj, entry);
                bankAdmindao.save(bankadminObj);
                updateMessage(bankadminObj, entry);
            }
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            for (CMJSBankAdmin.CGEntries e : realMsg.getEntries()) {
                if (e == null) {
                    CMJSError err = new CMJSError();
                    err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    err.setErrorDescription(MessageText._("Please refresh and try again."));
                    return err;
                }
                if(!validateUsername(e))
                {
                    CMJSError err = new CMJSError();
                    err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    err.setErrorDescription(MessageText._("Username already exist in DB, try with other username"));
                    return err;
                }
                
                User userEntry = new User();
                UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
                initializeUserObject(userEntry, e);
                String genPwd = PasswordGenUtil.generate();
                PasswordEncoder encoder = new ShaPasswordEncoder(1);
                String encPassword = encoder.encodePassword(genPwd, userEntry.getUsername());
                userEntry.setPassword(encPassword);
                userDAO.save(userEntry);
                BankAdmin bankAdminObj = new BankAdmin();
                bankAdminObj.setUser(userEntry);
                updateEntity(bankAdminObj, e);
                bankAdmindao.save(bankAdminObj);

                //send email to the bank admin
                log.info("Sending Email to the bank admin with username" + userEntry.getUsername());
                 String emailMsg =
	                        String.format(
	                        "Dear %s %s,\n\tYour username is %s \n\tYour pwd is %s" + ".\n You can login : " + ConfigurationUtil.getAppURL() + " \n" + ConfigurationUtil.getAdditionalMsg() + "\n" + ConfigurationUtil.getEmailSignature(), userEntry.getFirstName(),
	                        userEntry.getLastName(), userEntry .getUsername(), genPwd);
	            String emailSubject = ConfigurationUtil.getUserInsertSubject();

                try {
                    mailService.sendMailMultiX(userEntry.getEmail(), userEntry.getFirstName() + " " + userEntry.getLastName(), emailSubject,
                            emailMsg);
                } catch (Exception exception) {
                    log.error("Failed to send User Add information.", exception);
                    realMsg.setsuccess(CmFinoFIX.Boolean_False);
                    realMsg.seterrorCodes("Couldn't send email to " + e.getUsername());
                    return realMsg;
                }
                    updateMessage(bankAdminObj, e);

            }
        }

        return realMsg;
    }

}
