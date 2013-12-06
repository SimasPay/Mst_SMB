/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.testharnessbliimpl;

import com.mfino.mock.integrationtestharness.commons.ITHConstants;
import com.mfino.mock.integrationtestharness.commons.ITHLogger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;

/**
 *
 * @author sunil
 */
public class FreqTestController {
    private BlockingQueue<String> queue;
    private int selectionList;
    private int threadCount=0;
    private boolean shutDown=false;
    Logger logger;


    public FreqTestController(int list, int totalNoOfRequests){
        queue=new LinkedBlockingQueue<String>(totalNoOfRequests);
        selectionList=list;
        logger=ITHLogger.getLogger();
        
    }
    //Because of Time constraint we will ignore this logic for later implementation
    private ArrayList getListOfUtkFlows(int selectionList){
        ArrayList utkList=new ArrayList();
        if((selectionList& ITHConstants.CHANGE_MCASH_PIN_INT)!= 0){
            utkList.add(ITHConstants.CHANGE_MCASH_PIN_INT);
        }else if((selectionList& ITHConstants.CHANGE_PIN_INT)!= 0){
            utkList.add(ITHConstants.CHANGE_PIN_INT);
        }else if((selectionList& ITHConstants.CHECK_BALANCE_INT)!= 0){
            utkList.add(ITHConstants.CHECK_BALANCE_INT);
        }else if((selectionList& ITHConstants.GET_MCASH_TRANSACTIONS_INT)!= 0){
            utkList.add(ITHConstants.GET_MCASH_TRANSACTIONS_INT);
        }else if((selectionList& ITHConstants.GET_TRANSACTIONS_INT)!= 0){
            utkList.add(ITHConstants.GET_TRANSACTIONS_INT);
        }else if((selectionList& ITHConstants.MCASH_BALANCE_INQUIRY_INT)!= 0){
            utkList.add(ITHConstants.MCASH_BALANCE_INQUIRY_INT);
        }else if((selectionList& ITHConstants.MCASH_TOPUP_INT)!= 0){
            utkList.add(ITHConstants.MCASH_TOPUP_INT);
        }else if((selectionList& ITHConstants.MCASH_TO_MCASH_INT)!= 0){
            utkList.add(ITHConstants.MCASH_TO_MCASH_INT);
        }else if((selectionList& ITHConstants.MERCHANT_CHANGE_PIN_INT)!= 0){
            utkList.add(ITHConstants.MERCHANT_CHANGE_PIN_INT);
        }else if((selectionList& ITHConstants.MERCHANT_CHECK_BALANCE_INT)!= 0){
            utkList.add(ITHConstants.MERCHANT_CHECK_BALANCE_INT);
        }else if((selectionList& ITHConstants.MERCHANT_GET_TRANSACTIONS_INT)!= 0){
            utkList.add(ITHConstants.MERCHANT_GET_TRANSACTIONS_INT);
        }else if((selectionList& ITHConstants.MERCHANT_MPIN_RESET_INT)!= 0){
            utkList.add(ITHConstants.MERCHANT_MPIN_RESET_INT);
        }else if((selectionList& ITHConstants.MERCHANT_RESET_PIN_INT)!= 0){
            utkList.add(ITHConstants.MERCHANT_RESET_PIN_INT);
        }else if((selectionList& ITHConstants.MERCHANT_SHARE_LOAD_INT)!= 0){
            utkList.add(ITHConstants.MERCHANT_SHARE_LOAD_INT);
        }else if((selectionList& ITHConstants.MOBILE_AGENT_DISTRIBUTE_INT)!= 0){
            utkList.add(ITHConstants.MOBILE_AGENT_DISTRIBUTE_INT);
        }else if((selectionList& ITHConstants.MOBILE_AGENT_RECHARGE_INT)!= 0){
            utkList.add(ITHConstants.MOBILE_AGENT_RECHARGE_INT);
        }else if((selectionList& ITHConstants.RESET_PIN_INT)!= 0){
            utkList.add(ITHConstants.RESET_PIN_INT);
        }else if((selectionList& ITHConstants.SHARE_LOAD_INT)!= 0){
            utkList.add(ITHConstants.SHARE_LOAD_INT);
        }else if((selectionList& ITHConstants.SUBSRICPTION_ACTIVATION_INT)!= 0){
            utkList.add(ITHConstants.SUBSRICPTION_ACTIVATION_INT);
        }
        return utkList;

    }
    public void freqTestCtrlInit(){  
        logger.info("Freq Test Initialized");
        //Identify the List of requests that need to be performed
//Because of Time constraint we will ignore this logic for later implementation
//        ArrayList listOfFlows;
//        listOfFlows=getListOfUtkFlows(selectionList);
        //Generate the Data necessary for processing
//        logger.info("Data prepration started");
//        MerchantDataGenerator mdg=new MerchantDataGenerator();
//        SubscriberFileGenerator sfg=new SubscriberFileGenerator();
//        try{
//            mdg.prepareMerchantFiles();
//        }catch(IOException e){
//            logger.info("Unable to generate Merchant Data");
//        }
//        try{
//            sfg.prepareSubscriberFile();
//        }catch(IOException e){
//            logger.info("Unable to generate Subscriber Data");
//        }
        //Produce the data
//        int noOfFlows=listOfFlows.size();

        //Reading from the Txt
        FreqTestProducer produce=new FreqTestProducer(queue);
        ArrayList<String> urlList=produce.readFile(ITHConstants.SUBSCRIBER_FILE, ITHConstants.SUBSCRIBER_LIST);
        urlList.addAll(produce.readFile(ITHConstants.MERCHANT_FILE, ITHConstants.MERCHANT_LIST));
        //urlList.addAll(produce.readFile(ITHConstants.SUBSCRIBER_FILE, ITHConstants.MERCHANT_LIST));
       
        
        Iterator iter=urlList.iterator();
        ArrayList<Thread> t=new ArrayList(urlList.size());
        while(iter.hasNext()){
             String outputURL=(String)iter.next();
             CreateMfinoCallThread callMfinoSrvr=new CreateMfinoCallThread(outputURL);
             t.add(new Thread(callMfinoSrvr));
        }

        Iterator iter1 = t.iterator();
        int count = 0;
        Date stDt = new Date();
        while (iter1.hasNext()) {
            Thread t1 = (Thread) iter1.next();
            t1.start();
//            if (count >= 50) {
//                try {
//                    Thread.sleep(300);
//                    count=0;
//                } catch (InterruptedException e) {
//                    System.out.println(
//                            "Exceptin in sleep.");
//                }
//            }
            count++;
        }
        iter1 = t.iterator();
        while(iter1.hasNext()){
            Thread t2=(Thread)iter1.next();
            try {
                logger.info("Threads Joining.");
                //t1.join();
                t2.join();
            } catch (InterruptedException e) {
                logger.info("Exception: Thread main interrupted.");
            }

        }
        Date endDt = new Date();
        logger.info("StartTime::"+stDt);
        logger.info("EndTime::"+endDt);
        logger.info("TotalTimeTakenInMilliSeconds::"+ (endDt.getTime()-stDt.getTime()));

    }

    public synchronized void shutDown(){
        shutDown=true;
        while(threadCount>0||!queue.isEmpty()){
            try {
                    wait();
                } catch (InterruptedException e) {
                    logger.info(e.getStackTrace().toString());
                }        
         }
    }

    public synchronized boolean getShutDownStatus(){
    return shutDown;
    }
}



