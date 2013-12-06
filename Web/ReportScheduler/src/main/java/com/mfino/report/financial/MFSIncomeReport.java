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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionChargeLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.pdf.SpecialPDFReport;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.ExcelUtil;
import com.mfino.util.ReportUtil;

public class MFSIncomeReport extends OfflineReportBase{


	private Map<Long,BigDecimal> chargeFunds= new HashMap<Long, BigDecimal>();
	private String reportName;
	private String productName =  ConfigurationUtil.getReportProductName();


	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {

		File report=ReportUtil.getReportFilePath(reportName,start,end);
		File pdfReport=ReportUtil.getPdfReportFilePath(reportName,start,end);
		String startTime = getDateFormat().format(new Date());
		log.info(report.getName()+" Processing StartTime: "+startTime);
		
		try{
			FileOutputStream file = new FileOutputStream(report);
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			DateFormat tf = new SimpleDateFormat("HHmm");
			df.setTimeZone(ConfigurationUtil.getLocalTimeZone());
			tf.setTimeZone(ConfigurationUtil.getLocalTimeZone());

			if(data == null){
				data = new ReportBaseData();
				data.intializeStaticData();
			}
			data.getCommodityTransactions(start, end);
			data.getServiceTransactionLogs(start, end);
			BigDecimal[] counts = getCountsForCashFlow(data); 



			HSSFWorkbook hwb=new HSSFWorkbook();
			HSSFSheet sheet =  hwb.createSheet(reportName);
			sheet.setDisplayGridlines(false);
			sheet.setDisplayGridlines(false);
			sheet.setPrintGridlines(false);
			//			sheet.setDefaultColumnWidth(32);

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
				log.error("Failed to load logo",e);
			}
			sheet.addMergedRegion(new CellRangeAddress(0,3,0,2));
			HSSFRow row=   sheet.createRow(3);	
			ExcelUtil.createHeaderCell(hwb,row,1).setCellValue("MFS Income Report");   

			pdf.createAnotherTable("For current FY starting:"+df.format(start), 3);
			int index = 7;	
			row=   sheet.createRow(index++);
			createCell(hwb,row,1,boldfont,0,0).setCellValue("For current FY starting:");
			createCell(hwb,row,2,boldfont,0,0).setCellValue(df.format(start));

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,15).setCellValue("");
			createCell(hwb,row,1,boldfont,15,15).setCellValue("For the period starting");
			createCell(hwb,row,2,boldfont,15,15).setCellValue("To the period ending");
			pdf.addRowContent(",For the period starting:,To the period ending");

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,7).setCellValue("Date");
			createCell(hwb,row,1,font,15,7).setCellValue(df.format(start));
			createCell(hwb,row,2,font,15,7).setCellValue(df.format(end));
			pdf.addRowContent("Date,"+df.format(start)+","+df.format(end));

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,13).setCellValue("Time");
			createCell(hwb,row,1,font,15,13).setCellValue(tf.format(start));
			createCell(hwb,row,2,font,15,13).setCellValue(tf.format(end));
			pdf.addRowContent("Time,"+tf.format(start)+","+tf.format(end));
			pdf.addRowContent("empty,empty,empty");
			pdf.addTableToPdf();

			row=   sheet.createRow(index++);		
			index = 12;
			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,0,0).setCellValue("Income:");
			pdf.createAnotherTable("Income:", 2);
			double totalIncome = 0;

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,3).setCellValue("Service charge collected");
			createCell(hwb,row,1,font,15,6).setCellValue(counts[0].doubleValue());
			pdf.addRowContent("Service charge collected,"+counts[0].doubleValue());
			totalIncome +=counts[0].doubleValue();
			
			if(productName.indexOf("eaZymoney") != -1){
				index = addChargesFromDSTV(counts, hwb, sheet, font, index);
				pdf.addRowContent("DSTV Service charge collection,"+counts[1].doubleValue());
				totalIncome +=counts[1].doubleValue();
			}

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,1).setCellValue("Settlement of charges received");
			createCell(hwb,row,1,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());
			pdf.addRowContent("Settlement of charges received,"+BigDecimal.ZERO.doubleValue());
			totalIncome +=BigDecimal.ZERO.doubleValue();
			
			if(productName.indexOf("eaZymoney") != -1){
				index = addChargesFromVisafone(counts, hwb, sheet, font, index);
				pdf.addRowContent("Visafone Service Collection,"+counts[2].doubleValue());
				totalIncome +=counts[2].doubleValue();
			}

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,15).setCellValue("Total");
			createCell(hwb,row,1,boldfont,15,15).setCellValue(counts[0].doubleValue());
			pdf.addRowContent("Total,"+totalIncome);
			pdf.addRowContent("empty,empty");
			pdf.addTableToPdf();

			row=   sheet.createRow(index++);

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,0,0).setCellValue("Expenses:");
			pdf.createAnotherTable("Expenses:", 2);
			double totalExpenses = 0;

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,3).setCellValue("Commissions paid ");
			/*
			 * Code before using findbug tool 
			 * chargeFunds.get(1)
			 */
			/*
			 * Code after using findbug tool
			 * The type of the argument should be a supertype or a subtype of the corresponding generic type argument.
			 *  chargeFunds.get(1L)
			 */
			
			createCell(hwb,row,1,font,15,6).setCellValue(chargeFunds.get(1L)!=null?chargeFunds.get(1L).doubleValue():BigDecimal.ZERO.doubleValue());
			pdf.addRowContent("Commissions paid,"+(chargeFunds.get(1L)!=null?chargeFunds.get(1L).doubleValue():BigDecimal.ZERO.doubleValue()));
			totalExpenses += chargeFunds.get(1L)!=null?chargeFunds.get(1L).doubleValue():BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,1).setCellValue("Bonuses paid");
			createCell(hwb,row,1,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());
			pdf.addRowContent("Bonuses paid,"+BigDecimal.ZERO.doubleValue());
			totalExpenses += BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,1).setCellValue("Promotion expense");
			createCell(hwb,row,1,font,15,4).setCellValue(BigDecimal.ZERO.doubleValue());//get commission charge type id
			pdf.addRowContent("Promotion expense,"+BigDecimal.ZERO.doubleValue());
			totalExpenses += BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,1).setCellValue("Tax Paid");
			createCell(hwb,row,1,font,15,4).setCellValue(counts[3].doubleValue());
			pdf.addRowContent("Tax Paid,"+counts[3].doubleValue());
			totalExpenses += counts[3].doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,font,15,9).setCellValue("Service charges reversed");
			createCell(hwb,row,1,font,15,12).setCellValue(BigDecimal.ZERO.doubleValue());
			pdf.addRowContent("Service charges reversed,"+BigDecimal.ZERO.doubleValue());
			totalExpenses += BigDecimal.ZERO.doubleValue();

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,15).setCellValue("Total");
			createCell(hwb,row,1,boldfont,15,15).setCellFormula("SUM(C"+(index-1)+":C"+(index-6)+")");
			pdf.addRowContent("Total,"+totalExpenses);
			pdf.addRowContent("empty,empty");

			row=   sheet.createRow(index++);

			row=   sheet.createRow(index++);
			createCell(hwb,row,0,boldfont,15,15).setCellValue("Revenue");
			createCell(hwb,row,1,boldfont,15,15).setCellFormula("(C18-C26)");
			pdf.addRowContent("Revenue,"+(totalIncome+totalExpenses));
			pdf.addTableToPdf();
			pdf.closePdfReport();

			ExcelUtil.autoSizeColumn(hwb, 3);
			hwb.write(file);
			file.close();
			//creating pdf report
			//	XLSReport.convertXlsTopdf(report,pdfReport);	

		} catch ( Exception ex ) {
			log.error("Error in "+reportName+" :",ex);
		}
		log.info(report.getName()+" Processing StartTime:"+startTime+"EndTime: "+getDateFormat().format(new Date()));
		return report;
	}

	protected int addChargesFromVisafone(BigDecimal[] counts, HSSFWorkbook hwb,
			HSSFSheet sheet, Font font, int index) {
		HSSFRow row;
		row=   sheet.createRow(index++);
		createCell(hwb,row,0,font,15,9).setCellValue("Visafone Service Collection");
		createCell(hwb,row,1,font,15,12).setCellValue(counts[2].doubleValue());
		return index;
	}

	protected int addChargesFromDSTV(BigDecimal[] counts, HSSFWorkbook hwb,
			HSSFSheet sheet, Font font, int index) {
		HSSFRow row;
		row=   sheet.createRow(index++);
		createCell(hwb,row,0,font,15,1).setCellValue("DSTV Service charge collection");
		createCell(hwb,row,1,font,15,4).setCellValue(counts[1].doubleValue());
		return index;
	}

	private BigDecimal[] getCountsForCashFlow(ReportBaseData data) {
		BigDecimal[] counts = {BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO};
		for(ServiceChargeTransactionLog sctl:data.getServiceChargeTransactionLogs()){
			if(sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Confirmed)
					||sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Started)
					||sctl.getStatus().equals(CmFinoFIX.SCTLStatus_Distribution_Completed)){
				//Charge collected from Service
				counts[0] = counts[0].add(sctl.getCalculatedCharge());

				//dstv,visafone service charge
				if(ReportBaseData.serviceMap.get(sctl.getServiceID()).getServiceName().equals(ServiceAndTransactionConstants.SERVICE_PAYMENT)
						&&ReportBaseData.transactiontypeMap.get(sctl.getTransactionTypeID()).getTransactionName().equals(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY)){
					counts[1] = counts[1].add(sctl.getCalculatedCharge());
				}
				if(ReportBaseData.serviceMap.get(sctl.getServiceID()).getServiceName().equals(ServiceAndTransactionConstants.SERVICE_BUY)
						&&ReportBaseData.transactiontypeMap.get(sctl.getTransactionTypeID()).getTransactionName().equals(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE)){
					counts[2] = counts[2].add(sctl.getCalculatedCharge());
				}
				//Cash received to eaZymoney ( Bank )
				if(data.getCtMap().containsKey(sctl.getCommodityTransferID())){
					counts[3] = counts[3].add(getTaxPaid(sctl,data));	
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
		MFSIncomeReport cfs = new MFSIncomeReport();
		cfs.run(new Date(), new Date(), null);
	}
}