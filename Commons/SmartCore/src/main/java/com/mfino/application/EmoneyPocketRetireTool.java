package com.mfino.application;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author sunil
 */
public class EmoneyPocketRetireTool {
	private static Logger log = LoggerFactory.getLogger(EmoneyPocketRetireTool.class);
	
    public static void main(String[] args) {
        log.info("Start EMoney pocket retire tool");
        EmoneyPocketRetireTool emoneyPktRetireTool = new EmoneyPocketRetireTool();
        emoneyPktRetireTool.markRetiredPockets();
    }

    private EmoneyPocketRetireTool() {
    }

    private void markRetiredPockets() {
        int start=0;
        int limit=10;

        log.info("Attempting to get all Pending Retired Pockets.");

        PocketQuery pocketQuery = new PocketQuery();
        pocketQuery.setPocketStatus(CmFinoFIX.PocketStatus_PendingRetirement);
        pocketQuery.setPocketType(CmFinoFIX.PocketType_SVA);
        pocketQuery.setCommodity(CmFinoFIX.Commodity_Money);
        pocketQuery.setLimit(limit);
        pocketQuery.setStart(start);

        HibernateUtil.getCurrentSession().beginTransaction();
        PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
        List<Pocket> pendingRetiredPockets;
        do {
            pendingRetiredPockets = pocketDAO.get(pocketQuery);
            for (Pocket eachRtrdPkt : pendingRetiredPockets) {
            	log.info("Get pocket: " + eachRtrdPkt.getID());
                if (eachRtrdPkt.getCurrentBalance() == null ||
                        eachRtrdPkt.getCurrentBalance().compareTo(BigDecimal.ZERO) == 0) 
                {
                    CommodityTransferQuery ctQuery = new CommodityTransferQuery();
                    ctQuery.setSourceDestnPocket(eachRtrdPkt);
                    ctQuery.setLimit(1);

                    PendingCommodityTransferDAO pcDao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
                    try {
                        List<PendingCommodityTransfer> pcList = pcDao.get(ctQuery);

                        if (pcList.size() > 0) {
                        	log.info("Pocket (" + eachRtrdPkt.getID() + ") still has pending transactions");
                            continue;
                        } else {
                            eachRtrdPkt.setStatus(CmFinoFIX.PocketStatus_Retired);
                            pocketDAO.save(eachRtrdPkt);
                            log.info("Pocket (" + eachRtrdPkt.getID() + ") is retired");
                        }
                    } catch (Exception exp) {
                        log.error("Exception while checking if the pocket has Pending Transactions", exp);
                    }
                }else{
                	log.info("Pocket (" + eachRtrdPkt.getID() + ") still has balance");
                }
            }
            
            start += limit;
            pocketQuery.setStart(start);
        }while (pendingRetiredPockets.size() == limit);
        
        HibernateUtil.getCurrentSession().getTransaction().commit();
    }
}

