///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.mfino.mock.datagenerator;
//
//import com.mfino.mock.integrationtestharness.commons.ITHLogger;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// *
// * @author sunil
// */
//public class SubscriberResetPinFileGenTest {
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
//        SubscriberFileGenerator bulkTopup=new SubscriberFileGenerator();
//        try{
//            bulkTopup.prepareSubscriberFile();
//            //bulkTopup.createTopupUploadFile();
//        } catch(Exception e){
//            ITHLogger.getLogger().error("Exception:"+e.getMessage());
//        }
//    }
//
//}
//
