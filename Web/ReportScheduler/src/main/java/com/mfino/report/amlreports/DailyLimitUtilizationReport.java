package com.mfino.report.amlreports;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.ReportParametersService;
import com.mfino.util.ReportUtil;

public class DailyLimitUtilizationReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 12;
	private String HEADER_ROW = "# , Date, SubscriberMdn , LastName, FirstName, PartnerCode, PartnerName, WalletID, CurrentBalance, CurrentDailyExpenditure, DailyExpenditureLimit, LastTransactionTime ";
	private File report;
	private String reportName;
	
	
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
		    
		
		PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
		List<Pocket> lastUpdatedPockets = pocketDao.getLastUpdatedPockets(start,end); 
		List<Pocket> pockets = getLimitReachedPockets(lastUpdatedPockets);
		int seq = 1;
	
		for(Pocket pocket:pockets){
			SubscriberMDN mdn = pocket.getSubscriberMDNByMDNID();
			Subscriber sub = mdn.getSubscriber();
			Partner partner = sub.getPartnerFromSubscriberID()!=null&&(!sub.getPartnerFromSubscriberID().isEmpty())?sub.getPartnerFromSubscriberID().iterator().next():null;
			String rowContent = String.format(formatStr, 
					seq,
					df.format(pocket.getLastUpdateTime()),
					mdn.getMDN(),
					sub.getLastName(),
					sub.getFirstName(),
					partner!=null?partner.getPartnerCode():"",
					partner!=null?partner.getTradeName():"",
					pocket.getID(),
					pocket.getCurrentBalance(),		
					pocket.getCurrentDailyExpenditure(),
					pocket.getPocketTemplate().getMaxAmountPerDay(),
					pocket.getLastTransactionTime()!=null?df.format(pocket.getLastTransactionTime()):""
					);
			xlsReport.addRowContent(rowContent);  
			seq++;
		}
		xlsReport.writeToFileStream(report, HEADER_ROW, reportName);
		}catch (Exception e) {
			log.error("Error in "+reportName+":",e);
		} 
		log.info("Processing "+reportName+" StartTime:"+startTime+" EndTime:"+df.format(new Date()));
		return report;		
	}
	
	

	private List<Pocket> getLimitReachedPockets(List<Pocket> lastUpdatedPockets) {
		List<Pocket> pockets = new ArrayList<Pocket>();
		BigDecimal percent = ReportParametersService.getBigDecimal(ReportParameterKeys.DAILY_UTILIZATION_PERCENT).divide(new BigDecimal(100.00));
		for(Pocket pocket:lastUpdatedPockets){
			if(pocket.getCurrentDailyExpenditure().compareTo(pocket.getPocketTemplate().getMaxAmountPerDay().multiply(percent))>=0){
				pockets.add(pocket);
			}
		}
		return pockets;
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected List<File> runAndGetMutlipleReports(Date start, Date end,
			ReportBaseData data) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return report.getName();
	}
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}

	
}
