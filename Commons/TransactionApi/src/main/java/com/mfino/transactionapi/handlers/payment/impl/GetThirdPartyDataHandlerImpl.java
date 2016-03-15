package com.mfino.transactionapi.handlers.payment.impl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.ChannelCode;
import com.mfino.fix.CmFinoFIX.CMGetThirdPartData;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;
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
			categoryRelatedJsonFile = new File("../mfino_conf/CategoryFiles", "errorJson.txt");
			xmlResult.setMessage(categoryRelatedJsonFile.getAbsolutePath());
		    return xmlResult;
		}
		
		categoryRelatedJsonFile = new File("../mfino_conf/CategoryFiles", getThirdPartyData.getDataCategory()+".txt");
		
		if(!categoryRelatedJsonFile.exists()){
			//handle file not found
			log.error("category related file not found : "+categoryRelatedJsonFile.getName());
			categoryRelatedJsonFile = new File("../mfino_conf/CategoryFiles", "errorJson.txt");
			xmlResult.setMessage(categoryRelatedJsonFile.getAbsolutePath());
			return xmlResult;
		}
		
		float systemVersion = 0;
		float enteredVersion = 0;
		try{
			String jsonString = FileUtils.readFileToString(categoryRelatedJsonFile);
			JSONObject jsonObject = new JSONObject(jsonString);			
			systemVersion = Float.parseFloat(jsonObject.getString("version"));
			enteredVersion = Float.parseFloat(getThirdPartyData.getVersion());
		}
		catch(Exception e){
			log.error("Invalid float value either in system parameter or entered parameter : "+getThirdPartyData.getDataCategory());
			categoryRelatedJsonFile = new File("../mfino_conf/CategoryFiles", "errorJson.txt");
			xmlResult.setMessage(categoryRelatedJsonFile.getAbsolutePath());
			return xmlResult;
		}
		//return default up to date statement on entered version >= system version
		if(Float.compare(systemVersion, enteredVersion)<=0){
			//return default json statement
			log.info("System is up to date");
			categoryRelatedJsonFile = new File("../mfino_conf/CategoryFiles", "defaultJson.txt");
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
}
