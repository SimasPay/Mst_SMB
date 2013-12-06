/**
 * 
 */
package com.mfino.handlers;

import junit.framework.TestCase;

import org.junit.Test;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.domain.ChannelCode;

/**
 * @author Deva
 * 
 */
public class CheckBalanceHandlerTest extends TestCase
{

    @Override
    public void setUp()
    {
	MfinoServiceProviderDAO mfinoServiceProviderDAO = new MfinoServiceProviderDAO();
	FIXMessageHandler.msp = mfinoServiceProviderDAO.getById(1L);
    }

    @Test
    public void testSuccess()
    {
//		ChannelCode channelCode = getChannelCode("2002");
//	
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		MerchantCheckBalanceHandlerImpl checkBalanceHandler = new MerchantCheckBalanceHandlerImpl(channelCode, "6288116210961", "123456");
//		checkBalanceHandler.setResultType(ResultType.XML);
//		XMLResult xmlResult = (XMLResult) checkBalanceHandler.handle();
//		xmlResult.setWriter(baos);
//		ResponseXMLValidator validator = null;
//		try
//		{
//		    xmlResult.render();
//		    validator = new ResponseXMLValidator(25, null, baos.toString());
//		}
//		catch (Exception e)
//		{
//		    e.printStackTrace();
//		}
//		System.out.println(validator.getResponseXML());
//		assertEquals(true, validator.isValidOutput());
    }

    @Test
    public void testMDNIsNotRegistered()
    {
//		ChannelCode channelCode = getChannelCode("2002");
//	
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		MerchantCheckBalanceHandlerImpl checkBalanceHandler = new MerchantCheckBalanceHandlerImpl(channelCode, "62881156732", "123456");
//		checkBalanceHandler.setResultType(ResultType.XML);
//		XMLResult xmlResult = (XMLResult) checkBalanceHandler.handle();
//		xmlResult.setWriter(baos);
//		ResponseXMLValidator validator = null;
//		try
//		{
//		    xmlResult.render();
//		    validator = new ResponseXMLValidator(11, null, baos.toString());
//		}
//		catch (Exception e)
//		{
//		    e.printStackTrace();
//		}
//		System.out.println(validator.getResponseXML());
//		assertEquals(true, validator.isValidOutput());
    }

    @Test
    public void testMDNIsNotActive()
    {
//		ChannelCode channelCode = getChannelCode("2002");
//	
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		MerchantCheckBalanceHandlerImpl checkBalanceHandler = new MerchantCheckBalanceHandlerImpl(channelCode, "628811127281", "123456");
//		checkBalanceHandler.setResultType(ResultType.XML);
//		XMLResult xmlResult = (XMLResult) checkBalanceHandler.handle();
//		xmlResult.setWriter(baos);
//		ResponseXMLValidator validator = null;
//		try
//		{
//		    xmlResult.render();
//		    validator = new ResponseXMLValidator(7, null, baos.toString());
//		}
//		catch (Exception e)
//		{
//		    e.printStackTrace();
//		}
//		System.out.println(validator.getResponseXML());
//		assertEquals(true, validator.isValidOutput());
    }

    @Test
    public void testWrongPINSpecified()
    {
//		ChannelCode channelCode = getChannelCode("2002");
//	
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		MerchantCheckBalanceHandlerImpl checkBalanceHandler = new MerchantCheckBalanceHandlerImpl(channelCode, "6288116210961", "654321");
//		checkBalanceHandler.setResultType(ResultType.XML);
//		XMLResult xmlResult = (XMLResult) checkBalanceHandler.handle();
//		xmlResult.setWriter(baos);
//		ResponseXMLValidator validator = null;
//		try
//		{
//		    xmlResult.render();
//		    validator = new ResponseXMLValidator(29, null, baos.toString());
//		}
//		catch (Exception e)
//		{
//		    e.printStackTrace();
//		}
//		System.out.println(validator.getResponseXML());
//		assertEquals(true, validator.isValidOutput());
    }

    @Test
    public void testMerchantInitialized()
    {
//		ChannelCode channelCode = getChannelCode("2002");
//	
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		MerchantCheckBalanceHandlerImpl checkBalanceHandler = new MerchantCheckBalanceHandlerImpl(channelCode, "628811586624", "123456");
//		checkBalanceHandler.setResultType(ResultType.XML);
//		XMLResult xmlResult = (XMLResult) checkBalanceHandler.handle();
//		xmlResult.setWriter(baos);
//		ResponseXMLValidator validator = null;
//		try
//		{
//		    xmlResult.render();
//		    validator = new ResponseXMLValidator(7, null, baos.toString());
//		}
//		catch (Exception e)
//		{
//		    e.printStackTrace();
//		}
//		System.out.println(validator.getResponseXML());
//		assertEquals(true, validator.isValidOutput());
    }

    @Test
    public void testPendingRetirement()
    {
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ChannelCode channelCode = getChannelCode("2002");
//	
//		MerchantCheckBalanceHandlerImpl checkBalanceHandler = new MerchantCheckBalanceHandlerImpl(channelCode, "628811214746", "123456");
//		checkBalanceHandler.setResultType(ResultType.XML);
//		XMLResult xmlResult = (XMLResult) checkBalanceHandler.handle();
//		xmlResult.setWriter(baos);
//		ResponseXMLValidator validator = null;
//		try
//		{
//		    xmlResult.render();
//		    validator = new ResponseXMLValidator(25, null, baos.toString());
//		}
//		catch (Exception e)
//		{
//		    e.printStackTrace();
//		}
//		System.out.println(validator.getResponseXML());
//		assertEquals(true, validator.isValidOutput());
    }

    @Override
    public void tearDown()
    {
    }

    private ChannelCode getChannelCode(String channelIdStr)
    {
	ChannelCodeDAO channelCodeDAO = new ChannelCodeDAO();
	return channelCodeDAO.getByChannelCode(channelIdStr);
    }

}
