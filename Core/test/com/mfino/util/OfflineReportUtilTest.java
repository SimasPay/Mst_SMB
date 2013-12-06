/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ADMIN
 */
public class OfflineReportUtilTest {

    public OfflineReportUtilTest() {
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


    /**
     * Test of stripRx method, of class OfflineReportUtil.
     */
    @Test
    public void testStripRx() {
        System.out.println("stripRx");
        String mdn = "6288115866148898R0";
        String expResult = "6288115866148898";
        String result = OfflineReportUtil.stripRx(mdn);
        assertEquals(expResult, result);        
    }

    @Test
    public void testBulkUploadMsg(){
        String msg = "8=mFinoFIX.1.1	9=00020535=109934=4752=20100111-09:15:45.8145069=15385=WebAppFEForMerchants	5142=15078=628811001317	5084=05018=username	5019=password	5011=3000005002=	5079=6281310230782	5396=525272=15522=bonus fl	10=215";
        Long result = OfflineReportUtil.getUploadIDFromMsg(msg);
        Long expResult=52L;
        assertEquals(expResult, result);
    }

}