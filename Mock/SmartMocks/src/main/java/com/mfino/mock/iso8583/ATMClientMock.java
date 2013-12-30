package com.mfino.mock.iso8583;

import java.io.BufferedReader;
import java.io.FileReader;
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

public class ATMClientMock implements Runnable {

	private static Logger	log	= LoggerFactory.getLogger(ATMClientMock.class);

	private static ScheduledExecutorService threadPool = Executors
			.newScheduledThreadPool(5);
	private static MessageFactory mfact;

	private Socket socket;
	private static IsoMessage request;
	
	private static boolean sentInitialNTMRequest;

	public ATMClientMock(Socket sock) {
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
		
		//TransferInquiryRequest
		private void sendTransferInquiryRequest() throws IOException {
			BigDecimal amount = new BigDecimal(000000000000000000);
			IsoMessage transferReq = mfact.newMessage(0x200);
			transferReq.setValue(2, "1122334455661", IsoType.LLVAR, 19);
			transferReq.setValue(3, "371000", IsoType.NUMERIC, 6);
			transferReq.setValue(4, amount, IsoType.NUMERIC, 18);
			transferReq.setValue(7, "0420132720", IsoType.DATE10, 10);
			transferReq.setValue(11, "000004", IsoType.NUMERIC, 6);
			transferReq.setValue(12, "145020", IsoType.TIME, 6);
			transferReq.setValue(13, "0420", IsoType.DATE4, 4);
			transferReq.setValue(15, "0420", IsoType.DATE4, 4);
			transferReq.setValue(18, "1102", IsoType.NUMERIC, 4);
			transferReq.setValue(32, "889", IsoType.LLVAR, 11);
			transferReq.setValue(33, "881", IsoType.LLVAR, 11);			
			transferReq.setValue(37, "000000000004", IsoType.ALPHA, 12);
			transferReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			//transferReq.setValue(48, "11016288911111112000000006000", IsoType.LLVAR,108);
			transferReq.setValue(49, "360", IsoType.NUMERIC, 3);
			transferReq.setValue(100, "881", IsoType.LLVAR, 11);
			transferReq.setValue(103, "8883628910080", IsoType.LLVAR, 28);//to a/c
			System.out.println("TransferInquiry REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(transferReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			transferReq.write(sock.getOutputStream(), 4, false);
		}
		//CreditTransferRequest
		private void sendTransferCreditRequest() throws IOException {
			BigDecimal amount = new BigDecimal(500000);
			IsoMessage trasferCreditReq = mfact.newMessage(0x200);
			trasferCreditReq.setValue(2, "1122334455661", IsoType.LLVAR, 19);
			trasferCreditReq.setValue(3, "471000", IsoType.NUMERIC, 6);
			trasferCreditReq.setValue(4, amount, IsoType.NUMERIC, 18);
			trasferCreditReq.setValue(7, "0420132720", IsoType.DATE10, 10);
			trasferCreditReq.setValue(11, "000009", IsoType.NUMERIC, 6);
			trasferCreditReq.setValue(12, "132720", IsoType.TIME, 6);
			trasferCreditReq.setValue(13, "0420", IsoType.DATE4, 4);
			trasferCreditReq.setValue(15, "0420", IsoType.DATE4, 4);
			trasferCreditReq.setValue(18, "1102", IsoType.NUMERIC, 4);
			trasferCreditReq.setValue(32, "009", IsoType.LLVAR, 11);
			trasferCreditReq.setValue(33, "881", IsoType.LLVAR, 11);			
			trasferCreditReq.setValue(37, "000000000009", IsoType.ALPHA, 12);
			trasferCreditReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			trasferCreditReq.setValue(48, "EMONEY QQ UNREGISTERED        8882628910080                                 SMART E-MONEY                 10", IsoType.LLLVAR, 108);//addtional data
			trasferCreditReq.setValue(49, "360", IsoType.NUMERIC, 3);
			trasferCreditReq.setValue(100, "881", IsoType.LLVAR, 11);
			trasferCreditReq.setValue(102, "001 1234567", IsoType.LLVAR, 28);//from A/c
			trasferCreditReq.setValue(103, "8883628910080  ", IsoType.LLVAR, 28);//to A/c
			System.out.println("TransferCredit REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(trasferCreditReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			trasferCreditReq.write(sock.getOutputStream(), 4, false);
		}
		//Reversal
		private void sendTransferCreditReversalRequest() throws IOException {
			BigDecimal amount = new BigDecimal(100000);
			IsoMessage trasferCreditReversalReq = mfact.newMessage(0x420);
			trasferCreditReversalReq.setValue(2, "1122334455661", IsoType.LLVAR, 19);
			trasferCreditReversalReq.setValue(3, "471000", IsoType.NUMERIC, 6);
			trasferCreditReversalReq.setValue(4, amount, IsoType.NUMERIC, 18);
			trasferCreditReversalReq.setValue(7, "0420132720", IsoType.DATE10, 10);
			trasferCreditReversalReq.setValue(11, "000009", IsoType.NUMERIC, 6);
			trasferCreditReversalReq.setValue(12, "132720", IsoType.TIME, 6);
			trasferCreditReversalReq.setValue(13, "0420", IsoType.DATE4, 4);
			trasferCreditReversalReq.setValue(15, "0420", IsoType.DATE4, 4);
			trasferCreditReversalReq.setValue(18, "1102", IsoType.NUMERIC, 4);
			trasferCreditReversalReq.setValue(32, "009", IsoType.LLVAR, 11);
			trasferCreditReversalReq.setValue(33, "153", IsoType.LLVAR, 11);			
			trasferCreditReversalReq.setValue(37, "000000000009", IsoType.ALPHA, 12);
			trasferCreditReversalReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			trasferCreditReversalReq.setValue(48, "6288116210961                 123454321       6288116210961                 SMART BRANCH INDONESIA        10", IsoType.LLLVAR,108);
			trasferCreditReversalReq.setValue(49, "360", IsoType.NUMERIC, 3);
			String element90 = "0200" + "000009" + "0420132720" + "00000000009"+ "00000000153";
			trasferCreditReversalReq.setValue(90, element90, IsoType.NUMERIC, 42);
			trasferCreditReversalReq.setValue(100, "153", IsoType.LLVAR, 11);
			trasferCreditReversalReq.setValue(102, "0011234567", IsoType.LLVAR, 28);
			trasferCreditReversalReq.setValue(103, "8884628910080", IsoType.LLVAR, 28);
			System.out.println("TransferCreditReversal REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(trasferCreditReversalReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			trasferCreditReversalReq.write(sock.getOutputStream(), 4, false);
		}
		
		//eMoney - TransferInquiryRequest
		private void sendEMoneyTransferInquiryRequest() throws IOException {
			BigDecimal amount = new BigDecimal(000000000000000000);
			IsoMessage transferReq = mfact.newMessage(0x200);
			transferReq.setValue(2, "628910006", IsoType.LLVAR, 19);
			transferReq.setValue(3, "371000", IsoType.NUMERIC, 6);
			transferReq.setValue(4, amount, IsoType.NUMERIC, 18);
			transferReq.setValue(7, "0331180820", IsoType.DATE10, 10);
			transferReq.setValue(11, "000030", IsoType.NUMERIC, 6);
			transferReq.setValue(12, "132720", IsoType.TIME, 6);
			transferReq.setValue(13, "0331", IsoType.DATE4, 4);
			transferReq.setValue(15, "0331", IsoType.DATE4, 4);
			transferReq.setValue(18, "6011", IsoType.NUMERIC, 4);
			transferReq.setValue(32, "881", IsoType.LLVAR, 11);
			transferReq.setValue(33, "153", IsoType.LLVAR, 11);			
			transferReq.setValue(37, "000000000030", IsoType.ALPHA, 12);
			transferReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			transferReq.setValue(49, "360", IsoType.NUMERIC, 3);
			transferReq.setValue(100, "153", IsoType.LLVAR, 11);
			transferReq.setValue(103, "88848910006", IsoType.LLVAR, 28);//to a/c
			System.out.println("eMoney Transfer Inquiry REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(transferReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			transferReq.write(sock.getOutputStream(), 4, false);
		}
		//eMoney - CreditTransferRequest
		private void sendEMoneyTransferCreditRequest() throws IOException {
			//Reading from file the input values
			BufferedReader reader = new BufferedReader(new FileReader("va-input.txt"));
			String mdn = reader.readLine();
			String amount = reader.readLine();
			String stan = reader.readLine();
			String integrationCode = reader.readLine();
			
			//BigDecimal amount = new BigDecimal(300000);
			IsoMessage trasferCreditReq = mfact.newMessage(0x200);
			trasferCreditReq.setValue(2, mdn, IsoType.LLVAR, 19);
			trasferCreditReq.setValue(3, "471000", IsoType.NUMERIC, 6);
			trasferCreditReq.setValue(4, amount, IsoType.NUMERIC, 18);
			trasferCreditReq.setValue(7, "0405140820", IsoType.DATE10, 10);
			trasferCreditReq.setValue(11, stan, IsoType.NUMERIC, 6);
			trasferCreditReq.setValue(12, "132720", IsoType.TIME, 6);
			trasferCreditReq.setValue(13, "0331", IsoType.DATE4, 4);
			trasferCreditReq.setValue(15, "0331", IsoType.DATE4, 4);
			trasferCreditReq.setValue(18, "6011", IsoType.NUMERIC, 4);
			trasferCreditReq.setValue(32, "881", IsoType.LLVAR, 11);
			trasferCreditReq.setValue(33, "153", IsoType.LLVAR, 11);			
			trasferCreditReq.setValue(37, stan, IsoType.ALPHA, 12);
			trasferCreditReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			trasferCreditReq.setValue(48, "SMART EMONEY QQ TARAKA PODURU 88818910006                               SMART E-MONEY                 10", IsoType.LLLVAR, 108);//addtional data
			trasferCreditReq.setValue(49, "360", IsoType.NUMERIC, 3);
			trasferCreditReq.setValue(100, integrationCode, IsoType.LLVAR, 11);
			trasferCreditReq.setValue(102, "0011234567", IsoType.LLVAR, 28);//from A/c
			trasferCreditReq.setValue(103, "8881"+mdn, IsoType.LLVAR, 28);//to A/c
			System.out.println("eMoney Transfer REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(trasferCreditReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();
			reader.close();

			trasferCreditReq.write(sock.getOutputStream(), 4, false);
		}
		//eMoney reversal request
		private void sendEMoneyReversalRequest() throws IOException {
			// Here now send the Request.
			BigDecimal amount = new BigDecimal(500000);
			IsoMessage eMoneyReversalReq = mfact.newMessage(0x420);
			eMoneyReversalReq.setValue(2, "628910080", IsoType.LLVAR,19);
			eMoneyReversalReq.setValue(3, "471000", IsoType.NUMERIC, 6);
			eMoneyReversalReq.setValue(4, amount, IsoType.NUMERIC, 18);
			eMoneyReversalReq.setValue(7, "0401180820", IsoType.DATE10, 10);
			eMoneyReversalReq.setValue(11, "000038", IsoType.NUMERIC, 6);
			eMoneyReversalReq.setValue(12, "132720", IsoType.TIME, 6);
			eMoneyReversalReq.setValue(13, "0331", IsoType.DATE4, 4);
			eMoneyReversalReq.setValue(15, "0331", IsoType.DATE4, 4);
			eMoneyReversalReq.setValue(18, "6011", IsoType.NUMERIC, 4);
			eMoneyReversalReq.setValue(32, "153", IsoType.LLVAR, 11);
			eMoneyReversalReq.setValue(33, "153", IsoType.LLVAR, 11);			
			eMoneyReversalReq.setValue(37, "000000000038", IsoType.ALPHA, 12);
			eMoneyReversalReq.setValue(41, "00011098", IsoType.ALPHA, 8);
			eMoneyReversalReq.setValue(48, "SMART EMONEY QQ TARAKA PODURU 888188210126849                               SMART E-MONEY                 10", IsoType.LLLVAR, 108);
			eMoneyReversalReq.setValue(49, "360", IsoType.NUMERIC, 3);
			String element90 = "0200" + "000037" + "0405140820" + "00000000153"+ "00000000153";
			eMoneyReversalReq.setValue(90, element90, IsoType.NUMERIC, 42);
			eMoneyReversalReq.setValue(100, "153", IsoType.LLVAR, 11);
			eMoneyReversalReq.setValue(102, "0011234567", IsoType.LLVAR, 28);
			eMoneyReversalReq.setValue(103, "8881628910080", IsoType.LLVAR, 28);
			System.out.println("eMoney Transfer Reversal REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(eMoneyReversalReq.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			eMoneyReversalReq.write(sock.getOutputStream(), 4, false);
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
			billPaymentReq.setValue(103, "88886288116210961", IsoType.LLVAR, 28); // to A/c
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
			paymentInquiryRequest.setValue(103, "8888088116210961", IsoType.LLVAR, 28);
			System.out.println("BillPaymentInquiry REQUEST:");
			System.out.println("------------------------------");
			System.out.println(new String(paymentInquiryRequest.writeData()));
			System.out.println("------------------------------");
			System.out.println();

			paymentInquiryRequest.write(sock.getOutputStream(), 4, false);
		}
		
		// Bill payment reversal.
		private void sendBillPaymentReversalRequest() throws IOException {
		
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
			//billPaymentReversalReq.setValue(48, "6288116210961                 123454321       6288116210961                 SMART BRANCH INDONESIA        10", IsoType.LLLVAR, 108);
			billPaymentReversalReq.setValue(49, "360", IsoType.NUMERIC, 3);
			String element90 = "0200" + "000011" + "0707132720" + "00000000009"+ "00000000153";
			billPaymentReversalReq.setValue(90, element90, IsoType.NUMERIC, 42);
			billPaymentReversalReq.setValue(100, "153", IsoType.LLVAR, 11);
			billPaymentReversalReq.setValue(102, "0011234567", IsoType.LLVAR, 28);
			billPaymentReversalReq.setValue(103, "88886288116210961", IsoType.LLVAR, 28);
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
			SelfTopup.setValue(2, "628910006", IsoType.LLVAR, 19);
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
			SelfTopup.setValue(48, "628910006                 123454321       628910006                 SMART BRANCH INDONESIA        10", IsoType.LLLVAR, 108);//Additional data
			SelfTopup.setValue(49, "360", IsoType.NUMERIC, 3);
			SelfTopup.setValue(100, "153", IsoType.LLVAR, 11);
			SelfTopup.setValue(102, "0011234567", IsoType.LLVAR, 28);//from A/c
			SelfTopup.setValue(103, "8001628910006", IsoType.LLVAR, 28); // to A/c
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
				log.debug(String.format("Parsing incoming: '%s'", new String(msg)));
//				String strMsg = new String(msg);
//				if(strMsg.contains("I"))
//				{
//					strMsg = strMsg.substring(9);
//					strMsg = strMsg.substring(0,strMsg.length() - 1);
//					msg = strMsg.getBytes();
//				}
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
//					sendTransferInquiryRequest();
//					sendTransferCreditRequest();
//					sendTransferCreditReversalRequest();
					//sendPaymentInquiryRequest();		
//				    sendBillPaymentRequest();
					
					
					
					//sendBillPaymentReversalRequest();
					
//					sendEMoneyTransferInquiryRequest();
					sendEMoneyTransferCreditRequest();
//					sendEMoneyReversalRequest();
//					
//					sendSelfTopupRequest();
					//sendOthersTopupRequest();
				    //sendTopUpReversalRequest();
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
					mfact = ConfigParser.createFromClasspathConfig("VAHconfig.xml");
				} catch (IOException e) {
					log.error("Failed to parse the config file", e);
					return;
				}
				
				Socket sock = null;
				ATMClientMock client = null;
				try {
					log.debug("Connecting to server");
					sock = new Socket("localhost", 8881);
					client = new ATMClientMock(sock);
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
				
				IsoMessage SignOnReq = mfact.newMessage(0x800);
				
				SignOnReq.setValue(7, "1108124959", IsoType.DATE10, 10);
				SignOnReq.setValue(11, "000004", IsoType.NUMERIC, 6);
				SignOnReq.setValue(33, "01234565432", IsoType.LLVAR, 11);
				SignOnReq.setValue(70, "001", IsoType.NUMERIC, 3);
				
				log.debug("Sending sign-on request");
				System.out.println("------------------------------");
				System.out.println(new String(SignOnReq.writeData()));
				System.out.println("------------------------------");
				
				SignOnReq.write(sock.getOutputStream(), 4, false);
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
