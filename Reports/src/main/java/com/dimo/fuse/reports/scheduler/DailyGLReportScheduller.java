package com.dimo.fuse.reports.scheduler;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dimo.fuse.reports.FtpService;
import com.dimo.fuse.reports.db.QueryExecutor;

public class DailyGLReportScheduller{
	private static Logger log = LoggerFactory.getLogger("DailyGLReportScheduller");
	
	private Date startTime;
	private Date endTime;
	private FtpService ftpService;
	
	private static final String OMNIBUS_TO_NONKYC_QUERY = "SELECT "
				+ "SUM(ct.amount) "
			+ "FROM "
				+ "COMMODITY_TRANSFER ct, "
				+ "SERVICE_CHARGE_TXN_LOG sctl, "
				+ "POCKET pocket "
			+ "WHERE "
				+ "ct.TRANSACTIONID = sctl.TRANSACTIONID "
				+ "AND ct.DESTPOCKETID = pocket.ID "
				+ "AND sctl.STATUS = 4 "
				+ "AND sctl.LASTUPDATETIME > '${startDate}' "
				+ "AND sctl.LASTUPDATETIME <= '${endDate}' "
				+ "AND pocket.POCKETTEMPLATEID = (SELECT id FROM POCKET_TEMPLATE WHERE DESCRIPTION = 'Emoney-NoKyc') "
				+ "AND ( "
					+ "( sctl.TRANSACTIONTYPEID = (SELECT tt.id FROM TRANSACTION_TYPE tt WHERE tt.TRANSACTIONNAME = 'CashIn') AND sctl.SERVICEID = (SELECT s.id from SERVICE s WHERE s.SERVICENAME = 'Wallet') ) "
					+ "OR ( sctl.TRANSACTIONTYPEID = (SELECT tt.id FROM TRANSACTION_TYPE tt WHERE tt.TRANSACTIONNAME = 'B2ETransfer') AND sctl.SERVICEID = (SELECT s.id from SERVICE s WHERE s.SERVICENAME = 'Bank') ) "
				+ ")";
	
	private static final String OMNIBUS_TO_KYC_QUERY = "SELECT "
				+ "SUM(ct.amount) "
			+ "FROM "
				+ "COMMODITY_TRANSFER ct, "
				+ "SERVICE_CHARGE_TXN_LOG sctl, "
				+ "POCKET pocket "
			+ "WHERE "
				+ "ct.TRANSACTIONID = sctl.TRANSACTIONID "
				+ "AND ct.DESTPOCKETID = pocket.ID "
				+ "AND sctl.STATUS = 4 "
				+ "AND sctl.LASTUPDATETIME > '${startDate}' "
				+ "AND sctl.LASTUPDATETIME <= '${endDate}' "
				+ "AND pocket.POCKETTEMPLATEID = (SELECT id FROM POCKET_TEMPLATE WHERE DESCRIPTION = 'Emoney-UnBanked') "
				+ "AND ( "
					+ "( sctl.TRANSACTIONTYPEID IN (SELECT tt.id FROM TRANSACTION_TYPE tt WHERE tt.TRANSACTIONNAME IN ('CashIn', 'CashWithdrawalRefund') ) AND sctl.SERVICEID = (SELECT s.id from SERVICE s WHERE s.SERVICENAME = 'Wallet') ) "
					+ "OR( sctl.TRANSACTIONTYPEID = (SELECT tt.id FROM TRANSACTION_TYPE tt WHERE tt.TRANSACTIONNAME = 'B2ETransfer') AND sctl.SERVICEID = (SELECT s.id from SERVICE s WHERE s.SERVICENAME = 'Bank') ) "
				+ ")";
	
