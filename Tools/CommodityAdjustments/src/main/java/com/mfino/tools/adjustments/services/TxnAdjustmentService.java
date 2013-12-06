package com.mfino.tools.adjustments.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.LedgerDAO;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Ledger;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.ServiceChargeTransactionLog;

public class TxnAdjustmentService {

	private static Logger	       log	= LoggerFactory.getLogger(TxnAdjustmentService.class);
	
	public List<Ledger> getStaleLedgerEntries(ServiceChargeTransactionLog sctl)
	{
		List<Ledger> ledgers = new ArrayList<Ledger>();
		if(sctl!=null)
		{
			ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
			query.setSctlID(sctl.getID());
			
			ChargeTxnCommodityTransferMapDAO ctmapDAO = DAOFactory.getInstance().getTxnTransferMap();
			List<ChargeTxnCommodityTransferMap> txnTransferMapList = ctmapDAO.get(query);
			
			LedgerDAO ledgerDAO = DAOFactory.getInstance().getLedgerDAO();
			List<Ledger> ctLedgers = new ArrayList<Ledger>();
			Long ctID = null;
			log.info("Getting ledger for SCTL:"+sctl.getID());
			
			for(ChargeTxnCommodityTransferMap txnMap : txnTransferMapList)
			{
				ctID = txnMap.getCommodityTransferID();
				log.info("Getting ledger for CT:"+ctID);
				
				CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
				CommodityTransfer ct = ctDAO.getById(ctID);
				
				Long destPocketID = null;
				Long srcPocketID = null;
				if(ct!=null)
				{
					Pocket srcPocket = ct.getPocketBySourcePocketID();
					srcPocketID = srcPocket.getID();
					destPocketID = ct.getDestPocketID();
				}
				else
				{
					PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
					PendingCommodityTransfer pct = pctDAO.getById(ctID);
					if(pct!=null)
					{
						log.warn("CT is in pending");
						Pocket srcPocket = pct.getPocketBySourcePocketID();
						srcPocketID = srcPocket.getID();
						destPocketID = pct.getDestPocketID();
					}
					
				}
				
				if(destPocketID==null || srcPocketID==null)
				{
					log.error("Destination or Soruce Pocket ID are null");
					break; 
				}
				
				ctLedgers = ledgerDAO.getByCommmodityTransferID(ctID);
				if(ctLedgers!=null)
				{
					
					if(ctLedgers.size()==1)
					{
						log.warn("Manual Adjustments: Only one ledger entry for CT:"+ctID);
						ledgers.add(ctLedgers.get(0));
					}
					for(Ledger ledger: ctLedgers)
					{
						log.info("CT: "+ctID+" ledger: "+ledger.getID()+" sourcePocket: "+ledger.getSourcePocketID()+
								" destPocketId: "+ledger.getDestPocketID()+ " amount: "+ledger.getAmount());
								
					}
				}
			}
			
		}
		return ledgers;
	}
	
	public List<Ledger> getAllLedgerEntries(ServiceChargeTransactionLog sctl)
	{
		List<Ledger> ledgers = new ArrayList<Ledger>();
		if(sctl!=null)
		{
			ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
			query.setSctlID(sctl.getID());
			
			ChargeTxnCommodityTransferMapDAO ctmapDAO = DAOFactory.getInstance().getTxnTransferMap();
			List<ChargeTxnCommodityTransferMap> txnTransferMapList = ctmapDAO.get(query);
			
			LedgerDAO ledgerDAO = DAOFactory.getInstance().getLedgerDAO();
			List<Ledger> ctLedgers = new ArrayList<Ledger>();
			Long ctID = null;
			log.info("Getting ledger for SCTL:"+sctl.getID());
			
			for(ChargeTxnCommodityTransferMap txnMap : txnTransferMapList)
			{
				ctID = txnMap.getCommodityTransferID();
				log.info("Getting ledger for CT:"+ctID);
				
				CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
				CommodityTransfer ct = ctDAO.getById(ctID);
				
				Long destPocketID = null;
				Long srcPocketID = null;
				if(ct!=null)
				{
					Pocket srcPocket = ct.getPocketBySourcePocketID();
					srcPocketID = srcPocket.getID();
					destPocketID = ct.getDestPocketID();
				}
				else
				{
					PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
					PendingCommodityTransfer pct = pctDAO.getById(ctID);
					if(pct!=null)
					{
						log.warn("CT is in pending");
						Pocket srcPocket = pct.getPocketBySourcePocketID();
						srcPocketID = srcPocket.getID();
						destPocketID = pct.getDestPocketID();
					}
					
				}
				
				if(destPocketID==null || srcPocketID==null)
				{
					log.error("Destination or Soruce Pocket ID are null");
					break; 
				}
				
				ctLedgers = ledgerDAO.getByCommmodityTransferID(ctID);
				if(ctLedgers!=null)
				{
					
					if(ctLedgers.isEmpty())
					{
						log.warn("Manual Adjustments: Only one ledger entry for CT:"+ctID);
						//ledgers.add(ctLedgers.get(0));
					}
					for(Ledger ledger: ctLedgers)
					{
						log.info("CT: "+ctID+" ledger: "+ledger.getID()+" sourcePocket: "+ledger.getSourcePocketID()+
								" destPocketId: "+ledger.getDestPocketID()+ " amount: "+ledger.getAmount());
						ledgers.add(ledger);
								
					}
				}
			}
			
		}
		return ledgers;
	}
	
	public boolean isSuspensePocket(Long pocketID)
	{
		if(pocketID!=null)
		{
			PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
			Pocket pocket = pocketDAO.getById(pocketID);
			if(pocket!=null)
			{
				PocketTemplate pocketTemplate = pocket.getPocketTemplate();

				return pocketTemplate.getIsSuspencePocket();
			}
		}
		return false;
	}
}
