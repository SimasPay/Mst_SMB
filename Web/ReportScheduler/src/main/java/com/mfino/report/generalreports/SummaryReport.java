package com.mfino.report.generalreports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.SubscriberQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.base.ReportBaseData;
import com.mfino.report.pdf.PDFReport;
import com.mfino.service.PartnerService;
import com.mfino.service.SystemParametersService;
import com.mfino.util.ExcelUtil;
import com.mfino.util.ReportUtil;

public class SummaryReport extends OfflineReportBase {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private File reportFile;
	private File pdfReportFile;
	private SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
	private PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
	private String reportName;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;

	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {
		reportFile = ReportUtil.getReportFilePath(reportName,end,ReportUtil.EXCEL_EXTENTION);
		pdfReportFile =  ReportUtil.getReportFilePath(reportName,end,ReportUtil.PDF_EXTENTION);

		DateFormat df = getDateFormat();
		String startTime = df.format(new Date());
		log.info("processing "+reportName+" StartTime:"+startTime);
		//String[] HEADER_COLUMN = {"#" , "Date"," Total No.Of subscribers   "," Registrations for above period "," No. Of Transacting Subscribers "," Total No.Of Agents ","No. Of Transacting Agents "," Total No.Of Partners ","No. Of Transacting Partners "," Global Wallet Balance"," No. of Successful Transactions"," No. Of Failed Transactions"," Cash-in Transactions"," Cash-in Amount"," Cash-out Transactions"," Cash-out Amount"
		//		," BankToEWallet Transactions"," BankToEWallet Amount"," EWalletToBank Transactions"," EWalletToBank Amount "," BillPayment Transactions "," BillPayment Amount"," EWalletToEWallet Transactions "," EWalletToEWallet Amount"};

		String[] HEADER_COLUMN = {"#" , "Date","No.Of subscribers","No. Of subscribers registered","No. Of subscribers Used","Total SVA Value"," No. of Successful Transactions"," No. Of Failed Transactions"," Cash-in Transactions"," Cash-in Amount"," Cash-out Transactions"," Cash-out Amount"
				," BankToEWallet Transactions"," BankToEWallet Amount"," EWalletToBank Transactions"," EWalletToBank Amount "," BillPayment Transactions "," BillPayment Amount"," EWalletToEWallet Transactions "," EWalletToEWallet Amount"};


		try {
			if(data==null){
				data = new ReportBaseData();
				data.intializeStaticData();
				data.getCommodityTransactions(start, end);
			}
			int seq = 1;
			String date = df.format(start)+" to "+df.format(end);
			SubscriberQuery query = new SubscriberQuery();
			query.setCreateTimeGE(start);
			query.setCreateTimeLT(end);
			List<Subscriber> registeredSubscribers = subscriberDao.get(query);
			int regsubcount = registeredSubscribers!=null?registeredSubscribers.size():0;
			Long globalPocketId = systemParametersService.getLong(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY);
			Pocket globalPocket = pocketDao.getById(globalPocketId);
			List<CommodityTransfer> ctList = data.getCtList();

			BigDecimal[] counts = getTransactionCountsAndAmounts(ctList);
			BigDecimal[] subcounts = getSubscriberCounts();
			String[] dataColumn ={String.valueOf(seq),
					date,
					subcounts[0].toString(),
					String.valueOf(regsubcount),
					counts[14].toString(),
					/*	subcounts[1].toString(),
					counts[15].toString(),
					subcounts[2].toString(),
					counts[16].toString(), */
					globalPocket.getCurrentBalance().toString(),
					counts[0].toString(),
					counts[1].toString(),
					counts[2].toString(),
					counts[3].toString(),
					counts[4].toString(),
					counts[5].toString(),
					counts[6].toString(),
					counts[7].toString(),
					counts[8].toString(),
					counts[9].toString(),
					counts[10].toString(),
					counts[11].toString(),
					counts[12].toString(),
					counts[13].toString()
			};

			HSSFWorkbook hwb=new HSSFWorkbook();
			HSSFSheet sheet =  hwb.createSheet(reportName);
			sheet.setDisplayGridlines(false);
			sheet.setPrintGridlines(false);

			PDFReport pdf = new PDFReport(pdfReportFile, reportName);

			Font font = ExcelUtil.getFont(hwb, false, reportName);

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

			pdf.createTable(2);
			sheet.addMergedRegion(new CellRangeAddress(0,3,0,1));
			HSSFRow row = sheet.createRow(5);	
			ExcelUtil.createHeaderCell(hwb,row,0).setCellValue(reportName);   
			//ExcelUtil.autoSizeColumn(hwb,2);		

			int index = 7;	

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
			FileOutputStream file = new FileOutputStream(reportFile);
			hwb.write(file);
			file.close();

			//creating pdf report
			//	XLSReport.convertXlsTopdf(reportFile,pdfReportFile);

		} catch (Exception e) {
			log.error("Error in "+reportName+":", e);
		} 
		log.info("processing "+reportName+" StartTime:"+startTime+" EndTime:"+df.format(new Date()));
		return reportFile;
	}

	private Cell createCell(HSSFWorkbook hwb, HSSFRow row, int index, Font font,int border, int thickness) {
		return ExcelUtil.createCell(hwb, row, index, font, border, thickness);
	}


