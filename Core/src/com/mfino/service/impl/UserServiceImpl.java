/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.BranchCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.RoleDAO;
import com.mfino.dao.RolePermissionDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.RolePermissionQuery;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.BranchCodes;
import com.mfino.domain.Company;
import com.mfino.domain.Partner;
import com.mfino.domain.Role;
import com.mfino.domain.RolePermission;
import com.mfino.domain.Subscriber;
import com.mfino.domain.MfinoUser;
import com.mfino.exceptions.InvalidPasswordException;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMErrorNotification;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.MailService;
import com.mfino.service.SystemParametersService;
import com.mfino.service.UserDetailsServiceImpl;
import com.mfino.service.UserService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.MfinoUtil;
import com.mfino.util.PasswordGenUtil;

/**
 *
 * @author xchen
 */
@Service("UserServiceImpl")
public class UserServiceImpl implements UserService {
	
	private static UserDAO userDao = DAOFactory.getInstance().getUserDAO();
	private static RolePermissionDAO rpDao = DAOFactory.getInstance().getRolePermissionDAO();
	private static RoleDAO roleDao = DAOFactory.getInstance().getRoleDAO();
	private static BranchCodeDAO branchCodeDao = DAOFactory.getInstance().getBranchCodeDAO();
	
    private static String defaultLanguage = "English";
    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
  

