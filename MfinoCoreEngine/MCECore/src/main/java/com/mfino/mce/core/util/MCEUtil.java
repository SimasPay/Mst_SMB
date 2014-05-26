package com.mfino.mce.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CMultiXBuffer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMFIXResponse;
import com.mfino.fix.CmFinoFIX.CMHSMPINValidationResponse;
import com.mfino.handlers.hsm.HSMHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.MfinoUtil;

public class MCEUtil 
{
	private static Log log = LogFactory.getLog(MCEUtil.class);
	
	// *FindbugsChange*
	// Previous -- public static String SUSPENSE_POCKET_ID_KEY = "suspense.pocket.id";
	//			   public static String CHARGES_POCKET_ID_KEY = "charges.pocket.id";
	//			   public static String GLOBAL_SVA_POCKET_ID_KEY = "global.sva.pocket.id";
	//			   public static String PLATFORM_DUMMY_MDN_KEY = "platform.dummy.mdn";
	public static final String SUSPENSE_POCKET_ID_KEY = "suspense.pocket.id";
	public static final String CHARGES_POCKET_ID_KEY = "charges.pocket.id";
	public static final String GLOBAL_SVA_POCKET_ID_KEY = "global.sva.pocket.id";
	public static final String PLATFORM_DUMMY_MDN_KEY = "platform.dummy.mdn";
	public static final String BREADCRUMB_ID ="breadcrumbId";
	
	public static String SUSPENSE_ACCOUNT_MDN = "98889885555";
	public static String CHARGES_ACCOUNT_MDN = "9889884444";
	public static Long SUSPENSE_SVA_POCKET_ID = 120L;
	public static Long CHARGES_SVA_POCKET_ID = 121L;
	public static String OMNIBUS_ACCOUNT_NAME = "OMNIBUS_ACCOUNT";
	public static String FAKE_PIN_FOR_OMB = "123456";
	public static Integer timeOut = 9000000;
	public static String SOURCE_CARD_NUMBER_OMNIBUS = "00000000000";
	public static Integer SOURCE_BANK_CODE_FOR_OMNIBUS = 8938459;
	

	
	// *FindbugsChange*
	// Previous -- public static final String GLOBAL_ACCOUNT_KEY = "global.account.pocket.id";
	//			   public static final String SERVICE_UNAVAILABLE = "9999";
	public static final String GLOBAL_ACCOUNT_KEY = "global.account.pocket.id";
	
	public static final String SERVICE_UNAVAILABLE = "9999";
	
	public static String SERVICE_TIME_OUT = "8888";
	
	public static String INTEGRATION_CODE = "INTEGRATION_CODE";
	
	public static String getToBeanURI(String serviceName, String methodName)
	{
		if(serviceName==null||serviceName.trim().equals(""))
			return null;
		if(methodName==null)
			methodName="processMesssage";
		return "bean:"+serviceName+"?method="+methodName;
	}
	
	public static boolean isNullOrEmpty(String str){
		if((null != str) && !(str.equals(""))) return false;
			
		return true;
	}
	
	public static String safeString(String str){
		if((null != str) && !(str.equals(""))) return str;
			
		return "";
	}
	
	public static boolean isNullOrEmpty(Collection collection){
		if((collection == null) || (collection.size() == 0)) return true; 
			
		return false;
	}
	
	public static boolean isNullorZero(Integer integerValue){
		if((null != integerValue) && (0 != integerValue.intValue())) return false;
		
		return true;
	}
	
	public static boolean isNullorZero(Long longValue){
		if((null != longValue) && (0 != longValue.longValue())) return false;
		return true;
	}

	public static String msgToData(CMBase base) {
		CMultiXBuffer buffer = new CMultiXBuffer();
		try{
			base.toFIX(buffer);
			String data =new String(buffer.DataPtr());
			return data;
		}catch (Exception error) {
			log.error("error in converting msg to data", error);
			return base.DumpFields();
		}
	}
	
	public Integer getMessageType(CMBase base){
		
		if(base instanceof CMFIXResponse){
			return CmFinoFIX.MsgType_FIXResponse;
		}
		
		
		return -1;
	}
	
	/**
	 * Returns the Current date time according the format given in mfino.properties file
	 * @return
	 */
	public static String getCurrentDateTime(){
    	DateFormat df = new SimpleDateFormat(ConfigurationUtil.getTransactionDateTimeFormat());
        TimeZone zone = ConfigurationUtil.getLocalTimeZone();
        df.setTimeZone(zone);
        return df.format(new Timestamp());
    }
	
	/**
	 * copy headers from sourceMap to destMap.presently copying only the breadcrumbId header using for identifying transactions
	 * @param sourceMap
	 * @param destMap
	 */
	public static void setMandatoryHeaders(Map<String,Object> sourceMap , Map<String,Object> destMap)
	{
	    destMap.put(BREADCRUMB_ID, sourceMap.get(BREADCRUMB_ID));  
	}
	
	/**
	 * Creates a new map and sets the mandatory headers from source and returns the map.
	 * @param sourceMap
	 * @return
	 */
	public static Map<String,Object> generateMandatoryHeaders(Map<String,Object> sourceMap)
	{
	   Map<String,Object> destMap = new HashMap<String, Object>();
	   setMandatoryHeaders(sourceMap,destMap);  
	   return destMap;
	 
	}
	/**
	 * Sets the transactionIdentifier into the given map with key as the camel breadcrumbId
	 * @param sourceMap
	 * @param trxnIdentifier
	 */
	public static void setBreadCrumbId(Map<String,Object> sourceMap,String trxnIdentifier){
		sourceMap.put(BREADCRUMB_ID,trxnIdentifier);
	}
}
