package com.mfino.report.amlreports;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
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

import com.mfino.constants.ReportParameterKeys;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.CommodityTransfer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.ReportParametersService;
import com.mfino.util.ReportUtil;

public class B2E2BTransactionReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 8;
	private String HEADER_ROW = "# ,Date,SubscriberMdn , AccountId , No.of B2E Transactions  , B2E TotalAmount,  No.of E2B Transactions  , E2B TotalAmount";
	private File report;
	private String reportName;

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {

		report =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		String startTime = getDateFormat().format(new Date());
		log.info("Processing "+reportName+" StartTime:"+ startTime);

		String formatStr = getFormatString(NUM_COLUMNS);
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

			CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
			List<CommodityTransfer> ctList = ctDao.getSelfB2E2BTransactions(start,end);
			Map<Integer,Map<String,List<CommodityTransfer>>> srcList = getTransactionMap(ctList);
			Map<String, List<CommodityTransfer>> b2emap = srcList.get(CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf);
			Map<String, List<CommodityTransfer>> e2bmap = srcList.get(CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf);
			Set<String> mdns = b2emap.keySet();
			List<CommodityTransfer> b2eList;
			DateFormat df = getDateFormat();
			List<CommodityTransfer> e2bList;
			int limit = ReportParametersService.getInteger(ReportParameterKeys.E2B2E_LIMIT);
			int seq = 1;
			String st=df.format(start)+"to"+df.format(end);
			for(String mdn:mdns){
				b2eList = b2emap.get(mdn);
				e2bList = e2bmap.get(mdn);
				if(b2eList.size()>limit
						&&e2bList!=null
						&&e2bList.size()>limit){
					BigDecimal[] counts =getCounts(b2eList,e2bList);
					CommodityTransfer ct = b2eList.get(0);
					String rowContent = String.format(formatStr, 
							seq,
							st,
							ct.getSourceMDN(),
							ct.getSubscriberBySourceSubscriberID().getID(),
							counts[0],
							counts[1],
							counts[2],
							counts[3]
							);
					xlsReport.addRowContent(rowContent);  
					seq++;
				}
			}
			xlsReport.writeToFileStream(report, HEADER_ROW, reportName);
		}catch (Exception e) {
			log.error("Error in "+reportName+"::",e);
		} 
		log.info("Processing "+reportName+" StartTime:"+startTime+"  EndTime"+getDateFormat().format(new Date()));
		return report;		
	}



	private BigDecimal[] getCounts(List<CommodityTransfer> b2eList, List<CommodityTransfer> e2bList) {
		BigDecimal[] counts={BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO};

		counts[0] = new BigDecimal(b2eList.size());
		for(CommodityTransfer ct:b2eList){
			counts[1] = counts[1].add(ct.getAmount());
		}
		counts[2] = new BigDecimal(e2bList.size());
		for(CommodityTransfer ct:e2bList){
			counts[3] = counts[1].add(ct.getAmount());
		}
		return counts;
	}



	private Map<Integer, Map<String, List<CommodityTransfer>>> getTransactionMap(
			List<CommodityTransfer> ctList) {
		Map<Integer, Map<String, List<CommodityTransfer>>> map = new HashMap<Integer, Map<String,List<CommodityTransfer>>>();
		Map<String, List<CommodityTransfer>> b2emap = new HashMap<String, List<CommodityTransfer>>();
		Map<String, List<CommodityTransfer>> e2bmap = new HashMap<String, List<CommodityTransfer>>();
		List<CommodityTransfer> srcctlist;
		for(CommodityTransfer ct:ctList){
			if(CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf.equals(ct.getUICategory())){
				if(b2emap.containsKey(ct.getSourceMDN())){
					srcctlist = b2emap.get(ct.getSourceMDN());
				}else{
					srcctlist = new ArrayList<CommodityTransfer>();
				}
				srcctlist.add(ct);
				b2emap.put(ct.getSourceMDN(), srcctlist);
			}else if(CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf.equals(ct.getUICategory())){
				if(e2bmap.containsKey(ct.getSourceMDN())){
					srcctlist = e2bmap.get(ct.getSourceMDN());
				}else{
					srcctlist = new ArrayList<CommodityTransfer>();
				}
				srcctlist.add(ct);
				e2bmap.put(ct.getSourceMDN(), srcctlist);	
			}
		}
		map.put(CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf, b2emap);
		map.put(CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf, e2bmap);
		return map;
	}



	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public List<File> runAndGetMutlipleReports(Date start, Date end,
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
