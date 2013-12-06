/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PermissionsForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        //frame : true,
        isEditable: true
    });
    mFino.widget.PermissionsForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PermissionsForm, Ext.FormPanel, {
    initComponent : function () {
    	 this.labelWidth = 0;
         this.labelPad = 0; 
         this.tbar =  ['->',{
             iconCls: 'mfino-button-save',
             itemId: 'permissions.save',
             tooltip: _('Save Permissions'),
             handler: this.onSave.createDelegate(this)
         }];
         mFino.widget.PermissionsForm.superclass.initComponent.call(this);
         this.on('render', this.removeDisabled, this);
    },    
    
    afterRender : function() {
    	mFino.widget.PermissionsForm.superclass.afterRender.call(this);
    	this.loadPermissionGroups();
    },
    
    setRecord : function(record) {
    	this.record = record;
    },
    
    removeDisabled: function(){
        var tb = this.getTopToolbar();
        var itemIDs = [];
        for(var i = 0; i < tb.items.length; i++){
            itemIDs.push(tb.items.get(i).itemId);
        }        
        for(i = 0; i < itemIDs.length; i++){
            var itemID = itemIDs[i];
            if(!mFino.auth.isEnabledItem(itemID)){
                var item = tb.getComponent(itemID);
                tb.remove(item);
            }
        }
    },   
    
    loadPermissionGroups : function() {
    	Ext.Ajax.request({					
			url: 'loadPermissionGroups.htm',
			method: 'post',			
			success: this.buildPermissionGroups,
		   	failure: function(){
		   		Ext.ux.Toast.msg(_('Failure'), "Error while loading permission item groups");
		   	},
		   	scope: this
		});
    },
    
    buildPermissionGroups : function(response, options) {
    	var data=Ext.util.JSON.decode(response.responseText);
    	this.renderedPermissionsList = "";
        var items = new Array();
        for(var i = 0; i < data.rows.length; i++) {
        	var groupEntry = data.rows[i];
        	var permissionGrp = new Array();
        	if(groupEntry.permItems) {
        		for(var j = 0; j < groupEntry.permItems.length; j++) {
            		var permission = groupEntry.permItems[j];
            		permissionGrp.push({boxLabel: permission.permDescription, name: permission.permNumber});
            		this.renderedPermissionsList = this.renderedPermissionsList + permission.permNumber + ",";
            	}   
        	}
        	if(permissionGrp.length > 0) {
        		var item = new Ext.form.FieldSet({        			
            		title       : groupEntry.permGroupName,
            		itemId		: 'group' + groupEntry.permGroupId,
    			    collapsible : true,    		    
    			    items       : [{	            
    		            xtype: 'checkboxgroup',
    		            hideLabel: true,    	            
    		            columns: 3,
    		            items: permissionGrp    		            
    		        }]
            	});
            	items.push(item);
        	}        	        	
        }        
        for(var i=0; i< items.length; i++) {
        	this.add(items[i]);
        }
        this.doLayout();
    },
    
    loadRolePermissions : function(role) {
    	this.resetFormItems();
    	this.role = role;
    	Ext.Ajax.request({					
			url: 'loadRolePermissions.htm',
			method: 'post',
			params: { 'role' : role },
			success: this.fillRolePermissions,
		   	failure: function(){
		   		Ext.ux.Toast.msg(_('Failure'), "Error while loading role permissions");
		   	},
		   	scope: this
		});
    	//var responseText = mFino.util.permissionsCache.get(roleEnumCode);
    },
    
    fillRolePermissions : function(response, options) {
    	var data=Ext.util.JSON.decode(response.responseText);
    	this.initialPermissions = "";
    	if(data.rows) {
    		for(var i=0; i < data.rows.length; i++) {
    			this.permGroup = data.rows[i];
    			var formItem = this.find('itemId', 'group' + this.permGroup.permGroupId)[0];
    			formItem.items.items[0].items.each(function(checkboxItem) {
    				if(this.permGroup.permItemsList.indexOf(checkboxItem.name) >= 0) {
    					checkboxItem.setValue(1);
    					this.initialPermissions = this.initialPermissions + checkboxItem.name + ",";
    				}
    			}, this);
    		}
    	}
    },
    
    resetFormItems : function() {
    	this.getForm().reset();
    },
    
    onSave : function() {
    	if(this.record && this.record.get(CmFinoFIX.message.JSRole.Entries.IsSystemUser._name) == false) {
    		Ext.Msg.alert("Alert", "Cannot change permissions of a non-system role");
    		return;
    	}
    	var updatedPermissions = JSON.stringify(this.form.getValues());
    	var renderedPermissions = this.renderedPermissionsList.split(",");
    	var addedPermissions = "";
    	var removedPermissions = "";
    	if(this.initialPermissions != undefined) {
    		var saveRequired = false;
    		for(var i = 0; i < renderedPermissions.length; i++) {
        		var permItem = renderedPermissions[i];
        		if(permItem.length > 0) {
        			if(this.initialPermissions.indexOf(permItem) < 0 && updatedPermissions.indexOf(permItem) >=0 ) {
            			addedPermissions = addedPermissions + permItem + ",";
            			saveRequired = true;
            		} else if(this.initialPermissions.indexOf(permItem) >= 0 && updatedPermissions.indexOf(permItem) < 0 ) {
            			removedPermissions = removedPermissions + permItem + ",";
            			saveRequired = true;
            		}
        		}        		
        	}
    		if(saveRequired) {
    			Ext.Msg.confirm(_("Confirm?"), _("Are you sure you want to save permissions"),
                        function(btn){
                            if(btn !== "yes"){
                                return;
                            }
                            Ext.Ajax.request({					
                				url: 'updateRolePermissions.htm',
                				method: 'post',
                				params: { 
                							'role' : this.role, 
                							'addedPermissions' : addedPermissions,
                							'removedPermissions' : removedPermissions
                						},
                				success: function(response, options) {
                					Ext.ux.Toast.msg(_('Success'), "Permissions saved successfully");
                					this.fillRolePermissions(response, options);
                				},
                			   	failure: function(){
                			   		Ext.ux.Toast.msg(_('Failure'), "Error while saving role permissions");
                			   	},
                			   	scope: this
                			});
                        }, this);    			
    		} else {
    			Ext.Msg.alert("Alert", "No updated permissions to save");
    		}
    	} else {
    		Ext.Msg.alert("Alert", "Select a role to save permissions");
    	}    	
    }
});

Ext.reg("permissionsForm", mFino.widget.PermissionsForm);
