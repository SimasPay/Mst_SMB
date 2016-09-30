/**
 * 
 */
package com.mfino.service;

import java.io.IOException;
import java.util.ArrayList;
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
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSReport;
import com.mfino.service.impl.EnumTextServiceImpl;
import com.mfino.util.ConfigurationUtil;


/**
 * @author Maruthi
 *
 */
public class ReportService  {
	
	private String reportName;
	
	private String userName;	
	
	private String startDate;
	
	private String endDate;
	
	private CMJSReport reportParameters;
	
	private static final String ENCODING = "UTF-8";
	
	public static final String	REPORT_URL	= ConfigurationUtil.getReportURL();

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private EnumTextService entumTextService = new EnumTextServiceImpl();
	
	
	/**
	 * Send request to ReportScheduler to trigger report generation.
	 *
	 * @return HttpResponse
	 */
	
	public HttpResponse send(){
		HttpResponse httpResponse = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(REPORT_URL +"/AdminReport"+ "?" + URLEncodedUtils.format(getParameters(), ENCODING));
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
	
	private List<NameValuePair> getParameters()
	{
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_NAME, reportParameters.getReportName()));
		//qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_GENERATEDFILENAME, generatedReportName));
		qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_USERNAME, userName));
		
		if(StringUtils.isNotBlank(reportParameters.getReportStartDate())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_STARTDATE, reportParameters.getReportStartDate()));
		}
		if(StringUtils.isNotBlank(reportParameters.getReportEndDate())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_ENDDATE, reportParameters.getReportEndDate()));
		}
		if(StringUtils.isNotBlank(reportParameters.getFromUpdatedTime())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_FROMUPDATEDDATE, reportParameters.getFromUpdatedTime()));
		}
		if(StringUtils.isNotBlank(reportParameters.getToUpdatedTime())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_TOUPDATEDDATE, reportParameters.getToUpdatedTime()));
		}
		if(reportParameters.getSubscriberStatus() != null){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_SUBSCRIBERSTATUSID, reportParameters.getSubscriberStatus().toString()));
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_SUBSCRIBERSTATUSTEXT, entumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, CmFinoFIX.Language_English, reportParameters.getSubscriberStatus())));
		}
		if(reportParameters.getPocketTemplateID() != null){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_POCKETTEMPLATEID, reportParameters.getPocketTemplateID().toString()));
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_POCKETTEMPLATEDESCRIPTION, DAOFactory.getInstance().getPocketTemplateDao().getById(reportParameters.getPocketTemplateID()).getDescription()));
		}
		if(reportParameters.getSubscriberRestrictions() != null){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_SUBSCRIBERRESTRICTIONS, reportParameters.getSubscriberRestrictions().toString()));
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_SUBSCRIBERRESTRICTIONSTEXT, entumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberRestrictions, CmFinoFIX.Language_English, reportParameters.getSubscriberRestrictions())));
		}
		if(StringUtils.isNotBlank(reportParameters.getSourceMDN())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_SOURCEMDN, reportParameters.getSourceMDN()));
		}
		if(reportParameters.getTransactionTypeID() != null){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_TRANSACTIONTYPEID, reportParameters.getTransactionTypeID().toString()));
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_TRANSACTIONTYPETEXT, DAOFactory.getInstance().getTransactionTypeDAO().getById(reportParameters.getTransactionTypeID()).getTransactionname()));
		}
		if(StringUtils.isNotBlank(reportParameters.getTransactionStatus())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_TRANSACTIONSTATUSID, reportParameters.getTransactionStatus()));
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_TRANSACTIONSTATUSTEXT, entumTextService.getEnumTextValue(CmFinoFIX.TagID_SCTLStatus, CmFinoFIX.Language_English, reportParameters.getTransactionStatus())));
		}
		if(reportParameters.getDestinationPocketStatus() != null){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_DESTINATIONPOCKETSTATUSID, reportParameters.getDestinationPocketStatus().toString()));
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_DESTINATIONPOCKETSTATUSTEXT, entumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, CmFinoFIX.Language_English, reportParameters.getDestinationPocketStatus())));
		}
		if(StringUtils.isNotBlank(reportParameters.getDestMDN())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_DESTMDN, reportParameters.getDestMDN()));
		}
		if(StringUtils.isNotBlank(reportParameters.getPartnerCode())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_PARTNERCODE, reportParameters.getPartnerCode()));
		}
		if(StringUtils.isNotBlank(reportParameters.getBillerCode())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_BILLERCODE, reportParameters.getBillerCode()));
		}
		if(reportParameters.getPartnerType() != null){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_PARTNERTYPEID, reportParameters.getPartnerType().toString()));
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_PARTNERTYPETEXT, entumTextService.getEnumTextValue(CmFinoFIX.TagID_PartnerType, CmFinoFIX.Language_English, reportParameters.getPartnerType())));
		}
		if(reportParameters.getSettlementStatus() != null){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_SETTLEMENTSTATUSID, reportParameters.getSettlementStatus().toString()));
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_SETTLEMENTSTATUSTEXT, entumTextService.getEnumTextValue(CmFinoFIX.TagID_SettlementStatus, CmFinoFIX.Language_English, reportParameters.getSettlementStatus())));
		}
		if(StringUtils.isNotBlank(reportParameters.getCSRUserName())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_CSRUSERNAME, reportParameters.getCSRUserName()));
		}
		if(StringUtils.isNotBlank(reportParameters.getIDNumber())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_IDNO, reportParameters.getIDNumber()));
		}
		if(StringUtils.isNotBlank(reportParameters.getMDN())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_MDN, reportParameters.getMDN()));
		}
		if(reportParameters.getMerchantID() != null){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_MERCHANTID, reportParameters.getMerchantID().toString()));
		}
		if(StringUtils.isNotBlank(reportParameters.getMerchantAccount())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_MERCHANTACCOUNT, reportParameters.getMerchantAccount()));
		}
		if(reportParameters.getReferenceNumber() != null){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_REFERENCENUMBER, reportParameters.getReferenceNumber().toString()));
		}
		if(StringUtils.isNotBlank(reportParameters.getEmail())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_PARAMETER_EMAIL, reportParameters.getEmail().toString()));
		}
		if (StringUtils.isNotBlank(reportParameters.getSourcePartnerCode())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_SOURCE_PARTNER_CODE, reportParameters.getSourcePartnerCode()));
		}
		if (StringUtils.isNotBlank(reportParameters.getDestPartnerCode())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_DEST_PARTNER_CODE, reportParameters.getDestPartnerCode()));
		}
		if (StringUtils.isNotBlank(reportParameters.getBankRetrievalReferenceNumber())){
			qparams.add(new BasicNameValuePair(ReportParameterKeys.REPORT_BANK_RRN, reportParameters.getBankRetrievalReferenceNumber()));
		}
		return qparams;
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


	public void setReportParameters(CMJSReport realMsg) {
		reportParameters = realMsg;		
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}