package com.mfino.transactionapi.result.xmlresulttypes.nfc;

import java.util.List;

import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank.CGEntries;
import com.mfino.result.XMLResult;

/**
 * 
 * @author Amar
 *
 */
@org.springframework.stereotype.Service("NFCTransactionHistoryXMLResult")
public class NFCTransactionHistoryXMLResult extends XMLResult {

	private String	SourceMDN;
	private static final char TRANSACTION_FLAG_CREDIT = 'C';
	private String downloadURL;
		
	public NFCTransactionHistoryXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		super.render();
		
		if(getDownloadURL() != null)
		{
			getXmlWriter().writeStartElement("downloadURL");
			getXmlWriter().writeCharacters(getDownloadURL(),false);
			getXmlWriter().writeEndElement();
		}

		List<CMGetLastTransactionsFromBank.CGEntries> nfcTxnList = getNfcTransactionHistory();
		if (nfcTxnList != null) {
			if (nfcTxnList.size() > 0) {
				getXmlWriter().writeStartElement("transactionDetails");
				for (CGEntries entry : nfcTxnList) {
					getXmlWriter().writeStartElement("transactionDetail");

					getXmlWriter().writeStartElement("refID");
					getXmlWriter().writeCharacters(String.valueOf(entry.getBankTransactionReferenceNumber()),false);
					getXmlWriter().writeEndElement();
					
					getXmlWriter().writeStartElement("sourceMDN");
					getXmlWriter().writeCharacters(this.SourceMDN,false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("transactionType");
					getXmlWriter().writeCharacters(entry.getBankTransactionCode(), false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("transactionTime");
					getXmlWriter().writeCharacters(entry.getBankTransactionDate(),false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("amount");
					getXmlWriter().writeCharacters(String.valueOf(entry.getAmount()),false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("Merchant");
					getXmlWriter().writeCharacters(entry.getMerchantName(),false);
					getXmlWriter().writeEndElement();
					
					getXmlWriter().writeStartElement("CardPAN");
					getXmlWriter().writeCharacters(getCardPan(),false);
					getXmlWriter().writeEndElement();
					
					getXmlWriter().writeStartElement("CardAlias");
					getXmlWriter().writeCharacters(entry.getCardAlias(),false);
					getXmlWriter().writeEndElement();
					
					getXmlWriter().writeStartElement("isCredit");
					boolean isCredit ;
					if(TRANSACTION_FLAG_CREDIT == entry.getBankTransactionFlag()){
						isCredit = true;
					}
					else{
						isCredit = false;
					}
					getXmlWriter().writeCharacters(String.valueOf(isCredit),false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeEndElement();
				}
				getXmlWriter().writeEndElement();
			}
		}

		writeEndOfDocument();

	}

	public void setSourceMDN(String sourceMDN) {
		SourceMDN = sourceMDN;
	}

	public String getSourceMDN() {
		return SourceMDN;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}
	
}

