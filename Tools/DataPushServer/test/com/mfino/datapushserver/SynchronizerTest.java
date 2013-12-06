package com.mfino.datapushserver;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

/**
 * 
 */

/**
 * @author Deva
 *
 */
public class SynchronizerTest {
	
//	@Test
	public void testCreateNewSubscriber() {
		
	}
	
	@Test
	public void testUpdateSubscriber() {
		Synchronizer synchronizer = new Synchronizer();
		String msisdn = "88116210916";
		String firstName = null;
		String lastName = null;
		String email = "temp@temp.com";
		Integer language = null;
		String currency = null;
		String paidFlag = null;
		XMLGregorianCalendar birthDate = null;
                try
                {
                  birthDate  =  DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2000, 7, 4, 0);
                }
                catch(Exception e)
                {
                    
                }
		String idType = null;
		String idNumber = null;
		String gender = null;
		String address = null;
		String city = "Nagpur";
		String birthPlace = null;
		String imsi = null;
		String marketingCatg = null;
		String product = null;
		synchronizer.updateSubscriber(msisdn, firstName, lastName, email, language, currency, paidFlag, birthDate, idType, idNumber, gender, address, city, birthPlace, imsi, marketingCatg, product);
	}
}
