/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;

import com.mfino.domain.PermissionItem;

/**
 *
 * @author sandeepjs
 */
public class PermissionItemsDAO extends BaseDAO<PermissionItem> {

    public List<PermissionItem> getAll()
    {
        Criteria criteria = createCriteria();

        @SuppressWarnings("unchecked")
        List<PermissionItem> results = criteria.list();
        return results;
    }
    
    public List<PermissionItem> loadRolePermissionsByGroup(Integer role) {
    	String hqlString = "Select pi from RolePermission as rp, PermissionItem as pi where" +
    								" pi.permission = rp.permission and rp.role = :role order by pi.permissionGroup";
        Query queryObj = getQuery(hqlString);
        queryObj.setInteger("role", role);
        List<PermissionItem> results = queryObj.list();
        return results;
    }

}
