package com.mfino.dao.query;

public class CreditCardProductQuery extends BaseQuery{

	private String _productIndicatorCode;
	private Long companyID;
	
	public void setProductIndicatorCode(String _productIndicatorCode) {
        this._productIndicatorCode = _productIndicatorCode;
    }

    public String getProductIndicatorCode() {
        return _productIndicatorCode;
    }
    
    public Long getCompanyID() {
        return companyID;
}

    public void setCompanyID(Long companyID) {
        this.companyID = companyID;
    }
}
