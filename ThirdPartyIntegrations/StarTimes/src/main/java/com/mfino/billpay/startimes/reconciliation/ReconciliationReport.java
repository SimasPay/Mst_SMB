package com.mfino.billpay.startimes.reconciliation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.domain.BillPayments;
import com.mfino.domain.IntegrationSummary;
import com.mfino.mce.backend.IntegrationSummaryService;
import com.mfino.service.BillPaymentsService;
import com.mfino.util.DateUtil;

public class ReconciliationReport {

	private Log log = LogFactory.getLog(this.getClass());

	private String reportDirPath;
	private String systemID;
	private String billerCode;
	private List<Integer> billPayStatuses;
	private final String totalAmountString = "$TotalAmount";
	private BillPaymentsService billPaymentsService;
	private IntegrationSummaryService integrationSummaryService;

	public File generate(Date start, Date end) {
		log.info("ReconciliationReport::generate():begin");
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		DateFormat transactionTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		File report = new File(reportDirPath + "dz-" + systemID + "-"+ df.format(start) + ".req");
		File file = new File(reportDirPath + df.format(start) + "-temp");
		try {
			PrintWriter writer = new PrintWriter(file);

			BillPaymentsQuery billPaymentsQuery = new BillPaymentsQuery();
			billPaymentsQuery.setBillerCode(billerCode);
			billPaymentsQuery.setBillPayStatuses(billPayStatuses);
			billPaymentsQuery.setCreateTimeGE(start);
			billPaymentsQuery.setCreateTimeLT(end);
			List<BillPayments> billPayments = billPaymentsService.get(billPaymentsQuery);

			writer.write(transactionTimeFormat.format(start) + "\t"
					+ transactionTimeFormat.format(DateUtils.addSeconds(end,-1)) + "\t"
					+ billPayments.size() + "\t" + totalAmountString + "\n");

			BigDecimal amount = BigDecimal.ZERO;
			IntegrationSummary integrationSummary;
			for (BillPayments billPay : billPayments) {
				integrationSummary = integrationSummaryService.getIntegrationSummary(billPay.getSctlId(), 0l);
				String transactionNo = "";
				String orderno = "";
				String customerID = "";// not defined yet
				String smartCardNo = "";
				String transactionAmount = "";
				if (integrationSummary != null) {
					if (integrationSummary.getReconcilationID1() != null)
						transactionNo = integrationSummary.getReconcilationID1();
					if (integrationSummary.getReconcilationID2() != null)
						orderno = integrationSummary.getReconcilationID2();
				}
				if (billPay.getInvoiceNumber() != null)
					smartCardNo = billPay.getInvoiceNumber();
				if (billPay.getAmount() != null)
					transactionAmount = billPay.getAmount().setScale(0, BigDecimal.ROUND_DOWN).toString();

				writer.write(transactionNo + "\t" + orderno + "\t"
						+ transactionAmount + "\t" + customerID + "\t"
						+ smartCardNo + "\t"
						+ transactionTimeFormat.format(billPay.getCreateTime())
						+ "\n");
				amount = amount.add(billPay.getAmount());
			}
			writer.close();
			replaceTotalAmount(report, file, amount);
			file.delete();
		} catch (FileNotFoundException e) {
			log.error("ERROR:", e);
			return null;
		} catch (IOException e) {
			log.error("ERROR:", e);
			return null;
		}
		log.info("ReconciliationReport::generate():end");
		return report;
	}

	private void replaceTotalAmount(File report, File temp, BigDecimal amount)
			throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(temp));
		PrintWriter out = new PrintWriter(report);
		String line = in.readLine();
		line = line.replace(totalAmountString,
				amount.setScale(0, BigDecimal.ROUND_DOWN).toString());
		while (line != null) {
			out.write(line + "\n");
			line = in.readLine();
		}
		in.close();
		out.close();
	}

	public String getReportDirPath() {
		return reportDirPath;
	}

	public void setReportDirPath(String reportDirPath) {
		this.reportDirPath = reportDirPath;
	}

	public String getSystemID() {
		return systemID;
	}

	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}

	public String getBillerCode() {
		return billerCode;
	}

	public void setBillerCode(String billerCode) {
		this.billerCode = billerCode;
	}

	public List<Integer> getBillPayStatuses() {
		return billPayStatuses;
	}

	public void setBillPayStatuses(List<Integer> billPayStatuses) {
		this.billPayStatuses = billPayStatuses;
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

	public IntegrationSummaryService getIntegrationSummaryService() {
		return integrationSummaryService;
	}

	public void setIntegrationSummaryService(
			IntegrationSummaryService integrationSummaryService) {
		this.integrationSummaryService = integrationSummaryService;
	}

}
