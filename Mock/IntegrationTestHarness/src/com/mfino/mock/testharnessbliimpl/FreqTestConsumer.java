/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.testharnessbliimpl;

import com.mfino.mock.integrationtestharness.commons.ITHLogger;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;


/**
 *
 * @author sunil
 */
public class FreqTestConsumer implements Runnable {
    private final BlockingQueue queuePool;
    private final int frequency;
    Logger logger;
    long sleepTime;

    public FreqTestConsumer(BlockingQueue queue, int freq) {
        queuePool = queue;
        frequency=freq;
        logger=ITHLogger.getLogger();
        sleepTime=(long)((frequency*100)/6);
        
    }

    public void run() {

        double initCapacity=frequency/60;
        int poolCapacity=(int)Math.ceil(initCapacity);
        int counter=0;

        while (true) {
            try {
                String outGoingURL = queuePool.take().toString();
                logger.info( "OutGoing URL in Consumer is::" + outGoingURL);
                TestHarnessValueObject input=new TestHarnessValueObject();
                input.setOutgoingUrl(outGoingURL);
                TestHarnessValueObject output=new TestHarnessValueObject();
                CreateMfinoCallThread newMfinoThread= new CreateMfinoCallThread(input.getOutgoingUrl());
                new Thread(newMfinoThread).start();
                if(counter >= poolCapacity){
                    counter=0;
                    Thread.sleep(sleepTime);
                    
                }
                counter++;


            } catch (InterruptedException e) {

                logger.info("Thread  interrupted."+e.getMessage());
                return;
            }
    }


    }
    
}