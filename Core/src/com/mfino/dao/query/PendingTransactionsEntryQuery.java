/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author Raju
 */
public class PendingTransactionsEntryQuery extends BaseQuery{
Long PendingTransactionsFileID;
Integer LineNumber;

    public Integer getLineNumber() {
        return LineNumber;
    }

    public void setLineNumber(Integer LineNumber) {
        this.LineNumber = LineNumber;
    }
    public Long getPendingTransactionsFileID() {
        return PendingTransactionsFileID;
    }

    public void setPendingTransactionsFileID(Long PendingTransactionsFileID) {
        this.PendingTransactionsFileID = PendingTransactionsFileID;
    }
}