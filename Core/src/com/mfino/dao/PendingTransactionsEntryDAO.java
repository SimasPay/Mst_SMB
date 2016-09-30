/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.PendingTransactionsEntryQuery;
import com.mfino.domain.PendingTxnsEntry;
import com.mfino.domain.PendingTxnsFile;

/**
 *
 * @author Raju
 */
public class PendingTransactionsEntryDAO extends BaseDAO<PendingTxnsEntry> {

    public static final String ID_COLNAME = "ID";

    public List<PendingTxnsEntry> get(PendingTransactionsEntryQuery query) {

        Criteria criteria = createCriteria();
        if (query.getId() != null) {
            criteria.add(Restrictions.eq(ID_COLNAME, query.getId()));
        }
        if (query.getPendingTransactionsFileID() != null) {
            criteria.add(Restrictions.eq(PendingTxnsEntry.FieldName_PendingTransactionsFileID, query.getPendingTransactionsFileID()));
        }
        if (query.getLineNumber() != null) {
            criteria.add(Restrictions.eq(PendingTxnsEntry.FieldName_PendingTransactionsLineNumber, query.getLineNumber()));
        }
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<PendingTxnsEntry> results = criteria.list();

        return results;
    }
    
    public int getProcessedLineCount(PendingTxnsFile pendingTransactionsFile) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(PendingTxnsEntry.FieldName_PendingTransactionsFileID, pendingTransactionsFile.getId()));
    	criteria.setProjection(Projections.max(PendingTxnsEntry.FieldName_PendingTransactionsLineNumber));
    	List list = criteria.list();
    	if(list.size() > 0) {
    		return (Integer) (list.get(0)==null?0:list.get(0)); //since projection gives some result it can be null too. Chking before returing the value. 
    	} else {
    		return 0;
    	}
    }
}
