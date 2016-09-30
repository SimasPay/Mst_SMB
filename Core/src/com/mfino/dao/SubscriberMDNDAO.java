/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.KYCLevelQuery;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.KYCLevel;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;

/**
 *
 * @author sandeepjs
 */
public class SubscriberMDNDAO extends BaseDAO<SubscriberMdn> {

    public static final String SUBSCRIBER_TABLE_NAME = "Subscriber";
    
/*    public SubscriberMdn getByMDN(String MDN) {
        SubscriberMdnQuery query = new SubscriberMdnQuery();
        query.setExactMDN(MDN);
        query.setStart(0);
        query.setLimit(1);
        List<SubscriberMdn> subs = get(query);
        if (subs.size() > 0) {
            return subs.get(0);
        } else {
            return null;
        }
    }*/

    public SubscriberMdn getByMDN(String MDN, LockMode lockMode) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(SubscriberMdn.FieldName_MDN, MDN));
    	
        if(lockMode != null){
            criteria.setLockMode(lockMode);
        }
        
    	List<SubscriberMdn> subscribersList = criteria.list();
    	
    	if((null != subscribersList) && (subscribersList.size() > 0)){
    		return subscribersList.get(0);
    	}
    	
    	return null;
    }

    
    public SubscriberMdn getByMDN(String MDN) {
    	return getByMDN(MDN, null);
    }

    public List<SubscriberMdn> get(SubscriberMdnQuery query)
    {
    	return get(query,null);
    }
    
    public List<SubscriberMdn> get(SubscriberMdnQuery query, LockMode lockmode) {

        Criteria criteria = createCriteria();

        if (query.getBankCode() != null) {

            String countString = "select count(distinct mdn)";
            String mdnString = "select distinct mdn";
            String orderString = " order by mdn";
            StringBuffer queryString = new StringBuffer();
            
            queryString.append(" from Subscriber s, SubscriberMdn mdn, Pocket p, PocketTemplate pt, KYCLevel k ");
            queryString.append("where s.id = mdn.Subscriber and mdn.id=p.SubscriberMDNByMDNID and pt.id=p.PocketTemplate and pt.BankCode= :bankcode");
            
            if (StringUtils.isNotBlank(query.getFirstName())) {
            	queryString.append(" and s.FirstName = :firstName");
            }
            if (StringUtils.isNotBlank(query.getLastName())) {
            	queryString.append(" and s.LastName = :lastName");
            }
            if (StringUtils.isNotBlank(query.getMdn())) {
            	queryString.append(" and mdn.MDN = :mdn");
            }
            if (query.getStartRegistrationDate() != null) {
            	queryString.append(" and mdn.CreateTime >= :startTime");
            }
            if (query.getEndRegistrationDate() != null) {
            	queryString.append(" and mdn.CreateTime < :endTime");
            }
            
            if(null != query.getKycLevelId()) {
        		
            	queryString.append(" and k.KYCLevel = :kycLevel and s.KYCLevel = k.KYCLevel");
            }
            
            // This query is for getting total number of results i.e count(distinct mdn)
            Query newQuery = getSession().createQuery(countString + queryString + orderString); // .addEntity("SubscriberMdn", SubscriberMdn.class);
            newQuery.setInteger("bankcode", query.getBankCode());

            if (StringUtils.isNotBlank(query.getFirstName())) {
                newQuery.setString("firstName", query.getFirstName());
            }
            if (StringUtils.isNotBlank(query.getLastName())) {
                newQuery.setString("lastName", query.getLastName());
            }
            if (StringUtils.isNotBlank(query.getMdn())) {
                newQuery.setString("mdn", query.getMdn());
            }
            if (query.getStartRegistrationDate() != null) {
                newQuery.setTimestamp("startTime", query.getStartRegistrationDate());
            }
            if (query.getEndRegistrationDate() != null) {
                newQuery.setTimestamp("endTime", query.getEndRegistrationDate());
            }
            
            if(null != query.getKycLevelId()) {
            	
                newQuery.setLong("kycLevel", query.getKycLevelId());
            }

//            Integer count = (Integer) newQuery.uniqueResult();
//            query.setTotal(count);
//            newQuery.setResultTransformer(Criteria.ROOT_ENTITY);

            Long count = (Long) newQuery.list().get(0);
            int total = count.intValue();
            query.setTotal(total);

//            List<SubscriberMdn> list = new ArrayList<SubscriberMdn>();
            // if total count is zero then returning list
            if(total == 0) {
                return new ArrayList<SubscriberMdn>();
            }

            // This query is for getting total results as a mdn list
            newQuery = getSession().createQuery(mdnString + queryString + orderString); // .addEntity("SubscriberMdn", SubscriberMdn.class);
            newQuery.setInteger("bankcode", query.getBankCode());

            if (StringUtils.isNotBlank(query.getFirstName())) {
                newQuery.setString("firstName", query.getFirstName());
            }
            if (StringUtils.isNotBlank(query.getLastName())) {
                newQuery.setString("lastName", query.getLastName());
            }
            if (StringUtils.isNotBlank(query.getMdn())) {
                newQuery.setString("mdn", query.getMdn());
            }
            if (query.getStartRegistrationDate() != null) {
                newQuery.setTimestamp("startTime", query.getStartRegistrationDate());
            }
            if (query.getEndRegistrationDate() != null) {
                newQuery.setTimestamp("endTime", query.getEndRegistrationDate());
            }
            if(query.getStart() != null && query.getLimit() != null) {
                newQuery.setFirstResult(query.getStart());
                newQuery.setMaxResults(query.getLimit());
            }
            
            if(null != query.getKycLevelId()) {
            	
                newQuery.setLong("kycLevel", query.getKycLevelId());
            }
            
            @SuppressWarnings("unchecked")
            List<SubscriberMdn> list = newQuery.list();
            return list;
        }
        if (query.getExactMDN() != null) {
            criteria.add(Restrictions.eq(SubscriberMdn.FieldName_MDN, query.getExactMDN()));
        }

        if (query.getId() != null) {
            criteria.add(Restrictions.eq(SubscriberMdn.FieldName_RecordID, query.getId()));
        }
        
        if (query.getMdn() != null && query.getMdn().length() > 0) {
            addLikeStartRestriction(criteria, SubscriberMdn.FieldName_MDN, query.getMdn());
        }

        if (query.getStartRegistrationDate() != null) {
            criteria.add(Restrictions.gt(SubscriberMdn.FieldName_CreateTime, query.getStartRegistrationDate()));
        }
        if (query.getEndRegistrationDate() != null) {
            //This is to make today's records return to the user.
            //Date endDatePlus1 = DateUtil.addDays(query.getEndRegistrationDate(), 1);
            criteria.add(Restrictions.lt(SubscriberMdn.FieldName_CreateTime, query.getEndRegistrationDate()));
        }

        if (query.getStatusTimeGE() != null) {
            criteria.add(Restrictions.ge(SubscriberMdn.FieldName_StatusTime, query.getStatusTimeGE()));
        }

        if (query.getStatusTimeLT() != null) {
            criteria.add(Restrictions.lt(SubscriberMdn.FieldName_StatusTime, query.getStatusTimeLT()));
        }

        if (true == query.isSubscriberMDNStatusRetire()) {
            criteria.add(Restrictions.eq(SubscriberMdn.FieldName_MDNStatus, CmFinoFIX.MDNStatus_Retired));
        }

        Integer[] statuses = query.getStatusIn();
        if (null != statuses && statuses.length > 0) {
            criteria.add(Restrictions.in(SubscriberMdn.FieldName_MDNStatus, statuses));
        }

        if (query.isMDNNotRecycled()) {
            criteria.add(Restrictions.disjunction().add(Restrictions.eq(SubscriberMdn.FieldName_IsMDNRecycled, new Boolean(false))).add(Restrictions.isNull(SubscriberMdn.FieldName_IsMDNRecycled)));
        }

        Integer statusEQ = query.getStatusEQ();
        if (null != statusEQ) {
            criteria.add(Restrictions.eq(SubscriberMdn.FieldName_MDNStatus, statusEQ));
        }

        Integer statusNE = query.getStatusNE();
        if (null != statusNE) {
            criteria.add(Restrictions.ne(SubscriberMdn.FieldName_MDNStatus, statusNE));
        }
        
        KYCLevel kycLevel = null;
        if(null != query.getKycLevelId()) {
        	
        	KYCLevelDAO kycLevelDao = DAOFactory.getInstance().getKycLevelDAO();
        	
        	KYCLevelQuery kycQuery = new KYCLevelQuery();
        	kycQuery.setId(query.getKycLevelId());
        	//kycQuery.set_KycLevel(query.getKycLevelId().intValue());
        	
        	List<KYCLevel> kycResults = kycLevelDao.get(kycQuery);
        	
        	if(kycResults != null && kycResults.size() > 0) {
        		
        		kycLevel = kycResults.get(0);
        	}
        }
        
        final String subcriberTableNameAlias = SUBSCRIBER_TABLE_NAME + DAOConstants.ALIAS_SUFFIX;
        final String firstNameWithAlias = subcriberTableNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + Subscriber.FieldName_FirstName;
        final String lastNameWithAlias = subcriberTableNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + Subscriber.FieldName_LastName;
        final String companyIDAlias = subcriberTableNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + Subscriber.FieldName_Company;
        final String onlySubscribersAlias = subcriberTableNameAlias+ DAOConstants.ALIAS_COLNAME_SEPARATOR + Subscriber.FieldName_SubscriberType;
        final String subscriberState = subcriberTableNameAlias+ DAOConstants.ALIAS_COLNAME_SEPARATOR + Subscriber.FieldName_UpgradeState;
        final String kycLevelAlias = subcriberTableNameAlias+ DAOConstants.ALIAS_COLNAME_SEPARATOR + Subscriber.FieldName_KYCLevelByKYCLevel;
        
        criteria.createAlias(SUBSCRIBER_TABLE_NAME, subcriberTableNameAlias);
        processColumn(query, Subscriber.FieldName_FirstName, firstNameWithAlias);
        processColumn(query, Subscriber.FieldName_LastName, lastNameWithAlias);
        processColumn(query, Subscriber.FieldName_Company, companyIDAlias);
        processColumn(query, Subscriber.FieldName_SubscriberType, onlySubscribersAlias);
        processColumn(query, Subscriber.FieldName_UpgradeState, subscriberState);
        processColumn(query, Subscriber.FieldName_KYCLevel, kycLevelAlias);
       
        if (query.getFirstName() != null || query.getLastName() != null 
        		|| query.getCompany() != null || query.isOnlySubscribers()
        		|| query.getState()!=null || null != query.getKycLevelId()) {

            //criteria.createAlias(SUBSCRIBER_TABLE_NAME, subcriberTableNameAlias);

            if (query.getFirstName() != null && query.getFirstName().length() > 0) {
                addLikeStartRestriction(criteria, firstNameWithAlias, query.getFirstName());
                //processColumn(query, Subscriber.FieldName_FirstName, firstNameWithAlias);
            }

            if (query.getLastName() != null && query.getLastName().length() > 0) {
                addLikeStartRestriction(criteria, lastNameWithAlias, query.getLastName());
                //processColumn(query, Subscriber.FieldName_LastName, lastNameWithAlias);
            }
            if (query.getCompany() != null) {
                criteria.add(Restrictions.eq(companyIDAlias, query.getCompany()));
            }
            if(query.isOnlySubscribers()){
            	criteria.add(Restrictions.eq(onlySubscribersAlias, CmFinoFIX.SubscriberType_Subscriber));
            }
            if(query.getState()!=null){
            	criteria.add(Restrictions.eq(subscriberState, query.getState()));
            }
            
            if(null != query.getKycLevelId()) {
            	
            	criteria.add(Restrictions.eq(kycLevelAlias, kycLevel));
            }
        }
        if(StringUtils.isNotBlank(query.getAccountNumber())){
        	criteria.createAlias(SubscriberMdn.FieldName_PocketFromMDNID, "pocket");
        	criteria.add(Restrictions.eq("pocket."+Pocket.FieldName_CardPAN, query.getAccountNumber()));
          }
        if (query.getVersion() != null) {
            criteria.add(Restrictions.eq("Version", query.getVersion()));
        }

        if(null != query.getIsForceCloseRequested()) {
        	criteria.add(Restrictions.eq(SubscriberMdn.FieldName_IsForceCloseRequested, query.getIsForceCloseRequested()));
        }

        // Paging
		if (StringUtils.isBlank(query.getFirstName())
				&& StringUtils.isBlank(query.getLastName())
				&& StringUtils.isBlank(query.getMdn())
				&& StringUtils.isBlank(query.getExactMDN())
				&& query.getState()==null
				&& query.getStartRegistrationDate() == null
				&& query.getEndRegistrationDate() == null
				&& StringUtils.isBlank(query.getAccountNumber())
				&& query.getStatusEQ() == null
				&& query.getKycLevelId() == null) {
            // If we reach here then we have Default Search.
            // For paging use the processPaging locally without the use of inner
            // join.
            if (query.getStart() != null && query.getLimit() != null) {
                String queryStr = "select count(*) from Subscriber where Company = :company and Type = :subscriberType";

                Query newQuery = getQuery(queryStr);
                newQuery.setEntity("company", query.getCompany());
                newQuery.setInteger("subscriberType", CmFinoFIX.SubscriberType_Subscriber);
                Long count = (Long) newQuery.uniqueResult();
                query.setTotal(count.intValue());
                criteria.setResultTransformer(Criteria.ROOT_ENTITY);

                criteria.setFirstResult(query.getStart());
                criteria.setMaxResults(query.getLimit());
            }
        } else {
            processPaging(query, criteria);
        }

        //applying Order for columns which are not part of criteria
        //criteria.addOrder(Order.asc(firstNameWithAlias)).addOrder(Order.asc(lastNameWithAlias)).addOrder(Order.asc(SubscriberMdn.FieldName_MDN));
        if (query.isAssociationOrdered()) {
            if (StringUtils.isNotBlank(query.getMdn())) {
                criteria.addOrder(Order.asc(SubscriberMdn.FieldName_MDN));
            } else if (StringUtils.isNotBlank(query.getLastName())) {
                criteria.addOrder(Order.asc(lastNameWithAlias));
                criteria.addOrder(Order.asc(SubscriberMdn.FieldName_RecordID));
            } else if (StringUtils.isNotBlank(query.getFirstName())) {
                criteria.addOrder(Order.asc(firstNameWithAlias));
                criteria.addOrder(Order.asc(SubscriberMdn.FieldName_RecordID));
            }
        }

        if (query.isIDOrdered()) {
            criteria.addOrder(Order.asc(SubscriberMdn.FieldName_RecordID));
        }

        applyOrder(query, criteria);
        if(lockmode!=null)
           criteria.setLockMode(lockmode);
        @SuppressWarnings("unchecked")
        List<SubscriberMdn> results = criteria.list();

        return results;
    }

    public Integer getNonRetiredCount(Date end, Long companyID) {
        String sqlQuery = "SELECT count(1) FROM subscriber_mdn mdn join subscriber sub on mdn.subscriberid=sub.id where sub.companyid=:companyID and mdn.status != :retiredStatus";
        Query queryObj = getSQLQuery(sqlQuery);
        queryObj.setLong("companyID", companyID);
        queryObj.setInteger("retiredStatus", CmFinoFIX.SubscriberStatus_Retired);

       @SuppressWarnings("unchecked")
        List<Object[]> list = queryObj.list();
        if (list.size() > 0) {
            Object object = list.get(0);
            BigInteger count = (BigInteger) object;
            return count.intValue();
        }
        return 0;
    }

	@SuppressWarnings("unchecked")
	public  List<SubscriberMdn> getDeactivatedMdns(Date expireDate) {
		 List<SubscriberMdn> result = null;
		if(expireDate!=null){
		 Criteria criteria = createCriteria();
		 criteria.add(Restrictions.disjunction()
				 .add(Restrictions.isNull(SubscriberMdn.FieldName_LastTransactionTime))
				 .add(Restrictions.le(SubscriberMdn.FieldName_LastTransactionTime, expireDate)));
		 result=criteria.list();
		}
		 return result ;
	}
	
	/** 
	 * @param time in miliseconds
	 * @return
	 * 
	 *  -subscriber_type = partner && activation_time = null && upgradestate=UpgradeState_Approved
	 *	 - current time - approve or reject time > TIME_TO_SUSPEND_OF_NO_ACTIVATION &&
	 *	 -current time - status time > TIME_TO_SUSPEND_OF_NO_ACTIVATION	
	 */
	@SuppressWarnings("unchecked")
	public ScrollableResults getAllPartnerNotActivatedList(long noActivationTimeLimit)
	{		
		Criteria criteria = createCriteria();
		//join with subscriber table
		criteria.createAlias(SubscriberMdn.FieldName_Subscriber, "subscriber");
		// all subscribers of type partner
		criteria.add(Restrictions.eq("subscriber."+Subscriber.FieldName_SubscriberType,CmFinoFIX.SubscriberType_Partner.intValue()));
		// subscriber not activated , activation time is not set means it will be null
		criteria.add(Restrictions.isNull("subscriber."+Subscriber.FieldName_ActivationTime));
		// subscriber approved status is Approved
		criteria.add(Restrictions.eq("subscriber."+Subscriber.FieldName_UpgradeState,CmFinoFIX.UpgradeState_Approved.intValue()));
		Timestamp currentTime = new Timestamp();
		Timestamp beforeTimestamp = new Timestamp(currentTime.getTime() - noActivationTimeLimit);
		// ApproveRejectTime < current time - noActivationTime
		criteria.add(Restrictions.lt("subscriber."+Subscriber.FieldName_ApproveOrRejectTime,beforeTimestamp));
		// status time < current  time - noActivationTime
		criteria.add(Restrictions.lt("subscriber."+Subscriber.FieldName_StatusTime,beforeTimestamp));
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	/**
	 * 
	 * @param inActiveTimeLimit in milliseconds
	 * @return
	 * 
	 * -status = inactive
	 *  - current time - status time > TIME_TO_SUSPEND_OF_INACTIVE
	 *     (suspend subscriber 
	 *	   if partner suspend partner)
	 */
	public ScrollableResults getAllSubscriberInStatus(long inActiveTimeLimit, Integer status, boolean onlySubscriber)
	{		
		Criteria criteria = createCriteria();
		
		//join with subscriber table
		criteria.createAlias(SubscriberMdn.FieldName_Subscriber, "subscriber");
		// all subscribers in inactive state
		criteria.add(Restrictions.eq("subscriber."+SubscriberMdn.FieldName_MDNStatus, status));
		if(onlySubscriber)
			criteria.add(Restrictions.eq("subscriber."+Subscriber.FieldName_SubscriberType, CmFinoFIX.SubscriberType_Subscriber));
		// status time < current  time - noActivationTime
		Timestamp currentTime = new Timestamp();
		Timestamp beforeTimestamp = new Timestamp(currentTime.getTime() - inActiveTimeLimit);
		criteria.add(Restrictions.lt("subscriber."+Subscriber.FieldName_StatusTime,beforeTimestamp));
		// get a scrollable
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	
	/**
	 * Return scrollable list with subscribers who have not done any activity
	 * @param noTransTimeLimit in milliseconds
	 * @param status
	 * @return
	 * 
	 * - inactive subscriber for no activity(i.e there is no transaction listed in sctl table)
	    ( Get list of transactions as subscriber as source, lets call it subSctlList
		  Get list of transactions as subscriber as destinarion, lets call it destSctlList
		    if subSctList and destSctList is empty and 
			   if subscriber create time is > TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY then
			     inactivate subscriber 
				 if partner 
				   inactivate partner
			else
              get the latest transaction from srcSubList and destSubList
			  if trx time > TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY (no activity from last TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY days)
			    ( inactivate subscriber
				   if partner inactivate partner)
	 *
	 * basically need to do this 
	 * select s.* from subscriber_mdn s where not exists
			( SELECT id FROM service_charge_txn_log sctl WHERE (sctl.sourcemdn=s.mdn OR sctl.destmdn=s.mdn) 
				and sctl.createtime >= "2013-05-21 00:00:00"
			)
			 or s.createtime>="2013-05-21 00:00:00";
	 
	 */
	public ScrollableResults getAllSubscriberwithNoActivity(long noTransTimeLimit, Integer status, boolean onlySubscriber)
	{
		Criteria criteria = createCriteria();
		String subscriberMDNAlias = criteria.getAlias();
		criteria.createAlias(SubscriberMdn.FieldName_Subscriber, "subscriber");
		//subscriber with status active
		criteria.add(Restrictions.eq("subscriber."+Subscriber.FieldName_SubscriberStatus, status));
		//if only subscriber add that criteria
		if(onlySubscriber)
			criteria.add(Restrictions.eq("subscriber."+Subscriber.FieldName_SubscriberType, CmFinoFIX.SubscriberType_Subscriber));
		criteria.add(Restrictions.eq(Subscriber.FieldName_SubscriberStatus, status));
		
		// join with subscriber table
		DetachedCriteria sctlCriteria = DetachedCriteria.forClass(ServiceChargeTxnLog.class,"sctl");
		Timestamp currentTime = new Timestamp();
		Timestamp beforeTimestamp = new Timestamp(currentTime.getTime() - noTransTimeLimit);
		//get all sctl whose createtime is greater than beforetimestamp
		sctlCriteria.add(Restrictions.gt("sctl."+ServiceChargeTxnLog.FieldName_CreateTime,beforeTimestamp));
		// sourcemdn or destmdn same the mdn from subscriber_mdn table
		sctlCriteria.add(Restrictions.or(
				Restrictions.eqProperty("sctl."+ServiceChargeTxnLog.FieldName_SourceMDN,subscriberMDNAlias+"."+SubscriberMdn.FieldName_MDN), 
				Restrictions.eqProperty("sctl."+ServiceChargeTxnLog.FieldName_DestMDN,subscriberMDNAlias+"."+SubscriberMdn.FieldName_MDN)));
		// all subscribers in inactive state
		
		criteria.add(Subqueries.notExists(sctlCriteria.setProjection(Projections.property("sctl."+ServiceChargeTxnLog.FieldName_RecordID))));
		// get a scrollable
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	/**
	 * All the subscriber MDN with no fund movement done within the last <code>noTransTimeLimit</code> milliseconds
	 * @param noTransTimeLimit in milliseconds
	 * @param status subscriber status to consider 
	 * @param onlySubscriber 
	 * @return
	 * 
	 * select * from subscriber_mdn mdn, subscriber s where 
	 */
	public ScrollableResults getAllSubscriberwithNoFundMovement(long noTransTimeLimit, Integer status, boolean onlySubscriber)
	{
		Criteria criteria = createCriteria();
		String subscriberMDNAlias = criteria.getAlias();
		criteria.createAlias(SubscriberMdn.FieldName_Subscriber, "subscriber");
		//subscriber with status active
		criteria.add(Restrictions.eq("subscriber."+Subscriber.FieldName_SubscriberStatus, status));
		//if only subscriber add criteria to get only subscriber
		if(onlySubscriber)
			criteria.add(Restrictions.eq("subscriber."+Subscriber.FieldName_SubscriberType, CmFinoFIX.SubscriberType_Subscriber));
		//criteria.add(Restrictions.eq(Subscriber.FieldName_SubscriberStatus, status));
		
		// join with subscriber table
		DetachedCriteria ctCriteria = DetachedCriteria.forClass(CommodityTransfer.class,"ct");
		Timestamp currentTime = new Timestamp();
		Timestamp beforeTimestamp = new Timestamp(currentTime.getTime() - noTransTimeLimit);
		
		//get all ct whose last update is greater than beforetimestamp
		ctCriteria.add(Restrictions.gt("ct."+CommodityTransfer.FieldName_LastUpdateTime,beforeTimestamp));
		
		//transfer status is completed
		ctCriteria.add(Restrictions.eq("ct."+CommodityTransfer.FieldName_TransferStatus,CmFinoFIX.TransactionsTransferStatus_Completed.intValue()));
		
		// sourcemdn or destmdn same the mdn from subscriber_mdn table
		ctCriteria.add(Restrictions.or(
				Restrictions.eqProperty("ct."+CommodityTransfer.FieldName_SourceMDN,subscriberMDNAlias+"."+SubscriberMdn.FieldName_MDN), 
				Restrictions.eqProperty("ct."+CommodityTransfer.FieldName_DestMDN,subscriberMDNAlias+"."+SubscriberMdn.FieldName_MDN)));
		// all subscribers in inactive state
		
		criteria.add(Subqueries.notExists(ctCriteria.setProjection(Projections.property("ct."+CommodityTransfer.FieldName_RecordID))));
		criteria.add(Restrictions.gt("subscriber."+Subscriber.FieldName_LastUpdateTime, beforeTimestamp));
		// get a scrollable
		return criteria.scroll(ScrollMode.FORWARD_ONLY);
	}
	
	@SuppressWarnings("unchecked")
	public int getCountForStatusForMdns(SubscriberMdnQuery query){
        Criteria criteria = createCriteria();
        if (query!=null){
        	Integer[] statuses = query.getStatusIn();
            if (null != statuses && statuses.length > 0) {
                criteria.add(Restrictions.in(SubscriberMdn.FieldName_MDNStatus, statuses));
            }
        }
        criteria.setProjection(Projections.rowCount());
        Integer count = (Integer) criteria.uniqueResult();

        return count;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubscriberMdn> getStatusForMdns(SubscriberMdnQuery query){
        Criteria criteria = createCriteria();
        if (query!=null){
        	Integer[] statuses = query.getStatusIn();
            if (null != statuses && statuses.length > 0) {
                criteria.add(Restrictions.in(SubscriberMdn.FieldName_MDNStatus, statuses));
            }
        }
        processPaging(query,criteria);
        return criteria.list();
	}
	//its a temporary fix given jus for nibss integration since queryng a subscriber using sMDN was returning wierd results

	@SuppressWarnings("unchecked")
	public Subscriber getIDFromMDN(String MDN){
		String Query = "select smdn.Subscriber from SubscriberMdn smdn where smdn.MDN=:MDN";
		Query query = getSession().createQuery(Query).setParameter("MDN",MDN);
        return  (Subscriber) query.list().get(0);
	}
	
    public SubscriberMdn getByMDNAndNotRetiredStatus(String MDN, LockMode lockMode) {
    	Criteria criteria = createCriteria();
    	criteria.add(Restrictions.eq(SubscriberMdn.FieldName_MDN, MDN));
    	criteria.add(Restrictions.ne(SubscriberMdn.FieldName_MDNStatus, CmFinoFIX.SubscriberStatus_Retired));
        if(lockMode != null){
            criteria.setLockMode(lockMode);
        }
        
    	List<SubscriberMdn> subscribersList = criteria.list();
    	
    	if((null != subscribersList) && (subscribersList.size() > 0)){
    		return subscribersList.get(0);
    	}
    	
    	return null;
    }
    
    public SubscriberMdn getByMDNAndNotRetiredStatus(String MDN) {
    	return getByMDNAndNotRetiredStatus(MDN, null);
    }
}
