package com.mfino.dbcopytool.dbcopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mfino.dbcopytool.reportdb.domain.TableRow;
import com.mfino.module.DefaultModule;

public class RowFactory {
	
	private static Logger log = LoggerFactory.getLogger("RowFactory");
	private static String packageName = "com.mfino.dbcopytool.reportdb.domain.";
	
	@SuppressWarnings("unchecked")
	public static TableRow getRow(String rowClass) {
		Class<TableRow> c;
		try {
			c = (Class<TableRow>) Class.forName(packageName+rowClass);
			Injector injector = Guice.createInjector(new DefaultModule());
			return  injector.getInstance(c);
		} catch (ClassNotFoundException e) {
			log.error("Invalid rowClass:"+rowClass,e);
		}
		return null;
	}
}
