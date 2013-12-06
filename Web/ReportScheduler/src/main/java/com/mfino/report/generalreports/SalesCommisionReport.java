package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.TransactionAmountDistributionLogDAO;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionAmountDistributionLog;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.util.ReportUtil;

public class SalesCommisionReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 6;
	private String HEADER_ROW = "# ,PartnerTradeName,Channel Partner MDN,Servie Name ,Commision Earnerd ,Total No of Transactions";
	private TransactionAmountDistributionLogDAO tadlDao = DAOFactory.getInstance().getTransactionAmountDistributionLogDAO();
	private File reportFile;
	private String reportName;
	
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData basedata) {
		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.CSV_EXTENTION);
		String formatStr = getFormatString(NUM_COLUMNS);
		String starttime = getDateFormat().format(new Date());
		log.info("Processing "+reportName+" StartTime:"+starttime);
		try {
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
		    
			ReportBaseData data = basedata;
			if (data == null) {
				data = new ReportBaseData();
				data.intializeStaticData();
			} 
			data.getServiceTransactionLogs(start, end);
			Set<Long> sctlids = data.getSctlMap().keySet();
			List<TransactionAmountDistributionLog> tadl = tadlDao.getTransactionAmountDistributionLogBySCTLIds(sctlids);
			Map<Long,List<TransactionAmountDistributionLog>> partnerTADLMap = getPartnerTADLMap(tadl);
			Set<Long> partnerIds = partnerTADLMap.keySet();
			PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
			int seq = 1;
			for(Long partnerid:partnerIds){
				Partner partner = partnerDao.getById(partnerid);
				List<TransactionAmountDistributionLog> tdl = partnerTADLMap.get(partnerid);
				Map<Long, List<TransactionAmountDistributionLog>> serviceMap = getServiceTADLMap(tdl,data);
				
				Set<Long> serviceids = serviceMap.keySet();
				for(Long serviceid:serviceids){
					List<TransactionAmountDistributionLog> serviceTADL = serviceMap.get(serviceid);
					BigDecimal total = getTotalFund(serviceTADL);
					String rowContent = String.format(formatStr, 
							seq,
							partner.getTradeName(),
							partner.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next().getMDN(),
							ReportBaseData.serviceMap.get(serviceid).getDisplayName(),
							total,
							serviceTADL.size());
					xlsReport.addRowContent(rowContent);  
					seq++;
				}
				
			}
			
		xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);
		}catch (Exception e) {
			log.error("Error in "+reportName+":",e);
		} 
		log.info("Processing "+reportName+" StartTime:"+starttime+" EndTime"+getDateFormat().format(new Date()));
		return reportFile;
	}

	private BigDecimal getTotalFund(
			List<TransactionAmountDistributionLog> serviceTADL) {
		BigDecimal total =BigDecimal.ZERO;
		
		for(TransactionAmountDistributionLog tadl:serviceTADL){
			total = total.add(tadl.getShareAmount());
		}
		return total;
		
	}

	private Map<Long, List<TransactionAmountDistributionLog>> getServiceTADLMap(
			List<TransactionAmountDistributionLog> transactions,ReportBaseData data) {
		Map<Long,List<TransactionAmountDistributionLog>> serviceTADLMap =new HashMap<Long, List<TransactionAmountDistributionLog>>();
		for(TransactionAmountDistributionLog transaction:transactions){
			List<TransactionAmountDistributionLog> tadllist;
			ServiceChargeTransactionLog sctl = data.getSctlMap().get(transaction.getServiceChargeTransactionLogID());
			if(serviceTADLMap.containsKey(sctl.getServiceID())){
				tadllist=serviceTADLMap.get(sctl.getServiceID());
			}else{
				tadllist=new ArrayList<TransactionAmountDistributionLog>();
			}
			tadllist.add(transaction);
			serviceTADLMap.put(sctl.getServiceID(), tadllist);
		}
		
		return serviceTADLMap;
	}

	private Map<Long, List<TransactionAmountDistributionLog>> getPartnerTADLMap(
			List<TransactionAmountDistributionLog> tadl) {
		Map<Long,List<TransactionAmountDistributionLog>> partnerTADLMap =new HashMap<Long, List<TransactionAmountDistributionLog>>();
		for(TransactionAmountDistributionLog transaction:tadl){
			List<TransactionAmountDistributionLog> tadllist;
			if(partnerTADLMap.containsKey(transaction.getPartner().getID())){
				tadllist=partnerTADLMap.get(transaction.getPartner().getID());
			}else{
				tadllist=new ArrayList<TransactionAmountDistributionLog>();
			}
			tadllist.add(transaction);
			partnerTADLMap.put(transaction.getPartner().getID(), tadllist);
		}
		
		return partnerTADLMap;
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
		return reportFile.getName();
	}
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}


}
