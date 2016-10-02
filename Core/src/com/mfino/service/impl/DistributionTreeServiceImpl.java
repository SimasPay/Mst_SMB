package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.DistributionChainTemplateQuery;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.DistributionChainTemplateService;
import com.mfino.service.DistributionTreeService;
import com.mfino.service.PartnerService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TreeNode;
import com.mfino.service.UserService;

/**
 * 
 * @author Sasi
 *
 */
@Service("DistributionTreeServiceImpl")
public class DistributionTreeServiceImpl implements DistributionTreeService{
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	@Qualifier("DistributionChainTemplateServiceImpl")
	private DistributionChainTemplateService distributionChainTemplateService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

    @Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public List<TreeNode> getAllChildren(Integer nodeType, Long objectId, String distributionName, String serviceName, Long dctId, String srchDctName, Long srchServiceId){
		log.debug("DistributionTreeService");
		List<TreeNode> childNodesList = new ArrayList<TreeNode>();
		
		User user = userService.getCurrentUser();
		
		if(CmFinoFIX.NodeType_root.equals(nodeType)){
			childNodesList =  getAllDistributionChainTemplates(user, srchDctName, srchServiceId);
		}
		else if(CmFinoFIX.NodeType_dct.equals(nodeType)){
			DistributionChainTemplate distributionChainTemplate = distributionChainTemplateService.getDistributionChainTemplateById(objectId);
			childNodesList =  getAllChildren(distributionChainTemplate);
		}
		else if(CmFinoFIX.NodeType_partner.equals(nodeType)){
			DistributionChainTemplate distributionChainTemplate = distributionChainTemplateService.getDistributionChainTemplateById(dctId);
			Partner partner = partnerService.getPartnerById(objectId);
			DAOFactory.getInstance().getPocketDAO();
			childNodesList = getAllChildren(distributionChainTemplate, partner);
		}
		
		return childNodesList;
	}
	
	/**
	 * Get all children for this DCT
	 * @param dct
	 * @return
	 */
    @Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public List<TreeNode> getAllChildren(DistributionChainTemplate dct){
		log.info("DistributionTreeService :: getAllChildren(DistributionChainTemplate dct) BEGIN");
		List<TreeNode> childNodesList = new ArrayList<TreeNode>();
		
		PartnerQuery partnerQuery = new PartnerQuery();
		partnerQuery.setDistributionChainTemplateId(dct.getId().longValue());
		partnerQuery.setFirstLevelPartnerSearch(true);
		
		List<Partner> partnersList = partnerService.get(partnerQuery);
		
		for(Partner partner : partnersList){
			childNodesList.add(getTreeNode(dct, partner));
		}
		
		log.info("DistributionTreeService :: getAllChildren(DistributionChainTemplate dct) END childNodesList="+childNodesList);
		
		return childNodesList;
	}
	
	
	/**
	 * Get all children for this partner.
	 * @param partner
	 * @return
	 */
    @Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public List<TreeNode> getAllChildren(DistributionChainTemplate distributionChainTemplate, Partner parent){
		log.info("DistributionTreeService :: getAllChildren(DistributionChainTemplate distributionChainTemplate, Partner partner) BEGIN");
		List<TreeNode> childNodesList = new ArrayList<TreeNode>();
		
		PartnerQuery partnerQuery = new PartnerQuery();
		partnerQuery.setDistributionChainTemplateId(distributionChainTemplate.getId().longValue());
		partnerQuery.setParentId(parent.getId().longValue());
		
		List<Partner> partnersList = partnerService.get(partnerQuery);
		
		for(Partner partner : partnersList){
			childNodesList.add(getTreeNode(distributionChainTemplate, partner));
		}
		
		log.info("DistributionTreeService :: getAllChildren(DistributionChainTemplate distributionChainTemplate, Partner partner) END childNodesList="+childNodesList);
		
		return childNodesList;
	}
	
