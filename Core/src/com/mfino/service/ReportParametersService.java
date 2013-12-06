/**
 * 
 */
package com.mfino.service;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ReportParametersDao;
import com.mfino.domain.ReportParameters;

/**
 * @author Chaitanya
 *
 */
public class ReportParametersService {
		
	private static ReportParametersDao reportParameterDao = DAOFactory.getInstance().getReportParametersDao();
	
	private static Logger log = LoggerFactory.getLogger(ReportParametersService.class);
	
	//use these methods for other than adminapplication
	/**
	 * Returns the given property value from the Report Parameters table
	 * @param property
	 * @return
	 */
	public static String getUpdatedValue(String property){
		ReportParameters parameter=reportParameterDao.getReportParameterByName(property) ;
		if(parameter!=null){
		return parameter.getParameterValue();
		}
		return null;
	}	
	public static int getInteger(String property) {
		try {
			return Integer.parseInt(getUpdatedValue(property));
		} catch (NumberFormatException ex) {
			log.error("failed get property :"+property, ex);
			return -1;
		}
	}
	
	public static long getLong(String property) {
		try {
			return Long.parseLong(getUpdatedValue(property));
		} catch (NumberFormatException ex) {
			log.error("failed get property :"+property, ex);
			return -1;
		}
	}

	public static BigDecimal getBigDecimal(String property) {
		try {
			return new BigDecimal(getUpdatedValue(property));
		} catch (NumberFormatException ex) {
			log.error("failed get property :"+property, ex);
			return new BigDecimal("-1");
		}
	}

	public static int getColumnWidth(String reportName, String columnName) {
		String key = reportName+ReportParameterKeys.SEPARATOR+columnName;
		String value = getUpdatedValue(key);
		if (StringUtils.isEmpty(value)) {
			return 2000;
		} else {
			return Integer.valueOf(value);
			
		}
	}

	public static int getFontSize(String reportName){
		String key = reportName+ReportParameterKeys.SEPARATOR+ReportParameterKeys.FONTSIZE;
		String value = getUpdatedValue(key);
		if (StringUtils.isEmpty(value)) {
			return 7;
		} else {
			return Integer.valueOf(value);
		}
	}
	
	public static boolean isLandscape(String reportName){
		String key = reportName+ReportParameterKeys.SEPARATOR+ReportParameterKeys.ISLANDSCAPE;
		String value = getUpdatedValue(key);
		if(Boolean.valueOf(value)){
			return true;
		}else{
			return false;
		}
	}
}
