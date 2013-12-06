package com.mfino.cc.message;

import java.util.List;

import com.mfino.domain.CreditCardProduct;

public class CCProductInfo {

    private String productDescription;
    private String productIndicatorCode;
    private Integer amount;
    private Long companyID;
    private List<CreditCardProduct> ccpList;
    
    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    
    public String getProductIndicatorCode() {
        return productIndicatorCode;
    }

    public void setProductIndicatorCode(String productIndicatorCode) {
        this.productIndicatorCode = productIndicatorCode;
    }
    
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    
    public Long getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Long companyID) {
        this.companyID = companyID;
    }
    
    public List<CreditCardProduct> getCCPList() {
        return ccpList;
    }

    public void setCCPList(List<CreditCardProduct> ccpList) {
        this.ccpList = ccpList;
    }
    
    /*public List<CreditCardProduct> getCCPListByMDN(String MDN) {
    	CreditCardProductDAO ccpDAO = new CreditCardProductDAO();
    	ccpDAO.get
        return ccpList;
    }

    public void setCCPListByMDN(List<CreditCardProduct> ccpList) {
        this.ccpList = ccpList;
    }*/
    
    
    
}
