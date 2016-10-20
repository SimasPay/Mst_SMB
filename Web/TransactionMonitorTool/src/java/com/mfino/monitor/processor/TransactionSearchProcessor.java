package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.monitor.model.Transaction;
import com.mfino.monitor.processor.Interface.TransactionSearchProcessorI;
import com.mfino.service.EnumTextService;

/**
 * @author Srikanth
 * 
 */
@org.springframework.stereotype.Service("TransactionSearchProcessor")
public class TransactionSearchProcessor extends BaseProcessor implements TransactionSearchProcessorI{
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	public List<Transaction> process(Transaction searchBean) {
		List<Transaction> results = new ArrayList<Transaction>();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		sctlQuery.setBillerCode(searchBean.getMFSBillerCode());
		sctlQuery.setDestMdn(searchBean.getDestMDN());
		sctlQuery.setDestPartnerID(searchBean.getDestPartnerID());
		sctlQuery.setId(searchBean.getID());
		//sctlQuery.setLastUpdateTimeGE(searchBean.getUpdateTimeGE());
		//sctlQuery.setLastUpdateTimeLT(searchBean.getUpdateTimeLT());
		
		sctlQuery.setCreateTimeGE(searchBean.getUpdateTimeGE());
		sctlQuery.setCreateTimeLT(searchBean.getUpdateTimeLT());
		
		sctlQuery.setSourceMdn(searchBean.getSourceMDN());
		sctlQuery.setSourcePartnerID(searchBean.getSourcePartnerID());
		sctlQuery.setServiceID(searchBean.getServiceID());
		sctlQuery.setSourceChannelApplication(searchBean.getSourceChannelApplication());
		sctlQuery.setStatus(searchBean.getStatus());
		sctlQuery.setStatusList(searchBean.getStatusList());
		sctlQuery.setStart(searchBean.getStart());
		sctlQuery.setLimit(searchBean.getLimit());
		sctlQuery.setTransactionTypeID(searchBean.getTransactionTypeID());
		sctlQuery.setIDOrdered(true);
		List<ServiceChargeTxnLog> sctlList = sctlDAO.get(sctlQuery);
		if (sctlList != null) {
			for (int i = 0; i < sctlList.size(); i++) {
				ServiceChargeTxnLog sctl = sctlList.get(i);
				Transaction transaction = new Transaction();
				updateMessage(sctl, transaction);
				results.add(transaction);
			}
		}
		searchBean.setTotal(sctlQuery.getTotal());
		return results;
	}

	private void updateMessage(ServiceChargeTxnLog sctl, Transaction transaction) {
		TransactionType transactionType = null;
		Service service = null;
		if (sctl.getCalculatedcharge() != null) {
			transaction.setCalculatedCharge(sctl.getCalculatedcharge());
		}
		if (sctl.getChannelcodeid() != null) {
			ChannelCode cc = ccDAO.getById(sctl.getChannelcodeid().longValue());
			transaction.setAccessMethodText(cc != null ? cc.getChannelname()
					: "");
		}
		if (sctl.getCommoditytransferid() != null) {
			transaction.setCommodityTransferID(sctl.getCommoditytransferid().longValue());
		}
		if (sctl.getDestmdn() != null) {
			transaction.setDestMDN(sctl.getDestmdn());
		}
		if (sctl.getDestpartnerid() != null) {
			transaction.setDestPartnerID(sctl.getDestpartnerid().longValue());
			transaction.setDestPartnerCode(partnerDao.getById(
					sctl.getDestpartnerid().longValue()).getPartnercode());
		}
		if (sctl.getFailurereason() != null) {
			transaction.setFailureReason(sctl.getFailurereason());
		}
		if (sctl.getInvoiceno() != null) {
			transaction.setInvoiceNumber(sctl.getInvoiceno());
		}
		if (sctl.getMfsbillercode() != null) {
			transaction.setMFSBillerCode(sctl.getMfsbillercode());
		}
		if (sctl.getOnbehalfofmdn() != null) {
			transaction.setOnBeHalfOfMDN(sctl.getOnbehalfofmdn());
		}
		if (sctl.getTransactiontypeid() != null) {
			transaction.setTransactionTypeID(sctl.getTransactiontypeid().longValue());
			transactionType = ttDAO.getById(sctl.getTransactiontypeid().longValue());
			transaction.setTransactionName(transactionType.getDisplayname());
		}
		if (sctl.getServiceid() != null) {
			transaction.setServiceID(sctl.getServiceid().longValue());
			service = serviceDAO.getById(sctl.getServiceid().longValue());
			transaction.setServiceName(service.getDisplayname());
		}
		if (sctl.getServiceproviderid() != null) {
			transaction.setServiceProviderID(sctl.getServiceproviderid().longValue());
		}
		if (sctl.getSourcemdn() != null) {
			transaction.setSourceMDN(sctl.getSourcemdn());
		}
		if (sctl.getSourcepartnerid() != null) {
			transaction.setSourcePartnerID(sctl.getSourcepartnerid().longValue());
			transaction.setSourcePartnerCode(partnerDao.getById(
					sctl.getSourcepartnerid().longValue()).getPartnercode());
		}
		if (Long.valueOf(sctl.getStatus()) != null) {
			transaction.setStatus((int)sctl.getStatus());
			transaction.setTransferStatusText(enumTextService.getEnumTextValue(
					CmFinoFIX.TagID_SCTLStatus, CmFinoFIX.Language_English,
					sctl.getStatus()));
		}
		if (sctl.getTransactionamount() != null) {
			transaction.setTransactionAmount(sctl.getTransactionamount());
		}
		if (sctl.getTransactionid() != null) {
			transaction.setTransactionID(sctl.getTransactionid().longValue());
		}
		if (sctl.getTransactionruleid() != null) {
			transaction.setTransactionRuleID(sctl.getTransactionruleid().longValue());
		}
		transaction.setTransactionTime(sctl.getCreatetime());
		transaction.setID(sctl.getId().longValue());
		if (sctl.getParentsctlid() != null) {
			transaction.setParentSCTLID(sctl.getParentsctlid().longValue());
		}
		if (StringUtils.isNotBlank(sctl.getReversalreason())) {
			transaction.setReversalReason(sctl.getReversalreason());
		}
		transaction.setIsChargeDistributed(sctl.getIschargedistributed() != null && sctl.getIschargedistributed());
		if (sctl.getIstransactionreversed() != null) {
			transaction.setIsTransactionReversed(sctl.getIstransactionreversed() != null && sctl.getIstransactionreversed() );
		} else {
			transaction.setIsTransactionReversed(CmFinoFIX.Boolean_False);
		}
		transaction.setAmtRevStatus(sctl.getAmtrevstatus().intValue());
		transaction.setChrgRevStatus(sctl.getChrgrevstatus().intValue());

		if (sctl.getAmtrevstatus() != null) {
			transaction.setAmtRevStatusText(enumTextService.getEnumTextValue(
					CmFinoFIX.TagID_SCTLStatus, CmFinoFIX.Language_English,
					sctl.getAmtrevstatus()));
		}
		if (sctl.getInfo1() != null) {
			transaction.setInfo1(sctl.getInfo1());
		}

		if (sctl.getChrgrevstatus() != null) {
			transaction.setChrgRevStatusText(enumTextService.getEnumTextValue(
					CmFinoFIX.TagID_SCTLStatus, CmFinoFIX.Language_English,
					sctl.getChrgrevstatus()));
		} 
	}
}
