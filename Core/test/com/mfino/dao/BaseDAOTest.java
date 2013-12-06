/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import com.mfino.dao.query.BaseQuery;
import com.mfino.dao.query.CommodityTransferQuery;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
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
public class BaseDAOTest {

    public BaseDAOTest() {
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

    private static void invokeStaticMethod(Class targetClass,
            String methodName, Class[] argClasses, Object[] argObjects)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

            Method method = targetClass.getDeclaredMethod(methodName,argClasses);
            method.setAccessible(true);
            method.invoke(null, argObjects);
    }

    @Test
    public void testProcessColumnMethod()
    {
        LinkedHashMap expectedMap = new LinkedHashMap();
        expectedMap.put("id","asc");
        expectedMap.put("A.bd","desc");
        expectedMap.put("cd","asc");

        CommodityTransferQuery query = new CommodityTransferQuery();
        query.setSortString("id:asc,bd:desc,cd:asc");

        try {
        
            Class[] argClasses = {BaseQuery.class,String.class,String.class };
            Object[] argObjects = {query, "bd", "A.bd" };

            invokeStaticMethod(BaseDAO.class, "processColumn", argClasses, argObjects);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        
        assertEquals(query.getOrderMap(),expectedMap);
    }
}