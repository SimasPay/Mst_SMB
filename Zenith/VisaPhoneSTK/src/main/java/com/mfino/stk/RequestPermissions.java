package com.mfino.stk;

import java.util.ArrayList;
import java.util.List;

public class RequestPermissions {

	private String[]	brands;
	private String[]	companies;
	private boolean[]	allows;

	public RequestPermissions(List<String> tuples) {

		brands = new String[tuples.size()];
		companies = new String[tuples.size()];
		allows = new boolean[tuples.size()];

		for (int i = 0; i < tuples.size(); i++) {
			String str = tuples.get(i);
			String[] split = str.split(",");

			companies[i] = split[0];
			if (split[1].startsWith("0"))
				brands[i] = split[1].substring(1);
			else
				brands[i] = split[1];
			allows[i] = Boolean.parseBoolean(split[2]);
		}
	}

	public boolean getPermission(String company, String brand) {

		for (int i = 0; i < brands.length; i++) {
			if (brands[i].equals(brand))
				if (companies[i].equals(company))
					return allows[i];
		}
		return false;

	}
	
	public static void main(String... args){
		
		
		List<String> list= new ArrayList<String>();
		list.add("");
	}
}
