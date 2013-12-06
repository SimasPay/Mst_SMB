package com.mfino.fidelity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOSource;
import org.jpos.iso.ServerChannel;
import org.jpos.iso.channel.PostChannel;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.LogSource;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

public class FidelityISOServer implements ISORequestListener {

	private ExecutorService pool;
	
	public FidelityISOServer() {
		super();
		pool = Executors.newFixedThreadPool(10);
	}

	public boolean process(final ISOSource source, final ISOMsg m) {
		pool.execute(new RequestHandler(m, source));
		return true;
	}

	public static void main(String[] args) throws Exception {
		FidelityISOServer fidelityISOServer = new FidelityISOServer();
		fidelityISOServer.start();
	}

	private void start() throws ISOException, NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter port to listen:");
		int port = Integer.parseInt(reader.readLine());
		ServerChannel channel = new PostChannel();
		Logger logger = new Logger ();
		logger.addListener (new SimpleLogListener (System.out));
		((LogSource)channel).setLogger (logger, "channel");
		channel.setPackager(new GenericPackager("fidelity.xml"));
		ISOServer server = new ISOServer(port, channel, null);
		server.addISORequestListener(new FidelityISOServer());
		server.setLogger (logger, "server");
		new Thread (server).start ();
		
	}
}