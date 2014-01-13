
package com.mfino.mock.iso8583;

import java.io.IOException;
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
			System.out.println("Exception occurred...");
		}
		System.out.println(String.format("Exiting after reading %s requests", count));
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
				System.out.println(String.format("Parsing incoming: '%s'", new String(msg)));
				IsoMessage incoming = mfact.parseMessage(msg, 0);
				System.out.println("\n" + incoming.toDebugString());

				switch (incoming.getType()) {
				case 0x210:
				case 0x410:
				case 0x810:
					System.out.println("Done with response message");
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
						System.out.println("SIGN-ON");
						response.setValue(39, "00", IsoType.ALPHA, 2);
					}
					// B. Network ECHO-TEST
					else if ("301".equals(incoming.getObjectValue(70))) {
						System.out.println("ECHO-TEST");
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
						System.out.println("Unrecognonized message type");
					}
					break;

				case 0x200:
					// 2. SMART DOMPET Administrative messages
					if ("900000".equals(incoming.getObjectValue(3))) {
						// A. M-Commerce Activation, pin setup
						if ("196".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
						}
						// B. PIN Change
						else if ("139".equals(incoming.getObjectValue(24))) {
							response.setValue(38, "654321", IsoType.ALPHA, 6);
							response.setValue(39, "00", IsoType.ALPHA, 2);
						} else {
							System.out.println("Unrecognonized message type");
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
						response.setValue(54, "1002360C000000003426",
								IsoType.LLLVAR, 0);
					}
					else if ("309800".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						//response.setValue(35, " ", IsoType.LLLVAR, 0);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(54, "1002360C000000003426",
								IsoType.LLLVAR, 0);
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
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					//  Transfer_Inquiry
					else if ("370000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					// Intra-Inter Bank Transfer_Inquiry
					else if ("379810".equals(incoming.getObjectValue(3))) {
						response.setValue(3, "371010", IsoType.NUMERIC, 6);
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
						if("011".equals(incoming.getObjectValue(22))) {
							response.setValue(35, "6396879950000022=311299164783845385", IsoType.LLVAR, 0);
							response.setValue(98, "106001                   ", IsoType.ALPHA, 25);
						}
						response.setValue(102, "0001344568", IsoType.LLVAR, 0);
					}
					//  Transfer_CashIn_Inquiry
					else if ("379820".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
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
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
				//  Transfer_CashOut_Inquiry
					else if ("372000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
				//  Transfer_CashOut_Inquiry
					else if ("372098".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					// D. Transfer to other SMART
					else if ("490000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					// D. Transfer to other SMART
					else if ("499898".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					// D. Intra Inter Bank Transfer
					else if ("491010".equals(incoming.getObjectValue(3))) {
						
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
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
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					// Transfer_CashIn
					else if ("499820".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					// Transfer_CashOut
					else if ("492000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					// Transfer_CashOut
					else if ("492098".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					else {
						System.out.println("Unrecognonized message type");
					}
					break;
				case 0x400:
					// E: Reversal Transfer
					if ("490000".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					if ("499898".equals(incoming.getObjectValue(3))) {
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
								IsoType.LLLVAR, 0);
					}
					// E. Intra Inter Bank Transfer
					else if ("491010".equals(incoming.getObjectValue(3))) {
						
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
						response.setValue(48, "MOCH FARDIAN RIZMAN HADI",
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
					else if ("560000".equals(incoming.getObjectValue(3))) {
						if (incoming.getObjectValue(90) != null) {

						}
						response.setValue(38, "654321", IsoType.ALPHA, 6);
						response.setValue(39, "00", IsoType.ALPHA, 2);
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
						System.out.println("Unrecognonized message type");
					}
					break;
				default:
					System.out.println("Unrecognonized message type");
					break;
				}

				System.out.println("Sending response ISO message:\n" + response.toDebugString());
				response.write(sock.getOutputStream(), 4, false);
				
				if("001".equals(incoming.getObjectValue(70))){
					IsoMessage keyExchangeMsg = mfact.newMessage(0x800);
					keyExchangeMsg.setValue(7, incoming.getObjectValue(7), IsoType.DATE10, 10);
					keyExchangeMsg.setValue(11, incoming.getObjectValue(1), IsoType.NUMERIC, 6);
					keyExchangeMsg.setValue(33, incoming.getObjectValue(33), IsoType.LLVAR, 11);
					keyExchangeMsg.setValue(48, "82BB6123284106A8C2CA91E1B9E2588801FDC91F831AF87E", IsoType.LLLVAR, 0);
					keyExchangeMsg.setValue(70, "101", IsoType.NUMERIC, 3);
					System.out.println("Sending keyu exchange ISO message:\n" + incoming.toDebugString());
					keyExchangeMsg.write(sock.getOutputStream(), 4, false);
				}
				
			} catch (ParseException ex) {
				System.out.println("Error parsing incoming message");
			} catch (IOException ex) {
				System.out.println("Error sending response");
			}catch(Throwable t){
				System.out.println("Unexpected error");
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
					System.out.println("Failed to parse the config file");
					return;
				}
				System.out.println("Setting up server socket...");
				ServerSocket server;
				try {
					//server = new ServerSocket(9998);
					server = new ServerSocket(31532);
				} catch (IOException e) {
					System.out.println("Failed to set up server socket");
					return;
				}
				System.out.println("Waiting for connections...");
				
				while (true) {
					Socket sock;
					try {
						sock = server.accept();
					} catch (IOException e) {
						System.out.println("Failed to accept socket connection");
						break;
					}
					System.out.println(String.format("New connection from %s:%s", 
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
}
