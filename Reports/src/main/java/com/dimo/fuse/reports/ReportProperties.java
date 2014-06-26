package com.dimo.fuse.reports;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dimo.fuse.reports.util.FileReaderUtil;
import com.dimo.fuse.reports.scheduler.ReportSchedulerProperties;

/**
 * 
 * @author Amar
 * 
 */
public class ReportProperties {

	private JSONObject reportProperties;
	private static JSONObject commonReportProperties;
	public static final String COMMON_REPORT_PROPERTIES_FILE_PATH = "common.report.properties.filepath";
	private static Logger log = LoggerFactory.getLogger("ReportProperties");

	static {
		readCommonReportProperties();
	}

	public static void readCommonReportProperties() {
		String commonReportPropsFilePath = ReportSchedulerProperties
				.getProperty(COMMON_REPORT_PROPERTIES_FILE_PATH);
		commonReportProperties = FileReaderUtil
				.readFileContAsJsonObj(commonReportPropsFilePath);
		if(StringUtils.isNotBlank(commonReportPropsFilePath)){
			log.info("common report properties file located @" + commonReportPropsFilePath);
		} else{
			log.error("common report properties file not found @" + commonReportPropsFilePath);
		}
	}

	public ReportProperties(String ReportPropertiesFilePath) {
		reportProperties = FileReaderUtil
				.readFileContAsJsonObj(ReportPropertiesFilePath);
	}

	public String getProperty(String propertyName) {
		String propertyValue = null;
		try {
			if (reportProperties != null) {
				propertyValue = reportProperties.getString(propertyName);
				return propertyValue;
			}
		} catch (JSONException e) {
			try {
				if (commonReportProperties != null) {
					propertyValue = commonReportProperties
							.getString(propertyName);
					return propertyValue;
				}
			} catch (JSONException ex) {
			}
		}
		return propertyValue;
	}

	public JSONArray getJSONArray(String propertyName) throws JSONException {
		JSONArray propertyValue = null;
		try {
			if (reportProperties != null) {
				propertyValue = reportProperties.getJSONArray(propertyName);
			}
		} catch (JSONException e) {
			if (commonReportProperties != null) {
				propertyValue = commonReportProperties
						.getJSONArray(propertyName);
			}
		}
		return propertyValue;
	}
}
