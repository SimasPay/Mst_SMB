package com.mfino.report.generalreports;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.domain.CommodityTransfer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.EnumTextService;
import com.mfino.service.ReportParametersService;
import com.mfino.util.ReportUtil;

public class MobileMoneyFailedTransactionReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 9;
	private String HEADER_ROW = "# ,SourceMDN,DestinationMdn , Date , TransactionType, TransactionAmount, RefID, FailureReason, BankStan";
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
		log.info("Processing "+reportName+" StartTime:"
				+ startTime);		
		
		try {
			//PrintWriter writer = new PrintWriter(new BufferedWriter(
			//		new FileWriter(report)));
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
			if (data == null) {
				data = new ReportBaseData();
				data.intializeStaticData();
				data.getCommodityTransactions(start, end);
			}
			if (data.getCtList() != null) {
				Map<String, List<CommodityTransfer>> subfailedTransaction = getFailedTransactionsBySubscriber(data);
				Set<String> mdns = subfailedTransaction.keySet();
				int limit = ReportParametersService
						.getInteger(ReportParameterKeys.FAIL_AMOUNT_LIMIT);
				int seq = 1;
				for (String mdn : mdns) {

					List<CommodityTransfer> ctList = subfailedTransaction
							.get(mdn);
					if (ctList.size() >= limit) {
						//writer.println(mdn);
//						xlsReport.addRowContent(mdn);
						for (CommodityTransfer ct : ctList) {
							String bankStan = ct.getBankSystemTraceAuditNumber();
							if(bankStan == null){
								bankStan = "";
							}
							String transactionType = ct.getSourceMessage();
							if(transactionType==null || transactionType.isEmpty())
							{
								transactionType = enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, CmFinoFIX.Language_English, ct.getUICategory());
							}
							String rowContent = String.format(
									formatStr,
									seq,
									mdn,
									ct.getDestMDN(),
									df.format(ct.getCreateTime()),
									transactionType,
									ct.getAmount().add(ct.getCharges()),
									ct.getID(),
									enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferFailureReason,CmFinoFIX.Language_English,ct.getTransferFailureReason()),
									bankStan);
							xlsReport.addRowContent(rowContent);  
							seq++;
						}
					}
					//writer.println();
				}
			}
			xlsReport.writeToFileStream(report, HEADER_ROW, reportName);
		} catch (Exception e) {
			log.error("Error in "+reportName+":", e);
		} 
		log.info("Processing "+reportName+" StartTime:"
				+ startTime + " EndTime:" + df.format(new Date()));

		return report;
	}
	
	
	private Map<String, List<CommodityTransfer>> getFailedTransactionsBySubscriber(
			ReportBaseData data) {
		List<CommodityTransfer> ctList = data.getCtList();
		List<CommodityTransfer> subctList;
		Map<String, List<CommodityTransfer>> subFailtransactions = new HashMap<String, List<CommodityTransfer>>();
		for (CommodityTransfer ct : ctList) {
			if (CmFinoFIX.TransferStatus_Failed.equals(ct.getTransferStatus())) {
				if (subFailtransactions.containsKey(ct.getSourceMDN())) {
					subctList = subFailtransactions.get(ct.getSourceMDN());
				} else {
					subctList = new ArrayList<CommodityTransfer>();
				}
				subctList.add(ct);
				subFailtransactions.put(ct.getSourceMDN(), subctList);
			}
		}
		return subFailtransactions;
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
		return report.getName();
	}
	
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}

}
