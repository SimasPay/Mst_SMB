/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.report;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mfino.dao.PendingCommodityTransferDAO;

/**
 * 
 * @author sandeepjs
 */
public class PendingTransactionsReportTest {

	public PendingTransactionsReportTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testSearch() throws Exception {
		PendingCommodityTransferDAO pendingCommodityTransferDAO = new PendingCommodityTransferDAO();
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss'Z'");

		Date stDt = df.parse("20081117160000Z");
		Date endDt = df.parse("20101117160000Z");

		PendingTransactionsReport rpt = new PendingTransactionsReport(
				pendingCommodityTransferDAO);

		File report = rpt.run(stDt, endDt);

		report.delete();
	}
}