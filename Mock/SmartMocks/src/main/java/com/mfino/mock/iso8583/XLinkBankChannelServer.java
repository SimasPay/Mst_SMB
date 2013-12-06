package com.mfino.mock.iso8583;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

public class XLinkBankChannelServer implements Runnable {

	private static Logger	log	= LoggerFactory.getLogger(XLinkBankChannelServer.class);

	private static ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(5);
	private static MessageFactory mfact;

	private Socket socket;

	private boolean sentInitialNTMRequest;

	public XLinkBankChannelServer(Socket sock) {
		socket = sock;
	}

	public void run() {
		int count = 0;
		byte[] lenbuf = new byte[4];
		try {
			
			boolean firstTime = true;
			while (socket != null && socket.isConnected()
					&& Thread.currentThread().isAlive()
					&& !Thread.currentThread().isInterrupted()) {

				if (socket.getInputStream().read(lenbuf) == 4) {			

					int size = Integer.parseInt(new String(lenbuf));

					byte[] buf = new byte[size];

					socket.getInputStream().read(buf);
					System.out.println("THE DATA IS " + new String(buf));
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
		
		private void fillBasicParams(IsoMessage basicIsoMsg){			
			
			BigDecimal amount = new BigDecimal(000000100000);			
			Date now = new Date();
			String MMDD = String.format("%02d%02d", now.getMonth()+1, now.getDate());
			String hhmmss = String.format("%02d%02d%02d",now.getHours(),now.getMinutes(),now.getSeconds());;
			String transmissionDateTime = MMDD + hhmmss;
			
			
			basicIsoMsg.setValue(2, "6396870000678412", IsoType.LLVAR,16);
			//basicIsoMsg.setValue(3, "561000", IsoType.NUMERIC, 6); //Deafult is Topup.
			basicIsoMsg.setValue(4, amount, IsoType.AMOUNT, 12);
			basicIsoMsg.setValue(7, "0707132720", IsoType.DATE10, 10); //MMDDhhmmss
			basicIsoMsg.setValue(11, "002001", IsoType.NUMERIC, 6);
			basicIsoMsg.setValue(12, "165909", IsoType.TIME, 6);//hhmmss in Local time
			basicIsoMsg.setValue(13, MMDD, IsoType.DATE4, 4); //MMDD
			basicIsoMsg.setValue(15, MMDD, IsoType.DATE4, 4);
			basicIsoMsg.setValue(18, "6011", IsoType.NUMERIC, 4);
			basicIsoMsg.setValue(32, "153", IsoType.LLVAR, 11);
			basicIsoMsg.setValue(33, "153", IsoType.LLVAR, 11);
			basicIsoMsg.setValue(37, "000000002001", IsoType.ALPHA, 12);
			
			basicIsoMsg.setValue(41, "15302601", IsoType.ALPHA, 8);			
			basicIsoMsg.setValue(42, "000000ID0010026", IsoType.ALPHA, 15);
			basicIsoMsg.setValue(43, StringUtils.rightPad("SMS SMART", 40), IsoType.ALPHA, 40);
			basicIsoMsg.setValue(49, "360", IsoType.NUMERIC, 3);
			//basicIsoMsg.setValue(98, StringUtils.rightPad("019003", 25), IsoType.ALPHA, 25); //Default is Topup			
		}
		

		private IsoMessage getTopupMsg(String STAN, String RRN, String MDN, BigDecimal amount) {
			
			IsoMessage topupRequest = mfact.newMessage(0x200);		
			fillBasicParams(topupRequest);			
		
			topupRequest.setValue(3, "561000", IsoType.NUMERIC, 6); //Deafult is Topup.
			topupRequest.setValue(4, amount, IsoType.AMOUNT, 12);
			topupRequest.setValue(11, STAN, IsoType.NUMERIC, 6);
			topupRequest.setValue(37, RRN, IsoType.ALPHA, 12);			
			topupRequest.setValue(61, StringUtils.rightPad(MDN, 16), IsoType.LLLVAR, 16);
			topupRequest.setValue(63, "881", IsoType.LLLVAR, 3);
			topupRequest.setValue(98, StringUtils.rightPad("019003", 25), IsoType.ALPHA, 25); //Default is Topup
						
			return topupRequest;			
		}
		
		private IsoMessage getPostpaidPaymentMsg(String STAN, String RRN, String MDN, BigDecimal amount) {
			
			IsoMessage topupRequest = mfact.newMessage(0x200);		
			fillBasicParams(topupRequest);
			String amountStr =StringUtils.leftPad(amount.toString(), 12, '0'); 
		
			
			String element61 = StringUtils.rightPad(MDN, 206);
			element61 += amountStr;
			
			topupRequest.setValue(3, "501000", IsoType.NUMERIC, 6); //Deafult is Topup.
			topupRequest.setValue(4, amountStr, IsoType.NUMERIC, 12);
			topupRequest.setValue(11, STAN, IsoType.NUMERIC, 6);
			topupRequest.setValue(37, RRN, IsoType.ALPHA, 12);
			topupRequest.setValue(61, StringUtils.rightPad(element61, 292), IsoType.LLLVAR, 16);
			topupRequest.setValue(63, "881", IsoType.LLLVAR, 3);
			topupRequest.setValue(98, StringUtils.rightPad("019004", 25), IsoType.ALPHA, 25); //Default is Topup
						
			return topupRequest;			
		}
						
		private void sendTopupPaymentRequest() throws IOException {
			String STAN_11 = "000112";
			String RRN_37 = "000000000112";
			String MDN_61 = "6288116210961";
			BigDecimal amount_4 = new BigDecimal(000000100000);
			
			IsoMessage topupRequest = getTopupMsg(STAN_11, RRN_37, MDN_61, amount_4); 
			topupRequest.setValue(40, "001", IsoType.ALPHA, 3);
			
			System.out.println("PRE PAID TOPUP REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(topupRequest.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			topupRequest.write(sock.getOutputStream(), 4, false);
		}

		private void sendTopupPaymentReversalRequest() throws IOException {
			
			String STAN_11 = "000112";
			String RRN_37 = "000000000112";
			String MDN_61 = "6288116210961";
			BigDecimal amount_4 = new BigDecimal(000000100000);			
			
			String orignalSTAN = "000111";
			String orignalTxnTime = "0707132720"; //MMDDhhmmss
				
			IsoMessage topupReversalRequest = getTopupMsg(STAN_11, RRN_37, MDN_61, amount_4);
			topupReversalRequest.setType(0x420);
			String element90 = "0200" + orignalSTAN + orignalTxnTime + "153"	+ "153";
			topupReversalRequest.setValue(90, element90, IsoType.NUMERIC, 26);
			
			
			System.out.println("PRE PAID TOPUP REVERSAL REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(topupReversalRequest.writeData()));
			System.out.println(topupReversalRequest.toDebugString());
			System.out.println("------------------------------");
			System.out.println();

			topupReversalRequest.write(sock.getOutputStream(), 4, false);
		}		

		
		private void sendPostPaidBillPaymentRequest() throws IOException {
			
			String STAN_11 = "000171";
			String RRN_37 = "000000000171";
			String MDN_61 = "6288116210961";
			BigDecimal amount_4 = new BigDecimal(100000);
			
			
			IsoMessage postPaidPayment = getPostpaidPaymentMsg(STAN_11, RRN_37, MDN_61, amount_4);
			postPaidPayment.setValue(40, "001", IsoType.ALPHA, 3);
			
			System.out.println("POST PAID PAYMENT REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidPayment.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			postPaidPayment.write(sock.getOutputStream(), 4, false);
		}

		private void sendPostPaidBillPaymentReversalRequest()
				throws IOException {
						
			String STAN_11 = "000199";
			String RRN_37 = "000000000171";
			String MDN_61 = "6288116210961";
			BigDecimal amount_4 = new BigDecimal(000000100000);
						
			String orignalSTAN = "000171";
			String orignalTxnTime = "0707132720";
			String element90 = "0200" + orignalSTAN + orignalTxnTime + "153"	+ "153";
			
			IsoMessage postPaidPaymentReversal = getPostpaidPaymentMsg(STAN_11, RRN_37, MDN_61, amount_4);
			postPaidPaymentReversal.setValue(61, StringUtils.rightPad(MDN_61, 16), IsoType.LLLVAR, 16);
			postPaidPaymentReversal.setValue(90, element90, IsoType.NUMERIC, 26);
			postPaidPaymentReversal.setType(0x420);
						
			System.out.println("POST PAID PAYMENT REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidPaymentReversal.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			postPaidPaymentReversal.write(sock.getOutputStream(), 4, false);
		}

		private void sendPostPaidBillPaymentInquiryRequest() throws IOException {			
			
			String STAN_11 = "000111";
			String RRN_37 = "000000000111";
			String MDN_61 = "6288116210961";
						
			IsoMessage billInquiryRequest = mfact.newMessage(0x200);		
			fillBasicParams(billInquiryRequest);			
		
			billInquiryRequest.setValue(3, "380000", IsoType.NUMERIC, 6); 
			//billInquiryRequest.setValue(4, "000000000000", IsoType.AMOUNT, 12);
			billInquiryRequest.setValue(11, STAN_11, IsoType.NUMERIC, 6);
			billInquiryRequest.setValue(37, RRN_37, IsoType.ALPHA, 12);
			billInquiryRequest.setValue(40, "001", IsoType.ALPHA, 3);
			billInquiryRequest.setValue(61, StringUtils.rightPad(MDN_61, 16), IsoType.LLLVAR, 16);
			billInquiryRequest.setValue(98, StringUtils.rightPad("019004", 25), IsoType.ALPHA, 25); //Default is Topup
					
			System.out.println("POST PAID INQUIRY REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(billInquiryRequest.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			billInquiryRequest.write(sock.getOutputStream(), 4, false);
		}
		
		private void sendRegisterRequest() throws IOException {			
			
			Date now = new Date();
			String MMDD = String.format("%02d%02d", now.getMonth()+1, now.getDate());
			String hhmmss = String.format("%02d%02d%02d",now.getHours(),now.getMinutes(),now.getSeconds());;
			String transmissionDateTime = MMDD + hhmmss;
			
			String STAN_11 = "000111";
			String RRN_37 = "000000000111";
			String MDN_48 = "6288116210961";
			BigDecimal amount = new BigDecimal(000000000000);
			
			IsoMessage registerRequest = mfact.newMessage(0x200);		
			registerRequest.setValue(2, "6396870000678412", IsoType.LLVAR,16);
			registerRequest.setValue(3, "847273", IsoType.NUMERIC, 6); 
			registerRequest.setValue(4, amount, IsoType.AMOUNT, 12);
			registerRequest.setValue(7, transmissionDateTime, IsoType.DATE10, 10); //MMDDhhmmss
			registerRequest.setValue(11, STAN_11, IsoType.NUMERIC, 6);
			registerRequest.setValue(12, "165909", IsoType.TIME, 6);				//hhmmss in Local time
			registerRequest.setValue(13, MMDD, IsoType.DATE4, 4); 					//MMDD
			registerRequest.setValue(15, MMDD, IsoType.DATE4, 4);
			registerRequest.setValue(18, "6014", IsoType.NUMERIC, 4);
			registerRequest.setValue(32, "153", IsoType.LLVAR, 11);
			registerRequest.setValue(37, RRN_37, IsoType.ALPHA, 12);
			registerRequest.setValue(41, "15302601", IsoType.ALPHA, 8);			
			registerRequest.setValue(48, MDN_48,IsoType.LLLVAR, 200);
			registerRequest.setValue(49, "360", IsoType.NUMERIC, 3);
			registerRequest.setValue(63, "881", IsoType.LLLVAR, 3);
			
			System.out.println("REGISTER REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(registerRequest.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			registerRequest.write(sock.getOutputStream(), 4, false);
		}
		
		private void sendUnRegisterRequest() throws IOException {			
			
			Date now = new Date();
			String MMDD = String.format("%02d%02d", now.getMonth()+1, now.getDate());
			String hhmmss = String.format("%02d%02d%02d",now.getHours(),now.getMinutes(),now.getSeconds());;
			String transmissionDateTime = MMDD + hhmmss;
			
			String STAN_11 = "000111";
			String RRN_37 = "000000000111";
			String MDN_48 = "6288116210961";
			BigDecimal amount = new BigDecimal(000000000000);
			
			IsoMessage registerRequest = mfact.newMessage(0x200);		
			registerRequest.setValue(2, "6396870000678412", IsoType.LLVAR,16);
			registerRequest.setValue(3, "847286", IsoType.NUMERIC, 6); 
			registerRequest.setValue(4, amount, IsoType.AMOUNT, 12);
			registerRequest.setValue(7, transmissionDateTime, IsoType.DATE10, 10); //MMDDhhmmss
			registerRequest.setValue(11, STAN_11, IsoType.NUMERIC, 6);
			registerRequest.setValue(12, "165909", IsoType.TIME, 6);				//hhmmss in Local time
			registerRequest.setValue(13, MMDD, IsoType.DATE4, 4); 					//MMDD
			registerRequest.setValue(15, MMDD, IsoType.DATE4, 4);
			registerRequest.setValue(18, "6014", IsoType.NUMERIC, 4);
			registerRequest.setValue(32, "153", IsoType.LLVAR, 11);
			registerRequest.setValue(37, RRN_37, IsoType.ALPHA, 12);
			registerRequest.setValue(41, "15302601", IsoType.ALPHA, 8);			
			registerRequest.setValue(48, MDN_48,IsoType.LLLVAR, 200);
			registerRequest.setValue(49, "360", IsoType.NUMERIC, 3);
			registerRequest.setValue(63, "888", IsoType.LLLVAR, 3);
			
			System.out.println("UNREGISTER REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(registerRequest.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			registerRequest.write(sock.getOutputStream(), 4, false);
		}
		
private void sendQueryLowBalRegisteredMDNsRequest() throws IOException {			
			
			Date now = new Date();
			String MMDD = String.format("%02d%02d", now.getMonth()+1, now.getDate());
			String hhmmss = String.format("%02d%02d%02d",now.getHours(),now.getMinutes(),now.getSeconds());;
			String transmissionDateTime = MMDD + hhmmss;
			
			String STAN_11 = "000111";
			String RRN_37 = "000000000111";
			BigDecimal amount = new BigDecimal(000000000000);
			
			IsoMessage registerRequest = mfact.newMessage(0x200);		
			registerRequest.setValue(2, "0000000000000000", IsoType.LLVAR,16);
			registerRequest.setValue(3, "847287", IsoType.NUMERIC, 6); 
			registerRequest.setValue(4, amount, IsoType.AMOUNT, 12);
			registerRequest.setValue(7, transmissionDateTime, IsoType.DATE10, 10); //MMDDhhmmss
			registerRequest.setValue(11, STAN_11, IsoType.NUMERIC, 6);
			registerRequest.setValue(12, "165909", IsoType.TIME, 6);				//hhmmss in Local time
			registerRequest.setValue(13, MMDD, IsoType.DATE4, 4); 					//MMDD
			registerRequest.setValue(15, MMDD, IsoType.DATE4, 4);
			registerRequest.setValue(18, "6014", IsoType.NUMERIC, 4);
			registerRequest.setValue(32, "153", IsoType.LLVAR, 11);
			registerRequest.setValue(37, RRN_37, IsoType.ALPHA, 12);
			registerRequest.setValue(41, "15302601", IsoType.ALPHA, 8);			
			registerRequest.setValue(49, "360", IsoType.NUMERIC, 3);
			registerRequest.setValue(63, "881", IsoType.LLLVAR, 3);
			
			System.out.println("QUERY LOW BAL REGISTERED MDNs REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(registerRequest.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			registerRequest.write(sock.getOutputStream(), 4, false);
		}

		public void run() {
			try {
				log.debug(String.format("Parsing incoming: '%s'", new String(
						msg)));
				
				IsoMessage incoming = mfact.parseMessage(msg, 0);
				log.debug("\n" + incoming.toDebugString());

				switch (incoming.getType()) {
				case 0x810:
					log.debug("Done with response message");
					// sendPostPaidBillPaymentInquiryRequest();
					break;
				case 0x210:
				case 0x430:
//					if ("380000".equals(incoming.getObjectValue(3))) {
//						// sendPostPaidBillPaymentRequest();
//					}
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
						sentInitialNTMRequest = true;
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
					} else {
						log.error("Unrecognonized message type");
					}
					break;

				case 0x200:
					return;
				case 0x420:
				case 0x421:	
					return;
				default:
					log.error("Unrecognonized message type");
					break;
				}

				log.info("Sending response ISO message:\n"	+ response.toDebugString());
				response.write(sock.getOutputStream(), 4, false);

				if (sentInitialNTMRequest) {
					 sendPostPaidBillPaymentRequest();
//					sendPostPaidBillPaymentReversalRequest();
//					 sendTopupPaymentRequest();
//					 sendTopupPaymentReversalRequest();
//					sendPostPaidBillPaymentInquiryRequest();
//					sendRegisterRequest();
//					sendUnRegisterRequest();
//					sendQueryLowBalRegisteredMDNsRequest();
					sentInitialNTMRequest = false;
				}
			} catch (ParseException ex) {
				log.error("Error parsing incoming message", ex);
			} catch (IOException ex) {
				log.error("Error sending response", ex);
			} catch (Throwable t) {
				log.error("Unexpected error", t);
			}
		}
	}

	private static Thread worker = null;

	public static void stop() {
		if (isRunning()) {
			worker.interrupt();
		}
	}

	public static void start() {
		stop();

		worker = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mfact = ConfigParser
							.createFromClasspathConfig("xlink-config.xml");
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
					log.info(String.format("New connection from %s:%s", sock
							.getInetAddress(), sock.getPort()));

					new Thread(new XLinkBankChannelServer(sock),
							"j8583-server").start();
				}
			}
		});
		worker.start();
	}

	public static boolean isRunning() {
		return worker != null && worker.isAlive();
	}

	public static void main(String[] args) throws Exception {
		start();

		while (true) {
			if (isRunning()) {
				Thread.sleep(1000);
			} else {
				break;
			}
		}
	}

}
