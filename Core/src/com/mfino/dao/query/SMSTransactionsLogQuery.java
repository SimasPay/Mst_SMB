/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author Srinu
 */
public class SMSTransactionsLogQuery extends BaseQuery{

    private String _Source;
    private String _DestMdn;

    private String _FieldID;

    private Long _PartnerID;

    public Long getPartnerID() {
        return _PartnerID;
    }

    public void setPartnerID(Long _PartnerID) {
        this._PartnerID = _PartnerID;
    }

    public String getDestMdn() {
        return _DestMdn;
    }

    public void setDestMdn(String _DestMdn) {
        this._DestMdn = _DestMdn;
    }

    public String getFieldID() {
        return _FieldID;
    }

    public void setFieldID(String _FieldID) {
        this._FieldID = _FieldID;
    }

    public Long getSMSCID() {
        return _SMSCID;
    }

    public void setSMSCID(Long _SMSCID) {
        this._SMSCID = _SMSCID;
    }

    public String getSource() {
        return _Source;
    }

    public void setSource(String _Source) {
        this._Source = _Source;
    }

    private Long _SMSCID;

}
