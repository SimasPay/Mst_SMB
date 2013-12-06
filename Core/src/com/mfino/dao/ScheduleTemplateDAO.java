/**
 * 
 */
package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.ScheduleTemplateQuery;
import com.mfino.domain.ScheduleTemplate;
import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author Hemanth
 *
 */
public class ScheduleTemplateDAO extends BaseDAO<ScheduleTemplate> {

    public List<ScheduleTemplate> get(ScheduleTemplateQuery query) {
        Criteria criteria = createCriteria();
        
        if (query.getName() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRScheduleTemplate.FieldName_Name, query.getName()).ignoreCase());
        }
        if (query.getModeType() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRScheduleTemplate.FieldName_ModeType, query.getModeType()).ignoreCase());
        }
        if (query.getDayOfMonth() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRScheduleTemplate.FieldName_DayOfMonth, query.getDayOfMonth()).ignoreCase());
        }
        if (query.getDayOfWeek() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRScheduleTemplate.FieldName_DayOfWeek, query.getDayOfWeek()));
        }
        if (query.getCron() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRScheduleTemplate.FieldName_Cron, query.getCron()).ignoreCase());
        }
        
        if(query.getTimerValueHH()!=null){
        	criteria.add(Restrictions.eq(CmFinoFIX.CRScheduleTemplate.FieldName_TimerValueHH, query.getTimerValueHH()));
        }
        if(query.getTimerValueMM()!=null){
        	criteria.add(Restrictions.eq(CmFinoFIX.CRScheduleTemplate.FieldName_TimerValueMM, query.getTimerValueMM()));
        }
        if(query.getMonth()!=null){
        	criteria.add(Restrictions.eq(CmFinoFIX.CRScheduleTemplate.FieldName_Month, query.getMonth()));
        }
             
       
       
        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
        criteria.addOrder(Order.desc(CmFinoFIX.CRScheduleTemplate.FieldName_RecordID));
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<ScheduleTemplate> results = criteria.list();
        return results;
    }

    public ScheduleTemplate getByModeType(String mode) {
        Criteria criteria = createCriteria();
        criteria.add(Restrictions.eq(ScheduleTemplate.FieldName_ModeType, mode).ignoreCase());
        return (ScheduleTemplate) criteria.uniqueResult();
    }


}
