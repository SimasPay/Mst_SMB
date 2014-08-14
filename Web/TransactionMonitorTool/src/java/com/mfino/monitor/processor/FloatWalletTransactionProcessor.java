package com.mfino.monitor.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.MFSLedgerQuery;
import com.mfino.domain.MFSLedger;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.monitor.model.FloatWalletTransaction;
import com.mfino.monitor.processor.Interface.FloatWalletTransactionProcessorI;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.EnumTextService;

/**
 * @author Srikanth
 * 
 */
@Service("FloatWalletTransactionProcessor")
public class FloatWalletTransactionProcessor extends BaseProcessor implements FloatWalletTransactionProcessorI{

	private Map<Long, CRCommodityTransfer> ctMap;
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;

	public List<FloatWalletTransaction> process(
			FloatWalletTransaction searchBean) {
		List<FloatWalletTransaction> results = new ArrayList<FloatWalletTransaction>();
		MFSLedgerQuery query = new MFSLedgerQuery();
		query.setPocketId(searchBean.getSourceDestnPocketID());
		query.setCreateTimeGE(searchBean.getCreateTimeGE());
		query.setCreateTimeLT(searchBean.getCreateTimeLT());
		query.setStart(searchBean.getStart());
		query.setLimit(searchBean.getLimit());
		List<MFSLedger> ledgerResults = ledgerDAO.get(query);
		if (ledgerResults != null && !ledgerResults.isEmpty()) {
			ctMap= getCommodityTransferMap(ledgerResults);
			for (int i = 0; i < ledgerResults.size(); i++) {
				MFSLedger ledger = ledgerResults.get(i);
				FloatWalletTransaction floatWalletTransaction = new FloatWalletTransaction();
				updateMessage(ledger, floatWalletTransaction, searchBean);
				results.add(floatWalletTransaction);
			}
			searchBean.setTotal(query.getTotal());
		}
		return results;
	}

	private Map<Long, CRCommodityTransfer> getCommodityTransferMap(
			List<MFSLedger> results) {
		Map<Long, CRCommodityTransfer> ctMap = new HashMap<Long, CRCommodityTransfer>();
		for (MFSLedger ledger : results) {
			if (!ctMap.containsKey(ledger.getCommodityTransferID())) {
				CRCommodityTransfer ct = commodityTransferDAO.getById(ledger
						.getCommodityTransferID());
				if (ct == null) {
					ct = pendingDAO.getById(ledger.getCommodityTransferID());
				}
				if (ct != null) {
					ctMap.put(ct.getID(), ct);
				}
			}
		}
		return ctMap;
	}

	private void updateMessage(MFSLedger ledger,
			FloatWalletTransaction floatWalletTransaction,
			FloatWalletTransaction searchBean) {
		CRCommodityTransfer ct = ctMap.get(ledger.getCommodityTransferID());
		boolean isSystemPocket = true;
		boolean isRevertAmount = false;
		
		floatWalletTransaction.setTransType(ledger.getLedgerType());
		if (DAOConstants.DEBIT_LEDGER_TYPE.equals(ledger.getLedgerType())) {
			floatWalletTransaction.setDebitAmount(ledger.getAmount());
		}
		else if (DAOConstants.CREDIT_LEDGER_TYPE.equals(ledger.getLedgerType())) {		
			floatWalletTransaction.setCreditAmount(ledger.getAmount());
		}

		floatWalletTransaction.setTransactionID(ct.getID());
		floatWalletTransaction.setID(ledger.getID());
		floatWalletTransaction.setTransactionTime(ledger.getCreateTime());
			floatWalletTransaction.setAccessMethodText(channelCodeService
					.getChannelNameBySourceApplication(ct.getSourceApplication()));
			
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		Long sctlId = ledger.getSctlId();
		ServiceChargeTransactionLog sctl = null;
		if (sctlId != null) {
			sctl = sctlDAO.getById(sctlId);
		}
		if(sctl != null){
			TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
			TransactionType tt = ttDAO.getById(sctl.getTransactionTypeID());
			floatWalletTransaction.setTransactionName(tt.getDisplayName());
		}
		else {
			floatWalletTransaction.setTransactionName(enumTextService
					.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null,
							ct.getUICategory()));
		}			

		if (isRevertAmount) {
			floatWalletTransaction.setTransactionName("Reverted-"
					+ floatWalletTransaction.getTransactionName());
		}
		floatWalletTransaction.setCommodityText(enumTextService
				.getEnumTextValue(CmFinoFIX.TagID_Commodity, null,
						ct.getCommodity()));
		floatWalletTransaction.setTransferStatusText("Complete");
		if (!isSystemPocket && !isRevertAmount) {
			floatWalletTransaction.setTransferStatusText(enumTextService
					.getEnumTextValue(CmFinoFIX.TagID_TransferStatus, null,
							ct.getTransferStatus()));
		}
		floatWalletTransaction.setServiceChargeTransactionLogID(sctlId);

	}
}
