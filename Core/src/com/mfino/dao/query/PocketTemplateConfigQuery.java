/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;


/**
 * 
 * @author Pradeep
 */
public class PocketTemplateConfigQuery extends BaseQuery {

	private Long _pocketTemplateConfigID;
	private Integer _subscriberType;
	private Integer _businessPartnerType;
	private Long _KYCLevel;
	private Integer _pocketType;
	private Integer _commodity;
	private Long _pocketTemplateID;
	private Boolean _isDefault;
	private Boolean _isCollectorPocket;
	private Long _GroupID;
	public Long get_KYCLevel() {
		return _KYCLevel;
	}

	public void set_KYCLevel(Long _KYCLevel) {
		this._KYCLevel = _KYCLevel;
	}

	public Long get_pocketTemplateID() {
		return _pocketTemplateID;
	}

	public void set_pocketTemplateID(Long _pocketTemplateID) {
		this._pocketTemplateID = _pocketTemplateID;
	}

	
	public Boolean get_isCollectorPocket() {
		return _isCollectorPocket;
	}

	public void set_isCollectorPocket(Boolean _isCollectorPocket) {
		this._isCollectorPocket = _isCollectorPocket;
	}

	public Boolean get_isSuspensePocket() {
		return _isSuspensePocket;
	}

	public void set_isSuspensePocket(Boolean _isSuspensePocket) {
		this._isSuspensePocket = _isSuspensePocket;
	}

	private Boolean _isSuspensePocket;

	public Long get_pocketTemplateConfigID() {
		return _pocketTemplateConfigID;
	}

	public void set_pocketTemplateConfigID(Long _pocketTemplateConfigID) {
		this._pocketTemplateConfigID = _pocketTemplateConfigID;
	}

	public Integer get_subscriberType() {
		return _subscriberType;
	}

	public void set_subscriberType(Integer _subscriberType) {
		this._subscriberType = _subscriberType;
	}

	public Integer get_businessPartnerType() {
		return _businessPartnerType;
	}

	public void set_businessPartnerType(Integer _businessPartnerType) {
		this._businessPartnerType = _businessPartnerType;
	}	

	public Integer get_pocketType() {
		return _pocketType;
	}

	public void set_pocketType(Integer _pocketType) {
		this._pocketType = _pocketType;
	}

	public Integer get_commodity() {
		return _commodity;
	}

	public void set_commodity(Integer _commodity) {
		this._commodity = _commodity;
	}

	

	public Boolean get_isDefault() {
		return _isDefault;
	}

	public void set_isDefault(Boolean _isDefault) {
		this._isDefault = _isDefault;
	}

	public Long get_GroupID() {
		return _GroupID;
	}

	public void set_GroupID(Long _GroupID) {
		this._GroupID = _GroupID;
	}
}
