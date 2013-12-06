package com.mfino.service.impl;

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
		partnerQuery.setDistributionChainTemplateId(dct.getID());
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
		partnerQuery.setDistributionChainTemplateId(distributionChainTemplate.getID());
		partnerQuery.setParentId(parent.getID());
		
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
		
		treeNode.setDctId(distributionChainTemplate.getID());
		treeNode.setServiceId(distributionChainTemplate.getService().getID());
		treeNode.setObjectId(partner.getID());
		treeNode.setText(partner.getTradeName());
		treeNode.setIcon(getIcon(partner));
		treeNode.setNodeType(CmFinoFIX.NodeType_partner);
		treeNode.setPartnerId(partner.getID());
		treeNode.setPermissionType(CmFinoFIX.PermissionType_Read_Write);
		treeNode.setMdn(partner.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next().getMDN());
		treeNode.setSubscriberId(partner.getSubscriber().getID());
		treeNode.setBusinessPartnerType(partner.getBusinessPartnerType());
		treeNode.setLevels(distributionChainTemplate.getDistributionChainLevelFromTemplateID().size());
		Pocket p = subscriberService.getDefaultPocket(partner.getSubscriber().getID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
		treeNode.setBalance(p.getCurrentBalance());
		return treeNode;
	}
	
	private TreeNode getTreeNode(DistributionChainTemplate distributionChainTemplate){
		TreeNode treeNode = new TreeNode();
		
		treeNode.setObjectId(distributionChainTemplate.getID());
		treeNode.setText(distributionChainTemplate.getName());
		treeNode.setIcon("resources/images/distribute.png");
		treeNode.setNodeType(CmFinoFIX.NodeType_dct);
		treeNode.setDctId(distributionChainTemplate.getID());
		treeNode.setServiceId(distributionChainTemplate.getService().getID());
		treeNode.setPartnerId(-1L);
		treeNode.setPermissionType(CmFinoFIX.PermissionType_Read_Write);
		treeNode.setLevels(distributionChainTemplate.getDistributionChainLevelFromTemplateID().size());
		
		return treeNode;
	}
	
    @Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public List<TreeNode> getAllParents(Partner partner, String srchDctName, Long srchServiceId){
		log.info("DistributionTreeService :: getAllParents(Partner partner) BEGIN");
		List<TreeNode> nodesList = new ArrayList<TreeNode>();
		
		if(partner == null){
			return nodesList;
		}
		
		for(PartnerServices partnerService: partner.getPartnerServicesFromPartnerID()){
			
			boolean matchedPartnerService = true;
			
			/*This logic is for matching with search criteria.*/
			DistributionChainTemplate dct = partnerService.getDistributionChainTemplate();
			if(dct != null){
				if(StringUtils.isNotBlank(srchDctName)){
					if(!(dct.getName().toLowerCase().startsWith(srchDctName.toLowerCase()))){
						matchedPartnerService = false;
					}
				}
				
				if(srchServiceId != null){
					if(!(srchServiceId.equals(dct.getService().getID()))){
						matchedPartnerService = false;
					}
				}
			}
			else{
				matchedPartnerService = false;
			}
			
			if(matchedPartnerService){
				
				TreeNode leafNode = getTreeNode(partnerService.getDistributionChainTemplate(), partner);
				leafNode.setSelected(true);
				leafNode.setPermissionType(CmFinoFIX.PermissionType_Read_Only);
				TreeNode currentNode = leafNode; //we have to go bot-up
				PartnerServices tmpPartnerService = partnerService;
				
				if(null != tmpPartnerService.getPartnerByParentID()){
					while(null != tmpPartnerService.getPartnerByParentID()){
						Partner parent = tmpPartnerService.getPartnerByParentID();
						TreeNode parentNode = getTreeNode(tmpPartnerService.getDistributionChainTemplate(), parent);
						parentNode.setDisabled(true);
						parentNode.setPermissionType(CmFinoFIX.PermissionType_No_Permission);
						currentNode.addParent(parentNode);
						currentNode = parentNode;
						
						for(PartnerServices parentPartnerService : parent.getPartnerServicesFromPartnerID()){
							if(partnerService.getService().getID().equals(parentPartnerService.getService().getID())){
								tmpPartnerService = parentPartnerService;
								if(null == tmpPartnerService.getPartnerByParentID()){
									TreeNode dctNode = getTreeNode(tmpPartnerService.getDistributionChainTemplate());
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
					TreeNode dctNode = getTreeNode(tmpPartnerService.getDistributionChainTemplate());
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
			if((null != partner.getSubscriber().getUpgradeState()) && (CmFinoFIX.UpgradeState_Upgradable.equals(partner.getSubscriber().getUpgradeState()))){
				icon = "resources/images/customer_white.png";
			}
			else if((null != partner.getSubscriber().getUpgradeState()) && (CmFinoFIX.UpgradeState_Rejected.equals(partner.getSubscriber().getUpgradeState()))){
				icon = "resources/images/customer_red.png";
			}
			else if((null != partner.getSubscriber().getUpgradeState()) && (CmFinoFIX.UpgradeState_none.equals(partner.getSubscriber().getUpgradeState()))){
				icon = "resources/images/customer.png";
			}
			else if(((null != partner.getPartnerStatus()) && (CmFinoFIX.MDNStatus_Active.equals(partner.getPartnerStatus()))) &&
					((null != partner.getSubscriber().getRestrictions()) && (partner.getSubscriber().getRestrictions() > CmFinoFIX.SubscriberRestrictions_None))){
				icon = "resources/images/customer_orange.png";
			}
			else if((null != partner.getPartnerStatus()) && (CmFinoFIX.MDNStatus_Active.equals(partner.getPartnerStatus()))){
				icon = "resources/images/customer_green.png";
			}
			else if((null != partner.getPartnerStatus()) && (CmFinoFIX.MDNStatus_InActive.equals(partner.getPartnerStatus()))){
				icon = "resources/images/customer_yellow.png";
			}
			else if((null != partner.getPartnerStatus()) && (CmFinoFIX.MDNStatus_Suspend.equals(partner.getPartnerStatus()))){
				icon = "resources/images/customer_red.png";
			}
			else if((null != partner.getPartnerStatus()) && (CmFinoFIX.MDNStatus_Retired.equals(partner.getPartnerStatus()))){
				icon = "resources/images/customer_grey.png";
			}
			else if((null != partner.getPartnerStatus()) && (partner.getPartnerStatus() > CmFinoFIX.MDNStatus_Active)){
				icon = "resources/images/customer_black.png";
			}
		}
		
		return icon;
	}
}
