/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;

/**
 *
 * @author Venkata Krishna Teja D
 */
public class BulkRemittanceReportTest {

    @Test
    public void testReportForStartAndEnd() throws Exception {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss'Z'");

        Date stDt = df.parse("20091208160000Z");
        Date endDt = df.parse("20091221160000Z");

        BulkRemittanceReport bulkRemitanceReport = new BulkRemittanceReport();

        File report = bulkRemitanceReport.run(stDt, endDt);

        report.delete();
    }
}
