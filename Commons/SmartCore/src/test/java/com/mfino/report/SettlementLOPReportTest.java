/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.report;

import com.mfino.dao.LOPDAO;

import java.io.File;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import org.junit.Test;

/**
 *
 * @author sunil
 */
public class SettlementLOPReportTest {
    @Test
    public void testSearch() throws Exception {
        LOPDAO lopDao=new LOPDAO();
        DateFormat df=new SimpleDateFormat("yyyyMMddHHmmss'Z'");

        Date stDt=df.parse("20081117160000Z");
        Date endDt=df.parse("20101117160000Z");

        SettlementLOPReport rpt=new SettlementLOPReport(lopDao);

        File report = rpt.run(stDt, endDt);

        report.delete();
    }

}
