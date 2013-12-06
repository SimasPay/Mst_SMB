/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;

import com.mfino.dao.query.AgentCashInTransactionQuery;
import com.mfino.domain.AgentCashInTransactions;


public class AgentCashinTransactionLogDAO extends BaseDAO<AgentCashInTransactions> {

    public List<AgentCashInTransactions> get(AgentCashInTransactionQuery query) {
        Criteria criteria = createCriteria();

               
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<AgentCashInTransactions> results = criteria.list();

        return results;
    }
    
}
