/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;

import com.mfino.dao.query.AgentCashInTransactionQuery;
import com.mfino.domain.AgentCashinTxnLog;


public class AgentCashinTransactionLogDAO extends BaseDAO<AgentCashinTxnLog> {

    public List<AgentCashinTxnLog> get(AgentCashInTransactionQuery query) {
        Criteria criteria = createCriteria();

               
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<AgentCashinTxnLog> results = criteria.list();

        return results;
    }
    
}
