/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PermissionGroupDAO;
import com.mfino.dao.PermissionItemsDAO;
import com.mfino.dao.RolePermissionDAO;
import com.mfino.dao.query.RolePermissionQuery;
import com.mfino.domain.PermissionGroup;
import com.mfino.domain.PermissionItem;
import com.mfino.domain.RolePermission;
import com.mfino.service.PermissionService;

/**
 *
 * @author Srikanth
 */
@Service("PermissionServiceImpl")
public class PermissionServiceImpl implements PermissionService{
	
	private static PermissionItemsDAO permissionsDao = DAOFactory.getInstance().getPermissionItemsDAO();	
	private static PermissionGroupDAO permissionGroupDao = DAOFactory.getInstance().getPermissionGroupDAO();
	private static RolePermissionDAO rolePermissionDAO = DAOFactory.getInstance().getRolePermissionDAO();
    
    private static Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);
    
    /**
     * Returns the list of all records from the PermissionsGroup table
     * @return
     */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public List<PermissionGroup> loadPermissionGroups() {
    	List<PermissionGroup> list = permissionGroupDao.getAll();
    	return list;
    } 
    
    /**
     * Returns the list of all PermissionItems for the given role
     * @param role
     * @return
     */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public List<PermissionItem> loadRolePermissionsByGroup(Integer role) {
    	List<PermissionItem> list = permissionsDao.loadRolePermissionsByGroup(role);
    	return list;
    }
    
    /**
     * Deletes the given permissions for the input role
     * @param role
     * @param permissions
     */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void deleteRolePermissions(Integer role, List<Integer> permissions) {
    	log.info("In deleteRolePermissions method: role ->" + role);
    	RolePermissionQuery query = new RolePermissionQuery();		
		query.setUserRole(role);
		query.setPermissionList(permissions);
		List<RolePermission> list = rolePermissionDAO.get(query);
		rolePermissionDAO.delete(list);
	}
    
    /**
     * Add the given permissions for the input role
     * @param role
     * @param permissions
     */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public void addRolePermissions(Integer role, List<Integer> permissions) {
    	log.info("In addRolePermissions method: role ->" + role);
    	List<RolePermission> rolePermissionsList = new ArrayList<RolePermission>();
    	for(Integer permission: permissions) {
    		RolePermission rolePermission = new RolePermission();
    		rolePermission.setRole(role);
    		rolePermission.setPermission(permission);
    		rolePermissionsList.add(rolePermission);
    	}
    	rolePermissionDAO.save(rolePermissionsList);
    }
    
    /**
     * Get all RolePermissions with the given permission
     * @param permission
     * @return
     */
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public List<RolePermission> getByPermission(Integer permission) {
    	RolePermissionQuery query = new RolePermissionQuery();
    	query.setPermission(permission);
    	return rolePermissionDAO.get(query);
    }
}
