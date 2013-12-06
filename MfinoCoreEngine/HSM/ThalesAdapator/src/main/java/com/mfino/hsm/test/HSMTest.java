package com.mfino.hsm.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class HSMTest {
	 public static void main(String args[]) {

		  System.out.println("<<< Main Method Entry >>>");
		  System.out.println("<<< Given Ip ............ : " + args[1]);
		  String ipAddress = args[1];
		  System.out.println("<<< Given Port ............ : " + args[2]);
		  int port = Integer.parseInt(args[2]);
		  String command = "0004NC";
		  command = args[0];
		  Socket socket = null;
		  DataOutputStream out = null;
		  DataInputStream in = null;
		  byte[] b= new byte[100];
		  try {
		    socket = new Socket(ipAddress, port);
		    System.out.println("<<< Sockt s >>> :" + socket);
		    if (socket != null) {
		     System.out.println("<<< Connected to HSM  >>>:"
		       + socket.isConnected());
		    in = new DataInputStream (new BufferedInputStream(socket.getInputStream()));
		    out = new DataOutputStream (new BufferedOutputStream(socket.getOutputStream()));
		          
		          out.writeUTF(command);
		          System.out.println("Input to HSM :" +command);
		          out.flush();
		          String response = in.readUTF();
		          System.out.println("Output from HSM :" +response);
		          System.out.println("");
		   }
		  } catch (Exception ex) {
		   try {
		    socket.close();
		   } catch (Exception e) {
		   }
		   System.out.println("<<< Exception Message :: >>" + ex.getMessage());
		   ex.printStackTrace();
		  } finally {
		   try {
		    out.close();
		    in.close();
		    System.out.println("<<< Out/In Stream Closed>>>");
		    socket.close();
		    System.out.println("<<< Socket Closed>>>");
		   } catch (Exception exception) {
		    exception.printStackTrace();
		   }
		  }
		 }

}
