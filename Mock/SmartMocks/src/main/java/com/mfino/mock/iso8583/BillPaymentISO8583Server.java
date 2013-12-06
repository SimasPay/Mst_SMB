
package com.mfino.mock.iso8583;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

public class BillPaymentISO8583Server implements Runnable {

	private static Logger	log	= LoggerFactory.getLogger(BillPaymentISO8583Server.class);

	private static ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(5);
	private static MessageFactory mfact;

	private Socket socket;

	BillPaymentISO8583Server(Socket sock) {
		socket = sock;
	}
	

	public void run() {
		int count = 0;
		byte[] lenbuf = new byte[4];
		try {
			// For high volume apps you will be better off only reading the
			// stream in this thread
			// and then using another thread to parse the buffers and process
			// the requests
			// Otherwise the network buffer might fill up and you can miss a
			// request.
			while (socket != null && socket.isConnected()
					&& Thread.currentThread().isAlive()
					&& !Thread.currentThread().isInterrupted()) {
				if (socket.getInputStream().read(lenbuf) == 4) {
					int size = Integer.parseInt(new String(lenbuf));
					byte[] buf = new byte[size];
					// We're not expecting ETX in this case
					socket.getInputStream().read(buf);
					count++;
					// Set a job to parse the message and respond
					// Delay it a bit to pretend we're doing something important
					threadPool.schedule(new Processor(buf, socket), 400,
							TimeUnit.MILLISECONDS);
				}
			}
		} catch (IOException ex) {
			log.error("Exception occurred...", ex);
		}
		log.debug(String.format("Exiting after reading %s requests", count));
		try {
			socket.close();
		} catch (IOException ex) {
		}
	}

	private class Processor implements Runnable {
		private byte[] msg;
		private Socket sock;

		Processor(byte[] buf, Socket s) {
			msg = buf;
			sock = s;
		}

