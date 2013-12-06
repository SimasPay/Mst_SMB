/**
 * 
 */
package com.mfino.constants;

/**
 * @author Chaitanya
 *
 */
public class ReportParameterKeys {
	
	  //Before Correcting errors reported by Findbugs:
		//none of the fiels are final
	
	  //After Correcting the errors reported by Findbugs
		//all fields are final
	public static final String CASHIN_LIMIT = "report.limit.cashin";
	public static final String CASHOUT_LIMIT = "report.limit.cashout";
	public static final String P2P_LIMIT = "report.limit.p2p";
	public static final String BILLPAY_LIMIT = "report.limit.billpay";
	public static final String B2E_LIMIT = "report.limit.bank2emoney";
	public static final String B2B_LIMIT = "report.limit.bank2bank";
	public static final String E2B_LIMIT = "report.limit.emoney2bank";
	public static final String E2B2E_LIMIT = "report.limit.emoney2bank2emoney";
	public static final String DAILY_UTILIZATION_PERCENT = "report.limit.dailyutilizationpercent";
	public static final String ACCOUNT_DEACTIVATETIME = "report.account.deactivatetime";
	public static final String SUCCESS_AMOUNT_LIMIT = "report.successtransactionamount.limit";
	public static final String FAIL_AMOUNT_LIMIT = "report.failedtransactions.limit";
	public static final String DUPLICATE_TRANSACTION_LIMIT = "report.duplicatetransaction.limit";
	public static final String FINANCIALYEAR_DAY = "report.financialyear.day";
	public static final String FINANCIALYEAR_MONTH = "report.financialyear.month";
	public static final String DEFAULT_PARTNER = "deault.partner";
	
	public static final String COMMISION = "commission";
	public static final String SERVICECHARGE = "servicecharge";
	
	public static final String REPORT_PARAMETER_NAME ="reportName";
	public static final String REPORT_PARAMETER_CLASSNAME ="reportClassName";
	public static final String REPORT_PARAMETER_STARTDATE ="startDate";
	public static final String REPORT_PARAMETER_ENDDATE ="endDate";
	public static final String REPORT_BALANCESHEET = "MFSBalanceSheet";
	public static final String REPORT_CASHFLOW = "CashFlowStatement";
	public static final String REPORT_INCOME = "MFSIncomeReport";
	public static final String REPORT_CBNREPORT = "CBNReports";
	
	public static final String SEPARATOR = "_";
	public static final String FONTSIZE = "FontSize";
	public static final String ISLANDSCAPE = "IsLandscape";
	
	public static final String REPORT_HEADER_MARGIN = "report.header.margin";
	public static final String REPORT_FOOTER_MARGIN = "report.footer.margin";
}