    @Transactional(readOnly=true, propagation = Propagation.REQUIRED)
    public MfinoUser getCurrentUser() throws MfinoRuntimeException {
    	
    	try{
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        if (auth == null) {
	            return null;
	        }
	        UserQuery query = new UserQuery();
	        query.setUserName(auth.getName());
	        List<MfinoUser> results = (List<MfinoUser>) userDao.get(query);
	        if (results.size() > 0) {
	            return results.get(0);
	        } else {
	            return null;
	        }
    	}catch(Exception e){
    		throw new MfinoRuntimeException(e);
    	}
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public  String getUserString() {
        MfinoUser u = getCurrentUser();
        if (u == null) {
            return StringUtils.EMPTY;
        } else {
            return getUserString(u);
        }
    }

    public  String getUserString(MfinoUser userObj) {
        return String.format("%s %s (%s) Role: %s", userObj.getFirstname(), userObj.getLastname(), userObj.getUsername(), getUserRole(userObj.getRole().intValue()));
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public  String getUserLanguageCode() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return defaultLanguage;
        }
        
        String languageCode = ((UserDetailsServiceImpl) auth.getPrincipal()).getLanguageCode();
        if (languageCode == null) {
            return defaultLanguage;
        }else{
            return languageCode;
        }
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public  Company getUserCompany() {
        MfinoUser user = getCurrentUser();
        if (user != null) {
            return user.getCompany();
        } else {
            return null;
        }
    }
    
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public  void reloadPermissions(MfinoUser user) {
        Integer role = user.getRole().intValue();

        if (null == role) {
            //Shouldn't happen unless the data is corrupted.
            log.error(MessageText._("No role for defined for user : ") + user.getUsername());
            return;
        }
        
        RolePermissionQuery query = new RolePermissionQuery();
        query.setUserRole(role);
        List<RolePermission> rolePermissions = rpDao.get(query);
        
        Iterator iter = rolePermissions.iterator();
        Set<Integer> permissions = new HashSet<Integer>();

        if (iter != null) {
            while (iter.hasNext()) {
                RolePermission rp = (RolePermission) iter.next();
                permissions.add(((Long)rp.getPermission()).intValue());
            }
        }
        
        user.setPermissions(permissions);
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public  void loadPermissions(MfinoUser user) {
        Set<Integer> permissions = user.getPermissions();
        if (permissions == null || permissions.size() == 0) {
            reloadPermissions(user);
        }
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    //sends a simple email to the current logged in user account's registered email
    public  boolean sendEmail(String subject, String body) {
        UserQuery query = new UserQuery();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        String userName = auth.getName();
        query.setUserName(userName);
        List<MfinoUser> results = (List<MfinoUser>) userDao.get(query);
        if (results.size() > 0) {
            MfinoUser userObj = results.get(0);
            String email = userObj.getEmail();

            try {
                mailService.sendMailMultiX(email,
                        userObj.getFirstname() + " " + userObj.getLastname(),
                        subject, body);
            } catch (Exception e) {
                log.error("Failed to send email", e);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public  boolean isMerchant() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        Integer role = ((UserDetailsServiceImpl) auth.getPrincipal()).getRole();
        if (role == null) {
            //Shouldn't happen unless the data is corrupted.
            log.error(MessageText._("No role for defined for user : ") + auth.getName());
            return false;
        }

        return (CmFinoFIX.Role_Merchant.equals(role));
    }
    
    
    
    /**
     * This method is use to verify if given role is of type SystemUser or not
     * 
     * @param enumCode
     * @return true if enumCode - role is of type systemUser else returns false
     */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public  boolean isSystemUser(Integer enumCode) {      
        Role role = roleDao.getById(enumCode);
        if (role == null) {
            log.error(MessageText._("No role defined with the enumCode : ") + enumCode);
            return false;
        }
        return (role.getIssystemuser() != 0);
    }
    
    public  String getUserRole(Integer enumCode) {      
        Role role = roleDao.getById(enumCode);
       if (role == null) {
           log.error(MessageText._("No role defined with the enumCode : ") + enumCode);
           return null;
       }
        return role.getDisplaytext();
    }
    
    public  String getUserBranchCode(Integer enumCode) {      
        BranchCodes branchcodes = branchCodeDao.getById(enumCode);
        if (branchcodes == null) {
            log.error(MessageText._("No BranchCode defined with the code : ") + enumCode);
            return null;
        }
        return branchcodes.getBranchcode()+"-"+branchcodes.getBranchname();
    }

    public  String getUserBranchCodeString() {
    	MfinoUser user=getCurrentUser();
    	Long enumCode=user.getBranchcodeid();
        BranchCodes branchcodes = branchCodeDao.getById(enumCode);
        if (branchcodes == null) {
            log.error(MessageText._("No BranchCode defined with the code : ") + enumCode);
            return null;
        }
        return branchcodes.getBranchcode()+"-"+branchcodes.getBranchname();
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public  Partner getPartner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        MfinoUser user=getCurrentUser();
        if(user!=null){
		Set<Partner> partner=user.getPartners();
		if(partner!=null&&!partner.isEmpty()){
			Partner curPartner=partner.iterator().next();
			Subscriber sub=curPartner.getSubscriber();
			if(sub!=null&&CmFinoFIX.SubscriberType_Partner.equals(sub.getType())){
			return curPartner;
			}
			}
		}
        return null;
    }
    
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public  void changePassword(String username, String oldPassword, String newPassword, Boolean isMerchantRegistration, boolean checkExisting) throws InvalidPasswordException, MfinoRuntimeException{
    	MfinoUser user;
        UserQuery query = new UserQuery();
        query.setUserName(username);
        List<MfinoUser> results = userDao.get(query);
        if (results != null && results.size() > 0) {
            user = results.get(0);
            
            //verify the old password
            if(checkExisting) {
            	PasswordEncoder encoder2 = new ShaPasswordEncoder(1);
            	String encPassword2 = encoder2.encodePassword(oldPassword, username);
            	if(!encPassword2.equals(user.getPassword())) {
            		throw new InvalidPasswordException(MessageText._("Invalid Old password"));
            	}
            }
            
			PasswordEncoder encoder = new ShaPasswordEncoder(1);
			String encPassword = encoder.encodePassword(newPassword, username);
			
			if(isOldPassword(user,encPassword)){
				throw new InvalidPasswordException(MessageText._("password already used previously"));
			}
						
			user.setPassword(encPassword);
			if (isMerchantRegistration) {
				user.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
				user.setStatus(CmFinoFIX.UserStatus_Active);
			}
			user.setFirsttimelogin((short) 0);
			user.setLastpasswordchangetime(new Timestamp());
			userDao.save(user);
        } else {
            throw new UsernameNotFoundException(MessageText._("Invalid username or password"));
        }
    }

	public  boolean isOldPassword(MfinoUser user, String encPassword){
    	//verify newpassword and oldpassword are same
		log.info("Checking password history");
		if(encPassword.equals(user.getPassword())){
			return true;
		}
		int count = systemParametersService.getInteger(SystemParameterKeys.PASSWORD_HISTORY_COUNT)-1;
		if(count>0){
			//check history
			String newHistory=user.getPassword();
			String oldHistory = user.getPasswordhistory();
			if(oldHistory!=null){
				if(oldHistory.contains(encPassword)){
					return true;
				}
				String[] history = oldHistory.split("["+GeneralConstants.PASSWORDHISTORY_SEPARATOR+"]");
				int separatorindex = oldHistory.lastIndexOf(GeneralConstants.PASSWORDHISTORY_SEPARATOR);
				
				if(history.length<count){
					newHistory = newHistory+GeneralConstants.PASSWORDHISTORY_SEPARATOR+oldHistory;
				}else if(history.length==count&&separatorindex!=-1){
					newHistory = newHistory+GeneralConstants.PASSWORDHISTORY_SEPARATOR+oldHistory.substring(0,separatorindex);
				}else{
					for(int i=0;i<count-1;i++){
					 newHistory = newHistory+GeneralConstants.PASSWORDHISTORY_SEPARATOR+history[i];	
					}
				}
			}
			user.setPasswordhistory(newHistory);			
		}
		return false;
	}


	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public  Integer getNativeLanguageCode(){
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName =(auth != null) ? auth.getName() : " ";
        UserQuery query = new UserQuery();
        query.setUserName(userName);
        List<MfinoUser> results = (List<MfinoUser>) userDao.get(query);
        if (results.size() > 0) {
             MfinoUser userObj = results.get(0);
             language = ((Long)userObj.getLanguage()).intValue();

        }
        return language;
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public  void sendForgotPasswordLink(String username) throws EmailException {
		log.info("Forgot password request for user : "+username);
		 MfinoUser user;
	        UserQuery query = new UserQuery();
	        query.setUserName(username);
	        List<MfinoUser> results = userDao.get(query);
	        if (results != null && results.size() > 0) {
	            user = results.get(0);
				Integer OTPLength = systemParametersService.getOTPLength();
	            String code=MfinoUtil.calculateDigestPin(username,MfinoUtil.generateOTP(OTPLength));
	            user.setForgotpasswordcode(code);
	            user.setExpirationtime(new Timestamp(DateUtil.addDays(new Date(), 1)));
	            String mail = user.getEmail();
	            String sub =" Forgot Password Link";
	            String msg= MfinoUtil.generateForgotPasswordMail(user.getUsername(),code);
	            userDao.save(user);
				mailService.sendMail(mail, username, sub, msg);
				log.info("Reset password link sent to "+user.getEmail()+" for user:"+username);
	        } else {
	            throw new UsernameNotFoundException(MessageText._("Invalid username or password"));
	        }
		
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public  CMErrorNotification resetPassword(String username,String code) {
		log.info("Reset Password request for user:"+username+" with code:"+code);
		CMErrorNotification error =new CMErrorNotification();
		 error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		 MfinoUser user;
	        UserQuery query = new UserQuery();
	        query.setUserName(username);
	        List<MfinoUser> results = userDao.get(query);
	        if (results != null && results.size() > 0) {
	            user = results.get(0);
	            if(code.equals(user.getForgotpasswordcode())){
	            	if(user.getExpirationtime()!=null&&(new Timestamp().before(user.getExpirationtime()))){
	            		user.setExpirationtime(null);
	            		user.setForgotpasswordcode(null);
	            		error=resetPassword(user);
	            	}else{
	            		log.info("Reset Password link has been expired for user:"+username+" with code:"+code);
	            		error.setErrorDescription("Reset Password link has been expired");
	            	}
	            }else{
	            	log.info("Reset Password code missmatch for user:"+username+" with code:"+code);
	            	error.setErrorDescription("Reset Password link has been expired");
	            }	           
	        } else {
	            throw new UsernameNotFoundException(MessageText._("Invalid username or password"));
	        }
	        return error;
		
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public  CMErrorNotification resetPassword(MfinoUser user){
		 String genPwd = PasswordGenUtil.generate();
		    PasswordEncoder encoder = new ShaPasswordEncoder(1);
		    String encPassword = encoder.encodePassword(genPwd, user.getUsername());
		    while(isOldPassword(user, encPassword)){
		    	    genPwd = PasswordGenUtil.generate();
				    encPassword = encoder.encodePassword(genPwd, user.getUsername());
		    }
		    user.setPassword(encPassword);
		    // this triggers the user to change his password when he logs in
		    user.setFirsttimelogin((short) 1);
		    String emailMsg =
		        String.format(
		            "Dear %s %s,\n\tYour username is %s \n\tYour pwd has been reset to: %s"
		                + ".\n You can login : " + ConfigurationUtil.getAppURL()
		                + " \n" + ConfigurationUtil.getAdditionalMsg() + "\n"
		                + ConfigurationUtil.getEmailSignature(), user.getFirstname(),
		            user.getLastname(), user.getUsername(), genPwd);

		    userDao.save(user);

		    CFIXMsg pMsg = mailService.sendMailMultiX(user.getEmail(), user.getFirstname() + " "
		    		+ user.getLastname(), ConfigurationUtil.getResetPasswordSubject(),
		    				emailMsg);
		    return  (CMErrorNotification)pMsg;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<MfinoUser> get(UserQuery userQuery) throws MfinoRuntimeException
	{
		return userDao.get(userQuery);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MfinoUser getByUserName(String userName) 
	{
		UserDAO userDao = DAOFactory.getInstance().getUserDAO();
		return userDao.getByUserName(userName);
	}
	
	 
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public Integer getCurrentUserPriorityLevel() {
		try {
			Authentication auth = SecurityContextHolder.getContext()
					.getAuthentication();
			if (auth == null) {
				return null;
			}
			Integer roleID = ((UserDetailsServiceImpl) auth.getPrincipal())
					.getRole();
			if (roleID == null) {
				// Shouldn't happen unless the data is corrupted.
				log.error(MessageText._("No role for defined for user : ")
						+ auth.getName());
				return null;
			}
			Role role = roleDao.getById(roleID);
			if (role == null) {
				log.error(MessageText._("No Role entry for role : ") + roleID);
				return null;
			}
			return role.getPrioritylevel().intValue();
		} catch (Exception e) {
			log.error(MessageText._("Exception in getting priority level"));
		}
		return null;
	}

	@Override
	public String generateUserName(String userName) {
		if(StringUtils.isBlank(userName))
			userName = RandomStringUtils.randomAlphabetic(4);
		while(getByUserName(userName)!=null){
			userName = userName+RandomStringUtils.randomNumeric(2);
		}
		return userName;
	}
}
