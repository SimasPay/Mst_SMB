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

/**
 *
 * @author Raju
 */
public class SVAEmoneyReportTest {

    public SVAEmoneyReportTest() {
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
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss'Z'");

        Date stDt =  df.parse("20100102160000Z");
        Date endDt = df.parse("20100204160000Z");

        SVAEmoneyReport svaEmoneyReport = new SVAEmoneyReport();

        File report = svaEmoneyReport.run(stDt, endDt);

      //  report.delete();
    }
}
