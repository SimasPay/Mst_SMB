package com.mfino.report.financial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.ChargeDefinitionDAO;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.ChargeTypeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.ChargeDefinitionQuery;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.domain.ChargeDefinition;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.Partner;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionChargeLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.pdf.SpecialPDFReport;
import com.mfino.service.PartnerService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.ExcelUtil;
import com.mfino.util.ReportUtil;

public class ACCSnapShot extends OfflineReportBase{


	private Map<Long,BigDecimal> chargeFunds= new HashMap<Long, BigDecimal>();
	private SubscriberMDNDAO mdnDao = DAOFactory.getInstance().getSubscriberMdnDAO();
	private PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
	private double zero = BigDecimal.ZERO.doubleValue();
	private ChargeTypeDAO chargeTypeDAO = DAOFactory.getInstance().getChargeTypeDAO();
	private String reportName;
	private String productName =  ConfigurationUtil.getReportProductName();

	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {

		File report=ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		File pdfReport=ReportUtil.getReportFilePath(reportName,end,ReportUtil.PDF_EXTENTION);
		String startTime = getDateFormat().format(new Date());
		log.info(report.getName()+" Processing StartTime: "+startTime);
		try{
			FileOutputStream file = new FileOutputStream(report);
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			DateFormat tf = new SimpleDateFormat("HHmm");
			df.setTimeZone(ConfigurationUtil.getLocalTimeZone());
			tf.setTimeZone(ConfigurationUtil.getLocalTimeZone());

			start = ReportUtil.getFinacialYearDate(end);

			if(data == null){
				data = new ReportBaseData();
				data.intializeStaticData();
			}
			data.getCommodityTransactions(start, end);
			data.getPendingCommodityTransactions(start, end);
			data.getServiceTransactionLogs(start, end);

			HSSFWorkbook hwb=new HSSFWorkbook();
			HSSFSheet sheet =  hwb.createSheet(reportName);
			sheet.setDisplayGridlines(false);
			sheet.setPrintGridlines(false);

			Font boldfont =ExcelUtil.getFont(hwb, true, reportName); 
			Font font = ExcelUtil.getFont(hwb, false, reportName);

			SpecialPDFReport pdf = new SpecialPDFReport(pdfReport, reportName);
			pdf.addHeaderTable();
			pdf.addTableToPdf();

			//adding logo
			try{
				InputStream is = this.getClass().getResourceAsStream("/logo.png");
				byte[] bytes = IOUtils.toByteArray(is);
				int pictureIdx = hwb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
				is.close();
				CreationHelper helper = hwb.getCreationHelper();
				Drawing drawing = sheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setRow1(0);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.resize();
			}catch (Exception e) {
				log.error("Failed to load logo");
			}
			sheet.addMergedRegion(new CellRangeAddress(0,3,0,2));

			HSSFRow row=   sheet.createRow(3);	
			ExcelUtil.createHeaderCell(hwb,row,1).setCellValue("MFS Balance Sheet");   

			pdf.createAnotherTable("", 5);
			int index = 7;	
			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,7).setCellValue("For the Date");
			createCell(hwb,row,1,font,15,7).setCellValue(df.format(end));
			pdf.addRowContent("For the Date,"+df.format(end)+",empty,empty,empty");

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,5).setCellValue("Time");
			createCell(hwb,row,1,font,15,5).setCellValue(tf.format(end));
			pdf.addRowContent("Time,"+tf.format(end)+",empty,empty,empty");

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,13).setCellValue("Date");
			createCell(hwb,row,1,font,15,13).setCellValue(df.format(start)+" to "+df.format(end));
			pdf.addRowContent("Date,"+df.format(start)+" to "+df.format(end)+",empty,empty,empty");

			row=   sheet.createRow(index++);
			index =12;
			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,3).setCellValue("Float wallet current Balance");
			Pocket floaWallet = pocketDAO.getById(systemParametersService.getLong(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY));
			createCell(hwb,row,1,font,15,6).setCellValue(floaWallet!=null?floaWallet.getCurrentBalance().doubleValue():zero);
			pdf.addRowContent("Float wallet current Balance,"+(floaWallet!=null?floaWallet.getCurrentBalance().doubleValue():zero)+",empty,empty,empty");

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,1).setCellValue("Service charge collector wallet balance");
			Pocket chargeWallet = pocketDAO.getById(systemParametersService.getLong(SystemParameterKeys.CHARGES_POCKET_ID_KEY));
			createCell(hwb,row,1,font,15,4).setCellValue(chargeWallet!=null?chargeWallet.getCurrentBalance().doubleValue():zero);
			pdf.addRowContent("Service charge collector wallet balance,"+(chargeWallet!=null?chargeWallet.getCurrentBalance().doubleValue():zero)+",empty,empty,empty");

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,9).setCellValue("Commission payout wallet");
			createCell(hwb,row,1,font,15,12).setCellValue(getCommissionPayoutWalletBalance().doubleValue());
			pdf.addRowContent("Commission payout wallet,"+(getCommissionPayoutWalletBalance().doubleValue())+",empty,empty,empty"); 
			pdf.addRowContent("empty,empty,empty,empty,empty");

			row=   sheet.createRow(index++);
			BigDecimal[] samcounts =getSubAgentMerchantCounts();
			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,11).setCellValue("Total registered Subscribers");
			createCell(hwb,row,1,font,15,14).setCellValue(samcounts[0].intValue());
			createCell(hwb,row,3,font,15,11).setCellValue("Total registered Agents");
			createCell(hwb,row,4,font,15,14).setCellValue(samcounts[1].intValue());
			pdf.addRowContent("Total registered Subscribers,"+(samcounts[0].intValue())+",empty,Total registered Agents,"+samcounts[1].intValue()); 

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,11).setCellValue("Total User Balance");
			createCell(hwb,row,1,boldfont,15,14).setCellValue(samcounts[3].doubleValue());
			createCell(hwb,row,3,boldfont,15,11).setCellValue("Total Agent balance");
			createCell(hwb,row,4,boldfont,15,14).setCellValue(samcounts[4].doubleValue());
			pdf.addRowContent("Total User Balance,"+(samcounts[3].intValue())+",empty,Total Agent balance,"+samcounts[4].intValue()); 
			pdf.addRowContent("empty,empty,empty,empty,empty");

			row=   sheet.createRow(index++);
			BigDecimal[] pendingCount = getPendingCounts(data);
			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,11).setCellValue("Total Pending Txns");
			createCell(hwb,row,1,font,15,14).setCellValue(pendingCount[0].intValue());
			createCell(hwb,row,3,font,15,11).setCellValue("Total Registered Merchants/Billers");
			createCell(hwb,row,4,font,15,14).setCellValue(samcounts[2].intValue());	
			pdf.addRowContent("Total Pending Txns,"+(pendingCount[0].intValue())+",empty,Total Registered Merchants/Billers,"+samcounts[2].intValue()); 


			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,11).setCellValue("Total Amount in Pending Txns");
			createCell(hwb,row,1,boldfont,15,14).setCellValue(pendingCount[1].doubleValue());
			createCell(hwb,row,3,boldfont,15,11).setCellValue("Total Merchant balance");
			createCell(hwb,row,4,boldfont,15,14).setCellValue(samcounts[5].doubleValue());
			pdf.addRowContent("Total Amount in Pending Txns,"+(pendingCount[1].doubleValue())+",empty,Total Merchant balance,"+samcounts[5].doubleValue()); 
			pdf.addRowContent("empty,empty,empty,empty,empty");


			row=   sheet.createRow(index++);

			BigDecimal[] counts = getCounts(data); 
			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,0,0).setCellValue("Assets:");	
			createCell(hwb,row,3,boldfont,0,0).setCellValue("Liabilities:");	
			pdf.addRowContent("Assets:,empty,empty,Liabilities:,empty");
			double assetsTotal = 0;
			double liabilitiesTotal = 0;

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,3).setCellValue("Service Charge Collections");
			createCell(hwb,row,1,font,15,6).setCellValue(counts[0].doubleValue());
			createCell(hwb,row,3,font,15,3).setCellValue("Agent commissions paid out");
			Long commisionId =chargeTypeDAO.getChargeTypeByName(ReportParameterKeys.COMMISION).getID();
			createCell(hwb,row,4,font,15,6).setCellValue(chargeFunds.get(commisionId)!=null?chargeFunds.get(commisionId).doubleValue():BigDecimal.ZERO.doubleValue());
			pdf.addRowContent("Service Charge Collections,"+(counts[0].doubleValue())+",empty,Agent commissions paid out,"+(chargeFunds.get(commisionId)!=null?chargeFunds.get(commisionId).doubleValue():BigDecimal.ZERO.doubleValue())); 
			assetsTotal +=counts[0].doubleValue();
			liabilitiesTotal +=chargeFunds.get(commisionId)!=null?chargeFunds.get(commisionId).doubleValue():BigDecimal.ZERO.doubleValue(); 

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,1).setCellValue(productName+" Inventory");
			createCell(hwb,row,1,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());
			createCell(hwb,row,3,font,15,1).setCellValue("Bonus/Promo pay outs");
			createCell(hwb,row,4,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());
			pdf.addRowContent(productName+" Inventory,"+(BigDecimal.ZERO.doubleValue())+",empty,Bonus/Promo pay outs,"+BigDecimal.ZERO.doubleValue()); 
			assetsTotal +=BigDecimal.ZERO.doubleValue();
			liabilitiesTotal +=BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,1).setCellValue("Settelemnt of charges received");
			createCell(hwb,row,1,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());
			createCell(hwb,row,3,font,15,1).setCellValue("Merchant Payments due");
			createCell(hwb,row,4,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());
			pdf.addRowContent("Settelemnt of charges received,"+(BigDecimal.ZERO.doubleValue())+",empty,Merchant Payments due,"+BigDecimal.ZERO.doubleValue()); 
			assetsTotal +=BigDecimal.ZERO.doubleValue();
			liabilitiesTotal +=BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,1).setCellValue("Air-time stock inventory");
			createCell(hwb,row,1,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());
			createCell(hwb,row,3,font,15,1).setCellValue("Tax paid out");
			createCell(hwb,row,4,font,15,4).setCellValue(counts[1].doubleValue());
			pdf.addRowContent("Air-time stock inventory,"+(BigDecimal.ZERO.doubleValue())+",empty,Tax paid out,"+counts[1].doubleValue()); 
			assetsTotal +=BigDecimal.ZERO.doubleValue();
			liabilitiesTotal +=counts[1].doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,1).setCellValue("Interest from float");
			createCell(hwb,row,1,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());
			createCell(hwb,row,3,font,15,1).setCellValue("Settlement ammount due");
			createCell(hwb,row,4,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());
			pdf.addRowContent("Interest from float,"+(BigDecimal.ZERO.doubleValue())+",empty,Settlement ammount due,"+BigDecimal.ZERO.doubleValue()); 
			assetsTotal +=BigDecimal.ZERO.doubleValue();
			liabilitiesTotal +=BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,1).setCellValue("Misc");
			createCell(hwb,row,1,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());	
			createCell(hwb,row,3,font,15,1).setCellValue("Misc");
			createCell(hwb,row,4,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());
			pdf.addRowContent("Misc,"+(BigDecimal.ZERO.doubleValue())+",empty,Misc,"+BigDecimal.ZERO.doubleValue()); 
			assetsTotal +=BigDecimal.ZERO.doubleValue();
			liabilitiesTotal +=BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,1).setCellValue("Intersystem settlement float");
			createCell(hwb,row,1,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());	
			createCell(hwb,row,3,font,15,1).setCellValue("Charges reversed");
			createCell(hwb,row,4,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());
			pdf.addRowContent("Intersystem settlement float,"+(BigDecimal.ZERO.doubleValue())+",empty,Charges reversed,"+BigDecimal.ZERO.doubleValue()); 
			assetsTotal +=BigDecimal.ZERO.doubleValue();
			liabilitiesTotal +=BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,11).setCellValue("Total");
			createCell(hwb,row,1,boldfont,15,14).setCellFormula("SUM(B"+(index-1)+":B"+(index-7)+")");
			createCell(hwb,row,3,boldfont,15,11).setCellValue("Total");
			createCell(hwb,row,4,boldfont,15,14).setCellFormula("SUM(E"+(index-1)+":E"+(index-7)+")");
			pdf.addRowContent("Total,"+assetsTotal+",empty,Total,"+liabilitiesTotal); 
			pdf.addTableToPdf();
			pdf.closePdfReport();

			ExcelUtil.autoSizeColumn(hwb, 5);
			hwb.write(file);
			file.close();
			//creating pdf report
			//XLSReport.convertXlsTopdf(report,pdfReport);	
		} catch ( Exception ex ) {
			log.error("Error in "+reportName+" :",ex);
		}
		log.info(report.getName()+" Processing StartTime:"+startTime+"EndTime: "+getDateFormat().format(new Date()));
		return report;
	}

	private BigDecimal getCommissionPayoutWalletBalance() {
		BigDecimal balance = BigDecimal.ZERO;
		ChargeDefinitionQuery query =new ChargeDefinitionQuery();
		ChargeDefinitionDAO chargeDefDao = DAOFactory.getInstance().getChargeDefinitionDAO();
		query.setFundingPartnerAndPocketNotNull(true);
		List<ChargeDefinition> chargeDefinations = chargeDefDao.get(query);
		Map <Long,String> pocketIds =new HashMap<Long,String> ();
		for(ChargeDefinition chargedef:chargeDefinations){
			Pocket pocket =chargedef.getPocket();
			if(!CmFinoFIX.PocketType_SVA.equals(pocket.getPocketTemplate().getType())
					||(!CmFinoFIX.Commodity_Money.equals(pocket.getPocketTemplate().getCommodity()))
					||pocketIds.containsKey(pocket.getID())){
				continue;
			}else{
				pocketIds.put(pocket.getID(), "processed");
				balance = balance.add(pocket.getCurrentBalance());
			}
		}
		return balance;
	}

	private Cell createCell(HSSFWorkbook hwb, HSSFRow row, int index, Font font,int border, int thickness) {
		return ExcelUtil.createCell(hwb, row, index, font, border, thickness);
	}

	private BigDecimal[] getPendingCounts(ReportBaseData data) {
		BigDecimal[] count = {BigDecimal.ZERO,BigDecimal.ZERO};
		if(data.getPctList()!=null){
			for(PendingCommodityTransfer pct:data.getPctList()){
				count[0] = count[0].add(new BigDecimal(1));
				count[1] = count[1].add(pct.getAmount().add(pct.getCharges().add(pct.getTaxAmount())));
			}
		}
		return count;
	}

	private BigDecimal[] getSubAgentMerchantCounts() {
		int subcount = 0;
		int agentcount = 0;
		int merchantcount = 0;
		BigDecimal subbalance = BigDecimal.ZERO;
		BigDecimal agentbalance = BigDecimal.ZERO;
		BigDecimal merchantbalance = BigDecimal.ZERO;

		List<SubscriberMDN> submdns =mdnDao.getAll();
		for(SubscriberMDN mdn:submdns){
			Subscriber sub = mdn.getSubscriber() ;
			Set<Pocket> pockets = mdn.getPocketFromMDNID();
			if(CmFinoFIX.SubscriberType_Subscriber.equals(sub.getType())){
				subcount++;
				for(Pocket pocket:pockets){
					subbalance = subbalance.add(pocket.getCurrentBalance());
				}
			}else if(CmFinoFIX.SubscriberType_Partner.equals(sub.getType())){
				Partner partner = sub.getPartnerFromSubscriberID().iterator().next();
				if(partnerService.isAgentType(partner.getBusinessPartnerType())){
					agentcount++;
					for(Pocket pocket:pockets){
						agentbalance = agentbalance.add(pocket.getCurrentBalance());
					}
				}else if(CmFinoFIX.BusinessPartnerType_Merchant.equals(partner.getBusinessPartnerType())){
					merchantcount++;
					for(Pocket pocket:pockets){
						merchantbalance = merchantbalance.add(pocket.getCurrentBalance());
					}
				}
			}


		}
		BigDecimal[] count = {new BigDecimal(subcount),new BigDecimal(agentcount),new BigDecimal(merchantcount),subbalance,agentbalance,merchantbalance};
		return count;
	}

	private BigDecimal[] getCounts(ReportBaseData data) {
		BigDecimal[] counts = {BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO};
		for(ServiceChargeTransactionLog sctl:data.getServiceChargeTransactionLogs()){
			if(sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Confirmed)
					||sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Started)
					||sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Completed)){
				//Charge collected from Service
				counts[0] = counts[0].add(sctl.getCalculatedCharge());

				//tax
				if(data.getCtMap().containsKey(sctl.getCommodityTransferID())){
					counts[1] = counts[1].add(getTaxPaid(sctl,data));	
				}


				//commisionpaid
				List<Long> tclList = data.getSctltclMap().get(sctl.getID());
				if(tclList!=null){
					for(Long tclid:tclList){
						TransactionChargeLog tc = data.getTclMap().get(tclid);
						Long chargeid = tc.getTransactionCharge().getChargeType().getID();
						if (chargeFunds.containsKey(chargeid)) {
							chargeFunds.put(chargeid,chargeFunds.get(chargeid).add(tc.getCalculatedCharge()));
						} else {
							chargeFunds.put(chargeid,tc.getCalculatedCharge());
						}
					}
				}
			}
		}


		return counts;
	}



	private BigDecimal getTaxPaid(ServiceChargeTransactionLog sctl,ReportBaseData data) {
		BigDecimal tax = BigDecimal.ZERO;
		ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
		query.setSctlID(sctl.getID());
		ChargeTxnCommodityTransferMapDAO ctmDao = DAOFactory.getInstance().getTxnTransferMap();
		List<ChargeTxnCommodityTransferMap> sctlVsCt = ctmDao.get(query);
		for(ChargeTxnCommodityTransferMap scct:sctlVsCt){
			CRCommodityTransfer ct= data.getCtMap().get(scct.getCommodityTransferID());
			if(ct!=null&&ct.getTransferStatus().equals(CmFinoFIX.TransferStatus_Completed)){
				tax= tax.add(ct.getTaxAmount());
			}
		}
		return tax;
	}



	@Override
	public String getFileName() {
		return "MFSBalanceSheet";
	}


	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected List<File> runAndGetMutlipleReports(Date start, Date end,
			ReportBaseData data) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}

	public static void main(String a[]){
		ACCSnapShot cfs = new ACCSnapShot();
		cfs.run(new Date(), new Date(), null);
	}


}