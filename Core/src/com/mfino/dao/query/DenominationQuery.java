/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import java.math.BigDecimal;

/**
 *
 * @author Srinu
 */
public class DenominationQuery extends BaseQuery {

    private Long _billerId;
    private BigDecimal _amount;

    public BigDecimal getAmount() {
        return _amount;
    }

    public void setAmount(BigDecimal _amount) {
        this._amount = _amount;
    }

    public Long getBillerId() {
        return _billerId;
    }

    public void setBillerId(Long _billerId) {
        this._billerId = _billerId;
    }

}
