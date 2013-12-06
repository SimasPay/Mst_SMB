/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao.query;

/**
 *
 * @author ADMIN
 */
public class MerchantPrefixCodeQuery extends BaseQuery {

    String billerName;
    Integer merchantPrefixCode;

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public Integer getMerchantPrefixCode() {
        return merchantPrefixCode;
    }

    public void setMerchantPrefixCode(Integer merchantPrefixCode) {
        this.merchantPrefixCode = merchantPrefixCode;
    }
}
