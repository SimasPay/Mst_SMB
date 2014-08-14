package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTransactionLog;
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
		sctlQuery.setSourceChannelApplication(searchBean
				.getSourceChannelApplication());
		sctlQuery.setStatus(searchBean.getStatus());
		sctlQuery.setStatusList(searchBean.getStatusList());
		sctlQuery.setStart(searchBean.getStart());
		sctlQuery.setLimit(searchBean.getLimit());
		List<ServiceChargeTransactionLog> sctlList = sctlDAO.get(sctlQuery);
		if (sctlList != null) {
			for (int i = 0; i < sctlList.size(); i++) {
				ServiceChargeTransactionLog sctl = sctlList.get(i);
				Transaction transaction = new Transaction();
				updateMessage(sctl, transaction);
				results.add(transaction);
			}
		}
		searchBean.setTotal(sctlQuery.getTotal());
		return results;
	}

	private void updateMessage(ServiceChargeTransactionLog sctl,
			Transaction transaction) {
		TransactionType transactionType = null;
		Service service = null;
		if (sctl.getCalculatedCharge() != null) {
			transaction.setCalculatedCharge(sctl.getCalculatedCharge());
		}
		if (sctl.getChannelCodeID() != null) {
			ChannelCode cc = ccDAO.getById(sctl.getChannelCodeID());
			transaction.setAccessMethodText(cc != null ? cc.getChannelName()
					: "");
		}
		if (sctl.getCommodityTransferID() != null) {
			transaction.setCommodityTransferID(sctl.getCommodityTransferID());
		}
		if (sctl.getDestMDN() != null) {
			transaction.setDestMDN(sctl.getDestMDN());
		}
		if (sctl.getDestPartnerID() != null) {
			transaction.setDestPartnerID(sctl.getDestPartnerID());
			transaction.setDestPartnerCode(partnerDao.getById(
					sctl.getDestPartnerID()).getPartnerCode());
		}
		if (sctl.getFailureReason() != null) {
			transaction.setFailureReason(sctl.getFailureReason());
		}
		if (sctl.getInvoiceNo() != null) {
			transaction.setInvoiceNumber(sctl.getInvoiceNo());
		}
		if (sctl.getMFSBillerCode() != null) {
			transaction.setMFSBillerCode(sctl.getMFSBillerCode());
		}
		if (sctl.getOnBeHalfOfMDN() != null) {
			transaction.setOnBeHalfOfMDN(sctl.getOnBeHalfOfMDN());
		}
		if (sctl.getTransactionTypeID() != null) {
			transaction.setTransactionTypeID(sctl.getTransactionTypeID());
			transactionType = ttDAO.getById(sctl.getTransactionTypeID());
			transaction.setTransactionName(transactionType.getDisplayName());
		}
		if (sctl.getServiceID() != null) {
			transaction.setServiceID(sctl.getServiceID());
			service = serviceDAO.getById(sctl.getServiceID());
			transaction.setServiceName(service.getDisplayName());
		}
		if (sctl.getServiceProviderID() != null) {
			transaction.setServiceProviderID(sctl.getServiceProviderID());
		}
		if (sctl.getSourceMDN() != null) {
			transaction.setSourceMDN(sctl.getSourceMDN());
		}
		if (sctl.getSourcePartnerID() != null) {
			transaction.setSourcePartnerID(sctl.getSourcePartnerID());
			transaction.setSourcePartnerCode(partnerDao.getById(
					sctl.getSourcePartnerID()).getPartnerCode());
		}
		if (sctl.getStatus() != null) {
			transaction.setStatus(sctl.getStatus());
			transaction.setTransferStatusText(enumTextService.getEnumTextValue(
					CmFinoFIX.TagID_SCTLStatus, CmFinoFIX.Language_English,
					sctl.getStatus()));
		}
		if (sctl.getTransactionAmount() != null) {
			transaction.setTransactionAmount(sctl.getTransactionAmount());
		}
		if (sctl.getTransactionID() != null) {
			transaction.setTransactionID(sctl.getTransactionID());
		}
		if (sctl.getTransactionRuleID() != null) {
			transaction.setTransactionRuleID(sctl.getTransactionRuleID());
		}
		transaction.setTransactionTime(sctl.getCreateTime());
		transaction.setID(sctl.getID());
		if (sctl.getParentSCTLID() != null) {
			transaction.setParentSCTLID(sctl.getParentSCTLID());
		}
		if (StringUtils.isNotBlank(sctl.getReversalReason())) {
			transaction.setReversalReason(sctl.getReversalReason());
		}
		transaction.setIsChargeDistributed(sctl.getIsChargeDistributed());
		if (sctl.getIsTransactionReversed() != null) {
			transaction.setIsTransactionReversed(sctl
					.getIsTransactionReversed());
		} else {
			transaction.setIsTransactionReversed(CmFinoFIX.Boolean_False);
		}
		transaction.setAmtRevStatus(sctl.getAmtRevStatus());
		transaction.setChrgRevStatus(sctl.getChrgRevStatus());

		if (sctl.getAmtRevStatus() != null) {
			transaction.setAmtRevStatusText(enumTextService.getEnumTextValue(
					CmFinoFIX.TagID_SCTLStatus, CmFinoFIX.Language_English,
					sctl.getAmtRevStatus()));
		}
		if (sctl.getInfo1() != null) {
			transaction.setInfo1(sctl.getInfo1());
		}

		if (sctl.getChrgRevStatus() != null) {
			transaction.setChrgRevStatusText(enumTextService.getEnumTextValue(
					CmFinoFIX.TagID_SCTLStatus, CmFinoFIX.Language_English,
					sctl.getChrgRevStatus()));
		}
	}
}
