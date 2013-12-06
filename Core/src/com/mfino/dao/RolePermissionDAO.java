/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.RolePermissionQuery;
import com.mfino.domain.RolePermission;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author xchen
 */
public class RolePermissionDAO extends BaseDAO<RolePermission> {

    public List<RolePermission> get(RolePermissionQuery query) {
        Criteria criteria = createCriteria();
        if (query.getId() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBase.FieldName_RecordID, query.getId()));
        }
        if(query.getUserRole() != null)
        {
            criteria.add(Restrictions.eq(CmFinoFIX.CRRolePermission.FieldName_Role, query.getUserRole()));
        }
        if(query.getPermission() != null)
        {
            criteria.add(Restrictions.eq(CmFinoFIX.CRRolePermission.FieldName_Permission, query.getPermission()));
        }
        if(query.getPermissionList() != null)
        {
        	criteria.add(Restrictions.in(CmFinoFIX.CRRolePermission.FieldName_Permission, query.getPermissionList()));
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
