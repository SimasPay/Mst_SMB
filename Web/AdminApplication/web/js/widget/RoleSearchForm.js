/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.RoleSearchForm = function (config) {
    var localConfig = Ext.apply({title:''}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Role Search'),
        bodyStyle:'padding:5px 5px 0',
        defaultType: 'textfield',
        
        items: [{
            xtype:'remotedropdown',
            fieldLabel  : _('Role'),
            labelSeparator : '',
            anchor : '95%',
            emptyText : '<Select one..>',
            name: CmFinoFIX.message.JSRole.RoleID._name,
            store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSRole), 
            displayField: CmFinoFIX.message.JSRole.Entries.DisplayText._name, 
            valueField : CmFinoFIX.message.JSRole.Entries.ID._name, 
            hiddenName : CmFinoFIX.message.JSRole.RoleID._name,
            pageSize: 10,
            params: {start:0, limit:10},
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }
        ]
    });

    mFino.widget.RoleSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.RoleSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        mFino.widget.RoleSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
        this.on("render", function(){
            this.reloadRemoteDropDown();
        });
    },
    
    reloadRemoteDropDown : function(){
    	this.getForm().items.each(function(item) {
            if(item.getXType() == 'remotedropdown') {
                item.reload();
            }
        });
    },
    
    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        if(this.getForm().isValid()){
            var values = this.getForm().getValues();
            if(values.RoleID == "") {
            	values.RoleID = null;
            }
            this.fireEvent("search", values);
        } else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    }
});
