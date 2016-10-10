package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.RoleDAO;
import com.mfino.dao.query.RoleQuery;
import com.mfino.domain.Role;
import com.mfino.domain.MfinoUser;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSRole;
import com.mfino.service.UserService;
import com.mfino.i18n.MessageText;
import com.mfino.service.impl.UserServiceImpl;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.RoleProcessor;

@Service("RoleProcessorImpl")
public class RoleProcessorImpl extends BaseFixProcessor implements RoleProcessor{

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception  {
		CMJSRole realMsg = (CMJSRole) msg;

		RoleQuery query = new RoleQuery();
		RoleDAO roleDao = DAOFactory.getInstance().getRoleDAO();

		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			CMJSRole.CGEntries[] entries = realMsg.getEntries();			
			for (CMJSRole.CGEntries e: entries) {				
			if(e.getPriorityLevel() != null && e.getPriorityLevel().compareTo(userService.getCurrentUserPriorityLevel())<0) {
					CMJSError errorMsg = new CMJSError();
					errorMsg.setErrorDescription(MessageText._("Cannot update role with priorityLevel "+e.getPriorityLevel()));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					return errorMsg;
				}
				Role role = roleDao.getById(e.getID());
        		if (!(e.getRecordVersion().equals(role.getVersion()))) {
        			handleStaleDataException();
        		}        		
        		updateEntity(role, e);        		
        		roleDao.save(role);
        		updateMessage(role, e);
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg
				.getaction())) {
			log.info("RoleProcessor :: select action");

			MfinoUser currentUser = userService.getCurrentUser();
			Role currentUserRole = roleDao.getById(currentUser.getRole());
			if(realMsg.getRoleID() != null) {
				query.setId(realMsg.getRoleID());
			}
			query.setPriorityLevel(currentUserRole.getPrioritylevel().intValue());
			query.setStart(realMsg.getstart());
			query.setLimit(realMsg.getlimit());

			List<Role> results = roleDao.get(query);
			realMsg.allocateEntries(results.size());

			for (int i = 0; i < results.size(); i++) {
				Role role = results.get(i);
				CMJSRole.CGEntries entry = new CMJSRole.CGEntries();
				updateMessage(role, entry);
				realMsg.getEntries()[i] = entry;
			}

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());

			log.info("RoleProcessor :: query.getTotal() " + query.getTotal());

		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg
				.getaction())) {
			CMJSRole.CGEntries[] entries = realMsg.getEntries();			
			for (CMJSRole.CGEntries e: entries) {				
			if(e.getPriorityLevel() != null && e.getPriorityLevel().compareTo(userService.getCurrentUserPriorityLevel())<0) {
					CMJSError errorMsg = new CMJSError();
					errorMsg.setErrorDescription(MessageText._("Cannot add role with priorityLevel "+e.getPriorityLevel()));
					errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
					return errorMsg;
				}
				Role role = new Role();
				updateEntity(role, e);
				roleDao.save(role);
				updateMessage(role, e);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);

		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg
				.getaction())) {

		}

		return realMsg;

	}
	
	private void updateEntity(Role role, CMJSRole.CGEntries e) {		
		if(e.getPriorityLevel() != null) {
			role.setPrioritylevel(e.getPriorityLevel().shortValue());
		}
		if(e.getDisplayText() != null) {
			role.setDisplaytext(e.getDisplayText());
		}
		role.setIssystemuser((short) Boolean.compare((e.getIsSystemUser() == null) ? true : e.getIsSystemUser(), false));		
	}

	private void updateMessage(Role r, CMJSRole.CGEntries e) {
		e.setID(r.getId().longValue());
		e.setDisplayText(r.getDisplaytext());
		;
		e.setIsSystemUser(Boolean.valueOf(Short.valueOf(r.getIssystemuser()).toString()));
		e.setPriorityLevel(r.getPrioritylevel().intValue());
		
		Long tempVersionL = r.getVersion();
		Integer tempVersionLI = tempVersionL.intValue();
		
		e.setRecordVersion(tempVersionLI);
		e.setCreatedBy(r.getCreatedby());
		e.setCreateTime(r.getCreatetime());
		e.setUpdatedBy(r.getUpdatedby());
		e.setLastUpdateTime(r.getLastupdatetime());
	}

}
