/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author ADMIN
 */
public class MDNRangeQuery extends BaseQuery{

private String startPrefix;
private String endPrefix;
private Long merchantId;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }


    public String getEndPrefix() {
        return endPrefix;
    }

    public void setEndPrefix(String endPrefix) {
        this.endPrefix = endPrefix;
    }

    public String getStartPrefix() {
        return startPrefix;
    }

    public void setStartPrefix(String startPrefix) {
        this.startPrefix = startPrefix;
    }

}
