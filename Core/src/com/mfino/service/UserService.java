/**
 * 
 */
package com.mfino.service;

import java.util.List;

import org.apache.commons.mail.EmailException;

import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Partner;
import com.mfino.domain.MfinoUser;
import com.mfino.exceptions.InvalidPasswordException;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.fix.CmFinoFIX.CMErrorNotification;

/**
 * @author Shashank
 *
 */
public interface UserService {


	
    public MfinoUser getCurrentUser() throws MfinoRuntimeException;

    public  String getUserString();

    public  String getUserString(MfinoUser userObj);

    
    public  String getUserLanguageCode();

    
    public  Company getUserCompany();
    
     void reloadPermissions(MfinoUser user);
    
    public  void loadPermissions(MfinoUser user);

    public  boolean sendEmail(String subject, String body);
    
    public  boolean isMerchant();
    
    public  boolean isSystemUser(Integer enumCode);
    
    public String getUserRole(Integer enumCode);
    
    public String getUserBranchCode(Integer enumCode);
    
    public String getUserBranchCodeString();
    
    public  Partner getPartner();
    
    public  void changePassword(String username, String oldPassword, String newPassword, Boolean isMerchantRegistration, boolean checkExisting) throws InvalidPasswordException, MfinoRuntimeException;

	public  boolean isOldPassword(MfinoUser user, String encPassword);
	
    public  Integer getNativeLanguageCode();
    
	public  void sendForgotPasswordLink(String username) throws EmailException;

	
	public  CMErrorNotification resetPassword(String username,String code);
	
	
	public  CMErrorNotification resetPassword(MfinoUser user);
	
	
	public List<MfinoUser> get(UserQuery userQuery) throws MfinoRuntimeException;

	public MfinoUser getByUserName(String userName);
	
	public Integer getCurrentUserPriorityLevel();

	public String generateUserName(String tradeName);
}