	private static final String NONKYC_TO_OMNIBUS_QUERY = "SELECT "
				+ "SUM(ct.amount) "
			+ "FROM "
				+ "COMMODITY_TRANSFER ct, "
				+ "SERVICE_CHARGE_TXN_LOG sctl, "
				+ "POCKET pocket "
			+ "WHERE "
				+ "ct.TRANSACTIONID = sctl.TRANSACTIONID "
				+ "AND ct.SOURCEPOCKETID = pocket.ID "
				+ "AND sctl.STATUS = 4 "
				+ "AND sctl.LASTUPDATETIME > '${startDate}' "
				+ "AND sctl.LASTUPDATETIME <= '${endDate}' "
				+ "AND pocket.POCKETTEMPLATEID = (SELECT id FROM POCKET_TEMPLATE WHERE DESCRIPTION = 'Emoney-NoKyc') "
				+ "AND ( "
					+ "( sctl.TRANSACTIONTYPEID IN (SELECT tt.id FROM TRANSACTION_TYPE tt WHERE tt.TRANSACTIONNAME IN ('BillPay', 'QRPayment') ) AND sctl.SERVICEID = (SELECT s.id from SERVICE s WHERE s.SERVICENAME = 'Payment') ) "
					+ "OR( sctl.TRANSACTIONTYPEID = (SELECT tt.id FROM TRANSACTION_TYPE tt WHERE tt.TRANSACTIONNAME = 'AirtimePurchase') AND sctl.SERVICEID = (SELECT s.id from SERVICE s WHERE s.SERVICENAME = 'Buy') ) "
				+ ")";
	
	private static final String KYC_TO_OMNIBUS_QUERY = "SELECT "
				+ "SUM(ct.amount) "
			+ "FROM "
				+ "COMMODITY_TRANSFER ct, "
				+ "SERVICE_CHARGE_TXN_LOG sctl, "
				+ "POCKET pocket "
			+ "WHERE "
				+ "ct.TRANSACTIONID = sctl.TRANSACTIONID "
				+ "AND ct.SOURCEPOCKETID = pocket.ID "
				+ "AND sctl.STATUS = 4 "
				+ "AND sctl.LASTUPDATETIME > '${startDate}' "
				+ "AND sctl.LASTUPDATETIME <= '${endDate}' "
				+ "AND pocket.POCKETTEMPLATEID = (SELECT id FROM POCKET_TEMPLATE WHERE DESCRIPTION = 'Emoney-UnBanked') "
				+ "AND ( "
					+ "( sctl.TRANSACTIONTYPEID IN (SELECT tt.id FROM TRANSACTION_TYPE tt WHERE tt.TRANSACTIONNAME IN ('BillPay', 'QRPayment') ) AND sctl.SERVICEID = (SELECT s.id from SERVICE s WHERE s.SERVICENAME = 'Payment') ) "
					+ "OR( sctl.TRANSACTIONTYPEID = (SELECT tt.id FROM TRANSACTION_TYPE tt WHERE tt.TRANSACTIONNAME = 'AirtimePurchase') AND sctl.SERVICEID = (SELECT s.id from SERVICE s WHERE s.SERVICENAME = 'Buy') ) "
					+ "OR( sctl.TRANSACTIONTYPEID IN (SELECT tt.id FROM TRANSACTION_TYPE tt WHERE tt.TRANSACTIONNAME IN ('CashOutAtATM', 'TransferToUangku', 'E2BTransfer', 'InterBankTransfer')) AND sctl.SERVICEID = (SELECT s.id from SERVICE s WHERE s.SERVICENAME = 'Wallet') ) "
			+ ")";
	
	private static final String KYC_TO_NONKYC_QUERY = "SELECT "
				+ "SUM(ct.amount) "
			+ "FROM "
				+ "COMMODITY_TRANSFER ct, "
				+ "SERVICE_CHARGE_TXN_LOG sctl, "
				+ "POCKET pocket "
			+ "WHERE "
				+ "ct.TRANSACTIONID = sctl.TRANSACTIONID "
				+ "AND ct.DESTPOCKETID = pocket.ID "
				+ "AND sctl.STATUS = 4 "
				+ "AND sctl.LASTUPDATETIME > '${startDate}' "
				+ "AND sctl.LASTUPDATETIME <= '${endDate}' "
				+ "AND pocket.POCKETTEMPLATEID = (SELECT id FROM POCKET_TEMPLATE WHERE DESCRIPTION = 'Emoney-NoKyc') "
				+ "AND ( "
					+ "sctl.TRANSACTIONTYPEID IN (SELECT tt.id FROM TRANSACTION_TYPE tt WHERE tt.TRANSACTIONNAME IN ('E2ETransfer', 'TransferToUnregistered')) "
					+ "AND sctl.SERVICEID = (SELECT s.id from SERVICE s WHERE s.SERVICENAME = 'Wallet') "
				+ ")";
	
