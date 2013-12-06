package com.mfino.webapi.json;

import java.io.ByteArrayOutputStream;
	import java.io.IOException;
	 
	import javax.servlet.ServletOutputStream;
	 
	public class ByteArrayServletOutputStream extends ServletOutputStream {
 
	    private ByteArrayOutputStream _outputStream;
	 
	    public ByteArrayServletOutputStream() {
	        _outputStream = new ByteArrayOutputStream();
	    }
	 
	    @Override
	    public void write(int b) throws IOException {
	        _outputStream.write(b);
	    }
	 
	    @Override
	    public void write(byte[] b) throws IOException {
	        _outputStream.write(b);
	    }
	 
	    @Override
	    public void write(byte[] b, int off, int len) throws IOException {
	        _outputStream.write(b, off, len);
	    }
	 
	    @Override
	    public void close() throws IOException {
	        _outputStream.close();
	    }
	 
	    public byte[] getBytes() {
        return _outputStream.toByteArray();
	    }
	}