	/**
	 * Get all dcts for this user.
	 * @param user
	 * @return
	 */
    @Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public List<TreeNode> getAllDistributionChainTemplates(User user, String srchDctName, Long srchServiceId){
		log.debug("DistributionTreeService :: getAllDistributionChainTemplates() BEGIN");
		
		List<TreeNode> dctTreeNodes = new ArrayList<TreeNode>();
		boolean isSystemUser = ((CmFinoFIX.Role_Service_Partner.equals(user.getRole())) || (CmFinoFIX.Role_Business_Partner.equals(user.getRole()))) ? false : true; 
		
		if(isSystemUser){
			DistributionChainTemplateQuery query = new DistributionChainTemplateQuery();
			query.setDistributionChainTemplateName(srchDctName);
			query.setServiceIdSearch(srchServiceId);
			List<DistributionChainTemplate> distributionChainTemplates = distributionChainTemplateService.getDistributionChainTemplates(query);
			for(DistributionChainTemplate dct : distributionChainTemplates){
				dctTreeNodes.add(getTreeNode(dct));
			}
		}
		else{
			Partner partner = userService.getPartner();
			dctTreeNodes = getAllParents(partner, srchDctName, srchServiceId);
		}
		
		log.debug("DistributionTreeService :: getAllDistributionChainTemplates() END");
		return dctTreeNodes;
	}
	
