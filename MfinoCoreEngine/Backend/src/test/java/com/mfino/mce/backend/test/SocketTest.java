package com.mfino.mce.backend.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class SocketTest {
	
	public static void main(String[] args) {
		System.out.println("socket test");
		try {
			int port = 9901;
			SocketAddress sockaddr = new InetSocketAddress("172.29.8.192", port);
			Socket sock = new Socket();
			
			
			int timeoutMs = 2000; // 2 seconds9.8.192
			sock.connect(sockaddr, timeoutMs);
			
			sock.getOutputStream().write("08008238000000000000040000000000000011051047581000001047581105001".getBytes());
//			System.out.println(sock.getInputStream().read());
			
			System.out.println("successful");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
