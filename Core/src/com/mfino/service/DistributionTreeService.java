/**
 * 
 */
package com.mfino.service;

import java.util.List;

import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.Partner;
import com.mfino.domain.User;

/**
 * @author Sreenath
 *
 */
public interface DistributionTreeService {
	
	/**
	 * 
	 * @param nodeType
	 * @param objectId
	 * @param distributionName
	 * @param serviceName
	 * @param dctId
	 * @param srchDctName
	 * @param srchServiceId
	 * @return
	 */
	public List<TreeNode> getAllChildren(Integer nodeType, Long objectId, String distributionName, String serviceName, Long dctId, String srchDctName, Long srchServiceId);
	
	/**
	 * Get all children for this DCT
	 * @param dct
	 * @return
	 */
	public List<TreeNode> getAllChildren(DistributionChainTemplate dct);
	
	
	/**
	 * Get all children for this partner.
	 * @param partner
	 * @return
	 */
	public List<TreeNode> getAllChildren(DistributionChainTemplate distributionChainTemplate, Partner parent);
	
	/**
	 * Get all dcts for this user.
	 * @param user
	 * @return
	 */
	public List<TreeNode> getAllDistributionChainTemplates(User user, String srchDctName, Long srchServiceId);
	
	/**
	 * 	
	 * @param partner
	 * @param srchDctName
	 * @param srchServiceId
	 * @return
	 */
	public List<TreeNode> getAllParents(Partner partner, String srchDctName, Long srchServiceId);
	
	/**
	 * 
	 * @param partner
	 * @return
	 */
	public String getIcon(Partner partner);

}
