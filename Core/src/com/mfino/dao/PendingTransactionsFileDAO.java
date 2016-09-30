/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.dao.query.PendingTransactionsFileQuery;
import com.mfino.domain.PendingTxnsFile;
import com.mfino.fix.CmFinoFIX;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Raju
 */
public class PendingTransactionsFileDAO extends BaseDAO<PendingTxnsFile> {

    public List<PendingTxnsFile> get(PendingTransactionsFileQuery query) {
        Criteria criteria = createCriteria();
        
        if(query.getCompany()!=null){
            criteria.add(Restrictions.eq(PendingTxnsFile.FieldName_Company, query.getCompany()));
        }
        processPaging(query, criteria);
        criteria.addOrder(Order.desc(PendingTxnsFile.FieldName_RecordID));        
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<PendingTxnsFile> results = criteria.list();
        return results;
    }

    public List<PendingTxnsFile> getPendingUploadedFiles() {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(PendingTxnsFile.FieldName_UploadFileStatus, CmFinoFIX.UploadFileStatus_Uploaded));
        //criteria.add(Restrictions.eq(PendingTxnsFile.FieldName_RecordType, CmFinoFIX.RecordType_Bulkresolve));
        @SuppressWarnings("unchecked")
        List<PendingTxnsFile> results = criteria.list();
        return results;
    }
    
    public List<PendingTxnsFile> getProcessingFiles() {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(PendingTxnsFile.FieldName_UploadFileStatus, CmFinoFIX.UploadFileStatus_Processing));
        //criteria.add(Restrictions.eq(PendingTxnsFile.FieldName_RecordType, CmFinoFIX.RecordType_Bulkresolve));
        @SuppressWarnings("unchecked")
        List<PendingTxnsFile> results = criteria.list();
        return results;
    }
}
