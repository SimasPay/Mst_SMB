/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;

import com.mfino.domain.PermissionItems;

/**
 *
 * @author sandeepjs
 */
public class PermissionItemsDAO extends BaseDAO<PermissionItems> {

    public List<PermissionItems> getAll()
    {
        Criteria criteria = createCriteria();

        @SuppressWarnings("unchecked")
        List<PermissionItems> results = criteria.list();
        return results;
    }
    
    public List<PermissionItems> loadRolePermissionsByGroup(Integer role) {
    	String hqlString = "Select pi from RolePermission as rp, PermissionItems as pi where" +
    								" pi.Permission = rp.Permission and rp.Role = :role order by pi.PermissionGroup";
        Query queryObj = getQuery(hqlString);
        queryObj.setInteger("role", role);
        List<PermissionItems> results = queryObj.list();
        return results;
    }

}
