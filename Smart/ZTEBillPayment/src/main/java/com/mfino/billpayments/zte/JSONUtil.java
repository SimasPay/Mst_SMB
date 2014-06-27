package com.mfino.billpayments.zte;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;

public class JSONUtil {

    private static final Logger log = LoggerFactory.getLogger(JSONUtil.class);
    private static JSONObject jsonObject = new JSONObject();

    public static JSONObject loadErrorCodes(String filePath) {
		log.info("Begin:: JSONUtil.loadErrorCodes...");
		
		ApplicationContext ctx = new FileSystemXmlApplicationContext();
		Resource res = ctx.getResource(filePath);
		try {
			File file = res.getFile();
			if (file.exists()) {
				StringBuilder sb = new StringBuilder();
				BufferedReader br = new BufferedReader(new FileReader(file));
				String curLine = null;
				while((curLine=br.readLine()) != null){
					sb.append(curLine);
				}
				br.close();
				jsonObject =  new JSONObject(sb.toString());
			}
		} catch (FileNotFoundException e) {
			log.error("Error: FileNotFoundException while loading the json file: " + filePath);
		} catch (IOException e) {
			log.error("Error: IOException while loading the json file: " + filePath);
		} catch (JSONException e) {
			log.error("Error: JSONException while loading the json file: " + filePath);
		}
		return jsonObject;
    }
    
    /**
     * Returns the Operator message for the given response code and language from the JSON file
     * @param code
     * @param language
     * @return
     */
    public static String getOperatorDescription(String code, int language) {
    	String result = null;
    	try {
			JSONObject jsonCode = jsonObject.getJSONObject(code);
			if (jsonCode != null) {
				result = jsonCode.getString(language+"");
			}
			else {
				jsonCode = jsonObject.getJSONObject("default");
				if (jsonCode != null) {
					result = jsonCode.getString(language+"");
				}			
			}
		} catch (JSONException e) {
			log.error("Error while getting the description for the code: " + code);
		}
    	return result;
    }
}