	private TreeNode getTreeNode(DistributionChainTemplate distributionChainTemplate, Partner partner){
		TreeNode treeNode = new TreeNode();
		
		treeNode.setDctId(distributionChainTemplate.getId().longValue());
		treeNode.setServiceId(distributionChainTemplate.getServiceid());
		treeNode.setObjectId(partner.getId().longValue());
		treeNode.setText(partner.getTradename());
		treeNode.setIcon(getIcon(partner));
		treeNode.setNodeType(CmFinoFIX.NodeType_partner);
		treeNode.setPartnerId(partner.getId().longValue());
		treeNode.setPermissionType(CmFinoFIX.PermissionType_Read_Write);
		treeNode.setMdn(partner.getSubscriber().getSubscriberMdns().iterator().next().getMdn());
		treeNode.setSubscriberId(partner.getSubscriber().getId().longValue());
		treeNode.setBusinessPartnerType(partner.getBusinesspartnertype().intValue());
		treeNode.setLevels(distributionChainTemplate.getDistributionChainLvls().size());
		Pocket p = subscriberService.getDefaultPocket(partner.getSubscriber().getId().longValue(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
		
		BigDecimal currBalance = new BigDecimal(p.getCurrentbalance());
		treeNode.setBalance(currBalance);
		return treeNode;
	}
	
	private TreeNode getTreeNode(DistributionChainTemplate distributionChainTemplate){
		TreeNode treeNode = new TreeNode();
		
		treeNode.setObjectId(distributionChainTemplate.getId().longValue());
		treeNode.setText(distributionChainTemplate.getName());
		treeNode.setIcon("resources/images/distribute.png");
		treeNode.setNodeType(CmFinoFIX.NodeType_dct);
		treeNode.setDctId(distributionChainTemplate.getId().longValue());
		treeNode.setServiceId(distributionChainTemplate.getServiceid());
		treeNode.setPartnerId(-1L);
		treeNode.setPermissionType(CmFinoFIX.PermissionType_Read_Write);
		treeNode.setLevels(distributionChainTemplate.getDistributionChainLvls().size());
		
		return treeNode;
	}
	
    @Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public List<TreeNode> getAllParents(Partner partner, String srchDctName, Long srchServiceId){
		log.info("DistributionTreeService :: getAllParents(Partner partner) BEGIN");
		List<TreeNode> nodesList = new ArrayList<TreeNode>();
		
		if(partner == null){
			return nodesList;
		}
		
		for(PartnerServices partnerService: partner.getPartnerServicesesForPartnerid()){
			
			boolean matchedPartnerService = true;
			
			/*This logic is for matching with search criteria.*/
			DistributionChainTemplate dct = partnerService.getDistributionChainTemp();
			if(dct != null){
				if(StringUtils.isNotBlank(srchDctName)){
					if(!(dct.getName().toLowerCase().startsWith(srchDctName.toLowerCase()))){
						matchedPartnerService = false;
					}
				}
				
				if(srchServiceId != null){
					if(!(srchServiceId.equals(dct.getServiceid()))){
						matchedPartnerService = false;
					}
				}
			}
			else{
				matchedPartnerService = false;
			}
			
			if(matchedPartnerService){
				
				TreeNode leafNode = getTreeNode(partnerService.getDistributionChainTemp(), partner);
				leafNode.setSelected(true);
				leafNode.setPermissionType(CmFinoFIX.PermissionType_Read_Only);
				TreeNode currentNode = leafNode; //we have to go bot-up
				PartnerServices tmpPartnerService = partnerService;
				
				if(null != tmpPartnerService.getPartnerByParentid()){
					while(null != tmpPartnerService.getPartnerByParentid()){
						Partner parent = tmpPartnerService.getPartnerByParentid();
						TreeNode parentNode = getTreeNode(tmpPartnerService.getDistributionChainTemp(), parent);
						parentNode.setDisabled(true);
						parentNode.setPermissionType(CmFinoFIX.PermissionType_No_Permission);
						currentNode.addParent(parentNode);
						currentNode = parentNode;
						
						for(PartnerServices parentPartnerService : parent.getPartnerServicesesForPartnerid()){
							if(partnerService.getService().getId().equals(parentPartnerService.getService().getId())){
								tmpPartnerService = parentPartnerService;
								if(null == tmpPartnerService.getPartnerByParentid()){
									TreeNode dctNode = getTreeNode(tmpPartnerService.getDistributionChainTemp());
									dctNode.setDisabled(true);
									dctNode.setPermissionType(CmFinoFIX.PermissionType_No_Permission);
									currentNode.addParent(dctNode);
									nodesList.add(dctNode);
									break;
								}
							}
						}
					}
				}
				else{
					TreeNode dctNode = getTreeNode(tmpPartnerService.getDistributionChainTemp());
					dctNode.setDisabled(true);
					dctNode.setPermissionType(CmFinoFIX.PermissionType_No_Permission);
					currentNode.addParent(dctNode);
					nodesList.add(dctNode);
				}
			}
		}
		
		log.info("DistributionTreeService :: getAllParents(Partner partner) END nodesList="+nodesList);
		return nodesList;
	}
	
	public String getIcon(Partner partner){
		String icon = "resources/images/customer_green.png";
		
		if(partner != null){
			Long tempPartnerStatus = partner.getPartnerstatus();
			Long tempSubsRestrictions = partner.getSubscriber().getRestrictions();
			if((null != partner.getSubscriber().getUpgradestate()) && (CmFinoFIX.UpgradeState_Upgradable.equals(partner.getSubscriber().getUpgradestate()))){
				icon = "resources/images/customer_white.png";
			}
			else if((null != partner.getSubscriber().getUpgradestate()) && (CmFinoFIX.UpgradeState_Rejected.equals(partner.getSubscriber().getUpgradestate()))){
				icon = "resources/images/customer_red.png";
			}
			else if((null != partner.getSubscriber().getUpgradestate()) && (CmFinoFIX.UpgradeState_none.equals(partner.getSubscriber().getUpgradestate()))){
				icon = "resources/images/customer.png";
			}
			else if(((null != tempPartnerStatus) && (CmFinoFIX.MDNStatus_Active.equals(partner.getPartnerstatus()))) &&
					((null != tempSubsRestrictions) && (partner.getSubscriber().getRestrictions() > CmFinoFIX.SubscriberRestrictions_None))){
				icon = "resources/images/customer_orange.png";
			}
			else if((null != tempPartnerStatus) && (CmFinoFIX.MDNStatus_Active.equals(partner.getPartnerstatus()))){
				icon = "resources/images/customer_green.png";
			}
			else if((null != tempPartnerStatus) && (CmFinoFIX.MDNStatus_InActive.equals(partner.getPartnerstatus()))){
				icon = "resources/images/customer_yellow.png";
			}
			else if((null != tempPartnerStatus) && (CmFinoFIX.MDNStatus_Suspend.equals(partner.getPartnerstatus()))){
				icon = "resources/images/customer_red.png";
			}
			else if((null != tempPartnerStatus) && (CmFinoFIX.MDNStatus_Retired.equals(partner.getPartnerstatus()))){
				icon = "resources/images/customer_grey.png";
			}
			else if((null != tempPartnerStatus) && (partner.getPartnerstatus() > CmFinoFIX.MDNStatus_Active)){
				icon = "resources/images/customer_black.png";
			}
		}
		
		return icon;
	}
}
