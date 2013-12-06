/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.testharnessbliimpl;

import com.mfino.mock.integrationtestharness.commons.ITHLogger;
import com.mfino.mock.testharnessbli.CallMfinoServerBLI;
import org.slf4j.Logger;


/**
 *
 * @author sunil
 */
public class CreateMfinoCallThread implements Runnable{
    
    String outgoing_url;
    Logger logger;
    
    CreateMfinoCallThread(String outgoingUrl){
        outgoing_url=outgoingUrl;
        logger=ITHLogger.getLogger();
        
    }
    public void run(){

        try{
            CallMfinoServerBLI mFinoServer_req=TestHarnessBLIFactoryIMPL.getMfinoServerRequest(TestHarnessBLIFactoryIMPL.MfinoServer.HttpServer);
            mFinoServer_req.invokeMfinoServer_noVO(outgoing_url);
           
        }
        catch(Exception e)
        {
            logger.error( "OutGoing URL is:::"+outgoing_url+"Has Exception :" ,e);

        }

    }

}
