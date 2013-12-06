package com.mfino.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;

import com.mfino.service.impl.SubscriberServiceExtended;
import com.mfino.util.MfinoUtil;

public class SubscriberActivation {

	public static void main(String[] args) {
		int lineCount = 0;
		if (args.length < 1) {
			System.out.println("Pass the file name as first argument");
			return;
		}
		try {
			FileReader fread = new FileReader(args[0]);
			File filetoWrite = new File("SubActivation.csv");
			File filewithSQLScript = new File("SubActivationSQLScript.sql");
			FileWriter fwrite = new FileWriter(filetoWrite);
			FileWriter fwriteSQL = new FileWriter(filewithSQLScript);
			BufferedReader buread = new BufferedReader(fread);
			PrintWriter output = new PrintWriter(fwrite);
			PrintWriter outputSQL = new PrintWriter(fwriteSQL);
			
			String strLine;
			System.out.print("Filename" + args[0]);
			while ((strLine = buread.readLine()) != null) {
				if (strLine.trim().length() == 0) {
					continue; // invalid Lines are Skipped
				}
				lineCount++;
				System.out.println("processing line" + lineCount);
				String s[] = strLine.split(",");
				if (s.length == 2) {
					String mdn = s[0].trim();
					String pin = s[1].trim();
					if (pin.length() < 6) { // minimum pin length should be 6
						System.out.println("Skipped line because of invalid pin length"	+ lineCount);
						continue;
					} 
					String calcPIN = MfinoUtil.calculateDigestPin(mdn, pin);
					output.println(String.format("%s,%s,%s", mdn, pin,calcPIN));
					outputSQL.println("update subscriber_mdn m, subscriber s, pocket p set p.status =1, m.status =1, " +
							"m.DigestedPIN = '"+calcPIN+"' , "+" m.UpdatedBy = 'System' , " +
							"m.LastUpdateTime=now(), m.StatusTime=now(), s.status=1, s.UpdatedBy = 'System' , s.LastUpdateTime=now(), s.StatusTime=now(), " +
							"s.activationtime = now(),p.activationtime = now(),m.activationtime = now(), p.UpdatedBy = 'System' , p.LastUpdateTime=now(), p.StatusTime=now()"+" where m.mdn='"+mdn+"' and " +
							"m.subscriberid = s.id and p.mdnid=m.id and p.isdefault = 1 and (p.pockettemplateid=1 or p.pockettemplateid=2);" );
				}
				else{
					System.out.println("Skipped line because of invalid number of parametrs"	+ s.length);
				}
			}
			fread.close();
			fwrite.close();
			fwriteSQL.close();
			System.out.println("File is located at:"+ filetoWrite.getAbsolutePath());
			System.out.println("File is located at:"+ filewithSQLScript.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("Exception" + e);
		}

	}

}
