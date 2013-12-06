package com.mfino.mce.iso.jpos.camel.util;

import java.io.IOException;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.PostChannel;
import org.jpos.q2.iso.QMUX;
import org.jpos.util.NameRegistrar.NotFoundException;

import com.mfino.hibernate.Timestamp;
import com.mfino.mce.iso.jpos.util.DateTimeFormatter;
import com.mfino.util.DateTimeUtil;

public class ISOUtil 
{
    public static ISOMsg createSignOn() throws ISOException
    {
        Timestamp ts = DateTimeUtil.getLocalTime();
        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setMTI("0800");
        isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); //7
        //stan is static, this might cause one in 1 transaction with the same stan fail
        // probability of this happenning is very low so we can live with this for now
        isoMsg.set(11,"000001");//11
//        isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); //12
//        isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); //13
        isoMsg.set(33,"881");
        isoMsg.set(70,"001"); //70
        return isoMsg;        
    }
    
    public static ISOMsg createEcho() throws ISOException
    {
        ISOMsg isoMsg = new ISOMsg();
        Timestamp ts = DateTimeUtil.getLocalTime();
        isoMsg.setMTI("0800");
        isoMsg.set(7, DateTimeFormatter.getMMDDHHMMSS(ts)); //7
        //stan is static, this might cause one in 1L  transaction with the same stan fail
        // probability of this happening is very low so we can live with this for now
        isoMsg.set(11,"999998");//11
//        isoMsg.set(12, DateTimeFormatter.getHHMMSS(ts)); //12
//        isoMsg.set(13, DateTimeFormatter.getMMDD(ts)); //13
        isoMsg.set(33,"881");
        isoMsg.set(70,"301"); //70
        return isoMsg;
    }
	
	public static ISOMsg createSignOff()
	{
		ISOMsg signOff = new ISOMsg();
		return signOff;
	}
	
	public static boolean sendMessage(ISOMsg isoMsg) throws ISOException 
	{
		try
		{
			ISOChannel ch = BaseChannel.getChannel("mfino");
			if(ch.isConnected())
			{
			   ((PostChannel)ch).send(isoMsg);
				return true;
			}
			return false;
			
		}
		catch(IOException e)
		{
			throw new ISOException("error sending ISO Message",e);
		}
		catch(ISOException e)
		{
			throw new ISOException("error sending ISO Message",e);
		} 
		catch (NotFoundException e) 
		{
			throw new ISOException("error sending ISO Message",e);
		}
	}
	
	public static ISOMsg sendAndReceive(ISOMsg isoMsg,long timeout, String muxName) throws ISOException, NotFoundException 
	{
		// mux is not connected we will get an error, which will be handled
		QMUX mux = (QMUX) QMUX.getMUX(muxName);
		ISOMsg replyMsg = mux.request(isoMsg,timeout);
		return replyMsg;
	}
	
	public static ISOMsg receiveMessage() throws ISOException 
	{
		try
		{ 
			ISOChannel ch = BaseChannel.getChannel("mfino");
			if(ch.isConnected())
			{
				ISOMsg isoMsg = ((PostChannel)ch).receive();
				return isoMsg;
			}
			return null;
		}
		catch(IOException e)
		{
			throw new ISOException("error sending ISO Message",e);
		}
		catch(ISOException e)
		{
			throw new ISOException("error sending ISO Message",e);
		} 
		catch (NotFoundException e) 
		{
			throw new ISOException("error sending ISO Message",e);
		} 
	}
	
	public static void  sleep(long sleepTime)
	{

		try 
		{
			Thread.sleep(sleepTime);
		} 
		catch (InterruptedException e) 
		{
			// somebody doesn't want us to process anymore, 
			// so lets stop sleeping and return
			// if we dont return then we might end up becoming a deamon thread
			return;
		}
	}
}
