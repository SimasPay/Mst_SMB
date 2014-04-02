package com.dimo.fuse.reports.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dimo.fuse.reports.ReportParameters;
import com.dimo.fuse.reports.ReportPropertyConstants;
import com.dimo.fuse.reports.scheduler.OnlineReportGenerator;


/**
 * 
 * @author Amar
 *
 */
public class AdminReportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(this.getClass());

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.info("Post: Got Request:"+request);

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.info("Get: Got Request:"+request);
		processRequest(request, response);
	}


	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ReportParameters reportParams = new ReportParameters();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String reportName=request.getParameter(ReportPropertyConstants.ADMINAPP_REPORT_NAME);
		
		Date endDate = new Date();
		Date startDate = null;
		try {
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_START_DATE))) {
				startDate = dateFormat.parse(request.getParameter(ReportPropertyConstants.ADMINAPP_START_DATE));
			}
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_END_DATE))) {
				endDate = dateFormat.parse(request.getParameter(ReportPropertyConstants.ADMINAPP_END_DATE));
				if(startDate == null){
					Calendar calendar = Calendar.getInstance(); 
					calendar.setTime(endDate);
					calendar.add(Calendar.DATE, -1);
					startDate = calendar.getTime();
				}
			}
			reportParams.setStartTime(startDate);
			reportParams.setEndTime(endDate);
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_FROM_UPDATED_DATE))) {
				reportParams.setFromUpdatedDate(dateFormat.parse(request.getParameter(ReportPropertyConstants.ADMINAPP_FROM_UPDATED_DATE)));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_TO_UPDATED_DATE))) {
				reportParams.setToUpdatedDate(dateFormat.parse(request.getParameter(ReportPropertyConstants.ADMINAPP_TO_UPDATED_DATE)));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_SUBSCRIBER_STATUS_ID))) {
				reportParams.setSubscriberStatusId(request.getParameter(ReportPropertyConstants.ADMINAPP_SUBSCRIBER_STATUS_ID));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_SUBSCRIBER_STATUS_TEXT))) {
				reportParams.setSubscriberStatusText(request.getParameter(ReportPropertyConstants.ADMINAPP_SUBSCRIBER_STATUS_TEXT));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_POCKET_TEMPLATE_ID))) {
				reportParams.setPocketTemplateId(request.getParameter(ReportPropertyConstants.ADMINAPP_POCKET_TEMPLATE_ID));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_POCKET_TEMPLATE_DESCRIPTION))) {
				reportParams.setPocketTemplateDescription(request.getParameter(ReportPropertyConstants.ADMINAPP_POCKET_TEMPLATE_DESCRIPTION));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_SUBSCRIBER_RESTRICTIONS))) {
				reportParams.setSubscriberRestrictions(request.getParameter(ReportPropertyConstants.ADMINAPP_SUBSCRIBER_RESTRICTIONS));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_SUBSCRIBER_RESTRICTIONS_TEXT))) {
				reportParams.setSubscriberRestrictionsText(request.getParameter(ReportPropertyConstants.ADMINAPP_SUBSCRIBER_RESTRICTIONS_TEXT));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_SOURCE_MDN))) {
				reportParams.setSourceMdn(request.getParameter(ReportPropertyConstants.ADMINAPP_SOURCE_MDN));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_TRANSACTION_TYPE_ID))) {
				reportParams.setTransactionTypeId(request.getParameter(ReportPropertyConstants.ADMINAPP_TRANSACTION_TYPE_ID));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_TRANSACTION_TYPE_TEXT))) {
				reportParams.setTransactionTypeText(request.getParameter(ReportPropertyConstants.ADMINAPP_TRANSACTION_TYPE_TEXT));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_TRANSACTION_STATUS_ID))) {
				reportParams.setTransactionStatusId(request.getParameter(ReportPropertyConstants.ADMINAPP_TRANSACTION_STATUS_ID));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_TRANSACTION_STATUS_TEXT))) {
				reportParams.setTransactionStatusText(request.getParameter(ReportPropertyConstants.ADMINAPP_TRANSACTION_STATUS_TEXT));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_DESTINATION_POCKET_STATUS_ID))) {
				reportParams.setDestinationPocketStatusId(request.getParameter(ReportPropertyConstants.ADMINAPP_DESTINATION_POCKET_STATUS_ID));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_DESTINATION_POCKET_STATUS_TEXT))) {
				reportParams.setDestinationPocketStatusText(request.getParameter(ReportPropertyConstants.ADMINAPP_DESTINATION_POCKET_STATUS_TEXT));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_DEST_MDN))) {
				reportParams.setDestMdn(request.getParameter(ReportPropertyConstants.ADMINAPP_DEST_MDN));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_PARTNER_CODE))) {
				reportParams.setPartnerCode(request.getParameter(ReportPropertyConstants.ADMINAPP_PARTNER_CODE));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_BILLER_CODE))) {
				reportParams.setBillerCode(request.getParameter(ReportPropertyConstants.ADMINAPP_BILLER_CODE));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_PARTNER_TYPE_ID))) {
				reportParams.setPartnerTypeId(request.getParameter(ReportPropertyConstants.ADMINAPP_PARTNER_TYPE_ID));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_PARTNER_TYPE_TEXT))) {
				reportParams.setPartnerTypeText(request.getParameter(ReportPropertyConstants.ADMINAPP_PARTNER_TYPE_TEXT));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_SETTLEMENT_STATUS_ID))) {
				reportParams.setSettlementStatusId(request.getParameter(ReportPropertyConstants.ADMINAPP_SETTLEMENT_STATUS_ID));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_SETTLEMENT_STATUS_TEXT))) {
				reportParams.setSettlementStatusText(request.getParameter(ReportPropertyConstants.ADMINAPP_SETTLEMENT_STATUS_TEXT));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_CSR_USERNAME))) {
				reportParams.setCsrUserName(request.getParameter(ReportPropertyConstants.ADMINAPP_CSR_USERNAME));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_ID_NO))) {
				reportParams.setIdNumber(request.getParameter(ReportPropertyConstants.ADMINAPP_ID_NO));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_MDN))) {
				reportParams.setMdn(request.getParameter(ReportPropertyConstants.ADMINAPP_MDN));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_MERCHANT_ID))) {
				reportParams.setMerchantId(request.getParameter(ReportPropertyConstants.ADMINAPP_MERCHANT_ID));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_MERCHANT_ACCOUNT))) {
				reportParams.setMerchantAccount(request.getParameter(ReportPropertyConstants.ADMINAPP_MERCHANT_ACCOUNT));
			}
			
			if (StringUtils.isNotBlank(request.getParameter(ReportPropertyConstants.ADMINAPP_REFERENCE_NUMBER))) {
				reportParams.setReferenceNo(request.getParameter(ReportPropertyConstants.ADMINAPP_REFERENCE_NUMBER));
			}

			OnlineReportGenerator report = new OnlineReportGenerator();

			//report.generateReportsOnDemand(startDate,endDate,reportName);
			report.generateReportsOnDemand(reportName, reportParams);


		}catch (Exception e) {
			log.error("Error processing request:",e);
		}
	}

}
