
package com.mfino.mock.iso8583;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.impl.SimpleTraceGenerator;
import com.solab.iso8583.parse.ConfigParser;

public class ISO8583Client {

	private static Logger	log	= LoggerFactory.getLogger(ISO8583Server.class);

	private static MessageFactory mfact;
	private static ConcurrentHashMap<String, IsoMessage> pending = new ConcurrentHashMap<String, IsoMessage>();

	private Socket sock;
	private boolean done = false;

	public ISO8583Client(Socket socket) {
		sock = socket;
	}

	public void run() {
		byte[] lenbuf = new byte[4];
		try {
			// For high volume apps you will be better off only reading the
			// stream in one thread
			// and then using another thread to parse the buffers and process
			// the responses
			// Otherwise the network buffer might fill up and you can miss a
			// message.
			while (sock != null && sock.isConnected()) {
				int length = sock.getInputStream().read(lenbuf);
				if (length <= 0) {
					log.debug("No data read");
					Thread.sleep(1000);
					continue;
				}
				int size = Integer.parseInt(new String(lenbuf));
				byte[] buf = new byte[size];
				log.debug(String.format("Reading data of {}", size));
				if (sock.getInputStream().read(buf) == size) {
					log.debug(new String(buf));
					IsoMessage message = mfact.parseMessage(buf, 0);
					System.out.println(message);
				} else {
					log.debug("Failed to read data");
					pending.clear();
					return;
				}
			}
		} catch (IOException ex) {
			if (done) {
				log.info(String.format("Socket closed because we're done (%d pending)",
						pending.size()));
			} else {
				log.error(String.format("Reading responses, %d pending",
						pending.size()), ex);
				try {
					sock.close();
				} catch (IOException ex2) {
				}
				;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (sock != null) {
				try {
					sock.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	protected void stop() {
		done = true;
		try {
			sock.close();
		} catch (IOException ex) {
			log.error("Couldn't close socket");
		}
		sock = null;
	}

	public static void main(String[] args) throws Exception {
		
		log.debug("Reading config");
		mfact = ConfigParser.createFromClasspathConfig("config.xml");
		mfact.setAssignDate(true);
		mfact.setTraceNumberGenerator(new SimpleTraceGenerator((int) (System
				.currentTimeMillis() % 10000)));
		log.debug("Connecting to server");
		Socket sock = new Socket("localhost", 9999);
		
		ISO8583Client client = new ISO8583Client(sock);
//		IsoMessage req = mfact.newMessage(0x800);
//		req.setValue(33, "881", IsoType.LLVAR, 3);
//		req.setValue(70, "001", IsoType.NUMERIC, 3); 
//		req.setBinary(false);
//		pending.put(req.getField(11).toString(), req);
//		log.debug(String.format("Sending request %s %s", req.getField(11), req.getField(7)));
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		req.write(os, 4, false);
		byte[] temp = os.toByteArray();
		log.debug(new String(temp));
		String message = "0200623A4021A8621000166396879931004259300000032414044208618914044203240324601760388103881166396879931004259000000086189628811586597   SMS SMART                               00586189B7EF07D86498C424";
		sock.getOutputStream().write(message.getBytes());
		
//		req.write(sock.getOutputStream(), 4, false);
//		log.debug(new String(req.writeData()));
		log.debug("Waiting for responses");
		
		client.run();
		client.stop();
		
		log.debug("DONE.");
	}
}
