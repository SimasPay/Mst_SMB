/**
 * 
 */
package com.mfino.isorequests.listener.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Raju
 * 
 */
public class MultixConnectionListener extends Thread {

	private static final Logger log = LoggerFactory.getLogger(MultixConnectionListener.class);
	int port;

	public MultixConnectionListener(int p) {
		port = p;
	}

	@Override
	public void run() {
		log.info("Setting up server socket...");
		ServerSocket server;
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			log.error("Failed to set up server socket", e);
			return;
		}
		log.info("Waiting for connections...");

		while (true) {
			Socket sock;
			try {
				sock = server.accept();
				byte[] buf = null; 
				byte[] lenbuf = new byte[2];
				if(port==9990){
					lenbuf = new byte[4];
					if (sock.getInputStream().read(lenbuf) == 4) {			
						int size = Integer.parseInt(new String(lenbuf));
            			 buf = new byte[size];
                         sock.getInputStream().read(buf);					
					log.info("THE DATA is : " + new String(buf));
				}
				}else{
					if (sock.getInputStream().read(lenbuf ) == 2) {
						
						int size = lenbuf[0] + lenbuf[1];					// if Big endian
						
						//int size = Integer.parseInt(new String(lenbuf));		
						//int size = lenbuf[0] + lenbuf[1] * 256;
						
						buf = new byte[size];
						// We're not expecting ETX in this case
						sock.getInputStream().read(buf);
						log.info("THE DATA is : " + new String(buf));
					}
				}
				switch (port) {
				case 9990:
					Util.xlinkSocket = sock;
					XLinkBankChannelProcessor xlinkProcessor = new XLinkBankChannelProcessor(buf, Util.xlinkSocket, Util.mfact, null);
					try {
						xlinkProcessor.sendSignOnResponse();
					} catch (Exception exp) {
						log.error(exp.getMessage(), exp);
					}
					break;
				case 9993:
					Util.mobile8Socket = sock;
					Mobile8BankChannelProcessor processor = new Mobile8BankChannelProcessor(buf, Util.mobile8Socket, Util.mfact, null);
					try {
						processor.sendSignOnResponse();
					} catch (Exception exp) {
						log.error(exp.getMessage(), exp);
					}
					break;
				case 9999:
					Util.socket = sock;
					ArtajasaBankChannelProcessor artajasaProcessor = new ArtajasaBankChannelProcessor(buf, Util.socket, Util.mfact, null);
					try {
						artajasaProcessor.sendSignOnResponse();
					} catch (Exception exp) {
						log.error(exp.getMessage(), exp);
					}
					break;
				default:
					log.info("Invalid port number");
					break;
				}
				
			} catch (IOException e) {
				log.error("Failed to accept socket connection", e);
				break;
			}
			log.info(String.format("New connection from %s:%s", sock
					.getInetAddress(), sock.getPort()));
			
			Util.socket = sock;
		}
	}

}
