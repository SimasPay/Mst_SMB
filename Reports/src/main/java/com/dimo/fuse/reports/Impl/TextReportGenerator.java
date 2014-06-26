package com.dimo.fuse.reports.Impl;


/**
 * 
 * @author Amar
 * 
 */
public class TextReportGenerator extends CSVReportGenerator {

	private final String DEFAULT_TEXT_DELIMITER = "\t";

	protected String getCsvDelimiter() {
		return DEFAULT_TEXT_DELIMITER;
	}

}
