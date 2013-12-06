package com.mfino.mock.xmlrpc;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

public class JavaServer{
	public static void main (String [] args) {
		try {
	
		     System.out.println("Attempting to start XML-RPC Server...");
		     WebServer server = new WebServer(9991);
		     XmlRpcServer xmlRpcServer = server.getXmlRpcServer();
		     PropertyHandlerMapping phm = new PropertyHandlerMapping();
		     
		     phm.addHandler("Biller", com.mfino.mock.xmlrpc.Biller.class);
		     xmlRpcServer.setHandlerMapping(phm);
		     
		     XmlRpcServerConfigImpl serverConfig =
		         (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
		     serverConfig.setEnabledForExtensions(true);
		     serverConfig.setContentLengthOptional(false);
		     
		     server.start();
		     System.out.println("Started successfully.");
		     System.out.println("Accepting requests. (Halt program to stop.)");
		   } catch (Exception exception) {
		     System.err.println("JavaServer: " + exception);
		   }
	}

}
