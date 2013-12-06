package com.mfino.report;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Date;

import org.jasypt.hibernate.encryptor.HibernatePBEEncryptorRegistry;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mfino.report.generalreports.ConsolidatedSalesReport;
import com.mfino.report.generalreports.EODSummaryReport;
import com.mfino.report.generalreports.FundMovementReport;
import com.mfino.report.generalreports.MoneyAvailableReport;
import com.mfino.report.generalreports.PendingTransactionReport;
import com.mfino.report.generalreports.ReconciliationReport;
import com.mfino.report.generalreports.SalesCommisionReport;
import com.mfino.report.generalreports.ServiceChargeReport;
import com.mfino.report.generalreports.ServiceReport;
import com.mfino.report.generalreports.TransactionReport;
import com.mfino.util.DateUtil;
import com.mfino.util.EncryptionUtil;


public class ReportsTest {
	private static Date startTime = null;
    private static Date endTime = null;
    
	  @BeforeClass
	    public static void setDateRange() {
	        endTime = new Date();
	        startTime = DateUtil.addDays(endTime, -5);
	    }
	  
	  
//	@Test
//	public void testZenithOfflineReport() throws Exception {
//	    	ZenithOfflineReport report = new ZenithOfflineReport();
//	        report.run(startTime, endTime);
//	    }  
	
    @Test
    public void testConsolidateSalesReport() throws Exception {
        ConsolidatedSalesReport report = new ConsolidatedSalesReport();
        report.executeReport(startTime, endTime, null);
    }
    
    @Test
    public void testServiceReport() throws Exception {
        ServiceReport report = new ServiceReport();
        report.executeMutlipleReports(startTime, endTime, null);
    }
    
    @Test
    public void testReconciliationReport() throws Exception {
    	ReconciliationReport report = new ReconciliationReport();
        report.executeReport(startTime, endTime, null);
    }
     
    @Test
    public void testTransactionReport() throws Exception {
    	TransactionReport report = new TransactionReport();
        report.executeReport(startTime, endTime, null);
    }
    
    @Test
    public void testServiceChargeReport() throws Exception {
    	ServiceChargeReport report = new ServiceChargeReport();
        report.executeReport(startTime, endTime, null);
    }
    
    @Test
    public void testPendingReport() throws Exception {
    	PendingTransactionReport report = new PendingTransactionReport();
        report.executeReport(startTime, endTime, null);
    }
    
    @Test
    public void testSalesCommisionReport() throws Exception {
    	SalesCommisionReport report = new SalesCommisionReport();
        report.executeReport(startTime, endTime, null);
    }
    
    @Test
    public void testMoneyAvailableReport() throws Exception {
    	MoneyAvailableReport report = new MoneyAvailableReport();
        report.executeReport(startTime, endTime, null);
    }
    @Test
    public void testFundMoveMentReport() throws Exception {
    	FundMovementReport report = new FundMovementReport();
        report.executeReport(startTime, endTime, null);
    }
    @Test
    public void testEODReport() throws Exception {
    	HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
		registry.registerPBEStringEncryptor("hibernateStringEncryptor", EncryptionUtil.getStringEncryptor());
		registry.registerPBEStringEncryptor("hibernateUniqueStringEncryptor", EncryptionUtil.getUniqueStringEncryptor());
		registry.registerPBEStringEncryptor("dbPasswordEncryptor", EncryptionUtil.getDBPasswordEncryptor());
    	EODSummaryReport report = new EODSummaryReport();
    	report.setReportName("EOD");
        report.run();
    }
   
}
