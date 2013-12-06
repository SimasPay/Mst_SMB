/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import java.util.List;

/**
 *
 * @author sandeepjs
 */
public class RolePermissionQuery extends BaseQuery {

    private Integer _userRole;
    private Integer _permission;
    private List<Integer> _permissionList;
   
    public Integer getPermission() {
        return _permission;
    }

    public void setPermission(Integer _permission) {
        this._permission = _permission;
    }

    public Integer getUserRole() {
        return _userRole;
    }

    public void setUserRole(Integer _userRole) {
        this._userRole = _userRole;
    }

	public List<Integer> getPermissionList() {
		return _permissionList;
	}

	public void setPermissionList(List<Integer> _permissionList) {
		this._permissionList = _permissionList;
	}	
}
