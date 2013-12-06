package com.mfino.transactionapi.result.xmlresulttypes.merchant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.result.XMLResult;
import com.mfino.service.EnumTextService;
@Service("MerchantCheckBalanceXMLResult")
public class MerchantCheckBalanceXMLResult extends XMLResult {
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	public MerchantCheckBalanceXMLResult() {
		super();
	}

	public void render() throws Exception {
		writeStartOfDocument();

		super.render();

		try {
			Pocket pocket = getPocketList().get(0);
			if (pocket != null) {
				getXmlWriter().writeStartElement("balanceDetail");

				getXmlWriter().writeStartElement("commodityType");
				getXmlWriter().writeCharacters(
				        enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, pocket.getPocketTemplate().getCommodity()),false);
				getXmlWriter().writeEndElement();

				getXmlWriter().writeStartElement("balance");
				if (pocket.getCurrentBalance() == null)
					getXmlWriter().writeCharacters("0",false);
				else
					getXmlWriter().writeCharacters(String.valueOf(pocket.getCurrentBalance()),false);
				getXmlWriter().writeEndElement();

				getXmlWriter().writeEndElement();
			}
		}
		catch (Exception ex) {
			log.error("Error occurred while rending MerchantCheckBalance result xml:",ex);
		}

		writeEndOfDocument();
	}
}