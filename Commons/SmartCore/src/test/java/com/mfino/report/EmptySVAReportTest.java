/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.report;

import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.LOPDAO;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;

/**
 *
 * @author xchen
 */
//@Ignore //going to test it with DI
public class EmptySVAReportTest {
    @Test
    public void testSearch() throws Exception {
        CommodityTransferDAO lopDao=new CommodityTransferDAO();
        DateFormat df=new SimpleDateFormat("yyyyMMddHHmmss'Z'");

        Date stDt=df.parse("20081117160000Z");
        Date endDt=df.parse("20101117160000Z");

        EmptySVAReport rpt=new EmptySVAReport();

        File report = rpt.run(stDt, endDt);
        
        report.delete();
        //This is to test that we can reopen a new transaction/session in Hibernate
        report = rpt.run(stDt, endDt);
        
        report.delete();
    }
}

