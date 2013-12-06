/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.DAOConstants;
import com.mfino.constants.GeneralConstants;
import com.mfino.dao.query.BulkUploadQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author Raju
 */
public class BulkUploadDAO extends BaseDAO<BulkUpload> {

    public static final String ID_COLNAME = "ID";

    public List<BulkUpload> get(BulkUploadQuery query) {

        final String SUBSCRIBERMDNBYSOURCEMDNID = "SubscriberMDNFromSubscriberID";
        final String SUBSCRIBER_ASSOC_NAME = "Subscriber";


        Criteria criteria = createCriteria();

        if (query.getId() != null) {
            criteria.add(Restrictions.eq(ID_COLNAME, query.getId()));
        }
         if (query.getCompany() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUpload.FieldName_Company, query.getCompany()));
        }
        if (query.getFileType() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUpload.FieldName_BulkUploadFileType, query.getFileType()));
        }
        if (query.getFileStatus() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUpload.FieldName_BulkUploadDeliveryStatus, query.getFileStatus()));
        }
        if (query.getStartDate() != null) {
            criteria.add(Restrictions.gt(CmFinoFIX.CRBulkUpload.FieldName_CreateTime, query.getStartDate()));
        }
        if (query.getEndDate() != null) {
            criteria.add(Restrictions.lt(CmFinoFIX.CRBulkUpload.FieldName_CreateTime, query.getEndDate()));
        }

        if (query.getMdnID() != null) {

            final String subscriberAlias = SUBSCRIBER_ASSOC_NAME + DAOConstants.ALIAS_SUFFIX;
            criteria = criteria.createAlias(SUBSCRIBER_ASSOC_NAME, subscriberAlias);

            final String mdnAlias = SUBSCRIBERMDNBYSOURCEMDNID + DAOConstants.ALIAS_SUFFIX;
            criteria.createAlias(SUBSCRIBER_ASSOC_NAME + GeneralConstants.DOT_STRING + SUBSCRIBERMDNBYSOURCEMDNID, mdnAlias);

            final String mdnWithAlias = mdnAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRSubscriberMDN.FieldName_RecordID;
            criteria.add(Restrictions.eq(mdnWithAlias, query.getMdnID()));
            processColumn(query, CmFinoFIX.CRSubscriberMDN.FieldName_RecordID, mdnWithAlias);
        }

        if(query.getDeliveryDate() != null){
        	criteria.add(Restrictions.disjunction()
        			.add(Restrictions.isNull(CmFinoFIX.CRBulkUpload.FieldName_BulkUploadDeliveryDate))
        			.add(Restrictions.le(CmFinoFIX.CRBulkUpload.FieldName_BulkUploadDeliveryDate, query.getDeliveryDate())));
        }
        if (query.isAssociationOrdered()) {
            criteria.addOrder(Order.desc(ID_COLNAME));
        }
        if (query.getPaymentDate() != null) {
        	criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUpload.FieldName_PaymentDate, query.getPaymentDate()));
        }
		if (StringUtils.isNotBlank(query.getNameSearch())) {
			addLikeStartRestriction(criteria, CmFinoFIX.CRBulkUpload.FieldName_BulkUploadInFileName, query.getNameSearch());
		}
		if (query.getUserId() != null) {
			criteria.createAlias(CmFinoFIX.CRBulkUpload.FieldName_User, "user");
			criteria.add(Restrictions.eq("user."+CmFinoFIX.CRUser.FieldName_RecordID, query.getUserId()));
		}
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<BulkUpload> results = criteria.list();

        return results;
    }
    /**
     * Get the Bulk upload based on the Reverse SCTL Id
     * @param sctlId
     * @return
     */
    public BulkUpload getByReverseSCTLId(long sctlId) {
    	BulkUpload result = null;
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUpload.FieldName_ReverseSCTLID, sctlId));
    	
    	result = (BulkUpload)criteria.uniqueResult();
    	
    	return result;
    }
    
    /**
     * Get the Bulk upload based on the SCTL Id
     * @param sctlId
     * @return
     */
    public BulkUpload getBySCTLId(long sctlId) {
    	BulkUpload result = null;
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(CmFinoFIX.CRBulkUpload.FieldName_ServiceChargeTransactionLogID, sctlId));
    	
    	result = (BulkUpload)criteria.uniqueResult();
    	
    	return result;
    }
    
    @Override
    public void save(BulkUpload bu) {
        if (bu.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            bu.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(bu);
    }
}
