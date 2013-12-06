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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.domain.CommodityTransfer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.generalreports.MobileMoneyRepeatedTransactionsReport;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.ReportParametersService;
import com.mfino.util.ReportUtil;

public class RepeatedTransactionsPerTransactionTypeReport extends
		OfflineReportBase {
	private static final Map<Integer, String> uiCategoryVsTransactionType = new HashMap<Integer, String>(
			20);
	private static final Map<String, String> transactionTypeVsSysParams = new HashMap<String, String>(
			10);

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 5;
	private String HEADER_ROW = ",SubscriberMdn , DestinationMdn ,TransactionCount , TransactionAmount";
	static {
		uiCategoryVsTransactionType.put(
				CmFinoFIX.TransactionUICategory_EMoney_CashIn, "CashIn");
		uiCategoryVsTransactionType.put(
				CmFinoFIX.TransactionUICategory_EMoney_CashOut, "CashOut");
		uiCategoryVsTransactionType.put(
				CmFinoFIX.TransactionUICategory_EMoney_EMoney_Trf, "P2P");
		uiCategoryVsTransactionType.put(
				CmFinoFIX.TransactionUICategory_EMoney_Purchase, "BillPay");
		uiCategoryVsTransactionType.put(
				CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf,
				"Bank2Emoney");
		uiCategoryVsTransactionType.put(
				CmFinoFIX.TransactionUICategory_Dompet_Money_Transfer,
				"Bank2Bank");
		uiCategoryVsTransactionType.put(
				CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf,
				"Emoney2Bank");
		transactionTypeVsSysParams.put("CashIn",
				ReportParameterKeys.CASHIN_LIMIT);
		transactionTypeVsSysParams.put("CashOut",
				ReportParameterKeys.CASHOUT_LIMIT);
		transactionTypeVsSysParams.put("P2P", ReportParameterKeys.P2P_LIMIT);
		transactionTypeVsSysParams.put("BillPay",
				ReportParameterKeys.BILLPAY_LIMIT);
		transactionTypeVsSysParams.put("Bank2Emoney",
				ReportParameterKeys.B2E_LIMIT);
		transactionTypeVsSysParams.put("Bank2Bank",
				ReportParameterKeys.B2B_LIMIT);
		transactionTypeVsSysParams.put("Emoney2Bank",
				ReportParameterKeys.E2B_LIMIT);
	}
	private File report;
	private String reportName;

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {
		report =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		DateFormat df = getDateFormat();
		String formatStr = getFormatString(NUM_COLUMNS);
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" StartTime:" + startTime);
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
		    
//			writer.println("DateRange: " + df.format(start) + " to "
//					+ df.format(end));
//			writer.println(HEADER_ROW);
		    
			if (data == null) {
				data = new ReportBaseData();
				data.intializeStaticData();
				data.getCommodityTransactions(start, end);
			}
			if (data.getCtList() != null) {
				Map<Integer, Map<String, Map<String, BigDecimal[]>>> duplicateTransactions = getDuplicateTransactions(data
						.getCtList());
				Set<Integer> transactionTypes = duplicateTransactions.keySet();

				int seq = 1;
				for (Integer type : transactionTypes) {
					if (!uiCategoryVsTransactionType.containsKey(type)) {
						continue;
					}
					String transactionType = (uiCategoryVsTransactionType.get(type));
//					xlsReport.addRowContent(rowContent);  
					String rowContent = "";
					BigDecimal limit = ReportParametersService
							.getBigDecimal(transactionTypeVsSysParams
									.get(uiCategoryVsTransactionType.get(type)));
					Map<String, Map<String, BigDecimal[]>> srcdestMap = duplicateTransactions
							.get(type);
					Set<String> srcMdns = srcdestMap.keySet();
					for (String mdn : srcMdns) {
						Map<String, BigDecimal[]> destMap = srcdestMap.get(mdn);
						Set<String> destMdns = destMap.keySet();
						for (String destmdn : destMdns) {
							if (mdn.equals(destmdn)) {
								continue;
							}
							BigDecimal[] counts = destMap.get(destmdn);
							if (counts[0].compareTo(limit) >= 0) {
								 rowContent = String.format(formatStr, transactionType,
										mdn, destmdn, counts[0].intValue(),
										counts[1]);
								xlsReport.addRowContent(rowContent);
								transactionType = " ";
								seq++;
							}
						}
					}
					xlsReport.addRowContent("");
				}
			}
			xlsReport.writeToFileStream(report, HEADER_ROW, reportName);
		} catch (Exception e) {
			log.error("Error in "+reportName+":",			e);
		}
		log.info("Processing "+reportName+" StartTime:"+startTime+" EndTime:"+df.format(new Date()));
		return report;
	}

	private Map<Integer, Map<String, Map<String, BigDecimal[]>>> getDuplicateTransactions(
			List<CommodityTransfer> ctList) {
		Map<Integer, Map<String, Map<String, BigDecimal[]>>> duplicateTransactions = new HashMap<Integer, Map<String, Map<String, BigDecimal[]>>>();
		Map<String, List<CommodityTransfer>> sourceMap;
		Map<String, Map<String, BigDecimal[]>> srcdestCounts;
		MobileMoneyRepeatedTransactionsReport mmrt = new MobileMoneyRepeatedTransactionsReport();
		Map<Integer, List<CommodityTransfer>> transactionTypeCts = getTransactionsByTransactionType(ctList);
		Set<Integer> transactionTypes = transactionTypeCts.keySet();
		for (Integer type : transactionTypes) {
			ctList = transactionTypeCts.get(type);
			sourceMap = mmrt.getTransactionsBySource(ctList);
			srcdestCounts = getSrcDestCounts(sourceMap);
			duplicateTransactions.put(type, srcdestCounts);
		}
		return duplicateTransactions;
	}

	private Map<String, Map<String, BigDecimal[]>> getSrcDestCounts(
			Map<String, List<CommodityTransfer>> sourceMap) {
		Map<String, Map<String, BigDecimal[]>> srcdestcounts = new HashMap<String, Map<String, BigDecimal[]>>();
		Set<String> sourceMdns = sourceMap.keySet();
		for (String mdn : sourceMdns) {
			List<CommodityTransfer> srcctList = sourceMap.get(mdn);
			Map<String, BigDecimal[]> destMap = new HashMap<String, BigDecimal[]>();
			for (CommodityTransfer ct : srcctList) {
				if (StringUtils.isNotBlank(ct.getDestMDN())) {
					BigDecimal[] counts = { new BigDecimal(1),
							ct.getAmount().add(ct.getCharges()) };
					if (destMap.containsKey(ct.getDestMDN())) {
						counts = destMap.get(ct.getDestMDN());
						counts[0] = counts[0].add(new BigDecimal(1));
						counts[1] = counts[1].add(ct.getAmount().add(
								ct.getCharges()));
					}
					destMap.put(ct.getDestMDN(), counts);
				}
			}
			srcdestcounts.put(mdn, destMap);
		}
		return srcdestcounts;
	}

	private Map<Integer, List<CommodityTransfer>> getTransactionsByTransactionType(
			List<CommodityTransfer> ctList) {
		Map<Integer, List<CommodityTransfer>> transactionTypeCts = new HashMap<Integer, List<CommodityTransfer>>();
		List<CommodityTransfer> transactionctList;
		for (CommodityTransfer ct : ctList) {			
			if (transactionTypeCts.containsKey(ct.getUICategory())) {
				transactionctList = transactionTypeCts.get(ct.getUICategory());
			} else {
				transactionctList = new ArrayList<CommodityTransfer>();
			}
			transactionctList.add(ct);
			transactionTypeCts.put(ct.getUICategory(), transactionctList);
		}
		return transactionTypeCts;
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
