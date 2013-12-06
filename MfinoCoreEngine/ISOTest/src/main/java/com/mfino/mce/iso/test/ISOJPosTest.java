package com.mfino.mce.iso.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.PostChannel;
import org.jpos.iso.packager.PostPackager;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;

import com.sleepycat.je.log.LogSource;

public class ISOJPosTest 
{
	public static void main(String[] args) throws Exception 
	{
		Log log = LogFactory.getLog(ISOJPosTest.class);
		
		Logger logger = new Logger();
		logger.addListener(new SimpleLogListener(System.out));
		
		ISOChannel channel = new PostChannel("172.29.8.192", 9901, new PostPackager());
		((org.jpos.util.LogSource)channel).setLogger(logger, "test-channel");
		
		channel.connect();
		ISOMsg m = new ISOMsg();
		
		m.setMTI("0200");
		m.set(2, "2020193315");
		m.set(3, "312000");
		m.set(4, "000000000000");
		m.set(7, "1110192045");
		m.set(11,"100000");
		m.set(12,"192045");
		m.set(13,"1110");
		m.set(15,"1110");
		m.set(22,"000");
		m.set(23,"001");
		m.set(25,"00");
		m.set(26,"12");
		m.set(28,"C00000000");
		m.set(30,"C00000000");
		m.set(32,"413103");
		m.set(37,"100000");
		m.set(41,"40570005");
		m.set(42,"ZIB405700100001");
		m.set(43,"Zenith                     eaZymoneyLANG");
		m.set(49,"566");
		m.set(59,"100000");
		m.set(102,"2020193315");
		m.set(123, "610500613134021");
		m.set("127.0","");
		m.set("127.3","ZMobileSrc  ZMobileMeSnk470310000820VisaTG      ");
		m.set("127.20","20111110");
		
		channel.send(m);
		ISOMsg response = channel.receive();
		channel.disconnect();
		
		System.out.println("ISO done "+response.toString());
		
	}
}
