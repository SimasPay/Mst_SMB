/**
 * 
 */
package com.mfino.service;

import java.util.List;

import org.apache.commons.mail.EmailException;

import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Partner;
import com.mfino.domain.User;
import com.mfino.exceptions.InvalidPasswordException;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.fix.CmFinoFIX.CMErrorNotification;

/**
 * @author Shashank
 *
 */
public interface UserService {


	
    public User getCurrentUser() throws MfinoRuntimeException;

    public  String getUserString();

    public  String getUserString(User userObj);

    
    public  String getUserLanguageCode();

    
    public  Company getUserCompany();
    
     void reloadPermissions(User user);
    
    public  void loadPermissions(User user);

    public  boolean sendEmail(String subject, String body);
    
    public  boolean isMerchant();
    
    public  boolean isSystemUser(Integer enumCode);
    
    public String getUserRole(Integer enumCode);
        
    public  Partner getPartner();
    
    public  void changePassword(String username, String oldPassword, String newPassword, Boolean isMerchantRegistration, boolean checkExisting) throws InvalidPasswordException, MfinoRuntimeException;

	public  boolean isOldPassword(User user, String encPassword);
	
    public  Integer getNativeLanguageCode();
    
	public  void sendForgotPasswordLink(String username) throws EmailException;

	
	public  CMErrorNotification resetPassword(String username,String code);
	
	
	public  CMErrorNotification resetPassword(User user);
	
	
	public List<User> get(UserQuery userQuery) throws MfinoRuntimeException;

	public User getByUserName(String userName);
	
	public Integer getCurrentUserPriorityLevel();

	public String generateUserName(String tradeName);
}
