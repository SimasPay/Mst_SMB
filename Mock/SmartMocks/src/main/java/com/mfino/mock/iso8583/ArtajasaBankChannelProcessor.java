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

public class ArtajasaBankChannelProcessor implements Runnable {
		private byte[] msg;
		private Socket sock;
		private MessageFactory mfact;
		private boolean sentInitialNTMRequest;
		private static Logger	log	= LoggerFactory.getLogger(ArtajasaBankChannelProcessor.class);
		private IsoMessage isomessage=null;


		public ArtajasaBankChannelProcessor(byte[] buf, Socket s,MessageFactory messfact,IsoMessage iso) {
			msg = buf;
			sock = s;
			try {
				mfact = ConfigParser.createFromClasspathConfig("bank-config.xml");
			} catch (IOException e) {
				log.error("Failed to parse the config file", e);
				return;
			}
			isomessage=iso;			
		}
		
		public void sendSignOnResponse() throws Exception {
			IsoMessage incoming = mfact.parseMessage(msg, 0);
			IsoMessage response = mfact.createResponse(incoming);
			response.setValue(39, "00", IsoType.ALPHA, 2);
			response.write(sock.getOutputStream(), 2, true);
		}
		
		public void sendTopupPaymentReversalRequest() throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(000000006000);
			IsoMessage postPaidPayment = mfact.newMessage(0x400);
			postPaidPayment.setValue(2, "1111111111111111111", IsoType.LLVAR,
					19);
			postPaidPayment.setValue(3, "180000", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.AMOUNT, 12);
			postPaidPayment.setValue(7, "0707132720", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "000011", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "132720", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "1001", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "881", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "000000000011", IsoType.ALPHA, 12);
			postPaidPayment
					.setValue(42, "000000000011098", IsoType.NUMERIC, 15);
			postPaidPayment.setValue(48, "11016288911111112000000006000", IsoType.LLLVAR,
					120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "881", IsoType.LLLVAR, 3);
			String element90 = "0200" + "000011" + "0707132720" + "00000000881"
			+ "00000000000";
			postPaidPayment.setValue(90, element90, IsoType.NUMERIC, 42);
			System.out.println("PRE PAID TOPUP REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidPayment.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			postPaidPayment.write(sock.getOutputStream(), 2, true);
		}
		public void sendTopupPaymentRequest(Socket socket,IsoMessage message) throws IOException {
			System.out.println("TOPUP PAYMENT REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(message.writeData()));
			System.out.println("------------------------------");
			System.out.println();
			message.write(socket.getOutputStream(), 2, true);
		}
		public void sendTopupPaymentRequest() throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(000000006000);
			IsoMessage postPaidPayment = mfact.newMessage(0x200);
			postPaidPayment.setValue(2, "1111111111111111111", IsoType.LLVAR,19);
			postPaidPayment.setValue(3, "180000", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.AMOUNT, 12);
			postPaidPayment.setValue(7, "0707132720", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "000011", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "132720", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "1001", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "881", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "000000000011", IsoType.ALPHA, 12);
			postPaidPayment.setValue(42, "000000000011098", IsoType.NUMERIC, 15);
			postPaidPayment.setValue(48, "11016288116210961000000006000", IsoType.LLLVAR,120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "881", IsoType.LLLVAR, 3);
			System.out.println("PRE PAID TOPUP REQUEST:");
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
			postPaidPayment.setValue(2, "1111111111111111111", IsoType.LLVAR,
					19);
			postPaidPayment.setValue(3, "180000", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.AMOUNT, 12);
			postPaidPayment.setValue(7, "0707132720", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "000011", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "132720", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "1001", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "881", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "000000000011", IsoType.ALPHA, 12);
			postPaidPayment
					.setValue(42, "000000000011098", IsoType.NUMERIC, 15);
			postPaidPayment.setValue(48, "10026288911111112", IsoType.LLLVAR,
					120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "881", IsoType.LLLVAR, 3);
			System.out.println("POST PAID PAYMENT REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidPayment.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			postPaidPayment.write(sock.getOutputStream(), 2, true);
		}

		public void sendPostPaidBillPaymentReversalRequest()
				throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(000000006000);

			IsoMessage postPaidPayment = mfact.newMessage(0x400);
			postPaidPayment.setValue(2, "1111111111111111111", IsoType.LLVAR,
					19);
			postPaidPayment.setValue(3, "180000", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.AMOUNT, 12);
			postPaidPayment.setValue(7, "0707132720", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "000011", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "132720", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "1001", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "00000000881", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "000000000011", IsoType.ALPHA, 12);
			postPaidPayment
					.setValue(42, "000000000011098", IsoType.NUMERIC, 15);
			postPaidPayment.setValue(48, "1002628891111111213042487781200",
					IsoType.LLLVAR, 120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "881", IsoType.LLLVAR, 3);
			String element90 = "0200" + "000011" + "0707132720" + "00000000881"
					+ "00000000000";
			System.out.println("ELement 90 lenght is <" + element90.length()
					+ ">");
			postPaidPayment.setValue(90, element90, IsoType.NUMERIC, 42);
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
			IsoMessage postPaidInquiry = mfact.newMessage(0x200);
			postPaidInquiry.setValue(2, "1111111111111111111", IsoType.LLVAR,
					19);
			postPaidInquiry.setValue(3, "380000", IsoType.NUMERIC, 6);
			postPaidInquiry.setValue(7, "0707132720", IsoType.DATE10, 10);
			postPaidInquiry.setValue(11, "000011", IsoType.NUMERIC, 6);
			postPaidInquiry.setValue(12, "132720", IsoType.TIME, 6);
			postPaidInquiry.setValue(13, "0707", IsoType.DATE4, 4);
			postPaidInquiry.setValue(15, "0707", IsoType.DATE4, 4);
			postPaidInquiry.setValue(18, "1001", IsoType.NUMERIC, 4);
			postPaidInquiry.setValue(32, "881", IsoType.LLVAR, 11);
			postPaidInquiry.setValue(37, "000000000011", IsoType.ALPHA, 12);
			postPaidInquiry
					.setValue(42, "000000000011098", IsoType.NUMERIC, 15);
			postPaidInquiry.setValue(48, "10026288911111112", IsoType.LLLVAR,
					120);
			postPaidInquiry.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidInquiry.setValue(63, "881", IsoType.LLLVAR, 3);
			System.out.println("POST PAID INQUIRY REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidInquiry.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			postPaidInquiry.write(sock.getOutputStream(), 2, true);
		}

		public void run() {
			try {
				log.debug(String.format("Parsing incoming: '%s'", new String(
						msg)));
				// mfact.setUseBinaryMessages(true);
				IsoMessage incoming = mfact.parseMessage(msg, 0);
				log.debug("\n" + incoming.toDebugString());

				switch (incoming.getType()) {
				case 0x810:
					log.debug("Done with response message");
					//sendPostPaidBillPaymentInquiryRequest();
					break;
				case 0x210:
					if ("380000".equals(incoming.getObjectValue(3))) {
						//sendPostPaidBillPaymentRequest();
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

					if(sentInitialNTMRequest) {
					//	sendPostPaidBillPaymentRequest();
					//	sendPostPaidBillPaymentReversalRequest();
					//	sendTopupPaymentRequest();
						sendTopupPaymentReversalRequest();
					//	sendPostPaidBillPaymentInquiryRequest();	
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
	
