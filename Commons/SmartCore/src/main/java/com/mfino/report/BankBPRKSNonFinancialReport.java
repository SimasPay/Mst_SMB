/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.ActivitiesLogDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.ActivitiesLogQuery;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 * 
 * @author Maruthi
 */
public class BankBPRKSNonFinancialReport extends OfflineReportBase {

	private static final int NUM_COLUMNS = 17;
	private static final String HEADER_ROW = "#, Reference No, Transaction Type,  Start Date & Time, "
			+ "Completion Date & Time, Status, Bank Response Code,  Source MDN, Source Pocket ID,"
			+ "  Source Pocket Status, Source Pocket Template,Card Number, Channel, Source Company Code, "
			+ "Error Code, Error Description, Bank Code";
			

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private ActivitiesLogDAO activitiesDAO = DAOFactory.getInstance().getActivitiesLogDAO();
	private PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
	private SubscriberMDNDAO subMdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();

	private HashMap<Integer, String> channelcodes ;

	@Override
	public String getReportName() {
		return "BankBPRKSNonFinancialReport";
	}

	@Override
	public File run(Date start, Date end) {
		return run(start, end, null);
	}

	@Override
	public File run(Date start, Date end, Long companyID) {
		File reportFile = null;

		try {
			HibernateUtil.getCurrentSession().beginTransaction();

			ActivitiesLogQuery query = new ActivitiesLogQuery();
			Company company = null;
			if (companyID != null) {
				CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
				company = companyDao.getById(companyID);
				if (company != null) {
					query.setCompany(company);
				}
				reportFile = getReportFilePath(company);
			} else {
				reportFile = getReportFilePath();
			}
			if (null != start) {
				query.setLastUpdateTimeGE(start);
			}
			if (null != end) {
				query.setLastUpdateTimeLT(end);
			}
			query.setBankRoutingCode(ConfigurationUtil.getBPRKSRoutingCode());
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
			int seq = 1;

			writer.println(HEADER_ROW);
			int firstResult = 0;
			int batchSize = ConfigurationUtil.getActivityReportBatchSize();

			channelcodes = ChannelCodeService.getChannelCodeMap();
			int count = activitiesDAO.getActivityCountBetween(start, end, company, ConfigurationUtil.getBPRKSRoutingCode());
			while (firstResult < count) {
				query.setStart(firstResult);
				query.setLimit(batchSize);
				query.setIDOrdered(true);
				List<ActivitiesLog> results = activitiesDAO.get(query);
				seq = reportForBatch(results, writer, seq);
				firstResult += results.size();
				HibernateUtil.getCurrentSession().clear();
				results.clear();
			}
				writer.close();
			HibernateUtil.getCurrentTransaction().rollback();
			return reportFile;
		} catch (Throwable t) {
			HibernateUtil.getCurrentTransaction().rollback();
			log.error("Error in BankBPRKSNonFinancial Report", t);
		}
		return reportFile;
	}

	private int reportForBatch(List<ActivitiesLog> results, PrintWriter writer,int seq) {
		DateFormat df = getDateFormat();
		String formatStr = getFormatString(NUM_COLUMNS);
		for (ActivitiesLog activity : results) {

			Long transID = activity.getParentTransactionID();
			String uiCategory = StringUtils.EMPTY;
			String status = StringUtils.EMPTY;
			String sourcePocketTemplate = StringUtils.EMPTY;
			Pocket sourcePocket = null;
			Long sourcePocketId = null;
			SubscriberMDN subscriberMDN = null;
			String channel = null;
			
			if (activity.getTransferID() != null) {
				continue;
			}
			if(activity.getMsgType().equals(CmFinoFIX.MsgType_BankAccountToBankAccount)
					||activity.getMsgType().equals(CmFinoFIX.MsgType_BankAccountToBankAccountConfirmation)
					||activity.getMsgType().equals(CmFinoFIX.MsgType_BankAccountTopup)
					||activity.getMsgType().equals(CmFinoFIX.MsgType_BankAccountTopupReversalToBank)
					||activity.getMsgType().equals(CmFinoFIX.MsgType_BankAccountTopupToBank)){
				continue;
			}
			channel = channelcodes.get(activity.getSourceApplication());
			sourcePocketId = activity.getSourcePocketID();
			if (sourcePocketId != null) {
				sourcePocket = pocketDAO.getById(sourcePocketId);
				sourcePocketTemplate = sourcePocket.getPocketTemplate().getDescription();
				
			}
			Boolean isSuccessful = activity.getIsSuccessful();
			if (isSuccessful == null) {
				status = "Unknown";
			} else {
				status = (isSuccessful) ? "Successful" : "Failed";
			}
			uiCategory = OfflineReportUtil.getUICategory(activity.getMsgType(),activity.getSourceMDN(), "", activity.getServletPath(),activity.getCommodity(), activity.getSourcePocketType());
			if (StringUtils.isEmpty(uiCategory)) {
				uiCategory = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_MsgType, null, activity.getMsgType());
			}
			if (activity.getSourceMDN() != null) {
				subscriberMDN = subMdnDAO.getByMDN(activity.getSourceMDN());
				if (subscriberMDN != null) {
				
				}
			}

			try {
				writer.println(String.format(formatStr,
										seq,
										transID,
										uiCategory,
										df.format(activity.getCreateTime()),
										df.format(activity.getLastUpdateTime()),
										status,
										activity.getISO8583_ResponseCode() != null ? activity.getISO8583_ResponseCode() : StringUtils.EMPTY,
										activity.getSourceMDN() != null ? activity.getSourceMDN() : StringUtils.EMPTY,
										activity.getSourcePocketID() != null ? activity.getSourcePocketID() : StringUtils.EMPTY,
										sourcePocket != null ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus,null,sourcePocket.getStatus()) : StringUtils.EMPTY,
										sourcePocketTemplate != null ? sourcePocketTemplate	: StringUtils.EMPTY,
										sourcePocket != null ? sourcePocket.getCardPAN() : StringUtils.EMPTY,
										channel,
										//activity.getISO8583_RetrievalReferenceNumber() != null ? activity.getISO8583_RetrievalReferenceNumber() : StringUtils.EMPTY,
										subscriberMDN != null ? subscriberMDN.getSubscriber().getCompany().getCompanyCode() : StringUtils.EMPTY,
										activity.getNotificationCode() != null ? activity.getNotificationCode() : StringUtils.EMPTY,
										activity.getNotificationCode() != null ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_NotificationCode,CmFinoFIX.Language_English,activity.getNotificationCode()) : StringUtils.EMPTY,
										activity.getISO8583_AcquiringInstIdCode()));
			} catch (Exception e) {
				log.error("Exception in BPRKSNonFinancial Report ", e);
			}

			seq++;
		}
		return seq;
	}

	
	/* public static void main(String args[])
	 { 
	 BankBPRKSNonFinancialReport sReport = new BankBPRKSNonFinancialReport();
	 Date s = new Date();
	 Date e = DateUtil.addDays(s, -100);
	 sReport.run(e, s);
	 }*/
	 
}
