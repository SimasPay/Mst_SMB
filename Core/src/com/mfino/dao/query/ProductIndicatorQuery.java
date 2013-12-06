/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author Diwakar
 */
public class ProductIndicatorQuery extends BaseQuery {
    private Integer _transactionType;
    private Integer _companyCode;

    public Integer getCompanyCode() {
        return _companyCode;
    }

    public void setCompanyCode(Integer _companyCode) {
        this._companyCode = _companyCode;
    }
    private String _productCode;

    

    public String getProductCode() {
        return _productCode;
    }

    public void setProductCode(String _productCode) {
        this._productCode = _productCode;
    }

    public Integer getTransactionType() {
        return _transactionType;
    }

    public void setTransactionType(Integer _transactionType) {
        this._transactionType = _transactionType;
    }
}
