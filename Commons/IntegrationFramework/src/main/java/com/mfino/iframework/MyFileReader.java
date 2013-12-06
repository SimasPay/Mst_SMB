package com.mfino.iframework;

import java.io.BufferedReader;
import java.io.FileReader;

public class MyFileReader {

	private String	filePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String read() {

		BufferedReader br;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(filePath));
			String line = "";
			while ((line = br.readLine()) != null)
				sb = sb.append(line);
			br.close();
		}
		catch (Exception ex) {
		}

		return sb.toString();

	}

}
