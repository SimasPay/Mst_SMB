
package com.mfino.mock.iso8583;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CmFinoFIX;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.IsoType;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

public class ISO8583Server implements Runnable {

	private static String fileName;
	
	private static Logger	log	= LoggerFactory.getLogger(ISO8583Server.class);

//	private static ScheduledExecutorService threadPool = Executors
//			.newScheduledThreadPool(100);
	
	ExecutorService threadPool = Executors.newFixedThreadPool(100);
	
	private static MessageFactory mfact;

	private Socket socket;
	
	private boolean sentInitialNTMRequest;


	static Properties Property;
	
	
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
//					threadPool.schedule(new Processor(buf, socket), 0,
//							TimeUnit.MILLISECONDS);
					threadPool.execute(new Processor(buf, socket));
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
		
		private void sendRegistrationRequest() throws IOException {
			IsoMessage registrationRequest = mfact.newMessage(0x200);
			//registrationRequest.set(2, msg.getInfo2());// msg.getSourceCardPAN()
		//	String processingCode = GetConstantCodes.getTransactionType(msg);
			registrationRequest.setValue(2, "4847779905000046", IsoType.LLVAR, 19);
			registrationRequest.setValue(3,"980000", IsoType.NUMERIC, 6);
			registrationRequest.setValue(7, "0729110250", IsoType.DATE10, 10); // 7
			registrationRequest.setValue(11, "000011", IsoType.NUMERIC, 6);// 11
			registrationRequest.setValue(12, "132720", IsoType.TIME, 6); // 12
			registrationRequest.setValue(13, "0707", IsoType.DATE4, 4); // 13
			registrationRequest.setValue(18, "6011", IsoType.NUMERIC, 4); // 18
			registrationRequest.setValue(24, "213",IsoType.NUMERIC, 3);
			registrationRequest.setValue(27,"2",IsoType.NUMERIC,1); // 27
			registrationRequest.setValue(32,CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 32
			registrationRequest.setValue(33,CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 33
			//isoMsg.setValue(34, msg.getSourceMDN());
			registrationRequest.setValue(37,"000000000011", IsoType.ALPHA, 12);
			registrationRequest.setValue(42, "2349553319690",IsoType.ALPHA,15);
			registrationRequest.setValue(43, "SMS SMART                               ",IsoType.ALPHA, 40);

			//		sourcemdn (msisdn) de-61 field
			//		accountnumber (sourceaccountnumber) de-102 field
			//		cif no? (additionalprivatedate) de-48 
			//		pin (Encryptedvalue) de-52
			registrationRequest.setValue(48, "00356568475776         8745745642                              ",IsoType.LLLVAR,63);
			/*
			Random random = new Random();
			Long randomLong = random.nextLong();
			if(randomLong<0)
				randomLong = randomLong * -1;
			String accountNumber=randomLong.toString().substring(0,13);
			String mdn = randomLong.toString().substring(0, 10);
			*/
			String accountNumber=Property.getProperty("accountno");
			String mdn=Property.getProperty("mdn");
			String pin=Property.getProperty("pin");
			String mpan=Property.getProperty("mpan");
			registrationRequest.setValue(2, mpan, IsoType.LLVAR, 19);
			registrationRequest.setValue(61,mdn,IsoType.LLLVAR,16);
			registrationRequest.setValue(102, accountNumber,IsoType.LLVAR,28);
			registrationRequest.setValue(52, pin, IsoType.ALPHA, 16);
			registrationRequest.setValue(121,"A",IsoType.LLLVAR,1);
			registrationRequest.write(sock.getOutputStream(), 4, false);
		}
		private void sendChangePinRequest() throws IOException {
			IsoMessage changePinRequest = mfact.newMessage(0x200);
			changePinRequest.setValue(3,"980000", IsoType.NUMERIC, 6);
			changePinRequest.setValue(7, "0729110250", IsoType.DATE10, 10); // 7
			changePinRequest.setValue(11, "000011", IsoType.NUMERIC, 6);// 11
			changePinRequest.setValue(12, "132720", IsoType.TIME, 6); // 12
			changePinRequest.setValue(13, "0707", IsoType.DATE4, 4); // 13
			changePinRequest.setValue(18, "6011", IsoType.NUMERIC, 4); // 18
			changePinRequest.setValue(24, "139",IsoType.NUMERIC, 3);
			changePinRequest.setValue(27,"2",IsoType.NUMERIC,1); // 27
			changePinRequest.setValue(32,CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 32
			changePinRequest.setValue(33,CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 33
			changePinRequest.setValue(37,"000000000011", IsoType.ALPHA, 12);
			changePinRequest.setValue(42, "2349553319690",IsoType.ALPHA,15);
			changePinRequest.setValue(43, "SMS SMART                               ",IsoType.ALPHA, 40);
			changePinRequest.setValue(48, "00356568475776         8745745642                              ",IsoType.LLLVAR,63);
			Random random = new Random();
			Long randomLong = random.nextLong();
			if(randomLong<0)
				randomLong = randomLong * -1;
			String accountNumber=randomLong.toString().substring(0,13);
			String mdn = randomLong.toString().substring(0, 10);
			changePinRequest.setValue(61,mdn,IsoType.LLLVAR,16);
			changePinRequest.setValue(102, accountNumber,IsoType.LLVAR,28);
			//hack starts
			changePinRequest.setValue(61,"4556846583",IsoType.LLLVAR,16);
			changePinRequest.setValue(102, "4556846583044",IsoType.LLVAR,28);
			changePinRequest.setValue(52, "E31C262E8ADFBCC8", IsoType.ALPHA, 16);
			changePinRequest.write(sock.getOutputStream(), 4, false);
		}
		
		private void sendCashinInquiryRequest() throws IOException {
			
			String mdn=Property.getProperty("cashinmdn");
			String amount=Property.getProperty("amount");
			String rrn = Property.getProperty("rrn");
			
			IsoMessage cashinInquiryRequest = mfact.newMessage(0x200);
			cashinInquiryRequest.setValue(2, "4847779905000046", IsoType.LLVAR, 19);
			cashinInquiryRequest.setValue(3,"370000", IsoType.NUMERIC, 6);
			cashinInquiryRequest.setValue(4, amount, IsoType.NUMERIC, 18);
			cashinInquiryRequest.setValue(7, "0729110250", IsoType.DATE10, 10); // 7
			cashinInquiryRequest.setValue(11, rrn, IsoType.NUMERIC, 6);// 11
			cashinInquiryRequest.setValue(12, "132720", IsoType.TIME, 6); // 12
			cashinInquiryRequest.setValue(13, "0707", IsoType.DATE4, 4); // 13
			cashinInquiryRequest.setValue(18, "6011", IsoType.NUMERIC, 4); // 18
			cashinInquiryRequest.setValue(24, "213",IsoType.NUMERIC, 3);
			cashinInquiryRequest.setValue(27,"2",IsoType.NUMERIC,1); // 27
			cashinInquiryRequest.setValue(32,CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 32
			cashinInquiryRequest.setValue(33,CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 33
			cashinInquiryRequest.setValue(37,rrn, IsoType.ALPHA, 12);
			cashinInquiryRequest.setValue(42, "2349553319690",IsoType.ALPHA,15);
			cashinInquiryRequest.setValue(43, "SMS SMART                               ",IsoType.ALPHA, 40);
			cashinInquiryRequest.setValue(48, "00356568475776         8745745642                              ",IsoType.LLLVAR,63);
			cashinInquiryRequest.setValue(103, mdn,IsoType.LLVAR,28);
			cashinInquiryRequest.setValue(121,"A",IsoType.LLLVAR,1);
			cashinInquiryRequest.write(sock.getOutputStream(), 4, false);
		}
		
		private void sendCashinRequest() throws IOException {
			
			String mdn=Property.getProperty("cashinmdn");
			String amount=Property.getProperty("amount");
			String rrn = Property.getProperty("rrn");
			String source = Property.getProperty("source");
			
			IsoMessage cashinRequest = mfact.newMessage(0x200);
			cashinRequest.setValue(2, "4847779905000046", IsoType.LLVAR, 19);
			
			if(source.equals("1")) {
			
				cashinRequest.setValue(3,"490000", IsoType.NUMERIC, 6);
				
			} else if(source.equals("2")) {
				
				cashinRequest.setValue(3,"470000", IsoType.NUMERIC, 6);
			}
			cashinRequest.setValue(4, amount, IsoType.NUMERIC, 18);
			cashinRequest.setValue(7, "0729110250", IsoType.DATE10, 10); // 7
			cashinRequest.setValue(11, rrn, IsoType.NUMERIC, 6);// 11
			cashinRequest.setValue(12, "132720", IsoType.TIME, 6); // 12
			cashinRequest.setValue(13, "0707", IsoType.DATE4, 4); // 13
			cashinRequest.setValue(18, "6011", IsoType.NUMERIC, 4); // 18
			cashinRequest.setValue(24, "213",IsoType.NUMERIC, 3);
			cashinRequest.setValue(27,"2",IsoType.NUMERIC,1); // 27
			cashinRequest.setValue(32,CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 32
			cashinRequest.setValue(33,CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 33
			cashinRequest.setValue(37,rrn, IsoType.ALPHA, 12);
			cashinRequest.setValue(42, "2349553319690",IsoType.ALPHA,15);
			cashinRequest.setValue(43, "SMS SMART                               ",IsoType.ALPHA, 40);
			cashinRequest.setValue(48, "00356568475776         8745745642                              ",IsoType.LLLVAR,63);
			cashinRequest.setValue(100, 153,IsoType.LLVAR,28);
			cashinRequest.setValue(102, 153,IsoType.LLVAR,28);
			cashinRequest.setValue(103, mdn,IsoType.LLVAR,28);
			cashinRequest.setValue(121,"A",IsoType.LLLVAR,1);
			cashinRequest.write(sock.getOutputStream(), 4, false);
		}
		
		private void sendCashwithdrawalRefundInquiryRequest() throws IOException {
			
			String mdn=Property.getProperty("cashinmdn");
			String amount=Property.getProperty("amount");
			String rrn = Property.getProperty("rrn");
			String refundmdn = Property.getProperty("refundmdn");
			
			IsoMessage cashinInquiryRequest = mfact.newMessage(0x200);
			cashinInquiryRequest.setValue(2, "4847779905000046", IsoType.LLVAR, 19);
			cashinInquiryRequest.setValue(3,"370000", IsoType.NUMERIC, 6);
			cashinInquiryRequest.setValue(4, amount, IsoType.NUMERIC, 18);
			cashinInquiryRequest.setValue(7, "0729110250", IsoType.DATE10, 10); // 7
			cashinInquiryRequest.setValue(11, rrn, IsoType.NUMERIC, 6);// 11
			cashinInquiryRequest.setValue(12, "132720", IsoType.TIME, 6); // 12
			cashinInquiryRequest.setValue(13, "0707", IsoType.DATE4, 4); // 13
			cashinInquiryRequest.setValue(18, "6011", IsoType.NUMERIC, 4); // 18
			cashinInquiryRequest.setValue(24, "213",IsoType.NUMERIC, 3);
			cashinInquiryRequest.setValue(27,"2",IsoType.NUMERIC,1); // 27
			cashinInquiryRequest.setValue(32,CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 32
			cashinInquiryRequest.setValue(33,CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 33
			cashinInquiryRequest.setValue(37,rrn, IsoType.ALPHA, 12);
			cashinInquiryRequest.setValue(42, "2349553319690",IsoType.ALPHA,15);
			cashinInquiryRequest.setValue(43, "SMS SMART                               ",IsoType.ALPHA, 40);
			cashinInquiryRequest.setValue(48, "00356568475776         8745745642                              ",IsoType.LLLVAR,63);
			cashinInquiryRequest.setValue(102, refundmdn, IsoType.LLVAR,28);
			cashinInquiryRequest.setValue(103, mdn,IsoType.LLVAR,28);
			cashinInquiryRequest.setValue(121,"A",IsoType.LLLVAR,1);
			cashinInquiryRequest.write(sock.getOutputStream(), 4, false);
		}
		
		private void sendCashwithdrawalRefundRequest() throws IOException {
			
			String mdn=Property.getProperty("cashinmdn");
			String amount=Property.getProperty("amount");
			String rrn = Property.getProperty("rrn");
			String refundmdn = Property.getProperty("refundmdn");
			
			IsoMessage cashinRequest = mfact.newMessage(0x200);
			cashinRequest.setValue(2, "4847779905000046", IsoType.LLVAR, 19);
			cashinRequest.setValue(3,"470000", IsoType.NUMERIC, 6);
			cashinRequest.setValue(4, amount, IsoType.NUMERIC, 18);
			cashinRequest.setValue(7, "0729110250", IsoType.DATE10, 10); // 7
			cashinRequest.setValue(11, rrn, IsoType.NUMERIC, 6);// 11
			cashinRequest.setValue(12, "132720", IsoType.TIME, 6); // 12
			cashinRequest.setValue(13, "0707", IsoType.DATE4, 4); // 13
			cashinRequest.setValue(18, "6011", IsoType.NUMERIC, 4); // 18
			cashinRequest.setValue(24, "213",IsoType.NUMERIC, 3);
			cashinRequest.setValue(27,"2",IsoType.NUMERIC,1); // 27
			cashinRequest.setValue(32,CmFinoFIX.ISO8583_AcquiringInstIdCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 32
			cashinRequest.setValue(33,CmFinoFIX.ISO8583_ForwardingInstitutionIdentificationCode_Smart_To_Sinarmas.toString(),IsoType.LLVAR,11);// 33
			cashinRequest.setValue(37,rrn, IsoType.ALPHA, 12);
			cashinRequest.setValue(42, "2349553319690",IsoType.ALPHA,15);
			cashinRequest.setValue(43, "SMS SMART                               ",IsoType.ALPHA, 40);
			cashinRequest.setValue(48, "00356568475776         8745745642                              ",IsoType.LLLVAR,63);
			cashinRequest.setValue(100, 153,IsoType.LLVAR,28);
			cashinRequest.setValue(102, refundmdn, IsoType.LLVAR,28);
			cashinRequest.setValue(103, mdn,IsoType.LLVAR,28);
			cashinRequest.setValue(121,"A",IsoType.LLLVAR,1);
			cashinRequest.write(sock.getOutputStream(), 4, false);
		}
		
		public void run() {
			try {
				log.info(String.format("Parsing incoming: '%s'", new String(msg)));
				IsoMessage incoming = mfact.parseMessage(msg, 0);
				log.info("\n" + incoming.toDebugString());

				switch (incoming.getType()) {
				case 0x210:
				case 0x410:
				case 0x430:
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
						sentInitialNTMRequest = true;
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
					}
					// D. Network KEY-EXCHANGE
					else if ("101".equals(incoming.getObjectValue(70))) {
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}else{
						log.error("Unrecognonized message type");
					}
					break;

				case 0x200:
					// 2. SMART DOMPET Administrative messages
					if ("900000".equals(incoming.getObjectValue(3))) {
						// A. M-Commerce Activation, pin setup
						if ("196".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
//							response.setValue(102, "0001344568", IsoType.LLVAR, 0);
							response.setValue(102,createRandomInteger(1000000000, 9999999999L, new Random()), IsoType.LLVAR, 0);
						}
						// B. PIN Change
						else if ("139".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
							response.setValue(102,createRandomInteger(1000000000, 9999999999L, new Random()), IsoType.LLVAR, 0);
						} else {
							log.error("Unrecognonized message type");
						}
					}
					if ("901000".equals(incoming.getObjectValue(3))) {
						// A. M-Commerce Activation, pin setup
						if ("196".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
//							response.setValue(102, "0001344568", IsoType.LLVAR, 0);
							response.setValue(102,createRandomInteger(1000000000, 9999999999L, new Random()), IsoType.LLVAR, 0);
						}
						// B. PIN Change
						else if ("139".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
							response.setValue(102,createRandomInteger(1000000000, 9999999999L, new Random()), IsoType.LLVAR, 0);
						} else {
							log.error("Unrecognonized message type");
						}
					}
					if ("902000".equals(incoming.getObjectValue(3))) {
						// A. M-Commerce Activation, pin setup
						if ("196".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
//							response.setValue(102, "0001344568", IsoType.LLVAR, 0);
							response.setValue(102,createRandomInteger(1000000000, 9999999999L, new Random()), IsoType.LLVAR, 0);
						}
						// B. PIN Change
						else if ("139".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
							response.setValue(102,createRandomInteger(1000000000, 9999999999L, new Random()), IsoType.LLVAR, 0);
						} else {
							log.error("Unrecognonized message type");
						}
					}
					//subscriber Re-Activation for existing Users
					if ("909800".equals(incoming.getObjectValue(3))) {
						// A. M-Commerce Activation, pin setup
						if ("196".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
							response.setValue(102,createRandomInteger(1000000000, 9999999999L, new Random()), IsoType.LLVAR, 0);
						}
						// B. PIN Change
						else if ("139".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
							response.setValue(102,createRandomInteger(1000000000, 9999999999L, new Random()), IsoType.LLVAR, 0);
						} else {
							log.error("Unrecognonized message type");
						}
					}
					// 3. SMART DOMPET E-Load messages
					// A. TOPUP to self MDN (prepaid) from SMART Dompet account
					else if ("550000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					// A. TOPUP to self MDN (prepaid) from SMART Dompet account
					else if ("559800".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					// B. TOPUP other MDN (prepaid) from SMART Dompet account
					else if ("560000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(41, "87654321", IsoType.ALPHA, 8);
					}
					// B. TOPUP other MDN (prepaid) from SMART Dompet account
					else if ("569800".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(41, "87654321", IsoType.ALPHA, 8);
					}
					// 4. SMART DOMPET Mobile Banking messages
					// A. Balance inquiry
					else if ("300000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						//response.setValue(35, " ", IsoType.LLLVAR, 0);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
					}
					else if ("301000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						//response.setValue(35, " ", IsoType.LLLVAR, 0);
						Random r = new Random();
						if (r.nextInt(4) % 4 == 0) {
							Thread.sleep(65000);
						} 
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
					}
					else if ("302000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						//response.setValue(35, " ", IsoType.LLLVAR, 0);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
					}
					else if ("309800".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						//response.setValue(35, " ", IsoType.LLLVAR, 0);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000000034260000",
								IsoType.LLLVAR, 0);
					}
					// Get Subscriber Details
					else if ("320000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						//response.setValue(35, " ", IsoType.LLLVAR, 0);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						//response.setValue(54, "1002360C000000000034260000",
							//	IsoType.LLLVAR, 0);
						//response.setValue(48, "20304050601020304050simobisubscriberfirstnamefirstnamefirstnmiddlenamemiddlenamelastnamelastnamelassaliasonealiasonealiasonealiasonealiasone623456787774        hemanth.k@mfino.com                     24061991mothermaidensnameone",IsoType.LLLVAR , 300);
						response.setValue(48, "20304050601020304050simobisubscriberfirstnamefirstnamefirstnmiddlenamemiddlenamelastnamelastnamelassaliasonealiasonealiasonealiasonealiasone623456787774        hemanth.k@mfino.com                     24061991mothermaidensnameone",IsoType.LLLVAR , 300);
					}
					// B. Check last 3 transactions
					else if ("360000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(
										61,
										"100309DATM WithdrIDR000000008000000267FT09069134160309DATM WithdrIDR000000005000000267FT09075013",
										IsoType.LLLVAR, 0);
					}
					// B. Check last 3 transactions
					else if ("369800".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(
										61,
										"100309DATM WithdrIDR000000008000000267FT09069134160309DATM WithdrIDR000000005000000267FT09075013",
										IsoType.LLLVAR, 0);
					}
					
					// C. Transfer Inquiry
					else if ("379898".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					//  Transfer_Inquiry
					else if (incoming.getObjectValue(3).toString().startsWith("37")){
//					   response.setValue(3, "371010", IsoType.NUMERIC, 6);
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						String de4 = (String)incoming.getObjectValue(4);
						if ("000000000000100000".equals(de4)) {
							Thread.sleep(65000);
						}
						else if ("000000000000200000".equals(de4)) {
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						else {
							response.setValue(39, "00", IsoType.ALPHA, 2);
						}						
						response.setValue(48, "DONIH NISKALA                                                                                             00",
								IsoType.LLLVAR, 0);
					}
					else if ("372000".equals(incoming.getObjectValue(3))) {
					   response.setValue(3, "372010", IsoType.NUMERIC, 6);
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					//BillPay
					else if("380000".equals(incoming.getObjectValue(3))){
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(61,"0001344568",
								IsoType.LLLVAR, 0);
//						response.setValue(4, "000000000005148000", IsoType.NUMERIC, 18);
						response.setValue(4, "000000000020000000", IsoType.NUMERIC, 18);
						response.setValue(62, "08PAYMENT    : Telkom Fix Line (PSTN)   IDPEL      : 02188874874              NAME       :  WARINO                  BILLING AMT: RP. 51.480               ADMIN BANK : RP. 0                    TOTAL TAGIHAN : RP. 51.480                                                                                        02188874874     020008          11                000000000000000000000000000000000000                000000000000000000000000000000000000                000000000000000000000000000000000000701A            000000051480000000000000000000000000 WARINO                                         ", IsoType.LLLVAR, 0);
					}
					else if(incoming.getObjectValue(3).toString().startsWith("38")){
//					    response.setValue(3, "381010", IsoType.NUMERIC, 6);
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						Random r = new Random();
						int num = r.nextInt(5);
						/*if (num % 5 == 0) {
							Thread.sleep(65000);
						}
						else if (num % 4 == 0) {
							Thread.sleep(65000);
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						else {
							Thread.sleep(65000);
							response.setValue(39, "00", IsoType.ALPHA, 2);	
						}*/
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
//						response.setValue(62,"08PAYMENT    : Axis Postpaid            IDPEL      : 628382244000             NAME       : ARIEF SUSANTO MULAWARMAN BILLING AMT: RP. 11.111               ADMIN BANK : RP. 0                    PAYMENT AMT: RP. 11.111                                                                                           628382244000                    11                000000000000000000000000000000000000                000000000000000000000000000000000000                000000000000000000000000000000000000000000002708    000000011111000000000000000000000000ARIEF SUSANTO MULAWARMAN                          ", IsoType.LLLVAR, 0);
						response.setValue(62,"08PAYMENT    : Telkom Fix Line (PSTN)   IDPEL      : 02188874874              NAME       :  WARINO                  BILLING AMT: RP. 51.480               ADMIN BANK : RP. 0                    TOTAL TAGIHAN : RP. 51.480                                                                                        02188874874     020008          11                000000000000000000000000000000000000                000000000000000000000000000000000000                000000000000000000000000000000000000701A            000000051480000000000000000000000000 WARINO                                         ", IsoType.LLLVAR, 0);
						response.setValue(3, "381010", IsoType.NUMERIC, 6);
//						response.setValue(4, "1000", IsoType.NUMERIC, 18);
						response.setValue(4, "000000000010000000", IsoType.NUMERIC, 18);
						
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						String de4 = (String)incoming.getObjectValue(4);
						if ("000000000000100000".equals(de4)) {
							Thread.sleep(65000);
						}
						else if ("000000000000200000".equals(de4)) {
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						else {
							response.setValue(39, "00", IsoType.ALPHA, 2);
						}						
						response.setValue(48, "DONIH NISKALA                                                                                             00",
								IsoType.LLLVAR, 0);
						
						System.out.println("DE 3 starts with 38..........");
					}
					else if("382000".equals(incoming.getObjectValue(3))){
					    response.setValue(3, "382010", IsoType.NUMERIC, 6);
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
						response.setValue(61,"0001344568",
								IsoType.LLLVAR, 0);
					}
					// Intra-Inter Bank Transfer_Inquiry
					else if ("379810".equals(incoming.getObjectValue(3))) {
							boolean isSavingBankAccount = false;
						if(isSavingBankAccount == true){
							response.setValue(3, "371010", IsoType.NUMERIC, 6);
							response.setValue(102, "0001344568", IsoType.LLVAR, 0);
						}else{
							response.setValue(3, "379810", IsoType.NUMERIC, 6);
							response.setValue(102, "0001344568", IsoType.LLVAR, 0);//change to saving account value
						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
						response.setValue(103, "112233445566", IsoType.LLVAR, 0);
						if("011".equals(incoming.getObjectValue(22))) {
							response.setValue(35, "6396879950000022=311299164783845385", IsoType.LLVAR, 0);
							response.setValue(98, "106001                   ", IsoType.ALPHA, 25);
						}						
					}
					//  Transfer_CashIn_Inquiry
					else if ("379820".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
						if("011".equals(incoming.getObjectValue(22))) {
							response.setValue(35, "6396879950000022=311299164783845385", IsoType.LLVAR, 0);
							response.setValue(98, "106001                   ", IsoType.ALPHA, 25);
						}
					}
					// Transfer_CashIn Inquiry
					else if ("370020".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
				//  Transfer_CashOut_Inquiry
					else if ("372000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
				//  Transfer_CashOut_Inquiry
					else if ("372098".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					else if (incoming.getObjectValue(3).toString().startsWith("49")){
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						String de4 = (String)incoming.getObjectValue(4);
						if ("000000000000100100".equals(de4)) {
							Thread.sleep(65000);
						}
						else if ("000000000000200100".equals(de4)) {
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						else {
							response.setValue(39, "00", IsoType.ALPHA, 2);
						}	
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					// D. Transfer to other SMART
					else if ("499898".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						Thread.sleep(1000);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					// D. Intra Inter Bank Transfer
					else if ("499810".equals(incoming.getObjectValue(3))) {
						
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						Thread.sleep(60000);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
						if("011".equals(incoming.getObjectValue(22))) {
							response.setValue(35, "6396879950000022=311299164783845385", IsoType.LLVAR, 0);
							response.setValue(98, "106001                   ", IsoType.ALPHA, 25);
						}						
					}
					// Transfer_CashIn
					else if ("490020".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					// Transfer_CashIn
					else if ("499820".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					// Transfer_CashOut
					else if ("492000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						String de11 = (String)incoming.getObjectValue(11);
						de11 = de11.substring(4);
						int num = Integer.parseInt(de11);
						if (num % 9 == 0) {
							Thread.sleep(60000);
							if (num % 2 == 0) {
								response.setValue(39, "00", IsoType.ALPHA, 2);
							}
							else {
								response.setValue(39, "06", IsoType.ALPHA, 2);								
							}
						}
						else if (num % 8 == 0) {
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						else {
							response.setValue(39, "00", IsoType.ALPHA, 2);							
						}
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					// Transfer_CashOut
					else if ("492098".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					else if("500000".equals(incoming.getObjectValue(3))){
					    response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						//Thread.sleep(50000);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
						response.setValue(61,"TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					else if(incoming.getObjectValue(3).toString().startsWith("50")){
					    response.setValue(38, "654321", IsoType.ALPHA, 6);
					    String de4 = (String)incoming.getObjectValue(4);
						if ("000000000000300000".equals(de4)) {
							Thread.sleep(65000);
						}
						else if ("000000000000400000".equals(de4)) {
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						else {
							response.setValue(39, "00", IsoType.ALPHA, 2);	
						}					    
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
						
					}
					else if("501010".equals(incoming.getObjectValue(3))){
					    response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						//Thread.sleep(50000);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
						response.setValue(61,"TABUNGANKU B",
								IsoType.LLLVAR, 0);
					    response.setValue(62,"12PEMBAYARAN : ITC BSD & MALIBU         NO BAYAR   : 0060425                  NAMA       : ANDI GUNTORO             JML TAGIHAN: RP. 151.100              JML BAYAR  : RP. 151.100              PERIODE    : 04/13                    PEMBAYARAN : ITC BSD & MALIBU         NO BAYAR   : 0060425                  NAMA       : ANDI GUNTORO             JML TAGIHAN: RP. 151.100              JML BAYAR  : RP. 151.100              PERIODE    : 04/13                    ",
								IsoType.LLLVAR, 0);
					}
					else if("560000".equals(incoming.getObjectValue(3))){
					    response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						//Thread.sleep(50000);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
						response.setValue(61,"TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					else if(incoming.getObjectValue(3).toString().startsWith("56")){
					    response.setValue(38, "654321", IsoType.ALPHA, 6);
						Random r = new Random();
						int num = r.nextInt(5);
						if (num % 9 == 0) {
							Thread.sleep(65000);
						}
						else if (num % 8 == 0) {
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						else {
							response.setValue(39, "00", IsoType.ALPHA, 2);	
						}					    
						response.setValue(48, "TABUNGANKU Baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
								IsoType.LLLVAR, 0);
						response.setValue(62,"10PURCHASE : Telkomsel                MOBILE NO  : "+incoming.getObjectValue(42)+"            DENOM      : RP. "+incoming.getObjectValue(4)+"              ADMIN BANK : RP. "+incoming.getObjectValue(63)+"                VOUCHER REF: 0041000860488803                 FOR COMPLAINT CALL 155          NPWP TELKOMSEL:01.718.327.8.093.000          WISMA MULIA LT. M-19             JL. JEND GATOT SUBROTO KAV. 42                 JAKARTA 12710",
								IsoType.LLLVAR, 0);
						
					}
					else if("561010".equals(incoming.getObjectValue(3))){
					    response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						//Thread.sleep(50000);
						response.setValue(48, "TABUNGANKU Baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
								IsoType.LLLVAR, 0);
						response.setValue(61,"TABUNGANKU Baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
								IsoType.LLLVAR, 0);
					}
					else {
						log.error("Unrecognonized message type");
					}
					break;
				case 0x400:
				case 0x420:
					// E: Reversal Transfer
					if (incoming.getObjectValue(3).toString().startsWith("49")) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						Random r = new Random();
						int num = r.nextInt(5);
						if (num % 9 == 0) {
//							Thread.sleep(65000);
							response.setValue(39, "00", IsoType.ALPHA, 2);
						}
						else if (num % 8 == 0) {
							response.setValue(39, "00", IsoType.ALPHA, 2);
						}
						else {
							response.setValue(39, "00", IsoType.ALPHA, 2);	
						}						
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					if ("499898".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
					}
					// E. Intra Inter Bank Transfer reversal
					else if ("491010".equals(incoming.getObjectValue(3))) {
						
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						Random r = new Random();
						int num = r.nextInt(5);
						if (num % 5 == 0) {
							Thread.sleep(65000);
						}
						else if (num % 4 == 0) {
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						else {
							response.setValue(39, "00", IsoType.ALPHA, 2);	
						}						
						response.setValue(48, "TABUNGANKU B",
								IsoType.LLLVAR, 0);
						if("011".equals(incoming.getObjectValue(22))) {
							response.setValue(35, "6396879950000022=311299164783845385", IsoType.LLVAR, 0);
							response.setValue(98, "106001                   ", IsoType.ALPHA, 25);
						}
					}
					// C. Reversal TOPUP to self MDN
					else if ("550000".equals(incoming.getObjectValue(3))) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					// C. Reversal TOPUP to self MDN
					else if ("559800".equals(incoming.getObjectValue(3))) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					// D. Reversal TOPUP to other MDN
					else if (incoming.getObjectValue(3).toString().startsWith("56")) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						Random r = new Random();
						int num = r.nextInt(5);
						if (num % 5 == 0) {
							Thread.sleep(65000);
						}
						else if (num % 4 == 0) {
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						else {
							response.setValue(39, "00", IsoType.ALPHA, 2);	
						}						
						response.setValue(41, "87654321", IsoType.ALPHA, 8);
					}
					else if (incoming.getObjectValue(3).toString().startsWith("50")) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						Random r = new Random();
						int num = r.nextInt(5);
						if (num % 5 == 0) {
//							Thread.sleep(65000);
							response.setValue(39, "00", IsoType.ALPHA, 2);
						}
						else if (num % 4 == 0) {
							response.setValue(39, "06", IsoType.ALPHA, 2);
						}
						else {
							response.setValue(39, "00", IsoType.ALPHA, 2);	
						}						
						response.setValue(41, "87654321", IsoType.ALPHA, 8);
					}					
					// D. Reversal TOPUP to other MDN
					else if ("569800".equals(incoming.getObjectValue(3))) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(41, "87654321", IsoType.ALPHA, 8);
					} else {
						log.error("Unrecognonized message type");
					}
					break;
				default:
					log.error("Unrecognonized message type");
					break;
				}

				log.info("Sending response ISO message:\n" + response.toDebugString());
				response.write(sock.getOutputStream(), 4, false);
				if("001".equals(incoming.getObjectValue(70))){
					IsoMessage keyExchangeMsg = mfact.newMessage(0x800);
					keyExchangeMsg.setValue(7, incoming.getObjectValue(7), IsoType.DATE10, 10);
					keyExchangeMsg.setValue(11, incoming.getObjectValue(1), IsoType.NUMERIC, 6);
					keyExchangeMsg.setValue(33, incoming.getObjectValue(33), IsoType.LLVAR, 11);
					keyExchangeMsg.setValue(48, "82BB6123284106A8C2CA91E1B9E2588801FDC91F831AF87E", IsoType.LLLVAR, 0);
					keyExchangeMsg.setValue(70, "101", IsoType.NUMERIC, 3);
					log.info("Sending keyu exchange ISO message:\n" + incoming.toDebugString());
					keyExchangeMsg.write(sock.getOutputStream(), 4, false);
				}
				if(sentInitialNTMRequest && Property!=null) {
					String serviceType = Property.getProperty("servicetype");
					
					
					//Thread.sleep(30000);
					
					if(serviceType.equals("1"))
						sendRegistrationRequest();
					else if(serviceType.equals("2"))
						sendCashinInquiryRequest();
					else if(serviceType.equals("3"))
						sendCashinRequest();
					else if(serviceType.equals("4"))
						sendCashwithdrawalRefundInquiryRequest();
					else if(serviceType.equals("5"))
						sendCashwithdrawalRefundRequest();
					else
						System.out.println("Given a wrong data for servicetype");
						
					//sendRegistrationRequest();
					//sendChangePinRequest();
					//sendCashinInquiryRequest();
					//sendCashinRequest();
					//sendCashinRefundInquiryRequest();
					//sendCashinRefundRequest();
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
					mfact = ConfigParser.createFromClasspathConfig("config.xml");
				} catch (IOException e) {
					log.error("Failed to parse the config file", e);
					return;
				}
				log.info("Setting up server socket...8888");
				ServerSocket server;
				try {
					server = new ServerSocket(8888);
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
		String currentDir = new File(".").getAbsolutePath();
		currentDir=currentDir.replace(".", "");
		fileName = currentDir+"atmDetails.txt";
		System.out.println("checking for " +fileName+" for atmregistration/changepin request");
		File file = new File(fileName);
		 if (file.exists()) {
			  Property = new Properties();
		   FileInputStream inLoadFile = new FileInputStream(file);
		   Property.load(inLoadFile);	//loading properties into object from file
		   inLoadFile.close();
		}else{
			System.out.println("atmDetails.txt file not found");
		}
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
