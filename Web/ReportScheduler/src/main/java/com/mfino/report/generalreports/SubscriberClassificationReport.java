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

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Address;
import com.mfino.domain.Partner;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.PartnerService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.ReportUtil;

public class SubscriberClassificationReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final int SUBSCRIBER_NUM_COLUMNS = 6;
	private String SUBSCRIBER_HEADER_ROW = "# , City, Total Number, Banked , Semi Banked, Unbanked";
	private File reportFile;
	private SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
	private String reportName;
	private Map<String,Integer[]> countMap;

	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected List<File> runAndGetMutlipleReports(Date start, Date end, ReportBaseData basedata) {
		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		List<File> reports = new ArrayList<File>();
		DateFormat df = getDateFormat();
		String formatStr = getFormatString(SUBSCRIBER_NUM_COLUMNS);
		String startTime = df.format(new Date());	

		log.info("Processing "+reportName+" StartTime:"+startTime);
		try {
			XLSReport subscriberXlsReport = new XLSReport(reportName, end);
			//adding logo
			try{
				subscriberXlsReport.addLogo();
			}catch (Exception e) {
				log.error("Failed to load logo",e);
			}

			subscriberXlsReport.addMergedRegion();
			subscriberXlsReport.addReportTitle(reportName);

			subscriberXlsReport.addHeaderRow(SUBSCRIBER_HEADER_ROW);
			countMap=getCounts();

			int seq = 1;
			for(String city:countMap.keySet()){
				Integer[] counts = countMap.get(city);
				String subscriberRowContent = String.format(formatStr, 
						seq,
						city.replace(",", " "),
						counts[0],
						counts[1],
						counts[2],
						counts[3]);
				subscriberXlsReport.addRowContent(subscriberRowContent);
				seq++;
			}

			subscriberXlsReport.writeToFileStream(reportFile, SUBSCRIBER_HEADER_ROW, reportName);
		}catch (Exception e) {
			log.error("Error in SubscriberClassificationReport",e);
		} 
		log.info("Processing "+reportName+" StartTime:"+startTime+" EndTime:"+df.format(new Date()));

		reports.add(reportFile);
		File agentReport = generateAgentReport(start,end);
		if (agentReport != null) {
			reports.add(agentReport);
		}
		File partnerReport = generatePartnerReport(start,end);
		if (agentReport != null) {
			reports.add(partnerReport);
		}

		return reports;
	}





	private File generatePartnerReport(Date start, Date end) {
		PartnerClassificationReport partnerClassificationReport = new PartnerClassificationReport();
		return partnerClassificationReport.run(start, end, countMap);
	}


	private File generateAgentReport(Date start, Date end) {
		AgentClassificationReport agentClassificationReport = new AgentClassificationReport();
		return agentClassificationReport.run(start, end, countMap);	
	}





	public Map<String, Integer[]> getCounts() {
		Map<String, Integer[]> counts = new HashMap<String, Integer[]>();
		List<SubscriberMDN> subscriberMDN = subscriberMDNDAO.getAll();
		Subscriber sub;
		for(SubscriberMDN mdn:subscriberMDN){
			sub=mdn.getSubscriber();
			Address address=sub.getAddressBySubscriberAddressID();
			Set<Partner> partners = sub.getPartnerFromSubscriberID();
			Partner partner=null;
			if(partners!=null&&!partners.isEmpty()){
				partner =  partners.iterator().next();
				address =partner.getAddressByMerchantAddressID();
			}
			if(address==null){
				continue;
			}

			Integer[] count= {0,0,0,0,0,0,0,0,0,0,0};
			String city =address.getCity();
			city = (StringUtils.isNotBlank(city)) ? city.trim().toUpperCase() : StringUtils.EMPTY;
			if(counts.containsKey(city)){
				count = counts.get(city);
			}
			if(partner==null){
				count[0]=count[0]+1;
				if(sub.getKYCLevelByKYCLevel()!=null){
					if(sub.getKYCLevelByKYCLevel().getKYCLevel().equals(ConfigurationUtil.getBulkUploadSubscriberKYClevel())){
						count[1]=count[1]+1;
					}else if(sub.getKYCLevelByKYCLevel().getKYCLevel().equals(ConfigurationUtil.getIntialKyclevel())){
						count[3]=count[3]+1;
					}else{
						count[2]=count[2]+1;
					}
				}
			}else if(partnerService.isAgentType(partner.getBusinessPartnerType())){
				count[4] = count[4]+1;
				if(partner.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_SuperAgent)){
					count[5]=count[5]+1;
				}else if(partner.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_DirectAgent)){
					count[6] =count[6]+1;
				}else if(partner.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_SubRetailAgent)){
					count[7] =count[7]+1;
				}
			}else{
				count[8] = count[8]+1;
				if(partner.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_Merchant)){
					count[10]=count[10]+1;
				}
				if(partner.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_BranchOffice)){
					count[9]=count[9]+1;
				}
			}
			counts.put(city, count);
		}

		return counts;
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end,
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


	@Override
	public boolean hasMultipleReports(){
		return true;
	}

}
