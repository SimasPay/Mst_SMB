/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PermissionsTab = function(config){
    mFino.widget.PermissionsTab.superclass.constructor.call(this, config);
};

Ext.extend(mFino.widget.PermissionsTab, Ext.TabPanel, {
    initComponent : function () {
        var config = this.initialConfig;
        
        this.permissionsForm = new mFino.widget.PermissionsForm(Ext.apply({
            layout: "form",
            autoScroll: true
        }, config)); 

        this.activeTab = 0;
        this.items = [
        {
            title: _('Permissions'),
            layout : "fit",           
            items:  this.permissionsForm
        }
        ];
        mFino.widget.PermissionsTab.superclass.initComponent.call(this);       
    },
    
    loadRolePermissions : function(role) {
    	this.permissionsForm.loadRolePermissions(role);
    },
    
    setRecord : function(record) {
    	this.permissionsForm.setRecord(record);
    }    
});

