package com.mfino.report.generalreports;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.mfino.dao.OfflineReportDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.domain.OfflineReport;
import com.mfino.domain.Partner;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.EnumTextService;
import com.mfino.service.PartnerService;
import com.mfino.util.ReportUtil;

public class MoneyAvailableReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int NUM_COLUMNS = 7;
	//private String HEADER_ROW =  "# ,PartnerName,Channel Partner MDN,WalletType ,WalletID ,Available Balance,"+
	//		"InClearing Balace, Accoun type, Status, Registration Date, Last ModifiedTime, LastTransactionTime";
	private String HEADER_ROW =  "# ,PartnerName,Channel Partner MDN,Available Balance,"+
			"InClearing Balance,Status,LastTransactionTime";
	private String Subscriber_HEADER_ROW =  "# ,SubscriberName,Channel Partner MDN,Available Balance,"+
			"InClearing Balance,Status,LastTransactionTime";
	private String AGENT_HEADER_ROW =  "# ,AgentName,AgentCode,Channel Partner MDN,"+
			"Available Balance,Status,LastTransactionTime";
	private File reportFile;
	private String reportName;
	private File agentreportFile;
	private String agentreportName;
	private File subscriberreportFile;
	private String subscriberreportName;
	private File partnerreportFile;
	private String partnerreportName;
	private OfflineReportDAO offlineReportDAO = DAOFactory.getInstance().getOfflineReportDAO();
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;

	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData basedata) {
		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);

		OfflineReport report = offlineReportDAO.getByReportClass("com.mfino.report.zenithreport.AgentMoneyAvailableReport");
		agentreportName = report!=null?report.getName():"AgentMoneyAvailableReport";
		agentreportFile  =ReportUtil.getReportFilePath(agentreportName,end,ReportUtil.EXCEL_EXTENTION);

		report = offlineReportDAO.getByReportClass("com.mfino.report.zenithreport.SubscriberMoneyAvailableReport");
		subscriberreportName = report!=null?report.getName():"SubscriberMoneyAvailableReport";
		subscriberreportFile  =ReportUtil.getReportFilePath(subscriberreportName,end,ReportUtil.EXCEL_EXTENTION);

		report = offlineReportDAO.getByReportClass("com.mfino.report.zenithreport.PartnerMoneyAvailableReport");
		partnerreportName =  report!=null?report.getName():"PartnerMoneyAvailableReport";
		partnerreportFile  =ReportUtil.getReportFilePath(partnerreportName,end,ReportUtil.EXCEL_EXTENTION);

		class AgentRowObject{
			int seq;
			String tradeName;
			String agentCode;
			String mdn;
			BigDecimal currentBalance;
			String status;
			Timestamp ts;

			public AgentRowObject(int seq,String tradeName,String agentCode,String mdn,BigDecimal currentBalance,String status,Timestamp ts){
				this.seq = seq;
				this.tradeName =tradeName;
				this.agentCode = agentCode;
				this.mdn = mdn;
				this.currentBalance = currentBalance;
				this.status = status;
				this.ts = ts;
			}
		}	
		List<AgentRowObject> agentRows = new ArrayList<AgentRowObject>();

		DateFormat df = getDateFormat();
		String formatStr = getFormatString(NUM_COLUMNS);
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" StartTime:"+startTime);


		try {
			//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));

			XLSReport xlsReport = new XLSReport(reportName, end);
			XLSReport agentMAR = new XLSReport(agentreportName, end);
			XLSReport subscriberMAR = new XLSReport(subscriberreportName, end);
			XLSReport partnerMAR = new XLSReport(partnerreportName, end);
			//adding logo
			try{
				xlsReport.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}
			try{
				agentMAR.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}
			try{
				subscriberMAR.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}
			try{
				partnerMAR.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}

			xlsReport.addMergedRegion();			
			xlsReport.addReportTitle(reportName);
			xlsReport.addHeaderRow(HEADER_ROW);

			agentMAR.addMergedRegion();			
			agentMAR.addReportTitle(agentreportName);
			agentMAR.addHeaderRow(AGENT_HEADER_ROW);

			partnerMAR.addMergedRegion();			
			partnerMAR.addReportTitle(partnerreportName);
			partnerMAR.addHeaderRow(HEADER_ROW);

			subscriberMAR.addMergedRegion();			
			subscriberMAR.addReportTitle(subscriberreportName);
			subscriberMAR.addHeaderRow(Subscriber_HEADER_ROW);

			ReportBaseData data = basedata;
			if (data == null) {
				data = new ReportBaseData();
				data.intializeStaticData();
			} 
			data.getPendingCommodityTransactions(start, end);
			SubscriberDAO subDao = DAOFactory.getInstance().getSubscriberDAO();
			List<Subscriber> subscribers = subDao.getAll();
			//writer.println(HEADER_ROW);
			int seq=1;
			int subscriberSeq = 1;
			int agentSeq = 1;
			int partnerSeq =1;
			Map<Long,BigDecimal> inClearanceBalance = getInClearanceBalances(data);
			for(Subscriber subscriber:subscribers){
				Set<SubscriberMDN> subscriberMDNs = subscriber.getSubscriberMDNFromSubscriberID();
				if(subscriberMDNs==null||subscriberMDNs.isEmpty()){
					log.error("No mdn record for subscriber with id:"+subscriber.getID());
					continue;
				}
				SubscriberMDN mdn = subscriberMDNs.iterator().next();
				Set<Pocket> pockets=mdn.getPocketFromMDNID();
				Set<Partner> partner= subscriber.getPartnerFromSubscriberID();
				String tradeName = partner!=null&&(!partner.isEmpty())?partner.iterator().next().getTradeName():subscriber.getFirstName()+" "+subscriber.getLastName();
				String agentCode = partner!=null&&(!partner.isEmpty())?partner.iterator().next().getPartnerCode():subscriber.getFirstName()+" "+subscriber.getLastName();
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
										df.format(pocket.getLastUpdateTime()));

						xlsReport.addRowContent(rowContent);  
						if(subscriber.getType().equals(CmFinoFIX.SubscriberType_Subscriber)){
							rowContent = String.format(formatStr, 
									subscriberSeq,
									tradeName,
									mdn.getMDN(),
									pocket.getCurrentBalance(), 
									inclearanceBalance!=null?inclearanceBalance:BigDecimal.ZERO,
											enumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, CmFinoFIX.Language_English, pocket.getStatus()),
											df.format(pocket.getLastUpdateTime()));
							subscriberMAR.addRowContent(rowContent);
							subscriberSeq++;
						}else{
							Integer businessPartnerType = partner.iterator().next().getBusinessPartnerType();
							if(partnerService.isAgentType(businessPartnerType)){
								agentRows.add(new AgentRowObject(agentSeq, tradeName, agentCode, mdn.getMDN(), pocket.getCurrentBalance(), enumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, CmFinoFIX.Language_English, pocket.getStatus()), pocket.getLastUpdateTime()));
								agentSeq++;
							}else{
								rowContent = String.format(formatStr, 
										partnerSeq,
										tradeName,
										mdn.getMDN(),
										pocket.getCurrentBalance(), 
										inclearanceBalance!=null?inclearanceBalance:BigDecimal.ZERO,
												enumTextService.getEnumTextValue(CmFinoFIX.TagID_PocketStatus, CmFinoFIX.Language_English, pocket.getStatus()),
												df.format(pocket.getLastUpdateTime()));
								partnerSeq++;
								partnerMAR.addRowContent(rowContent);
							}
						}
						seq++;
					}
				}
			}
			Collections.sort(agentRows, new Comparator(){
				@Override
				public int compare(Object o1, Object o2) {
					AgentRowObject r1 = (AgentRowObject) o1;
					AgentRowObject r2 = (AgentRowObject) o2;
					return r2.currentBalance.compareTo(r1.currentBalance);
				}

			});
			seq = 1;
			for (AgentRowObject ar : agentRows) {
				String rowContent = String.format(formatStr, 
						seq++,
						ar.tradeName,
						ar.agentCode,
						ar.mdn,
						ar.currentBalance, 
						ar.status,
						df.format(ar.ts));
				agentMAR.addRowContent(rowContent);
			}

			xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);
			agentMAR.writeToFileStream(agentreportFile, AGENT_HEADER_ROW, agentreportName);
			subscriberMAR.writeToFileStream(subscriberreportFile, Subscriber_HEADER_ROW, subscriberreportName);
			partnerMAR.writeToFileStream(partnerreportFile, HEADER_ROW, partnerreportName);

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
