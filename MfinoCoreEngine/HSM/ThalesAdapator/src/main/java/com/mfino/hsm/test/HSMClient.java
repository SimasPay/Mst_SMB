package com.mfino.hsm.test;

import java.io.*;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.*;

class HSMClient
{
    public static void main(String [] arg) throws Exception
    {
        String sentence;
        String modifiedSentence;
		
        String command="0000NC";
        int port= 9991 ;
        String host="localhost";
        if(arg.length>0)
        	command =arg[0];
        if(arg.length>1)
        {
		  host = arg[1];
		  port =  Integer.parseInt(arg[2]);
        }
		
		
		byte[] lenghPrependedCommand = prependLength(command.getBytes());
		//byte[] lenghPrependedCommand = command.getBytes();
		String hexString = new String(binToHex(lenghPrependedCommand));
		System.out.println("hex command:"+hexString);
       Socket clientSocket = new Socket(host,port);

        //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        InputStream inFromServer = clientSocket.getInputStream();
        
        // send to server
        outToServer.write(lenghPrependedCommand);
        
       // result length
        byte lenBytes[] = new byte[2];
        int resLen = lenBytes[0];
        System.out.println("length bytes:"+lenBytes[0] +" "+lenBytes[1] );
        resLen = resLen<< 8;
        resLen = resLen +lenBytes[1];
        System.out.println("length:"+resLen);
        byte[] resByteArray = new byte[resLen+2];
        inFromServer.read(resByteArray, 2, resLen);
        System.out.println("FROM SERVER: " + new String(binToHex(resByteArray)));
        clientSocket.close();
    }
    
    private static byte[] prependLength(byte[] input) 
    {
    	byte[] res = new byte[input.length+2];
    	int len = input.length;
    	res[0] = (byte)(len >>> 8);
        res[1] = (byte)len;
    	for(int i=0;i<input.length;i++)
    	{
    		res[i+2] = input[i];
    	}
		return res;
	}

	public static char[] binToHex(byte[] byteArray) {

		char[] hexadecimalChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[byteArray.length * 2];// every byte becomes
		                                                 // two Hex characters
		for (int i = 0; i < hexChars.length / 2; i++) {
			hexChars[i * 2] = hexadecimalChars[(byteArray[i] & 0xff) >> 4];
			hexChars[i * 2 + 1] = hexadecimalChars[(byteArray[i] & 0xff) & 0xf];
		}
		return hexChars;
	}
}
