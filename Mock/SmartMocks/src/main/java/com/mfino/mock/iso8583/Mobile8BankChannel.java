package com.mfino.mock.iso8583;

import java.io.IOException;
import java.math.BigDecimal;
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

public class Mobile8BankChannel implements Runnable {

	private static Logger	log	= LoggerFactory.getLogger(Mobile8BankChannel.class);

	private static ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(5);
	private static MessageFactory mfact;

	private Socket socket;
	
	private static boolean sentInitialNTMRequest;

	public Mobile8BankChannel(Socket sock) {
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
			BigDecimal amount = new BigDecimal(000000006000);
			IsoMessage postPaidPayment = mfact.newMessage(0x200);
			postPaidPayment.setIsoHeader("ISO0100");
			postPaidPayment.setIsMobile8(true);
			postPaidPayment.setValue(2, "6288116210961", IsoType.LLVAR, 16);
			postPaidPayment.setValue(3, "880000", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.NUMERIC, 12);
			postPaidPayment.setValue(7, "1025163502", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "001598", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "163502", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "1025", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "1025", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "6014", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "6622", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "001025000084", IsoType.ALPHA, 12);
			postPaidPayment.setValue(41, "0000991622716855", IsoType.ALPHA, 16);
			postPaidPayment.setValue(48, "6288116210961 000000025000", IsoType.LLLVAR, 120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "055", IsoType.LLLVAR, 3);
			
			System.out.println("MERCHANT TOPUP REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidPayment.writeData()));
			System.out.println("------------------------------");
			System.out.println();
						
			postPaidPayment.write(sock.getOutputStream(), 2, true);		
		}
		
		private void sendTopUpPaymentRequest() throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(000000006000);
			IsoMessage postPaidPayment = mfact.newMessage(0x200);
			postPaidPayment.setIsoHeader("ISO0100");
			postPaidPayment.setIsMobile8(true);
			postPaidPayment.setValue(2, "6288116210961", IsoType.LLVAR, 16);
			postPaidPayment.setValue(3, "570000", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.NUMERIC, 12);
			postPaidPayment.setValue(7, "1025163502", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "000015", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "141400", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "0729", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "0729", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "6014", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "881", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "00102500008 ", IsoType.ALPHA, 12);
			postPaidPayment.setValue(41, "0000991622716855", IsoType.ALPHA, 16);
			postPaidPayment.setValue(48, "6288116210961 000000025000", IsoType.LLLVAR, 120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "055", IsoType.LLLVAR, 3);
			//postPaidPayment.setBinary(true);
			System.out.println("TOPUP PAYMENT REQUEST:");
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
			
			String orignalSTAN = "000015";
			String orignalTxnMMDD = "0729";
			String OriginalTxnTime = "141400";
			String dummyData = "0000000000000000000000";
			String element90 = "0200" + orignalSTAN + orignalTxnMMDD + OriginalTxnTime + dummyData;
	
			BigDecimal amount = new BigDecimal(000000006000);
			IsoMessage postPaidReversal = mfact.newMessage(0x400);
			postPaidReversal.setIsoHeader("ISO0100");
			postPaidReversal.setIsMobile8(true);
			postPaidReversal.setValue(2, "6288116210961", IsoType.LLVAR, 16);
			postPaidReversal.setValue(3, "570000", IsoType.NUMERIC, 6);
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
			postPaidReversal.setValue(48, "6288116210961 000000025000", IsoType.LLLVAR, 120);
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
		
		private void sendPostPaidBillPaymentRequest() throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(6000);
			IsoMessage postPaidPayment = mfact.newMessage(0x200);
			postPaidPayment.setIsoHeader("ISO0100");
			postPaidPayment.setIsMobile8(true);
			postPaidPayment.setValue(2, "6288116210961", IsoType.LLVAR, 16);
			postPaidPayment.setValue(3, "500099", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.NUMERIC, 12);
			postPaidPayment.setValue(7, "1025163502", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "001598", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "163502", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "1025", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "1025", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "6014", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "881", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "001025000084", IsoType.ALPHA, 12);
			postPaidPayment.setValue(41, "0000991622716855", IsoType.ALPHA, 16);
			postPaidPayment.setValue(48, "6288116210961 222285634       SINAR SOSRO PT QQ             000000020301", IsoType.LLLVAR, 120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "055", IsoType.LLLVAR, 3);
			//postPaidPayment.setBinary(true);
			System.out.println("POST PAID PAYMENT REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidPayment.writeData()));
			System.out.println("------------------------------");
			System.out.println();
						
			postPaidPayment.write(sock.getOutputStream(), 2, true);		
		}
		
		private void sendPostPaidBillPaymentInquiryRequest() throws IOException {
			// Here now send the Request.
			// PI = 1002, 6288911111112
			BigDecimal amount = new BigDecimal(000000000000);
			IsoMessage postPaidInquiry = mfact.newMessage(0x200);
			postPaidInquiry.setIsoHeader("ISO0100");
			postPaidInquiry.setIsMobile8(true);
			postPaidInquiry.setValue(2, "6288116210961", IsoType.LLVAR, 16);
			postPaidInquiry.setValue(3, "380099", IsoType.NUMERIC, 6);
			postPaidInquiry.setValue(4, amount, IsoType.NUMERIC, 12);
			postPaidInquiry.setValue(7, "1025163502", IsoType.DATE10, 10);
			postPaidInquiry.setValue(11, "001598", IsoType.NUMERIC, 6);
			postPaidInquiry.setValue(12, "163502", IsoType.TIME, 6);
			postPaidInquiry.setValue(13, "1025", IsoType.DATE4, 4);
			postPaidInquiry.setValue(15, "1025", IsoType.DATE4, 4);
			postPaidInquiry.setValue(18, "6014", IsoType.NUMERIC, 4);
			postPaidInquiry.setValue(32, "881", IsoType.LLVAR, 11);
			postPaidInquiry.setValue(37, "001025000084", IsoType.ALPHA, 12);
			postPaidInquiry.setValue(41, "0000991622716855", IsoType.ALPHA, 16);
			postPaidInquiry.setValue(48, "088116210961  1002", IsoType.LLLVAR, 120);
			postPaidInquiry.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidInquiry.setValue(63, "055", IsoType.LLLVAR, 3);
			//postPaidInquiry.setBinary(true);
			System.out.println("POST PAID INQUIRY REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidInquiry.writeData()));
			System.out.println("------------------------------");
			System.out.println();
		
			postPaidInquiry.write(sock.getOutputStream(), 2, true);	
		}
		
		private void sendPostPaidBillPaymentReversalRequest() throws IOException {
			// Here now send the Request.
			String STAN_11 = "001598";
			String RRN_37 = "001025000084";
			
			String orignalSTAN = "001598";
			String orignalTxnMMDD = "1003";
			String OriginalTxnTime = "162502";
			String dummyData = "1234567891234567";
			String element90 = "0200" + orignalSTAN + orignalTxnMMDD + OriginalTxnTime + "153"	+ "153" + dummyData;
						
			BigDecimal amount = new BigDecimal(000000006000);
			IsoMessage postPaidPaymentReversal = mfact.newMessage(0x400);
			postPaidPaymentReversal.setIsoHeader("ISO0100");
			postPaidPaymentReversal.setIsMobile8(true);
			postPaidPaymentReversal.setValue(2, "6288116210961", IsoType.LLVAR, 16);
			postPaidPaymentReversal.setValue(3, "500099", IsoType.NUMERIC, 6);
			postPaidPaymentReversal.setValue(4, amount, IsoType.AMOUNT, 12);
			postPaidPaymentReversal.setValue(7, "1025163502", IsoType.DATE10, 10);
			postPaidPaymentReversal.setValue(11, STAN_11, IsoType.NUMERIC, 6);
			postPaidPaymentReversal.setValue(12, "163502", IsoType.TIME, 6);
			postPaidPaymentReversal.setValue(13, "1025", IsoType.DATE4, 4);
			postPaidPaymentReversal.setValue(15, "1025", IsoType.DATE4, 4);
			postPaidPaymentReversal.setValue(18, "6014", IsoType.NUMERIC, 4);
			postPaidPaymentReversal.setValue(32, "881", IsoType.LLVAR, 11);
			postPaidPaymentReversal.setValue(37, RRN_37, IsoType.ALPHA, 12);
			postPaidPaymentReversal.setValue(41, "0000991622716855", IsoType.ALPHA, 16);
			postPaidPaymentReversal.setValue(48, "6288116210961 000000020301", IsoType.LLLVAR, 120);
			postPaidPaymentReversal.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPaymentReversal.setValue(60, "050", IsoType.LLLVAR, 3);
			postPaidPaymentReversal.setValue(63, "055", IsoType.LLLVAR, 3);
			postPaidPaymentReversal.setValue(90, element90, IsoType.NUMERIC, 42);
			//postPaidPayment.setBinary(true);
			System.out.println("POST PAID PAYMENT REVERSAL REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidPaymentReversal.writeData()));
			System.out.println("------------------------------");
			System.out.println();
			
			
			postPaidPaymentReversal.write(sock.getOutputStream(), 2, true);		
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
					log.debug("Done with response message");
					sendPostPaidBillPaymentInquiryRequest();
					break;
				case 0x210:
					if("380000".equals(incoming.getObjectValue(3))) {
						sendPostPaidBillPaymentRequest();
					}
					break;
				}

				// Create a response, this copies the incoming
				IsoMessage response = mfact.createResponse(incoming);
				
							
				// Determine what message this is
				// Based on each type of message, create a response
				// In real system, here we would be calling the business logic
				// to do some real work, but here
				// we just return "true"/"good" for every one.
				response.setIsoHeader("ISO0100");
				response.setIsMobile8(true);
//				response.setValue(7, "0400000000", IsoType.DATE10, 10);
//				response.setValue(11, "000000", IsoType.NUMERIC, 6);
//				response.setValue(48, "1108124959000004040test                test                ", IsoType.LLLVAR, 120);
//				response.setValue(70, "001", IsoType.NUMERIC, 3);
//				
				switch (incoming.getType()) {
				// 1. Network Management Messages
				case 0x800:
					// A. Network SIGN-ON
					if ("001".equals(incoming.getObjectValue(70))) {
						log.debug("SIGN-ON");
						response.setValue(39, "00", IsoType.ALPHA, 2);
						sentInitialNTMRequest =true;
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
					return;
				case 0x400:
					return;
				default:
					log.error("Unrecognonized message type");
					break;
				}

				log.info("Sending response ISO message:\n" + response.toDebugString());
				response.setIsMobile8(true);
				response.write(sock.getOutputStream(), 2, true);
				
				//Thread.sleep(2000*2);
				if(sentInitialNTMRequest) {
//					sendPostPaidBillPaymentInquiryRequest();
//					sendPostPaidBillPaymentRequest();
					//sendPostPaidBillPaymentReversalRequest();
					sendTopUpPaymentRequest();
//					sendTopUpPaymentReversalRequest();
					//sendMerchangtTopupRequest();
					sentInitialNTMRequest = false;
				}
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
					mfact = ConfigParser.createFromClasspathConfig("mobile8-config.xml");
				} catch (IOException e) {
					log.error("Failed to parse the config file", e);
					return;
				}
				log.info("Setting up server socket...");
				ServerSocket server;
				try {
					server = new ServerSocket(9995);
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
					
				
					new Thread(new Mobile8BankChannel(sock), "j8583-server").start();
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
