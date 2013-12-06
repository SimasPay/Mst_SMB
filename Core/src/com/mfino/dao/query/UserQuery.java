/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

import java.util.Date;
import java.util.List;

/**
 *
 * @author xchen
 */
public class UserQuery extends BaseQuery {

    private String _userName;
    private String _userNameLike;
    private Date _CreateTimeSearchLE;
    private Boolean _addOrder;
    private Date _activationTimeGE;
    private String _firstNameLike;
    private String _lastNameLike;
    private Date _activationTimeLT;
    private Date _confirmationTimeGE;
    private Date _confirmationTimeLT;
    private Integer role;
    private Integer notequalsRole;
    private Integer status;
    private Integer restrictions;
    private Integer priorityLevel;
    private List<Integer> roles;

    public Date getActivationTimeGE() {
        return _activationTimeGE;
    }

    public void setActivationTimeGE(Date _activationTimeGE) {
        this._activationTimeGE = _activationTimeGE;
    }

    public Date getActivationTimeLT() {
        return _activationTimeLT;
    }

    public void setActivationTimeLT(Date _activationTimeLT) {
        this._activationTimeLT = _activationTimeLT;
    }

    public Date getConfirmationTimeGE() {
        return _confirmationTimeGE;
    }

    public void setConfirmationTimeGE(Date _confirmationTimeGE) {
        this._confirmationTimeGE = _confirmationTimeGE;
    }

    public Date getConfirmationTimeLT() {
        return _confirmationTimeLT;
    }

    public void setConfirmationTimeLT(Date _confirmationTimeLT) {
        this._confirmationTimeLT = _confirmationTimeLT;
    }    

    public Boolean getAddOrder() {
        return _addOrder;
    }

    public void setAddOrder(Boolean _addOrder) {
        this._addOrder = _addOrder;
    }
    
    public Date getCreateTimeSearchLE() {
        return _CreateTimeSearchLE;
    }

    public void setCreateTimeSearchLE(Date _CreateTimeSearchLE) {
        this._CreateTimeSearchLE = _CreateTimeSearchLE;
    }

    public Integer getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Integer restrictions) {
        this.restrictions = restrictions;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }    

    public String getFirstNameLike() {
        return _firstNameLike;
    }

    public void setFirstNameLike(String _firstNameLike) {
        this._firstNameLike = _firstNameLike;
    }

    public String getLastNameLike() {
        return _lastNameLike;
    }

    public void setLastNameLike(String _lastNameLike) {
        this._lastNameLike = _lastNameLike;
    }

    public String getUserNameLike() {
        return _userNameLike;
    }

    public void setUserNameLike(String _userNameLike) {
        this._userNameLike = _userNameLike;
    }
    
    public String getUserName() {
        return _userName;
    }
    
    public void setUserName(String userName) {
        this._userName = userName;
    }

    public void setNotequalsRole(Integer notequalsRole) {
        this.notequalsRole = notequalsRole;
    }

    public Integer getNotequalsRole() {
        return notequalsRole;
    }

	public Integer getPriorityLevel() {
		return priorityLevel;
	}

	public void setPriorityLevel(Integer priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

	public List<Integer> getRoles() {
		return roles;
	}

	public void setRoles(List<Integer> roles) {
		this.roles = roles;
	}
    
    
}
