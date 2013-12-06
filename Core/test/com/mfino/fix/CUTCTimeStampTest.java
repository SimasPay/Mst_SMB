/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.fix;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 *
 * @author xchen
 */
public class CUTCTimeStampTest extends TestCase {

    public void testFromSameDateToSameDate(){
        String date = "20080715-16:33:12.123";

        CUTCTimeStamp time = CUTCTimeStamp.fromString(date);

        String newDate = time.toString();

        Assert.assertEquals(newDate, date);
    }
}
