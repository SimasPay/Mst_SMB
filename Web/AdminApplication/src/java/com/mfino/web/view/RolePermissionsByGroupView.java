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

import com.mfino.domain.PermissionGroup;
import com.mfino.domain.PermissionItem;

/**
 * @author Srikanth
 *
 */
public class RolePermissionsByGroupView extends AbstractView {
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("RolePermissionsByGroupView :: renderMergedOutputMode BEGIN");
		if(null != model){
			this.setContentType("text/html");
			JSONObject json = new JSONObject();
	        json.put("success", true);
			List<PermissionItem> permissionItems = (List<PermissionItem>) model.get("rolePermissions");
			JSONArray permGroupsJsonArray = new JSONArray();
			if(permissionItems != null) {
				long presentGroupId = -1;
				StringBuffer sb = null;
				JSONObject jsonPermGroup = null;
				for(PermissionItem permissionItem : permissionItems){ 	// PermissionItems retrieved, ordering by group Id, are parsed to 
																		// generate permGroupId, permItemsList map 
					PermissionGroup permissionGroup = permissionItem.getPermissionGroup();
					if(permissionGroup != null) { //skip permission items that are not associated to any permission group
						long permissionGroupId = permissionGroup.getId().longValue();					
						if(permissionGroupId != presentGroupId) {
							if(presentGroupId > 0) {
								jsonPermGroup = new JSONObject();
								jsonPermGroup.put("permGroupId", presentGroupId);
								jsonPermGroup.put("permItemsList", sb.toString());
								permGroupsJsonArray.add(jsonPermGroup);
							}
							sb = new StringBuffer(Long.valueOf(permissionItem.getPermission()).toString());
							presentGroupId = permissionGroupId;
						} else {
							sb.append("," + Long.valueOf(permissionItem.getPermission()).toString());
						}
					}										
				}
				if(presentGroupId > 0) {
					jsonPermGroup = new JSONObject();
					jsonPermGroup.put("permGroupId", presentGroupId);
					jsonPermGroup.put("permItemsList", sb.toString());
					permGroupsJsonArray.add(jsonPermGroup);
				}
			}			
			json.put("rows", permGroupsJsonArray);
			PrintWriter out = response.getWriter();
			out.print(json.toString());
			out.flush();
		}
	}
}
