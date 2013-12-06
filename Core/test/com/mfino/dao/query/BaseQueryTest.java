/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sandeepjs
 */
public class BaseQueryTest {

    private BaseQuery query = new BaseQuery();

    public BaseQueryTest() {
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
    public void testParseSortString() {

        String column1 = "Name";
        String order1 = "asc";
        String column2 = "Number";
        String order2 = "desc";


        Map expectedResult = new LinkedHashMap();

        expectedResult.put(column1, order1);
        expectedResult.put(column2, order2);


        query.setSortString(column1 + ":" + order1 + "," + column2 + ":" + order2);

        assertEquals(expectedResult, query.getOrderMap());
    }
}
