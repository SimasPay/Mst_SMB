/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.constants.QueryConstants;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 * @author Bala Sunku
 *
 */
public class ServiceChargeTransactionLogDAO extends BaseDAO<ServiceChargeTransactionLog> {
	
	/**
	 * Returns the Service Charge Transaction Log based on the Transaction Log Id.
	 * @param transactionLogId
	 * @return
	 */
	private ChannelCodeDAO channelDao = DAOFactory.getInstance().getChannelCodeDao();
	public ServiceChargeTransactionLog getByTransactionLogId(long transactionLogId) {
		ServiceChargeTransactionLog sctl = null;
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_TransactionID, transactionLogId));
		
		@SuppressWarnings("unchecked")
		List<ServiceChargeTransactionLog> lst = criteria.list();
		
		if (CollectionUtils.isNotEmpty(lst)) {
			sctl = lst.get(0);
		}
		return sctl;
	}
	
	/**
	 * Returns the List of Service charge Transaction Log entries with the given status
	 * @param status
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ServiceChargeTransactionLog> getByStatus(Integer[] status) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.in(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_SCTLStatus, status));
		addOrder(QueryConstants.DESC_STRING, CmFinoFIX.CRServiceChargeTransactionLog.FieldName_CreateTime, criteria);
		List<ServiceChargeTransactionLog> result = criteria.list();
		return result;
	}
	
		
    @Override
    public void save(ServiceChargeTransactionLog sctl) {
        if (sctl.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            sctl.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(sctl);
    }
    
    public List<ServiceChargeTransactionLog> get(ServiceChargeTransactionsLogQuery query){
    	Criteria criteria = createCriteria();
    		
    		if(query.getSourceChannelApplication()!=null){
    			Long channelID=channelDao.getByChannelSourceApplication(query.getSourceChannelApplication()).getID();
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_ChannelCodeID, channelID));
    		}
    		if(query.getDestMdn()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_DestMDN, query.getDestMdn()));
    		}
    		if(query.getSourceMdn()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_SourceMDN, query.getSourceMdn()));
    		}
    		if(StringUtils.isNotBlank(query.getSourceDestMdn())){
    			criteria.add(Restrictions.or(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_SourceMDN, query.getSourceDestMdn()),
    					Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_DestMDN, query.getSourceDestMdn())));
    		}
    		if(query.getSourceDestPartnerID()!=null){
    			criteria.add(Restrictions.or(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_SourcePartnerID, query.getSourceDestPartnerID()),
    					Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_DestPartnerID, query.getSourceDestPartnerID())));
    		}
    		if(query.getDestPartnerID()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_DestPartnerID, query.getDestPartnerID()));
    		}
    		if(query.getSourcePartnerID()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_SourcePartnerID, query.getSourcePartnerID()));
    		}
    		if(query.getBillerCode()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_MFSBillerCode, query.getBillerCode()));
    		}
    		if(query.getId()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_RecordID, query.getId()));
    		}
    		
    		if(query.getStatus()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_SCTLStatus, query.getStatus()));
    		} else if(query.getStatusList()!=null){
    			criteria.add(Restrictions.in(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_SCTLStatus, query.getStatusList()));
    		}
    		if(query.getTransationID()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_TransactionID, query.getTransationID()));
    		}
    		if(query.getTransferID()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_CommodityTransferID, query.getTransferID()));
    		}
    		if(query.getTransactionTypeIds()!=null && !query.getTransactionTypeIds().isEmpty()){
    			criteria.add(Restrictions.in(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_TransactionTypeID, query.getTransactionTypeIds()));
    	    	
    		}
    		if(query.getTransactionTypeID()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_TransactionTypeID, query.getTransactionTypeID()));
    		}
    		if(query.getServiceID()!=null){
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_ServiceID, query.getServiceID()));
    		}
    		if (query.getParentSCTLID() != null) {
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_ParentSCTLID, query.getParentSCTLID()));
    		}
    		if (query.getIntegrationTxnID()!= null) {
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_IntegrationTransactionID, query.getIntegrationTxnID()));
    		}
    		if (query.getAdjustmentStatus()!= null) {
    			criteria.createAlias(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_AdjustmentsFromSctlId, "adjustment");
    			criteria.add(Restrictions.eq("adjustment." + CmFinoFIX.CRAdjustments.FieldName_AdjustmentStatus, query.getAdjustmentStatus()));
    		}
    		if(query.getInfo1()!=null)
    		{
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_Info1, query.getInfo1()));
    		}
    		if(query.getParentIntegrationTransID()!=null)
    		{
    			criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_ParentIntegrationTransID, query.getParentIntegrationTransID()));
    		}
    		
    	  processBaseQuery(query, criteria);

          // Paging
          processPaging(query, criteria);

          if(query.isIDOrdered()) {
            criteria.addOrder(Order.desc(CmFinoFIX.CRActivitiesLog.FieldName_RecordID));
          }
          
          //applying Order
          applyOrder(query, criteria);
          @SuppressWarnings("unchecked")
          List<ServiceChargeTransactionLog> results = criteria.list();

          return results;
    	
    }
    		
	public List<ServiceChargeTransactionLog> getByParentSctlId(long parentSctlId) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_ParentSCTLID, parentSctlId));
		
		@SuppressWarnings("unchecked")
		List<ServiceChargeTransactionLog> lst = criteria.list();
		
		return lst;
	}
	public ServiceChargeTransactionLog getByIntegrationTransactionsID(long integrationTransactionID) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRServiceChargeTransactionLog.FieldName_IntegrationTransactionID, integrationTransactionID));
		
		@SuppressWarnings("unchecked")
		List<ServiceChargeTransactionLog> lst = criteria.list();
		
		if(criteria.list()==null||criteria.list().isEmpty())
			return null;
		return lst.get(0);
	}

}
