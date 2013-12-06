package com.mfino.report.generalreports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ReportParameterKeys;
import com.mfino.dao.ChargeTypeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.ChargeType;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.pdf.PDFReport;
import com.mfino.service.PartnerService;
import com.mfino.util.ExcelUtil;
import com.mfino.util.ReportUtil;

public class EndofDayProcessReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	private File reportFile;
	private File pdfReportFile;
	private ChargeTypeDAO chargeTypeDAO = DAOFactory.getInstance().getChargeTypeDAO();

	private Map<Long,List<CommodityTransfer>> agentSettlements = new HashMap<Long, List<CommodityTransfer>>();
	private Map<Long,List<CommodityTransfer>> billerSettlements = new HashMap<Long, List<CommodityTransfer>>();
	private String reportName;

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected List<File> runAndGetMutlipleReports(Date start, Date end, ReportBaseData data) {
		reportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		pdfReportFile =ReportUtil.getReportFilePath(reportName,end,ReportUtil.PDF_EXTENTION);
		List<File> reports = new ArrayList<File>();
		DateFormat df = getDateFormat();
		String startTime = df.format(new Date());
		log.info("processing "+reportName+" StartTime:"+startTime);
		String[] HEADER_COLUMN = { "Date"," Charges collected "," Commissions paid out "," Agents settled "," Agents settled Amount ",
				" Billers settled "," Billers settled Amount ","No. Of Pending transactions "," Pending transactions balance"};
		try {
			if(data==null){
				data = new ReportBaseData();
				data.intializeStaticData();
				data.getCommodityTransactions(start, end);
				data.getServiceTransactionLogs(start, end);
				data.getPendingCommodityTransactions(start, end);
			}
			//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
			String date = df.format(start)+" to "+df.format(end);
			List<CommodityTransfer> ctList = data.getCtList();		
			ChargeType serChargeType = chargeTypeDAO.getChargeTypeByName(ReportParameterKeys.SERVICECHARGE);
			ChargeType commision = chargeTypeDAO.getChargeTypeByName(ReportParameterKeys.COMMISION);
			HashMap<Long, BigDecimal> chageTypeFunds = getChargeTypeFunds(data,data.getServiceChargeTransactionLogs());
			BigDecimal[] counts = getTransactionCountsAndAmounts(ctList);
			BigDecimal[] pendingCounts = getPendingCounts(data.getServiceChargeTransactionLogs());
			String[] dataColumn ={date,
					chageTypeFunds.get(serChargeType.getID())!=null?chageTypeFunds.get(serChargeType.getID()).toString():"0",
							chageTypeFunds.get(commision.getID())!=null?chageTypeFunds.get(commision.getID()).toString():"0",
									counts[0].toString(),
									counts[1].toString(),
									counts[2].toString(),
									counts[3].toString(),
									pendingCounts[0].toString(),
									pendingCounts[1].toString()
			};

			//				HEADER_COLUMN = copyData(HEADER_COLUMN,dataColumn);


			HSSFWorkbook hwb=new HSSFWorkbook();
			HSSFSheet sheet =  hwb.createSheet(reportName);
			sheet.setDisplayGridlines(false);
			sheet.setPrintGridlines(false);

			Font font = ExcelUtil.getFont(hwb, false, reportName);

			File pdfReportFilePath =ReportUtil.getReportFilePath(reportName,end,ReportUtil.PDF_EXTENTION);
			PDFReport pdf = new PDFReport(pdfReportFilePath, reportName);

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
			HSSFRow row = sheet.createRow(3);	
			ExcelUtil.createHeaderCell(hwb,row,1).setCellValue(reportName);   
			ExcelUtil.autoSizeColumn(hwb,2);		

			int index = 7;	

			pdf.createTable(2);

			for(int i=0;i<HEADER_COLUMN.length;i++){
				row = sheet.createRow(index);
				createCell(hwb, row, 0, font, 15, 0).setCellValue(HEADER_COLUMN[i]);
				createCell(hwb, row, 1, font, 15, 0).setCellValue(dataColumn[i]);
				pdf.addRowContent(HEADER_COLUMN[i]+","+dataColumn[i]);
				index++;
			}

			pdf.closePdfReport();
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);

			//writer.close();
			FileOutputStream file = new FileOutputStream(reportFile);
			hwb.write(file);
			file.close();

			//creating pdf report
			//	XLSReport.convertXlsTopdf(reportFile,pdfReportFile);	

			log.info("processing "+reportName+" StartTime:"+startTime+" EndTime:"+df.format(new Date()));
			File agentsettlement = generateAgentSettlementReport(start,end);
			File partnersettlement = generatePartnerSettlementReport(start,end);
			if(agentsettlement!=null){
				reports.add(agentsettlement);
			}
			if(partnersettlement!=null){
				reports.add(partnersettlement);
			}
		} catch (Exception e) {
			log.error("Error in "+reportName+" :",e);
		} 
		reports.add(reportFile);
		return reports;
	}


	private File generateAgentSettlementReport(Date start,Date end) {
		AgentsSettlementReport agentsettlement = new AgentsSettlementReport();		
		return agentsettlement.run(start,end, agentSettlements);

	}

	private File generatePartnerSettlementReport(Date start, Date end) {
		PartnersSettlementReport partnersSettlementReport = new PartnersSettlementReport();
		return partnersSettlementReport.run(start, end, billerSettlements);
	}

	private BigDecimal[] getPendingCounts(List<ServiceChargeTransactionLog> serviceChargeTransactionLogs) {
		int pendingCount=0;
		BigDecimal pendingAmount = BigDecimal.ZERO;
		if(serviceChargeTransactionLogs!=null){
			for(ServiceChargeTransactionLog st: serviceChargeTransactionLogs){
				if(st.getStatus().equals(CmFinoFIX.SCTLStatus_Processing)){				
					pendingCount++;
					pendingAmount= pendingAmount.add(st.getTransactionAmount().add(st.getCalculatedCharge()));
					continue;
				}
			}
		}
		BigDecimal[] counts ={ new BigDecimal(pendingCount),pendingAmount};
		return counts;
	}

	public BigDecimal[] getTransactionCountsAndAmounts(List<CommodityTransfer> ctList) {
		int agentCount= 0 ;
		BigDecimal agentamount = BigDecimal.ZERO;
		int billercount = 0 ;
		BigDecimal billerAmount = BigDecimal.ZERO;
		Subscriber sub;
		Partner partner;
		List<CommodityTransfer> commodity;
		for(CommodityTransfer ct:ctList){
			if(ct.getTransferStatus().equals(CmFinoFIX.TransferStatus_Completed)&&ct.getUICategory().equals(CmFinoFIX.TransactionUICategory_Settlement_Of_Charge)){
				sub = ct.getSubscriberBySourceSubscriberID();
				partner = sub.getPartnerFromSubscriberID().iterator().next();
				if(partnerService.isAgentType(partner.getBusinessPartnerType())){
					agentCount++;
					agentamount = agentamount.add(ct.getAmount());
					if(agentSettlements.containsKey(partner.getID())){
						commodity = agentSettlements.get(partner.getID());
					}else{
						commodity = new ArrayList<CommodityTransfer>();
					}
					commodity.add(ct);
					agentSettlements.put(partner.getID(), commodity);
				}else{
					billercount++;
					billerAmount= billerAmount.add(ct.getAmount());
					if(billerSettlements.containsKey(partner.getID())){
						commodity = billerSettlements.get(partner.getID());
					}else{
						commodity = new ArrayList<CommodityTransfer>();
					}
					commodity.add(ct);
					billerSettlements.put(partner.getID(), commodity);
				}
			}
		}
		BigDecimal[] count = {new BigDecimal(agentCount),agentamount,new BigDecimal(billercount),billerAmount}; 		
		return count;
	}

	private Cell createCell(HSSFWorkbook hwb, HSSFRow row, int index, Font font,int border, int thickness) {
		return ExcelUtil.createCell(hwb, row, index, font, border, thickness);
	}

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end,
			ReportBaseData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasMultipleReports(){
		return true;
	}

	@Override
	public String getFileName() {
		return reportFile.getName();
	}

	@Override
	public void setReportName(String reportName) {
		this.reportName =reportName;
	}

	public Map<Long, List<CommodityTransfer>> getAgentSettlements() {
		return agentSettlements;
	}


	public Map<Long, List<CommodityTransfer>> getBillerSettlements() {
		return billerSettlements;
	}

}
