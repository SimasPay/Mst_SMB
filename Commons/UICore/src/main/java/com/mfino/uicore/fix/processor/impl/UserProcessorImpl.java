package com.mfino.uicore.fix.processor.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.RoleDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Role;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSUsers;
import com.mfino.fix.CmFinoFIX.CMJSUsers.CGEntries;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.service.MailService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.UserProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.PasswordGenUtil;

@Service("UserProcessorImpl")
public class UserProcessorImpl extends BaseFixProcessor implements UserProcessor{
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;	

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;

	//private static final String COMMA = ", ";
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSUsers realMsg = (CMJSUsers) msg;

		UserDAO userDAO = DAOFactory.getInstance().getUserDAO();

		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {

			CMJSUsers.CGEntries[] entries = realMsg.getEntries();

			for (CMJSUsers.CGEntries entry : entries) {
				User userObj = userDAO.getById(entry.getID());
				log.info("User:"+userObj.getID()+" details edit requested by:"+getLoggedUserNameWithIP());
				// Check for Stale Data
				if (!entry.getRecordVersion().equals(userObj.getVersion())) {
					handleStaleDataException();
				}
				User currentUser =userService.getCurrentUser();
				if(currentUser.equals(userObj)){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					error.setErrorDescription(MessageText._("You are not allowed to edit your details"));
					log.warn("User:"+userObj.getID()+" not allowed to edit self details");
					return error;
				}
				if(entry.getRole()!= null){
					CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
					error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					if(userService.isSystemUser(entry.getRole()) && !userService.isSystemUser(userObj.getRole())){
						error.setErrorDescription(MessageText._("Role cannot be updated to system type from non-system type"));
						log.warn("User:"+userObj.getID()+" role cannot be updated to system type from non-system type");
					return error;
					} else if(!userService.isSystemUser(entry.getRole()) && userService.isSystemUser(userObj.getRole())){
						error.setErrorDescription(MessageText._("Role cannot be updated to non-system type from system type"));
						log.warn("User:"+userObj.getID()+" role cannot be updated to non-system type from system type");
						return error;
					}					
				}

				// Integer oldRestrictions = userObj.getRestrictions();
				Integer newRestrictions = entry.getUserRestrictions();

				// Here check for the Authorization.
				// CHeck if the restrictions are edited or not.
				if (entry.isRemoteModifiedUserRestrictions()) {
					userObj.setRestrictions(newRestrictions);
					log.info("User:"+userObj.getID()+" restrictions:"+newRestrictions+" updated by:"+getLoggedUserNameWithIP());
				}

				if (entry.isRemoteModifiedRole()) {					
					Integer newRole = entry.getRole();
					if (newRole != null) {
						userObj.setRole(newRole);
						log.info("User:"+userObj.getID()+" role:"+newRole+" updated by:"+getLoggedUserNameWithIP());
					}
				}


				if ((null != entry.getIsCreditCardUserModified() && entry.getIsCreditCardUserModified()) || modifiedOtherFields(entry)) {					
					if ((null != entry.getIsCreditCardUserModified() && entry.getIsCreditCardUserModified())) {
						//Confirmation Code
						String confirmationCode = UUID.nameUUIDFromBytes((userObj.getUsername()+new Date()).getBytes()).toString();
						userObj.setConfirmationCode(confirmationCode);
						updateEntity(userObj, entry);
					} else {
						updateEntity(userObj, entry);
					}
				}
				userDAO.save(userObj);
				updateMessage(userObj, entry);
				log.info("User:"+userObj.getID()+" details edit completed by:"+getLoggedUserNameWithIP());
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {

			UserQuery query = new UserQuery();
			
			User currentUser = userService.getCurrentUser();
			RoleDAO roleDao = DAOFactory.getInstance().getRoleDAO();
			Role role = roleDao.getById(currentUser.getRole());
			
			query.setPriorityLevel(role.getPriorityLevel());
			query.setStart(realMsg.getstart());
			query.setLimit(realMsg.getlimit());
			query.setFirstNameLike(realMsg.getFirstNameSearch());
			query.setLastNameLike(realMsg.getLastNameSearch());
			query.setUserNameLike(realMsg.getUsernameSearch());
			String roleSearch = realMsg.getRoleSearch();
			if (roleSearch != null && roleSearch.trim().length() > 0) {
				query.setRole(Integer.parseInt(roleSearch));
			}
			if(realMsg.getIsRequestFromCCReviewerTab()!=null && realMsg.getIsRequestFromCCReviewerTab().equals(Boolean.TRUE)){
				query.setRole(CmFinoFIX.Role_Subscriber);
				query.setAddOrder(Boolean.TRUE);
			}
			if(realMsg.getCreationDateStartTime() !=null){
				query.setCreateTimeGE(realMsg.getCreationDateStartTime());
			}
			if(realMsg.getCreationDateEndTime() !=null){
				query.setCreateTimeLT(realMsg.getCreationDateEndTime());
			}

			if(realMsg.getLastUpdateStartTime() !=null){
				query.setLastUpdateTimeGE(realMsg.getLastUpdateStartTime());
			}
			if(realMsg.getLastUpdateEndTime() !=null){
				query.setLastUpdateTimeLT(realMsg.getLastUpdateEndTime());
			}

			if(realMsg.getUserActivationStartTime() !=null){
				query.setActivationTimeGE(realMsg.getUserActivationStartTime());
			}
			if(realMsg.getUserActivationEndTime() !=null){
				query.setActivationTimeLT(realMsg.getUserActivationEndTime());
			}

			if(realMsg.getConfirmationDateStartTime() !=null){
				query.setConfirmationTimeGE(realMsg.getConfirmationDateStartTime());
			}
			if(realMsg.getConfirmationDateEndTime() !=null){
				query.setConfirmationTimeLT(realMsg.getConfirmationDateEndTime());
			}

			String status = realMsg.getStatusSearch();
			if (status != null && status.trim().length() > 0) {
				query.setStatus(Integer.parseInt(status));
			}
			query.setCompany(userService.getUserCompany());
			String restrictions = realMsg.getRestrictionsSearch();
			if (restrictions != null && restrictions.trim().length() > 0) {
				query.setRestrictions(Integer.parseInt(restrictions));
			}           
			log.info("User details queried by:"+getLoggedUserNameWithIP());
			List<User> results = userDAO.get(query);

			realMsg.allocateEntries(results.size());

			for (int i = 0; i < results.size(); i++) {
				User u = results.get(i);
				CMJSUsers.CGEntries entry = new CMJSUsers.CGEntries();
				log.info("Returing user:"+u.getID()+" details requested by:"+getLoggedUserNameWithIP());
				updateMessage(u, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			log.info("Completed User details queried by:"+getLoggedUserNameWithIP());
		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {

			CMJSUsers.CGEntries[] entries = realMsg.getEntries();

			for (CMJSUsers.CGEntries e : entries) {
				UserQuery query = new UserQuery();
				query.setUserName(e.getUsername());
				log.info("Insert User data for"+e.getUsername()+" requested by:"+getLoggedUserNameWithIP());
				
				List<User> users = userDAO.get(query);
				if (users != null && users.size() != 0) {
					// username already in use. So skip adding and report failure
					CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
					errorMsg.setErrorDescription(String.format(
							"Username %s not available", e.getUsername()));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					// realMsg.setsuccess(CmFinoFIX.Boolean_False);
					// realMsg.seterrorCodes("Username not available");
					return errorMsg;
				}
				if(!userService.isSystemUser(e.getRole())){
					CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
					errorMsg.setErrorDescription("User creation with this Role(non-system user) not allowed");
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					log.warn("User:"+e.getUsername()+ " creation with the Role:"+e.getRole() +"(non-system user) not allowed, requested by:"+getLoggedUserNameWithIP());
					return errorMsg;
				}

				User u = new User();
				if (userService.getUserCompany() != null) {
					Company company = userService.getUserCompany();
					e.setCompanyID(company.getID());
				} else {
					CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
					errorMsg.setErrorDescription(String
							.format("Company does not exist for the logged in user"));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					return errorMsg;
				}
				updateEntity(u, e);

				// generate a password and hash it
				String password = PasswordGenUtil.generate();
				PasswordEncoder encoder = new ShaPasswordEncoder(1);
				String encPassword = encoder.encodePassword(password, u.getUsername());
				u.setPassword(encPassword);

				userDAO.save(u);

				// send mail
				String emailMsg =
					String.format(
							"Dear %s %s,\n\tYour username is %s \n\tYour pwd is %s" + ".\n You can login : " + ConfigurationUtil.getAppURL() + " \n" + ConfigurationUtil.getAdditionalMsg() + "\n" + ConfigurationUtil.getEmailSignature(), u.getFirstName(),
							u.getLastName(), u.getUsername(), password);
				String emailSubject = ConfigurationUtil.getUserInsertSubject();

				updateMessage(u, e);

				String email=u.getEmail();
				String name=u.getFirstName();
				log.info("sending mail to user "+ u.getUsername());
				mailService.asyncSendEmail(email, name, emailSubject, emailMsg);
				log.info("Completed Insert User data for"+e.getUsername()+" requested by:"+getLoggedUserNameWithIP());
			}

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
		}
		return msg;
	}

	public CFIXMsg processSubscriber(CFIXMsg msg) {
		CMJSUsers realMsg = (CMJSUsers) msg;
		UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
		CMJSUsers.CGEntries[] entries = realMsg.getEntries();

		for (CMJSUsers.CGEntries e : entries) {
			UserQuery query = new UserQuery();
			query.setUserName(e.getUsername());
			List<User> users = userDAO.get(query);
			if (users != null && users.size() != 0) {
				// username already in use. So skip adding and report failure
				CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
				errorMsg.setErrorDescription(String.format(
						"Username %s not available", e.getUsername()));
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				return errorMsg;
			}
			log.info("Insert User data for"+e.getUsername()+" requested by:"+getLoggedUserNameWithIP());
			User u = new User();
			u.setPassword(e.getPassword());
			if(e.getFirstTimeLogin() != null)
				u.setFirstTimeLogin(e.getFirstTimeLogin());

			updateEntity(u, e);

			//Confirmation Code
			String confirmationCode = UUID.nameUUIDFromBytes((u.getUsername()+new Date()).getBytes()).toString();
			u.setConfirmationCode(confirmationCode);
			userDAO.save(u);

			updateMessage(u, e);
			log.info("Completed Insert User data for"+e.getUsername()+" requested by:"+getLoggedUserNameWithIP());
		}

		realMsg.setsuccess(CmFinoFIX.Boolean_True);
		realMsg.settotal(entries.length);

		return realMsg;
	}

	private void updateEntity(User user, CGEntries e) {
		String ID = String.valueOf(user.getID());
		if(ID==null){
			ID = e.getUsername();
		}
		if (e.getUsername() != null) {
			if(!e.getUsername().equals(user.getUsername())){
				log.info("User:"+ID+" username updated to:"+e.getUsername()+" by:"+getLoggedUserNameWithIP());
			}
			user.setUsername(e.getUsername());
		}
		if(e.getCompanyID()!=null){
			Company company = DAOFactory.getInstance().getCompanyDAO().getById(e.getCompanyID());
			if(company!=user.getCompany()){
				log.info("User:"+ID+" company updated to:"+company.getID()+" by:"+getLoggedUserNameWithIP());
			}
			user.setCompany(company);
		}
		if (e.getPassword() != null) {
			// generate a password and hash it
			String pwd = e.getPassword();
			PasswordEncoder encoder = new ShaPasswordEncoder(1);
			String encPassword = encoder.encodePassword(pwd, user.getUsername());
			if(!encPassword.equals(user.getPassword())){
				log.info("User:"+ID+" password updated by:"+getLoggedUserNameWithIP());
			}
			user.setPassword(encPassword);
		}

		if (e.getEmail() != null) {
			if(!e.getEmail().equals(user.getEmail())){
				log.info("User:"+ID+" email updated to:"+e.getEmail()+" by:"+getLoggedUserNameWithIP());
			}
			user.setEmail(e.getEmail());
		}
		Integer role = e.getRole();

		if (role != null) {
			if(!role.equals(user.getRole())){
				log.info("User:"+ID+" role updated to:"+role+" by:"+getLoggedUserNameWithIP());
			}
			user.setRole(role);
		}
		
		Long branchcodeID = e.getBranchCodeID();

		if (branchcodeID != null) {
			
			user.setBranchCodeID(branchcodeID);
		}

		if (e.getFirstName() != null) {
			if(user.getRole()==CmFinoFIX.Role_Subscriber){
				if(user.getFirstName()!= null){
					user.setOldFirstName(user.getFirstName());
				}
				else{
					user.setOldFirstName(e.getFirstName());
				}
			}
			if(!e.getFirstName().equals(user.getFirstName())){
        		log.info("User:"+ID+" firstname updated to:"+e.getFirstName()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setFirstName(e.getFirstName());
		}

		if (e.getLastName() != null) {
			if(user.getRole()==CmFinoFIX.Role_Subscriber){
				if(user.getLastName()!= null){
					user.setOldLastName(user.getLastName());
				}
				else{
					user.setOldLastName(e.getLastName());
				}
			}
			if(!e.getLastName().equals(user.getLastName())){
        		log.info("User:"+ID+" Lastname updated to:"+e.getLastName()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setLastName(e.getLastName());
		}

		if (e.getCreatedBy() != null) {
			if(!e.getCreatedBy().equals(user.getCreatedBy())){
        		log.info("User:"+ID+" createdBy updated to:"+e.getCreatedBy()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setCreatedBy(e.getCreatedBy());
		}

		if (e.getLanguage() != null) {
			if(!e.getLanguage().equals(user.getLanguage())){
        		log.info("User:"+ID+" Language updated to:"+e.getLanguage()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setLanguage(e.getLanguage());
		}

		if (e.getTimezone() != null) {
			if(!e.getTimezone().equals(user.getTimezone())){
        		log.info("User:"+ID+" timezone updated to:"+e.getTimezone()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setTimezone(e.getTimezone());
		}

		if (e.getUserStatus() != null) {
			if(!e.getUserStatus().equals(user.getStatus())){
        		log.info("User:"+ID+" status updated to:"+e.getUserStatus()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setStatus(e.getUserStatus());
			user.setStatusTime(new Timestamp());
			if(CmFinoFIX.UserStatus_Active.equals(e.getUserStatus()))
			{
				// this holds gud only for subscriber activation
				user.setUserActivationTime(user.getStatusTime());
			}
			else if(CmFinoFIX.UserStatus_Confirmed.equals(e.getUserStatus())){
				user.setConfirmationTime(user.getStatusTime());
			}
			else if(CmFinoFIX.UserStatus_Expired.equals(e.getUserStatus()))
			{
				user.setExpirationTime(user.getStatusTime());
			}
			else if(CmFinoFIX.UserStatus_Rejected.equals(e.getUserStatus()))
			{
				user.setRejectionTime(user.getStatusTime());
			}
		}
		if(e.getDateOfBirth() != null)
		{
			if(!e.getDateOfBirth().equals(user.getDateOfBirth())){
        		log.info("User:"+ID+" Date of Birth updated to:"+e.getDateOfBirth()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setDateOfBirth(e.getDateOfBirth());
		}

		if(e.getSecurityQuestion()!=null){
			if(user.getRole()==CmFinoFIX.Role_Subscriber){
				if(user.getSecurityQuestion()!= null){
					user.setOldSecurityQuestion(user.getSecurityQuestion());
				}
				else{
					user.setOldSecurityQuestion(e.getSecurityQuestion());
				}
			}
			if(!e.getSecurityQuestion().equals(user.getSecurityQuestion())){
        		log.info("User:"+ID+" security question updated to:"+e.getSecurityQuestion()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setSecurityQuestion(e.getSecurityQuestion());
		}
		if(e.getSecurityAnswer()!=null){
			if(user.getRole()==CmFinoFIX.Role_Subscriber){
				if(user.getSecurityAnswer()!= null){
					user.setOldSecurityAnswer(user.getSecurityAnswer());
				}
				else{
					user.setOldSecurityAnswer(e.getSecurityAnswer());
				}
			}
			if(!e.getSecurityAnswer().equals(user.getSecurityAnswer())){
        		log.info("User:"+ID+" securityAnswer updated by:"+getLoggedUserNameWithIP());
        	}
			user.setSecurityAnswer(e.getSecurityAnswer());
		}

		if (e.getUpdatedBy() != null) {
			if(!e.getUpdatedBy().equals(user.getUpdatedBy())){
        		log.info("User:"+ID+" UpdatedBy updated to:"+e.getUpdatedBy()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setUpdatedBy(e.getUpdatedBy());
		}

		if (e.getUserRestrictions() != null) {
			if(!e.getUserRestrictions().equals(user.getRestrictions())){
        		log.info("User:"+ID+" restrictions updated to:"+e.getUserRestrictions()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setRestrictions(e.getUserRestrictions());
		}

		if (e.getAdminComment() != null) {
			if(!e.getAdminComment().equals(user.getAdminComment())){
        		log.info("User:"+ID+" admin comment updated to:"+e.getAdminComment()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setAdminComment(e.getAdminComment());
		}

		if(e.getHomePhone()!=null){
			if(user.getRole()==CmFinoFIX.Role_Subscriber){
				if(user.getHomePhone() != null){
					user.setOldHomePhone(user.getHomePhone());
				}
				else{
					user.setOldHomePhone(e.getHomePhone());
				}
			}
			if(!e.getHomePhone().equals(user.getHomePhone())){
        		log.info("User:"+ID+" HomePhone updated to:"+e.getHomePhone()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setHomePhone(e.getHomePhone());
		}
		if(e.getWorkPhone()!=null){
			if(user.getRole()==CmFinoFIX.Role_Subscriber){
				if(user.getWorkPhone() != null){
					user.setOldWorkPhone(user.getWorkPhone());
				}
				else{
					user.setOldWorkPhone(e.getWorkPhone());
				}
			}
			if(!e.getWorkPhone().equals(user.getWorkPhone())){
        		log.info("User:"+ID+" WorkPhone updated to:"+e.getWorkPhone()+" by:"+getLoggedUserNameWithIP());
        	}
			user.setWorkPhone(e.getWorkPhone());
		}
	}

	private void updateMessage(User user, CGEntries e) {

		if (user.getUsername() != null) {
			e.setUsername(user.getUsername());
		}
		if (user.getID() != null) {
			e.setID(user.getID());
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

		if (user.getCreateTime() != null) {
			e.setCreateTime(user.getCreateTime());
		}

		if (user.getCreatedBy() != null) {
			e.setCreatedBy(user.getCreatedBy());
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

		if (user.getLastUpdateTime() != null) {
			e.setLastUpdateTime(user.getLastUpdateTime());
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

		if (user.getUpdatedBy() != null) {
			e.setUpdatedBy(user.getUpdatedBy());
		}

		e.setRole(user.getRole());
		e.setRoleText(userService.getUserRole(user.getRole()));

		e.setBranchCodeID(user.getBranchCodeID());
		if(user.getBranchCodeID()!=null){
			e.setBranchCodeText(userService.getUserBranchCode(user.getBranchCodeID().intValue()));	
		}
		
		
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
		if(user.getHomePhone()!=null){
			e.setHomePhone(user.getWorkPhone());
		}
		if(user.getWorkPhone()!=null){
			e.setWorkPhone(user.getWorkPhone());
		}
		e.setRecordVersion(user.getVersion());
	}

	private boolean modifiedOtherFields(CMJSUsers.CGEntries entry) {
		return (entry.isRemoteModifiedFirstName() || entry.isRemoteModifiedLastName() || entry.isRemoteModifiedEmail() || entry.isRemoteModifiedRole() || entry.isRemoteModifiedLanguage() || entry.isRemoteModifiedTimezone() || entry.isRemoteModifiedAdminComment() || entry.isRemoteModifiedUserStatus());
	}
}
