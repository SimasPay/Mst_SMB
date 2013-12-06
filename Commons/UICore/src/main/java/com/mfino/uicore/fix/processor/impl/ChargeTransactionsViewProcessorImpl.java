package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.fix.CmFinoFIX.CRPendingCommodityTransfer;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ChargeTransactionsViewProcessor;
import com.mfino.uicore.fix.processor.CommodityTransferUpdateMessage;
@Service("ChargeTransactionsViewProcessorImpl")
public class ChargeTransactionsViewProcessorImpl extends BaseFixProcessor implements ChargeTransactionsViewProcessor{
	
	 @Autowired
	    @Qualifier("CommodityTransferUpdateMessageImpl")
	    private CommodityTransferUpdateMessage commodityTransferUpdateMessage;
	
		@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
		public CFIXMsg process(CFIXMsg msg) throws Exception {
			CMJSCommodityTransfer realMsg = (CMJSCommodityTransfer) msg;
			DAOFactory daoFactory = DAOFactory.getInstance();
			 if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
				 ChargeTxnCommodityTransferMapDAO cTxnDao = daoFactory.getTxnTransferMap();
				 ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
				 query.setSctlID(realMsg.getServiceChargeTransactionLogID());
				 List<ChargeTxnCommodityTransferMap> cTxnMap = cTxnDao.get(query);
				 int i=0;
				 if(cTxnMap!=null&&!cTxnMap.isEmpty()){
					 CommodityTransferDAO ctDao = daoFactory.getCommodityTransferDAO();
					 PendingCommodityTransferDAO pctDao = daoFactory.getPendingCommodityTransferDAO();
					 realMsg.allocateEntries(cTxnMap.size());
					
					 for(ChargeTxnCommodityTransferMap ctxn:cTxnMap){
						 CMJSCommodityTransfer.CGEntries entry = new CMJSCommodityTransfer.CGEntries();
						 Long id = ctxn.getCommodityTransferID();
						 CRCommodityTransfer ct = ctDao.getById(id);
						 if(ct!=null){
							 commodityTransferUpdateMessage.updateMessage(ct, null, entry, realMsg);
							realMsg.getEntries()[i]=entry;
							i++;
						 }else{
							 CRPendingCommodityTransfer pct = pctDao.getById(id);
							 if(pct!=null){
								 commodityTransferUpdateMessage.updateMessage(pct, pct, entry, realMsg);
								 entry.setTransferStatusText("Pending -"+entry.getTransferStatusText());
								realMsg.getEntries()[i]=entry;
								i++;
							 }
						 }
					 }
					
				 }
				 realMsg.settotal(i);
				 realMsg.setsuccess(true);
			 }
			 return realMsg;
		}

}
