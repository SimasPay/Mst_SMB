/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.BulkBankAccountQuery;
import com.mfino.domain.BulkBankAccount;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author xchen
 */
public class BulkBankAccountDAO extends BaseDAO<BulkBankAccount> {

    public List<BulkBankAccount> get(BulkBankAccountQuery query) {
        Criteria criteria = createCriteria();

        if (query.getStartDate() != null) {
            criteria.add(Restrictions.disjunction()
                    .add(Restrictions.ge(BulkBankAccount.FieldName_CreateTime, query.getStartDate()))
                    .add(Restrictions.ge(BulkBankAccount.FieldName_LastUpdateTime, query.getStartDate())));
        }
        if (query.getEndDate() != null) {
            criteria.add(Restrictions.disjunction()
                    .add(Restrictions.ge(BulkBankAccount.FieldName_CreateTime, query.getEndDate()))
                    .add(Restrictions.ge(BulkBankAccount.FieldName_LastUpdateTime, query.getEndDate())));
        }

        if (null != query.getLastUpdateDateTime()) {
            criteria.add(Restrictions.eq(BulkBankAccount.FieldName_LastUpdateTime, query.getLastUpdateDateTime()));
        }

        if (query.isStatusUploadToBankOrCompleted() != null && query.isStatusUploadToBankOrCompleted()) {
            criteria.add(Restrictions.disjunction()
                    .add(Restrictions.eq(BulkBankAccount.FieldName_UploadFileStatus, CmFinoFIX.UploadFileStatus_Processed))
                    .add(Restrictions.eq(BulkBankAccount.FieldName_UploadFileStatus, CmFinoFIX.UploadFileStatus_Uploaded)));
        }

        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<BulkBankAccount> results = criteria.list();

        return results;
    }
}
