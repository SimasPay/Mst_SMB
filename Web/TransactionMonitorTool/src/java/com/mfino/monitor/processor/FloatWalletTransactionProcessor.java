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
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.MFSLedger;
import com.mfino.domain.ServiceChargeTxnLog;
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

	private Map<Long, CommodityTransfer> ctMap;
	
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

	private Map<Long, CommodityTransfer> getCommodityTransferMap(
			List<MFSLedger> results) {
		Map<Long, CommodityTransfer> ctMap = new HashMap<Long, CommodityTransfer>();
		for (MFSLedger ledger : results) {
			if (!ctMap.containsKey(ledger.getCommoditytransferid())) {
				CommodityTransfer ct = commodityTransferDAO.getById(ledger.getCommoditytransferid().longValue());
				if (ct == null) {
					//ct =pendingDAO.getById(ledger.getCommoditytransferid().longValue());
				}
				if (ct != null) {
					ctMap.put(ct.getId().longValue(), ct);
				}
			}
		}
		return ctMap;
	}

	private void updateMessage(MFSLedger ledger,
			FloatWalletTransaction floatWalletTransaction,
			FloatWalletTransaction searchBean) {
		CommodityTransfer ct = ctMap.get(ledger.getCommoditytransferid());
		boolean isSystemPocket = true;
		boolean isRevertAmount = false;
		
		floatWalletTransaction.setTransType(ledger.getLedgertype());
		if (DAOConstants.DEBIT_LEDGER_TYPE.equals(ledger.getLedgertype())) {
			floatWalletTransaction.setDebitAmount(ledger.getAmount());
		}
		else if (DAOConstants.CREDIT_LEDGER_TYPE.equals(ledger.getLedgertype())) {		
			floatWalletTransaction.setCreditAmount(ledger.getAmount());
		}

		floatWalletTransaction.setTransactionID(ct.getId().longValue());
		floatWalletTransaction.setID(ledger.getId().longValue());
		floatWalletTransaction.setTransactionTime(ledger.getCreatetime());
			floatWalletTransaction.setAccessMethodText(channelCodeService
					.getChannelNameBySourceApplication((int)ct.getSourceapplication()));
			
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		Long sctlId = ledger.getSctlid().longValue();
		ServiceChargeTxnLog sctl = null;
		if (sctlId != null) {
			sctl = sctlDAO.getById(sctlId);
		}
		if(sctl != null){
			TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
			TransactionType tt = ttDAO.getById(sctl.getTransactiontypeid().longValue());
			floatWalletTransaction.setTransactionName(tt.getDisplayname());
		}
		else {
			floatWalletTransaction.setTransactionName(enumTextService
					.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null,
							ct.getUicategory()));
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
							ct.getTransferstatus()));
		}
		floatWalletTransaction.setServiceChargeTransactionLogID(sctlId);

	}
}
