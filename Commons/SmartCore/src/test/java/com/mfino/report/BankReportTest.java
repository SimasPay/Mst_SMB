/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.report;

import com.mfino.dao.CommodityTransferDAO;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sandeepjs
 */
public class BankReportTest {

    public BankReportTest() {
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
        CommodityTransferDAO ctDao=new CommodityTransferDAO();
        DateFormat df=new SimpleDateFormat("yyyyMMddHHmmss'Z'");

        Date stDt=df.parse("20081117160000Z");
        Date endDt=df.parse("20101117160000Z");

        BankReport rpt=new BankReport(ctDao);

        List<File> reports = rpt.runAndGetMutlipleReports(stDt, endDt);
        
        for(File f : reports){
        	f.delete();
        }
    }
}