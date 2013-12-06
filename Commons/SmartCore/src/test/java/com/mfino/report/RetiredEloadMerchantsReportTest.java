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

import com.mfino.dao.MerchantDAO;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 
 * @author sandeepjs
 */
public class RetiredEloadMerchantsReportTest {

    	// We also want the query times to be based on Asia/Jakarta timezone.
	private static final TimeZone tz = TimeZone.getTimeZone("Asia/Jakarta");
	private static Calendar cal = Calendar.getInstance(tz);

	private static Date startTime = null;
	private static Date endTime = null;


	public RetiredEloadMerchantsReportTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
            	cal.set(2010, 0, 8, 0, 0, 0);
                startTime = cal.getTime();
		cal.set(2010, 0, 9, 0, 0, 0);
                endTime = cal.getTime();
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

		MerchantDAO merchantDAO = new MerchantDAO();

//		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
//
//		Date stDt = df.parse("20081117160000Z");
//		Date endDt = df.parse("20101117160000Z");

		RetiredEloadMerchantsReport rpt = new RetiredEloadMerchantsReport(
				merchantDAO);

		rpt.run(startTime, endTime);

		
	}
}