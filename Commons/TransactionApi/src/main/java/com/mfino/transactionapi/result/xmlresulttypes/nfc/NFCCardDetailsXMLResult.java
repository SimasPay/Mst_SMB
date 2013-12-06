package com.mfino.transactionapi.result.xmlresulttypes.nfc;

import java.text.NumberFormat;
import java.util.List;

import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank.CGEntries;
import com.mfino.result.XMLResult;
import com.mfino.util.MfinoUtil;

/**
 * 
 * @author Amar
 *
 */
@org.springframework.stereotype.Service("NFCCardDetailsXMLResult")
public class NFCCardDetailsXMLResult extends XMLResult {

	public NFCCardDetailsXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		super.render();

		List<CMBalanceInquiryFromBank.CGEntries> lastBankTrxnsList = getNfcCardBalances();
		if (lastBankTrxnsList != null) {
			if (lastBankTrxnsList.size() > 0) {
				getXmlWriter().writeStartElement("NFCCardDetails");
				for (CGEntries entry : lastBankTrxnsList) {
					getXmlWriter().writeStartElement("NFCCard");

					getXmlWriter().writeStartElement("CardPAN");
					getXmlWriter().writeCharacters(String.valueOf(entry.getSourceCardPAN()),true);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("CardAlias");
					getXmlWriter().writeCharacters(String.valueOf(entry.getCardAlias()),true);
					getXmlWriter().writeEndElement();

					if (entry.getAmount() != null) {
						getXmlWriter().writeStartElement("Amount");
						NumberFormat numberFormat = MfinoUtil.getNumberFormat();
						getXmlWriter().writeCharacters(numberFormat.format(entry.getAmount()),true);
						getXmlWriter().writeEndElement();
					}

					getXmlWriter().writeEndElement();
				}
				getXmlWriter().writeEndElement();
			}
		}

		writeEndOfDocument();
	}
}

