package com.mfino.web.admin.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mfino.service.DistributionTreeService;
import com.mfino.service.TreeNode;

/**
 * @author Sasi
 * 
 */
@Controller("DistributionTreeController")
public class DistributionTreeController {
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("DistributionTreeServiceImpl")
	private DistributionTreeService dctTreeService;

	@RequestMapping(value = { "/distributionTree.htm" }, method = {
			RequestMethod.GET, RequestMethod.POST })
	public ModelAndView treeView(
			@RequestParam(required = true) Integer dctNodeType,
			@RequestParam(required = true) Long objectId,
			@RequestParam(required = false) String serviceName,
			@RequestParam(required = false) String distributionName,
			@RequestParam(required = false) Long dctId,
			@RequestParam(required = false) Long partnerId,
			@RequestParam(required = true) boolean getParents,
			@RequestParam(required = false) String srchDctName,
			@RequestParam(required = false) Long srchServiceId,
			@RequestParam(required = false) boolean addCheckBox
			) {
		log.info("DistributionTreeController : treeView BEGIN");
		ModelAndView mAv = new ModelAndView("treeNodesView");

		List<TreeNode> childNodeList = dctTreeService.getAllChildren(
				dctNodeType, objectId, distributionName, serviceName, dctId, srchDctName, srchServiceId);
		mAv.addObject("treeNodes", childNodeList);
		mAv.addObject("addCheckBox", addCheckBox);
		
		log.info("DistributionTreeController : treeView END");
		return mAv;
	}
	
}
