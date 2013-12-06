package com.mfino.web.view;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

import com.mfino.service.TreeNode;

/**
 * @author Sasi
 *
 */
public class TreeNodesView extends AbstractView {
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("TreeNodesView :: renderMergedOutputMode BEGIN");
		if(null != model){
			this.setContentType("text/html");
			
			List<TreeNode> treeNodes = (List<TreeNode>)model.get("treeNodes");
			boolean addCheckBox = (Boolean)model.get("addCheckBox");
			JSONArray treeNodesJsonArray = new JSONArray();
			
			for(TreeNode treeNode : treeNodes){
				JSONObject jsonTreeNode = toJson(treeNode,addCheckBox);
				treeNodesJsonArray.add(jsonTreeNode);
			}
			
			PrintWriter out = response.getWriter();
			out.print(treeNodesJsonArray.toString());
			out.flush();
		}
	}
	
	public JSONObject toJson(TreeNode treeNode, boolean addCheckBox){
		
		JSONObject jsonTreeNode = new JSONObject();
//		jsonTreeNode.put("id", treeNode.getObjectId());
		jsonTreeNode.put("text", treeNode.getText());
		jsonTreeNode.put("icon", treeNode.getIcon());
		jsonTreeNode.put("objectId", treeNode.getObjectId());
		jsonTreeNode.put("dctNodeType", treeNode.getNodeType());
		jsonTreeNode.put("level", treeNode.getLevel());
		jsonTreeNode.put("serviceId", treeNode.getServiceId());
		if(addCheckBox) {
			jsonTreeNode.put("checked", false);
		} else {
			jsonTreeNode.put("checked", null);
		}
		if((null != treeNode.getChildren()) && (treeNode.getChildren().size() > 0)){
			jsonTreeNode.put("expanded", true);
		}
		
		if(treeNode.isDisabled()){
			jsonTreeNode.put("disabled", treeNode.isDisabled());
			jsonTreeNode.put("expandable", false);
		}
		
		jsonTreeNode.put("PermissionType", treeNode.getPermissionType());
		
		if(treeNode.isSelected()){
			jsonTreeNode.put("selected", treeNode.isSelected());
		}
		
		jsonTreeNode.put("dctId", treeNode.getDctId());
		jsonTreeNode.put("partnerId", treeNode.getPartnerId());
		jsonTreeNode.put("mdn", treeNode.getMdn());
		jsonTreeNode.put("subscriberId", treeNode.getSubscriberId());
		jsonTreeNode.put("businessPartnerType", treeNode.getBusinessPartnerType());
		jsonTreeNode.put("levels", treeNode.getLevels());
		jsonTreeNode.put("balance", treeNode.getBalance());
		
		JSONArray childNodes = new JSONArray();
		
		for(TreeNode childNode : treeNode.getChildren()){
			JSONObject jsonChildNode = toJson(childNode,addCheckBox);
			childNodes.add(jsonChildNode);
		}
		
		if(childNodes.size() > 0){
			jsonTreeNode.put("children", childNodes);
		}
		
		return jsonTreeNode;
	}
}
