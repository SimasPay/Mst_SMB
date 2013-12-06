/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ServicePartnerSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 80,
        frame:true,
        //title: _('Partner Search'),
        bodyStyle:'padding:5px 5px 0',
        items: [
        /*{
            xtype : 'numberfield',
            allowDecimals:false,
            fieldLabel: _('ID'),
            labelSeparator : '',
            anchor : '98%',
            maxLength:16,
            minValue:0,
            name: CmFinoFIX.message.JSPartner.PartnerIDSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },*/
        
        {
            xtype : 'textfield',
            allowDecimals:false,
            fieldLabel: _('Code'),
            labelSeparator : '',
            anchor :'98%',
            maxLength:255,
            name: CmFinoFIX.message.JSPartner.PartnerCodeSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype : 'textfield',
            fieldLabel: _('Trade Name'),
            labelSeparator : '',
            anchor :'98%',
            maxLength : 255,
            name: CmFinoFIX.message.JSPartner.TradeNameSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype : 'textfield',
            fieldLabel: _('Authorized Email'),
            vtype: 'email',            
            labelSeparator : '',
            anchor :'98%',
            maxLength : 255,
            name: CmFinoFIX.message.JSPartner.AuthorizedEmailSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype : 'textfield',
            fieldLabel: _("Account No."),
            vtype:'tendigitnumber',
            labelSeparator : '',
            anchor : '98%',
            name: CmFinoFIX.message.JSPartner.CardPAN._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
	        xtype : "remotedropdown",
	        anchor : '98%',
            fieldLabel :_("Service Type"),            
			emptyText : _('<select one..>'),            
            store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSService),
            displayField: CmFinoFIX.message.JSService.Entries.ServiceName._name,
            valueField : CmFinoFIX.message.JSService.Entries.ID._name,
            hiddenName : CmFinoFIX.message.JSPartner.ServiceIDSearch._name,
            name: CmFinoFIX.message.JSPartner.ServiceIDSearch._name,
            listeners: {
            	 specialkey: this.enterKeyHandler.createDelegate(this)
	          }            
        },
        {
            xtype : 'daterangefield',
            fieldLabel: _('Date range'),
            labelSeparator : '',
            anchor :'98%',
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
        {
            xtype : "enumdropdown",
            fieldLabel: _('Status'),
            labelSeparator : '',
           // itemId: "sub.state",
            anchor : '98%',            
			emptyText : _('<select one..>'),
            enumId : CmFinoFIX.TagID.UpgradeStateSearch,
            name: CmFinoFIX.message.JSPartner.UpgradeStateSearch._name,
            value: CmFinoFIX.TagID.UpgradeStateSearch.All,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
            }
        ]
    });

    mFino.widget.ServicePartnerSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ServicePartnerSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [
        {
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }
        ];
        mFino.widget.ServicePartnerSearchForm.superclass.initComponent.call(this);
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
            this.fireEvent("search", values);
        }
        else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    }
});
