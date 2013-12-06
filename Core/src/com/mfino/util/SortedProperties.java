package com.mfino.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class SortedProperties extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Overrides, called by the store method.
	 */
        @Override
        @SuppressWarnings("unchecked")
	public synchronized Enumeration<Object> keys() {
		Enumeration<Object> keysEnum = super.keys();
		Vector keyList = new Vector();
		while(keysEnum.hasMoreElements()){
			keyList.add(keysEnum.nextElement());
		}
		Collections.sort(keyList);
                @SuppressWarnings("unchecked")
                Enumeration results = keyList.elements();
		return results;
	}
}
