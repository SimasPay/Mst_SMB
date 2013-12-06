package com.mfino.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.util.MfinoUtil;

public class MerchantsPIN {

	private static Logger log = LoggerFactory.getLogger(MerchantsPIN.class);
	
	public static void main(String[] args) {
		int lineCount = 0;
		if (args.length < 1) {
			System.out.println("Pass the file name as first argument");
			return;
		}
		try {
			FileReader fread = new FileReader(args[0]);
			File filetoWrite = new File("MerchantDigestedPINs.csv");
			File filewithSQLScript = new File("MerchantDigestedPINsSQLScript.sql");
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
					outputSQL.println("update subscriber_mdn m, subscriber s set m.DigestedPIN = '"+
							calcPIN+"' , "+" m.MerchantDigestedPIN= '" + calcPIN +"' , m.UpdatedBy = 'System' , " +
						   "m.LastUpdateTime=now(), s.UpdatedBy = 'System' , s.LastUpdateTime=now()"+
						   " where mdn='"+mdn+"' and m.subscriberid = s.id ;" );
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
		} catch (Exception exp) {
			log.error(exp.getMessage(), exp);
		}

	}

}
