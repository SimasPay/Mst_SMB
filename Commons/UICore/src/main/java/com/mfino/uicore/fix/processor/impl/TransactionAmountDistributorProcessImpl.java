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
		if(Boolean.valueOf(transaction.getIsactualamount().toString())){
			e.setIsActualAmount(Boolean.valueOf(transaction.getIsactualamount().toString()));
		}
		if(Boolean.valueOf(transaction.getIspartofsharedupchain().toString())){
			e.setIsPartOfSharedUpChain(Boolean.valueOf(transaction.getIspartofsharedupchain().toString()));
		}
		if(transaction.getCreatedby()!=null){
			
		}
		e.setMSPID(transaction.getMfinoServiceProvider().getId().longValue());
		if(transaction.getPartner() != null)
		{
			e.setPartnerID(transaction.getPartner().getId().longValue());
			e.setDestPartnerTradeName(partnerDao.getById(transaction.getPartner().getId().longValue()).getTradename());
		}
		if(transaction.getSubscriber() != null)
		{
			e.setSubscriberID(transaction.getSubscriber().getId().longValue());
		}
		e.setChargeTypeName(tc.getChargeType().getName());
		e.setIsChargeFromCustomer(tc.getChargeDefinition().getIschargefromcustomer() == 1 ? Boolean.TRUE : Boolean.FALSE);
		e.setPocketID(transaction.getPocket().getId().longValue());
		e.setServiceChargeTransactionLogID(transaction.getServicechargetransactionlogid().longValue());
		e.setShareAmount(transaction.getShareamount());
		e.setTaxAmount(transaction.getTaxamount());
		e.setTransactionID(sr.getCommoditytransferid().longValue());
		e.setStatus(Integer.valueOf(Long.valueOf(transaction.getStatus()).intValue()));
		e.setSourceMDN(sr.getSourcemdn());
		//e.setSourceSubscriberName(sr.getSourceSubscriberName());
		e.setUpdatedBy(transaction.getUpdatedby());
		e.setCreatedBy(transaction.getCreatedby());
		e.setCreateTime(transaction.getCreatetime());
		//e.setCurrency(sr.getCurrency());
		e.setLastUpdateTime(transaction.getLastupdatetime());
		e.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TADLStatus, CmFinoFIX.Language_English, transaction.getStatus()));
		e.setChannelSourceApplicationText(sr.getChannelcodeid()+"");
		e.setTransferFailureReasonText(transaction.getFailurereason());
	}
}