		public void run() {
			try {
				log.debug(String.format("Parsing incoming: '%s'", new String(msg)));
				IsoMessage incoming = mfact.parseMessage(msg, 0);
				log.debug("\n" + incoming.toDebugString());

				switch (incoming.getType()) {
				case 0x210:
				case 0x430:
				case 0x810:
					log.debug("Done with response message");
					return;
				}

				// Create a response, this copies the incoming
				IsoMessage response = mfact.createResponse(incoming);

				// Determine what message this is
				// Based on each type of message, create a response
				// In real system, here we would be calling the business logic
				// to do some real work, but here
				// we just return "true"/"good" for every one.

				switch (incoming.getType()) {
				// 1. Network Management Messages
				case 0x800:
					// A. Network SIGN-ON
					if ("001".equals(incoming.getObjectValue(70))) {
						log.debug("SIGN-ON");
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					// B. Network ECHO-TEST
					else if ("301".equals(incoming.getObjectValue(70))) {
						log.debug("ECHO-TEST");
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					// C. Network SIGN-OFF
					else if ("002".equals(incoming.getObjectValue(70))) {
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					// D. Network KEY-EXCHANGE
					else if ("101".equals(incoming.getObjectValue(70))) {
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}else{
						log.error("Unrecognonized message type");
					}
					break;

				case 0x200:
					// 2. SMART DOMPET Administrative messages
					if ("900000".equals(incoming.getObjectValue(3))) {
						// A. M-Commerce Activation, pin setup
						if ("196".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
						}
						// B. PIN Change
						else if ("139".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
						} else {
							log.error("Unrecognonized message type");
						}
					}
					// 3. SMART DOMPET E-Load messages
					// A. Top Up to number itself by SMSBanking from Saving Account
					else if ("551000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(62, "00", IsoType.ALPHA, 2);
					}
					// A. Top Up to number itself by SMSBanking from Checking Account
					else if ("552000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(62, "00", IsoType.ALPHA, 2);
					}
					// B. Top Up from Saving Account
					else if ("569800".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "628811000938                                                                                                                                                                                  241032741       000000050000                        SMRT POST                               20111025  ", IsoType.LLLVAR, 0);
					}
					// B. Top Up from Checking Account
					else if ("562000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "628811000938                                                                                                                                                                                  241032741       000000050000                        SMRT POST                               20111025  ", IsoType.ALPHA, 8);
					}
					// B. Top Up Blitz from Saving Account
					else if ("571000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "628811000938                                                                                                                                                                                  241032741       000000050000                        SMRT POST                               20111025  ", IsoType.ALPHA, 8);
					}
					// B. Top Up Blitz from Checking Account
					else if ("572000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "628811000938                                                                                                                                                                                  241032741       000000050000                        SMRT POST                               20111025  ", IsoType.ALPHA, 8);
					}
					// 4. Bank Bill payment messages
					// A. Inquiry Payment for PAN base transaction (from account: PAN)
					else if ("389800".equals(incoming.getObjectValue(3))) {
						if ("000000000000".equals(incoming.getObjectValue(4)))
							response.setValue(4,450000,IsoType.NUMERIC,12);
						//else
							//response.setValue(4,000000006000,IsoType.NUMERIC,12);
						
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "628811000938                                                                                                                                                                                  241032741       000000050000                        SMRT POST                               20111025  ",
								IsoType.LLLVAR, 0);
					}
					// A. Inquiry Payment from Saving Account
					else if ("381000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "1002360C000000003426",
								IsoType.LLLVAR, 0);
					}
					// A. Inquiry Payment from Checking Account
					else if ("382000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "1002360C000000003426",
								IsoType.LLLVAR, 0);
					}
					// C. Payment for PAN base transaction (from account: PAN)
					else if ("509800".equals(incoming.getObjectValue(3))) {
						if ("0".equals(incoming.getObjectValue(4)))
							response.setValue(4,000000004500,IsoType.NUMERIC,12);
						
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "628811000938                                                                                                                                                                                  241032741       000000050000                        SMRT POST                               20111025  ",
								IsoType.LLLVAR, 0);
					}
					//  Payment from Saving Account
					else if ("501000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					// D.Payment from Checking Accounts
					else if ("502000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					else {
						log.error("Unrecognonized message type");
					}
					break;
				case 0x420:
					// E: Reversal Payment for PAN base transaction (from account: PAN)
					if ("500000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "16MSISDN1234567816CUSTID1234567811161234567876543200000001234500000000012200000000011140          Payment Response Leo Himawan30052011",
								IsoType.LLLVAR, 0);
					}
					// E: Reversal Payment from Saving Account
					if ("509800".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "16MSISDN1234567816CUSTID1234567811161234567876543200000001234500000000012200000000011140          Payment Response Leo Himawan30052011",
								IsoType.LLLVAR, 0);
					}
					// E: Payment from Checking Account
					if ("502000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					// C. Reversal Top Up to number itself by SMSBanking from Saving Account
					else if ("569800".equals(incoming.getObjectValue(3))) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "MSISDN12345432VSN123456789999920110629", IsoType.LLLVAR, 0);
					}
					// C. Reversal Top Up to number itself by SMSBanking from Checking Account
					else if ("552000".equals(incoming.getObjectValue(3))) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					// D. Reversal Top Up from Saving Account
					else if ("561000".equals(incoming.getObjectValue(3))) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "87654321", IsoType.ALPHA, 8);
					} 
					// D. Reversal Top Up from Checking Account
					else if ("562000".equals(incoming.getObjectValue(3))) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "87654321", IsoType.ALPHA, 8);
					} 
					// D. Reversal Top Up Blitz from Saving Account
					else if ("571000".equals(incoming.getObjectValue(3))) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "87654321", IsoType.ALPHA, 8);
					} 
					// D. Reversal Top Up Blitz from Checking Account
					else if ("572000".equals(incoming.getObjectValue(3))) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, "87654321", IsoType.ALPHA, 8);
					} else {
						log.error("Unrecognonized message type");
					}
					break;
				default:
					log.error("Unrecognonized message type");
					break;
				}

				log.info("Sending response ISO message:\n" + response.toDebugString());
				response.write(sock.getOutputStream(), 4, false);
			} catch (ParseException ex) {
				log.error("Error parsing incoming message", ex);
			} catch (IOException ex) {
				log.error("Error sending response", ex);
			}catch(Throwable t){
				log.error("Unexpected error", t);
			}
		}
	}

	private static Thread worker = null;
	
	public static void stop(){
		if(isRunning()){
			worker.interrupt();
		}
	}
	
	public static void start(){
		stop();
		
		worker = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mfact = ConfigParser.createFromClasspathConfig("BillPaymentConfig.xml");
				} catch (IOException e) {
					log.error("Failed to parse the config file", e);
					return;
				}
				log.info("Setting up server socket...");
				ServerSocket server;
				try {
					server = new ServerSocket(9994);
				} catch (IOException e) {
					log.error("Failed to set up server socket", e);
					return;
				}
				log.info("Waiting for connections...");
				
				while (true) {
					Socket sock;
					try {
						sock = server.accept();
					} catch (IOException e) {
						log.error("Failed to accept socket connection", e);
						break;
					}
					log.info(String.format("New connection from %s:%s", 
							sock.getInetAddress(), 
							sock.getPort()));
					
					new Thread(new BillPaymentISO8583Server(sock), "j8583-server").start();
				}
			}
		});
		worker.start();
	}
	
	public static boolean isRunning(){
		return worker != null && worker.isAlive();
	}
	 
	public static void main(String[] args) throws Exception {
		start();
		
		while(true){
			if(isRunning()){
				Thread.sleep(1000);
			}else{
				break;
			}
		}
	}
}
