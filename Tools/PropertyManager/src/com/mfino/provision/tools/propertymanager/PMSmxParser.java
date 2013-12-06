package com.mfino.provision.tools.propertymanager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PMSmxParser extends DefaultHandler
{

	String xmlFilePath;
	String actualFileLocation;
	String actualFileLocation2;
	boolean foundproperty = false;
	boolean foundvalue = false;
	boolean tomcatvalue = false;
	int fileLocationFound = 0;
	int tempinteger = 0;
	ArrayList<String> fileLocationActual = new ArrayList<String>();
	ArrayList<String> propertynames = new ArrayList<String>();
	String tempVal;
	ByteArrayOutputStream outputStream;

	public PMSmxParser(String xmlFilePath)
	{
		this.xmlFilePath = xmlFilePath;
	}

	public PMSmxParser(String xmlFilePath, ByteArrayOutputStream baos)
	{
		this.xmlFilePath = xmlFilePath;
		this.outputStream = baos;
	}

	public void parseDocument()
	{

		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			sp.parse(this.xmlFilePath, this);
		} catch (SAXException se) {
			// se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			// pce.printStackTrace();
		} catch (IOException ie) {
			// ie.printStackTrace();
		} catch (NullPointerException e) {
		}

	}

	// Event Handlers
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		// reset
		this.tempVal = "";
		if (qName.equalsIgnoreCase("property")) {
			this.foundproperty = true;
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			// this.propertynames.add(attributes.getValue("value"));
			// System.out.println(value);
			if (value != null && value.contains("$") == true) {
				this.foundvalue = true;
				this.propertynames.add(value);
			}

			if (name.equalsIgnoreCase("locations")) {
				this.foundproperty = false;
				this.fileLocationFound = 1;
				this.tempinteger = 1;
			}

		} else if (qName.equalsIgnoreCase("value") == true && this.foundproperty == true && this.foundvalue == false) {

			// System.out.println(value);
			this.tomcatvalue = true;
			// System.out.println("u are done !!");

		} else if (this.fileLocationFound == 1 && qName.equalsIgnoreCase("list") && this.tempinteger == 1) {
			this.fileLocationFound += 1;
			this.tempinteger += 1;
			// System.out.println(this.fileLocationFound);
		} else if (this.tempinteger == 2 && qName.equalsIgnoreCase("value")) {
			this.fileLocationFound += 1;
			this.tempinteger += 1;
			// System.out.println(this.tempinteger);
			// System.out.println(this.fileLocationFound);

		} else if ((this.fileLocationFound == 1000 && qName.equalsIgnoreCase("value"))) {
			this.tempinteger += 1;
			// System.out.println(this.tempinteger);
			// System.out.println(this.fileLocationFound);
			this.fileLocationFound = 8;
			// System.out.println("hllll");
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if (this.fileLocationFound == 3) {
			this.actualFileLocation = new String(ch, start, length);
			// System.out.println(this.actualFileLocation+"hello");
			this.fileLocationFound = 1000;
			this.fileLocationActual.add(this.actualFileLocation);
			// System.out.println("actualFile :"+this.actualFileLocation);
		}
		if (this.fileLocationFound == 8 && this.tempinteger == 4) {
			this.actualFileLocation2 = new String(ch, start, length);
			this.tempinteger = 0;
			this.fileLocationFound = 0;
			// System.out.println("hello");
			this.fileLocationActual.add(this.actualFileLocation2);
			// System.out.println("actualFile :"+this.actualFileLocation);
		}
		if (this.tomcatvalue == true) {
			String value = new String(ch, start, length);

			if (value.contains("$")) {
				// System.out.println(value);
				this.propertynames.add(value);
			}
			this.tomcatvalue = false;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{

	}
}
