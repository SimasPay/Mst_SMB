/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.LOPDAO;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.PendingCommodityTransferDAO;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class AllReportTest {

    // We also want the query times to be based on Asia/Jakarta timezone.
    private static final TimeZone tz = TimeZone.getTimeZone("Asia/Jakarta");
    private static Calendar cal = Calendar.getInstance(tz);
    private static Date startTime = null;
    private static Date endTime = null;
    private static final long companyId = 1;

    @BeforeClass
    public static void setDateRange() {
        cal.set(2010, 0, 04, 0, 0, 0);
        startTime = cal.getTime();

        cal.set(2010, 0, 05, 0, 0, 0);

        endTime = cal.getTime();
    }

    @Test
    public void testActivityReport() throws Exception {
        ActivityReport aReport = new ActivityReport();
        aReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testBankSMFailedReport() throws Exception {
        BankSMFailedReport aReport = new BankSMFailedReport();
        aReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testBankSMReport() throws Exception {
        BankSMReport aReport = new BankSMReport();
        aReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testSubscribersReport() throws Exception {
        SubscribersReport aReport = new SubscribersReport();
        aReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testMerchantReport() throws Exception {
        MerchantReport aReport = new MerchantReport(new MerchantDAO());
        aReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testOpenAPIReport() throws Exception {
        OpenAPIReport aReport = new OpenAPIReport();
        aReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testEmptySVAReport() throws Exception {
        EmptySVAReport aReport = new EmptySVAReport();
        aReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testEmptySVAMoneyReport() throws Exception {
        EmptySVAMoneyReport aReport = new EmptySVAMoneyReport();
        aReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testSettlementReport() throws Exception {
        CommodityTransferDAO ctDao = new CommodityTransferDAO();
        SettlementReport sr = new SettlementReport(ctDao);

        sr.run(startTime, endTime, companyId);
    }

    @Test
    public void testSettlementLOPReport() throws Exception {
        LOPDAO lopDao = new LOPDAO();
        SettlementLOPReport rpt = new SettlementLOPReport(lopDao);
        rpt.run(startTime, endTime, companyId);
    }

    @Test
    public void testResolvedTransactionsReport() throws Exception {
        CommodityTransferDAO ctDao = new CommodityTransferDAO();
        ResolvedPendingTransactionsReport rpt = new ResolvedPendingTransactionsReport(ctDao);

        rpt.run(startTime, endTime, companyId);
    }

    @Test
    public void testMerchantDompetReport() throws Exception {
        MerchantDompetReport aReport = new MerchantDompetReport();
        aReport.run(null, null, companyId);
    }

    @Test
    public void testBankReport() throws Exception {
        BankReport aReport = new BankReport(new CommodityTransferDAO());
        aReport.runAndGetMutlipleReports(startTime, endTime, companyId);
    }

    @Test
    public void testPendingTransactionsReport() throws Exception {
        PendingTransactionsReport aReport = new PendingTransactionsReport(new PendingCommodityTransferDAO());
        aReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testRetiredMerchantsReport() throws Exception {
        System.out.println("**********Retired Report*********");
        RetiredEloadMerchantsReport aReport = new RetiredEloadMerchantsReport(new MerchantDAO());
        aReport.run(startTime, endTime, companyId);
    }

    public void testBulkRemittance() throws Exception {
        BulkRemittanceReport bulkRemittanceReport = new BulkRemittanceReport();
        bulkRemittanceReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testSubRetirementReport() throws Exception {
        SubscriberRetirementReport SubscriberRetirementReport = new SubscriberRetirementReport();
        SubscriberRetirementReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testSvaEmoneyReport() throws Exception {
        SVAEmoneyReport svaEmoneyReport = new SVAEmoneyReport();
        svaEmoneyReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testDistributionTreeReport() throws Exception {
        DistributionTreeReport distributionTreeReport = new DistributionTreeReport(new MerchantDAO());
        distributionTreeReport.run(startTime, endTime, companyId);
    }

    @Test
    public void testH2HMerchantReport() {
        /*H2HMerchantsReport bu = new H2HMerchantsReport();
        bu.sendReports();*/
    }
    @Test
    public void testLOPDiscountReport() {
        LOPDAO lDao = new LOPDAO();
        LOPDiscountChangeReport lop = new LOPDiscountChangeReport(lDao);
        lop.run(startTime, endTime, companyId);
    }
    @Test
    public void testCCRegistrationReport() {
        CreditCardRegistrationReport ccReport = new CreditCardRegistrationReport();
        ccReport.run(startTime, endTime, companyId);
    }
    @Test
    public void testCCTransactionReport() {
        CreditCardTransactionReport ccReport = new CreditCardTransactionReport();
        ccReport.run(startTime, endTime, companyId);
    }
}
