package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ExpirationTypeDAO;
import com.mfino.dao.query.ExpirationTypeQuery;
import com.mfino.domain.ExpirationType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSExpirationType;
import com.mfino.fix.CmFinoFIX.CMJSExpirationType.CGEntries;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ExpirationTypeProcessor;
import com.mfino.uicore.web.WebContextError;

@Service("ExpirationTypeProcessorImpl")
public class ExpirationTypeProcessorImpl extends BaseFixProcessor implements ExpirationTypeProcessor{
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSExpirationType realMsg = (CMJSExpirationType) msg;
		ExpirationTypeDAO expirationTypeDAO = DAOFactory.getInstance().getExpirationTypeDAO();
		
		if(CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())){
			
		}
		else if(CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())){
			ExpirationTypeQuery query = new ExpirationTypeQuery();
			if(realMsg.getstart() != null){
				query.setStart(realMsg.getstart());
			}
			if(realMsg.getlimit() != null)
			{
				query.setLimit(realMsg.getlimit());
			}
			List<ExpirationType> results = expirationTypeDAO.get(query);
			realMsg.allocateEntries(results.size());
			for (int i = 0; i < results.size(); i++) {
				ExpirationType expirationType = results.get(i);
				CMJSExpirationType.CGEntries entry = new CMJSExpirationType.CGEntries();
				updateMessage(expirationType, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			return realMsg;
		}
		else if(CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())){
			CMJSExpirationType.CGEntries[] entries = realMsg.getEntries();

			for (CMJSExpirationType.CGEntries e : entries) {
				if(e != null)
				{
					ExpirationType expirationType = new ExpirationType();
					updateEntity(expirationType, e);
					try{
						validate(expirationType);
					}
					catch(Exception ex){
						handleException(ex);
					}
					expirationTypeDAO.save(expirationType);
					updateMessage(expirationType, e);
				}
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		}
		else if(CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())){
			
		}
		return realMsg;
		
		
	}

	private void updateEntity(ExpirationType expirationType, CGEntries e) {
		if(e.getExpiryMode()!=null){
			expirationType.setExpirymode(e.getExpiryMode().longValue());
		}
		if(e.getExpiryType()!=null){
			expirationType.setExpirytype(e.getExpiryType().longValue());
		}
		if(e.getExpiryValue()!=null){
			expirationType.setExpiryvalue(new BigDecimal(e.getExpiryValue()));
		}
		if(StringUtils.isNotBlank(e.getExpiryDescription())){
			expirationType.setExpirydescription(e.getExpiryDescription());
		}
		
		//expirationType.setmFinoServiceProviderByMSPID(DAOFactory.getInstance().getMfinoServiceProviderDAO().getById(1L));
		
	}

	private void updateMessage(ExpirationType expirationType, CGEntries e) {
		e.setID(expirationType.getId().longValue());
		e.setExpiryValue(expirationType.getExpiryvalue().longValue());
		e.setExpiryMode(expirationType.getExpirymode().intValue());
		e.setExpiryModeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ExpiryMode, null, expirationType.getExpirymode()));
		e.setExpiryType(expirationType.getExpirytype().intValue());
		e.setExpiryTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_ExpiryType, null, expirationType.getExpirytype()));
		e.setExpiryDescription(expirationType.getExpirydescription());
	}
	
	private void validate(ExpirationType expirationType) throws Exception{
		List<ExpirationType> entries = DAOFactory.getInstance().getExpirationTypeDAO().getAll();
		Iterator<ExpirationType> it = entries.iterator();
		while(it.hasNext())
		{
			ExpirationType existingExpirationType = it.next();
			
			if(expirationType.getExpiryvalue().equals(existingExpirationType.getExpiryvalue()))
			{				
				throw new Exception("ExpirationType with same expiry value already exists");
			}

		}
	}
	
	private CFIXMsg handleException(Exception e) throws Exception {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
		newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
		errorMsg.setErrorDescription(e.getMessage());
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		newEntries[0].setErrorDescription(e.getMessage());
		log.warn(e.getMessage(), e);
		WebContextError.addError(errorMsg);
		throw e;
	}

}
