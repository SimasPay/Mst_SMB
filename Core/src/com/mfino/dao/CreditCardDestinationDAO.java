/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.CreditCardDestinationQuery;
import com.mfino.domain.CreditCardDestinations;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author Maruthi
 */
public class CreditCardDestinationDAO extends BaseDAO<CreditCardDestinations> {

	
	@SuppressWarnings("unchecked")
	public List<CreditCardDestinations> get(CreditCardDestinationQuery query) {
		Criteria criteria = createCriteria();
		 if (query.getSubscriber() != null) {
        	 criteria.add(Restrictions.eq(CreditCardDestinations.FieldName_Subscriber, query.getSubscriber()));
        }
		 if(query.getMdnstatus()!= null){
			 criteria.add(Restrictions.eq(CreditCardDestinations.FieldName_CCMDNStatus, query.getMdnstatus()));
		 }
		 if(query.getCreateTimeGE()!=null&&query.getCreateTimeLT()!=null){
			 criteria.add(Restrictions.between(CreditCardDestinations.FieldName_CreateTime, query.getCreateTimeGE(), query.getCreateTimeLT()));
		 }
		 
//		 processBaseQuery(query, criteria);
		 List<CreditCardDestinations> results = criteria.list();
		 return results;
	}
	public List<CreditCardDestinations> getAllDestinations(Subscriber subs){
		CreditCardDestinationDAO creditCardDestinationDAO = DAOFactory.getInstance().getCreditCardDestinationDAO();
		CreditCardDestinationQuery creditCardDestinationQuery = new CreditCardDestinationQuery();
		creditCardDestinationQuery.setSubscriber(subs);
		return creditCardDestinationDAO.get(creditCardDestinationQuery);		
}
	public int expireRegistrations(Date time) {
		String queryString = "update credit_card_destinations set CCMDNStatus = :expired " +
        ", LastUpdateTime = :currentDate" +
        " where createTime < :creationDate and CCMDNStatus = :registered ";
		Query queryObj = getSQLQuery(queryString);
        queryObj.setTimestamp("currentDate", new Date());
        queryObj.setInteger("expired", CmFinoFIX.CCMDNStatus_Expired);
        queryObj.setInteger("registered", CmFinoFIX.CCMDNStatus_Registered);
        queryObj.setTimestamp("creationDate", time);
         
        int updatedRows = queryObj.executeUpdate();
        return updatedRows;
	}
}
