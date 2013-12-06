/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.tools.schemachangeupgradetool.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.mfino.tools.schemachangeupgradetool.SchemaChangeUpgradeTool;

/**
 *
 * @author sandeepjs
 */
public class SchemaUpgradeToolTester {

    public SchemaUpgradeToolTester() {
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
    public void test1() {
        SchemaChangeUpgradeTool sm = new SchemaChangeUpgradeTool("C:/mfino/trunk/Data/mFino/Upgrade","C:/mfino/trunk/Data/mFino/Table/Tables.sql",1);
        sm.setDbParameters("C:/Program Files/MySQL/MySQL Server 5.1","root","mFino260", 1);
        sm.performSchemaChangeUpgrade(null);

    }

    @Test
    public void test2() {

      //  SchemaChangeUpgradeTool sm = new SchemaChangeUpgradeTool("C:/Documents and Settings/sandeepjs/My Documents/NetBeansProjects/trunk/Data/mFino/Upgrade","C:/Documents and Settings/sandeepjs/My Documents/NetBeansProjects/trunk/Data/mFino/Table/Tables.sql",2);
      //  sm.performSchemaChangeUpgrade(null);
    }


}