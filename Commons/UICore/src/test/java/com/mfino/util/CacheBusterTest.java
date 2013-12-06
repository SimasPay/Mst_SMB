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

import com.mfino.uicore.util.CacheBuster;

import static org.junit.Assert.*;

/**
 *
 * @author sandeepjs
 */
public class CacheBusterTest {

    public CacheBusterTest() {
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
    public void testGetTimeStamp()
    {

        for(int i =0;i<10;i++)
        {
            System.out.println("Time Stamp = "+CacheBuster.getTimeStamp());
        }

    }

}