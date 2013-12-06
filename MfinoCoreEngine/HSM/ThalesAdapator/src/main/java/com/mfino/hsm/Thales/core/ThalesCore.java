package com.mfino.hsm.thales.core;

import org.jpos.iso.ISOException;
import org.jpos.iso.packager.DummyPackager;
import org.jpos.util.Log;




import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Random;

/**
 * Thales class to take care of creating the channel and talking to HSM for 
 * keeping connection alive
 * 
 * @author POCHADRI
 *
 */
public class ThalesCore 
{                          
      private ThalesChannel channel;
      String basePath;
      Log log;

     public ThalesCore(String host, int port, String basePath, String schema, Log log) {
         channel = new ThalesChannel();
         channel.setHost(host, port);
         channel.setPackager(new DummyPackager());
         this.basePath=basePath;
         this.log=log;
         channel.setBasePath(basePath);
         channel.setSchema(schema);
     }
     
     public void connect() throws IOException {
         channel.connect();
     }

     public boolean isConnected()  
     {
         return channel.isConnected();
     }

     public void disconnect() throws IOException 
     {
         channel.disconnect();    
     }
     
     public  ThalesMsg createRequest(String command) throws IOException
     {
    	 return createRequest(command,basePath);
     }

     public static ThalesMsg createRequest(String command, String basePath) throws IOException {

         ThalesMsg req = new ThalesMsg("file:"+basePath);
         
       //Warning, this contain a rough hack
       //Somehow JPOS HThalesMsg will generate a stackoverflow if your schema is not present
       //Can't let that happening to a production system.

         if (command != null)
         {     
        	 req.set("command", command);
             File f = null;
             f = new File(new URL(req.getBasePath()+command+".xml").getFile());
             

             if(!f.exists())
             {
            	 System.out.println("file not found " +f.getAbsolutePath());
                 throw new IOException("Schema File not defined "+f.getAbsolutePath());
             }

         }      
         return req;
     }


     public ThalesMsg createResponse(String response) {
         ThalesMsg resp = new ThalesMsg("file:"+basePath+"resp-");

         if (response != null)
             resp.set("response", response);

         return resp;
     }

     public ThalesMsg diagnostics() throws IOException {
         return createRequest("NC");
     }

    public ThalesMsg echoTest() throws IOException {



          ThalesMsg req =  createRequest("B2");

          req.set("length", "8");

          Random rnd = new Random();
          int i =  rnd.nextInt(99999999);

           req.set("data", Integer.toString(i));

            return req;


    }

    public void sendKeepAlive() throws Exception
    {
       //command(echoTest());
    	command(diagnostics());    	
    }
    
   
    
	public ThalesMsg generateZPK(String zmk) throws IOException
    {
    	ThalesMsg req = createRequest("IA");
    	req.set("zmk",zmk);
    	return req;
    }
	
    public ThalesMsg generatePVK(String zmk) throws IOException 
    {
    	ThalesMsg req = createRequest("FG");
    	req.set("zmk",zmk);
    	req.dump(System.out,"after setting values 111 FG");
    	return req;
    }
    
     public ThalesMsg generateDoubleLengthKey() throws IOException {
         ThalesMsg req = createRequest("A0");
         req.set("mode", "0");
         req.set("key-type", "001");
         req.set("key-scheme-lmk", "X");

         return req;
     }
     
     /**
      * Generate the ZMK from two components encrypted by LMK under 04-05
      * @return
      * @throws IOException
      */
     public ThalesMsg generateZMK() throws IOException {
         ThalesMsg req = createRequest("GY");
         req.set("noc", "2");
         req.set("firstzmk", "EB8AB461149D145B5708D09F81E287E9");
         req.set("secondzmk", "43D34615B7082439B8A9BE9D14E17995");
         //req.set("thirdzmk", "AC73042C190FD04F");
         req.dump(System.out,"after setting values 111");
         return req;
     }
     
     public ThalesMsg generateZMKfromThreeComponents() throws IOException {
         ThalesMsg req = createRequest("GG");
         req.set("firstzmk", "A3A883D18499A759");
         req.set("secondzmk", "BBF9AEBA7C9D642E");
         req.set("thirdzmk", "AC73042C190FD04F");
         req.dump(System.out,"after setting values");
         return req;
     }
                                            
     public ThalesMsg importDoubleLengthKey(String zmk, String key) throws IOException {
         ThalesMsg req = createRequest("A6");
         req.set("key-type", "001");
         req.set("zmk", "X" + zmk);
         req.set("key-under-zmk", "X" + key);
         req.set("key-scheme", "X");

         return req;
     }

     public synchronized ThalesMsg command(ThalesMsg request) throws ISOException, IOException,InterruptedException 
     {
         StringBuffer sbuffer = new StringBuffer(request.get("command"));
         sbuffer.setCharAt(1, (char) (sbuffer.charAt(1) + 1));
         try 
         {
            String packedRequest =  request.pack();
            log.info("request:"+request.get("command"));
            log.debug("complete packed request:"+packedRequest);
         } 
         catch(Exception e) 
         {
        	 log.error("Exception 1e"+e);
         }

         ThalesMsg resp;
         resp = createResponse(sbuffer.toString());
         ThalesISOMsg msg = new ThalesISOMsg(request);
         
         channel.setBasePath(resp.getBasePath());
         channel.setSchema(resp.getBaseSchema());
         
         try 
         {
             msg.pack();
         } 
         catch(Exception e) 
         {
        	 log.error("Exception 2e "+e); 
         }
         
         channel.send(msg);

         ThalesISOMsg response = (ThalesISOMsg) channel.receive();
         log.debug("got response from hsm");
         resp.merge(response.getFSDMsg());
         log.info("[response:" + resp.get("response")+"][code:" + resp.get("error")+"]");
         log.debug("contents of message:\n"+getAppendedStringForMap(resp.getMap()));
         
         return resp;
     } 
     
     private String getAppendedStringForMap(Map map) 
		{
			StringBuffer res = new StringBuffer();
			for(Object key:map.keySet())
			{
				res.append(key);
				res.append(":");
				res.append(map.get(key));
				res.append("\n");
			}
			// TODO Auto-generated method stub
			return res.toString();
		}
}