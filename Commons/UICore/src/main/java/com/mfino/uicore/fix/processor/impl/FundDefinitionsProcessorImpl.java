package com.mfino.uicore.fix.processor.impl;


import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.FundDefinitionDAO;
import com.mfino.dao.query.FundDefinitionQuery;
import com.mfino.domain.ExpirationType;
import com.mfino.domain.FundDefinition;
import com.mfino.domain.FundEvents;
import com.mfino.domain.Purpose;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSFundDefinitions;
import com.mfino.fix.CmFinoFIX.CMJSFundDefinitions.CGEntries;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.FundDefinitionsProcessor;
import com.mfino.uicore.web.WebContextError;
@Service("FundDefinitionsProcessorImpl")
public class FundDefinitionsProcessorImpl extends BaseFixProcessor implements FundDefinitionsProcessor{
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

 	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSFundDefinitions realMsg = (CMJSFundDefinitions) msg;
		FundDefinitionDAO fundDefinitionDAO = DAOFactory.getInstance().getFundDefinitionDAO();
		
		if(CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())){
			CMJSFundDefinitions.CGEntries[] entries = realMsg.getEntries();
			
			for(CMJSFundDefinitions.CGEntries e : entries){
				if(e!=null){
					FundDefinition fundDefinition = fundDefinitionDAO.getById(e.getID());
					
					//Check for stale data
					if (!e.getRecordVersion().equals(fundDefinition.getVersion())) {
						handleStaleDataException();
					}
					updateEntity(fundDefinition, e);
					fundDefinitionDAO.save(fundDefinition);
					updateMessage(fundDefinition, e);
				}
			}
			
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		}
	else if(CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())){
			FundDefinitionQuery query = new FundDefinitionQuery();
			if(realMsg.getstart() != null){
				query.setStart(realMsg.getstart());
			}
			if(realMsg.getlimit() != null)
			{
				query.setLimit(realMsg.getlimit());
			}
			List<FundDefinition> results = fundDefinitionDAO.get(query);
			realMsg.allocateEntries(results.size());
			for (int i = 0; i < results.size(); i++) {
				FundDefinition fundDefinition = results.get(i);
				CMJSFundDefinitions.CGEntries entry = new CMJSFundDefinitions.CGEntries();
				updateMessage(fundDefinition, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			return realMsg;
		}
		else if(CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())){
			CMJSFundDefinitions.CGEntries[] entries = realMsg.getEntries();

			for (CMJSFundDefinitions.CGEntries e : entries) {
				if(e != null)
				{
					FundDefinition fundDefinition = new FundDefinition();
					updateEntity(fundDefinition, e);
					try{
						validate(fundDefinition);
					}
					catch(Exception ex){
						handleException(ex);
					}
					fundDefinitionDAO.save(fundDefinition);
					updateMessage(fundDefinition, e);
				}
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
			
		}
		else if(CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())){
			
		}
		return realMsg;
		
	}
	
	private void updateMessage(FundDefinition fundDefinition, CGEntries e) {
		e.setID(fundDefinition.getID());
		if(fundDefinition.getPurpose()!=null){
			e.setPurposeID(fundDefinition.getPurpose().getID());
			e.setPurposeCode(fundDefinition.getPurpose().getCode());	
		}
		e.setFACLength(fundDefinition.getFACLength());
		e.setFACPrefix(fundDefinition.getFACPrefix());
		if(fundDefinition.getExpirationTypeByExpiryID()!=null){
			e.setExpiryID(fundDefinition.getExpirationTypeByExpiryID().getID());
			e.setExpiryValue(fundDefinition.getExpirationTypeByExpiryID().getExpiryValue());

		}
		e.setMaxFailAttemptsAllowed(fundDefinition.getMaxFailAttemptsAllowed());
		if(fundDefinition.getFundEventsByOnFundAllocationTimeExpiry()!=null){
			e.setOnFundAllocationTimeExpiry(fundDefinition.getFundEventsByOnFundAllocationTimeExpiry().getID());
			e.setOnFundAllocationTimeExpiryText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_FundEventType, null, 
					fundDefinition.getFundEventsByOnFundAllocationTimeExpiry().getFundEventType()));
		}
		if(fundDefinition.getFundEventsByOnFailedAttemptsExceeded()!=null){
			e.setOnFailedAttemptsExceeded(fundDefinition.getFundEventsByOnFailedAttemptsExceeded().getID());
			e.setOnFailedAttemptsExceededText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_FundEventType, null, 
					fundDefinition.getFundEventsByOnFailedAttemptsExceeded().getFundEventType()));
		}
		if(fundDefinition.getFundEventsByGenerationOfOTPOnFailure()!=null){
			e.setGenerationOfOTPOnFailure(fundDefinition.getFundEventsByGenerationOfOTPOnFailure().getID());
			e.setGenerationOfOTPOnFailureText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_FundEventType, null, 
					fundDefinition.getFundEventsByGenerationOfOTPOnFailure().getFundEventType()));
		}
		e.setIsMultipleWithdrawalAllowed(fundDefinition.getIsMultipleWithdrawalAllowed());
		e.setRecordVersion(fundDefinition.getVersion());
		e.setCreatedBy(fundDefinition.getCreatedBy());
		e.setCreateTime(fundDefinition.getCreateTime());
		e.setUpdatedBy(fundDefinition.getUpdatedBy());
		e.setLastUpdateTime(fundDefinition.getLastUpdateTime());
	}

	private void updateEntity(FundDefinition fundDefinition, CGEntries e) {
		if(e.getPurposeID()!=null){
			Purpose purpose = DAOFactory.getInstance().getPurposeDAO().getById(e.getPurposeID());
			fundDefinition.setPurpose(purpose);
		}
		if(e.getFACLength()!=null){
			fundDefinition.setFACLength(e.getFACLength());
		}
		if(StringUtils.isNotBlank(e.getFACPrefix())){
			fundDefinition.setFACPrefix(e.getFACPrefix());
		}
		if(e.getExpiryID()!=null){
			ExpirationType expType = DAOFactory.getInstance().getExpirationTypeDAO().getById(e.getExpiryID());
			fundDefinition.setExpirationTypeByExpiryID(expType);
			
		}
		if(e.getMaxFailAttemptsAllowed()!=null){
			fundDefinition.setMaxFailAttemptsAllowed(e.getMaxFailAttemptsAllowed());
		}
		if(e.getOnFundAllocationTimeExpiry()!=null){
			FundEvents fundEvent = DAOFactory.getInstance().getFundEventsDAO().getById(e.getOnFundAllocationTimeExpiry());
			fundDefinition.setFundEventsByOnFundAllocationTimeExpiry(fundEvent);
		}
		if(e.getOnFailedAttemptsExceeded()!=null){
			FundEvents fundEvent = DAOFactory.getInstance().getFundEventsDAO().getById(e.getOnFailedAttemptsExceeded());
			fundDefinition.setFundEventsByOnFailedAttemptsExceeded(fundEvent);
		}
		if(e.getGenerationOfOTPOnFailure()!=null){
			FundEvents fundEvent = DAOFactory.getInstance().getFundEventsDAO().getById(e.getGenerationOfOTPOnFailure());
			fundDefinition.setFundEventsByGenerationOfOTPOnFailure(fundEvent);
		}
		if(e.getIsMultipleWithdrawalAllowed()!=null){
			fundDefinition.setIsMultipleWithdrawalAllowed(e.getIsMultipleWithdrawalAllowed());
		}
		else{
			fundDefinition.setIsMultipleWithdrawalAllowed(false);
		}
		
		fundDefinition.setmFinoServiceProviderByMSPID(DAOFactory.getInstance().getMfinoServiceProviderDAO().getById(1L));
	}

	private void validate(FundDefinition fundDefinition) throws Exception{
		if(fundDefinition.getPurpose() == null){
			throw new Exception("Partner code can't be null.");
		}
		List<FundDefinition> entries = DAOFactory.getInstance().getFundDefinitionDAO().getAll();
		Iterator<FundDefinition> it = entries.iterator();
		while(it.hasNext())
		{
			FundDefinition existingFundDefinitions = it.next();
			
			if(fundDefinition.getPurpose().equals(existingFundDefinitions.getPurpose()))
			{				
				throw new Exception("FundDefinition using the same partner code already exists");
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
