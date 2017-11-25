package com.mfino.hsm.thales7.core;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.FSDChannel;
import org.jpos.util.LogEvent;
import org.jpos.util.Logger;


/**
 * Jpos Channel using FSDChannel for sending the message to HSM
 * @author POCHADRI
 *
 */
public class ThalesChannel extends FSDChannel {
     String basePath;
     String schema;

    String baseResponsePath;

    public String getBaseResponsePath() {
        return baseResponsePath;
    }

    public void setBaseResponsePath(String baseResponsePath) {
        this.baseResponsePath = baseResponsePath;
    }



     public String getBasePath() {
         return basePath;
     }

     public void setBasePath(String basePath) {
         this.basePath = basePath;
     }

     public String getSchema() {
         return schema;
     }

     public void setSchema(String schema) {
         this.schema = schema;
     }

     @Override
     public ISOMsg createMsg() {
         if (basePath != null && schema != null)
             return new ThalesISOMsg(new ThalesMsg(basePath, schema));

         if (basePath != null)
             return new ThalesISOMsg(new ThalesMsg(basePath));

         return new ThalesISOMsg();
     }
     
     protected int getMessageLength() throws IOException, ISOException {
    	 LogEvent evt = new LogEvent (this, "thales-channel");
         evt.addMessage ("checking received message length");
         final byte[] b = new byte[2];
         Callable<Void> callable = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				serverIn.readFully(b,0,2);
				return null;
			}
		};
        ExecutorService executor = Executors.newCachedThreadPool();
        Future<Void> submit = executor.submit(callable);
        try {
			submit.get(1, TimeUnit.MINUTES);
			evt.addMessage("success", "received message length");
		} catch (InterruptedException e) {
			 evt.addMessage ("InterruptedException", "prepare to creating new connection ["+e.getMessage()+"]");
			 return 0;
		} catch (ExecutionException e) {
			 evt.addMessage ("ExecutionException", "prepare to creating new connection ["+e.getMessage()+"]");
			 return 0;
		} catch (TimeoutException e) {
			 evt.addMessage ("TimeoutException", "prepare to creating new connection ["+e.getMessage()+"]");
			 return 0;
		} finally{
			Logger.log (evt);
		}
        
        return ((((int)b[0])&0xFF) << 8) | (((int)b[1])&0xFF);
     }
}