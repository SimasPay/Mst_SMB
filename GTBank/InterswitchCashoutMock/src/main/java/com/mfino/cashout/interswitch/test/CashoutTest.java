package com.mfino.cashout.interswitch.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;


public class CashoutTest {

	public static void main(String... args) throws Exception{

		
		File file = new File("A:\\MFS_V2_6\\GTBank\\InterswitchCashoutMock\\test\\request");
		
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		String str="",str1="";
		while((str=br.readLine())!=null){
			str1 = str1+str;
		}

//		CashoutCommunicator comm = new CashoutCommunicator();
//		comm.setWebServiceEndpointBean("cxf:http://localhost:9000/Iso8583PostXml?dataFormat=PAYLOAD");
//		
		
	}
	
}