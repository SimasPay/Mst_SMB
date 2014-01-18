/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX.CMGetTransactions;
import com.mfino.result.Result;

/**
 * @author Sreenath
 *
 */
public interface CommodityTransferService {

	/**
	 * Retrieves and adds the details of the commoditytransfer using it's id to
	 * set it in result.This commoditytransfer is used in result.render() to
	 * send the details as xml.If reponse from multix is says not successful we
	 * will just add timestamp to the result and
	 * getDetailsofPresentTransaction() returns null
	 * 
	 * @author Gurram Karthik
	 * 
	 * @param result
	 *            returned by createResult()
	 */
	public void addCommodityTransferToResult(Result result);
	
	public void addCommodityTransferToResult(Result result, Long commodityTransferId);
	
	/**
	 * Returns a list of ct entries to create the transaction history.The no of items of list is based on a system paramter
	 * Max_txn_count_in_history
	 * @param pocket
	 * @param subscriberMDN
	 * @param transactionsHistory
	 * @return
	 * @throws Exception
	 */
	public List<CommodityTransfer> getTranscationsHistory(Pocket pocket, SubscriberMDN subscriberMDN, CMGetTransactions transactionsHistory) throws Exception;

	/**
	 * 
	 * @param pocket
	 * @param subscriberMDN
	 * @param transactionsHistory
	 * @return
	 * @throws Exception
	 */
	public Long getTranscationsCount(Pocket pocket, SubscriberMDN subscriberMDN, CMGetTransactions transactionsHistory) throws Exception;

	/**
	 * Gets the Commodity transfer record by the commodity transfer id
	 * @param commodityTransferId
	 * @return
	 */
	public CommodityTransfer getCommodityTransferById(Long commodityTransferId);
	
	public List<CommodityTransfer>  get(CommodityTransferQuery query) throws Exception;

	

}
