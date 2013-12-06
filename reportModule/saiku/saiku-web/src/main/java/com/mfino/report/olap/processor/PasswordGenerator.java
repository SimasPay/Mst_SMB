package com.mfino.report.olap.processor;

public class PasswordGenerator {
	
	final int PASSWORD_LENGTH = 16; 
	public String generate()  
	  { 
	    StringBuffer sb = new StringBuffer();  
	    for (int x = 0; x < PASSWORD_LENGTH; x++)  
	    {  
	      sb.append((char)((int)(Math.random()*26)+97));  
	    }  
	    return sb.toString();  
	  }  

}
