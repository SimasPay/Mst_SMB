package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeTypeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.TransactionAmountDistributionLogDAO;
import com.mfino.dao.TransactionChargeDAO;
import com.mfino.dao.query.TransactionAmountDistributionQuery;
import com.mfino.domain.Partner;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.TxnAmountDstrbLog;
import com.mfino.domain.TransactionCharge;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSTransactionAmountDistributionLog;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.TransactionAmountDistributorProcess;

@Service("TransactionAmountDistributorProcessImpl")
public class TransactionAmountDistributorProcessImpl extends BaseFixProcessor implements TransactionAmountDistributorProcess {

	PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
	TransactionChargeDAO transactionChargeDao = DAOFactory.getInstance().getTransactionChargeDAO();
	ChargeTypeDAO chargeTypeDao = DAOFactory.getInstance().getChargeTypeDAO();
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSTransactionAmountDistributionLog realMsg = (CMJSTransactionAmountDistributionLog) msg;
		TransactionAmountDistributionLogDAO dao = DAOFactory.getInstance().getTransactionAmountDistributionLogDAO();
    	TransactionAmountDistributionQuery query = new TransactionAmountDistributionQuery();
		if(realMsg.getTradeNameSearch()!=null){
			PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
			Partner partner=partnerDAO.getPartnerByTradeName(realMsg.getTradeNameSearch());
			if(partner!=null){
				query.setPartner(partner);
			}
		}
		if(realMsg.getServiceChargeTransactionLogID()!=null){
			query.setServiceChargeTransactionLogID(realMsg.getServiceChargeTransactionLogID());
		}
		if(realMsg.getEndTime()!=null){
			query.setCreateTimeLT(realMsg.getEndTime());
		}
		if(realMsg.getStartTime()!=null){
			query.setCreateTimeGE(realMsg.getStartTime());
		}
    	if(realMsg.getTransactionsTransferStatus()!=null){
    		query.setStatus(realMsg.getTransactionsTransferStatus());
    	}
		List<TxnAmountDstrbLog>  tran =dao.get(query);
		   	
    	realMsg.allocateEntries(tran.size());
    	int i = 0;
    	for(TxnAmountDstrbLog entity: tran){
    		CMJSTransactionAmountDistributionLog.CGEntries e = new CMJSTransactionAmountDistributionLog.CGEntries();
			updateMessage(entity, e);
			realMsg.getEntries()[i] = e;
			i++;
    	}

    	realMsg.setsuccess(CmFinoFIX.Boolean_True);
    	realMsg.settotal(tran.size());
		
		return realMsg;
	}

	private void updateMessage(TxnAmountDstrbLog transaction,
			CMJSTransactionAmountDistributionLog.CGEntries e) {
		ServiceChargeTransactionLogDAO st =DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sr = st.getById(transaction.getServicechargetransactionlogid().longValue());
		TransactionCharge tc = transaction.getTransactionCharge();
		
		
//		CommodityTransferDAO ctdao = new CommodityTransferDAO();
//		CommodityTransfer ct = ctdao.getById(sr.getCommodityTransferID());
		if(transaction.getIsactualamount()){
			e.setIsActualAmount(transaction.getIsactualamount());
		}
		if(transaction.getIspartofsharedupchain()){
			e.setIsPartOfSharedUpChain(transaction.getIspartofsharedupchain());
		}
		if(transaction.getCreatedby()!=null){
			
		}
		e.setMSPID(transaction.getMfinoServiceProvider().getId());
		if(transaction.getPartner() != null)
		{
			e.setPartnerID(transaction.getPartner().getId());
			e.setDestPartnerTradeName(partnerDao.getById(transaction.getPartner().getId()).getTradeName());
		}
		if(transaction.getSubscriber() != null)
		{
			e.setSubscriberID(transaction.getSubscriber().getId());
		}
		e.setChargeTypeName(tc.getChargeType().getName());
		e.setIsChargeFromCustomer(tc.getChargeDefinition().getIsChargeFromCustomer());
		e.setPocketID(transaction.getPocket().getId());
		e.setServiceChargeTransactionLogID(transaction.getServicechargetransactionlogid());
		e.setShareAmount(transaction.getShareAmount());
		e.setTaxAmount(transaction.getTaxAmount());
		e.setTransactionID(sr.getCommodityTransferID());
		e.setStatus(transaction.getStatus());
		e.setSourceMDN(sr.getSourceMDN());
		//e.setSourceSubscriberName(sr.getSourceSubscriberName());
		e.setUpdatedBy(transaction.getUpdatedBy());
		e.setCreatedBy(transaction.getCreatedby());
		e.setCreateTime(transaction.getCreateTime());
		//e.setCurrency(sr.getCurrency());
		e.setLastUpdateTime(transaction.getLastUpdateTime());
		e.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TADLStatus, CmFinoFIX.Language_English, transaction.getStatus()));
		e.setChannelSourceApplicationText(sr.getChannelCodeID()+"");
		e.setTransferFailureReasonText(transaction.getFailureReason());
	}
}
