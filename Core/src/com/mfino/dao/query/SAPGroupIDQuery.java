/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author xchen
 */
public class SAPGroupIDQuery extends BaseQuery {
    private String _groupID;
    private String _groupIDName;

    /**
     * @return the _groupID
     */
    public String getGroupID() {
        return _groupID;
    }

    /**
     * @param groupID the _groupID to set
     */
    public void setGroupID(String groupID) {
        this._groupID = groupID;
    }

    /**
     * @return the _groupIDName
     */
    public String getGroupIDName() {
        return _groupIDName;
    }

    /**
     * @param groupIDName the _groupIDName to set
     */
    public void setGroupIDName(String groupIDName) {
        this._groupIDName = groupIDName;
    }

}
