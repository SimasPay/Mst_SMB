package com.mfino.uicore.fix.processor.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SystemParametersDao;
import com.mfino.dao.query.SystemParametersQuery;
import com.mfino.domain.SystemParameters;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSSystemParameters;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SystemParametersProcessor;
import com.mfino.uicore.web.WebContextError;

@Service("SystemParametersProcessorImpl")
public class SystemParametersProcessorImpl extends BaseFixProcessor implements SystemParametersProcessor{


	//@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {

		CMJSSystemParameters realMsg = (CMJSSystemParameters) msg;
		SystemParametersDao systemParametersDao = DAOFactory.getInstance().getSystemParameterDao();

		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			CMJSSystemParameters.CGEntries[] entries = realMsg.getEntries();

			for (CMJSSystemParameters.CGEntries e : entries) {
				SystemParameters systemParameter = systemParametersDao.getById(e.getID());

				// Check for Stale Data
				if (!e.getRecordVersion().equals(systemParameter.getVersion())) {
					handleStaleDataException();
				}
				updateEntity(systemParameter, e);
				try {
					validate(systemParameter);
					systemParametersDao.save(systemParameter);
				} catch (Exception ex) {
					handleException(ex);
				}
				updateMessage(systemParameter, e);
			}

			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			SystemParametersQuery query = new SystemParametersQuery();

			if (StringUtils.isNotBlank(realMsg.getParameterNameSearch())) {
				query.setParameterName(realMsg.getParameterNameSearch());
			}
			if (StringUtils.isNotBlank(realMsg.getParameterValueSearch())) {
				query.setParemeterValue(realMsg.getParameterValueSearch());
			}
			if (StringUtils.isNotBlank(realMsg.getDescriptionSearch())) {
				query.setDescription(realMsg.getDescriptionSearch());
			}
			if(realMsg.getstart() != null){
				query.setStart(realMsg.getstart());
			}
			if(realMsg.getlimit() != null)
			{
				query.setLimit(realMsg.getlimit());
			}
			List<SystemParameters> results = systemParametersDao.get(query);
			realMsg.allocateEntries(results.size());
			for (int i = 0; i < results.size(); i++) {
				SystemParameters systemParameter = results.get(i);
				CMJSSystemParameters.CGEntries entry = new CMJSSystemParameters.CGEntries();
				updateMessage(systemParameter, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			return realMsg;
		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			CMJSSystemParameters.CGEntries[] entries = realMsg.getEntries();

			for (CMJSSystemParameters.CGEntries e : entries) {
				SystemParameters systemParameter = new SystemParameters();
				updateEntity(systemParameter, e);
				try {
					validate(systemParameter);
					systemParametersDao.save(systemParameter);
				} catch (Exception ex) {
					handleException(ex);
				}
				updateMessage(systemParameter, e);
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
			CMJSSystemParameters.CGEntries[] entries = realMsg.getEntries();			
			for (CMJSSystemParameters.CGEntries e : entries) {
				log.info("Deleted the " + e.getParameterName() + " with value " + e.getParameterValue() + " by user:"+getLoggedUserNameWithIP());
				systemParametersDao.deleteById(e.getID());
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		}
		return realMsg;

	}


	private void validate(SystemParameters s) throws Exception 
	{
		List<SystemParameters> entries = DAOFactory.getInstance().getSystemParameterDao().getAll();
		Iterator<SystemParameters> it = entries.iterator();
		while(it.hasNext())
		{
			SystemParameters existingSystemParameter = it.next();
			// *FindbugsChange*
        	// Previous -- if(s.getParameterName().equals(existingSystemParameter.getParameterName())  && s.getID() != existingSystemParameter.getID())
			if(s.getParameterName().equals(existingSystemParameter.getParameterName())  && (s.getID()!=null && !(s.getID().equals(existingSystemParameter.getID()))))
			{				
				throw new Exception("System Parameter already exists");
			}
		}
	}

	private void updateEntity(SystemParameters systemParameters, CMJSSystemParameters.CGEntries e) {
		if (StringUtils.isNotBlank(e.getParameterName())) {
			systemParameters.setParameterName(e.getParameterName());
		}

		if (StringUtils.isNotBlank(e.getParameterValue())) {
			log.info("Updated the value for " + systemParameters.getParameterName() + " from " + systemParameters.getParameterValue() + " to " + 
					e.getParameterValue() + " by user:"+getLoggedUserNameWithIP());
			systemParameters.setParameterValue(e.getParameterValue());
		}

		if (StringUtils.isNotBlank(e.getDescription())) {
			systemParameters.setDescription(e.getDescription());
		}

	}

	private void updateMessage(SystemParameters systemParameters, CMJSSystemParameters.CGEntries e) {
		e.setID(systemParameters.getID());
		e.setParameterName(systemParameters.getParameterName());
		e.setDescription(systemParameters.getDescription());
		e.setParameterValue(systemParameters.getParameterValue());
		e.setRecordVersion(systemParameters.getVersion());
		e.setCreatedBy(systemParameters.getCreatedBy());
		e.setCreateTime(systemParameters.getCreateTime());
		e.setUpdatedBy(systemParameters.getUpdatedBy());
		e.setLastUpdateTime(systemParameters.getLastUpdateTime());
	}

	private CFIXMsg handleException(Exception e) throws Exception {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
		newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
		String message = MessageText._("System Parameter with given name already exists, Please enter different name.");
		errorMsg.setErrorDescription(message);
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		newEntries[0].setErrorName(CmFinoFIX.CRSystemParameters.FieldName_ParameterName);
		newEntries[0].setErrorDescription(message);
		log.warn(message, e);
		WebContextError.addError(errorMsg);
		throw e;
	}
}
