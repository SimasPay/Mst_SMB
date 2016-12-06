/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CMultiXBuffer;
import com.mfino.service.impl.SystemParametersServiceImpl;
import com.mfino.util.EncryptionUtil;
import com.mfino.util.httputils.ByteArrayHttpPostAsyncSerializer;
import com.mfino.util.httputils.ByteArrayHttpPostSerializer;

/**
 *
 * @author sandeepjs
 */
public class FixMessageSerializer extends AbstractFixMessageSerializer {
	private static final Logger log = LoggerFactory.getLogger(FixMessageSerializer.class);
	
    public FixMessageSerializer() {
    }

    public FixMessageSerializer(String url) {
        this.URL = url;
        this.parseHostNPort();
    }

    @Override
    public CFIXMsg send(CFIXMsg msg) {
        CFIXMsg retValFixMsg = null;
        CMultiXBuffer buffer = new CMultiXBuffer();

        try {
        	
        	String messageText = msg.DumpFields();
        	log.info("Message Data:" + messageText);
        	
        	msg.toFIX(buffer);
            byte[]  ToSend  =   new byte[buffer.Length()];
            System.arraycopy(buffer.DataPtr(),0,ToSend,0,buffer.Length());
            
            //Check if fix message needs to be encrypted.
            //FIXME : commenting this, fix this when required
//            SystemParametersServiceImpl systemParametersServiceImpl = new SystemParametersServiceImpl();
//            String strEncryptFixMessage = systemParametersServiceImpl.getString(SystemParameterKeys.ENCRYPT_FIX_MESSAGE);
            String strEncryptFixMessage = null;
            log.info("FixMessageSerializer : send : strEncryptFixMessage="+strEncryptFixMessage);
            if((null != strEncryptFixMessage) && ("true".equals(strEncryptFixMessage))){
            	ToSend = EncryptionUtil.encrypt(ToSend);
            	log.info("FixMessageSerializer :: send encryptedToSend="+ToSend);
            }
            
            byte[] receivedByteArray = ByteArrayHttpPostSerializer.serialize(ToSend, host, port,requestLine);
            
            log.info("FixMessageSerializer :: send receivedByteArray="+receivedByteArray);
            if((null != strEncryptFixMessage) && ("true".equals(strEncryptFixMessage))){
            	byte[] decryptedByteArray = EncryptionUtil.decrypt(receivedByteArray);
            	log.info("FixMessageSerializer :: send decryptedByteArray="+decryptedByteArray);
            	receivedByteArray = decryptedByteArray;
            }
            
            CMultiXBuffer buffers = new CMultiXBuffer();
            buffers.Append(receivedByteArray);
            retValFixMsg = CFIXMsg.fromFIX(buffers);
        } catch (Exception e) {
            String messageText = msg.DumpFields();
            log.error("Failed to communicate with backend server with message: " + messageText, e);
        }

        return retValFixMsg;
    }

    @Override
    public void sendAsync(CFIXMsg msg, FixMessageSerializationHandler handler) {
        CMultiXBuffer buffer = new CMultiXBuffer();
        
        try {
            msg.toFIX(buffer);
            handler.setData(buffer.Length(),buffer.DataPtr());
            handler.setRequestLine(requestLine);
            ByteArrayHttpPostAsyncSerializer.serializeAsync(host, port, handler, handler.callback, handler.requestCount);
        } catch (Exception e) {
            String messageText = msg.DumpFields();
            log.error("Failed to communicate with backend server with message: " + messageText, e);
        }
    }
}
