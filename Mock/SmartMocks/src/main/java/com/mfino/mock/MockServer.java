package com.mfino.mock;

import com.mfino.mock.iso8583.ISO8583Server;
import com.mfino.mock.smtp.SMTPServer;

public class MockServer {

	public static void main(String[] args) throws InterruptedException{
		//start all mock servers 
		ISO8583Server.start();
		SMTPServer.start();
		
		Thread.sleep(99999999999l);
	}
}
