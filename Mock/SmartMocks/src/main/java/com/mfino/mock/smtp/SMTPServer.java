package com.mfino.mock.smtp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.mock.iso8583.ATMClientMock;

public class SMTPServer implements Runnable {

	private static Logger	log	= LoggerFactory.getLogger(SMTPServer.class);

	private static Thread worker;
	private Socket socket;
	
	public SMTPServer(Socket s){
		this.socket = s;
	}
	
	public static boolean isRunning(){
		return worker != null && worker.isAlive();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException{
		start();
		
		while(true){
			if(isRunning()){
				Thread.sleep(1000);
			}else{
				break;
			}
		}
	}
	
	public static void stop(){
		if(isRunning()){
			worker.interrupt();
		}
	}
	
	public static void start(){
		stop();
		
		worker = new Thread(new Runnable(){
			@Override
			public void run() {
				log.info("Setting up server socket...");
				ServerSocket server;
				try {
					server = new ServerSocket(2525);
				} catch (IOException e) {
					log.error("Failed to serve!!", e);
					return;
				}
				log.info("Waiting for connections...");
				
				while (true) {
					Socket sock;
					try {
						
						sock = server.accept();
					} catch (IOException e) {
						log.error("Failed to accept connection!!", e);
						break;
					}
					log.info(String.format("New connection from %s:%s", 
							sock.getInetAddress(), 
							sock.getPort()));
					
					new Thread(new SMTPServer(sock), "smtp-server").start();
				}
			}
		});
		
		worker.start();
	}

	@Override
	public void run() {
		//this method reads the input line by line and finds the response
		try {
			BufferedReader scan = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			boolean isResponding = true;
			boolean isClosing = false;
			
			while (socket.isConnected()
					&& Thread.currentThread().isAlive()
					&& !Thread.currentThread().isInterrupted()) 
			{
				log.info("reading content from socket\n");
				String request = scan.readLine();
				log.info("done reading content from socket\n");
				log.info(request);
				
				if(request == null)
				{
					Thread.sleep(5000);
					log.info("sleeping for 5 secs");
					continue;
				}

				log.info(request);
				String response = null;
				
				if(isResponding){
					response = "250";
				}
				
				if(request.startsWith("data")){
					response = "354";
					isResponding = false;
				}else if(request.startsWith("quit")){
					response = "221";
					isClosing = true;
				}else if(request.startsWith(".")){
					response = "250";
				}
				
				if(response != null){
					log.info(response);
					response = response + " \r\n";
					socket.getOutputStream().write(response.getBytes());
				}
				
				if(isClosing){
					break;
				}
			}
		} catch (IOException ex) {
			log.error("Exception occurred...", ex);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.error("error while sleeping",e);
		}

		log.info(String.format("Exiting..."));
		
		try {
			socket.close();
		} catch (IOException ex) {
		}
	}
}
