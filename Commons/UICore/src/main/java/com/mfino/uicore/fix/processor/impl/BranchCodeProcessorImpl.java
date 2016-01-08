package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BranchCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.query.BranchCodeQuery;
import com.mfino.domain.BranchCodes;
import com.mfino.domain.Role;
import com.mfino.domain.User;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBranchCodes;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSRole;
import com.mfino.i18n.MessageText;
import com.mfino.service.MfinoServiceProviderService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.BranchCodeProcessor;
import com.mfino.uicore.fix.processor.RoleProcessor;

@Service("BranchCodeProcessorImpl")
public class BranchCodeProcessorImpl extends BaseFixProcessor implements BranchCodeProcessor{

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("MfinoServiceProviderServiceImpl")
	private MfinoServiceProviderService mfinoServiceProvidersService;

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception  {
//		System.out.println("==============branchcodes processor=================");
		CMJSBranchCodes realMsg = (CMJSBranchCodes) msg;

		BranchCodeQuery query=new BranchCodeQuery();
		BranchCodeDAO branchCodeDAO = DAOFactory.getInstance().getBranchCodeDAO();
		MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
//		  Integer mspid = Integer.valueOf(msg.getMspId());
//		  mFinoServiceProvider msp = mspDAO.getById(mspid);

		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {

		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg
				.getaction())) {
			log.info("BranchCodeProcessor :: select action");

//			User currentUser = userService.getCurrentUser(msp);
//			Role currentUserRole = roleDao.getById(currentUser.getRole());
			
			
			query.setStart(realMsg.getstart());
			query.setLimit(realMsg.getlimit());
//			query.setMsp(msp);

			List<BranchCodes> results = branchCodeDAO.get(query);
			System.out.println("resluts size : "+results.size());
			realMsg.allocateEntries(results.size());

			for (int i = 0; i < results.size(); i++) {
				BranchCodes role = results.get(i);
				CMJSBranchCodes.CGEntries entry = new CMJSBranchCodes.CGEntries();
				updateMessage(role, entry);
				realMsg.getEntries()[i] = entry;
			}

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());

			log.info("RoleProcessor :: query.getTotal() " + query.getTotal());

		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg
				.getaction())) {

		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg
				.getaction())) {

		}

		return realMsg;

	}
	
	private void updateEntity(Role role, CMJSRole.CGEntries e) {		
		if(e.getPriorityLevel() != null) {
			role.setPriorityLevel(e.getPriorityLevel());
		}
		if(e.getDisplayText() != null) {
			role.setDisplayText(e.getDisplayText());
		}
		role.setIsSystemUser((e.getIsSystemUser() == null) ? true : e.getIsSystemUser());
		
	}

	private void updateMessage(BranchCodes r, CMJSBranchCodes.CGEntries e) {
		e.setBranchName(r.getBranchName());
		e.setID(r.getID());
		e.setBranchCodeNo(r.getBranchCode());

	}

}
