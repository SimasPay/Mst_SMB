/**
 * 
 */
package com.mfino.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.OfflineReportDAO;
import com.mfino.domain.OfflineReport;
import com.mfino.util.ConfigurationUtil;


/**
 * @author Maruthi
 *
 */
public class ReportService  {
	
	private String reportName;
	
	private String startDate;
	
	private String endDate;
	
	private static final String ENCODING = "UTF-8";
	
	public static final String	REPORT_URL	= ConfigurationUtil.getReportURL();

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	/**
	 * Send request to ReportScheduler to trigger report generation.
	 *
	 * @return HttpResponse
	 */
	
	public HttpResponse send(){
		HttpResponse httpResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_NAME, reportName));
		
		OfflineReportDAO offlineReportDAO = DAOFactory.getInstance().getOfflineReportDAO();
		OfflineReport report = offlineReportDAO.getByReportName(reportName);		
		qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_CLASSNAME, report.getReportClass()));
		if(StringUtils.isNotBlank(startDate)){
		qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_STARTDATE, startDate));
		}
		if(StringUtils.isNotBlank(endDate)){
		qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_ENDDATE, endDate));
		}
		HttpGet httpget = new HttpGet(REPORT_URL +"/AdminReport"+ "?" + URLEncodedUtils.format(qparams, ENCODING));
		try {
			log.info("Sending Request to generate Report:"+reportName + "to "+httpget.getURI() );
			httpResponse = httpclient.execute(httpget);
			} catch (ClientProtocolException protocolEx) {
			log.error("Error sending http request for report", protocolEx);
		} catch (IOException ioEx) {
			log.error("Error sending http request for report", ioEx);
		}
		return httpResponse;
	}
	
	

	public String getReportName() {
		return reportName;
	}


	public void setReportName(String reportName) {
		this.reportName = reportName;
	}


	public String getStartDate() {
		return startDate;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}


	public String getEndDate() {
		return endDate;
	}


	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
}
