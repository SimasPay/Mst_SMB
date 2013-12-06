/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service;

import java.util.Date;
import java.util.List;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.dao.query.TransactionsLogQuery;
import com.mfino.domain.TransactionsLog;

/**
 * @author Venkata Krishna Teja D
 */
public class TransactionsLogService extends BaseService<TransactionsLog> {

    public static List<TransactionsLog> getAllParentTxnsBetween(Date startDate, Date endDate) {
        // Step 1: Get all transactions logs btwn this dates with null parent txn id.
        TransactionsLogQuery transactionsLogQuery = new TransactionsLogQuery();
        transactionsLogQuery.setCreateTimeGE(startDate);
        transactionsLogQuery.setCreateTimeLT(endDate);
        transactionsLogQuery.setTransactionsWithNullParentTxnIdSearch(Boolean.TRUE);

        TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
        return transactionsLogDAO.get(transactionsLogQuery);
    }
    
    public static List<TransactionsLog> getAllTxnsBetween(Date startDate, Date endDate, int firstResult, int maxResults) {
      // Step 1: Get all transactions logs btwn this dates with null parent txn id.
      TransactionsLogQuery transactionsLogQuery = new TransactionsLogQuery();
      transactionsLogQuery.setCreateTimeGE(startDate);
      transactionsLogQuery.setCreateTimeLT(endDate);
      transactionsLogQuery.setStart(firstResult);
      transactionsLogQuery.setLimit(maxResults);
      
      TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
      return transactionsLogDAO.get(transactionsLogQuery);
   }

    public static List<TransactionsLog> getTxnsByParentTxnId(Long parentTransactionId) {
        TransactionsLogQuery transactionsLogQuery = new TransactionsLogQuery();
        transactionsLogQuery.setParentTransactionId(parentTransactionId);

        TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
        return transactionsLogDAO.get(transactionsLogQuery);
    }
}
