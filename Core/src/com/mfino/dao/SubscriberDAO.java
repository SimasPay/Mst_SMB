package com.mfino.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.SubscriberQuery;
import com.mfino.domain.Subscriber;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author sandeepjs
 */
public class SubscriberDAO extends BaseDAO<Subscriber> {

    private static final String POCKETASSOCNAME = "PocketFromSubscriberID";
    private static final String POCKETTEMPLATEASSOCNAME = "PocketTemplate";

    public SubscriberDAO() {
        super();
    }

    public List<Subscriber> get(SubscriberQuery query) {
        Criteria criteria = createCriteria();

        if (query.getId() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRSubscriber.FieldName_RecordID, query.getId()));
        }

        if(query.getIsDompetMerchant() != null){
            criteria.add(Restrictions.eq(CmFinoFIX.CRSubscriber.FieldName_DompetMerchant, query.getIsDompetMerchant()));
        }
        
        if(query.getRegistrationMedium()!=null){
        	criteria.add(Restrictions.eq(CmFinoFIX.CRSubscriber.FieldName_RegistrationMedium, query.getRegistrationMedium()));
        }
        
        if(query.getStatus()!=null){
        	criteria.add(Restrictions.eq(CmFinoFIX.CRSubscriber.FieldName_SubscriberStatus, query.getStatus()));
        }
//        if (query.getPocketTemplateId() != null) {
//
//            String pocketAlias = POCKETASSOCNAME + DAOConstants.ALIAS_SUFFIX;
//            criteria.createAlias(POCKETASSOCNAME, pocketAlias);
//
//            String pocketTemplateAlias = POCKETTEMPLATEASSOCNAME + DAOConstants.ALIAS_SUFFIX;
//            criteria.createAlias(POCKETASSOCNAME + GeneralConstants.DOT_STRING + POCKETTEMPLATEASSOCNAME, pocketTemplateAlias);
//            final String pocketTemplatewithAlias = pocketTemplateAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRPocketTemplate.FieldName_RecordID;
//            criteria.add(Restrictions.eq(pocketTemplatewithAlias, query.getPocketTemplateId()));
//            processColumn(query, CmFinoFIX.CRPocketTemplate.FieldName_RecordID, pocketTemplatewithAlias);
//
//        }
        
        processBaseQuery(query, criteria);
        
        processPaging(query, criteria);

        @SuppressWarnings("unchecked")
        List<Subscriber> results = criteria.list();

        return results;
    }

    @Override
    public void save(Subscriber s) {
        //FIXME : everyone save it as 1 for now
        if (s.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            s.setmFinoServiceProviderByMSPID(msp);
        }
        super.save(s);
    }
    
    @Override
    public void saveWithoutFlush(Subscriber s){
        //FIXME : everyone save it as 1 for now
        if (s.getmFinoServiceProviderByMSPID() == null) {
            s.setmFinoServiceProviderByMSPID((mFinoServiceProvider) this.getSession().load(mFinoServiceProvider.class, 1l));
        }
        super.saveWithoutFlush(s);
    }

	@SuppressWarnings("unchecked")
	public List<Subscriber> getProfileChangedSubscribers(Date startTime, Date endTime) {
		 Criteria criteria = createCriteria();
		 if(endTime!=null){
			 criteria.add(Restrictions.le(CmFinoFIX.CRSubscriber.FieldName_ApproveOrRejectTime, endTime));
		 }
		 if(startTime!=null){
			 criteria.add(Restrictions.gt(CmFinoFIX.CRSubscriber.FieldName_ApproveOrRejectTime, startTime));
		 }
		 criteria.add(Restrictions.eq(CmFinoFIX.CRSubscriber.FieldName_UpgradeState,CmFinoFIX.UpgradeState_Approved));
		 List<Subscriber> result = criteria.list();
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Subscriber> getSubscribersNotIn(List<Long> subIds) {
		 Criteria criteria = createCriteria();
		 criteria.add(Restrictions.not(Restrictions.in(CmFinoFIX.CRSubscriber.FieldName_RecordID,subIds)));
		 criteria.add(Restrictions.eq(CmFinoFIX.CRSubscriber.FieldName_SubscriberType, CmFinoFIX.SubscriberType_Subscriber));
		 List<Subscriber> result = criteria.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Subscriber> getSubscribers(Integer type) {
		 Criteria criteria = createCriteria();
		 if(type!=null){
			 criteria.add(Restrictions.eq(CmFinoFIX.CRSubscriber.FieldName_SubscriberType, type));
		 }
		 List<Subscriber> result = criteria.list();
		return result;
	}

	public Integer getSubscribersByRegisteringPartneId(Long partnerId, Date start, Date end) {
		 Criteria criteria = createCriteria();
		 if(partnerId==null){
			 return 0;
		 }
		 if(end!=null){
			 criteria.add(Restrictions.le(CmFinoFIX.CRSubscriber.FieldName_CreateTime, end));
		 }
		 if(start!=null){
			 criteria.add(Restrictions.gt(CmFinoFIX.CRSubscriber.FieldName_CreateTime, start));
		 }
		 criteria.add(Restrictions.eq(CmFinoFIX.CRSubscriber.FieldName_RegisteringPartnerID,partnerId));
		@SuppressWarnings("unchecked")
		List<Subscriber> result = criteria.list();
		if(result==null||result.isEmpty()){
			return 0;
		}
		return result.size();
	}

	@SuppressWarnings("unchecked")
	public List<Subscriber> getAllPartners() {
		 Criteria criteria = createCriteria();
		 criteria.add(Restrictions.eq(CmFinoFIX.CRSubscriber.FieldName_SubscriberType, CmFinoFIX.SubscriberType_Partner));
		 List<Subscriber> results = criteria.list();
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getNewSubscribersCount(Date startDate, Date endDate) {

		String sql= "select s.RegisteringPartnerID, count(*) " +
		 		"from Subscriber as s " + 
				"where s.RegistrationMedium = :rmedium " +
		 		"and s.CreateTime >= :start " +
				"and s.CreateTime <= :end " +
				"group by s.RegisteringPartnerID"; 
		 
		Query queryObj = getSession().createQuery(sql);
		queryObj.setParameter("rmedium", CmFinoFIX.RegistrationMedium_Agent);
		queryObj.setParameter("start", startDate);
		queryObj.setParameter("end", endDate);
		List<Object[]> results = queryObj.list();
		return results;
	}	

}