	private BigDecimal[] getSubscriberCounts() {
		List<Subscriber> subscriber = subscriberDao.getAll();
		int subcount = 0;
		int agents = 0;
		int partners = 0;
		for(Subscriber sub:subscriber){
			if(CmFinoFIX.SubscriberType_Subscriber.equals(sub.getType())){
				subcount++;
				continue;
			}else{
				Iterator<Partner>  it = sub.getPartnerFromSubscriberID().iterator();
				if(it.hasNext()){
					Partner partner=it.next();
					if(partnerService.isAgentType(partner.getBusinessPartnerType())){
						agents++;
					}else{
						partners++;
					}
				}
			}
		}
		BigDecimal[] count= {new BigDecimal(subcount),new BigDecimal(agents),new BigDecimal(partners)};
		return count;
	}



	private BigDecimal[] getTransactionCountsAndAmounts(
			List<CommodityTransfer> ctList) {
		int successCount = 0;
		int failcount = 0;
		int cashinCount = 0;
		BigDecimal cashinAmount =BigDecimal.ZERO;
		int cashoutCount = 0;
		BigDecimal cashoutAmount =BigDecimal.ZERO;
		int b2eCount = 0;
		BigDecimal b2eAmount =BigDecimal.ZERO;
		int e2bCount = 0;
		BigDecimal e2bAmount =BigDecimal.ZERO;
		int billPaymentCount = 0;
		BigDecimal billPaymentAmount =BigDecimal.ZERO;
		int e2eCount = 0;
		BigDecimal e2eAmount =BigDecimal.ZERO;
		List<Long> usedSubscribers= new ArrayList<Long>();
		List<Long> usedAgents= new ArrayList<Long>();
		List<Long> userPartners= new ArrayList<Long>();

		SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
		for(CommodityTransfer ct:ctList){
			if(CmFinoFIX.TransactionsTransferStatus_Completed.equals(ct.getTransferStatus())){
				successCount++;
				if(CmFinoFIX.TransactionUICategory_EMoney_CashIn.equals(ct.getUICategory())){
					cashinCount++;
					cashinAmount = cashinAmount.add(ct.getAmount());
				}else if(CmFinoFIX.TransactionUICategory_EMoney_CashOut.equals(ct.getUICategory())){
					cashoutCount++;
					cashoutAmount =cashoutAmount.add(ct.getAmount());
				}else if(CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf.equals(ct.getUICategory())
						||(CmFinoFIX.PocketType_BankAccount.equals(ct.getSourcePocketType())&&CmFinoFIX.PocketType_SVA.equals(ct.getDestPocketType()))){
					b2eCount++;
					b2eAmount = b2eAmount.add(ct.getAmount());
				}else if(CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf.equals(ct.getUICategory())
						||(CmFinoFIX.PocketType_SVA.equals(ct.getSourcePocketType())&&CmFinoFIX.PocketType_BankAccount.equals(ct.getDestPocketType()))){
					e2bCount++;
					e2bAmount = e2bAmount.add(ct.getAmount());
				}
				if(CmFinoFIX.TransactionUICategory_Bill_Payment.equals(ct.getUICategory())
						||CmFinoFIX.TransactionUICategory_EMoney_Purchase.equals(ct.getUICategory())){
					billPaymentCount++;
					billPaymentAmount = billPaymentAmount.add(ct.getAmount());
				}
				if(CmFinoFIX.TransactionUICategory_EMoney_EMoney_Trf.equals(ct.getUICategory()) && 
						(ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER.equals(ct.getSourceMessage())
								|| ServiceAndTransactionConstants.MESSAGE_TRANSFER_UNREGISTERED.equals(ct.getSourceMessage()))){
					e2eCount++;
					e2eAmount = e2eAmount.add(ct.getAmount());
				}
			}else{
				failcount++;
			}
			Subscriber sub = subscriberDAO.getById(ct.getSubscriberBySourceSubscriberID().getID());
			if(CmFinoFIX.SubscriberType_Subscriber.equals(sub.getType())&&(!usedSubscribers.contains(sub.getID()))){
				usedSubscribers.add(sub.getID());
			}else if(!CmFinoFIX.SubscriberType_Subscriber.equals(sub.getType())){
				Partner partner = sub.getPartnerFromSubscriberID().iterator().next();
				if(partnerService.isAgentType(partner.getBusinessPartnerType())){
					if(!usedAgents.contains(sub.getID())){
						usedAgents.add(sub.getID());
					}
				}else if(!userPartners.contains(sub.getID())){
					userPartners.add(sub.getID());
				}
			}
		}		
		BigDecimal[] counts ={new BigDecimal(successCount),new BigDecimal(failcount),new BigDecimal(cashinCount),cashinAmount
				,new BigDecimal(cashoutCount),cashoutAmount,new BigDecimal(b2eCount),b2eAmount,new BigDecimal(e2bCount),e2bAmount
				,new BigDecimal(billPaymentCount),billPaymentAmount,new BigDecimal(e2eCount),e2eAmount,new BigDecimal(usedSubscribers.size())
		,new BigDecimal(usedAgents.size()),new BigDecimal(userPartners.size())};
		return counts;
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
