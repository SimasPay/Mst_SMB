/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import java.util.Date;

/**
 * 
 * @author Raju
 */
public class PocketTemplateQuery extends BaseQuery {

	private Long _pocketTemplateID;
	private String _descriptionSearch;
	private Integer _pocketType;
	private Integer _commodityType;
	private Date _startDate;
	private Date _endDate;
	private String _exactPocketDescription;
	private String _pocketCode;
	private Boolean _isCollectorPocket;
	private Boolean _isSuspencePocket;
	

	public String getPocketCode() {
		return _pocketCode;
	}

	public void setPocketCode(String _pocketCode) {
		this._pocketCode = _pocketCode;
	}

	public String getExactPocketDescription() {
		return _exactPocketDescription;
	}

	public void setExactPocketDescription(String _pocketDescription) {
		this._exactPocketDescription = _pocketDescription;
	}

	public Long getPocketTemplateID() {
		return _pocketTemplateID;
	}

	public void setPocketTemplateID(Long _pocketTemplateID) {
		this._pocketTemplateID = _pocketTemplateID;
	}

	public Integer getCommodityType() {
		return _commodityType;
	}

	public void setCommodityType(Integer _commodityType) {
		this._commodityType = _commodityType;
	}

	public String getDescriptionSearch() {
		return _descriptionSearch;
	}

	public void setDescriptionSearch(String _descriptionSearch) {
		this._descriptionSearch = _descriptionSearch;
	}

	public Date getEndDate() {
		return _endDate;
	}

	public void setEndDate(Date _endDate) {
		this._endDate = _endDate;
	}

	public Integer getPocketType() {
		return _pocketType;
	}

	public void setPocketType(Integer _pocketType) {
		this._pocketType = _pocketType;
	}

	public Date getStartDate() {
		return _startDate;
	}

	public void setStartDate(Date _startDate) {
		this._startDate = _startDate;
	}

	public Boolean get_isCollectorPocket() {
		return _isCollectorPocket;
	}

	public void set_isCollectorPocket(Boolean _isCollectorPocket) {
		this._isCollectorPocket = _isCollectorPocket;
	}

	public Boolean get_isSuspencePocket() {
		return _isSuspencePocket;
	}

	public void set_isSuspencePocket(Boolean _isSuspencePocket) {
		this._isSuspencePocket = _isSuspencePocket;
	}

}
