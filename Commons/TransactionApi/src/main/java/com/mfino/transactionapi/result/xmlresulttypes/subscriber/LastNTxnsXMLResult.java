package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import java.text.NumberFormat;
import java.util.List;

import com.mfino.dao.DAOFactory;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.SCTLSettlementMap;
import com.mfino.domain.Service;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank.CGEntries;
import com.mfino.result.XMLResult;
import com.mfino.service.EnumTextService;
import com.mfino.util.MfinoUtil;

@org.springframework.stereotype.Service("LastNTxnsXMLResult")
public class LastNTxnsXMLResult extends XMLResult {

	private String	SourceMDN;
	private EnumTextService enumTextService;
	private static final String TRANSACTION_FLAG_DEBIT = "D";
	private NumberFormat numberFormat = MfinoUtil.getNumberFormat();
	private String downloadURL;
	
	public LastNTxnsXMLResult() {
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

		getXmlWriter().writeStartElement("Name");
		getXmlWriter().writeCharacters((getFirstName() != null ? getFirstName() + " ": "") + (getLastName() != null ? getLastName(): "") ,false);
		getXmlWriter().writeEndElement();

		List<CMGetLastTransactionsFromBank.CGEntries> lastBankTrxnsList = getLastBankTrxnList();
		if (lastBankTrxnsList != null) {
			if (lastBankTrxnsList.size() > 0) {
				getXmlWriter().writeStartElement("transactionDetails");
				for (CGEntries entry : lastBankTrxnsList) {
					getXmlWriter().writeStartElement("transactionDetail");

					getXmlWriter().writeStartElement("refID");
					getXmlWriter().writeCharacters(String.valueOf(entry.getBankTransactionReferenceNumber()),true);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("commodityType");
					getXmlWriter().writeCharacters("money",false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("sourceMDN");
					getXmlWriter().writeCharacters(this.SourceMDN,false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("transactionType");
					StringBuilder sb = new StringBuilder();
					if (entry.getBankTransactionCodeDescription() != null) {
						sb.append(entry.getBankTransactionCodeDescription());
					}
					if (entry.getBankTransactionFlag() != null) {
						sb.append(" (");
						sb.append(entry.getBankTransactionFlag());
						sb.append(')');
					}
					getXmlWriter().writeCharacters(String.valueOf(sb),false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("transactionTime");
					getXmlWriter().writeCharacters(entry.getBankTransactionDate(),false);
					getXmlWriter().writeEndElement();

					if (entry.getAmount() != null ) {
						getXmlWriter().writeStartElement("amount");
						getXmlWriter().writeCharacters(numberFormat.format(entry.getAmount()),true);
						getXmlWriter().writeEndElement();
					}

					getXmlWriter().writeStartElement("bankID");
					getXmlWriter().writeCharacters(String.valueOf(getBankCode()),false);
					getXmlWriter().writeEndElement();
					
					getXmlWriter().writeStartElement("isCredit");
					boolean isCredit ;
					if(TRANSACTION_FLAG_DEBIT.equals(entry.getBankTransactionFlag().toString())){
						isCredit = false;
					}
					else{
						isCredit = true;
					}
					getXmlWriter().writeCharacters(String.valueOf(isCredit),false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeEndElement();
				}
				getXmlWriter().writeEndElement();
			}
		}

		List<CommodityTransfer> transactionList = getTransactionList();
		if (transactionList != null) {
			if (transactionList.size() > 0) {
				getXmlWriter().writeStartElement("transactionDetails");
				for (CommodityTransfer ct : transactionList) {
					getXmlWriter().writeStartElement("transactionDetail");

					getXmlWriter().writeStartElement("refID");
					getXmlWriter().writeCharacters(String.valueOf(ct.getSctlId()),true);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("commodityType");
					getXmlWriter().writeCharacters(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, ct.getCommodity()),false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("sourceMDN");
					getXmlWriter().writeCharacters(ct.getSourceMDN(),false);
					getXmlWriter().writeEndElement();
					
					if(ct.getUICategory().equals(CmFinoFIX.TransactionUICategory_NFC_Pocket_Topup)){
						getXmlWriter().writeStartElement("destMDN");
						getXmlWriter().writeCharacters(ct.getDestCardPAN(),false);
						getXmlWriter().writeEndElement();
					}else{
						getXmlWriter().writeStartElement("destMDN");
						getXmlWriter().writeCharacters(ct.getDestMDN(),false);
						getXmlWriter().writeEndElement();
					}
					

					/*getXmlWriter().writeStartElement("transactionType");
					getXmlWriter().writeCharacters(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory()),false);
					getXmlWriter().writeEndElement();*/
					getXmlWriter().writeStartElement("transactionType");
					getXmlWriter().writeCharacters(ct.getSourceMessage(), false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("transactionTime");
					getXmlWriter().writeCharacters(formatDate(ct.getStartTime()),false);
					getXmlWriter().writeEndElement();

					if (ct.getAmount() != null) {
						getXmlWriter().writeStartElement("amount");
						getXmlWriter().writeCharacters(numberFormat.format(ct.getAmount()),false);
						getXmlWriter().writeEndElement();
					}
					
					getXmlWriter().writeStartElement("isCredit");
					boolean isCredit ;
					if(ct.getPocketBySourcePocketID().getID().equals(getSourcePocket().getID())){
						isCredit = false;
					}
					else{
						isCredit = true;
					}
					getXmlWriter().writeCharacters(String.valueOf(isCredit),false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeEndElement();
				}
				getXmlWriter().writeEndElement();
			}
		}
		
		List<SCTLSettlementMap> pendingSettlementsList = getPendingSettlements();
		if (pendingSettlementsList != null) {
			if (pendingSettlementsList.size() > 0) {
				getXmlWriter().writeStartElement("transactionDetails");
				for (SCTLSettlementMap pendingSettlement : pendingSettlementsList) {
					getXmlWriter().writeStartElement("transactionDetail");

					getXmlWriter().writeStartElement("refID");
					getXmlWriter().writeCharacters(String.valueOf(pendingSettlement.getID()),true);
					getXmlWriter().writeEndElement();

					Partner partner = DAOFactory.getInstance().getPartnerDAO().getById(pendingSettlement.getPartnerID());
            		getXmlWriter().writeStartElement("TradeName");
					getXmlWriter().writeCharacters(String.valueOf(partner!=null?partner.getTradeName():""),false);
					getXmlWriter().writeEndElement();

					Service service = DAOFactory.getInstance().getServiceDAO().getById(pendingSettlement.getServiceID());
					getXmlWriter().writeStartElement("Service");
					getXmlWriter().writeCharacters(String.valueOf(service!=null?service.getDisplayName():""),false);
					getXmlWriter().writeEndElement();
					
					getXmlWriter().writeStartElement("SCTLID");
					getXmlWriter().writeCharacters(String.valueOf(pendingSettlement.getSctlId()),false);
					getXmlWriter().writeEndElement();
					
					getXmlWriter().writeStartElement("SettlementStatus");
					getXmlWriter().writeCharacters(String.valueOf(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SettlementStatus, null, pendingSettlement.getStatus())),false);
					getXmlWriter().writeEndElement();
					
					if (pendingSettlement.getAmount() != null) {
						getXmlWriter().writeStartElement("Amount");
						getXmlWriter().writeCharacters(numberFormat.format(pendingSettlement.getAmount()),false);
						getXmlWriter().writeEndElement();
					}
					
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
	public EnumTextService getEnumTextService() {
		return enumTextService;
	}
	
	public void setEnumTextService(EnumTextService enumTextService) {
		this.enumTextService = enumTextService;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}
}

