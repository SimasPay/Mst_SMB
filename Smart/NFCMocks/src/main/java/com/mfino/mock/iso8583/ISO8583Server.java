
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

public class ISO8583Server implements Runnable {

	private static Logger	log	= LoggerFactory.getLogger(ISO8583Server.class);

	private static ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(5);
	private static MessageFactory mfact;

	private Socket socket;

	ISO8583Server(Socket sock) {
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
//						<field id="33" value="881"/>
						response.setValue(39, "881", IsoType.ALPHA, 11);
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					// C. Network SIGN-OFF
					else if ("002".equals(incoming.getObjectValue(70))) {
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}else{
						log.error("Unrecognonized message type");
					}
					break;

				case 0x200:
					// 2.  Administrative messages
					// A. Card Linking
					if ("500000".equals(incoming.getObjectValue(3))) {
						
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					// B. Card Unlinking
					if ("520000".equals(incoming.getObjectValue(3))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
						}
					// 8 Balance inquiry in CMS
					
					else if ("300000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "01360000034260000",
								IsoType.LLLVAR, 0);
					}
					// 10 Card Status
					else if ("301000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(61, "000",
								IsoType.LLLVAR,3);
					}
					// 9 Transaction History
					//Last 1 txn
					else if ("360001".equals(incoming.getObjectValue(3))){
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
						response.setValue(
								61,
								"100309213943DIDR00000000100000026790691340  Merchant                 ",
								IsoType.LLLVAR, 0);
					}
					//Last 2 txn
					else if ("360002".equals(incoming.getObjectValue(3))){
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
						response.setValue(
								61,
								"100309213943DIDR00000000100000026790691340  Merchant                 " +
								"100309203943DIDR00000000200000126790691341  Merchant                 ",
								IsoType.LLLVAR, 0);
					}
					//Last 3 txn
					else if ("360003".equals(incoming.getObjectValue(3))){
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
						response.setValue(
								61,
								"100309213943DIDR00000000100000026790691340  Merchant                 " +
								"100309203943DIDR00000000200000126790691341  Merchant                 " +
								"100309193943DIDR00000000300000226790691342  Merchant                 ",
								IsoType.LLLVAR, 0);
					}
					//Last 4 txn
					else if ("360004".equals(incoming.getObjectValue(3))){
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
						response.setValue(
								61,
								"100309213943DIDR00000000100000026790691340  Merchant                 " +
								"100309203943DIDR00000000200000126790691341  Merchant                 " +
								"100309193943DIDR00000000300000226790691342  Merchant                 " +
								"100309183943DIDR00000000400000326790691343  Merchant                 ",
								IsoType.LLLVAR, 0);
					}
					//Last 5 txn
					else if ("360005".equals(incoming.getObjectValue(3))){
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
						response.setValue(
								61,
								"100309213943DIDR00000000100000026790691340  Merchant                 " +
								"100309203943DIDR00000000200000126790691341  Merchant                 " +
								"100309193943DIDR00000000300000226790691342  Merchant                 " +
								"100309183943DIDR00000000400000326790691343  Merchant                 " +
								"100309173943DIDR00000000500000426790691344  Merchant                 ",
								IsoType.LLLVAR, 0);
					}
					//Last 6 txn
					else if ("360006".equals(incoming.getObjectValue(3))){
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
						response.setValue(
								61,
								"100309213943DIDR00000000100000026790691340  Merchant                 " +
								"100309203943DIDR00000000200000126790691341  Merchant                 " +
								"100309193943DIDR00000000300000226790691342  Merchant                 " +
								"100309183943DIDR00000000400000326790691343  Merchant                 " +
								"100309173943DIDR00000000500000426790691344  Merchant                 " +
								"100309163943DIDR00000000600000526790691345  Merchant                 ",
								IsoType.LLLVAR, 0);
					}
					//Last 7 txn
					else if ("360007".equals(incoming.getObjectValue(3))){
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
						response.setValue(
								61,
								"100309213943DIDR00000000100000026790691340  Merchant                 " +
								"100309203943DIDR00000000200000126790691341  Merchant                 " +
								"100309193943DIDR00000000300000226790691342  Merchant                 " +
								"100309183943DIDR00000000400000326790691343  Merchant                 " +
								"100309173943DIDR00000000500000426790691344  Merchant                 " +
								"100309163943DIDR00000000600000526790691345  Merchant                 " +
								"100309153943DIDR00000000700000626790691346  Merchant                 ",
								IsoType.LLLVAR, 0);
					}
					//Last 8 txn
					else if ("360008".equals(incoming.getObjectValue(3))){
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
						response.setValue(
								61,
								"100309213943DIDR00000000100000026790691340  Merchant                 " +
								"100309203943DIDR00000000200000126790691341  Merchant                 " +
								"100309193943DIDR00000000300000226790691342  Merchant                 " +
								"100309183943DIDR00000000400000326790691343  Merchant                 " +
								"100309173943DIDR00000000500000426790691344  Merchant                 " +
								"100309163943DIDR00000000600000526790691345  Merchant                 " +
								"100309153943DIDR00000000700000626790691346  Merchant                 " +
								"1003091143943DIDR0000000800000726790691347  Merchant                 ",
								IsoType.LLLVAR, 0);
					}
					//Last 9 txn
					else if ("360009".equals(incoming.getObjectValue(3))){
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
						response.setValue(
								61,
								"100309213943DIDR00000000100000026790691340  Merchant                 " +
								"100309203943DIDR00000000200000126790691341  Merchant                 " +
								"100309193943DIDR00000000300000226790691342  Merchant                 " +
								"100309183943DIDR00000000400000326790691343  Merchant                 " +
								"100309173943DIDR00000000500000426790691344  Merchant                 " +
								"100309163943DIDR00000000600000526790691345  Merchant                 " +
								"100309153943DIDR00000000700000626790691346  Merchant                 " +
								"100309143943DIDR00000000800000726790691347  Merchant                 " +
								"100309143943DIDR00000000900000826790691348  Merchant                 ",
								IsoType.LLLVAR, 0);
					}
					//Last 10 txn
					else if ("360010".equals(incoming.getObjectValue(3))){
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
						response.setValue(
								61,
								"100309213943DIDR00000000100000026790691340  Merchant                 " +
								"100309203943DIDR00000000200000126790691341  Merchant                 " +
								"100309193943DIDR00000000300000226790691342  Merchant                 " +
								"100309183943DIDR00000000400000326790691343  Merchant                 " +
								"100309173943DIDR00000000500000426790691344  Merchant                 " +
								"100309163943DIDR00000000600000526790691345  Merchant                 " +
								"100309153943DIDR00000000700000626790691346  Merchant                 " +
								"100309143943DIDR00000000800000726790691347  Merchant                 " +
								"100309133943DIDR00000000900000826790691348  Merchant                 " +
								"100309123943DIDR00000001000000926790691349  Merchant                 ",
								IsoType.LLLVAR, 0);
					}

					else {
						log.error("Unrecognonized message type");
					}
					break;
				case 0x400:
					//Card Linking Reversal
					if ("500000".equals(incoming.getObjectValue(3))) {
						
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
				}
					//Card Unlinking Reversal
					if ("520000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					else {
						log.error("Unrecognized message type");
					}
					break;
				default:
					log.error("Unrecognized message type");
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
					mfact = ConfigParser.createFromClasspathConfig("config.xml");
				} catch (IOException e) {
					log.error("Failed to parse the config file", e);
					return;
				}
				log.info("Setting up server socket...");
				ServerSocket server;
				try {
					server = new ServerSocket(9977);
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
					
					new Thread(new ISO8583Server(sock), "j8583-server").start();
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
	
	private static String createRandomInteger(int aStart, long aEnd, Random aRandom){
	    if ( aStart > aEnd ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    //get the range, casting to long to avoid overflow problems
	    long range = aEnd - (long)aStart + 1;
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * aRandom.nextDouble());
	    long randomNumber =  fraction + (long)aStart;    
	    
	    return String.valueOf(randomNumber);
	  }
}
