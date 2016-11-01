/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.RolePermissionQuery;
import com.mfino.domain.Base;
import com.mfino.domain.RolePermission;

/**
 *
 * @author xchen
 */
public class RolePermissionDAO extends BaseDAO<RolePermission> {

    public List<RolePermission> get(RolePermissionQuery query) {
        Criteria criteria = createCriteria();
        if (query.getId() != null) {
            criteria.add(Restrictions.eq(Base.FieldName_RecordID, query.getId()));
        }
        if(query.getUserRole() != null)
        {
            criteria.add(Restrictions.eq(RolePermission.FieldName_Role, query.getUserRole()));
        }
        if(query.getPermission() != null)
        {
            criteria.add(Restrictions.eq(RolePermission.FieldName_Permission, query.getPermission()));
        }
        if(query.getPermissionList() != null)
        {
        	criteria.add(Restrictions.in(RolePermission.FieldName_Permission, query.getPermissionList()));
        }
        processBaseQuery(query, criteria);
        // Paging
        processPaging(query, criteria);
        //applying Order
        applyOrder(query, criteria);
        @SuppressWarnings("unchecked")
        List<RolePermission>results = criteria.list();
        return results;
    }

    @Override
    public void save(RolePermission template){
        super.save(template);
    }
}
