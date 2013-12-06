package com.mfino.sterling;

import java.util.Iterator;
import com.mfino.sterling.bank.util.SterlingBankWebServiceReponseParser;
import com.mfino.sterling.bank.util.SterlingBankWebServiceResponse;

/**
 * 
 * @author Amar
 *
 */
public class SterlingBankWebServiceReponseParserTest {

	public static void main(String[] args) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
				"<IBSResponse>" + 
				"<ReferenceID>1</ReferenceID>" +
				"<RequestType>101</RequestType>" + 
				"<ResponseCode>00</ResponseCode>" +
				"<ResponseText>Approve</ResponseText>" +
				"</IBSResponse>" ;

		String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<IBSResponse>" + 
				"<ReferenceID>1</ReferenceID>" +
				"<RequestType>211</RequestType>" +
				"<Account>0041996149</Account>" +
				"<Records>" +
				"<Rec>" +
				"<Date>2012-11-10</Date>" +
				"<DC>C</DC>" +
				"<Amount>2000.00</Amount>" +
				"</Rec>" +
				"<Rec>" +
				"<Date>2012-11-12</Date>" +
				"<DC>D</DC>" +
				"<Amount>21000.00</Amount>" +
				"</Rec>" +
				"</Records>" +
				"<Available>1000.00</Available>" +
				"<Book>1200.00</Book>" +
				"</IBSResponse>" ;
		
		String xml3 = "<ns1:IBSBridgeResponse xmlns:ns1=\"http://tempuri.org/\">" +
				"<ns1:IBSBridgeResult>" +
				//"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
				"<IBSResponse>" + 
				"<ReferenceID>1</ReferenceID>" +
				"<RequestType>101</RequestType>" + 
				"<ResponseCode>00</ResponseCode>" +
				"<ResponseText>Approve</ResponseText>" +
				"</IBSResponse>" +
				"</ns1:IBSBridgeResult>" +
				"</ns1:IBSBridgeResponse>";


		SterlingBankWebServiceResponse sbwsr = SterlingBankWebServiceReponseParser.getSterlingBankWebServiceResponse(xml3);
		System.out.println("ReferenceID:" + sbwsr.getReferenceID());
		System.out.println("RequestType:" + sbwsr.getRequestType());
		System.out.println("ResponseCode:" + sbwsr.getResponseCode());
		System.out.println("ResponseText:" + sbwsr.getResponseText());
		System.out.println("Account:" + sbwsr.getAccount());
		System.out.println("Available:" + sbwsr.getAvailableBalance());
		System.out.println("Book:" + sbwsr.getBook());

		if(sbwsr.getRecords() != null)
		{
			System.out.println("No of records:" + sbwsr.getRecords().size());
			Iterator<SterlingBankWebServiceResponse.Record> it = sbwsr.getRecords().iterator();
			while(it.hasNext())
			{
				SterlingBankWebServiceResponse.Record record = it.next();
				System.out.println("Record Details: date-"+ record.getDate() + ", Amount-" + record.getAmount() + ", isCredit-" + record.getIsCredit());
			}
		}
	}

}
