/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author xchen
 */
public class DistributionChainLevelQuery extends BaseQuery {
    private Long _distributionChainTemplateID;
    private Integer _level;

    public Integer getLevel() {
        return _level;
    }

    public void setLevel(Integer _level) {
        this._level = _level;
    }

    /**
     * @return the _distributionChainTemplateID
     */
    public Long getDistributionChainTemplateID() {
        return _distributionChainTemplateID;
    }


    /**
     * @param distributionChainTemplateID the _distributionChainTemplateID to set
     */
    public void setDistributionChainTemplateID(Long distributionChainTemplateID) {
        this._distributionChainTemplateID = distributionChainTemplateID;
    }
}
