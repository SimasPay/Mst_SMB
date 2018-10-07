/**
 * 
 */
package com.mfino.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaJoinWalker;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.QueryConstants;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.Adjustments;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.ServiceChargeTxnLog;

/**
 * @author Bala Sunku
 *
 */
public class ServiceChargeTransactionLogDAO extends BaseDAO<ServiceChargeTxnLog> {
	
	/**
	 * Returns the Service Charge Transaction Log based on the Transaction Log Id.
	 * @param transactionLogId
	 * @return
	 */
	private ChannelCodeDAO channelDao = DAOFactory.getInstance().getChannelCodeDao();
	private static Logger log = LoggerFactory.getLogger(CommodityTransferDAO.class);
	 
	public ServiceChargeTxnLog getByTransactionLogId(long transactionLogId) {
		ServiceChargeTxnLog sctl = null;
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_TransactionID, transactionLogId));
		
		@SuppressWarnings("unchecked")
		List<ServiceChargeTxnLog> lst = criteria.list();
		
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
	public List<ServiceChargeTxnLog> getByStatus(Integer[] status) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.in(ServiceChargeTxnLog.FieldName_SCTLStatus, status));
		addOrder(QueryConstants.DESC_STRING, ServiceChargeTxnLog.FieldName_CreateTime, criteria);
		List<ServiceChargeTxnLog> result = criteria.list();
		return result;
	}
	
		
    @Override
    public void save(ServiceChargeTxnLog sctl) {
        if (sctl.getMfinoServiceProvider() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            MfinoServiceProvider msp = mspDao.getById(1);
            sctl.setMfinoServiceProvider(msp);
        }
        super.save(sctl);
    }
    
    public static String printQueryFromCriteria(Criteria criteria){
    	String sql=null;
    	try{
	    	CriteriaImpl criteriaImpl = (CriteriaImpl)criteria;
	    	SessionImplementor session = criteriaImpl.getSession();
	    	SessionFactoryImplementor factory = session.getFactory();
	    	CriteriaQueryTranslator translator=new CriteriaQueryTranslator(factory,criteriaImpl,criteriaImpl.getEntityOrClassName(),CriteriaQueryTranslator.ROOT_SQL_ALIAS);
	    	String[] implementors = factory.getImplementors( criteriaImpl.getEntityOrClassName() );
	
	    	CriteriaJoinWalker walker = new CriteriaJoinWalker((OuterJoinLoadable)factory.getEntityPersister(implementors[0]), 
	    	                        translator,
	    	                        factory, 
	    	                        criteriaImpl, 
	    	                        criteriaImpl.getEntityOrClassName(), 
	    	                        session.getLoadQueryInfluencers()   );
	
	    	sql=walker.getSQLString();
    	}catch(Exception e){
    		log.error("error",e);
    	}
    	return sql;
    }
    
    public List<ServiceChargeTxnLog> get(ServiceChargeTransactionsLogQuery query){
    	Criteria criteria = createCriteria();
    		
    		if(query.getSourceChannelApplication()!=null){
    			Long channelID=channelDao.getByChannelSourceApplication(query.getSourceChannelApplication()).getId();
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_ChannelCodeID, channelID));
    		}
    		if(query.getDestMdn()!=null){
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_DestMDN, query.getDestMdn()));
    		}
    		if(query.getSourceMdn()!=null){
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_SourceMDN, query.getSourceMdn()));
    		}
    		if(StringUtils.isNotBlank(query.getSourceDestMdn())){
    			criteria.add(Restrictions.or(Restrictions.eq(ServiceChargeTxnLog.FieldName_SourceMDN, query.getSourceDestMdn()),
    					Restrictions.eq(ServiceChargeTxnLog.FieldName_DestMDN, query.getSourceDestMdn())));
    		}
    		if(query.getSourceDestPartnerID()!=null){
    			criteria.add(Restrictions.or(Restrictions.eq(ServiceChargeTxnLog.FieldName_SourcePartnerID, query.getSourceDestPartnerID()),
    					Restrictions.eq(ServiceChargeTxnLog.FieldName_DestPartnerID, query.getSourceDestPartnerID())));
    		}
    		if(query.getDestPartnerID()!=null){
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_DestPartnerID, query.getDestPartnerID()));
    		}
    		if(query.getSourcePartnerID()!=null){
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_SourcePartnerID, query.getSourcePartnerID()));
    		}
    		if(query.getBillerCode()!=null){
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_MFSBillerCode, query.getBillerCode()));
    		}
    		if(query.getId()!=null){
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_RecordID, query.getId()));
    		}
    		
    		if(query.getStatus()!=null){
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_SCTLStatus, query.getStatus()));
    		} else if(query.getStatusList()!=null){
    			criteria.add(Restrictions.in(ServiceChargeTxnLog.FieldName_SCTLStatus, query.getStatusList()));
    		}
    		if(query.getTransationID()!=null){
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_TransactionID, query.getTransationID()));
    		}
    		if(query.getTransferID()!=null){
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_CommodityTransferID, query.getTransferID()));
    		}
    		if(query.getTransactionTypeIds()!=null && !query.getTransactionTypeIds().isEmpty()){
    			criteria.add(Restrictions.in(ServiceChargeTxnLog.FieldName_TransactionTypeID, query.getTransactionTypeIds()));
    	    	
    		}
    		if(query.getTransactionTypeID()!=null){
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_TransactionTypeID, query.getTransactionTypeID()));
    		}
    		if(query.getServiceID()!=null){
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_ServiceID, query.getServiceID()));
    		}
    		if (query.getParentSCTLID() != null) {
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_ParentSCTLID, query.getParentSCTLID()));
    		}
    		if (query.getIntegrationTxnID()!= null) {
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_IntegrationTransactionID, query.getIntegrationTxnID()));
    		}
    		if (query.getAdjustmentStatus()!= null) {
    			criteria.createAlias(ServiceChargeTxnLog.FieldName_AdjustmentsFromSctlId, "adjustment");
    			criteria.add(Restrictions.eq("adjustment." + Adjustments.FieldName_AdjustmentStatus, query.getAdjustmentStatus()));
    		}
    		if(query.getInfo1()!=null)
    		{
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_Info1, query.getInfo1()));
    		}
    		if(query.getParentIntegrationTransID()!=null)
    		{
    			criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_ParentIntegrationTransID, query.getParentIntegrationTransID()));
    		}
    		
    	  processBaseQuery(query, criteria);

          // Paging
          processPaging(query, criteria);

          if(query.isIDOrdered()) {
        	criteria.addOrder(Order.desc(ServiceChargeTxnLog.FieldName_CreateTime));//@kris
            criteria.addOrder(Order.desc(ActivitiesLog.FieldName_RecordID));
          }
          
          //applying Order
          applyOrder(query, criteria);
          
          log.info("@kris print SCTL query:"+printQueryFromCriteria(criteria));
          
          @SuppressWarnings("unchecked")
          List<ServiceChargeTxnLog> results = criteria.list();

          return results;
    	
    }
    		
	public List<ServiceChargeTxnLog> getByParentSctlId(long parentSctlId) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_ParentSCTLID, parentSctlId));
		
		@SuppressWarnings("unchecked")
		List<ServiceChargeTxnLog> lst = criteria.list();
		
		return lst;
	}
	public ServiceChargeTxnLog getByIntegrationTransactionsID(long integrationTransactionID) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_IntegrationTransactionID, integrationTransactionID));
		
		@SuppressWarnings("unchecked")
		List<ServiceChargeTxnLog> lst = criteria.list();
		
		if(criteria.list()==null||criteria.list().isEmpty())
			return null;
		return lst.get(0);
	}

	public List<ServiceChargeTxnLog> getSubscriberPendingTransactions(ServiceChargeTransactionsLogQuery query){
		
    	Criteria criteria = createCriteria();
    	
    	if(query.getSourceMdn()!=null){
    		
    		Criterion sourceCriteria = Restrictions.eq(ServiceChargeTxnLog.FieldName_SourceMDN, query.getSourceMdn());
        	Criterion destCriteria = Restrictions.eq(ServiceChargeTxnLog.FieldName_DestMDN, query.getSourceMdn());
        	
    		criteria.add(Restrictions.or(sourceCriteria , destCriteria));
    	}
    		
    	if(query.getStatus()!=null){
    		
    		criteria.add(Restrictions.eq(ServiceChargeTxnLog.FieldName_SCTLStatus, query.getStatus()));
    		
    	} else if(query.getStatusList()!=null){
    		
    		criteria.add(Restrictions.in(ServiceChargeTxnLog.FieldName_SCTLStatus, query.getStatusList()));
    	}
    		
    	processBaseQuery(query, criteria);

        // Paging
        processPaging(query, criteria);

        if(query.isIDOrdered()) {
           criteria.addOrder(Order.desc(ActivitiesLog.FieldName_RecordID));
        }
          
        //applying Order
        applyOrder(query, criteria);
        
        @SuppressWarnings("unchecked")
        List<ServiceChargeTxnLog> results = criteria.list();

        return results;
    	
    }
}
