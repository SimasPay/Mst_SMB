package com.mfino.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.GroupQuery;
import com.mfino.domain.Group;
import com.mfino.fix.CmFinoFIX;

/**
 * 
 * @author Sasi
 *
 */
public class GroupDao extends BaseDAO<Group>{
	
	public Group getByName(String name) {
		Criteria criteria = createCriteria();

		if (StringUtils.isNotBlank(name)) {
			criteria.add(Restrictions.eq(CmFinoFIX.CRGroup.FieldName_GroupName, name).ignoreCase());
		}else{
			return null;
		}
		List<Group> lst = criteria.list();
		if(lst==null||lst.isEmpty()){
			return null;
		}
		
		return lst.get(0);
	}
	
	public List<Group> get(GroupQuery query) {
		Criteria criteria = createCriteria();

		if (StringUtils.isNotBlank(query.getGroupName())) {
			addLikeStartRestriction(criteria, CmFinoFIX.CRGroup.FieldName_GroupName, query.getGroupName());
		}
		
		if(!query.isIncludeSystemGroups()){
			criteria.add(Restrictions.eq(CmFinoFIX.CRGroup.FieldName_SystemGroup, Boolean.FALSE));
		}
		
		criteria.addOrder(Order.asc(CmFinoFIX.CRGroup.FieldName_RecordID));
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		
		List<Group> lst = criteria.list();
			
		return lst;
	}
	
	@SuppressWarnings("unchecked")
	public Group getSystemGroup() {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRGroup.FieldName_SystemGroup, Boolean.TRUE));
		
		List<Group> groups = criteria.list();
		if(groups != null  && !groups.isEmpty())
		{
			return groups.get(0);
		}	
		return null;
	}
}
