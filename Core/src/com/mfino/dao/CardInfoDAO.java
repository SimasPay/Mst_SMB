package com.mfino.dao;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.CardInfoQuery;
import com.mfino.domain.CardInfo;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Query;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;



public class CardInfoDAO extends BaseDAO<CardInfo> {

    public static final String SUBSCRIBER_RELATION_NAME = "Subscriber";
    public static final String SUBSCRIBER_TABLE_NAME = "Subscriber";
    public static final String subcriberTableNameAlias = SUBSCRIBER_TABLE_NAME + DAOConstants.ALIAS_SUFFIX;
    public static final String companyIDAlias = subcriberTableNameAlias + DAOConstants.ALIAS_COLNAME_SEPARATOR + CmFinoFIX.CRSubscriber.FieldName_Company;
    public List<CardInfo> get(CardInfoQuery query) {

        Criteria criteria = createCriteria();

       
          if(query.getCardStatus()!=null){
             criteria.add(Restrictions.eq(CmFinoFIX.CRCardInfo.FieldName_CardStatus, query.getCardStatus()));
          }
        if(query.ShowBothConfirmAndActiveCards()!=null && Boolean.TRUE.equals(query.ShowBothConfirmAndActiveCards())){
            Set<Integer> collection = new HashSet<Integer>();
            collection.add(CmFinoFIX.UserStatus_Active);
            collection.add(CmFinoFIX.UserStatus_Confirmed);
            criteria.add(Restrictions.in(CmFinoFIX.CRCardInfo.FieldName_CardStatus, collection));
        }
        
        if(query.showBothRegisteredAndActiveCards()!=null && Boolean.TRUE.equals(query.showBothRegisteredAndActiveCards())){
            Set<Integer> collection = new HashSet<Integer>();
            collection.add(CmFinoFIX.UserStatus_Registered);
            collection.add(CmFinoFIX.UserStatus_Active);
            criteria.add(Restrictions.in(CmFinoFIX.CRCardInfo.FieldName_CardStatus, collection));
        }

//        if (query.getCreateTimeLT() != null) {
//            criteria.add(Restrictions.lt(CmFinoFIX.CRCardInfo.FieldName_CreateTime, query.getCreateTimeLT()));
//        }
//        if (query.getCreateTimeGE() != null) {
//            criteria.add(Restrictions.ge(CmFinoFIX.CRCardInfo.FieldName_CreateTime, query.getCreateTimeGE()));
//        }
        if(query.getCreateTimeGE()!=null && query.getCreateTimeLT()!=null){
        	criteria.add(Restrictions.between(CmFinoFIX.CRCardInfo.FieldName_CreateTime, query.getCreateTimeGE(),query.getCreateTimeLT()));
        	query.setCreateTimeGE(null);
        	query.setCreateTimeLT(null);
        	}
        	
        if(query.getCompany() != null){
            criteria.createAlias(SUBSCRIBER_TABLE_NAME, subcriberTableNameAlias);
            criteria.add(Restrictions.eq(companyIDAlias, query.getCompany()));
        }
        if(query.getSubscriber() != null){
//            criteria.createAlias(SUBSCRIBER_TABLE_NAME, subcriberTableNameAlias);
//            criteria.add(Restrictions.eq(CmFinoFIX.CRCardInfo.FieldName_Subscriber, query.getSubscriber()));
              criteria.add(Restrictions.eq(SUBSCRIBER_RELATION_NAME,query.getSubscriber()));
        }
        if(query.getIsConfirmationRequired()!=null){
        	criteria.add(Restrictions.eq(CmFinoFIX.CRCardInfo.FieldName_isConformationRequired, query.getIsConfirmationRequired()));
        }
        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
        criteria.addOrder(Order.asc(CmFinoFIX.CRCardInfo.FieldName_RecordID));
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<CardInfo> results = criteria.list();

        return results;
    }
    public int expireRegistrations(Date creationDate){
        // TODO .. take care of update by
          String queryString = "update card_info set CardStatus = :expired " +
                  ", LastUpdateTime = :currentDate" +
                  " where createTime < :creationDate and CardStatus = :registered";

        Query queryObj = getSQLQuery(queryString);
        queryObj.setTimestamp("currentDate", new Date());
        queryObj.setInteger("expired", CmFinoFIX.UserStatus_Expired);
        queryObj.setInteger("registered", CmFinoFIX.UserStatus_Registered);
        queryObj.setTimestamp("creationDate", creationDate);
        int updatedRows = queryObj.executeUpdate();
        return updatedRows;
    }
    public List<CardInfo> getCards(Subscriber subscriber){
    CardInfoQuery query = new CardInfoQuery();
    CardInfoDAO dao = DAOFactory.getInstance().getCardInfoDAO();
    query.setSubscriber(subscriber);
    return dao.get(query);
   }

}
