/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.DistributionChainTemplateQuery;
import com.mfino.domain.DistributionChainTemplate;

/**
 * @author Sreenath
 *
 */
public interface DistributionChainTemplateService {
    /**
     * Returns list of distribution chain template objects for a particular user.
     * @return
     */
    public List<DistributionChainTemplate> getDistributionChainTemplates(DistributionChainTemplateQuery query);
   
    /**
     * 
     * @param dctId
     * @return
     */
    public DistributionChainTemplate getDistributionChainTemplateById(Long dctId);

}
