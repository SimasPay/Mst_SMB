package com.mfino.report.generalreports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.report.base.ReportBaseData;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.util.ReportUtil;


public class CBNReports extends  OfflineReportBase{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private TransactionReport transactionReport = new TransactionReport();
	private SubscriberClassificationReport subscriberClassificationReport = new SubscriberClassificationReport();
	private AgentClassificationReport agentClassificationReport = new AgentClassificationReport();
	private MoneyAvailableReport moneyAvailableReport = new MoneyAvailableReport();
	private List<File> trList;
	private List<File> scrList;
	private List<File> acrList;
	private List<File> marList;
	private File tr;
	private File scr;
	private File acr;
	private File mar;
	private ZipOutputStream pdfZip;
	private ZipOutputStream XLZip;
	private String reportName;
	private File pdfZipFile;
	private File XLZipFile;



	@Override
	@Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	protected File run(Date start, Date end, ReportBaseData data) {
		DateFormat df = getDateFormat();
		String startTime = df.format(new Date());
		log.info("Processing "+reportName+" Starttime: "+ startTime);

		transactionReport.setReportName("TransactionReport");
		if(transactionReport.hasMultipleReports()){
			trList = transactionReport.executeMutlipleReports(start, end, data);
		}else{
			tr = transactionReport.executeReport(start, end, data);
		}

		subscriberClassificationReport.setReportName("Subscriber Classification Report");
		if(subscriberClassificationReport.hasMultipleReports()){
			scrList = subscriberClassificationReport.executeMutlipleReports(start, end, data);
		}else{
			scr = subscriberClassificationReport.executeReport(start, end, data);
		}
		agentClassificationReport.setReportName("Agent Classification Report");
		if(agentClassificationReport.hasMultipleReports()){
			acrList = agentClassificationReport.executeMutlipleReports(start, end, data);
		}else{
			acr = agentClassificationReport.executeReport(start, end, data);
		}
		moneyAvailableReport.setReportName("Money Available Report");
		if(moneyAvailableReport.hasMultipleReports()){
			marList = moneyAvailableReport.executeMutlipleReports(start, end, data);
		}else{
			mar = moneyAvailableReport.executeReport(start, end, data);
		}

		pdfZipFile = ReportUtil.getReportFilePath(reportName+"Pdf",end,ReportUtil.ZIP_EXTENTION);
		XLZipFile = ReportUtil.getReportFilePath(reportName+"XL",end,ReportUtil.ZIP_EXTENTION);


		log.info("Creating CBNReports Zip");
		createCBNReportsZip();
		log.info("Successfully Created CBNReports Zip");

		return pdfZipFile;
	}

	@Override
	protected List<File> runAndGetMutlipleReports(Date start, Date end,
			ReportBaseData data) {
		return null;
	}


	private void createCBNReportsZip(){
		try{
			XLZip = new ZipOutputStream(new FileOutputStream(XLZipFile));
			XLZip.setLevel(Deflater.DEFAULT_COMPRESSION);
			pdfZip = new ZipOutputStream(new FileOutputStream(pdfZipFile));
			pdfZip.setLevel(Deflater.DEFAULT_COMPRESSION);
			if(tr != null){
				addXlAndPdfFilesToZip(tr);
			}
			if(scr != null){
				addXlAndPdfFilesToZip(scr);
			}
			if(acr != null){
				addXlAndPdfFilesToZip(acr);
			}
			if(mar != null){
				addXlAndPdfFilesToZip(mar);
			}

			addFileListToZip(scrList);
			addFileListToZip(acrList);
			addFileListToZip(marList);
			addFileListToZip(trList);
			XLZip.close();
			pdfZip.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void addFileListToZip(List<File> fList){
		if(fList != null){
			for(File f : fList){
				addXlAndPdfFilesToZip(f);
			}
		}

	}

	private void addXlAndPdfFilesToZip(File f){
		//adding XL file to Zip
		addFileToZip(f,XLZip);

		//Get the Pdf file corresponding to XL file
		String xlsFileName = f.getAbsolutePath();
		int extIndex = xlsFileName.indexOf(".xls");
		String fileNameNoExt = xlsFileName.substring(0,extIndex);
		String pdfFileName = fileNameNoExt+".pdf";
		File pdfFile = new File(pdfFileName);

		//add Pdf file to Zip
		addFileToZip(pdfFile,pdfZip);
	}

	private void addFileToZip(File f, ZipOutputStream out){
		try{
			FileInputStream in = new FileInputStream(f);
			out.putNextEntry(new ZipEntry(f.getName()));
			byte[] buffer = new byte[18024];
			int len;
			while ((len = in.read(buffer)) > 0){
				out.write(buffer, 0, len);
			}
			out.closeEntry();
			in.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReportName(String reportName) {
		this.reportName = reportName;

	}

	public static void main(String args[]){
		try{
			CBNReports cbn = new CBNReports();
			cbn.setReportName("cbnreports");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date startDate = dateFormat.parse("01/02/2012");
			Date endDate = dateFormat.parse("02/02/2012");
			cbn.run(startDate, endDate, null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
