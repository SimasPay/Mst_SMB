/**
 * NibssFasterPaySkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */
package com.zenithbank.nfp;

/**
 * NibssFasterPaySkeleton java skeleton for the axisService
 */
public class NibssFasterPaySkeleton implements NibssFasterPaySkeletonInterface {

	/**
	 * Auto generated method signature
	 * 
	 * @param nameenquirybulkitem0
	 * @return nameenquirybulkitemResponse1
	 */

	public com.zenithbank.nfp.NameenquirybulkitemResponse nameenquirybulkitem(
			com.zenithbank.nfp.Nameenquirybulkitem nameenquirybulkitem0) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#nameenquirybulkitem");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param fundtransfersingleitem2_dc2
	 * @return fundtransfersingleitem2_dcResponse3
	 */

	public com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse fundtransfersingleitem2_dc(
			com.zenithbank.nfp.Fundtransfersingleitem2_dc fundtransfersingleitem2_dc2) {
		
		Fundtransfersingleitem2_dcResponse response = new Fundtransfersingleitem2_dcResponse();
		
		String input = fundtransfersingleitem2_dc2.getIn0();
		System.out.println("NibssFasterPaySkeleton :: fundtransfersingleitem2_dc() "+input);
		
		String strResponse 	=   "<?xml version='1.0' encoding='UTF-8' ?>" +
								"<FTSingleCreditResponse>" +
								"<SessionID>057011120123082347000000196249</SessionID>" +
								"<DestinationBankCode>011</DestinationBankCode>" +
								"<ChannelCode>3</ChannelCode>" +
								"<AccountName> JOHN SMITH</AccountName>" +
								"<AccountNumber>0013538419</AccountNumber>" +
								"<OriginatorName>1020105333-0-0-KENNETH COLE</OriginatorName>" +
								"<Narration> EazyMoney NIP Tfr</Narration>" +
								"<PaymentReference>999</PaymentReference>" +
								"<Amount>25000.0</Amount>" +
								"<ResponseCode>00</ResponseCode>" +
								"</FTSingleCreditResponse>";
		
		if(input.contains("987654321")){
			strResponse = "<?xml version='1.0' encoding='UTF-8' ?>" +
					"<FTSingleCreditResponse>" +
					"<SessionID>057011120123082347000000196249</SessionID>" +
					"<DestinationBankCode>011</DestinationBankCode>" +
					"<ChannelCode>3</ChannelCode>" +
					"<AccountName> JOHN SMITH</AccountName>" +
					"<AccountNumber>0013538419</AccountNumber>" +
					"<OriginatorName>1020105333-0-0-KENNETH COLE</OriginatorName>" +
					"<Narration> EazyMoney NIP Tfr</Narration>" +
					"<PaymentReference>999</PaymentReference>" +
					"<Amount>25000.0</Amount>" +
					"<ResponseCode>03</ResponseCode>" +
					"</FTSingleCreditResponse>";
		}
		
		if(input.contains("9999999999")){
			strResponse = "<?xml version='1.0' encoding='UTF-8' ?>" +
					"<FTSingleCreditResponse>" +
					"<SessionID>057011120123082347000000196249</SessionID>" +
					"<DestinationBankCode>011</DestinationBankCode>" +
					"<ChannelCode>3</ChannelCode>" +
					"<AccountName> JOHN SMITH</AccountName>" +
					"<AccountNumber>0013538419</AccountNumber>" +
					"<OriginatorName>1020105333-0-0-KENNETH COLE</OriginatorName>" +
					"<Narration> EazyMoney NIP Tfr</Narration>" +
					"<PaymentReference>999</PaymentReference>" +
					"<Amount>25000.0</Amount>" +
					"<ResponseCode>68</ResponseCode>" +
					"</FTSingleCreditResponse>";
		}
		
		response.setOut(strResponse);
		
		return response;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param fundtransfernotification_dc4
	 * @return fundtransfernotification_dcResponse5
	 */

	public com.zenithbank.nfp.Fundtransfernotification_dcResponse fundtransfernotification_dc(
			com.zenithbank.nfp.Fundtransfernotification_dc fundtransfernotification_dc4) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#fundtransfernotification_dc");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param fundtransferbulkitem6
	 * @return fundtransferbulkitemResponse7
	 */

	public com.zenithbank.nfp.FundtransferbulkitemResponse fundtransferbulkitem(
			com.zenithbank.nfp.Fundtransferbulkitem fundtransferbulkitem6) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#fundtransferbulkitem");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param fundtransferbulkitem_dd8
	 * @return fundtransferbulkitem_ddResponse9
	 */

	public com.zenithbank.nfp.Fundtransferbulkitem_ddResponse fundtransferbulkitem_dd(
			com.zenithbank.nfp.Fundtransferbulkitem_dd fundtransferbulkitem_dd8) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#fundtransferbulkitem_dd");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param fundtransfersingleitem_dc10
	 * @return fundtransfersingleitem_dcResponse11
	 */

	public com.zenithbank.nfp.Fundtransfersingleitem_dcResponse fundtransfersingleitem_dc(
			com.zenithbank.nfp.Fundtransfersingleitem_dc fundtransfersingleitem_dc10) {
		
		Fundtransfersingleitem_dcResponse response = new Fundtransfersingleitem_dcResponse();
		
		String input = fundtransfersingleitem_dc10.getIn0();
		System.out.println("NibssFasterPaySkeleton :: fundtransfersingleitem_dc() "+input);
		
		String strResponse 	=   "<?xml version='1.0' encoding='UTF-8' ?>" +
								"<FTSingleCreditResponse>" +
								"<SessionID>057011120123082347000000196249</SessionID>" +
								"<DestinationBankCode>011</DestinationBankCode>" +
								"<ChannelCode>3</ChannelCode>" +
								"<AccountName> JOHN SMITH</AccountName>" +
								"<AccountNumber>0013538419</AccountNumber>" +
								"<OriginatorName>1020105333-0-0-KENNETH COLE</OriginatorName>" +
								"<Narration> EazyMoney NIP Tfr</Narration>" +
								"<PaymentReference>999</PaymentReference>" +
								"<Amount>25000.0</Amount>" +
								"<ResponseCode>00</ResponseCode>" +
								"</FTSingleCreditResponse>";
		
		if(input.contains("987654321")){
			strResponse = "<?xml version='1.0' encoding='UTF-8' ?>" +
					"<FTSingleCreditResponse>" +
					"<SessionID>057011120123082347000000196249</SessionID>" +
					"<DestinationBankCode>011</DestinationBankCode>" +
					"<ChannelCode>3</ChannelCode>" +
					"<AccountName> JOHN SMITH</AccountName>" +
					"<AccountNumber>0013538419</AccountNumber>" +
					"<OriginatorName>1020105333-0-0-KENNETH COLE</OriginatorName>" +
					"<Narration> EazyMoney NIP Tfr</Narration>" +
					"<PaymentReference>999</PaymentReference>" +
					"<Amount>25000.0</Amount>" +
					"<ResponseCode>03</ResponseCode>" +
					"</FTSingleCreditResponse>";
		}
		
		response.setOut(strResponse);
		
		return response;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param balanceenquiry12
	 * @return balanceenquiryResponse13
	 */

	public com.zenithbank.nfp.BalanceenquiryResponse balanceenquiry(
			com.zenithbank.nfp.Balanceenquiry balanceenquiry12) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#balanceenquiry");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param fundtransfernotification_dd14
	 * @return fundtransfernotification_ddResponse15
	 */

	public com.zenithbank.nfp.Fundtransfernotification_ddResponse fundtransfernotification_dd(
			com.zenithbank.nfp.Fundtransfernotification_dd fundtransfernotification_dd14) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#fundtransfernotification_dd");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param txnstatusquerybulkitem16
	 * @return txnstatusquerybulkitemResponse17
	 */

	public com.zenithbank.nfp.TxnstatusquerybulkitemResponse txnstatusquerybulkitem(
			com.zenithbank.nfp.Txnstatusquerybulkitem txnstatusquerybulkitem16) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#txnstatusquerybulkitem");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param txnstatusquerysingleitem18
	 * @return txnstatusquerysingleitemResponse19
	 */

	public com.zenithbank.nfp.TxnstatusquerysingleitemResponse txnstatusquerysingleitem(
			com.zenithbank.nfp.Txnstatusquerysingleitem txnstatusquerysingleitem18) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#txnstatusquerysingleitem");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param fundtransferbulkitem_dc20
	 * @return fundtransferbulkitem_dcResponse21
	 */

	public com.zenithbank.nfp.Fundtransferbulkitem_dcResponse fundtransferbulkitem_dc(
			com.zenithbank.nfp.Fundtransferbulkitem_dc fundtransferbulkitem_dc20) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#fundtransferbulkitem_dc");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param getTransactionStatus22
	 * @return getTransactionStatusResponse23
	 */

	public com.zenithbank.nfp.GetTransactionStatusResponse getTransactionStatus(
			com.zenithbank.nfp.GetTransactionStatus getTransactionStatus22) {

		com.zenithbank.nfp.GetTransactionStatusResponse response = new com.zenithbank.nfp.GetTransactionStatusResponse();
		
		String inXml = getTransactionStatus22.getIn0();
		System.out.println("NibssFasterPaySkeleton :: getTransactionStatus() inXml="+inXml);
		
		String responseXml = "<TSQuerySingleResponse>" +
						"<DestinationBankCode>044</DestinationBankCode>" +
						"<ChannelCode>3</ChannelCode>" +
						"<PaymentReference>999</PaymentReference>" +
						"<SessionID>0XX0YY100913103301000000000001</SessionID>" +
						"<ResponseCode>00</ResponseCode>" +
						"</TSQuerySingleResponse>";
		
		if(inXml.contains("023")){
			responseXml = "<TSQuerySingleResponse>" +
					"<DestinationBankCode>044</DestinationBankCode>" +
					"<ChannelCode>3</ChannelCode>" +
					"<PaymentReference>999</PaymentReference>" +
					"<SessionID>0XX0YY100913103301000000000001</SessionID>" +
					"<ResponseCode>06</ResponseCode>" +
					"</TSQuerySingleResponse>";
		}
		
		response.setOut(responseXml);
		
		System.out.println("NibssFasterPaySkeleton :: getTransactionStatus() responseXml="+responseXml);
		
		return response;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param nameenquirysingleitem24
	 * @return nameenquirysingleitemResponse25
	 */

	public com.zenithbank.nfp.NameenquirysingleitemResponse nameenquirysingleitem(
			com.zenithbank.nfp.Nameenquirysingleitem nameenquirysingleitem24) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#nameenquirysingleitem");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param nameenquirynotification26
	 * @return nameenquirynotificationResponse27
	 */

	public com.zenithbank.nfp.NameenquirynotificationResponse nameenquirynotification(
			com.zenithbank.nfp.Nameenquirynotification nameenquirynotification26) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#nameenquirynotification");
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param fundtransfersingleitem_dd28
	 * @return fundtransfersingleitem_ddResponse29
	 */

	public com.zenithbank.nfp.Fundtransfersingleitem_ddResponse fundtransfersingleitem_dd(
			com.zenithbank.nfp.Fundtransfersingleitem_dd fundtransfersingleitem_dd28) {
		// TODO : fill this with the necessary business logic
		throw new java.lang.UnsupportedOperationException("Please implement "
				+ this.getClass().getName() + "#fundtransfersingleitem_dd");
	}

}
