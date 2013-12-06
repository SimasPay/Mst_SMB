package com.mfino.report.generalreports;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.OfflineReportDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.util.ReportUtil;

public class PartnersSettlementReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private int NUM_COLUMNS = 8;
	private String HEADER_ROW = "# ,Partner Name , SourceWalletID, SourceWalletType, DestinationWalletId, DestinationWalletType, Amount, BankStan";
	private File reportFile;
	private String reportName;

	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end,  Map<Long, List<CommodityTransfer>> partnerSettlements) {
		try {
			if(StringUtils.isBlank(reportName)){
				OfflineReportDAO offlineReportDAO =DAOFactory.getInstance().getOfflineReportDAO();
				reportName =  offlineReportDAO.getByReportClass("com.mfino.report.generalreports.PartnersSettlementReport").getName();
			}
			if(partnerSettlements == null){
				EndofDayProcessReport eod = new EndofDayProcessReport();
				ReportBaseData data = new ReportBaseData();
				data.getCommodityTransactions(start, end);
				eod.getTransactionCountsAndAmounts(data.getCtList());
				partnerSettlements = eod.getBillerSettlements();
			}
			reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
			
			Set<Long> ids = partnerSettlements.keySet();
			PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
			PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
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

			int seq=1;
			Partner partner;
			String formatStr = getFormatString(NUM_COLUMNS);
			//writer.println(HEADER_ROW);
			for(Long id:ids){
				partner = partnerDao.getById(id);
				List<CommodityTransfer> ctList = partnerSettlements.get(id);
				for(CommodityTransfer ct:ctList){
					Pocket destPocket =  ct.getDestPocketID()!=null?pocketDao.getById(ct.getDestPocketID()):null;
					String bankStan = ct.getBankSystemTraceAuditNumber();
					if(bankStan == null){
						bankStan = "";
					}
					String rowContent = String.format(formatStr, 
							seq,
							partner.getTradeName(),
							ct.getPocketBySourcePocketID().getID(),
							ct.getPocketBySourcePocketID().getPocketTemplate().getDescription(),
							destPocket!=null?destPocket.getID():"",
									destPocket!=null?destPocket.getPocketTemplate().getDescription():"",
							ct.getAmount(),
							bankStan);

					xlsReport.addRowContent(rowContent); 
					seq++;
				}

			}
			xlsReport.writeToFileStream(reportFile,HEADER_ROW, reportName);
		} catch (Exception e) {
			log.error("Error in"+reportName+"", e);
		}
		return reportFile;
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
		this.reportName = reportName;
	}



	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {
		Map<Long, List<CommodityTransfer>> partnerSettlements = null;
		return run(start, end, partnerSettlements);
	}

}
