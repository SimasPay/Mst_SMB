package com.mfino.report.generalreports;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.jasypt.hibernate.encryptor.HibernatePBEEncryptorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.xlsreport.XLSReport;
import com.mfino.service.EnumTextService;
import com.mfino.service.ReportParametersService;
import com.mfino.util.EncryptionUtil;
import com.mfino.util.OfflineReportUtil;
import com.mfino.util.ReportUtil;

/**
 *
 * @author Bala Sunku
 */
public class CustomerRegistrationReport extends OfflineReportBase{
	private static final int NUM_COLUMNS = 10;
	private String defaultRegesteringPartner;
	//private String HEADER_ROW = "#, Date, Customer MDN, No. Of Pockets, Registration Medium, Current Status, Last Updated, Customer Type,Account type, Agent Registered, WalletTypes ";
	private String HEADER_ROW = "#, Date, Customer MDN, No. Of Pockets, Registration Medium, Current Status, Customer Type, Account type, Agent Registered, WalletTypes ";
	private File reportFile;
	private SubscriberMDNDAO mdnDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
	private PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
	private String reportName;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;


	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end,ReportBaseData data) {
		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		defaultRegesteringPartner = ReportParametersService.getUpdatedValue(ReportParameterKeys.DEFAULT_PARTNER);
		int seq = 1;
		String startTime = getDateFormat().format(new Date());
		log.info("Processing "+reportName+" StartTime:"+startTime);
		try {
			SubscriberMdnQuery query = new SubscriberMdnQuery();
			query.setStartRegistrationDate(start);
			query.setEndRegistrationDate(end);
			query.setIDOrdered(true);
			//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
			//			HEADER_ROW = modifyHeader();
			//writer.println(HEADER_ROW);

			List<SubscriberMDN> results =  mdnDAO.get(query);
			reportForSubList(results, end, seq);
		
		
		} catch (HibernateException e) {
			log.error("Error in "+reportName+": " + e.getMessage(), e);
		} catch (IOException e) {
			log.error("Error in "+reportName+": " + e.getMessage(), e);
		}
		log.info("Processing "+reportName+" StartTime:"+startTime+"  EndTime:"+getDateFormat().format(new Date()));
		return reportFile;
	}

	
	public int reportForSubList(List<SubscriberMDN> results, Date end, int seq) throws IOException{

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

		String formatStr = getFormatString(NUM_COLUMNS);
		DateFormat df = getDateFormat();
		if(results!=null){
		for (SubscriberMDN smdn : results) {
			Subscriber sub = smdn.getSubscriber();                
			Set<Pocket> pocketSet= smdn.getPocketFromMDNID();
			Set<Partner> partner = sub.getPartnerFromSubscriberID();
			String accountType = partner!=null&&(!partner.isEmpty())?enumTextService.getEnumTextValue(CmFinoFIX.TagID_BusinessPartnerType, CmFinoFIX.Language_English, partner.iterator().next().getBusinessPartnerType()):enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberType, CmFinoFIX.Language_English, sub.getType());
			String mdn = smdn.getMDN();
			mdn = OfflineReportUtil.stripRx(mdn);
			String pockets=getPocketsList(pocketSet);
			String registeringAgent = sub.getRegisteringPartnerID()!=null?partnerDao.getById(sub.getRegisteringPartnerID()).getTradeName():defaultRegesteringPartner+sub.getCreatedBy();
//			String rowContent = String.format(formatStr,
//					seq,
//					df.format(smdn.getCreateTime()),
//					mdn,
//					pocketSet.size(),
//					EnumTextService.getEnumTextValue(CmFinoFIX.TagID_RegistrationMedium, null, sub.getRegistrationMedium()),
//					EnumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, null, smdn.getStatus()),
//					df.format((smdn.getLastUpdateTime().getTime() > sub.getLastUpdateTime().getTime()) 
//							? sub.getLastUpdateTime() : smdn.getLastUpdateTime()),
//							sub.getKYCLevelByKYCLevel().getKYCLevelName(),
//							accountType,
//							registeringAgent,
//							pockets
//					);
			
			String rowContent = String.format(formatStr,
					seq,
					df.format(smdn.getCreateTime()),
					mdn,
					pocketSet.size(),
					enumTextService.getEnumTextValue(CmFinoFIX.TagID_RegistrationMedium, null, sub.getRegistrationMedium()),
					enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberStatus, null, smdn.getStatus()),					
					sub.getKYCLevelByKYCLevel().getKYCLevelName(),
					accountType,
					registeringAgent,
					pockets
					);
			
			
			xlsReport.addRowContent(rowContent);
			seq++;
		}		
		}
		xlsReport.writeToFileStream(reportFile, HEADER_ROW, reportName);
		return seq;
	}

	private String getPocketsList(Set<Pocket> pocketSet) {
		if(pocketSet==null||pocketSet.isEmpty()){
			return "";
		}
		String pocketTypes=" ";
		for(Pocket pocket:pocketSet){
			pocketTypes = pocketTypes+pocket.getPocketTemplate().getDescription()+" &";
		}
		return pocketTypes.substring(0, pocketTypes.length()-1);
	}

	public static void main(String a[]) {
		HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
		registry.registerPBEStringEncryptor("hibernateStringEncryptor", EncryptionUtil.getStringEncryptor());
		registry.registerPBEStringEncryptor("hibernateUniqueStringEncryptor", EncryptionUtil.getUniqueStringEncryptor());

		CustomerRegistrationReport crr = new CustomerRegistrationReport();
		crr.run(new Date(), new Date(), null);
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
