/**
 * 
 */
package com.mfino.dao;

import com.mfino.domain.BulkUploadFile;
import com.mfino.dao.query.BulkUploadFileQuery;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * @author Deva
 *
 */
public class BulkUploadFileDAO extends BaseDAO<BulkUploadFile> {

    public static final String ID_COLNAME = "ID";

    public List<BulkUploadFile> get(BulkUploadFileQuery query) {

        Criteria criteria = createCriteria();

        if (query.getId() != null) {
            criteria.add(Restrictions.eq(ID_COLNAME, query.getId()));
        }
        if (query.getRecordType() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUploadFile.FieldName_RecordType, query.getRecordType()));
        }
        if (query.getUploadStatus() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUploadFile.FieldName_UploadFileStatus, query.getUploadStatus()));
        }
        if (query.getStartDate() != null) {
            criteria.add(Restrictions.gt(CmFinoFIX.CRBulkUploadFile.FieldName_CreateTime, query.getStartDate()));
            criteria.add(Restrictions.le(CmFinoFIX.CRBulkUploadFile.FieldName_CreateTime, query.getEndDate()));
        }
        if (query.getUploadFileStatusSearch() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUploadFile.FieldName_UploadFileStatus, query.getUploadFileStatusSearch()));
        }
        if(query.getCompany()!=null){
            criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUploadFile.FieldName_Company,query.getCompany()));
        }
        if (query.isAssociationOrdered()) {
            criteria.addOrder(Order.desc(ID_COLNAME));
        }
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<BulkUploadFile> results = criteria.list();

        return results;
    }

    /**
     * @return
     */
    public List<BulkUploadFile> getPendingFiles() {
        Criteria criteria = createCriteria();
        Integer [] statusList = { CmFinoFIX.UploadFileStatus_Uploaded, CmFinoFIX.UploadFileStatus_Processing };
        criteria.add(Restrictions.in(CmFinoFIX.CRBulkUploadFile.FieldName_UploadFileStatus, statusList));
        criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUploadFile.FieldName_UploadFileStatus, CmFinoFIX.UploadFileStatus_Uploaded));
        @SuppressWarnings("unchecked")
        List<BulkUploadFile> results = criteria.list();

        return results;
    }
}
