package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.RoleQuery;
import com.mfino.domain.Role;

/**
 * 
 * @author srikanth
 */
public class RoleDAO extends BaseDAO<Role> {
	public Role getByEnumCode(String enumCode) {
		RoleQuery query = new RoleQuery();
		query.setEnumCode(enumCode);
		List<Role> results = get(query);
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	public List<Role> get(RoleQuery query) {
		Criteria criteria = createCriteria();

		if (query.getEnumCode() != null) {
			criteria.add(Restrictions.eq(Role.FieldName_EnumCode,
					query.getEnumCode()));
		}
		if (query.getEnumValue() != null) {
			criteria.add(Restrictions.eq(Role.FieldName_EnumValue,
					query.getEnumValue()).ignoreCase());
		}
		if (query.getDisplayText() != null) {
			criteria.add(Restrictions.eq(
					Role.FieldName_DisplayText,
					query.getDisplayText()).ignoreCase());
		}
		if (query.getIsSystemUser() != null) {
			criteria.add(Restrictions.eq(
					Role.FieldName_IsSystemUser,
					query.getIsSystemUser()));
		}
		if (query.getPriorityLevel() != null) {
			criteria.add(Restrictions.ge(
					Role.FieldName_PriorityLevel,
					query.getPriorityLevel()));
		}
		processBaseQuery(query, criteria);
		processPaging(query, criteria);
		@SuppressWarnings("unchecked")
		List<Role> results = criteria.list();

		return results;
	}

}
