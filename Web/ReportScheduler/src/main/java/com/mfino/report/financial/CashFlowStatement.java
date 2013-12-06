package com.mfino.report.financial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.ChargeTypeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.LedgerDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Ledger;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionChargeLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.pdf.SpecialPDFReport;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.ExcelUtil;
import com.mfino.util.ReportUtil;

public class CashFlowStatement extends OfflineReportBase{

	private Map<Long,BigDecimal> serviceCharge= new HashMap<Long, BigDecimal>();
	private Map<Long,BigDecimal> chargeFunds= new HashMap<Long, BigDecimal>();
	private ChargeTypeDAO chargeTypeDAO = DAOFactory.getInstance().getChargeTypeDAO();
	private String reportName;
	private String productName =  ConfigurationUtil.getReportProductName();
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData reportdata) {

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

			ReportBaseData data = new ReportBaseData();
			data.intializeStaticData();

			data.getCommodityTransactions(start, end);
			data.getServiceTransactionLogs(start, end);
			BigDecimal[] counts = getCountsForCashFlow(data); 

			HSSFWorkbook hwb=new HSSFWorkbook();
			HSSFSheet sheet =  hwb.createSheet(reportName);
			sheet.setDisplayGridlines(false);
			sheet.setDisplayGridlines(false);
			sheet.setPrintGridlines(false);

			Font boldfont =ExcelUtil.getFont(hwb, true, reportName); 
			Font font = ExcelUtil.getFont(hwb, false, reportName);

			SpecialPDFReport pdf = new SpecialPDFReport(pdfReport, reportName);
			pdf.addHeaderTable();
			pdf.addTableToPdf();
			//adding logo
			try{
				InputStream is =this.getClass().getResourceAsStream("/logo.png");
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
			ExcelUtil.createHeaderCell(hwb,row,1).setCellValue("CashFlowStatement");
			int index = 7;	
			int servicerows = serviceCharge.keySet().size();
			row=   sheet.createRow(index++);
			createCell(hwb,row,1,boldfont,0,0).setCellValue("");
			createCell(hwb,row,2,boldfont,0,0).setCellValue("For current FY starting:");
			createCell(hwb,row,3,boldfont,0,0).setCellValue(df.format(start));

			pdf.createAnotherTable("For current FY starting: "+df.format(start), 3);

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,15).setCellValue("");
			createCell(hwb,row,1,boldfont,15,15).setCellValue("For the period starting");
			createCell(hwb,row,2,boldfont,15,15).setCellValue("To the period ending");
			pdf.addRowContent(",For the period starting,To the period ending");

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,15).setCellValue("Date");
			createCell(hwb,row,1,font,15,15).setCellValue(df.format(start));
			createCell(hwb,row,2,font,15,15).setCellValue(df.format(end));
			pdf.addRowContent("Date,"+df.format(start)+","+df.format(end));

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,15).setCellValue("Time");
			createCell(hwb,row,1,font,15,15).setCellValue(tf.format(start));
			createCell(hwb,row,2,font,15,15).setCellValue(tf.format(end));
			pdf.addRowContent("Time,"+tf.format(start)+","+tf.format(start));
			pdf.addTableToPdf();

			row=   sheet.createRow(index++);

			pdf.createAnotherTable(" ", 3);
			index = 12;
			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,0,0).setCellValue("");
			createCell(hwb,row,1,boldfont,0,0).setCellValue("Particulars");
			createCell(hwb,row,2,boldfont,0,0).setCellValue("Amount");
			pdf.addRowContent(" ,Particulars,Amount");

			//A
			List<String> specialRows = new ArrayList<String>();
			double cashReceivedtotalSum = 0;
			sheet.addMergedRegion(new CellRangeAddress(index,17+servicerows,0,0));
			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,15).setCellValue("A");
			createCell(hwb,row,1,boldfont,15,15).setCellValue("Cash received from operations");
			createCell(hwb,row,2,boldfont,15,15).setCellFormula("SUM(C15:C"+(18+servicerows)+")");
			int rowSpan = 1;

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,5).setCellValue("");
			createCell(hwb,row,1,font,15,5).setCellValue("Cash received from customer Registration");
			createCell(hwb,row,2,font,15,5).setCellValue(counts[0].doubleValue());
			specialRows.add("Cash received from customer Registration,"+counts[0].doubleValue());
			cashReceivedtotalSum += counts[0].doubleValue();
			rowSpan++;

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,5).setCellValue("");
			createCell(hwb,row,1,font,15,5).setCellValue("Cash received to "+productName+" ( Bank )");
			createCell(hwb,row,2,font,15,5).setCellValue(counts[1].doubleValue());
			specialRows.add("Cash received to "+productName+" ( Bank ),"+counts[1].doubleValue());
			cashReceivedtotalSum += counts[1].doubleValue();
			rowSpan++;

			for(Long serviceID:serviceCharge.keySet()){
				row=   sheet.createRow(index++);
				createCell(hwb,row,0,font,15,5).setCellValue("");
				createCell(hwb,row,1,font,15,5).setCellValue("Charge collected from "+ReportBaseData.serviceMap.get(serviceID).getServiceName());
				//			BigDecimal value = serviceCharge.get(serviceID);
				//			createCell(hwb,row,2,font,0,0).setCellValue(value!=null?value.doubleValue():BigDecimal.ZERO.doubleValue());
				createCell(hwb,row,2,font,15,5).setCellValue(serviceCharge.get(serviceID).doubleValue());
				specialRows.add("Charge collected from "+ReportBaseData.serviceMap.get(serviceID).getServiceName()+","+serviceCharge.get(serviceID).doubleValue());
				cashReceivedtotalSum += serviceCharge.get(serviceID).doubleValue();
				rowSpan++;
			}		

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,5).setCellValue("");
			createCell(hwb,row,1,font,15,5).setCellValue("Interest received on Float");
			createCell(hwb,row,2,font,15,5).setCellValue(BigDecimal.ZERO.doubleValue());
			specialRows.add("Interest received on Float,"+BigDecimal.ZERO.doubleValue());
			cashReceivedtotalSum += BigDecimal.ZERO.doubleValue();
			rowSpan++;

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,13).setCellValue("");
			createCell(hwb,row,1,font,15,13).setCellValue("Remittance received ");
			createCell(hwb,row,2,font,15,13).setCellValue(BigDecimal.ZERO.doubleValue());
			specialRows.add("Remittance received,"+BigDecimal.ZERO.doubleValue());
			cashReceivedtotalSum += BigDecimal.ZERO.doubleValue();
			rowSpan++;

			pdf.addSpecialRow("A,Cash received from operations,"+cashReceivedtotalSum, rowSpan);
			for(String s:specialRows){
				pdf.addRowContent(s);
			}



			row=   sheet.createRow(index++);
			pdf.addRowContent("empty,empty,empty");

			//B
			specialRows = new ArrayList<String>();
			double cashPaidtotalSum = 0;
			sheet.addMergedRegion(new CellRangeAddress(index,24+servicerows,0,0));
			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,7).setCellValue("B");
			createCell(hwb,row,1,boldfont,15,7).setCellValue("Cash Paid from Operations");
			createCell(hwb,row,2,boldfont,15,7).setCellFormula("SUM(C"+(21+servicerows)+":C"+(25+servicerows)+")");

			row=   sheet.createRow(index++);
			Long commissionID = chargeTypeDAO.getChargeTypeByName(ReportParameterKeys.COMMISION).getID();
			createCell(hwb,row,1,font,15,7).setCellValue("Commission paid");
			createCell(hwb,row,2,font,15,7).setCellValue(chargeFunds.get(commissionID)!=null?chargeFunds.get(commissionID).doubleValue():BigDecimal.ZERO.doubleValue());//get commission charge type id
			specialRows.add("Commission paid,"+(chargeFunds.get(commissionID)!=null?chargeFunds.get(commissionID).doubleValue():0));
			cashPaidtotalSum += chargeFunds.get(commissionID)!=null?chargeFunds.get(commissionID).doubleValue():BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,5).setCellValue("");
			createCell(hwb,row,1,font,15,5).setCellValue("Tax Paid");
			createCell(hwb,row,2,font,15,5).setCellValue(counts[2].doubleValue());
			specialRows.add("Tax Paid,"+counts[2].doubleValue());
			cashPaidtotalSum += counts[2].doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,5).setCellValue("");
			createCell(hwb,row,1,font,15,5).setCellValue("Cash Sent to Bank ("+productName+")");
			createCell(hwb,row,2,font,15,5).setCellValue(counts[3].doubleValue());
			specialRows.add("Cash Sent to Bank ("+productName+"),"+counts[3].doubleValue());
			cashPaidtotalSum += counts[3].doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,5).setCellValue("");
			createCell(hwb,row,1,font,15,5).setCellValue("Bonus and promotions Paid");
			createCell(hwb,row,2,font,15,5).setCellValue(BigDecimal.ZERO.doubleValue());
			specialRows.add("Bonus and promotions Paid,"+BigDecimal.ZERO.doubleValue());
			cashPaidtotalSum +=BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,5).setCellValue("");
			createCell(hwb,row,1,font,15,5).setCellValue("Remittance Paid");
			createCell(hwb,row,2,font,15,5).setCellValue(BigDecimal.ZERO.doubleValue());
			specialRows.add("Remittance Paid,"+BigDecimal.ZERO.doubleValue());
			cashPaidtotalSum += BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,13).setCellValue("");
			createCell(hwb,row,1,font,15,13).setCellValue("Payment to Billers/merchants/partners etc");
			createCell(hwb,row,2,font,15,13).setCellValue(BigDecimal.ZERO.doubleValue());
			specialRows.add("Payment to Billers/merchants/partners etc,"+BigDecimal.ZERO.doubleValue());
			cashPaidtotalSum += BigDecimal.ZERO.doubleValue();


			pdf.addSpecialRow("B,Cash Paid from Operations,"+cashPaidtotalSum, 7);
			for(String s:specialRows){
				pdf.addRowContent(s);
			}
			pdf.addRowContent("empty,empty,empty");
			pdf.addRowContent("empty,empty,empty");


			row=   sheet.createRow(index++);
			row=   sheet.createRow(index++);

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,15).setCellValue("C");
			createCell(hwb,row,1,boldfont,15,15).setCellValue("Net Cash (A-B)");
			createCell(hwb,row,2,boldfont,15,15).setCellFormula("(C14-C26)");
			double netCash = cashReceivedtotalSum-cashPaidtotalSum;
			pdf.addRowContent("C,Net Cash (A-B),"+netCash);

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,15).setCellValue("D");
			createCell(hwb,row,1,boldfont,15,15).setCellValue("Cash at the beginning");
			createCell(hwb,row,2,boldfont,15,15).setCellValue(getGlobalStartingBalance(data).doubleValue());
			pdf.addRowContent("D,Cash at the beginning,"+getGlobalStartingBalance(data).doubleValue());

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,15).setCellValue("E");
			createCell(hwb,row,1,boldfont,15,15).setCellValue("Cash at the end (C+D)");
			createCell(hwb,row,2,boldfont,15,15).setCellFormula("(C34+C35)");
			pdf.addRowContent("E,Cash at the end (C+D),"+(netCash+getGlobalStartingBalance(data).doubleValue()));
			pdf.addTableToPdf();
			pdf.closePdfReport();

			ExcelUtil.autoSizeColumn(hwb,3);
			hwb.write(file);
			file.close();
			//creating pdf report
			//	XLSReport.convertXlsTopdf(report,pdfReport);

		} catch ( Exception ex ) {
			log.error("Error in "+reportName+" :",ex);
		}
		log.info(report.getName()+" Processing StartTime:"+startTime+" EndTime: "+getDateFormat().format(new Date()));
		return report;
	}

	private BigDecimal getGlobalStartingBalance(ReportBaseData data) {
		int size = data.getCtList().size();
		Long globalSvaId = systemParametersService.getLong(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY);
		CommodityTransfer ct = null;
		for(int i=size;i>0;i--){
			ct = data.getCtList().get(i-1);
			if(CmFinoFIX.PocketType_BankAccount.equals(ct.getSourcePocketType())&&CmFinoFIX.PocketType_SVA.equals(ct.getDestPocketType())
					||CmFinoFIX.PocketType_SVA.equals(ct.getSourcePocketType())&&CmFinoFIX.PocketType_BankAccount.equals(ct.getDestPocketType())){
				break;
			}
		}
		if(ct==null){
			PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
			return	pocketDAO.getById(globalSvaId).getCurrentBalance();
		}
		LedgerDAO ledDao =DAOFactory.getInstance().getLedgerDAO();
		List<Ledger> ledgers=ledDao.getByCommmodityTransferID(ct.getID());
		for(Ledger ledger : ledgers){
			if(ledger.getSourcePocketID().equals(globalSvaId)){
				return ledger.getSourcePocketBalance();
			}
			if(ledger.getDestPocketID().equals(globalSvaId)){
				return ledger.getDestPocketBalance();
			}
		}
		return BigDecimal.ZERO;
	}

	private BigDecimal[] getCountsForCashFlow(ReportBaseData data) {
		BigDecimal[] counts = {BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO};
		for(ServiceChargeTransactionLog sctl:data.getServiceChargeTransactionLogs()){
			if(sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Confirmed)
					||sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Started)
					||sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Completed)){
				//Cash received from customer Registration	
				// *FindbugsChange*
	        	// Previous -- if(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBERREGISTRATION.equals(ReportBaseData.transactiontypeMap.get(sctl.getTransactionTypeID()))){
				
				if(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBERREGISTRATION.equals(String.valueOf(ReportBaseData.transactiontypeMap.get(sctl.getTransactionTypeID())))){
					counts[0] = counts[0].add(sctl.getCalculatedCharge());
				}
				//Cash received to eaZymoney ( Bank )
				if(data.getCtMap().containsKey(sctl.getCommodityTransferID())){
					CommodityTransfer ct = data.getCtMap().get(sctl.getCommodityTransferID());
					counts[2] = counts[2].add(ct.getTaxAmount());
					if(CmFinoFIX.PocketType_BankAccount.equals(ct.getSourcePocketType())
							&&CmFinoFIX.PocketType_SVA.equals(ct.getDestPocketType())
							&&CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())){
						counts[1] = counts[1].add(ct.getAmount());
					}
					if(CmFinoFIX.PocketType_SVA.equals(ct.getSourcePocketType())
							&&CmFinoFIX.PocketType_BankAccount.equals(ct.getDestPocketType())
							&&CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())){
						counts[3] = counts[3].add(ct.getAmount());
					}
				}
				//Charge collected from Service
				if(serviceCharge.containsKey(sctl.getServiceID())){
					serviceCharge.put(sctl.getServiceID(), serviceCharge.get(sctl.getServiceID()).add(sctl.getCalculatedCharge()));
				}else{
					serviceCharge.put(sctl.getServiceID(), sctl.getCalculatedCharge());
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

	private Cell createCell(HSSFWorkbook hwb, HSSFRow row, int index, Font font,int border, int thickness) {
		return ExcelUtil.createCell(hwb, row, index, font, border, thickness);
	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
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
		CashFlowStatement cfs = new CashFlowStatement();
		cfs.run(new Date(), new Date(), null);
	}
}