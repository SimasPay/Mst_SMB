/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author Venkata Krishna Teja D
 */
public class TransactionsLogQuery extends BaseQuery {
     
    private Long parentTransactionId;
    private Boolean isTransactionsWithNullParentTxnIdSearch;

    public void setTransactionsWithNullParentTxnIdSearch(Boolean isTransactionsWithNullParentTxnIdSearch) {
        this.isTransactionsWithNullParentTxnIdSearch = isTransactionsWithNullParentTxnIdSearch;
    }

    public Boolean isTransactionsWithNullParentTxnIdSearch() {
        return isTransactionsWithNullParentTxnIdSearch;
    }

    public void setParentTransactionId(Long parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
    }

    public Long getParentTransactionId() {
        return parentTransactionId;
    }
}
