/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.serialization;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CMultiXBuffer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.httputils.MockFixHttpServer;


/**
 *
 * @author sandeepjs
 */
public class FixMessageSerializationTester {

    static final String host = "localhost";
    static final int port = 9797;
    static final String URL = "http://" + host + ":" + port + "/";
    FixMessageSerializer fms = new FixMessageSerializer(URL);
    FixMessageSerializer fmss = new FixMessageSerializer();
    static MockFixHttpServer mockFixHttpServer = null;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public FixMessageSerializationTester() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {

        mockFixHttpServer = new MockFixHttpServer();
        mockFixHttpServer.startServer(host, port, 0);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        mockFixHttpServer.stopServer();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
 
    @Test
    public void testSyncRequest() throws InvalidDataException {

        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
        CmFinoFIX.CMChangePin Msg = new CmFinoFIX.CMChangePin();
        Msg.setNewPin("123456");
        Msg.setOldPin("456789");
        Msg.setSourceMDN("rtete");
        CMultiXBuffer Buf = new CMultiXBuffer();
        Msg.toFIX(Buf);

        CFIXMsg pMsg = fms.send(Msg);

        CMultiXBuffer Buf1 = new CMultiXBuffer();
        pMsg.toFIX(Buf1);

        log.info("Begin Testing Synchronous send.....................");
        log.info("Before Serialization :" + new String(Buf.DataPtr(), 0, Buf.Length()));
        log.info("After deSerialization :" + new String(Buf1.DataPtr(), 0, Buf1.Length()));
        log.info("End Testing Synchronous send.....................");
    }


    @Test
    public void testSyncRequestURL() throws InvalidDataException {

        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
        CmFinoFIX.CMChangePin Msg = new CmFinoFIX.CMChangePin();
        Msg.setNewPin("123456");
        Msg.setOldPin("456789");
        Msg.setSourceMDN("rtete");
        CMultiXBuffer Buf = new CMultiXBuffer();
        Msg.toFIX(Buf);

        CFIXMsg pMsg = fmss.send(Msg, URL);

        CMultiXBuffer Buf1 = new CMultiXBuffer();
        pMsg.toFIX(Buf1);
        
        log.info("Begin Testing Synchronous send with URL.................");
        log.info("Before Serialization :" + new String(Buf.DataPtr(), 0, Buf.Length()));
        log.info("After deSerialization :" + new String(Buf1.DataPtr(), 0, Buf1.Length()));
        log.info("End Testing Synchronous send with URL.................");
    }


    @Test
    public void testASyncRequest() throws InvalidDataException {

        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
        CmFinoFIX.CMChangePin Msg = new CmFinoFIX.CMChangePin();
        Msg.setNewPin("123456");
        Msg.setOldPin("456789");
        Msg.setSourceMDN("rtete");
        CMultiXBuffer Buf = new CMultiXBuffer();
        Msg.toFIX(Buf);

        log.info("Begin Testing ASynchronous send .................");
        log.info("Before Serialization :" + new String(Buf.DataPtr(), 0, Buf.Length()));
        fms.sendAsync(Msg, new FixMessageSerializationHandler());
        log.info("End Testing ASynchronous send .................");

    }

    @Test
    public void testASyncRequestURL() throws InvalidDataException {

        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
        CmFinoFIX.CMChangePin Msg = new CmFinoFIX.CMChangePin();
        Msg.setNewPin("123456");
        Msg.setOldPin("456789");
        Msg.setSourceMDN("rtete");
        CMultiXBuffer Buf = new CMultiXBuffer();
        Msg.toFIX(Buf);

        log.info("Begin Testing ASynchronous send with URL.................");
        log.info("Before Serialization :" + new String(Buf.DataPtr(), 0, Buf.Length()));
        fmss.sendAsync(Msg, new FixMessageSerializationHandler(), URL);
        log.info("End Testing ASynchronous send with URL.................");

    }

/*
    @Test
    public void testSyncRequestURL2() {

        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CmFinoFIXMessageCreator());
        CmFinoFIX.CmFinoFIX_message_ChangePin Msg = new CmFinoFIX.CmFinoFIX_message_ChangePin();
        Msg.SetNewPin("123456");
        Msg.SetOldPin("456789");
        Msg.SetSourceMDN("rtete");
        CMultiXBuffer Buf = new CMultiXBuffer();
        Msg.ToFIX(Buf);

        CFIXMsg pMsg = fmss.send(Msg, "http://localhost:8400/MockFixServer/FixServlet");

        CMultiXBuffer Buf1 = new CMultiXBuffer();
        pMsg.ToFIX(Buf1);

        DefaultLogger.getCoreLoggerInstance().info("Begin Testing Synchronous send with URL.................");
        DefaultLogger.getCoreLoggerInstance().info("Before Serialization :" + new String(Buf.DataPtr(), 0, Buf.Length()));
        DefaultLogger.getCoreLoggerInstance().info("After deSerialization :" + new String(Buf1.DataPtr(), 0, Buf1.Length()));
        DefaultLogger.getCoreLoggerInstance().info("End Testing Synchronous send with URL.................");
    }



    @Test
    public void testASyncRequestURL2() {

        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CmFinoFIXMessageCreator());
        CmFinoFIX.CmFinoFIX_message_ChangePin Msg = new CmFinoFIX.CmFinoFIX_message_ChangePin();
        Msg.SetNewPin("123456");
        Msg.SetOldPin("456789");
        Msg.SetSourceMDN("rtete");
        CMultiXBuffer Buf = new CMultiXBuffer();
        Msg.ToFIX(Buf);

        DefaultLogger.getCoreLoggerInstance().info("Begin Testing ASynchronous send with URL.................");
        DefaultLogger.getCoreLoggerInstance().info("Before Serialization :" + new String(Buf.DataPtr(), 0, Buf.Length()));
        fmss.sendAsync(Msg, new FixMessageSerializationHandler(), "http://localhost:8400/MockFixServer/FixServlet");
        DefaultLogger.getCoreLoggerInstance().info("End Testing ASynchronous send with URL.................");

    }
*/

    
}
