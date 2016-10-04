/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.RetiredCardPANInfoQuery;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.RetiredCardPANInfo;
import com.mfino.fix.CmFinoFIX;
import com.mfino.scheduler.service.PocketRetireService;
import com.mfino.service.PendingCommodityTransferService;
import com.mfino.service.PocketService;
import com.mfino.service.RetiredCardPANInfoService;

/**
 * @author Bala Sunku
 *
 */
@Service("PocketRetireServiceImpl")
public class PocketRetireServiceImpl  implements PocketRetireService {
	private static final Logger log = LoggerFactory.getLogger(PocketRetireServiceImpl.class);
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("RetiredCardPANInfoServiceImpl")
	private RetiredCardPANInfoService retiredCardPANInfoService;
	
	@Autowired
	@Qualifier("PendingCommodityTransferServiceImpl")
	private PendingCommodityTransferService pendingCommodityTransferService;
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	/**
	 * Change the status of the pending retired pockets to Retired status.
	 */
	
	public void retirePockets() {
		log.info("BEGIN retirePockets");
			PocketQuery pocketQuery = new PocketQuery();
			pocketQuery.setPocketStatus(CmFinoFIX.PocketStatus_PendingRetirement);
			List<Pocket> lstPockets = pocketService.get(pocketQuery);
			
			if (CollectionUtils.isNotEmpty(lstPockets)) {
				for (Pocket p: lstPockets) {
					try {
						log.info("Checking the status of the pocket with cardPan " + p.getCardpan());
						checkNChangePocketStatus(p);
					} catch (Exception e) {
						log.error("Error: while checking the pocket with cardPan " + p.getCardpan() + e.getMessage(), e);
					}
				}
			}
		log.info("END retirePockets");
	}
	
	
	private void checkNChangePocketStatus(Pocket p) {
		if (p.getCurrentbalance() != null && BigDecimal.ZERO.compareTo(new BigDecimal(p.getCurrentbalance())) == 0) {
			
            CommodityTransferQuery ctQuery = new CommodityTransferQuery();
            ctQuery.setSourceDestnPocket(p);
            ctQuery.setLimit(1);

            try {
                List<PendingCommodityTransfer> pcList = pendingCommodityTransferService.getByQuery(ctQuery);

                if (CollectionUtils.isNotEmpty(pcList)) {
                	log.info("Pocket with cardPan " + p.getCardpan() + " can not be retired because, it still has pending transactions");
                } 
                else {
                	int timesRetired = 0;
					String cardPan = p.getCardpan();
					String cardPanStringToReplace;
					if (StringUtils.isNotBlank(cardPan)) {
					timesRetired = getTimesRetiredForThisCardPan(cardPan);
					cardPanStringToReplace = cardPan + "R" + timesRetired;
                	p.setCardpan(cardPanStringToReplace);
                    p.setStatus(CmFinoFIX.PocketStatus_Retired);
                    p.setIsdefault(CmFinoFIX.Boolean_False);
                    try{
                    pocketService.save(p);
                    }catch(ConstraintViolationException e){
							//Handles already existing duplicate card pans insertion, Scheduler picks it in next cycle
							log.info("Handling Constraint violation Exception Occured: " + e );
							if (StringUtils.isNotBlank(cardPan)) {
								timesRetired=timesRetired+1;
								updateCardPANInfo(cardPan, timesRetired);
								throw e;
							}
						}
						updateCardPANInfo(cardPan, timesRetired+1);
                    log.info("Pocket with cardPan " + p.getCardpan() + " is retired");
                }
                }
            } catch (Exception e) {
                log.error("Exception while checking if the pocket with cardPan " + p.getCardpan() + " has Pending Transactions", e);
            }
		}
		else {
			log.info("Pocket with cardPan " + p.getCardpan() + " can not be retired because the balance is not zero");
		}
	}
	
		/**
		 * Gets the number of times a cardpan is retired.
		 * @param cardPan
		 * @return
		 */
		private int getTimesRetiredForThisCardPan(String cardPan) {
			RetiredCardPANInfoQuery query = new RetiredCardPANInfoQuery();
			query.setCardPan(cardPan);

			List<RetiredCardPANInfo> results = retiredCardPANInfoService.get(query);
			int timesRetired = 0;
			if(results.size() > 0){
				RetiredCardPANInfo retiredCardPANInfo = results.get(0);
				if(retiredCardPANInfo != null){
					timesRetired = (int)retiredCardPANInfo.getRetirecount();    		
				}
			}

			return timesRetired;    	
		}

		/**
		 * Updates the retired_cardpan_info with the number of times a cardpan is retired.
		 * @param cardPan
		 * @param timesRetired
		 * @return
		 */
		private void updateCardPANInfo(String cardPan, int timesRetired) {
			RetiredCardPANInfoQuery query = new RetiredCardPANInfoQuery();
			query.setCardPan(cardPan);

			List<RetiredCardPANInfo> results = retiredCardPANInfoService.get(query);

			if(results.size() > 0){
				RetiredCardPANInfo retiredCardPANInfo = results.get(0);
				if(retiredCardPANInfo != null){
					retiredCardPANInfo.setRetirecount(timesRetired);
					retiredCardPANInfoService.save(retiredCardPANInfo);
				}
			}
			else{
				RetiredCardPANInfo retiredCardPANInfo = new RetiredCardPANInfo();
				retiredCardPANInfo.setCardpan(cardPan);
				retiredCardPANInfo.setRetirecount(timesRetired);
				retiredCardPANInfoService.save(retiredCardPANInfo);
			}
		}

}
