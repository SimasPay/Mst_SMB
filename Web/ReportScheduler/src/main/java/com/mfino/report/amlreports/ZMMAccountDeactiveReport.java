package com.mfino.report.amlreports;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.ReportParametersService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;
import com.mfino.util.ReportUtil;

public class ZMMAccountDeactiveReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 5;
	private String HEADER_ROW = "# ,SubscriberMdn ,AccountID , LastTransactionTime , LasttransactionId";
	private File report;
	private String reportName;

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {
		report =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		DateFormat df = getDateFormat();
		String formatStr = getFormatString(NUM_COLUMNS);
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" StartTime:"+startTime);
		try{
			//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(report)));
			//writer.println(HEADER_ROW);
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

			SubscriberMDNDAO subDao = DAOFactory.getInstance().getSubscriberMdnDAO();
			Date expireDate = DateUtil.addDays(end, -ReportParametersService.getInteger(ReportParameterKeys.ACCOUNT_DEACTIVATETIME));
			List<SubscriberMDN> submdns = subDao.getDeactivatedMdns(expireDate); 
			int seq = 1;
			for(SubscriberMDN mdn:submdns){
				if(mdn.getLastTransactionTime()!=null||expireDate.after(mdn.getCreateTime())){
					Subscriber sub = mdn.getSubscriber();
					if(sub.getKYCLevelByKYCLevel()!=null && !sub.getKYCLevelByKYCLevel().getKYCLevel().equals(ConfigurationUtil.getBulkUploadSubscriberKYClevel())){
						String rowContent = String.format(formatStr, 
								seq,
								mdn.getMDN(),
								sub.getID(),
								mdn.getLastTransactionTime()!=null?df.format(mdn.getLastTransactionTime()):df.format(mdn.getCreateTime()),
										mdn.getLastTransactionID()!=null?mdn.getLastTransactionID():""
								);
						xlsReport.addRowContent(rowContent);
						seq++;
					}
				}
			}
			xlsReport.writeToFileStream(report, HEADER_ROW, reportName);
		}catch (Exception e) {
			log.error("Error in "+reportName+"::",e);
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
