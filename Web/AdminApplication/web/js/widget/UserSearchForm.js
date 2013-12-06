/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.UserSearchForm = function (config) {
    var localConfig = Ext.apply({title:''}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('User Search'),
        bodyStyle:'padding:5px 5px 0',
        defaultType: 'textfield',
        
        items: [{
            fieldLabel: _('Username'),
            labelSeparator : '',
            anchor : '95%',
            name: CmFinoFIX.message.JSUsers.UsernameSearch._name,
            maxLength : 255,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }, {
            fieldLabel: _('First Name'),
            labelSeparator : '',
            anchor : '95%',
            name: CmFinoFIX.message.JSUsers.FirstNameSearch._name,
            maxLength : 255,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },{
            fieldLabel: _('Last Name'),
            labelSeparator : '',
            anchor : '95%',
            name: CmFinoFIX.message.JSUsers.LastNameSearch._name,
            maxLength : 255,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },{
            xtype:'remotedropdown',
            fieldLabel  : _('Role'),
            labelSeparator : '',
            anchor : '95%',
            emptyText : '<Select one..>',
            name: CmFinoFIX.message.JSUsers.RoleSearch._name,
            store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSRole), 
            displayField: CmFinoFIX.message.JSRole.Entries.DisplayText._name, 
            valueField : CmFinoFIX.message.JSRole.Entries.ID._name, 
            hiddenName : CmFinoFIX.message.JSUsers.RoleSearch._name,
            pageSize: 10,
            params: {start:0, limit:10},
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }, {
            fieldLabel: _('Status'),
            xtype:'enumdropdown',
            anchor : '95%',
            emptyText : '<Any>',
            enumId: CmFinoFIX.TagID.UserStatus,
            labelSeparator : '',
            name: CmFinoFIX.message.JSUsers.StatusSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },{
            fieldLabel: _('Restrictions'),
            width  : 130,
            labelSeparator : '',
            anchor : '95%',
            emptyText:'<Any>',
            xtype:'enumdropdown',
            enumId: CmFinoFIX.TagID.UserRestrictions,
            name: CmFinoFIX.message.JSUsers.RestrictionsSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        }
        ]
    });

    mFino.widget.UserSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.UserSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        mFino.widget.UserSearchForm.superclass.initComponent.call(this);
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
            //set the value to null if it is empty to fix below issue(#3385):
            //Users with lastname 'null' will be missed out in search done with empty lastname value.
            if(values.FirstNameSearch.trim() == "") {
            	values.FirstNameSearch = null;
            }
            if(values.LastNameSearch.trim() == "") {
            	values.LastNameSearch = null;
            }
            if(values.UsernameSearch.trim() == "") {
            	values.UsernameSearch = null;
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
