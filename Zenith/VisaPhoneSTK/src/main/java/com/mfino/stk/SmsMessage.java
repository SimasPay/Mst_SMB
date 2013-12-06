package com.mfino.stk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class SmsMessage {

	public static void main(String... args) throws Exception {

		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\karthik\\Desktop\\result"));
		String str = "";
		String path = "C:\\Users\\karthik\\Desktop\\input";

		Map<String, BufferedWriter> fileMap = new HashMap<String, BufferedWriter>();

		try {
			while ((str = br.readLine()) != null) {

				String request = str.substring(0, 4);
				String file = path + "\\" + request + ".txt";
				if (!fileMap.containsKey(request)) {
					BufferedWriter bw = null;
					try {
						bw = new BufferedWriter(new FileWriter(file));
					}
					catch (Exception ex) {
						file = file.substring(0, file.length() - 5) + ".txt";
						bw = new BufferedWriter(new FileWriter(file));
					}
					fileMap.put(request, bw);
				}

				BufferedWriter bw = fileMap.get(request);
				// System.out.print(Pattern.quote("*"));
//				request = str.replace('*', ' ');
//				String[] spl = request.split(" ");
//				request = "";
//				int i = 0;
//				for (String s : spl) {
//					if (s != null && !s.equals("")) {
//						if (i == 0)
//							request = request + " " + s;
//						else
//							request = request + " " + s.charAt(0);
//					}
//					i++;
//				}

				bw.write(str);
				bw.newLine();
			}

			br.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			for (Entry<String, BufferedWriter> e : fileMap.entrySet())
				e.getValue().close();
		}

		System.out.print(Pattern.quote("*"));
	}

}
