/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.BulkUploadFileEntryQuery;
import com.mfino.domain.BulkUploadFile;
import com.mfino.domain.BulkUploadFileEntry;

/**
 * @author Srikanth
 *
 */
public class BulkUploadFileEntryDAO extends BaseDAO<BulkUploadFileEntry> {    

    public List<BulkUploadFileEntry> get(BulkUploadFileEntryQuery query) {
        Criteria criteria = createCriteria();
        if(query.getUploadFileID() != null) {        	
        	criteria.createAlias(BulkUploadFileEntry.FieldName_BulkUploadFile, "b");
			criteria.add(Restrictions.eq("b." + BulkUploadFile.FieldName_RecordID, query.getUploadFileID()));
        }
        processPaging(query, criteria);
        criteria.addOrder(Order.asc(BulkUploadFileEntry.FieldName_RecordID));
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<BulkUploadFileEntry> results = criteria.list();
        return results;
    }
   
}
