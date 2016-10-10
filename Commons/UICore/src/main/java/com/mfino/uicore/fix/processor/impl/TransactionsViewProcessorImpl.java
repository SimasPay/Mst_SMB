package com.mfino.uicore.fix.processor.impl;

import java.util.ArrayList;
import java.util.List;

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
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.MfsLedger;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer.CGEntries;
import com.mfino.fix.CmFinoFIX.CMJSError;
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
	SubscriberMdn queryPocketMDN = null;
	
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
			queryPocketMDN = pocket.getSubscriberMdn();
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
				if (mdn.equals(suspensePocket.getSubscriberMdn().getMdn())
						|| mdn.equals(chargesPocket.getSubscriberMdn().getMdn())
						|| mdn.equals(globalPocket.getSubscriberMdn().getMdn())
						|| mdn.equals(taxPocket.getSubscriberMdn().getMdn())) {
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
				query.setPocketId(globalPocket.getId().longValue());
			}
		}		
		query.setStart(realMsg.getstart());
		query.setLimit(realMsg.getlimit());
		List<MfsLedger> results = ledgerDao.get(query);
		if (results != null&&!results.isEmpty()) {
			realMsg.allocateEntries(results.size());

			for (int i = 0; i < results.size(); i++) {
				CMJSCommodityTransfer.CGEntries entry = new CMJSCommodityTransfer.CGEntries();
				MfsLedger ledger = results.get(i);
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

	private void updateMessage(MfsLedger ledger, CGEntries entry,
			CMJSCommodityTransfer realMsg) {
		
		CommodityTransfer ct = ctDao.getById(ledger.getCommoditytransferid().longValue());
		boolean isSystemPocket = false;
		boolean isRevertAmount = false;
		
		if(realMsg.getSourceDestnPocketID() != null) {
			if(DAOConstants.DEBIT_LEDGER_TYPE.equals(ledger.getLedgertype())) {
				entry.setSourcePocketID(ledger.getPocketid().longValue());
				entry.setSourceMDN(queryPocketMDN.getMdn());
				entry.setDestPocketID(suspensePocket.getId().longValue());
				entry.setDestMDN(suspensePocket.getSubscriberMdn().getMdn());
			} else if(DAOConstants.CREDIT_LEDGER_TYPE.equals(ledger.getLedgertype())) {
				entry.setSourcePocketID(suspensePocket.getId().longValue());
				entry.setSourceMDN(suspensePocket.getSubscriberMdn().getMdn());
				entry.setDestPocketID(ledger.getPocketid().longValue());
				entry.setDestMDN(queryPocketMDN.getMdn());
			}
		}
		entry.setAmount(ledger.getAmount());
		entry.setTransType(ledger.getLedgertype());
		if (DAOConstants.DEBIT_LEDGER_TYPE.equals(ledger.getLedgertype())) {
			entry.setDebitAmount(ledger.getAmount());
		}
		else if (DAOConstants.CREDIT_LEDGER_TYPE.equals(ledger.getLedgertype())) {
			entry.setCreditAmount(ledger.getAmount());
		}
		if(ct != null){
	        entry.setBankRetrievalReferenceNumber(ct.getBankretrievalreferencenumber());
			entry.setTransactionID(ct.getId().longValue());
			entry.setAccessMethodText(channelCodeService.getChannelNameBySourceApplication(((Long)ct.getSourceapplication()).intValue()));
			// Added as part of GT Request to identify the internal transaction type like E-B, E-E, B-E, B-B
	   		entry.setInternalTxnType(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUicategory()));
			entry.setCommodityText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_Commodity, null, ct.getCommodity()));
			if(!isSystemPocket&&!isRevertAmount){
				 entry.setTransferStatusText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null, ct.getTransferstatus()));
			}
		}
        
		
		entry.setID(ledger.getId().longValue());
		entry.setStartTime(ledger.getCreatetime());
		
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		Long sctlId = ledger.getSctlid().longValue();
		ServiceChargeTxnLog sctl = null;
		if (sctlId != null) {
			sctl = sctlDAO.getById(sctlId);
		}
		if(sctl != null){
			TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
			TransactionType tt = ttDAO.getById(sctl.getTransactiontypeid().longValue());
			entry.setTransactionTypeText(tt.getDisplayname());
		}else if(ct!=null){
			entry.setTransactionTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUicategory()));
		}
		if(isRevertAmount){
			entry.setTransactionTypeText("Reverted-"+entry.getTransactionTypeText());
		}
		
   		
		entry.setTransferStatusText("Complete");
		entry.setServiceChargeTransactionLogID(sctlId);
		
	}
}
