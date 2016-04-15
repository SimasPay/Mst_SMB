package com.mfino.transactionapi.handlers.payment.impl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.handlers.FIXMessageHandler;
import com.mfino.result.XMLResult;
import com.mfino.transactionapi.handlers.payment.GetThirdPartyLocationHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.RegistrationXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;


@Service("GetThirdPartyLocationHandlerImpl")
public class GetThirdPartyLocationHandlerImpl extends FIXMessageHandler implements GetThirdPartyLocationHandler{
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	String province = "";
	String region = "";
	String district = "";
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public XMLResult handle(TransactionDetails transDetails) {
		
		XMLResult xmlResult = new  RegistrationXMLResult();

		File categoryRelatedJsonFile;
		province = transDetails.getState();
		region = transDetails.getRegionName();
		district = transDetails.getCity();
		//String parentPath = "../webapps/webapi/WEB-INF";
		
		String parentPath = "../mfino_conf/CategoryFiles";
		
		//String parentPath = "D://json//";
		String fileName = "";
		
		log.info("Handling Get ThirdpartLocation webapi request with Province: " + province
				+" and Region: "+region
				+" and District: "+district);
		
		if(province.length() > 0 && region.length() <= 0 && district.length() > 0){
			log.info("Invalid data entered");
			categoryRelatedJsonFile = new File(parentPath, "errorJson.txt");
			String str = getStringJsonObject(categoryRelatedJsonFile);
			xmlResult.setMessage(str);
		    return xmlResult;
		}
		
		if(province.length() > 0){
			fileName = "category.region";
			if(region.length() > 0){
				fileName = "category.district";
				if(null != district && district.length() > 0){
					fileName = "category.village";
				}
			}
		}else{
			log.info("Invalid data entered for Province");
			categoryRelatedJsonFile = new File(parentPath, "errorJson.txt");
			String str = getStringJsonObject(categoryRelatedJsonFile);
			xmlResult.setMessage(str);
		    return xmlResult;
		}
		
		categoryRelatedJsonFile = new File(parentPath, fileName+".txt");
		
		if(!categoryRelatedJsonFile.exists()){
			log.info("Location related file not found : "+categoryRelatedJsonFile.getName());
			categoryRelatedJsonFile = new File(parentPath, "errorJson.txt");
			String str = getStringJsonObject(categoryRelatedJsonFile);
			xmlResult.setMessage(str);
			return xmlResult;
		}
		
		try{
			String jsonString = getStringJsonObject(categoryRelatedJsonFile);
			JSONObject jsonObject = new JSONObject(jsonString);	
			String resObj = getThirdParyLocData(jsonObject);
			if(resObj.length() > 0){
				xmlResult.setMessage(resObj);
			}else{
				log.info("Data not found for the given values");
				categoryRelatedJsonFile = new File(parentPath, "errorJson.txt");
				String str = getStringJsonObject(categoryRelatedJsonFile);
				xmlResult.setMessage(str);
				return xmlResult;
			}
		}
		catch(Exception e){
			log.error("Error occured in getting location data : ",e);
			categoryRelatedJsonFile = new File(parentPath, "errorJson.txt");
			String str = getStringJsonObject(categoryRelatedJsonFile);
			xmlResult.setMessage(str);
			return xmlResult;
		}
		log.info("Request processed successfully from the file path:"+categoryRelatedJsonFile.getAbsolutePath());
	    return xmlResult;
	}

	public String getThirdParyLocData(JSONObject jo){
		log.info("Entered into getThirdParyLocData method");
		String resStr = "";
		try{
			JSONObject jIndonesia = (JSONObject)jo.getJSONObject("indonesia");
			JSONArray jProvinceArr=jIndonesia.getJSONArray("province");
		
			for(int i=0;i<jProvinceArr.length();i++)
			{
				resStr = "";
				JSONObject jProvinceJObj=(JSONObject)jProvinceArr.get(i);
				String pName = jProvinceJObj.getString("province_name");
				if(pName.equals(province)){
					i = jProvinceArr.length();
					JSONArray jRegionsArr=jProvinceJObj.getJSONArray("region");
					resStr = "{\"region\": "+jRegionsArr+"}";
					
					if(region.length() > 0){
						for(int j=0;j<jRegionsArr.length();j++){
							resStr = "";
							JSONObject jRegionJObj=(JSONObject)jRegionsArr.get(j);
							String rName = jRegionJObj.getString("region_name");
							if(rName.equals(region)){
								j = jRegionsArr.length();
								JSONArray jDistrictsArr=jRegionJObj.getJSONArray("district");
								resStr = "{\"district\": "+jDistrictsArr+"}";
								
								if(district.length() > 0){
									for(int k=0;k<jDistrictsArr.length();k++){
										resStr = "";
										JSONObject jDistJObj=(JSONObject)jDistrictsArr.get(k);
										String dName = jDistJObj.getString("district_name");
										if(dName.equals(district)){
											k = jDistrictsArr.length();
											JSONArray jVillages=jDistJObj.getJSONArray("village");
											resStr = "{\"village\": "+jVillages+"}";
										}
									}
								}
							}
						}
					}
				}
			}
			log.info("End of getThirdParyLocData method");
		}catch(Exception exc){
			log.error("Error occured in parsing the JSON file: ", exc);
		}
		return resStr;
	}
	
	private String getStringJsonObject(File fName){
		String strJObj = new String();
		try{
			strJObj = FileUtils.readFileToString(fName);
		}
		catch(Exception e){
			log.error("Error occured in getting location data : ",e);
		}
		return strJObj;
	}
}
