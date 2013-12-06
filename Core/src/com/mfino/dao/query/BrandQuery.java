/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author admin
 */
public class BrandQuery extends BaseQuery  {
    private String _PrefixCode;
    private String _BrandName;
    private Long _CompanyId;
    private String _PrefixCodeLike;

    public String getPrefixCodeLike() {
        return _PrefixCodeLike;
    }

    public void setPrefixCodeLike(String _PrefixCodeLike) {
        this._PrefixCodeLike = _PrefixCodeLike;
    }

    public String getBrandName() {
        return _BrandName;
    }

    public void setBrandName(String _BrandName) {
        this._BrandName = _BrandName;
    }

    public Long getCompanyId() {
        return _CompanyId;
    }

    public void setCompanyId(Long _CompanyId) {
        this._CompanyId = _CompanyId;
    }


    public String getPrefixCode() {
        return _PrefixCode;
    }

    public void setPrefixCode(String _PrefixCode) {
        this._PrefixCode = _PrefixCode;
    }
}
