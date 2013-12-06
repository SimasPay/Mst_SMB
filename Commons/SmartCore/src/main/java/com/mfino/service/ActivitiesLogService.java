/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service;

import java.util.List;

import com.mfino.dao.ActivitiesLogDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.ActivitiesLogQuery;
import com.mfino.domain.ActivitiesLog;

/**
 *
 * @author Venkata Krishna Teja D
 */
public class ActivitiesLogService extends BaseService<ActivitiesLog> {

    public static ActivitiesLog getRecordForThisParentTxnId(Long parentTxnId) {
        ActivitiesLogQuery activitiesLogQuery = new ActivitiesLogQuery();
        activitiesLogQuery.setParentTransactionId(parentTxnId);

        ActivitiesLogDAO activitiesLogDAO = DAOFactory.getInstance().getActivitiesLogDAO();
        List<ActivitiesLog> results = activitiesLogDAO.get(activitiesLogQuery);

        if (null == results || 0 == results.size()) {
            return null;
        }

        return results.get(0);
    }
}
