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

public class ATMMock implements Runnable {

	private static Logger	log	= LoggerFactory.getLogger(ATMClientMock.class);

	private static ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(5);
	private static MessageFactory mfact;

	private Socket socket;

	private boolean sentInitialNTMRequest;

	public ATMMock(Socket sock) {
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
			boolean firstTime = true;
			while (socket != null && socket.isConnected()
					&& Thread.currentThread().isAlive()
					&& !Thread.currentThread().isInterrupted()) {

				if (socket.getInputStream().read(lenbuf) == 4) {
					// System.out.println("THE SIZE IS " + new String(lenbuf));

					int size = Integer.parseInt(new String(lenbuf));

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
		
		//TransferInquiryRequest
		private void sendTransferInquiryRequest() throws IOException {
			BigDecimal amount = new BigDecimal(000000000000000000);
			IsoMessage transferReq = mfact.newMessage(0x200);
			transferReq.setValue(2, "6288116210923", IsoType.LLVAR, 19);
			transferReq.setValue(3, "371000", IsoType.NUMERIC, 6);
			transferReq.setValue(4, amount, IsoType.NUMERIC, 18);
			transferReq.setValue(7, "0707132720", IsoType.DATE10, 10);
			transferReq.setValue(11, "000011", IsoType.NUMERIC, 6);
			transferReq.setValue(12, "132720", IsoType.TIME, 6);
			transferReq.setValue(13, "0707", IsoType.DATE4, 4);
			transferReq.setValue(15, "0707", IsoType.DATE4, 4);
			transferReq.setValue(18, "6011", IsoType.NUMERIC, 4);
			transferReq.setValue(32, "009", IsoType.LLVAR, 11);
			transferReq.setValue(33, "153", IsoType.LLVAR, 11);			
			transferReq.setValue(37, "000000000011", IsoType.ALPHA, 12);
			transferReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			//transferReq.setValue(48, "11016288911111112000000006000", IsoType.LLVAR,108);
			transferReq.setValue(49, "360", IsoType.NUMERIC, 3);
			transferReq.setValue(100, "153", IsoType.LLVAR, 11);
			transferReq.setValue(103, "80016288116210923", IsoType.LLVAR, 28);//to a/c
			System.out.println("TransferInquiry REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(transferReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			transferReq.write(sock.getOutputStream(), 4, false);
		}
		//CreditTransferRequest
		private void sendTransferCreditRequest() throws IOException {
			BigDecimal amount = new BigDecimal(000000000000600000);
			IsoMessage trasferCreditReq = mfact.newMessage(0x200);
			trasferCreditReq.setValue(2, "6288116210961", IsoType.LLVAR, 19);
			trasferCreditReq.setValue(3, "471000", IsoType.NUMERIC, 6);
			trasferCreditReq.setValue(4, amount, IsoType.NUMERIC, 18);
			trasferCreditReq.setValue(7, "0707132720", IsoType.DATE10, 10);
			trasferCreditReq.setValue(11, "000011", IsoType.NUMERIC, 6);
			trasferCreditReq.setValue(12, "132720", IsoType.TIME, 6);
			trasferCreditReq.setValue(13, "0707", IsoType.DATE4, 4);
			trasferCreditReq.setValue(15, "0707", IsoType.DATE4, 4);
			trasferCreditReq.setValue(18, "6011", IsoType.NUMERIC, 4);
			trasferCreditReq.setValue(32, "009", IsoType.LLVAR, 11);
			trasferCreditReq.setValue(33, "153", IsoType.LLVAR, 11);			
			trasferCreditReq.setValue(37, "000000000011", IsoType.ALPHA, 12);
			trasferCreditReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			trasferCreditReq.setValue(48, "6288116210961                 123454321       6288116210961                 SMART BRANCH INDONESIA        10", IsoType.LLLVAR, 108);//addtional data
			trasferCreditReq.setValue(49, "360", IsoType.NUMERIC, 3);
			trasferCreditReq.setValue(100, "153", IsoType.LLVAR, 11);
			trasferCreditReq.setValue(102, "0011234567", IsoType.LLVAR, 28);//from A/c
			trasferCreditReq.setValue(103, "80016288116210961", IsoType.LLVAR, 28);//to A/c
			System.out.println("TransferCredit REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(trasferCreditReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			trasferCreditReq.write(sock.getOutputStream(), 4, false);
		}
		//eMoney - TransferInquiryRequest
		private void sendEMoneyTransferInquiryRequest() throws IOException {
			BigDecimal amount = new BigDecimal(000000000000000000);
			IsoMessage transferReq = mfact.newMessage(0x200);
			transferReq.setValue(2, "6288116210961", IsoType.LLVAR, 19);
			transferReq.setValue(3, "371000", IsoType.NUMERIC, 6);
			transferReq.setValue(4, amount, IsoType.NUMERIC, 18);
			transferReq.setValue(7, "0707132720", IsoType.DATE10, 10);
			transferReq.setValue(11, "000011", IsoType.NUMERIC, 6);
			transferReq.setValue(12, "132720", IsoType.TIME, 6);
			transferReq.setValue(13, "0707", IsoType.DATE4, 4);
			transferReq.setValue(15, "0707", IsoType.DATE4, 4);
			transferReq.setValue(18, "6011", IsoType.NUMERIC, 4);
			transferReq.setValue(32, "009", IsoType.LLVAR, 11);
			transferReq.setValue(33, "153", IsoType.LLVAR, 11);			
			transferReq.setValue(37, "000000000011", IsoType.ALPHA, 12);
			transferReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			transferReq.setValue(49, "360", IsoType.NUMERIC, 3);
			transferReq.setValue(100, "153", IsoType.LLVAR, 11);
			transferReq.setValue(103, "80026288116210961", IsoType.LLVAR, 28);//to a/c
			System.out.println("eMoney Transfer Inquiry REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(transferReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			transferReq.write(sock.getOutputStream(), 4, false);
		}
		//eMoney - CreditTransferRequest
		private void sendEMoneyTransferCreditRequest() throws IOException {
			BigDecimal amount = new BigDecimal(000000000000600000);
			IsoMessage trasferCreditReq = mfact.newMessage(0x200);
			trasferCreditReq.setValue(2, "6288116210923", IsoType.LLVAR, 19);
			trasferCreditReq.setValue(3, "471000", IsoType.NUMERIC, 6);
			trasferCreditReq.setValue(4, amount, IsoType.NUMERIC, 18);
			trasferCreditReq.setValue(7, "0707132720", IsoType.DATE10, 10);
			trasferCreditReq.setValue(11, "000011", IsoType.NUMERIC, 6);
			trasferCreditReq.setValue(12, "132720", IsoType.TIME, 6);
			trasferCreditReq.setValue(13, "0707", IsoType.DATE4, 4);
			trasferCreditReq.setValue(15, "0707", IsoType.DATE4, 4);
			trasferCreditReq.setValue(18, "6011", IsoType.NUMERIC, 4);
			trasferCreditReq.setValue(32, "009", IsoType.LLVAR, 11);
			trasferCreditReq.setValue(33, "153", IsoType.LLVAR, 11);			
			trasferCreditReq.setValue(37, "000000000011", IsoType.ALPHA, 12);
			trasferCreditReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			trasferCreditReq.setValue(48, "6288116210923                 123454321       6288116210923                 SMART BRANCH INDONESIA        10", IsoType.LLLVAR, 108);//addtional data
			trasferCreditReq.setValue(49, "360", IsoType.NUMERIC, 3);
			trasferCreditReq.setValue(100, "153", IsoType.LLVAR, 11);
			trasferCreditReq.setValue(102, "0011234567", IsoType.LLVAR, 28);//from A/c
			trasferCreditReq.setValue(103, "80026288116210961", IsoType.LLVAR, 28);//to A/c
			System.out.println("eMoney Transfer REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(trasferCreditReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			trasferCreditReq.write(sock.getOutputStream(), 4, false);
		}
		//BillPaymentRequest
		private void sendBillPaymentRequest() throws IOException {
			BigDecimal amount = new BigDecimal(000000000000600000);
			IsoMessage billPaymentReq = mfact.newMessage(0x200);
			billPaymentReq.setValue(2, "6288116210961", IsoType.LLVAR, 19);
			billPaymentReq.setValue(3, "501000", IsoType.NUMERIC, 6);
			billPaymentReq.setValue(4, amount, IsoType.NUMERIC, 18);
			billPaymentReq.setValue(7, "0707132720", IsoType.DATE10, 10);
			billPaymentReq.setValue(11, "000011", IsoType.NUMERIC, 6);
			billPaymentReq.setValue(12, "132720", IsoType.TIME, 6);
			billPaymentReq.setValue(13, "0707", IsoType.DATE4, 4);
			billPaymentReq.setValue(15, "0707", IsoType.DATE4, 4);
			billPaymentReq.setValue(18, "6011", IsoType.NUMERIC, 4);
			billPaymentReq.setValue(32, "009", IsoType.LLVAR, 11);
			billPaymentReq.setValue(33, "153", IsoType.LLVAR, 11);			
			billPaymentReq.setValue(37, "000000000011", IsoType.ALPHA, 12);
			billPaymentReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			billPaymentReq.setValue(48, "6288116210961                 123454321       6288116210961                 SMART BRANCH INDONESIA        10", IsoType.LLLVAR, 108);//Additional data
			billPaymentReq.setValue(49, "360", IsoType.NUMERIC, 3);
			billPaymentReq.setValue(100, "153", IsoType.LLVAR, 11);
			billPaymentReq.setValue(102, "0011234567", IsoType.LLVAR, 28);//from A/c
			billPaymentReq.setValue(103, "80016288116210962", IsoType.LLVAR, 28); // to A/c
			System.out.println("Billpayment REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(billPaymentReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			billPaymentReq.write(sock.getOutputStream(), 4, false);
		}
		
		//PaymentInquiryRequest
		private void sendPaymentInquiryRequest() throws IOException {
			BigDecimal amount = new BigDecimal(000000000000000000);
			IsoMessage paymentInquiryRequest = mfact.newMessage(0x200);
			paymentInquiryRequest.setValue(2, "6288116210961", IsoType.LLVAR, 19);
			paymentInquiryRequest.setValue(3, "381000", IsoType.NUMERIC, 6);
			paymentInquiryRequest.setValue(4, amount, IsoType.NUMERIC, 18);
			paymentInquiryRequest.setValue(7, "0707132720", IsoType.DATE10, 10);
			paymentInquiryRequest.setValue(11, "000011", IsoType.NUMERIC, 6);
			paymentInquiryRequest.setValue(12, "132720", IsoType.TIME, 6);
			paymentInquiryRequest.setValue(13, "0707", IsoType.DATE4, 4);
			paymentInquiryRequest.setValue(15, "0707", IsoType.DATE4, 4);
			paymentInquiryRequest.setValue(18, "6011", IsoType.NUMERIC, 4);
			paymentInquiryRequest.setValue(32, "009", IsoType.LLVAR, 11);
			paymentInquiryRequest.setValue(33, "153", IsoType.LLVAR, 11);			
			paymentInquiryRequest.setValue(37, "000000000011", IsoType.ALPHA, 12);
			paymentInquiryRequest.setValue(41, "00011098", IsoType.ALPHA, 8);
			//paymentInquiryRequest.setValue(48, "11016288911111112000000006000", IsoType.LLVAR, 108);
			paymentInquiryRequest.setValue(49, "360", IsoType.NUMERIC, 3);
			paymentInquiryRequest.setValue(100, "153", IsoType.LLVAR, 11);
			paymentInquiryRequest.setValue(103, "80016288116210961", IsoType.LLVAR, 28);
			System.out.println("BillPaymentInquiry REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(paymentInquiryRequest.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			paymentInquiryRequest.write(sock.getOutputStream(), 4, false);
		}
		
		//Reversal
		private void sendTransferCreditReversalRequest() throws IOException {
			BigDecimal amount = new BigDecimal(000000000000600000);
			IsoMessage trasferCreditReversalReq = mfact.newMessage(0x420);
			trasferCreditReversalReq.setValue(2, "6288116210961", IsoType.LLVAR, 19);
			trasferCreditReversalReq.setValue(3, "471000", IsoType.NUMERIC, 6);
			trasferCreditReversalReq.setValue(4, amount, IsoType.NUMERIC, 18);
			trasferCreditReversalReq.setValue(7, "0707132720", IsoType.DATE10, 10);
			trasferCreditReversalReq.setValue(11, "000011", IsoType.NUMERIC, 6);
			trasferCreditReversalReq.setValue(12, "132720", IsoType.TIME, 6);
			trasferCreditReversalReq.setValue(13, "0707", IsoType.DATE4, 4);
			trasferCreditReversalReq.setValue(15, "0707", IsoType.DATE4, 4);
			trasferCreditReversalReq.setValue(18, "6011", IsoType.NUMERIC, 4);
			trasferCreditReversalReq.setValue(32, "009", IsoType.LLVAR, 11);
			trasferCreditReversalReq.setValue(33, "153", IsoType.LLVAR, 11);			
			trasferCreditReversalReq.setValue(37, "000000000011", IsoType.ALPHA, 12);
			trasferCreditReversalReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			trasferCreditReversalReq.setValue(48, "6288116210961                 123454321       6288116210961                 SMART BRANCH INDONESIA        10", IsoType.LLLVAR,108);
			trasferCreditReversalReq.setValue(49, "360", IsoType.NUMERIC, 3);
			String element90 = "0200" + "000011" + "0707132720" + "00000000009"+ "00000000153";
			trasferCreditReversalReq.setValue(90, element90, IsoType.NUMERIC, 42);
			trasferCreditReversalReq.setValue(100, "153", IsoType.LLVAR, 11);
			trasferCreditReversalReq.setValue(102, "0011234567", IsoType.LLVAR, 28);
			trasferCreditReversalReq.setValue(103, "80016288116210961", IsoType.LLVAR, 28);
			System.out.println("TransferCreditReversal REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(trasferCreditReversalReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			trasferCreditReversalReq.write(sock.getOutputStream(), 4, false);
		}
		
		private void sendBillPaymentReversalRequest() throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(000000000000600000);
			IsoMessage billPaymentReversalReq = mfact.newMessage(0x420);
			billPaymentReversalReq.setValue(2, "6288116210961", IsoType.LLVAR,19);
			billPaymentReversalReq.setValue(3, "501000", IsoType.NUMERIC, 6);
			billPaymentReversalReq.setValue(4, amount, IsoType.NUMERIC, 18);
			billPaymentReversalReq.setValue(7, "0707132720", IsoType.DATE10, 10);
			billPaymentReversalReq.setValue(11, "000011", IsoType.NUMERIC, 6);
			billPaymentReversalReq.setValue(12, "132720", IsoType.TIME, 6);
			billPaymentReversalReq.setValue(13, "0707", IsoType.DATE4, 4);
			billPaymentReversalReq.setValue(15, "0707", IsoType.DATE4, 4);
			billPaymentReversalReq.setValue(18, "6011", IsoType.NUMERIC, 4);
			billPaymentReversalReq.setValue(32, "009", IsoType.LLVAR, 11);
			billPaymentReversalReq.setValue(33, "153", IsoType.LLVAR, 11);			
			billPaymentReversalReq.setValue(37, "000000000011", IsoType.ALPHA, 12);
			billPaymentReversalReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			billPaymentReversalReq.setValue(48, "6288116210961                 123454321       6288116210961                 SMART BRANCH INDONESIA        10", IsoType.LLLVAR, 108);
			billPaymentReversalReq.setValue(49, "360", IsoType.NUMERIC, 3);
			String element90 = "0200" + "000011" + "0707132720" + "00000000009"+ "00000000153";
			billPaymentReversalReq.setValue(90, element90, IsoType.NUMERIC, 42);
			billPaymentReversalReq.setValue(100, "153", IsoType.LLVAR, 11);
			billPaymentReversalReq.setValue(102, "0011234567", IsoType.LLVAR, 28);
			billPaymentReversalReq.setValue(103, "80016288116210961", IsoType.LLVAR, 28);
			System.out.println("BillPaymentReversal REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(billPaymentReversalReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			billPaymentReversalReq.write(sock.getOutputStream(), 4, false);
		}
		
		private void sendSelfTopupRequest() throws IOException {
			BigDecimal amount = new BigDecimal(000000000000600000);
			IsoMessage SelfTopup = mfact.newMessage(0x200);
			SelfTopup.setValue(2, "6288116210961", IsoType.LLVAR, 19);
			SelfTopup.setValue(3, "551000", IsoType.NUMERIC, 6);
			SelfTopup.setValue(4, amount, IsoType.NUMERIC, 18);
			SelfTopup.setValue(7, "0707132720", IsoType.DATE10, 10);
			SelfTopup.setValue(11, "000011", IsoType.NUMERIC, 6);
			SelfTopup.setValue(12, "132720", IsoType.TIME, 6);
			SelfTopup.setValue(13, "0707", IsoType.DATE4, 4);
			SelfTopup.setValue(15, "0707", IsoType.DATE4, 4);
			SelfTopup.setValue(18, "6011", IsoType.NUMERIC, 4);
			SelfTopup.setValue(32, "009", IsoType.LLVAR, 11);
			SelfTopup.setValue(33, "153", IsoType.LLVAR, 11);			
			SelfTopup.setValue(37, "000000000011", IsoType.ALPHA, 12);
			SelfTopup.setValue(41, "00011098", IsoType.ALPHA, 8);
			SelfTopup.setValue(48, "6288116210961                 123454321       6288116210961                 SMART BRANCH INDONESIA        10", IsoType.LLLVAR, 108);//Additional data
			SelfTopup.setValue(49, "360", IsoType.NUMERIC, 3);
			SelfTopup.setValue(100, "153", IsoType.LLVAR, 11);
			SelfTopup.setValue(102, "0011234567", IsoType.LLVAR, 28);//from A/c
			SelfTopup.setValue(103, "80016288116210961", IsoType.LLVAR, 28); // to A/c
			System.out.println("Self Topup REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(SelfTopup.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			SelfTopup.write(sock.getOutputStream(), 4, false);
		}
		
		private void sendOthersTopupRequest() throws IOException {
			BigDecimal amount = new BigDecimal(000000000000600000);
			IsoMessage TopOthers = mfact.newMessage(0x200);
			TopOthers.setValue(2, "6288116210961", IsoType.LLVAR, 19);
			TopOthers.setValue(3, "561000", IsoType.NUMERIC, 6);
			TopOthers.setValue(4, amount, IsoType.NUMERIC, 18);
			TopOthers.setValue(7, "0707132720", IsoType.DATE10, 10);
			TopOthers.setValue(11, "000011", IsoType.NUMERIC, 6);
			TopOthers.setValue(12, "132720", IsoType.TIME, 6);
			TopOthers.setValue(13, "0707", IsoType.DATE4, 4);
			TopOthers.setValue(15, "0707", IsoType.DATE4, 4);
			TopOthers.setValue(18, "6011", IsoType.NUMERIC, 4);
			TopOthers.setValue(32, "009", IsoType.LLVAR, 11);
			TopOthers.setValue(33, "153", IsoType.LLVAR, 11);			
			TopOthers.setValue(37, "000000000011", IsoType.ALPHA, 12);
			TopOthers.setValue(41, "00011098", IsoType.ALPHA, 8);
			TopOthers.setValue(48, "6288116210961                 123454321       6288116210961                 SMART BRANCH INDONESIA        10", IsoType.LLLVAR, 108);//Additional data
			TopOthers.setValue(49, "360", IsoType.NUMERIC, 3);
			TopOthers.setValue(100, "153", IsoType.LLVAR, 11);
			TopOthers.setValue(102, "0011234567", IsoType.LLVAR, 28);//from A/c
			TopOthers.setValue(103, "80016288116210961", IsoType.LLVAR, 28); // to A/c
			System.out.println("Topup Others REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(TopOthers.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			TopOthers.write(sock.getOutputStream(), 4, false);
		}
		
		private void sendTopUpReversalRequest() throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(000000000000600000);
			IsoMessage topUpReversalReq = mfact.newMessage(0x420);
			topUpReversalReq.setValue(2, "6288116210961", IsoType.LLVAR,19);
			topUpReversalReq.setValue(3, "501000", IsoType.NUMERIC, 6);
			topUpReversalReq.setValue(4, amount, IsoType.NUMERIC, 18);
			topUpReversalReq.setValue(7, "0707132720", IsoType.DATE10, 10);
			topUpReversalReq.setValue(11, "000011", IsoType.NUMERIC, 6);
			topUpReversalReq.setValue(12, "132720", IsoType.TIME, 6);
			topUpReversalReq.setValue(13, "0707", IsoType.DATE4, 4);
			topUpReversalReq.setValue(15, "0707", IsoType.DATE4, 4);
			topUpReversalReq.setValue(18, "6011", IsoType.NUMERIC, 4);
			topUpReversalReq.setValue(32, "009", IsoType.LLVAR, 11);
			topUpReversalReq.setValue(33, "153", IsoType.LLVAR, 11);			
			topUpReversalReq.setValue(37, "000000000011", IsoType.ALPHA, 12);
			topUpReversalReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			topUpReversalReq.setValue(48, "6288116210961                 123454321       6288116210961                 SMART BRANCH INDONESIA        10", IsoType.LLLVAR, 108);
			topUpReversalReq.setValue(49, "360", IsoType.NUMERIC, 3);
			String element90 = "0200" + "000011" + "0707132720" + "00000000009"+ "00000000153";
			topUpReversalReq.setValue(90, element90, IsoType.NUMERIC, 42);
			topUpReversalReq.setValue(100, "153", IsoType.LLVAR, 11);
			topUpReversalReq.setValue(102, "0011234567", IsoType.LLVAR, 28);
			topUpReversalReq.setValue(103, "80016288116210961", IsoType.LLVAR, 28);
			System.out.println("TopUp Reversal REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(topUpReversalReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			topUpReversalReq.write(sock.getOutputStream(), 4, false);
		}
		
		public void run() {
			try {
				log.debug(String.format("Parsing incoming: '%s'", new String(
						msg)));
				// mfact.setUseBinaryMessages(true);
				IsoMessage incoming = mfact.parseMessage(msg, 0);
				if(incoming !=null)
				{
				log.debug("\n" + incoming.toDebugString());

				switch (incoming.getType()) {
				case 0x810:
				case 0x210:	
				case 0x410:	
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
				case 0x400:
					return;
				default:
					log.error("Unrecognonized message type");
					break;
				}

				log.info("Sending response ISO message:\n"
						+ response.toDebugString());
					response.write(sock.getOutputStream(), 4, false);
				}
					if(sentInitialNTMRequest) {
//						sendTransferInquiryRequest();
						//sendTransferCreditRequest();
//						sendPaymentInquiryRequest();		
						
						//sendTransferCreditReversalRequest();
						
						//sendBillPaymentReversalRequest();
						
						//sendEMoneyTransferInquiryRequest();
//						sendEMoneyTransferCreditRequest();
						//sendSelfTopupRequest();
//						sendOthersTopupRequest();
					    //sendTopUpReversalRequest();
					    
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
							.createFromClasspathConfig("VAHconfig.xml");
				} catch (IOException e) {
					log.error("Failed to parse the config file", e);
					return;
				}
				log.info("Setting up server socket...");
				ServerSocket server;
				try {
					server = new ServerSocket(9991);
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

					new Thread(new ATMMock(sock),
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
