/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.jsfileconcatenator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author sandeepjs
 */
@Ignore
public class JSFileConcatenatorTest {

    public JSFileConcatenatorTest() {
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
    public void testMain() throws Exception{

        JSFileConcatenator jspCont = new JSFileConcatenator("C:/mfino/trunk/Web/AdminApplication/web/WEB-INF/jspf/footer.jspf", "C:/mfino/trunk/Web/AdminApplication/web", "c:/out.js");
        jspCont.concatenateFiles();

    }
}
