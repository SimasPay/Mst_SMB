package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
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

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.domain.Address;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.EnumTextService;
import com.mfino.util.ReportUtil;

public class SubscriberMoneyAvailableReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 8;
	private String HEADER_ROW =  "# ,SubscriberName,Channel Partner MDN,Available Balance,"+
			"InClearing Balace,Status, Address, LastTransactionTime";
	private File reportFile;
	private String reportName;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData basedata) {
		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		DateFormat df = getDateFormat();
		String formatStr = getFormatString(NUM_COLUMNS);
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" StartTime:"+startTime);


		try {
			//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));

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
			data.getPendingCommodityTransactions(start, end);
			SubscriberDAO subDao = DAOFactory.getInstance().getSubscriberDAO();
			List<Subscriber> subscribers = subDao.getSubscribers(CmFinoFIX.SubscriberType_Subscriber);
			//writer.println(HEADER_ROW);
			int seq=1;
			Map<Long,BigDecimal> inClearanceBalance = getInClearanceBalances(data);
			for(Subscriber subscriber:subscribers){
				Set<SubscriberMDN> subscriberMDNs = subscriber.getSubscriberMDNFromSubscriberID();
				if(subscriberMDNs==null||subscriberMDNs.isEmpty()){
					log.error("No mdn record for subscriber with id:"+subscriber.getID());
					continue;
				}
				SubscriberMDN mdn = subscriberMDNs.iterator().next();
				Set<Pocket> pockets=mdn.getPocketFromMDNID();
				String tradeName = subscriber.getFirstName()+" "+subscriber.getLastName();
				tradeName = tradeName.replaceAll(",", " ");
				Address subAddress = subscriber.getAddressBySubscriberAddressID();
				String address ="";
				if(subAddress != null)
				{
					address = subAddress.getLine1() + " " + subAddress.getLine2() + " " + subAddress.getCity() + " " + subAddress.getCountry() + " " + subAddress.getZipCode();
					/*
					 * Replace comma(,) with a space as comma is used as delimiter in generating an excel report
					 */
					address = address.replaceAll(",", " ");
				}
				for(Pocket pocket:pockets){
					if(pocket.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_SVA)
							&&pocket.getPocketTemplate().getCommodity().equals(CmFinoFIX.Commodity_Money)){
						BigDecimal inclearanceBalance = inClearanceBalance.get(pocket.getID());

						String rowContent = String.format(formatStr, 
								seq,
								tradeName,
								mdn.getMDN(),
								pocket.getCurrentBalance(), 
								inclearanceBalance!=null?inclearanceBalance:BigDecimal.ZERO,
								enumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, CmFinoFIX.Language_English, pocket.getStatus()),
								address,
								df.format(pocket.getLastUpdateTime()));

						xlsReport.addRowContent(rowContent);  
						seq++;
					}
				}
			}
			xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);

		} catch (Exception e) {
			log.error("Error in "+reportName+" Report", e);
		} 
		log.info("Processing "+reportName+" StartTime:"+startTime+" EndTime:"+df.format(new Date()));
		return reportFile;
	}


	private Map<Long,BigDecimal> getInClearanceBalances(ReportBaseData data) {
		List<PendingCommodityTransfer> pctlist = data.getPctList();
		Map<Long,BigDecimal> pocketCleringBalance = new HashMap<Long,BigDecimal>();
		for(PendingCommodityTransfer pct:pctlist){
			BigDecimal balance = BigDecimal.ZERO;
			if(pocketCleringBalance.containsKey(pct.getPocketBySourcePocketID().getID())){
				balance = pocketCleringBalance.get(pct.getPocketBySourcePocketID().getID()).add(pct.getAmount().add(pct.getCharges()));
			}
			pocketCleringBalance.put(pct.getPocketBySourcePocketID().getID(), balance);
		} 

		return pocketCleringBalance;
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
