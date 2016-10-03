package com.mfino.transactionapi.result.xmlresulttypes.subscriber;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

import com.mfino.dao.DAOFactory;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.SctlSettlementMap;
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
	private Long totalTxnCount;
	private Boolean moreRecordsAvailable;
	
	
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

		if(getTotalTxnCount() != null)
		{
			getXmlWriter().writeStartElement("totalTxnCount");
			getXmlWriter().writeCharacters(String.valueOf(getTotalTxnCount()),false);
			getXmlWriter().writeEndElement();
		}
		if(getMoreRecordsAvailable() != null)
		{
			getXmlWriter().writeStartElement("MoreRecordsAvailable");
			getXmlWriter().writeCharacters(String.valueOf(getMoreRecordsAvailable()),false);
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
					getXmlWriter().writeCharacters(ct.getSourcemdn(),false);
					getXmlWriter().writeEndElement();
					
					if(ct.getUicategory().equals(CmFinoFIX.TransactionUICategory_NFC_Pocket_Topup)){
						getXmlWriter().writeStartElement("destMDN");
						getXmlWriter().writeCharacters(ct.getDestcardpan(),false);
						getXmlWriter().writeEndElement();
					}else{
						getXmlWriter().writeStartElement("destMDN");
						getXmlWriter().writeCharacters(ct.getDestmdn(),false);
						getXmlWriter().writeEndElement();
					}
					

					/*getXmlWriter().writeStartElement("transactionType");
					getXmlWriter().writeCharacters(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory()),false);
					getXmlWriter().writeEndElement();*/
					getXmlWriter().writeStartElement("transactionType");
					getXmlWriter().writeCharacters(ct.getSourcemessage(), false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("transactionTime");
					getXmlWriter().writeCharacters(formatDateForTransaction(ct.getStarttime()),false);
					getXmlWriter().writeEndElement();
					
					getXmlWriter().writeStartElement("transactionDescription");
					getXmlWriter().writeCharacters(ct.getGeneratedTxnDescription(),false);
					getXmlWriter().writeEndElement();

					getXmlWriter().writeStartElement("isCredit");
					boolean isCredit ;
					if(ct.getPocket().getId().equals(getSourcePocket().getId())){
						isCredit = false;
					}
					else{
						isCredit = true;
					}
					getXmlWriter().writeCharacters(String.valueOf(isCredit),false);
					getXmlWriter().writeEndElement();
					
					if (ct.getAmount() != null) {
						BigDecimal txnAmount = ct.getAmount();
						if (!isCredit) {
							txnAmount = txnAmount.add(ct.getCharges());
						}
						getXmlWriter().writeStartElement("amount");
						getXmlWriter().writeCharacters(numberFormat.format(txnAmount),false);
						getXmlWriter().writeEndElement();
					}

					getXmlWriter().writeEndElement();
				}
				getXmlWriter().writeEndElement();
			}
		}
		
		List<SctlSettlementMap> pendingSettlementsList = getPendingSettlements();
		if (pendingSettlementsList != null) {
			if (pendingSettlementsList.size() > 0) {
				getXmlWriter().writeStartElement("transactionDetails");
				for (SctlSettlementMap pendingSettlement : pendingSettlementsList) {
					getXmlWriter().writeStartElement("transactionDetail");

					getXmlWriter().writeStartElement("refID");
					getXmlWriter().writeCharacters(String.valueOf(pendingSettlement.getId()),true);
					getXmlWriter().writeEndElement();

					Partner partner = DAOFactory.getInstance().getPartnerDAO().getById(pendingSettlement.getPartner().getId().longValue());
            		getXmlWriter().writeStartElement("TradeName");
					getXmlWriter().writeCharacters(String.valueOf(partner!=null?partner.getTradename():""),false);
					getXmlWriter().writeEndElement();

					Service service = DAOFactory.getInstance().getServiceDAO().getById(pendingSettlement.getService().getId().longValue());
					getXmlWriter().writeStartElement("Service");
					getXmlWriter().writeCharacters(String.valueOf(service!=null?service.getDisplayname():""),false);
					getXmlWriter().writeEndElement();
					
					getXmlWriter().writeStartElement("SCTLID");
					getXmlWriter().writeCharacters(String.valueOf(pendingSettlement.getServiceChargeTxnLog().getId()),false);
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

	public Long getTotalTxnCount() {
		return totalTxnCount;
	}

	public void setTotalTxnCount(Long totalTxnCount) {
		this.totalTxnCount = totalTxnCount;
	}

	public Boolean getMoreRecordsAvailable() {
		return moreRecordsAvailable;
	}

	public void setMoreRecordsAvailable(Boolean moreRecordsAvailable) {
		this.moreRecordsAvailable = moreRecordsAvailable;
	}
}

