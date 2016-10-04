package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.query.GroupQuery;
import com.mfino.domain.Groups;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSGroup;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.GroupsProcessor;

/**
 * @author Sasi
 *
 */
@Service("GroupsProcessorImpl")
public class GroupsProcessorImpl extends BaseFixProcessor implements GroupsProcessor{

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSGroup realMsg = (CMJSGroup) msg;
		log.debug("GroupsProcessor :: process() BEGIN realMsg.getaction()="+realMsg.getaction());
		
		GroupDao dao = DAOFactory.getInstance().getGroupDao();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			GroupQuery query = new GroupQuery();
			int i=0;
			if (StringUtils.isNotBlank(realMsg.getNameSearch())) {
				query.setGroupName(realMsg.getNameSearch());
			}
			if (realMsg.getStartDateSearch() != null) {
				query.setCreateTimeGE(realMsg.getStartDateSearch());
			}
			if (realMsg.getEndDateSearch() != null) {
				query.setCreateTimeLT(realMsg.getEndDateSearch());
			}
			if (realMsg.getstart() != null) {
				query.setStart(realMsg.getstart());
			}
			if (realMsg.getlimit() != null) {
				query.setLimit(realMsg.getlimit());
			}
			if((StringUtils.isNotBlank(realMsg.getSystemGroupSearch())) && ("false".equals(realMsg.getSystemGroupSearch()))){
				query.setIncludeSystemGroups(false);
			}
			else{
				query.setIncludeSystemGroups(true);
			}
			
			List<Groups> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (Groups group : lst){
					CMJSGroup.CGEntries e = new CMJSGroup.CGEntries();
					updateMessage(group, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());			
		}
		else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSGroup.CGEntries[] entries = realMsg.getEntries();
			
			for(CMJSGroup.CGEntries e: entries) {
				Groups group = new Groups();
				group.setSystemgroup((short) 0);
				updateEntity(group, e);
        		try {
					dao.save(group);
				} catch (ConstraintViolationException ce) {
					return generateError(ce);
				}
				updateMessage(group, e);
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		}
		else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSGroup.CGEntries[] entries = realMsg.getEntries();
			
			for (CMJSGroup.CGEntries e: entries) {
				Groups ct = dao.getById(Long.valueOf(e.getID()));
        		if (!(e.getRecordVersion().equals(ct.getVersion()))) {
        			handleStaleDataException();
        		}
        		
        		updateEntity(ct, e);
        		try {
					dao.save(ct);
				} catch (ConstraintViolationException ce) {
					return generateError(ce);
				}
        		updateMessage(ct, e);
        	}
        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		}
		
		return realMsg;
	}

	private void updateEntity(Groups group, CMJSGroup.CGEntries e) {
		if (StringUtils.isNotBlank(e.getGroupName())) {
			group.setGroupname(e.getGroupName());
		}
		
		if (StringUtils.isNotBlank(e.getDescription())) {
			group.setDescription(e.getDescription());
		}
		
		if(e.getSystemGroup() != null){
			group.setSystemgroup((short) (e.getSystemGroup() ? 1 : 0));
		}
	}
	
	private void updateMessage(Groups group, CMJSGroup.CGEntries e) {
		e.setID(""+group.getId());
		e.setGroupName(group.getGroupname());
		e.setDescription(group.getDescription());
		e.setSystemGroup(group.getSystemgroup() != 0);
		e.setRecordVersion(((Long)group.getVersion()).intValue());
		e.setCreatedBy(group.getCreatedby());
		e.setCreateTime(group.getCreatetime());
		e.setUpdatedBy(group.getUpdatedby());
		e.setLastUpdateTime(group.getLastupdatetime());
	}
	
	private CFIXMsg generateError(ConstraintViolationException cve) {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
		newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
		String message = MessageText._("Groups with given name already exists, Please enter different name.");
		errorMsg.setErrorDescription(message);
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		newEntries[0].setErrorName(CmFinoFIX.CMJSGroup.FieldName_NameSearch);
		newEntries[0].setErrorDescription(message);
		log.warn(message, cve);
		return errorMsg;
	}
}
