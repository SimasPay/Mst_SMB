/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.util;

import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @author xchen
 */
public class DateUtilTest extends TestCase {

    //Java is the stupidest thing with dates
    public void testAddDays() {
        Calendar cl = Calendar.getInstance();
        cl.set(2009, 7, 10);
        Date orig = cl.getTime();
        Date after20Days = DateUtil.addDays(orig, 20);
        cl.set(2009, 7, 30);
        assertEquals(cl.getTime(), after20Days);
        Date after35Days = DateUtil.addDays(after20Days, 15);
        cl.set(2009, 8, 14);
        assertEquals(cl.getTime(), after35Days);


    }
}
