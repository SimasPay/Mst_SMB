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

public class ArtajasaBankChannelServer implements Runnable {

	private static Logger	log	= LoggerFactory.getLogger(ArtajasaBankChannelServer.class);

	private static ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(5);
	private static MessageFactory mfact;

	private Socket socket;

	private boolean sentInitialNTMRequest;

	public ArtajasaBankChannelServer(Socket sock) {
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
			boolean firstTime = true;
			while (socket != null && socket.isConnected()
					&& Thread.currentThread().isAlive()
					&& !Thread.currentThread().isInterrupted()) {

				if (socket.getInputStream().read(lenbuf) == 2) {
					// System.out.println("THE SIZE IS " + new String(lenbuf));

					int size = lenbuf[0] + lenbuf[1];

					byte[] buf = new byte[size];
					// We're not expecting ETX in this case
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
		
		private void sendTopupPaymentReversalRequest() throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(000000065000);
			IsoMessage postPaidPayment = mfact.newMessage(0x400);
			postPaidPayment.setValue(2, "1111111111111111111", IsoType.LLVAR,
					19);
			postPaidPayment.setValue(3, "180000", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.AMOUNT, 12);
			postPaidPayment.setValue(7, "0203192720", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "000011", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "132720", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "1001", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "881", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "1122B3659001", IsoType.ALPHA, 12);
			postPaidPayment
					.setValue(42, "000000000011098", IsoType.NUMERIC, 15);
			postPaidPayment.setValue(48, "1101088216210961 000000006000", IsoType.LLLVAR,
					120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "110", IsoType.LLLVAR, 3);
			String element90 = "0200" + "000031" + "0203192720" + "00000000881"
			+ "00000000000";
			postPaidPayment.setValue(90, element90, IsoType.NUMERIC, 42);
			System.out.println("PRE PAID TOPUP REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidPayment.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			postPaidPayment.write(sock.getOutputStream(), 2, true);
		}

		private void sendTopupPaymentRequest() throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(000000065000);
			IsoMessage postPaidPayment = mfact.newMessage(0x200);
			postPaidPayment.setValue(2, "1111111111111111111", IsoType.LLVAR,
					19);
			postPaidPayment.setValue(3, "180000", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.AMOUNT, 12);
			postPaidPayment.setValue(7, "0203192720", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "000031", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "132720", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "1001", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "110", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "1122B3659001", IsoType.ALPHA, 12);
			postPaidPayment
					.setValue(42, "000000000011098", IsoType.NUMERIC, 15);
			postPaidPayment.setValue(48, "1191088116210961 000000006000", IsoType.LLLVAR,
					120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "881", IsoType.LLLVAR, 3);
			System.out.println("PRE PAID TOPUP REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidPayment.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			postPaidPayment.write(sock.getOutputStream(), 2, true);
		}
		private void sendPostPaidBillPaymentRequest() throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(000000060000);
			IsoMessage postPaidPayment = mfact.newMessage(0x200);
			postPaidPayment.setValue(2, "1111111111111111111", IsoType.LLVAR,
					19);
			postPaidPayment.setValue(3, "180000", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.AMOUNT, 12);
			postPaidPayment.setValue(7, "0203192720", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "000035", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "132720", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "1001", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "881", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "1122B3659001", IsoType.ALPHA, 12);
			postPaidPayment
					.setValue(42, "000000000011098", IsoType.NUMERIC, 15);
			postPaidPayment.setValue(48, "11946288116210961", IsoType.LLLVAR,
					120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "100", IsoType.LLLVAR, 3);
			System.out.println("POST PAID PAYMENT REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(postPaidPayment.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			postPaidPayment.write(sock.getOutputStream(), 2, true);
		}

		private void sendPostPaidBillPaymentReversalRequest()
				throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(000000006000);

			IsoMessage postPaidPayment = mfact.newMessage(0x400);
			postPaidPayment.setValue(2, "1111111111111111111", IsoType.LLVAR,
					19);
			postPaidPayment.setValue(3, "180000", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(4, amount, IsoType.AMOUNT, 12);
			postPaidPayment.setValue(7, "0203192720", IsoType.DATE10, 10);
			postPaidPayment.setValue(11, "000011", IsoType.NUMERIC, 6);
			postPaidPayment.setValue(12, "132720", IsoType.TIME, 6);
			postPaidPayment.setValue(13, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(15, "0707", IsoType.DATE4, 4);
			postPaidPayment.setValue(18, "1001", IsoType.NUMERIC, 4);
			postPaidPayment.setValue(32, "00000000881", IsoType.LLVAR, 11);
			postPaidPayment.setValue(37, "1122B3659001", IsoType.ALPHA, 12);
			postPaidPayment
					.setValue(42, "000000000011098", IsoType.NUMERIC, 15);
			postPaidPayment.setValue(48, "1002008811621096113042487781200",
					IsoType.LLLVAR, 120);
			postPaidPayment.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidPayment.setValue(63, "100", IsoType.LLLVAR, 3);
			String element90 = "0200" + "000035" + "0203192720" + "00000000881"
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

		private void sendPostPaidBillPaymentInquiryRequest() throws IOException {
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
			postPaidInquiry.setValue(37, "1122B3659001", IsoType.ALPHA, 12);
			postPaidInquiry
					.setValue(42, "000000000011098", IsoType.NUMERIC, 15);
			postPaidInquiry.setValue(48, "1192088116210961 ", IsoType.LLLVAR,
					120);
			postPaidInquiry.setValue(49, "360", IsoType.NUMERIC, 3);
			postPaidInquiry.setValue(63, "100", IsoType.LLLVAR, 3);
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
//						sendPostPaidBillPaymentRequest();
//						sendPostPaidBillPaymentReversalRequest();
						sendTopupPaymentRequest();
//						sendTopupPaymentReversalRequest();
//						sendPostPaidBillPaymentInquiryRequest();	
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
							.createFromClasspathConfig("bank-config.xml");
				} catch (IOException e) {
					log.error("Failed to parse the config file", e);
					return;
				}
				log.info("Setting up server socket...");
				ServerSocket server;
				try {
					server = new ServerSocket(9997);
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

					new Thread(new ArtajasaBankChannelServer(sock),
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