	private static final String NONKYC_TO_KYC_QUERY_UPGRADE = "SELECT "
				+ "POCKET_BALANCE "
			+ "FROM "
				+ "SUBSCRIBER_UPGRADE_BALANCE_LOG "
			+ "WHERE "
				+ "LASTUPDATETIME > '${startDate}' "
				+ "AND LASTUPDATETIME <= '${endDate}'";
	
	private static final String NONKYC_TO_KYC_QUERY_REVERSAL = "SELECT "
				+ "sctl.TRANSACTIONAMOUNT "
			+ "FROM "
				+ "SERVICE_CHARGE_TXN_LOG sctl "
			+ "WHERE "
				+ "LASTUPDATETIME > '${startDate}' "
				+ "AND LASTUPDATETIME <= '${endDate}' "
				+ "AND sctl.TRANSACTIONTYPEID = (SELECT tt.id FROM TRANSACTION_TYPE tt WHERE tt.TRANSACTIONNAME = 'FundReversal') "
				+ "AND sctl.SERVICEID = (SELECT s.id from SERVICE s WHERE s.SERVICENAME = 'Wallet') "
				+ "AND sctl.STATUS = 2";
	
	public void generate(){
		log.info("Scheduler Trigered, for the generation of Daily GL Reports");
		try {
			initaliseTimes();
			BigDecimal amtNonKycToOmnibus = getTotalAmount(NONKYC_TO_OMNIBUS_QUERY, null);
			BigDecimal amtKycToOmnibus = getTotalAmount(KYC_TO_OMNIBUS_QUERY, null);
			BigDecimal amtOmnibusToNonKyc = getTotalAmount(OMNIBUS_TO_NONKYC_QUERY, null);
			BigDecimal amtOmnibusToKyc = getTotalAmount(OMNIBUS_TO_KYC_QUERY, null);
			BigDecimal amtKycToNonKyc = getTotalAmount(KYC_TO_NONKYC_QUERY, null);

			JSONArray encryptedFields = new JSONArray("[1]");
			BigDecimal amtNonKycToKyc1 = getTotalAmount(NONKYC_TO_KYC_QUERY_REVERSAL, null);
			BigDecimal amtNonKycToKyc2 = getTotalAmount(NONKYC_TO_KYC_QUERY_UPGRADE, encryptedFields);
			BigDecimal amtNonKycToKyc = amtNonKycToKyc1.add(amtNonKycToKyc2);
			
			String[] omnibusToNonKycStruc = {ReportSchedulerProperties.getGlOmnibusAccount(), ReportSchedulerProperties.getGlDebitCurrency(), formatAmount(amtOmnibusToNonKyc), 
					ReportSchedulerProperties.getGlNonKycAccount(), ReportSchedulerProperties.getGlCreditCurrency(), "", "", "", ReportSchedulerProperties.getGlBranchCode()};

			String[] omnibusToKycStruc = {ReportSchedulerProperties.getGlOmnibusAccount(), ReportSchedulerProperties.getGlDebitCurrency(), formatAmount(amtOmnibusToKyc), 
					ReportSchedulerProperties.getGlKycAccount(), ReportSchedulerProperties.getGlCreditCurrency(), "", "", "", ReportSchedulerProperties.getGlBranchCode()};

			
			String[] nonKycToOmnibusStruc = {ReportSchedulerProperties.getGlNonKycAccount(), ReportSchedulerProperties.getGlDebitCurrency(), formatAmount(amtNonKycToOmnibus), 
					ReportSchedulerProperties.getGlOmnibusAccount(), ReportSchedulerProperties.getGlCreditCurrency(), "", "", "", ReportSchedulerProperties.getGlBranchCode()};

			String[] kycToOmnibusStruc = {ReportSchedulerProperties.getGlKycAccount(), ReportSchedulerProperties.getGlDebitCurrency(), formatAmount(amtKycToOmnibus), 
					ReportSchedulerProperties.getGlOmnibusAccount(), ReportSchedulerProperties.getGlCreditCurrency(), "", "", "", ReportSchedulerProperties.getGlBranchCode()};

			String[] nonKycToKyc = {ReportSchedulerProperties.getGlNonKycAccount(), ReportSchedulerProperties.getGlDebitCurrency(), formatAmount(amtNonKycToKyc), 
					ReportSchedulerProperties.getGlKycAccount(), ReportSchedulerProperties.getGlCreditCurrency(), "", "", "", ReportSchedulerProperties.getGlBranchCode()};

			String[] kycToNonKyc = {ReportSchedulerProperties.getGlKycAccount(), ReportSchedulerProperties.getGlDebitCurrency(), formatAmount(amtKycToNonKyc), 
					ReportSchedulerProperties.getGlNonKycAccount(), ReportSchedulerProperties.getGlCreditCurrency(), "", "", "", ReportSchedulerProperties.getGlBranchCode()};
			
			String omnibusToNonKycLine = StringUtils.join(omnibusToNonKycStruc, ',');
			String omnibusToKycLine = StringUtils.join(omnibusToKycStruc, ',');
			String nonKycToOmnibusLine = StringUtils.join(nonKycToOmnibusStruc, ',');
			String kycToOmnibusLine = StringUtils.join(kycToOmnibusStruc, ',');
			String nonKycToKycLine = StringUtils.join(nonKycToKyc, ',');
			String kycToNonKycLine = StringUtils.join(kycToNonKyc, ',');
			
			String[] completeReportStruc = {omnibusToNonKycLine, omnibusToKycLine, nonKycToOmnibusLine, kycToOmnibusLine, kycToNonKycLine, nonKycToKycLine};
			String reportContent = StringUtils.join(completeReportStruc, '\n');
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			String currTimeMillis = fmt.format(System.currentTimeMillis());
			
			String fileName = "/glreport_"+currTimeMillis+".txt";
			File file = new File(ReportSchedulerProperties.getScheduledReportsOutputDir()+fileName);
			FileUtils.writeStringToFile(file , reportContent);
			
			ftpService.sendThroughSftp(file.getAbsolutePath(), ReportSchedulerProperties.getGlReportRemoteDir()+fileName);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private BigDecimal getTotalAmount(String query, JSONArray encryptedFields)
	{
		BigDecimal totalAmount = BigDecimal.ZERO;
		QueryExecutor qe = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(ReportSchedulerProperties.getProperty("dateFormatInReportQuery"));
		try {
			Date startGMTDate = new Date(startTime.getTime() - TimeZone.getDefault().getRawOffset());
			Date endGMTDate = new Date(endTime.getTime() - TimeZone.getDefault().getRawOffset());
			
			query = StringUtils.replace(query, "${startDate}", dateFormat.format(startGMTDate));
			query = StringUtils.replace(query, "${endDate}", dateFormat.format(endGMTDate));
			
			qe = new QueryExecutor();
			if(encryptedFields != null)
				qe.setEncryptedFields(encryptedFields);
			ResultSet rs = qe.getResultSet(query);
			
			while (rs != null && rs.next()) {
				String[] rowContent = qe.fetchNextRowData(rs);
				if(rowContent != null && rowContent.length > 0){
					String content = rowContent[0];
					try {
						if(StringUtils.isNotBlank(content))
							totalAmount = totalAmount.add(new BigDecimal(content));
						
					} catch (NumberFormatException e) {
						log.warn("Handle "+e.getMessage()+" value ["+content+"]");
					}
				}
			}
		} catch (SQLException e) {
			log.error("[Query Error]", e);
		}finally{
			if(qe != null){
				qe.closeConnection();
			}
		}
		return totalAmount;
	}
	
	private void initaliseTimes() throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currTimeMillis = fmt.format(System.currentTimeMillis());
		Date currDateTime = fmt.parse(currTimeMillis);
		Calendar cal = Calendar.getInstance(); 
		
		cal.setTime(currDateTime);
		cal.add(Calendar.DATE, -1);
		startTime = cal.getTime();
		
		cal.setTime(currDateTime);
		cal.add(Calendar.MILLISECOND, -1);
		endTime = cal.getTime();
		
		log.info("StartTime : "+startTime.toString());
		log.info("endTime : "+endTime.toString());		
	}
	
	private String formatAmount(BigDecimal amount){
		if(amount != null){
			amount.setScale(2, BigDecimal.ROUND_DOWN);

			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			df.setMinimumFractionDigits(2);
			df.setGroupingUsed(false);
			return df.format(amount);
		}
		return "0.00";
	}

	public FtpService getFtpService() {
		return ftpService;
	}

	public void setFtpService(FtpService ftpService) {
		this.ftpService = ftpService;
	}
	
}
