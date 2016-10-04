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
import com.mfino.dao.PurposeDAO;
import com.mfino.dao.query.PurposeQuery;
import com.mfino.domain.Purpose;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSPurpose;
import com.mfino.fix.CmFinoFIX.CMJSPurpose.CGEntries;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.PurposeProcessor;
import com.mfino.uicore.web.WebContextError;

@Service("PurposeProcessorImpl")
public class PurposeProcessorImpl extends BaseFixProcessor implements PurposeProcessor{
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSPurpose realMsg = (CMJSPurpose) msg;
		PurposeDAO purposeDAO = DAOFactory.getInstance().getPurposeDAO();
		
		if(CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())){
			
		}
		else if(CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())){
			PurposeQuery query = new PurposeQuery();
			if(realMsg.getstart() != null){
				query.setStart(realMsg.getstart());
			}
			if(realMsg.getlimit() != null)
			{
				query.setLimit(realMsg.getlimit());
			}
			List<Purpose> results = purposeDAO.get(query);
			realMsg.allocateEntries(results.size());
			for (int i = 0; i < results.size(); i++) {
				Purpose purpose = results.get(i);
				CMJSPurpose.CGEntries entry = new CMJSPurpose.CGEntries();
				updateMessage(purpose, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			return realMsg;
		}
		else if(CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())){
			CMJSPurpose.CGEntries[] entries = realMsg.getEntries();

			for (CMJSPurpose.CGEntries e : entries) {
				if(e != null)
				{
					Purpose purpose = new Purpose();
					updateEntity(purpose, e);
					try{
						validate(purpose);
					}
					catch(Exception ex){
						handleException(ex);
					}
					purposeDAO.save(purpose);
					updateMessage(purpose, e);
				}
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		}
		else if(CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())){
			
		}
		return realMsg;
		
		
	}

	private void updateEntity(Purpose purpose, CGEntries e) {
		if(StringUtils.isNotBlank(e.getPurposeCode())){
			purpose.setCode(e.getPurposeCode());
		}
		if(e.getCategory()!=null){
			purpose.setCategory(e.getCategory().longValue());
		}
		purpose.setMspid(new BigDecimal(1));
		
	}

	private void updateMessage(Purpose purpose, CGEntries e) {
		e.setID(purpose.getId().longValue());
		e.setPurposeCode(purpose.getCode());
		e.setCategory(purpose.getCategory().intValue());
		e.setCategoryText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Category, null, purpose.getCategory()));

	}
	
	private void validate(Purpose purpose) throws Exception{
		List<Purpose> entries = DAOFactory.getInstance().getPurposeDAO().getAll();
		Iterator<Purpose> it = entries.iterator();
		while(it.hasNext())
		{
			Purpose existingPurpose = it.next();
			
			if(purpose.getCode().equals(existingPurpose.getCode()))
			{				
				throw new Exception("Purpose using the same partner code already exists");
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
