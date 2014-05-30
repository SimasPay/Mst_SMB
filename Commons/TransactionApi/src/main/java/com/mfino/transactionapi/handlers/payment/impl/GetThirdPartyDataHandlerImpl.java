package com.mfino.transactionapi.handlers.payment.impl;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.fix.CmFinoFIX.CMGetThirdPartData;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.handlers.payment.GetThirdPartyDataHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.RegistrationMediumXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
@Service("GetThirdPartyDataHandlerImpl")
public class GetThirdPartyDataHandlerImpl extends FIXMessageHandler implements GetThirdPartyDataHandler{
	
	private static Logger log = LoggerFactory.getLogger(GetThirdPartyDataHandlerImpl.class);
	CMGetThirdPartData getThirdPartyData;
	public static final Integer SUCCESS = 0;
	public static final Integer INVALID_CATEGORY = 1;
	public static final Integer INVALID_VERSION = 2;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public XMLResult handle(TransactionDetails transDetails) {
		
		getThirdPartyData = new CMGetThirdPartData();
		ChannelCode cc = transDetails.getCc();
		
		getThirdPartyData.setChannelCode(cc != null? cc.getChannelCode():null);
		getThirdPartyData.setDataCategory(transDetails.getCategory());
		getThirdPartyData.setVersion(transDetails.getVersion());
		
		log.info("Handling Get ThirdpartData webapi request with category:"+ getThirdPartyData.getDataCategory()+" and version: "+getThirdPartyData.getVersion());
		XMLResult xmlResult = new  RegistrationMediumXMLResult();
		File categoryRelatedJsonFile;
		Integer isValidData = validateData();
		if(isValidData!=SUCCESS){
			log.error("Invalid data entered for category or version");
			categoryRelatedJsonFile = new File("../webapps/webapi/WEB-INF", "errorJson.txt");
			xmlResult.setMessage(categoryRelatedJsonFile.getAbsolutePath());
		    return xmlResult;
		}
		boolean isSystemparameterExists = checkSystemParamterExists(getThirdPartyData.getDataCategory());

		if(!isSystemparameterExists){
			log.error("Invalid system parameter or no system paramter: "+getThirdPartyData.getDataCategory()+" has been created");
			categoryRelatedJsonFile = new File("../webapps/webapi/WEB-INF", "errorJson.txt");
			xmlResult.setMessage(categoryRelatedJsonFile.getAbsolutePath());
		    return xmlResult;
		}		
		float systemVersion = 0;
		float enteredVersion = 0;
		try{
			systemVersion = Float.parseFloat(systemParametersService.getString(getThirdPartyData.getDataCategory()));
			enteredVersion = Float.parseFloat(getThirdPartyData.getVersion());
		}
		catch(Exception e){
			log.error("Invalid float value either in system parameter or entered parameter : "+getThirdPartyData.getDataCategory());
			categoryRelatedJsonFile = new File("../webapps/webapi/WEB-INF", "errorJson.txt");
			xmlResult.setMessage(categoryRelatedJsonFile.getAbsolutePath());
			return xmlResult;
		}
		//return default up to date statement on entered version >= system version
		if(Float.compare(systemVersion, enteredVersion)<=0){
			//return default json statement
			log.info("System is up to date");
			categoryRelatedJsonFile = new File("../webapps/webapi/WEB-INF", "defaultJson.txt");
			xmlResult.setMessage(categoryRelatedJsonFile.getAbsolutePath());
		    return xmlResult;
		}

		categoryRelatedJsonFile = new File("../webapps/webapi/WEB-INF", getThirdPartyData.getDataCategory()+".txt");
		
		if(!categoryRelatedJsonFile.exists()){
			//handle file not found
			log.error("category related file not found : "+categoryRelatedJsonFile.getName());
			categoryRelatedJsonFile = new File("../webapps/webapi/WEB-INF", "errorJson.txt");
			xmlResult.setMessage(categoryRelatedJsonFile.getAbsolutePath());
			return xmlResult;
		}

		log.info("path:"+categoryRelatedJsonFile.getAbsolutePath());
		xmlResult.setMessage(categoryRelatedJsonFile.getAbsolutePath());
	    return xmlResult;
		
	}

	private Integer validateData() {
		if(StringUtils.isBlank(getThirdPartyData.getDataCategory())){
			return INVALID_CATEGORY;
		}
		else if(StringUtils.isBlank(getThirdPartyData.getVersion())){
			return INVALID_VERSION;
		}
		return SUCCESS;
	}

	private boolean checkSystemParamterExists(String dataCategory) {
		boolean isSystemParameterExists = false;
		if(SystemParameterKeys.CATEGORY_BANK_CODES.equals(dataCategory) || SystemParameterKeys.CATEGORY_PAYMENTS.equals(dataCategory) 
				|| SystemParameterKeys.CATEGORY_PURCHASE.equals(dataCategory) || SystemParameterKeys.CATEGORY_PREPAID.equals(dataCategory)
				|| SystemParameterKeys.CATEGORY_POSTPAID.equals(dataCategory) || SystemParameterKeys.CATEGORY_PREPAIDPLN.equals(dataCategory)
				|| SystemParameterKeys.CATEGORY_POSTPAIDPLN.equals(dataCategory) || SystemParameterKeys.CATEGORY_PREPAIDPHONE.equals(dataCategory)
				|| SystemParameterKeys.CATEGORY_POSTPAIDPHONE.equals(dataCategory) || SystemParameterKeys.CATEGORY_HELP.equals(dataCategory)
				|| SystemParameterKeys.CATEGORY_ADDRESSLIST.equals(dataCategory)){
			return true;
		}
		return isSystemParameterExists;

	}
}
