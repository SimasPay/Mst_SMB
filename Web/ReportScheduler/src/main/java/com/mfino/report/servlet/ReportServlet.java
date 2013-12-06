/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.report.amlreports.AccountProfileChangeReport;
import com.mfino.report.amlreports.B2E2BTransactionReport;
import com.mfino.report.amlreports.DailyLimitUtilizationReport;
import com.mfino.report.amlreports.KinInformationMissingAccountReport;
import com.mfino.report.amlreports.OverLimitTransactionReport;
import com.mfino.report.amlreports.RepeatedTransactionsPerTransactionTypeReport;
import com.mfino.report.amlreports.UpdatedAccountsReport;
import com.mfino.report.amlreports.ZMMAccountDeactiveReport;
import com.mfino.report.base.OfflineReportBase;
import com.mfino.report.financial.ACCSnapShot;
import com.mfino.report.financial.CashFlowStatement;
import com.mfino.report.financial.MFSIncomeReport;
import com.mfino.report.generalreports.AgentSalesCommisionReport;
import com.mfino.report.generalreports.AggregatevalueAndTransactionsperSubscriberReport;
import com.mfino.report.generalreports.ConsolidatedSalesReport;
import com.mfino.report.generalreports.CustomerRegistrationReport;
import com.mfino.report.generalreports.EndofDayProcessReport;
import com.mfino.report.generalreports.FundMovementReport;
import com.mfino.report.generalreports.MobileMoneyFailedTransactionReport;
import com.mfino.report.generalreports.MobileMoneyRepeatedTransactionsReport;
import com.mfino.report.generalreports.MoneyAvailableReport;
import com.mfino.report.generalreports.PartnerTransactionReport;
import com.mfino.report.generalreports.PendingTransactionReport;
import com.mfino.report.generalreports.ResolvedTransactionReport;
import com.mfino.report.generalreports.ServiceChargeReport;
import com.mfino.report.generalreports.SubscriberClassificationReport;
import com.mfino.report.generalreports.SummaryReport;
import com.mfino.report.generalreports.TransactionReport;
import com.mfino.report.generalreports.UserRolesAndRightsReport;

public class ReportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
		dispatcher.forward(request, response);
	}

	
	private void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/result.jsp");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String reportType=request.getParameter("reportType");
		
		try{
		Date startdate = dateFormat.parse(request.getParameter("start"));
		Date enddate = dateFormat.parse(request.getParameter("end"));
		int reporttype = Integer.valueOf(reportType);
		OfflineReportBase report;
		switch (reporttype) {
		case 1:
			 report = new ConsolidatedSalesReport();
			break;
		case 2:
			report = new CustomerRegistrationReport();
			break;
		case 3:
			 report = new MoneyAvailableReport();
			break;
		case 4:
			report = new PendingTransactionReport();
			break;
		case 5:
			 report = new AgentSalesCommisionReport();
			break;
		case 6:
			report = new ServiceChargeReport();
			break;	
		case 7:
			report = new TransactionReport();
			break;	
		case 8:
			report = new FundMovementReport();
			break;
		case 9:
			report = new MobileMoneyFailedTransactionReport();
			break;
		case 10:
			report = new UserRolesAndRightsReport();
			break;
		case 11:
			report = new SummaryReport();
			break;	
		case 12:
			report = new SubscriberClassificationReport();
			break;	
		case 13:
			report = new PartnerTransactionReport();
			break;
		case 14:
			report = new AggregatevalueAndTransactionsperSubscriberReport();
			break;	
		case 15:
			report = new ResolvedTransactionReport();
			break;
		case 16:
			report = new MobileMoneyRepeatedTransactionsReport();
			break;
		case 17:
			report = new RepeatedTransactionsPerTransactionTypeReport();
			break;
		case 18:
			report = new OverLimitTransactionReport();
			break;
		case 19:
			report = new ZMMAccountDeactiveReport();
			break;
		case 20:
			report = new AccountProfileChangeReport();
			break;
		case 21:
			report = new UpdatedAccountsReport();
			break;
		case 22:
			report = new DailyLimitUtilizationReport();
			break;
		case 23:
			report = new KinInformationMissingAccountReport();
			break;
		case 24:
			report = new B2E2BTransactionReport();
			break;
		case 25:
			report = new CashFlowStatement();
			break;	
		case 26:
			report = new MFSIncomeReport();
			break;
		case 27:
			report = new ACCSnapShot();
			break;
		case 28:
			report = new EndofDayProcessReport();
			break;
		default:
			report = null;
		}
		if(report!=null){
			List<File> files=processReport(report,startdate,enddate);
			String path="";
			for(File file:files){
				path=path+file.getName()+",";
			}
			request.setAttribute("path", path);
			request.setAttribute("response", 0);
		}else{
			request.setAttribute("response", 1);
		}
		
	}catch (Exception exp) {
		log.error("Exception ",exp);
		exp.printStackTrace();
		request.setAttribute("response", 1);
	}
	dispatcher.forward(request, response);
	}
	
	 public List<File> processReport(OfflineReportBase report,  Date startdate, Date enddate){ 	
	    		//log.info("ThreadPool ID: " + threadPool.toString());
					List<File> file =new ArrayList<File>();
					if (report.hasMultipleReports()) {
		              file=   report.executeMutlipleReports(startdate, enddate,null);
		            } else {
		           File  file2 =   report.executeReport(startdate, enddate,null);
		           file.add(file2);
		            }
//					File dir = new File("webapps/ReportScheduler/report");
//					if(!dir.exists()){
//						dir.mkdir();
//					}
//					for(File sorceFile:file){
//						File destFile = new File(dir.getPath()+"/"+report.getFileName());
//						try {
//							copyFile(sorceFile, destFile);
//						} catch (IOException e) {
//							log.error("Exception",e);
//						}
//					}
					return file;
				}
	 public static void copyFile(File source, File dest) throws IOException{
		 		dest.createNewFile();
		 		InputStream in = null;
		 		OutputStream out = null;
		 		try{
		 			in = new FileInputStream(source);
		 			out = new FileOutputStream(dest);
		 			byte[] buf = new byte[1024];
		 			int len;
		 			while((len = in.read(buf)) > 0){
		 				out.write(buf, 0, len);
		 				}
		 			}
		 		finally{
		 			in.close();
		 			out.close();
		 			}
		 }


}
