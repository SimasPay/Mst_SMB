/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ChannelCodeQuery;
import com.mfino.domain.ChannelCode;

/**
 * @author Deva
 *
 */
public class ChannelCodeDAO extends BaseDAO<ChannelCode> {

    public List<ChannelCode> get(ChannelCodeQuery query) {
        Criteria criteria = createCriteria();

        if (query.getChannelCode() != null) {
            criteria.add(Restrictions.eq(ChannelCode.FieldName_ChannelCode, query.getChannelCode()).ignoreCase());
        }
        if (query.getChannelName() != null) {
            criteria.add(Restrictions.eq(ChannelCode.FieldName_ChannelName, query.getChannelName()).ignoreCase());
        }
        if(query.getChannelNameLike() != null) {
            addLikeStartRestriction(criteria, ChannelCode.FieldName_ChannelName, query.getChannelNameLike());
        }
        if (query.getSourceApplication() != null) {
            criteria.add(Restrictions.eq(ChannelCode.FieldName_ChannelSourceApplication, query.getSourceApplication()));
        }
        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
        criteria.addOrder(Order.desc(ChannelCode.FieldName_RecordID));
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<ChannelCode> results = criteria.list();
        return results;
    }

    public ChannelCode getByChannelCode(String channelCode) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(ChannelCode.FieldName_ChannelCode, channelCode).ignoreCase());
        return (ChannelCode) criteria.uniqueResult();
    }
    public Integer getMaximumChannelSourceValue(){
        Criteria criteria = createCriteria();
        criteria.setProjection(Projections.max(ChannelCode.FieldName_ChannelSourceApplication));
        List list = criteria.list();
    	return (Integer) (list.get(0)==null?0:list.get(0)); //since projection gives some result it can be null too. Chking before returing the value.
    }
    public ChannelCode getByChannelSourceApplication(Integer channelSourceApplication) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(ChannelCode.FieldName_ChannelSourceApplication, channelSourceApplication));
        return (ChannelCode) criteria.uniqueResult();
    }

}
