/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.DistributionChainTemplateQuery;
import com.mfino.domain.DistributionChainTemp;

/**
 * @author Sreenath
 *
 */
public interface DistributionChainTemplateService {
    /**
     * Returns list of distribution chain template objects for a particular user.
     * @return
     */
    public List<DistributionChainTemp> getDistributionChainTemplates(DistributionChainTemplateQuery query);
   
    /**
     * 
     * @param dctId
     * @return
     */
    public DistributionChainTemp getDistributionChainTemplateById(Long dctId);

}
