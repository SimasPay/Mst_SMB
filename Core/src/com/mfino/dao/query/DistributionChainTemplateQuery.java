/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

/**
 *
 * @author xchen
 */
public class DistributionChainTemplateQuery extends BaseQuery {
	
    private String _distributionChainTemplateName;
    private String _createdBy;
    private String _exactdistributionChainTemplateName;
    private Long serviceIdSearch;
    private Long userIdSearch;
    
    public String getExactdistributionChainTemplateName() {
        return _exactdistributionChainTemplateName;
    }

    public void setExactdistributionChainTemplateName(String _exactdistributionChainTemplateName) {
        this._exactdistributionChainTemplateName = _exactdistributionChainTemplateName;
    }

    public String getCreatedBy() {
        return _createdBy;
    }

    public void setCreatedBy(String _createdBy) {
        this._createdBy = _createdBy;
    }

    /**
     * @return the _distributionChainTemplateName
     */
    public String getDistributionChainTemplateName() {
        return _distributionChainTemplateName;
    }

    /**
     * @param distributionChainTemplateName the _distributionChainTemplateName to set
     */
    public void setDistributionChainTemplateName(String distributionChainTemplateName) {
        this._distributionChainTemplateName = distributionChainTemplateName;
    }

	public Long getServiceIdSearch() {
		return serviceIdSearch;
	}

	public void setServiceIdSearch(Long serviceIdSearch) {
		this.serviceIdSearch = serviceIdSearch;
	}

	public Long getUserIdSearch() {
		return userIdSearch;
	}

	public void setUserIdSearch(Long userIdSearch) {
		this.userIdSearch = userIdSearch;
	}
}
