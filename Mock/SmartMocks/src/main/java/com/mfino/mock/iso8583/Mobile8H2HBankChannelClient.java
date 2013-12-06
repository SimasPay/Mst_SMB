package com.mfino.mock.iso8583;

import java.io.IOException;
import java.math.BigDecimal;
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

public class Mobile8H2HBankChannelClient implements Runnable {

	private static Logger	log	= LoggerFactory.getLogger(Mobile8H2HBankChannelClient.class);

	private static ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(5);
	private static MessageFactory mfact;

	private Socket socket;
	private static IsoMessage request;
	
	private static boolean sentInitialNTMRequest;

	public Mobile8H2HBankChannelClient(Socket sock) {
		socket = sock;
	}

	public void run() {
		int count = 0;
		byte[] lenbuf = new byte[2];
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
				if (socket.getInputStream().read(lenbuf) == 2) {
					
					int size = lenbuf[0] + lenbuf[1];					// if Big endian
					
					//int size = Integer.parseInt(new String(lenbuf));		
					//int size = lenbuf[0] + lenbuf[1] * 256;
					
					byte[] buf = new byte[size];
					// We're not expecting ETX in this case
					socket.getInputStream().read(buf);
					System.out.println("THE DATA is : " + new String(buf));
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
		
		private void sendMerchangtTopupRequest() throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(000000000001);
			IsoMessage postPaidPayment = mfact.newMessage(0x200);
			postPaidPayment.setIsoHeader("ISO0100");
			postPaidPayment.setIsMobile8(true);
			postPaidPayment.setValue(2, "6288116210961", IsoType.LLVAR, 16);
			postPaidPayment.setValue(3, "880000", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.NUMERIC, 12);
			postPaidPayment.setValue(7, "1202112422", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "000004", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "112422", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "1202", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "1204", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "6011", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "6622", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "41062625000 ", IsoType.ALPHA, 12);
			postPaidPayment.setValue(41, "888             ", IsoType.ALPHA, 16);
			postPaidPayment.setValue(48, "6288116210961 000000800000", IsoType.LLLVAR, 120);
			//postPaidPayment.setValue(48, "6288116210961 000000025000", IsoType.LLLVAR, 120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "888", IsoType.LLLVAR, 3);
			
			System.out.println("MERCHANT TOPUP REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidPayment.writeData()));
			System.out.println("------------------------------");
			System.out.println();
						
			postPaidPayment.write(sock.getOutputStream(), 2, true);		
		}
		
		private void sendTopUpPaymentReversalRequest() throws IOException {
			// Here now send the Request.
			String STAN_11 = "001598";
			String RRN_37 = "001025000084";
			
			String orignalSTAN = "000004";
			String orignalTxnMMDD = "1202";
			String OriginalTxnTime = "112422";
			String dummyData = "0000000000000000000000";
			String element90 = "0200" + orignalSTAN + orignalTxnMMDD + OriginalTxnTime + dummyData;
	
			BigDecimal amount = new BigDecimal(000000006000);
			IsoMessage postPaidReversal = mfact.newMessage(0x400);
			postPaidReversal.setIsoHeader("ISO0100");
			postPaidReversal.setIsMobile8(true);
			postPaidReversal.setValue(2, "6288291515820", IsoType.LLVAR, 16);
			postPaidReversal.setValue(3, "880000", IsoType.NUMERIC, 6);
			postPaidReversal.setValue(4, amount, IsoType.NUMERIC, 12);
			postPaidReversal.setValue(7, "1025163502", IsoType.DATE10, 10);
			postPaidReversal.setValue(11, STAN_11, IsoType.NUMERIC, 6);
			postPaidReversal.setValue(12, "163502", IsoType.TIME, 6);
			postPaidReversal.setValue(13, "1025", IsoType.DATE4, 4);
			postPaidReversal.setValue(15, "1025", IsoType.DATE4, 4);
			postPaidReversal.setValue(18, "6014", IsoType.NUMERIC, 4);
			postPaidReversal.setValue(32, "881", IsoType.LLVAR, 11);
			postPaidReversal.setValue(37, RRN_37, IsoType.ALPHA, 12);
			postPaidReversal.setValue(41, "0000991622716855", IsoType.ALPHA, 16);
			postPaidReversal.setValue(48, "6288291515820 000000025000", IsoType.LLLVAR, 120);
			postPaidReversal.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidReversal.setValue(60, "050", IsoType.LLLVAR, 3);
			postPaidReversal.setValue(63, "055", IsoType.LLLVAR, 3);
			postPaidReversal.setValue(90, element90, IsoType.NUMERIC, 42);
			//postPaidPayment.setBinary(true);
			System.out.println("TOPUP PAYMENT REVERSAL REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidReversal.writeData()));
			System.out.println("------------------------------");
			System.out.println();
						
			postPaidReversal.write(sock.getOutputStream(), 2, true);		
		}
		
		public void run() {
			try {
				log.debug(String.format("Parsing incoming: '%s'", new String(msg)));
				String strMsg = new String(msg);
				if(strMsg.contains("I"))
				{
					strMsg = strMsg.substring(7);
					strMsg = strMsg.substring(0,strMsg.length() - 1);
					msg = strMsg.getBytes();
				}
				//mfact.setUseBinaryMessages(true);
				IsoMessage incoming = mfact.parseMessage(msg,0);
				//log.debug("\n" + incoming.toDebugString());
				
				
				switch (incoming.getType()) {
				case 0x810:
					log.debug("SIGN-ON done");
					sentInitialNTMRequest =true;
					break;
				case 0x210:
					if("380000".equals(incoming.getObjectValue(3))) {
					}
					break;
				}

				// Create a response, this copies the incoming
//				IsoMessage response = mfact.createResponse(incoming);

				// Determine what message this is
				// Based on each type of message, create a response
				// In real system, here we would be calling the business logic
				// to do some real work, but here
				// we just return "true"/"good" for every one.
//				response.setIsoHeader("ISO0100");
				
//				switch (incoming.getType()) {
				// 1. Network Management Messages
//				case 0x800:
//					// A. Network SIGN-ON
//					if ("001".equals(incoming.getObjectValue(70))) {
//						log.debug("SIGN-ON done");
//						response.setValue(39, "00", IsoType.ALPHA, 2);
//						sentInitialNTMRequest =true;
//					}
//					// B. Network ECHO-TEST
//					else if ("301".equals(incoming.getObjectValue(70))) {
//						log.debug("ECHO-TEST");
//						response.setValue(39, "00", IsoType.ALPHA, 2);
//					}
//					// C. Network SIGN-OFF
//					else if ("002".equals(incoming.getObjectValue(70))) {
//						response.setValue(39, "00", IsoType.ALPHA, 2);
//					}
//					// D. Network KEY-EXCHANGE
//					else if ("101".equals(incoming.getObjectValue(70))) {
//						response.setValue(39, "00", IsoType.ALPHA, 2);
//					}else{
//						log.error("Unrecognonized message type");
//					}
//					break;
//
//				case 0x200:
//					return;
//				case 0x400:
//					return;
//				default:
//					log.error("Unrecognonized message type");
//					break;
//				}

//				log.info("Sending response ISO message:\n" + response.toDebugString());
//				response.setIsMobile8(true);
//				response.write(sock.getOutputStream(), 2, true);
				
				//Thread.sleep(2000*2);
				if(sentInitialNTMRequest) {
					sendMerchangtTopupRequest();
//					sendTopUpPaymentReversalRequest();
					sentInitialNTMRequest = false;
				}
			} catch (IOException ex) {
				log.error("Error sending request", ex);
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
	
	public static void start() throws InterruptedException, IOException, ParseException{
		stop();
				try {
					mfact = ConfigParser.createFromClasspathConfig("mobile8-config.xml");
				} catch (IOException e) {
					log.error("Failed to parse the config file", e);
					return;
				}
				
				Socket sock = null;
				Mobile8H2HBankChannelClient client = null;
				try {
					log.debug("Connecting to server");
					sock = new Socket("localhost", 9990);
					client = new Mobile8H2HBankChannelClient(sock);
				} catch (IOException e) {
					log.error("Failed to set up client socket", e);
					return;
				}
//				log.info("Sent request to server...");
				
				//send sign-on request
				//byte[] ReqMsg;
				//String strMsg =  "08008220000000000000040000000000000010201003310000010013";
				//ReqMsg = strMsg.getBytes();
				//request = mfact.parseMessage(ReqMsg,0);
				
				//
//				String STAN_11 = "001598";
//				String RRN_37 = "001025000084";
//				
//				String orignalSTAN = "001598";
//				String orignalTxnMMDD = "1003";
//				String OriginalTxnTime = "162502";
//				String dummyData = "1234567891234567";
//				String element90 = "0200" + orignalSTAN + orignalTxnMMDD + OriginalTxnTime + "153"	+ "153" + dummyData;
//		
//				BigDecimal amount = new BigDecimal(000000006000);
//				IsoMessage postPaidReversal = mfact.newMessage(0x400);
//				postPaidReversal.setIsoHeader("ISO0100");
//				postPaidReversal.setIsMobile8(true);
//				postPaidReversal.setValue(2, "6288291515820", IsoType.LLVAR, 16);
//				postPaidReversal.setValue(3, "880000", IsoType.NUMERIC, 6);
//				postPaidReversal.setValue(4, amount, IsoType.NUMERIC, 12);
//				postPaidReversal.setValue(7, "1025163502", IsoType.DATE10, 10);
//				postPaidReversal.setValue(11, STAN_11, IsoType.NUMERIC, 6);
//				postPaidReversal.setValue(12, "163502", IsoType.TIME, 6);
//				postPaidReversal.setValue(13, "1025", IsoType.DATE4, 4);
//				postPaidReversal.setValue(15, "1025", IsoType.DATE4, 4);
//				postPaidReversal.setValue(18, "6014", IsoType.NUMERIC, 4);
//				postPaidReversal.setValue(32, "881", IsoType.LLVAR, 11);
//				postPaidReversal.setValue(37, RRN_37, IsoType.ALPHA, 12);
//				postPaidReversal.setValue(41, "0000991622716855", IsoType.ALPHA, 16);
//				postPaidReversal.setValue(48, "6288291515820 000000025000", IsoType.LLLVAR, 120);
//				postPaidReversal.setValue(49, "360", IsoType.NUMERIC, 3);
//				postPaidReversal.setValue(60, "050", IsoType.LLLVAR, 3);
//				postPaidReversal.setValue(63, "055", IsoType.LLLVAR, 3);
//				postPaidReversal.setValue(90, element90, IsoType.NUMERIC, 42);
//				//postPaidPayment.setBinary(true);
//				System.out.println("TOPUP PAYMENT REVERSAL REQUEST:");
//				System.out.println("------------------------------");
//				System.out.println(new String(postPaidReversal.writeData()));
//				System.out.println("------------------------------");
//				System.out.println();
//							
//				postPaidReversal.write(sock.getOutputStream(), 2, true);
				//--
				IsoMessage SignOnReq = mfact.newMessage(0x800);
				SignOnReq.setIsoHeader("ISO0100");
				SignOnReq.setIsMobile8(true);
				SignOnReq.setValue(7, "1108124959", IsoType.DATE10, 10);
				SignOnReq.setValue(11, "000004", IsoType.NUMERIC, 6);
				SignOnReq.setValue(48, "test                test                ", IsoType.LLLVAR, 120);
				SignOnReq.setValue(70, "001", IsoType.NUMERIC, 3);
				
				log.debug("Sending sign-on request");
				System.out.println("------------------------------");
				System.out.println(new String(SignOnReq.writeData()));
				System.out.println("------------------------------");
				
				SignOnReq.write(sock.getOutputStream(), 2, true);
				Thread.sleep(2000);
				
				// send the h2h topup
				client.run();
				stop();
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
