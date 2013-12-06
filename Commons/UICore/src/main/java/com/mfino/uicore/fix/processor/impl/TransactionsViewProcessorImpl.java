package com.mfino.uicore.fix.processor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.DAOConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSLedgerDAO;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.MFSLedgerQuery;
import com.mfino.domain.MFSLedger;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer.CGEntries;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.EnumTextService;
import com.mfino.service.SystemParametersService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.TransactionsViewProcessor;

@Service("TransactionsViewProcessorImpl")
public class TransactionsViewProcessorImpl extends BaseFixProcessor implements TransactionsViewProcessor{

	MFSLedgerDAO ledgerDao = DAOFactory.getInstance().getMFSLedgerDAO();
	PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
	CommodityTransferDAO ctDao = DAOFactory.getInstance().getCommodityTransferDAO();
	PendingCommodityTransferDAO pctDao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
	SubscriberDAO subscriberDao = DAOFactory.getInstance().getSubscriberDAO();
	Pocket suspensePocket = null;
	Pocket chargesPocket = null;
	Pocket globalPocket = null;
	Pocket taxPocket = null;
	SubscriberMDN queryPocketMDN = null;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSCommodityTransfer realMsg = (CMJSCommodityTransfer) msg;
		suspensePocket = pocketDao.getById(systemParametersService.getLong(SystemParameterKeys.SUSPENSE_POCKET_ID_KEY));
		chargesPocket = pocketDao.getById(systemParametersService.getLong(SystemParameterKeys.CHARGES_POCKET_ID_KEY));
		globalPocket = pocketDao.getById(systemParametersService.getLong(SystemParameterKeys.GLOBAL_SVA_POCKET_ID_KEY));
		taxPocket = pocketDao.getById(systemParametersService.getLong(SystemParameterKeys.TAX_POCKET_ID_KEY));
		if (realMsg.getSourceDestnPocketID() != null) {
			Pocket pocket = pocketDao.getById(realMsg.getSourceDestnPocketID());
			queryPocketMDN = pocket.getSubscriberMDNByMDNID();
			if(CmFinoFIX.PocketType_SVA.equals(pocket.getPocketTemplate().getType())&&
					CmFinoFIX.Commodity_Money.equals(pocket.getPocketTemplate().getCommodity())){
				return processTransactions(realMsg, null, null, CmFinoFIX.PocketType_SVA);
			}
			else if (CmFinoFIX.PocketType_BankAccount.equals(pocket.getPocketTemplate().getType())&&
					CmFinoFIX.Commodity_Money.equals(pocket.getPocketTemplate().getCommodity())){
				return processTransactions(realMsg, null, null, CmFinoFIX.PocketType_BankAccount);
			}

		} else if (realMsg.getSourceDestMDNAndID() != null) {
			String[] mdnAndID = realMsg.getSourceDestMDNAndID().split(",");
			if (mdnAndID.length == 2 && StringUtils.isNumeric(mdnAndID[1])) {
				String mdn = mdnAndID[0];
				long mdnId = new Long(mdnAndID[1]);
				if (mdn.equals(suspensePocket.getSubscriberMDNByMDNID().getMDN())
						|| mdn.equals(chargesPocket.getSubscriberMDNByMDNID().getMDN())
						|| mdn.equals(globalPocket.getSubscriberMDNByMDNID().getMDN())
						|| mdn.equals(taxPocket.getSubscriberMDNByMDNID().getMDN())) {
					return processTransactions(realMsg, mdn, mdnId, null);
				}
			}
		}
		CMJSError error = new CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		return error;
	}

	private CFIXMsg processTransactions(CMJSCommodityTransfer msg, String mdn, Long mdnId, Integer pocketType) throws Exception  {
		CMJSCommodityTransfer realMsg = msg;
		MFSLedgerQuery query = new MFSLedgerQuery();
		Map<Long,CRCommodityTransfer> ctMap;

		if (realMsg.getSourceDestnPocketID() != null) {
			query.setPocketId(realMsg.getSourceDestnPocketID());
		}
		if (realMsg.getSourceDestMDNAndID() != null) {
			query.setMdnId(mdnId);
		}
		if(realMsg.getStartTime()!=null){
			query.setCreateTimeGE(realMsg.getStartTime());
		}
		if(realMsg.getEndTime()!=null){
			query.setCreateTimeLT(realMsg.getEndTime());
		}
		if(realMsg.getServiceChargeTransactionLogID()!=null){
			List<Long> transferIDs = DAOFactory.getInstance().getTxnTransferMap().geTransferIdsBySCTLId(realMsg.getServiceChargeTransactionLogID());
			if(transferIDs==null||transferIDs.size() ==0){
				transferIDs = new ArrayList<Long>();
				transferIDs.add(0L);
				}
				query.setTransferIDs(transferIDs);				
		}
		if (pocketType != null && CmFinoFIX.PocketType_BankAccount.equals(pocketType)) {
			List<Long> transferIDs = ctDao.getCommodityTransferIdsBySourceAndDestPocketId(realMsg.getSourceDestnPocketID());
			if (CollectionUtils.isNotEmpty(transferIDs)) {
				query.setTransferIDs(transferIDs);
				query.setPocketId(globalPocket.getID());
			}
		}		
		query.setStart(realMsg.getstart());
		query.setLimit(realMsg.getlimit());
		List<MFSLedger> results = ledgerDao.get(query);
		if (results != null&&!results.isEmpty()) {
			realMsg.allocateEntries(results.size());

			for (int i = 0; i < results.size(); i++) {
				CMJSCommodityTransfer.CGEntries entry = new CMJSCommodityTransfer.CGEntries();
				MFSLedger ledger = results.get(i);
				updateMessage(ledger, entry, realMsg);
				realMsg.getEntries()[i] = entry;
			}
			  realMsg.settotal(query.getTotal());
		}
		 realMsg.setsuccess(CmFinoFIX.Boolean_True);
       
		return realMsg;
	}

	/*private Map<Long, CRCommodityTransfer> getCommodityTransferMap(
			List<MFSLedger> results) {
		Map<Long, CRCommodityTransfer> ctMap=new HashMap<Long, CRCommodityTransfer>();
		for(MFSLedger ledger:results){
			if(!ctMap.containsKey(ledger.getCommodityTransferID())){
				CRCommodityTransfer ct = ctDao.getById(ledger.getCommodityTransferID());
				if(ct==null){
					ct = pctDao.getById(ledger.getCommodityTransferID());
				}
				if(ct!=null){
					ctMap.put(ct.getID(), ct);
				}
			}
		}
		return ctMap;
	}*/

	private void updateMessage(MFSLedger ledger, CGEntries entry,
			CMJSCommodityTransfer realMsg) {
		
		CRCommodityTransfer ct = ctDao.getById(ledger.getCommodityTransferID());
		boolean isSystemPocket = false;
		boolean isRevertAmount = false;
		
		if(realMsg.getSourceDestnPocketID() != null) {
			if(DAOConstants.DEBIT_LEDGER_TYPE.equals(ledger.getLedgerType())) {
				entry.setSourcePocketID(ledger.getPocketID());
				entry.setSourceMDN(queryPocketMDN.getMDN());
				entry.setDestPocketID(suspensePocket.getID());
				entry.setDestMDN(suspensePocket.getSubscriberMDNByMDNID().getMDN());
			} else if(DAOConstants.CREDIT_LEDGER_TYPE.equals(ledger.getLedgerType())) {
				entry.setSourcePocketID(suspensePocket.getID());
				entry.setSourceMDN(suspensePocket.getSubscriberMDNByMDNID().getMDN());
				entry.setDestPocketID(ledger.getPocketID());
				entry.setDestMDN(queryPocketMDN.getMDN());
			}
		}
		entry.setAmount(ledger.getAmount());
		entry.setTransType(ledger.getLedgerType());
		if (DAOConstants.DEBIT_LEDGER_TYPE.equals(ledger.getLedgerType())) {
			entry.setDebitAmount(ledger.getAmount());
		}
		else if (DAOConstants.CREDIT_LEDGER_TYPE.equals(ledger.getLedgerType())) {
			entry.setCreditAmount(ledger.getAmount());
		}
		if(ct != null){
	        entry.setBankRetrievalReferenceNumber(ct.getBankRetrievalReferenceNumber());
			entry.setTransactionID(ct.getID());
			entry.setAccessMethodText(channelCodeService.getChannelNameBySourceApplication(ct.getSourceApplication()));
			// Added as part of GT Request to identify the internal transaction type like E-B, E-E, B-E, B-B
	   		entry.setInternalTxnType(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory()));
			entry.setCommodityText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, ct.getCommodity()));
			if(!isSystemPocket&&!isRevertAmount){
				 entry.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null, ct.getTransferStatus()));
			}
		}
        
		
		entry.setID(ledger.getID());
		entry.setStartTime(ledger.getCreateTime());
		
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		Long sctlId = ledger.getSctlId();
		ServiceChargeTransactionLog sctl = null;
		if (sctlId != null) {
			sctl = sctlDAO.getById(sctlId);
		}
		if(sctl != null){
			TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
			TransactionType tt = ttDAO.getById(sctl.getTransactionTypeID());
			entry.setTransactionTypeText(tt.getDisplayName());
		}else if(ct!=null){
			entry.setTransactionTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory()));
		}
		if(isRevertAmount){
			entry.setTransactionTypeText("Reverted-"+entry.getTransactionTypeText());
		}
		
   		
		entry.setTransferStatusText("Complete");
		entry.setServiceChargeTransactionLogID(sctlId);
		
	}
}
