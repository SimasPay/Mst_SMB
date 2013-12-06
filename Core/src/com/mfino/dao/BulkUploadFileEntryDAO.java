/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.BulkUploadFileEntryQuery;
import com.mfino.domain.BulkUploadFileEntry;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Srikanth
 *
 */
public class BulkUploadFileEntryDAO extends BaseDAO<BulkUploadFileEntry> {    

    public List<BulkUploadFileEntry> get(BulkUploadFileEntryQuery query) {
        Criteria criteria = createCriteria();
        if(query.getUploadFileID() != null) {        	
        	criteria.createAlias(CmFinoFIX.CRBulkUploadFileEntry.FieldName_BulkUploadFile , "b");
			criteria.add(Restrictions.eq("b." + CmFinoFIX.CRBulkUploadFile.FieldName_RecordID, query.getUploadFileID()));
        }
        processPaging(query, criteria);
        criteria.addOrder(Order.asc(CmFinoFIX.CRBulkUploadFileEntry.FieldName_RecordID));
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<BulkUploadFileEntry> results = criteria.list();
        return results;
    }
   
}
