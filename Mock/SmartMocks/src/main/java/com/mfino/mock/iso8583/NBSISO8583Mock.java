
package com.mfino.mock.iso8583;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

public class NBSISO8583Mock implements Runnable {

	private static Logger	log	= LoggerFactory.getLogger(NBSISO8583Mock.class);

	private static ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(5);
	private static MessageFactory mfact;

	private Socket socket;

	NBSISO8583Mock(Socket sock) {
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
				case 0x410:
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
					if("180000".equals(incoming.getObjectValue(3))) {
						// TOPUP
						// Positive response.
						String mdn = (String) incoming.getObjectValue(61);
						String amount = (String) incoming.getObjectValue(4);
						String rrn = (String)incoming.getObjectValue(37);
					    String element62 = mdn+ amount + "20071223" + rrn;
					    Random rand = new Random();
					    if (rand.nextInt() % 2 == 0) {
					    	response.setValue(39, "00", IsoType.ALPHA, 2);
					    }
					    else {
					    	response.setValue(39, "06", IsoType.ALPHA, 2);
					    }
						response.setValue(62, element62, IsoType.LLLVAR, 999);
//						
						// Negative Response 
						// Case 1 - The Destination MDN not present in NBS.
//			     		String element62 = mdn+ amount;
//						response.setValue(39, "31", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
//						
//						// Case -2 Voucher Denomination not available
//						response.setValue(39, "13", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
//					
//						// Case -3
//						response.setValue(39, "30", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
//
//						// Case -4
//						response.setValue(39, "31", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
//					
//						// Case -5
//						response.setValue(39, "68", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
//					
//						// Case -6
//						response.setValue(39, "70", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
//					
//						// Case -7
//						response.setValue(39, "79", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
//					
//						// Case -8
//						response.setValue(39, "81", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
//					
//						// Case -9
//						response.setValue(39, "88", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
//					
//						// Case -10
//						response.setValue(39, "89", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
//					
//						// Case -11
//						response.setValue(39, "91", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
//					
//						// Case -12
//						response.setValue(39, "96", IsoType.ALPHA, 2);
//						response.setValue(62, element62, IsoType.LLLVAR, 999);
						
					} else if ("400000".equals(incoming.getObjectValue(3))) {
						// Share Load.
						// Element 62 - SourceMDN + SourceName + DestinationMDN + Bill Reference Number + Transfer Amount.
						String element61 = (String) incoming.getObjectValue(61);
						
						String sourceMDN = element61.substring(0, 13);
						String destMDN = element61.substring(13);
						String amount = (String) incoming.getObjectValue(4);
						String sourceMDNName = "TEST                          ";
						String destMDNName = "TEST2                         ";
						
						String element62 = sourceMDN + sourceMDNName + destMDN + destMDNName + "12345678901" + amount;
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
						
						/*// Error responses.
						String element62Error = sourceMDN + destMDN + amount;
						response.setValue(39, "05", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "13", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "30", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "31", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "47", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "48", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "68", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "78", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "79", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "81", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "89", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "91", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "96", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						*/
					} else if ("280000".equals(incoming.getObjectValue(3))) {
						// PostPaid Payment.
						String element61 = (String) incoming.getObjectValue(61);
						String amount = (String) incoming.getObjectValue(4);
						// Now this element61 is the MDN we need to use. :)					
						String element62 = element61 + "130424877812 " +  amount;
						Random rand = new Random();
						if (rand.nextInt() % 2 == 0) {
							response.setValue(39, "00", IsoType.ALPHA, 2);
						}
						else { 
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						response.setValue(62, element62, IsoType.LLLVAR, 999);
						
					//	String element62Error = element61 + amount;
					//	response.setValue(39, "05", IsoType.ALPHA, 2);
					//	response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						
						// Case -2 Voucher Denomination not available
					//	response.setValue(39, "13", IsoType.ALPHA, 2);
					//	response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
					//	response.setValue(39, "14", IsoType.ALPHA, 2);
					//	response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						// Case -3
				//		response.setValue(39, "30", IsoType.ALPHA, 2);
				//		response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						// Case -4
				//		response.setValue(39, "31", IsoType.ALPHA, 2);
				//		response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						// Case -5
				//		response.setValue(39, "68", IsoType.ALPHA, 2);
				//		response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						// Case -6
				//		response.setValue(39, "70", IsoType.ALPHA, 2);
				//		response.setValue(62, element62Error, IsoType.LLLVAR, 999);
				
						// Case -7
					//	response.setValue(39, "79", IsoType.ALPHA, 2);
					//	response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						// Case -8
					//	response.setValue(39, "81", IsoType.ALPHA, 2);
					//	response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						// Case -9
					//	response.setValue(39, "88", IsoType.ALPHA, 2);
					//	response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						/*		// Case -10
						response.setValue(39, "89", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
				
						// Case -11
						response.setValue(39, "91", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						// Case -12
						response.setValue(39, "96", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);*/
						
						
					} else if("380000".equals(incoming.getObjectValue(3))) {
						// Post Paid Inquiry.
						String mdn = (String) incoming.getObjectValue(61);
						String mdn1 = "0882111222333";
						String imsi = "00000510098810020040";
						String esn = "00000510098810020041";
						String subtype = "1";
						String cos = "FREN-BC08           ";
						String mktg = "VIP                 ";
						String substatus = "01";
						String name = "Deepti Sreekumar              ";
						String lastbill = "111222333   ";
						String billdate = "20100803";
						String billref = (String) incoming.getObjectValue(37);
						String cummamt = "000000002301";
						String expdate = "20100831";
						String bal = "540000      ";
						String usgamt = "000005000000";
						String credit = "20100000    ";
						String lastbal = "40000       ";
						String prevsta = "01";
						String changedate = "20100819";
						String floflag = "1";
						String element62Successful = mdn + mdn1 + imsi + esn + subtype + cos + mktg + substatus + name + lastbill + billdate + billref + cummamt + expdate + bal + usgamt + credit + lastbal + prevsta +changedate +floflag;
						Random rand = new Random();
						if (rand.nextInt() % 2 == 0) {
							response.setValue(39, "00", IsoType.ALPHA, 2);
						}
						else {
							response.setValue(39, "05", IsoType.ALPHA, 2);
							element62Successful = mdn;
						}
						response.setValue(62, element62Successful, IsoType.LLLVAR, 999);
						
						// Error Cases.
				//		response.setValue(39, "14", IsoType.ALPHA, 2);
				//		response.setValue(62, mdn, IsoType.LLLVAR, 999);
//
//						response.setValue(39, "30", IsoType.ALPHA, 2);
//						response.setValue(62, mdn, IsoType.LLLVAR, 999);
//						
//
//						response.setValue(39, "31", IsoType.ALPHA, 2);
//						response.setValue(62, mdn, IsoType.LLLVAR, 999);
//						
//
//						response.setValue(39, "68", IsoType.ALPHA, 2);
//						response.setValue(62, mdn, IsoType.LLLVAR, 999);
//						
//
//						response.setValue(39, "79", IsoType.ALPHA, 2);
//						response.setValue(62, mdn, IsoType.LLLVAR, 999);
						
//
//						response.setValue(39, "81", IsoType.ALPHA, 2);
//						response.setValue(62, mdn, IsoType.LLLVAR, 999);
//						

//						response.setValue(39, "89", IsoType.ALPHA, 2);
//						response.setValue(62, mdn, IsoType.LLLVAR, 999);
//
//						response.setValue(39, "91", IsoType.ALPHA, 2);
//						response.setValue(62, mdn, IsoType.LLLVAR, 999);
//
//						response.setValue(39, "96", IsoType.ALPHA, 2);
//						response.setValue(62, mdn, IsoType.LLLVAR, 999);
	
					} else {
						log.error("Unrecognonized message type");
					}
					break;
				case 0x400:
					// This is the reversal message.
					if("180000".equals(incoming.getObjectValue(3))) {
						// TOPUP
						// Positive response.
						String mdn = (String) incoming.getObjectValue(61);
						String amount = (String) incoming.getObjectValue(4);
						String element62 = mdn+ amount + "20071223" + "A1331212111211";
						Random rand = new Random();
						if (rand.nextInt() % 2 == 0) {
							response.setValue(39, "00", IsoType.ALPHA, 2);
						} else {
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						response.setValue(62, element62, IsoType.LLLVAR, 999);
//						
						// Negative Response 
						// Case 1 - The Destination MDN not present in NBS.
//						String element62 = mdn+ amount;
	//					response.setValue(39, "14", IsoType.ALPHA, 2);
		//				response.setValue(62, element62, IsoType.LLLVAR, 999);
						
					/*	// Case -2 Voucher Denomination not available
						response.setValue(39, "13", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
					
						// Case -3
						response.setValue(39, "30", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);

						// Case -4
						response.setValue(39, "31", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
					
						// Case -5
						response.setValue(39, "68", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
					
						// Case -6
						response.setValue(39, "70", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
					
						// Case -7
						response.setValue(39, "79", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
					
						// Case -8
						response.setValue(39, "81", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
					
						// Case -9
						response.setValue(39, "88", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
					
						// Case -10
						response.setValue(39, "89", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
					
						// Case -11
						response.setValue(39, "91", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
					
						// Case -12
						response.setValue(39, "96", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
						*/
					} else if ("400000".equals(incoming.getObjectValue(3))) {
						// Share Load.
						// Element 62 - SourceMDN + SourceName + DestinationMDN + Bill Reference Number + Transfer Amount.
						String element61 = (String) incoming.getObjectValue(61);
						
						String sourceMDN = element61.substring(0, 13);
						String destMDN = element61.substring(13);
						String amount = (String) incoming.getObjectValue(4);
						String sourceMDNName = "TEST                          ";
						String destMDNName = "TEST2                         ";
						
						String element62 = sourceMDN + sourceMDNName + destMDN + destMDNName + "12345678901" + amount;
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
						
						// Error responses.
/*						String element62Error = sourceMDN + destMDN + amount;
						response.setValue(39, "05", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						response.setValue(39, "13", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
				
						response.setValue(39, "30", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "31", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "47", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
				
						response.setValue(39, "48", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "68", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "78", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "79", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "81", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "89", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "91", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
						response.setValue(39, "96", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
*/						
					} else if ("280000".equals(incoming.getObjectValue(3))) {
						// PostPaid Payment.
						String element61 = (String) incoming.getObjectValue(61);
						String amount = (String) incoming.getObjectValue(4);
						// Now this element61 is the MDN we need to use. :)					
						String element62 = element61 + "130424877812 " +  amount;
						Random rand = new Random();
						if (rand.nextInt() % 2 == 0) {
							response.setValue(39, "00", IsoType.ALPHA, 2);
						} else {
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						response.setValue(62, element62, IsoType.LLLVAR, 999);
		//Error scenarios				
//						String element62Error = element61 + amount;
//						response.setValue(39, "05", IsoType.ALPHA, 2);
//						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
//						
	/*					// Case -2 Voucher Denomination not available
						response.setValue(39, "13", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						// Case -3
						response.setValue(39, "30", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);

						// Case -4
						response.setValue(39, "31", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						// Case -5
						response.setValue(39, "68", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						// Case -6
						response.setValue(39, "70", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						// Case -7
						response.setValue(39, "79", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						// Case -8
						response.setValue(39, "81", IsoType.ALPHA, 2);
						response.setValue(62, element62, IsoType.LLLVAR, 999);
					
						// Case -9
						response.setValue(39, "88", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						// Case -10
						response.setValue(39, "89", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						// Case -11
						response.setValue(39, "91", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
					
						// Case -12
						response.setValue(39, "96", IsoType.ALPHA, 2);
						response.setValue(62, element62Error, IsoType.LLLVAR, 999);
						
		*/				
					}  else {
						log.error("Unrecognonized message type");
					}
					break;
				default:
					log.error("Unrecognonized message type");
					break;
				}

				System.out.println("Sending response ISO message:\n" + response.toDebugString());
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
					mfact = ConfigParser.createFromClasspathConfig("nbsconfig.xml");
				} catch (IOException e) {
					log.error("Failed to parse the config file", e);
					return;
				}
				System.out.println("Setting up server socket... 6668");
				ServerSocket server;
				try {
					server = new ServerSocket(6668);
				} catch (IOException e) {
					log.error("Failed to set up server socket", e);
					return;
				}
				System.out.println("Waiting for connections...");
				
				while (true) {
					Socket sock;
					try {
						sock = server.accept();
					} catch (IOException e) {
						log.error("Failed to accept socket connection", e);
						break;
					}
					System.out.println(String.format("New connection from %s:%s", 
							sock.getInetAddress(), 
							sock.getPort()));
					
					new Thread(new NBSISO8583Mock(sock), "j8583-server").start();
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