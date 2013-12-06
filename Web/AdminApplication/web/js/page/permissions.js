/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.permissions = function(config){ 

    var searchBox = new mFino.widget.RoleSearchForm(Ext.apply({
        height : 160
    }, config));
    
    var roleAddForm = new mFino.widget.FormWindowWithValidation(Ext.apply({
        form : new mFino.widget.RoleForm(config),
        height : 200,
        width: 400
    },config));
    
    var listBox = new mFino.widget.RoleList(Ext.apply({
    	height : 400,
    	anchor : "100%, -155"
    }, config));
    
    var detailsForm = new mFino.widget.RoleDetails(Ext.apply({
        height : 145
    }, config));
    
    var tabPanel = new mFino.widget.PermissionsTab(Ext.apply({
    	height : 400
    }, config));

    var panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            height : 165,
            anchor : "100%",
            autoScroll : true,
            tbar : [
            '<b class= x-form-tbar>' + _('Role Details') + '</b>',
            '->',
            {
                iconCls: 'mfino-button-user-add',
                tooltip : _('Add Role'),
                handler:  function(){                	
                	var record = new listBox.store.recordType();
                	roleAddForm.setTitle(_("Add Role"));
                    roleAddForm.setMode("add");
                    roleAddForm.show();
                    roleAddForm.setEditable(true);
                    roleAddForm.setRecord(record);
                    roleAddForm.setStore(listBox.store);
                }
            },
            {
                iconCls: 'mfino-button-user-edit',
                tooltip : _('Edit Role'),
                handler:  function(){
                	if(!detailsForm.record){
                        Ext.MessageBox.alert(_("Alert"), _("No Role selected!"));
                    } else {
                    	roleAddForm.setTitle( _("Edit Role"));
                    	roleAddForm.setMode("edit");
                    	roleAddForm.show();
                    	roleAddForm.setRecord(detailsForm.record);
                    	roleAddForm.setStore(detailsForm.store);
                    }                	
                }
            }],
            items: [ detailsForm ]
        },
        {
            anchor : "100%, -165",
            layout: "fit",            
            items: [ tabPanel ]
        }
        ]
    });    

    searchBox.on("search", function(values){
    	detailsForm.getForm().reset();
        detailsForm.record = null;
        listBox.store.baseParams = values;
        listBox.store.lastOptions = {
            params : {
                start : 0,
                limit : CmFinoFIX.PageSize.Default
            }
        };
        Ext.apply(listBox.store.lastOptions.params, values);
        listBox.store.load(listBox.store.lastOptions);        
    });

    listBox.on("defaultSearch", function() {
        searchBox.searchHandler();
    });
    
    listBox.on("clearSelected", function() {
   	 	detailsForm.getForm().reset();
        detailsForm.record = null;        
    });
    
    listBox.selModel.on("rowselect", function(sm, rowIndex, record){
        detailsForm.setRecord(record);
        detailsForm.setStore(listBox.store);
        tabPanel.setRecord(record);
        tabPanel.loadRolePermissions(record.get("ID"));  
    });

    var panel = new Ext.Panel({
        layout: "border",
        broder: false,
        width : 1020,
        items: [
        {
            region: 'west',
            width : 266,
            layout : "anchor",
            items:[ searchBox , listBox ]
        },
        {
            region: 'center',
            layout : "fit",
            items: [ panelCenter ]
        }
        ]
    });
    
    return panel;
};

