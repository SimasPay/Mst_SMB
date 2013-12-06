package com.mfino.webapi.json;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

	 
	/**
	 * Converts xml to json.
	 */
	public class JsonResponseWrapper extends HttpServletResponseWrapper {
		private static Logger	log = LoggerFactory.getLogger(JsonResponseWrapper.class);
	    private ByteArrayServletOutputStream _servletOutputStream;
	    private PrintWriter _printWriter;
 
	    public JsonResponseWrapper(HttpServletResponse response) {
	        super(response);
	        response.setContentType("text/javascript");
	    }
	    
	 
	    @Override
	    public ServletOutputStream getOutputStream() throws IOException {
        if (_printWriter != null) {
            throw new IllegalStateException("Servlet already accessed print writer.");
        }
 
	        if (_servletOutputStream == null) {
	            _servletOutputStream = new ByteArrayServletOutputStream();
        }
	        return _servletOutputStream;
	    }
	 
	    @Override
	    public PrintWriter getWriter() throws IOException {
	        if (_printWriter == null && _servletOutputStream != null) {
	            throw new IllegalStateException("Servlet already accessed output stream.");
	        }
	 
	        if (_printWriter == null) {
	            _servletOutputStream = new ByteArrayServletOutputStream();
	            Writer writer = new OutputStreamWriter(_servletOutputStream, getResponse().getCharacterEncoding());
	            _printWriter = new PrintWriter(writer);
	        }
	        return _printWriter;
	    }
 
	    public void convertToJson() throws IOException {
	    	 if (_servletOutputStream != null) {
	            if (_printWriter != null) {
	                _printWriter.flush();
	            }
	            try {
	            	String xml = new String( _servletOutputStream.getBytes(), getResponse().getCharacterEncoding());
	            	String xmlWithCode = getXmlWithCode(xml);
	            	XMLSerializer xmlSerialize = new XMLSerializer();
	                JSON json = xmlSerialize.read(xmlWithCode); 
	                PrintWriter writer = new PrintWriter(getResponse().getWriter()) ;
	                
	                writer.print("callback({data:");
	                writer.print(json);
	                writer.print("})");
	                writer.flush();
	                
	            } catch (Exception e) {
	            	log.error("Exception"+e);
	            }
        }
	    }
 
	    private String getXmlWithCode(String xml) {
	    	int codeIndex=xml.indexOf("code=")+6;
			String code = xml.substring(codeIndex,xml.indexOf("\"", codeIndex));
			System.out.println(code);
			String [] xmlSplit = xml.split("</message>");
			code = xmlSplit[0]+"</message><code>"+code+"</code>"+xmlSplit[1];
			return code;
		}

		@Override
		public void resetBuffer() {
        _servletOutputStream = null;
	        _printWriter = null;
	        super.resetBuffer();
	    }
	 
	    @Override
	    public void reset() {
	        _servletOutputStream = null;
	        _printWriter = null;
	        super.reset();
	    }
	}
