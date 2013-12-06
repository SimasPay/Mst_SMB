/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.PocketTemplateConfigSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Pocket Template Config Search'),
        bodyStyle:'padding:5px 5px 0',
        items: [
        {
            xtype: 'enumdropdown',
            labelWidth : 100,
            fieldLabel: _('Subscriber Type'),
            labelSeparator : '',
            anchor:'98%',
            enumId : CmFinoFIX.TagID.SubscriberType,
            name: CmFinoFIX.message.JSPocketTemplateConfig.SubscriberType._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this),
			    select: function(field) {
                            	var subType= field.getValue();
								if(subType==CmFinoFIX.SubscriberType.Subscriber){
									this.findParentByType().find('fieldLabel',"Business Partner Type")[0].setValue('');
									this.findParentByType().find('fieldLabel',"Business Partner Type")[0].setDisabled(true);								
								}else if(subType==CmFinoFIX.SubscriberType.Partner){
									this.findParentByType().find('fieldLabel',"Business Partner Type")[0].setDisabled(false);
								}
                         }
            }

        },
		{
            xtype: 'enumdropdown',
            labelWidth : 100,
            fieldLabel: _('Business Partner Type'),
            labelSeparator : '',
            anchor:'98%',
            enumId : CmFinoFIX.TagID.BusinessPartnerType,
            name: CmFinoFIX.message.JSPocketTemplateConfig.BusinessPartnerType._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }

        },
        {
            xtype: 'enumdropdown',
            labelWidth : 100,
            fieldLabel: _('Pocket Type'),
            labelSeparator : '',
            anchor:'98%',
            enumId : CmFinoFIX.TagID.PocketType,
            name: CmFinoFIX.message.JSPocketTemplateConfig.PocketTypeSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }

        },
        {
            xtype: 'enumdropdown',
            fieldLabel: _('Commodity Type'),
            labelSeparator : '',
            anchor:'98%',
            enumId : CmFinoFIX.TagID.Commodity,
            name: CmFinoFIX.message.JSPocketTemplateConfig.CommodityTypeSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {//added enhancement for #2714
        	xtype : "remotedropdown",
        	anchor : '98%',
        	allowBlank: true,
        	fieldLabel :"Group",
        	pageSize : 5,
        	emptyText : _('<select one..>'),
        	RPCObject : CmFinoFIX.message.JSGroup,
        	displayField: CmFinoFIX.message.JSGroup.Entries.GroupName._name,
        	valueField : CmFinoFIX.message.JSGroup.Entries.ID._name,
        	name: CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupID._name,
        	hiddenName:CmFinoFIX.message.JSPocketTemplateConfig.Entries.GroupID._name,
        	listeners   : {
        		specialkey: this.enterKeyHandler.createDelegate(this)
        	}
        	
        }
        ]
    });

    mFino.widget.PocketTemplateConfigSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.PocketTemplateConfigSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        mFino.widget.PocketTemplateConfigSearchForm.superclass.initComponent.call(this);
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
			if(!values.BusinessPartnerType){
				values.BusinessPartnerType="";
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
