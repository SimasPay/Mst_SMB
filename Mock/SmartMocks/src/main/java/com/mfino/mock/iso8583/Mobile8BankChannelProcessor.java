package com.mfino.mock.iso8583;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

public class Mobile8BankChannelProcessor implements Runnable {
	private byte[] msg;
	private Socket sock;
	private MessageFactory mfact;
	private boolean sentInitialNTMRequest;
	private static Logger	log	= LoggerFactory.getLogger(Mobile8BankChannelProcessor.class);
	private IsoMessage isomessage = null;

	public Mobile8BankChannelProcessor(byte[] buf, Socket s,MessageFactory messfact,IsoMessage iso) {
		msg = buf;
		sock = s;
		try {
			mfact = ConfigParser.createFromClasspathConfig("mobile8-config.xml");
		} catch (IOException e) {
			log.error("Failed to parse the config file", e);
			return;
		}
		isomessage = iso;
	}
	public void sendTopUpPaymentRequest(Socket socket,IsoMessage message) throws IOException {
		System.out.println("TOPUP PAYMENT REQUEST:");
		System.out.println("------------------------------");
		System.out.println(new String(message.writeData()));
		System.out.println("------------------------------");
		System.out.println();

		message.write(socket.getOutputStream(), 2, true);
	}
	public void sendTopUpPaymentReversalRequest() throws IOException {
		// Here now send the Request.
		String STAN_11 = "001598";
		String RRN_37 = "001025000084";
		
		String orignalSTAN = "001598";
		String orignalTxnMMDD = "1003";
		String OriginalTxnTime = "162502";
		String dummyData = "1234567891234567";
		String element90 = "0200" + orignalSTAN + orignalTxnMMDD + OriginalTxnTime + "153"	+ "153" + dummyData;

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
		postPaidReversal.setValue(48, " 62881162109611002", IsoType.LLLVAR, 120);
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
	public void sendTopUpPaymentRequest() throws IOException {
		// Here now send the Request.
		BigDecimal amount = new BigDecimal(000000006000);
		IsoMessage postPaidPayment = mfact.newMessage(0x200);
		postPaidPayment.setValue(2, "6288116210961", IsoType.LLVAR, 16);
		postPaidPayment.setValue(3, "570000", IsoType.NUMERIC, 6);
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
		postPaidPayment.setValue(48, " 62881162109611002", IsoType.LLLVAR, 120);
		postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
		postPaidPayment.setValue(63, "055", IsoType.LLLVAR, 3);
		// postPaidPayment.setBinary(true);
		System.out.println("TOPUP PAYMENT REQUEST:");
		System.out.println("------------------------------");
		System.out.println(new String(postPaidPayment.writeData()));
		System.out.println("------------------------------");
		System.out.println();

		postPaidPayment.write(sock.getOutputStream(), 2, true);
	}

	public void sendPostPaidBillPaymentRequest() throws IOException {
		// Here now send the Request.
		BigDecimal amount = new BigDecimal(000000006000);
		IsoMessage postPaidPayment = mfact.newMessage(0x200);
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
		postPaidPayment.setValue(48, " 62881162109611002", IsoType.LLLVAR, 120);
		postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
		postPaidPayment.setValue(63, "055", IsoType.LLLVAR, 3);
		// postPaidPayment.setBinary(true);
		System.out.println("POST PAID PAYMENT REQUEST:");
		System.out.println("------------------------------");
		System.out.println(new String(postPaidPayment.writeData()));
		System.out.println("------------------------------");
		System.out.println();

		postPaidPayment.write(sock.getOutputStream(), 2, true);
	}

	public void sendPostPaidBillPaymentInquiryRequest() throws IOException {
		// Here now send the Request.
		// PI = 1002, 6288911111112
		BigDecimal amount = new BigDecimal(000000000000);
		IsoMessage postPaidInquiry = mfact.newMessage(0x200);
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
		postPaidInquiry.setValue(48, " 62881162109611002", IsoType.LLLVAR, 120);
		postPaidInquiry.setValue(49, "360", IsoType.NUMERIC, 3);
		postPaidInquiry.setValue(63, "055", IsoType.LLLVAR, 3);
		// postPaidInquiry.setBinary(true);
		System.out.println("POST PAID INQUIRY REQUEST:");
		System.out.println("------------------------------");
		System.out.println(new String(postPaidInquiry.writeData()));
		System.out.println("------------------------------");
		System.out.println();

		postPaidInquiry.write(sock.getOutputStream(), 2, true);
	}

	public void sendPostPaidBillPaymentReversalRequest() throws IOException {
		// Here now send the Request.
		BigDecimal amount = new BigDecimal(000000006000);
		IsoMessage postPaidPayment = mfact.newMessage(0x400);
		postPaidPayment.setValue(2, "6288116210961", IsoType.LLVAR, 16);
		postPaidPayment.setValue(3, "500099", IsoType.NUMERIC, 6);
		postPaidPayment.setValue(4, amount, IsoType.AMOUNT, 12);
		postPaidPayment.setValue(7, "1025163502", IsoType.DATE10, 10);
		postPaidPayment.setValue(11, "001598", IsoType.NUMERIC, 6);
		postPaidPayment.setValue(12, "163502", IsoType.TIME, 6);
		postPaidPayment.setValue(13, "1025", IsoType.DATE4, 4);
		postPaidPayment.setValue(15, "1025", IsoType.DATE4, 4);
		postPaidPayment.setValue(18, "6014", IsoType.NUMERIC, 4);
		postPaidPayment.setValue(32, "881", IsoType.LLVAR, 11);
		postPaidPayment.setValue(37, "001025000084", IsoType.ALPHA, 12);
		postPaidPayment.setValue(41, "0000991622716855", IsoType.ALPHA, 16);
		postPaidPayment.setValue(48, " 62881162109611002", IsoType.LLLVAR, 120);
		postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
		postPaidPayment.setValue(60, "050", IsoType.LLLVAR, 3);
		postPaidPayment.setValue(63, "055", IsoType.LLLVAR, 3);
		postPaidPayment.setValue(90, "", IsoType.NUMERIC, 42);
		// postPaidPayment.setBinary(true);
		System.out.println("POST PAID PAYMENT REQUEST:");
		System.out.println("------------------------------");
		System.out.println(new String(postPaidPayment.writeData()));
		System.out.println("------------------------------");
		System.out.println();

		postPaidPayment.write(sock.getOutputStream(), 2, true);
	}

	public void run() {
		try {
			log.debug(String.format("Parsing incoming: '%s'", new String(msg)));
			String strMsg = new String(msg);
			if (strMsg.contains("I")) {
				strMsg = strMsg.substring(9);
				strMsg = strMsg.substring(0, strMsg.length() - 1);
				msg = strMsg.getBytes();
			}
			// mfact.setUseBinaryMessages(true);
			IsoMessage incoming = mfact.parseMessage(msg, 0);
			// log.debug("\n" + incoming.toDebugString());

			switch (incoming.getType()) {
			case 0x810:
				log.debug("Done with response message");
				sendPostPaidBillPaymentInquiryRequest();
				break;
			case 0x210:
				if ("380000".equals(incoming.getObjectValue(3))) {
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
			case 0x400:
				return;
			default:
				log.error("Unrecognonized message type");
				break;
			}

			log.info("Sending response ISO message:\n"
					+ response.toDebugString());
			response.write(sock.getOutputStream(), 2, true);

			// Thread.sleep(2000*2);
			if (sentInitialNTMRequest) {
				// sendPostPaidBillPaymentInquiryRequest();
				// sendPostPaidBillPaymentRequest();
				// sendPostPaidBillPaymentReversalRequest();
				sendTopUpPaymentRequest();				
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

	public void sendSignOnResponse() throws Exception {
		IsoMessage incoming = mfact.parseMessage(msg, 0);
		IsoMessage response = mfact.createResponse(incoming);
		response.setValue(39, "00", IsoType.ALPHA, 2);
		response.write(sock.getOutputStream(), 2, true);
	}
}
