package com.mfino.hsm.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;

import net.sf.json.JSONObject;

public class HSMTest {
	 public static void main(String args[]) {
		 JSONObject json = new JSONObject();
		 for(int i =0;i<2;i++){
		  System.out.println("<<< Main Method Entry >>>");
		  System.out.println("<<< Given Ip ............ : " + args[1]);
		  String ipAddress = args[1];
		  System.out.println("<<< Given Port ............ : " + args[2]);
		  int port = Integer.parseInt(args[2]);
		  StringBuffer sb = new StringBuffer();
		  sb.append("0000JG544B13CB7616C02201");
		  Random random = new Random();
		  Long longacnumber = random.nextLong();
		  String accountNumber = longacnumber.toString().substring(2,14);
		  sb.append(accountNumber);
		  sb.append("01234");
		  String command = sb.toString();
		  //command = args[0];
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
		          System.out.println("response json :" + response.substring(8, 24));
		          json.put(accountNumber, response.substring(8, 24));
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
		System.out.println("json objecgt \n"+json.toString());
	 }
}
