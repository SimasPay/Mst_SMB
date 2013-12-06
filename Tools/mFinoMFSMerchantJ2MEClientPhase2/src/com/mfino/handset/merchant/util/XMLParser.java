package com.mfino.handset.merchant.util;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.kxml.Attribute;
import org.kxml.Xml;
import org.kxml.parser.ParseEvent;
import org.kxml.parser.XmlParser;

public class XMLParser {
	public ResponseData parse(String xml) throws Exception{
		ResponseData result = new ResponseData();
		ByteArrayInputStream inStream = new ByteArrayInputStream(xml.getBytes());
		InputStreamReader reader = new InputStreamReader(inStream);
		XmlParser parser = new XmlParser(reader);
		traverse(parser, result);
		return result;
	}
	
	public void traverse( XmlParser parser, ResponseData result) throws Exception
	{
		boolean leave = false;
		do {
			ParseEvent event = parser.read ();
			ParseEvent pe;
			switch ( event.getType() ) {
				 case Xml.START_TAG:
					  if ("message".equals(event.getName())) {
							pe = parser.read();
							result.setMsg(pe.getText());
							result.setMsgCode(event.getAttribute("code").getValue());
					  }
					  if ("transactionTime".equals(event.getName())) {
							pe = parser.read();
							result.setTransactionTime(pe.getText());
					  }
					  if ("refID".equals(event.getName())) {
							pe = parser.read();
							result.setRefId(pe.getText());
					  }
					  if ("transferID".equals(event.getName())) {
							pe = parser.read();
							result.setTransferId(pe.getText());
					  }
					  if ("parentTxnID".equals(event.getName())) {
							pe = parser.read();
							result.setParentTxnId(pe.getText());
					  }
					  if("input".equals(event.getName())){
						Attribute name = event.getAttribute("name");
						Attribute value = event.getAttribute("value");
						
						if((null != name) && !("".equals(name.getValue()) && "ParentTransactionID".equals(name.getValue()))){
							if(value != null){ 
								result.setParentTxnId(value.getValue());
							}
						}
						
						if((null != name) && !("".equals(name.getValue()) && "TransferID".equals(name.getValue()))){
							if(value != null){ 
								result.setTransferId(value.getValue());
							}
						}						
					  }
					  if ("amount".equals(event.getName())) {
							pe = parser.read();
							result.setAmount(pe.getText());
					  }
					  if("billDetails".equals(event.getName())){
						  pe = parser.read();
						  result.setBillDetails(pe.getText());
					  }
					  
					  traverse( parser, result) ; // recursion call for each <tag></tag>
					  break;
				  
				 case Xml.END_TAG:
					  leave = true;
					  break;
				  
				 case Xml.END_DOCUMENT:
					  leave = true;
					  break;
					  
				 case Xml.TEXT:
					  break;
				  
				 case Xml.WHITESPACE:
					  break;
				  
				  default:
			}
		} while( !leave );
	  }
	
//	public static void main(String[] args) throws Exception {
//		XMLParser p = new XMLParser();
//		String str = new String("<?xml version='1.0'?><response>" +
//				"<message code='72'>You requested to transfer IDR 120000 to 629876543210 -- XYZ.</message>" +
//				"<transactionTime>28/01/2011 12:22</transactionTime><transferID>1000123</transferID>" +
//				"<parentTxnID>78911231</parentTxnID></response>");
//		ResponseData res = p.parse(str);
//		System.out.println("*********** = " + res.toString());
//	}
}
