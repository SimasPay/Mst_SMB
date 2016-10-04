package com.mfino.uicore.fix.processor.impl;


import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import oracle.net.aso.p;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.FundDefinitionDAO;
import com.mfino.dao.PurposeDAO;
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
		e.setID(fundDefinition.getId().longValue());
		if(fundDefinition.getPurposeid()!=null){
			e.setPurposeID(fundDefinition.getPurposeid().longValue());
			PurposeDAO purposeDAO = DAOFactory.getInstance().getPurposeDAO();
			Purpose purpose = purposeDAO.getById(fundDefinition.getPurposeid().longValue());
			e.setPurposeCode(purpose.getCode());
		}
		e.setFACLength(fundDefinition.getFaclength().intValue());
		e.setFACPrefix(fundDefinition.getFacprefix());
		if(fundDefinition.getExpirationType()!=null){
			e.setExpiryID(fundDefinition.getExpirationType().getId().longValue());
			e.setExpiryValue(fundDefinition.getExpirationType().getExpiryvalue().longValue());

		}
		e.setMaxFailAttemptsAllowed(fundDefinition.getMaxfailattemptsallowed().intValue());
		if(fundDefinition.getFundEventsByOnfundallocationtimeexpiry()!=null){
			e.setOnFundAllocationTimeExpiry(fundDefinition.getFundEventsByOnfundallocationtimeexpiry().getId().longValue());
			e.setOnFundAllocationTimeExpiryText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_FundEventType, null, 
					fundDefinition.getFundEventsByOnfundallocationtimeexpiry().getFundeventtype()));
		}
		if(fundDefinition.getFundEventsByOnfailedattemptsexceeded()!=null){
			e.setOnFailedAttemptsExceeded(fundDefinition.getFundEventsByOnfailedattemptsexceeded().getId().longValue());
			e.setOnFailedAttemptsExceededText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_FundEventType, null, 
					fundDefinition.getFundEventsByOnfailedattemptsexceeded().getFundeventtype()));
		}
		if(fundDefinition.getFundEventsByGenerationofotponfailure()!=null){
			e.setGenerationOfOTPOnFailure(fundDefinition.getFundEventsByGenerationofotponfailure().getId().longValue());
			e.setGenerationOfOTPOnFailureText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_FundEventType, null, 
					fundDefinition.getFundEventsByGenerationofotponfailure().getFundeventtype()));
		}
		e.setIsMultipleWithdrawalAllowed(fundDefinition.getIsmultiplewithdrawalallowed() != 0);
		e.setRecordVersion(((Long)fundDefinition.getVersion()).intValue());
		e.setCreatedBy(fundDefinition.getCreatedby());
		e.setCreateTime(fundDefinition.getCreatetime());
		e.setUpdatedBy(fundDefinition.getUpdatedby());
		e.setLastUpdateTime(fundDefinition.getLastupdatetime());
	}

	private void updateEntity(FundDefinition fundDefinition, CGEntries e) {
		if(e.getPurposeID()!=null){
			fundDefinition.setPurposeid(new BigDecimal(e.getPurposeID()));
		}
		if(e.getFACLength()!=null){
			fundDefinition.setFaclength(e.getFACLength().longValue());
		}
		if(StringUtils.isNotBlank(e.getFACPrefix())){
			fundDefinition.setFacprefix(e.getFACPrefix());
		}
		if(e.getExpiryID()!=null){
			ExpirationType expType = DAOFactory.getInstance().getExpirationTypeDAO().getById(e.getExpiryID());
			fundDefinition.setExpirationType(expType);
			
		}
		if(e.getMaxFailAttemptsAllowed()!=null){
			fundDefinition.setMaxfailattemptsallowed(e.getMaxFailAttemptsAllowed().longValue());
		}
		if(e.getOnFundAllocationTimeExpiry()!=null){
			FundEvents fundEvent = DAOFactory.getInstance().getFundEventsDAO().getById(e.getOnFundAllocationTimeExpiry());
			fundDefinition.setFundEventsByOnfundallocationtimeexpiry(fundEvent);
		}
		if(e.getOnFailedAttemptsExceeded()!=null){
			FundEvents fundEvent = DAOFactory.getInstance().getFundEventsDAO().getById(e.getOnFailedAttemptsExceeded());
			fundDefinition.setFundEventsByOnfailedattemptsexceeded(fundEvent);
		}
		if(e.getGenerationOfOTPOnFailure()!=null){
			FundEvents fundEvent = DAOFactory.getInstance().getFundEventsDAO().getById(e.getGenerationOfOTPOnFailure());
			fundDefinition.setFundEventsByGenerationofotponfailure(fundEvent);
		}
		if(e.getIsMultipleWithdrawalAllowed()!=null){
			fundDefinition.setIsmultiplewithdrawalallowed((short) (e.getIsMultipleWithdrawalAllowed() ? 1 : 0));
		}
		else{
			fundDefinition.setIsmultiplewithdrawalallowed((short) 0);
		}
		fundDefinition.setMspid(new BigDecimal(1));
	}

	private void validate(FundDefinition fundDefinition) throws Exception{
		if(fundDefinition.getPurposeid() == null){
			throw new Exception("Partner code can't be null.");
		}
		List<FundDefinition> entries = DAOFactory.getInstance().getFundDefinitionDAO().getAll();
		Iterator<FundDefinition> it = entries.iterator();
		while(it.hasNext())
		{
			FundDefinition existingFundDefinitions = it.next();
			
			if(fundDefinition.getPurposeid().equals(existingFundDefinitions.getPurposeid()))
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
