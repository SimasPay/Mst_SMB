package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.scheduler.service.EmoneyPocketRetireService;
import com.mfino.service.PendingCommodityTransferService;
import com.mfino.service.PocketService;

/**
 *
 * @author sunil
 */
@Service("EmoneyPocketRetireServiceImpl")
public class EmoneyPocketRetireServiceImpl implements EmoneyPocketRetireService{
	private static Logger log = LoggerFactory.getLogger(EmoneyPocketRetireServiceImpl.class);
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("PendingCommodityTransferServiceImpl")
	private PendingCommodityTransferService  pendingCommodityTransferService;

	public static void main(String[] args) {
		log.info("Start EMoney pocket retire tool");
		EmoneyPocketRetireServiceImpl emoneyPktRetireTool = new EmoneyPocketRetireServiceImpl();
		emoneyPktRetireTool.markRetiredPockets();
	}
   
	
	public void markRetiredPockets() {
		int start=0;
		int limit=10;

		log.info("Attempting to get all Pending Retired Pockets.");
			PocketQuery pocketQuery = new PocketQuery();
			pocketQuery.setPocketStatus(CmFinoFIX.PocketStatus_PendingRetirement);
			pocketQuery.setPocketType(CmFinoFIX.PocketType_SVA);
			pocketQuery.setCommodity(CmFinoFIX.Commodity_Money);
			pocketQuery.setLimit(limit);
			pocketQuery.setStart(start);

			List<Pocket> pendingRetiredPockets;
			do {
				pendingRetiredPockets = pocketService.get(pocketQuery);
				for (Pocket eachRtrdPkt : pendingRetiredPockets) {
					log.info("Get pocket: " + eachRtrdPkt.getID());
					if (eachRtrdPkt.getCurrentBalance() == null ||
							eachRtrdPkt.getCurrentBalance().compareTo(BigDecimal.ZERO) == 0) 
					{
						CommodityTransferQuery ctQuery = new CommodityTransferQuery();
						ctQuery.setSourceDestnPocket(eachRtrdPkt);
						ctQuery.setLimit(1);
						try {
							List<PendingCommodityTransfer> pcList = pendingCommodityTransferService.getByQuery(ctQuery);

							if (pcList.size() > 0) {
								log.info("Pocket (" + eachRtrdPkt.getID() + ") still has pending transactions");
								continue;
							} else {
								eachRtrdPkt.setStatus(CmFinoFIX.PocketStatus_Retired);
								pocketService.save(eachRtrdPkt);
								log.info("Pocket (" + eachRtrdPkt.getID() + ") is retired");
							}
						} catch (Exception e) {
							log.error("Exception while checking if the pocket has Pending Transactions", e);
						}
					}else{
						log.info("Pocket (" + eachRtrdPkt.getID() + ") still has balance");
					}
				}

				start += limit;
				pocketQuery.setStart(start);
			}while (pendingRetiredPockets.size() == limit);
		log.info("END Mark Retired Pockets");
	}

}

