/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.UserQuery;
import com.mfino.domain.Role;
import com.mfino.domain.User;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRUser;
import com.mfino.hibernate.Timestamp;

/**
 *
 * @author sandeepjs
 */
public class UserDAO extends BaseDAO<User> {
	  //Before Correcting errors reported by Findbugs:
		//public static String EXPIRY_TAG = "_EXPIRED_";
    	//public static String RETIRE_TAG = "_RETIRED_";
	
	  //After Correcting the errors reported by Findbugs
    public static final String EXPIRY_TAG = "_EXPIRED_";
    public static final String RETIRE_TAG = "_RETIRED_";
    

    @Override
    public void save(User theUser) {
        //FIXME : everyone save it as 1 for now
        if (theUser.getmFinoServiceProviderByMSPID() == null) {
            MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
            mFinoServiceProvider msp = mspDao.getById(1);
            theUser.setmFinoServiceProviderByMSPID(msp);
        }

        if (theUser.getStatusTime() == null) {
            theUser.setStatusTime(new Timestamp());
        }
        super.save(theUser);
    }

    public List<User> get(UserQuery query) {
        Criteria criteria = createCriteria();
        MfinoServiceProviderDAO mspDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
        mFinoServiceProvider msp = mspDAO.getById(1l);
        
        if (query.getUserName() != null) {
        	criteria.add(Restrictions.eq(CRUser.FieldName_mFinoServiceProviderByMSPID, msp));
            criteria.add(Restrictions.eq(CRUser.FieldName_Username, query.getUserName()).ignoreCase());
            //It is recommended to declare the general fetch mode in the hbm file
            // and over-ride it as and when required            
        }
        if (query.getUserNameLike() != null) {
        	criteria.add(Restrictions.eq(CRUser.FieldName_mFinoServiceProviderByMSPID, msp));
            addLikeStartRestriction(criteria, CRUser.FieldName_Username, query.getUserNameLike());
        }
        if(query.getConfirmationTimeGE() !=null){
        	criteria.add(Restrictions.ge(CRUser.FieldName_ConfirmationTime, query.getConfirmationTimeGE()));
        }
        if(query.getConfirmationTimeLT() !=null){
        	criteria.add(Restrictions.lt(CRUser.FieldName_ConfirmationTime, query.getConfirmationTimeLT()));
        }
       
        if(query.getActivationTimeGE() !=null){
        	criteria.add(Restrictions.ge(CRUser.FieldName_UserActivationTime, query.getActivationTimeGE()));
        }
        if(query.getActivationTimeLT() !=null){
        	criteria.add(Restrictions.lt(CRUser.FieldName_UserActivationTime, query.getActivationTimeLT()));
        }

        if (query.getFirstNameLike() != null) {
            addLikeStartRestriction(criteria, CRUser.FieldName_FirstName, query.getFirstNameLike());
        }
        if (query.getLastNameLike() != null) {
            addLikeStartRestriction(criteria, CRUser.FieldName_LastName, query.getLastNameLike());
        }
        if (query.getStatus() != null) {
            criteria.add(Restrictions.eq(CRUser.FieldName_UserStatus, query.getStatus()));
        }
        if(query.getCompany()!=null){
        	criteria.add(Restrictions.eq(CRUser.FieldName_Company,query.getCompany()));
        }
        if (query.getRestrictions() != null) {
            int rest = query.getRestrictions();
            if (rest > 0) {
                criteria.add(Restrictions.sqlRestriction("(Restrictions & ?) > 0", query.getRestrictions(), Hibernate.INTEGER));
            } else {
                //When user restrictions equal
                criteria.add(Restrictions.eq(CRUser.FieldName_UserRestrictions, CmFinoFIX.SubscriberRestrictions_None));
            }
        }        
        if(query.getRole() != null) {
            criteria.add(Restrictions.eq(CRUser.FieldName_Role, query.getRole()));
        }
        if(query.getRoles() != null) {
        	criteria.add(Restrictions.in(CRUser.FieldName_Role, query.getRoles()));
        }
        if(query.getNotequalsRole() != null) {
            criteria.add(Restrictions.ne(CRUser.FieldName_Role, query.getNotequalsRole()));
        }
        /*
         * Remove check to add Order to criteria... by default add order by RecordID
         * if(query.getAddOrder()!=null && Boolean.TRUE.equals(query.getAddOrder())){
            criteria.addOrder(Order.desc(CmFinoFIX.CRUser.FieldName_RecordID));
        }*/
        criteria.addOrder(Order.desc(CmFinoFIX.CRUser.FieldName_RecordID));
		if(query.getPriorityLevel() != null){ //fetch users whose priority level is greater than given level(ie., current user level)
			DetachedCriteria roles = DetachedCriteria.forClass(Role.class)
					.setProjection(Property.forName(CmFinoFIX.CRRole.FieldName_RecordID))
					.add(Restrictions.ge(CmFinoFIX.CRRole.FieldName_PriorityLevel, query.getPriorityLevel()));
			criteria.add(Property.forName(CmFinoFIX.CRUser.FieldName_Role).in(roles));
		}
		

        processBaseQuery(query, criteria);
        processPaging(query, criteria);
        @SuppressWarnings("unchecked")
        List<User> results = criteria.list();

        return results;
    }

    public User getByUserName(String userName){
        UserQuery query = new UserQuery();
        query.setUserName(userName);
        List<User> results = get(query);
        if(results.size() > 0){
            return results.get(0);
        }else{
            return null;
        }
    }
    public int expireRegistrations(Date creationDate){
        // TODO .. take care of update by
          String queryString = "update user set username = concat(username,'" + EXPIRY_TAG + "', :currentDate)" +
                  ", Status = :expired , StatusTime = :currentDate, ExpirationTime = :currentDate " +
                  ", LastUpdateTime = :currentDate" + 
                  " where createTime < :creationDate and Status = :registered and Role = :role";

        Query queryObj = getSQLQuery(queryString);
        queryObj.setTimestamp("currentDate", new Date());
        queryObj.setInteger("expired", CmFinoFIX.UserStatus_Expired);
        queryObj.setInteger("registered", CmFinoFIX.UserStatus_Registered);
        queryObj.setTimestamp("creationDate", creationDate);
        queryObj.setInteger("role", CmFinoFIX.Role_Subscriber);
        int updatedRows = queryObj.executeUpdate();
        return updatedRows;
    }
}
