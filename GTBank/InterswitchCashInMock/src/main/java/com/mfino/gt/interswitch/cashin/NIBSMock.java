package com.mfino.gt.interswitch.cashin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
//99bff9e9-c29e-48b7-8db2-8e8f15ff1218 //99bff9e9-c29e-48b7-8db2-8e8f15ff1218
public class NIBSMock {
	
	
	public static void main(String... args) throws Exception{
		
		URL url = new URL("http://localhost:7777");
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);

		File file = new File("A:\\MFS_V2_6\\GTBank\\InterswitchCashInMock\\src\\main\\java\\com\\mfino\\gt\\interswitch\\cashin\\Test2.xml");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		String str="";
		String requestStr="";
		while((str=br.readLine())!=null){
			requestStr=requestStr+str;
		}
		System.out.println(requestStr);
		
		OutputStream out = conn.getOutputStream();
		out.write(requestStr.getBytes());	

		InputStream is = conn.getInputStream();
		BufferedReader br1 = new BufferedReader(new InputStreamReader(is));
		String responseStr = "";
		while((str=br1.readLine())!=null){
			responseStr=responseStr+str;
		}
		System.out.println(responseStr);
		
	}

}
