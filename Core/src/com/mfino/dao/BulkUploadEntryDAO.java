/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.dao.query.BulkUploadEntryQuery;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Raju
 */
public class BulkUploadEntryDAO extends BaseDAO<BulkUploadEntry> {

    public List<BulkUploadEntry> get(BulkUploadEntryQuery query) throws Exception {
        Criteria criteria = createCriteria();
        if (query.getId() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUploadEntry.FieldName_BulkUploadID, query.getId()));
            
        }
        if (query.getBulkUploadLineNumber() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUploadEntry.FieldName_BulkUploadLineNumber, query.getBulkUploadLineNumber()));
            
        }
        if (query.getStatus() != null) {
        	criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUploadEntry.FieldName_TransferStatus, query.getStatus()));
        }
        if (query.getIsUnRegistered() != null && query.getIsUnRegistered().booleanValue()) {
        	criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUploadEntry.FieldName_IsUnRegistered, query.getIsUnRegistered()));
        }
        if (query.getUploadLineNumbers() != null) {
        	criteria.add(Restrictions.in(CmFinoFIX.CRBulkUploadEntry.FieldName_BulkUploadLineNumber, query.getUploadLineNumbers()));
        }
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<BulkUploadEntry> results = criteria.list();
        return results;
    }
    
    public BulkUploadEntry getBySCTLId(long sctlId) {
    	BulkUploadEntry result = null;
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUploadEntry.FieldName_ServiceChargeTransactionLogID, sctlId));
    	
    	result = (BulkUploadEntry)criteria.uniqueResult();
    	
    	return result;
    }
    
    public List<BulkUploadEntry> getBulkUploadEntriesForBulkUpload(Long BulkUploadId)
    {
    	Criteria criteria = createCriteria();
    	if (BulkUploadId != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUploadEntry.FieldName_BulkUploadID, BulkUploadId));   
        }
    	List<BulkUploadEntry> results = criteria.list();
        return results;
    	
    }
}
