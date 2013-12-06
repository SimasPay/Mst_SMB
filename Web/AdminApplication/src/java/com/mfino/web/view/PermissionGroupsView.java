package com.mfino.web.view;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

import com.mfino.domain.PermissionGroup;
import com.mfino.domain.PermissionItems;

/**
 * @author Srikanth
 *
 */
public class PermissionGroupsView extends AbstractView {
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("PermissionGroupsView :: renderMergedOutputMode BEGIN");
		if(null != model){
			this.setContentType("text/html");
			JSONObject json = new JSONObject();
	        json.put("success", true);
			List<PermissionGroup> permissionGroups = (List<PermissionGroup>) model.get("permissionGroups");
			JSONArray permGroupsJsonArray = new JSONArray();
			if(permissionGroups != null) {
				for(PermissionGroup permissionGroup : permissionGroups){
					JSONObject jsonPermGroup = toJson(permissionGroup);
					permGroupsJsonArray.add(jsonPermGroup);
				}
			}			
			json.put("rows", permGroupsJsonArray);
			PrintWriter out = response.getWriter();
			out.print(json.toString());
			out.flush();
		}
	}
	
	public JSONObject toJson(PermissionGroup permissionGroup){		
		JSONObject jsonPermGroup = new JSONObject();
		jsonPermGroup.put("permGroupId", permissionGroup.getID());
		jsonPermGroup.put("permGroupName", permissionGroup.getPermissionGroupName());		
		JSONArray permissionItems = new JSONArray();
		Set<PermissionItems> permItemsSet = permissionGroup.getPermissionItemsFromPermissionGroupID();
		Iterator<PermissionItems> iterator = permItemsSet.iterator();
		while(iterator.hasNext()) {
			JSONObject jsonPermItemNode = new JSONObject();
			PermissionItems permissionItem = iterator.next();
			jsonPermItemNode.put("permNumber", permissionItem.getPermission());
			jsonPermItemNode.put("permDescription", permissionItem.getDescription());
			jsonPermItemNode.put("permItemId", permissionItem.getItemID());
			permissionItems.add(jsonPermItemNode);
		}		
		if(permItemsSet.size() > 0){
			jsonPermGroup.put("permItems", permissionItems);
		}		
		return jsonPermGroup;
	}
}
