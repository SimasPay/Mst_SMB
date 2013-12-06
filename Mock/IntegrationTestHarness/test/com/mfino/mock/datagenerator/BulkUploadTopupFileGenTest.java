///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.mfino.mock.datagenerator;
//
//import com.mfino.mock.integrationtestharness.commons.ITHLogger;
//import com.mfino.util.HibernateUtil;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.slf4j.Logger;
//
///**
// *
// * @author sunil
// */
//public class BulkUploadTopupFileGenTest{
//    @Before
//    public void setUp() {
//        insertTestData();
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    @Test
//    public void insertTestData() {
//        BulkUploadTopupFileGenerator bulkTopup=new BulkUploadTopupFileGenerator();
//        try{
//            bulkTopup.createTopupUploadFile();
//        } catch(Exception e){
//            ITHLogger.getLogger().error("Exception:"+e.getMessage());
//        }
//    }
//
//}
