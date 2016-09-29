package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.GroupQuery;
import com.mfino.domain.Groups;

/**
 * 
 * @author Sasi
 *
 */
public class GroupDao extends BaseDAO<Groups>{
	
	public Groups getByName(String name) {
		Criteria criteria = createCriteria();

		if (StringUtils.isNotBlank(name)) {
			criteria.add(Restrictions.eq(Groups.FieldName_GroupName, name).ignoreCase());
		}else{
			return null;
		}
		List<Groups> lst = criteria.list();
		if(lst==null||lst.isEmpty()){
			return null;
		}
		
		return lst.get(0);
	}
	
	public List<Groups> get(GroupQuery query) {
		Criteria criteria = createCriteria();

		if (StringUtils.isNotBlank(query.getGroupName())) {
			addLikeStartRestriction(criteria, Groups.FieldName_GroupName, query.getGroupName());
		}
		
		if(!query.isIncludeSystemGroups()){
			criteria.add(Restrictions.eq(Groups.FieldName_SystemGroup, Boolean.FALSE));
		}
		
		criteria.addOrder(Order.asc(Groups.FieldName_RecordID));
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		
		List<Groups> lst = criteria.list();
			
		return lst;
	}
	
	@SuppressWarnings("unchecked")
	public Groups getSystemGroup() {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(Groups.FieldName_SystemGroup, Boolean.TRUE));
		
		List<Groups> groups = criteria.list();
		if(groups != null  && !groups.isEmpty())
		{
			return groups.get(0);
		}	
		return null;
	}
}
