/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.domain.PermissionGroup;
import com.mfino.domain.PermissionItems;
import com.mfino.domain.RolePermission;

/**
 * @author Sreenath
 *
 */
public interface PermissionService {
    /**
     * Returns the list of all records from the PermissionsGroup table
     * @return
     */
    public List<PermissionGroup> loadPermissionGroups(); 
    
    /**
     * Returns the list of all PermissionItems for the given role
     * @param role
     * @return
     */
    public List<PermissionItems> loadRolePermissionsByGroup(Integer role);
    
    /**
     * Deletes the given permissions for the input role
     * @param role
     * @param permissions
     */
    public void deleteRolePermissions(Integer role, List<Integer> permissions);
    
    /**
     * Add the given permissions for the input role
     * @param role
     * @param permissions
     */
    public void addRolePermissions(Integer role, List<Integer> permissions);
    
    /**
     * Get all RolePermissions with the given permission
     * @param permission
     * @return
     */
    public List<RolePermission> getByPermission(Integer permission);

}
