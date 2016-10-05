/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.web.admin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mfino.domain.PermissionGroup;
import com.mfino.domain.PermissionItem;
import com.mfino.service.PermissionService;
import com.mfino.service.UserService;

/**
 * 
 * @author Srikanth
 */
@Controller
public class PermissionsController {
	
    @Autowired
    @Qualifier("PermissionServiceImpl")
    private PermissionService permissionService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

    private static Logger log = LoggerFactory.getLogger(PermissionsController.class);

    @RequestMapping("/loadPermissionGroups.htm")
    public ModelAndView loadPermissionGroups(HttpServletRequest request, HttpServletResponse response){
    	log.info("In loadPermissionGroups method");
    	ModelAndView mAv = new ModelAndView("permissionGroupsView");
    	List<PermissionGroup> list = permissionService.loadPermissionGroups();    	
    	mAv.addObject("permissionGroups", list);
    	return mAv;
    }
    
    @RequestMapping("/loadRolePermissions.htm")
    public ModelAndView loadRolePermissions(HttpServletRequest request, HttpServletResponse response){
    	log.info("In loadRolePermissions method");
    	ModelAndView mAv = new ModelAndView("rolePermissionsByGroupView");
    	String role = request.getParameter("role");
    	if(role != null) {
    		log.info("Role ->" + role);
    		List<PermissionItem> list = permissionService.loadRolePermissionsByGroup(Integer.valueOf(role));    	
        	mAv.addObject("rolePermissions", list);
    	}    	
    	return mAv;
    }    
    
    @RequestMapping("/updateRolePermissions.htm")
    public ModelAndView updateRolePermissions(HttpServletRequest request, HttpServletResponse response){
    	log.info("In updateRolePermissions method");
    	ModelAndView mAv = new ModelAndView("rolePermissionsByGroupView");
    	String role = request.getParameter("role");
    	String addedPermissions = request.getParameter("addedPermissions");
    	String removedPermissions = request.getParameter("removedPermissions");
    	if(role != null) {    		
    		if(StringUtils.isNotBlank(removedPermissions)) {
    			log.info("RemovedPermissions list ->" + removedPermissions + " requested by user:"+ userService.getCurrentUser()
						.getUsername());
    			List<Integer> deletePermList = new ArrayList<Integer>();
    			for(String s : removedPermissions.split(",")) {
    				if(StringUtils.isNotBlank(s)) {
    					deletePermList.add(Integer.valueOf(s));
    				}
    			}
    			if(!deletePermList.isEmpty()) {
    				permissionService.deleteRolePermissions(Integer.valueOf(role), deletePermList);
    			}    			
    		}
    		if(StringUtils.isNotBlank(addedPermissions)) {
    			log.info("AddedPermissions list ->" + addedPermissions + " requested by user:"+ userService.getCurrentUser()
						.getUsername());
    			List<Integer> addPermList = new ArrayList<Integer>();
    			for(String s : addedPermissions.split(",")) {
    				if(StringUtils.isNotBlank(s)) {
    					addPermList.add(Integer.valueOf(s));
    				}
    			}
    			if(!addPermList.isEmpty()) {
    				permissionService.addRolePermissions(Integer.valueOf(role), addPermList);
    			}    			
    		}
    		log.info("Loading the updated role permissions");
    		List<PermissionItem> list = permissionService.loadRolePermissionsByGroup(Integer.valueOf(role));    	
        	mAv.addObject("rolePermissions", list);
    	}    	
    	return mAv;
    }
}
