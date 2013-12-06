/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

/**
 *
 * @author xchen
 */
public class PocketQuery extends BaseQuery {

    private Long _mdnIDSearch;
    private Boolean _isDefault;
    private Long _pocketTemplateID;
    private Integer _pocketType;
    private Integer _commodity;
    private String _cardPan;
    private String _cardAlias;
    private Integer _pocketStatus;
    private Boolean _pocketCardPanLike;
    private Integer _bankCode;
    private Boolean isCollectorPocket;
    private Boolean isCollectorPocketAllowed;
    private Boolean isSuspencePocketAllowed;
    private String	statusSearchString;
    
    public Integer getBankCode() {
        return _bankCode;
    }

    public void setBankCode(Integer _bankCode) {
        this._bankCode = _bankCode;
    }

    public Boolean isPocketCardPanLikeSearch() {
        return _pocketCardPanLike;
    }

    public void setPocketCardPaneLikeSearch(Boolean isCardPanLikeSearch) {
        _pocketCardPanLike = isCardPanLikeSearch;
    }

    /**
     * @return the _mdnQuery
     */
    public Long getMdnIDSearch() {
        return _mdnIDSearch;
    }

    /**
     * @param mdnQuery the _mdnQuery to set
     */
    public void setMdnIDSearch(Long mdnSearch) {
        this._mdnIDSearch = mdnSearch;
    }

    public Boolean isIsDefault() {
        return _isDefault;
    }

    public void setIsDefault(Boolean _isDefault) {
        this._isDefault = _isDefault;
    }

    public Long getPocketTemplateID() {
        return _pocketTemplateID;
    }

    public void setPocketTemplateID(Long _pocketTemplateID) {
        this._pocketTemplateID = _pocketTemplateID;
    }

    public Integer getPocketType() {
        return _pocketType;
    }

    public void setPocketType(Integer _pocketType) {
        this._pocketType = _pocketType;
    }

    /**
     * @return the _commodity
     */
    public Integer getCommodity() {
        return _commodity;
    }

    /**
     * @param commodity the _commodity to set
     */
    public void setCommodity(Integer commodity) {
        this._commodity = commodity;
    }

    /**
     * @return the _cardPan
     */
    public String getCardPan() {
        return _cardPan;
    }

    /**
     * @param cardPan the _cardPan to set
     */
    public void setCardPan(String cardPan) {
        this._cardPan = cardPan;
    }

    public String getCardAlias() {
		return _cardAlias;
	}

	public void setCardAlias(String _cardAlias) {
		this._cardAlias = _cardAlias;
	}

	/**
     * @return the _pocketStatus
     */
    public Integer getPocketStatus() {
        return _pocketStatus;
    }

    /**
     * @param pocketStatus the _pocketStatus to set
     */
    public void setPocketStatus(Integer pocketStatus) {
        this._pocketStatus = pocketStatus;
    }

	public Boolean getIsCollectorPocket() {
		return isCollectorPocket;
	}

	public void setIsCollectorPocket(Boolean isCollectorPocket) {
		this.isCollectorPocket = isCollectorPocket;
	}

	public Boolean IsCollectorPocketAllowed() {
		return isCollectorPocketAllowed;
	}

	public void setIsCollectorPocketAllowed(Boolean isCollectorPocketAllowed) {
		this.isCollectorPocketAllowed = isCollectorPocketAllowed;
	}

	public Boolean getIsSuspencePocketAllowed() {
		return isSuspencePocketAllowed;
	}

	public void setIsSuspencePocketAllowed(Boolean isSuspencePocketAllowed) {
		this.isSuspencePocketAllowed = isSuspencePocketAllowed;
	}

	public String getStatusSearchString() {
		return statusSearchString;
	}

	public void setStatusSearchString(String statusSearchString) {
		this.statusSearchString = statusSearchString;
	}
}
