package com.mfino.report.generalreports;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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

public class MobileMoneyRepeatedTransactionsReport extends
OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 9;
	private String HEADER_ROW = "# ,SubscriberMdn , Date , TransactionType, TransactionAmount, Charge, DestinationMdn, Status, BankStan";
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
		log.info("Processing "+reportName+" StartTime: "
				+ startTime);

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

		try {
			//PrintWriter writer = new PrintWriter(new BufferedWriter(
			//		new FileWriter(report)));
			//writer.println(HEADER_ROW);



			if (data == null) {
				data = new ReportBaseData();
				data.intializeStaticData();
				data.getCommodityTransactions(start, end);
			}
			if (data.getCtList() != null) {
				Map<String, Map<String, List<CommodityTransfer>>> duplicateTransactions = getDuplicateTransactions(data
						.getCtList());
				Set<String> sourceMdns = duplicateTransactions.keySet();

				int limit = ReportParametersService
						.getInteger(ReportParameterKeys.DUPLICATE_TRANSACTION_LIMIT);
				int seq = 1;
				for (String mdn : sourceMdns) {
					Map<String, List<CommodityTransfer>> destMap = duplicateTransactions
							.get(mdn);
					Set<String> destMdns = destMap.keySet();
					for (String destMdn : destMdns) {
						List<CommodityTransfer> ctList = destMap.get(destMdn);
						if (ctList.size() >= limit) {
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
										ct.getSourceMDN(),
										df.format(ct.getCreateTime()),
										transactionType,
										ct.getAmount(), ct.getCharges(),
										ct.getDestMDN(),
										enumTextService.getEnumTextValue(
												CmFinoFIX.TagID_TransferStatus,
												CmFinoFIX.Language_English,
												ct.getTransferStatus()),
										bankStan);
								xlsReport.addRowContent(rowContent);  
								seq++;
							}
						}
					}
				}
			}
			xlsReport.writeToFileStream(report, HEADER_ROW, reportName);
		} catch (Exception e) {
			log.error("Error in "+reportName+":", e);
		} 
		log.info("Processing "+reportName+" StartTime: "
				+ startTime + " EndTime:" + df.format(new Date()));

		return report;
	}

	
	public Map<String, Map<String, List<CommodityTransfer>>> getDuplicateTransactions(
			List<CommodityTransfer> ctList) {

		Map<String, Map<String, List<CommodityTransfer>>> duplicateTransactions = new HashMap<String, Map<String, List<CommodityTransfer>>>();
		Map<String, List<CommodityTransfer>> sourceMap = getTransactionsBySource(ctList);
		Set<String> sourceMdns = sourceMap.keySet();
		for (String mdn : sourceMdns) {
			List<CommodityTransfer> srcctList = sourceMap.get(mdn);
			Map<String, List<CommodityTransfer>> destMap = new HashMap<String, List<CommodityTransfer>>();
			List<CommodityTransfer> destctList;
			for (CommodityTransfer ct : srcctList) {
				if (StringUtils.isNotBlank(ct.getDestMDN())) {
					if (destMap.containsKey(ct.getDestMDN())) {
						destctList = destMap.get(ct.getDestMDN());
					} else {
						destctList = new ArrayList<CommodityTransfer>();
					}
					destctList.add(ct);
					destMap.put(ct.getDestMDN(), destctList);
				}
			}
			duplicateTransactions.put(mdn, destMap);
		}
		return duplicateTransactions;
	}

	public Map<String, List<CommodityTransfer>> getTransactionsBySource(
			List<CommodityTransfer> ctList) {
		Map<String, List<CommodityTransfer>> sourceMap = new HashMap<String, List<CommodityTransfer>>();
		List<CommodityTransfer> sourceCts;
		for (CommodityTransfer ct : ctList) {
			if (sourceMap.containsKey(ct.getSourceMDN())) {
				sourceCts = sourceMap.get(ct.getSourceMDN());
			} else {
				sourceCts = new ArrayList<CommodityTransfer>();
			}
			sourceCts.add(ct);
			sourceMap.put(ct.getSourceMDN(), sourceCts);
		}
		return sourceMap;
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
		return "MobileMoneyRepeatedTransactionsReport";
	}
	
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}


}
