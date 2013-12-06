/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import java.util.Date;

/**
 *
 * @author sunil
 */
public class Person2PersonQuery extends BaseQuery{

    private String _mdn;
    private String _peerName;
    private Long _subscriberId;

    public Long getSubscriberId() {
        return _subscriberId;
    }

    public void setSubscriberId(Long _subscriberId) {
        this._subscriberId = _subscriberId;
    }
    
    public String getMdn() {
        return _mdn;
    }

    public void setMdn(String _mdn) {
        this._mdn = _mdn;
    }

    public String getPeerName() {
        return _peerName;
    }

    public void setPeerName(String _peerName) {
        this._peerName = _peerName;
    }
    


   

}

