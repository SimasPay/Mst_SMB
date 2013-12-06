package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.LedgerDAO;
import com.mfino.dao.query.LedgerQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Ledger;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.EnumTextService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ReportUtil;

/**
 * 
 * @author Amar
 *
 */
public class FloatAccountReport extends OfflineReportBase {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 15;
	private Long servicePartnerCollectorPocketID;
	private String HEADER_ROW = "# ," +
			"Commodity Transfer ID," +
			"Create Time ," +
			"SourcePocketID," +
			"DestPocketID," +
			"TransactionType," +
			"SourceMDN," +
			"DestMDN," +
			"Credit," +
			"Debit," +
			"Opening Balance," +
			"Closing Balance," +
			"TransferStatus," +
			"Commodity," +
			"SourceApplication";
	private File reportFile;
	DateFormat df;
	private String reportName;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData basedata) {

		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		df = getDateFormat();
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" StartTime:"+ startTime);
		try {
			//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
			//writer.println(HEADER_ROW);
			List<Ledger> ledgerList = getTransactionsFromLedger(start, end);
			int seq=1;
			
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
			
			if(ledgerList!=null){					
				seq = writeToFile(ledgerList,seq,xlsReport);
			}
			//writer.close();
			xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);
			
		} catch (Exception e) {
			log.error("Error in "+reportName+":", e);
		} 
		log.info("Processing "+reportName+" StartTime:"+startTime+"  EndTime"+df.format(new Date()));
		return reportFile;
	}

	private int writeToFile(List<Ledger> ledgerList, int seq, XLSReport xlsReport) {

		for(Ledger ledger:ledgerList){
			String date=df.format(ledger.getCreateTime());
			String formatStr = getFormatString(NUM_COLUMNS); 
			CommodityTransfer ct = DAOFactory.getInstance().getCommodityTransferDAO().getById(ledger.getCommodityTransferID());
			if(ct != null)
			{
				String transactionType = enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, CmFinoFIX.Language_English, ct.getUICategory());
				String status = enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, CmFinoFIX.Language_English, ct.getTransferStatus());
				String commodity = enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, CmFinoFIX.Language_English, ct.getCommodity());
				String sourceApplication = enumTextService.getEnumTextValue(CmFinoFIX.TagID_SourceApplication, CmFinoFIX.Language_English, ct.getSourceApplication());

				BigDecimal openingBalance;
				BigDecimal closingBalance;
				BigDecimal credit = BigDecimal.ZERO;
				BigDecimal debit = BigDecimal.ZERO;
				if(ledger.getSourcePocketID().equals(servicePartnerCollectorPocketID))
				{
					openingBalance =ct.getSourcePocketBalance()!=null?ct.getSourcePocketBalance():BigDecimal.ZERO;
					debit = ledger.getAmount().setScale(2, RoundingMode.HALF_DOWN);
					closingBalance = openingBalance;
					if(!CmFinoFIX.TransferStatus_Failed.equals(ct.getTransferStatus()))
						closingBalance = openingBalance.subtract(debit);
				}
				else
				{
					openingBalance =ct.getDestPocketBalance()!=null?ct.getDestPocketBalance():BigDecimal.ZERO;
					credit = ledger.getAmount().setScale(2, RoundingMode.HALF_DOWN);
					closingBalance = openingBalance;
					if(!CmFinoFIX.TransferStatus_Failed.equals(ct.getTransferStatus()))
						closingBalance = openingBalance.add(credit);
				}
				openingBalance = openingBalance.setScale(2, RoundingMode.HALF_DOWN);
				closingBalance = closingBalance.setScale(2, RoundingMode.HALF_DOWN);

				String rowContent = String.format(formatStr, 
						seq,
						ledger.getCommodityTransferID(),
						date,
						ledger.getSourcePocketID(),
						ledger.getDestPocketID(),
						transactionType,
						ledger.getSourceMDN(),
						ledger.getDestMDN(),
						credit,
						debit,
						openingBalance, 
						closingBalance,
						status,
						commodity,
						sourceApplication);
				xlsReport.addRowContent(rowContent);
				seq++;
			}
		}	
		return seq;
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
		return "FloatAccountReport";
	}

	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}
	
	private List<Ledger> getTransactionsFromLedger(Date start, Date end) 
	{		
		servicePartnerCollectorPocketID = getServicePartnerCollectorPocket();
		log.info("Service Partner Collector Pocket ID = " + servicePartnerCollectorPocketID);LedgerQuery query = new LedgerQuery();
		LedgerDAO ledgerDao = DAOFactory.getInstance().getLedgerDAO();
		query.setCreateTimeGE(start);
		query.setCreateTimeLT(end);
		query.setIDOrdered(true);
		query.setSourceDestnPocketID(servicePartnerCollectorPocketID);
		
		List<Ledger> ledgerList= ledgerDao.get(query);
		return ledgerList;	
	}
	
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	private Long getServicePartnerCollectorPocket()
	{
		return systemParametersService.getLong(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY);		
	}



}

