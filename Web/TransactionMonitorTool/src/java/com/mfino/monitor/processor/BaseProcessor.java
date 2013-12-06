package com.mfino.monitor.processor;

import java.util.Date;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSLedgerDAO;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.TransactionTypeDAO;

/**
 * @author Srikanth
 *
 */

public abstract class BaseProcessor {
	
	public ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
	public TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
	public ChannelCodeDAO ccDAO= DAOFactory.getInstance().getChannelCodeDao();
	public ServiceDAO serviceDAO = DAOFactory.getInstance().getServiceDAO();
	public PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
	public PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
	public CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();
    public PendingCommodityTransferDAO pendingDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
    public MFSLedgerDAO ledgerDAO = DAOFactory.getInstance().getMFSLedgerDAO();
    public ChargeTxnCommodityTransferMapDAO ctMapDAO = DAOFactory.getInstance().getTxnTransferMap();
	
	public Date lastUpdateTimeGE;

	public Date getLastUpdateTimeGE() {
		return lastUpdateTimeGE;
	}

	public void setLastUpdateTimeGE(Date lastUpdateTimeGE) {
		this.lastUpdateTimeGE = lastUpdateTimeGE;
	}

	
	
}
