package com.dimo.fuse.reports.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Amar
 *
 */
public class FileReaderUtil {
	
	private static Logger log = LoggerFactory
			.getLogger(FileReaderUtil.class);

	public static String readFileContAsStr(String filePath){
		BufferedReader br = null;
		StringBuilder fileContent = new StringBuilder();
		try{
			br = new BufferedReader(new FileReader(filePath));
			String curLine;
			while((curLine = br.readLine()) != null){
				fileContent.append(curLine);
			}
			return fileContent.toString();
		}catch(Exception e){
			log.error("An Exception Occured while reading the file " + filePath, e.getMessage());
			return null;
		}finally{
			try{
				if(br != null) {
					br.close();
				}
			}catch (Exception e) {
				log.error("An Exception Occured", e);
			}
		}
	}

	public static JSONObject readFileContAsJsonObj(String filePath){
		try{
			String fileContent = readFileContAsStr(filePath);
			JSONObject jsonObj = new JSONObject(fileContent.toString());
			return jsonObj;
		}catch(Exception e){
			log.error("File " + filePath + " is not a valid json file", e);
			return null;
		}

	}
	
	public static byte[] readFileAsByteArray(String filePath) 
	{
		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;
		try{
			baos = new ByteArrayOutputStream();
			fis = new FileInputStream(filePath);
			byte [] b = new byte[1];
			while ( fis.read(b) != -1 ) {
				baos.write(b);
			}
			byte[] byteArray = baos.toByteArray();
			return byteArray;
		}catch(Exception e){
			log.error("An Exception Occured", e);
			return null;
		}finally{
			try{
				if(baos != null){
					baos.close();
				}
				if(fis != null){
					fis.close();
				}
			}catch(Exception e){
				log.error("An Exception Occured", e);
			}
		}
		
	}

}
