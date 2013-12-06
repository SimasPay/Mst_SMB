package com.mfino.report.amlreports;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscribersAdditionalFieldsDAO;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.EnumTextService;
import com.mfino.util.ReportUtil;

public class KinInformationMissingAccountReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 5;
	private String HEADER_ROW = "# ,SubscriberMdn ,AccountID , AccountType , Status";
	private File report;
	private String reportName;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)	
	protected File run(Date start, Date end, ReportBaseData data) {
		
		report =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		DateFormat df = getDateFormat();
		String formatStr = getFormatString(NUM_COLUMNS);
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" StartTime:"+startTime);
		try{
			XLSReport xlsReport = new XLSReport(reportName, end);
			
			//adding logo
			try{
				xlsReport.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}
			
			xlsReport.addMergedRegion();
			
			xlsReport.addReportTitle(reportName);
		
		    xlsReport.addHeaderRow(HEADER_ROW);
		    
		SubscribersAdditionalFieldsDAO subscribersAdditionalFieldsDAO = DAOFactory.getInstance().getSubscribersAdditionalFieldsDAO();
		SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
		List<Long> subIds = subscribersAdditionalFieldsDAO.getSubcriberIDsOfKinInfoAvailable();
		List<Subscriber> subscribers = subscriberDAO.getSubscribersNotIn(subIds); 
		int seq = 1;
		for(Subscriber sub:subscribers){
			SubscriberMDN mdn = sub.getSubscriberMDNFromSubscriberID().iterator().next();
			String rowContent = String.format(formatStr, 
					seq,
					mdn.getMDN(),
					sub.getID(),
					sub.getKYCLevelByKYCLevel().getKYCLevelName(),
					enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, CmFinoFIX.Language_English, sub.getStatus())
					);
			xlsReport.addRowContent(rowContent);  
			seq++;
				}
		xlsReport.writeToFileStream(report, HEADER_ROW, reportName);
		}catch (Exception e) {
			log.error("Error in "+reportName+"",e);
		} 
		log.info("Processing "+reportName+" StartTime:"+startTime+" EndTime:"+df.format(new Date()));
		return report;		
	}
	
	

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected List<File> runAndGetMutlipleReports(Date start, Date end,
			ReportBaseData data) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}



	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return report.getName();
	}
	
}
