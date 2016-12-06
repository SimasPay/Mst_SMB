/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SubscriberSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 80,
        frame:true,
        title: _('Subscriber Search'),
        bodyStyle:'padding:5px 5px 0',
        items: [
	        {
	            xtype : 'textfield',
	            fieldLabel: _('First Name'),
	            labelSeparator : '',
	            anchor :'98%',
	            maxLength:255,
	            name: CmFinoFIX.message.JSSubscriberMDN.FirstNameSearch._name,
	            listeners   : {
	                specialkey: this.enterKeyHandler.createDelegate(this)
	            }
	        },
	        {
	            xtype : 'textfield',
	            fieldLabel: _('Last Name'),
	            labelSeparator : '',
	            anchor :'98%',
	            maxLength:255,
	            name: CmFinoFIX.message.JSSubscriberMDN.LastNameSearch._name,
	            listeners   : {
	                specialkey: this.enterKeyHandler.createDelegate(this)
	            }
	        },
	        {
	            xtype : 'textfield',
	            vtype: 'smarttelcophoneAdd',
	            fieldLabel: _('MDN'),
	            labelSeparator : '',
	            anchor :'98%',
	            maxLength : 16,
	            name: CmFinoFIX.message.JSSubscriberMDN.MDNSearch._name,
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
	            name: CmFinoFIX.message.JSSubscriberMDN.CardPAN._name,
	            listeners   : {
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
	        	fieldLabel: _('State'),
	        	labelSeparator : '',
	        	itemId: "sub.state",
	        	anchor : '98%',
	        	emptyText : _('<select one..>'),
	        	addEmpty : false,
	        	enumId : CmFinoFIX.TagID.UpgradeStateSearch,
	        	name: CmFinoFIX.message.JSSubscriberMDN.UpgradeStateSearch._name,
	        	value: CmFinoFIX.TagID.UpgradeStateSearch.All,
	        	listeners   : {
	        		specialkey: this.enterKeyHandler.createDelegate(this)
	        	}
	        },
	        {
	            xtype : "combo",
	            anchor : '98%',
	            labelSeparator : '',
	            fieldLabel :_('KYC'),
	            emptyText : _('<select one..>'),
	            addEmpty : false,
	            store : new FIX.FIXStore("fix.htm",CmFinoFIX.message.JSKYCCheck),
	            displayField: CmFinoFIX.message.JSKYCCheck.Entries.KYCLevelName._name,
	            valueField : CmFinoFIX.message.JSKYCCheck.Entries.KYCLevel._name,
	            name: CmFinoFIX.message.JSKYCCheck.Entries.KYCLevelName._name,
	            triggerAction: 'all',
	            listeners: {
	                select: function(field,record) {
	                	var KYCLevel = record.get(CmFinoFIX.message.JSKYCCheck.Entries.KYCLevel._name);
	                	/*var kyc= field.getValue();
	                	if(kyc === 0) {
	                		Ext.MessageBox.alert(_("Alert"), _("Subscriber with NoKyc is not allowed to create"));
	                		field.clearValue();
	                		return;
	                	}*/
	                	
	                	var kf_combo = Ext.getCmp("sub.form.kycfield.search");
	                	kf_combo.setValue(KYCLevel)
	                }
	            }
	        },
	        {
	            xtype : "enumdropdown",
	            fieldLabel: _('Status'),
	            labelSeparator : '',
	            itemId: "sub.status",
	            anchor : '98%',
	            emptyText : _('<select one..>'),            
	            enumId : CmFinoFIX.TagID.MDNStatus,
	            name: CmFinoFIX.message.JSSubscriberMDN.MDNStatus._name,
	            listeners   : {
	                specialkey: this.enterKeyHandler.createDelegate(this)
	            }
	        },
	        {
	            xtype : "enumdropdown",
	            fieldLabel: _('Upgrade Status'),
	            labelSeparator : '',
	            itemId: "sub.kyc.upgrade.status",
	            anchor : '98%',
	            emptyText : _('<select one..>'),            
	            enumId : CmFinoFIX.TagID.UpgradeKycStatusSearch,
	            name: CmFinoFIX.message.JSSubscriberMDN.UpgradeKycStatusSearch._name,
	            listeners   : {
	                specialkey: this.enterKeyHandler.createDelegate(this)
	            }
	        },
	        {
	            	xtype : "hidden",
	                itemId : 'sub.form.kycfield.search',
	                id : 'sub.form.kycfield.search',
	                name: CmFinoFIX.message.JSKYCCheckFields.Entries.KYCFieldsLevelID._name
	        }
        ]
    });

    mFino.widget.SubscriberSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberSearchForm, Ext.FormPanel, {

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
        mFino.widget.SubscriberSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },

    searchHandler : function(){
        if(this.getForm().isValid()){
        	
            var values = this.getForm().getValues();
            var currdatetime= new Date();
            var edate1=values.endDate;
            
            var d1=currdatetime.format("ymd");
            var d2=Ext.util.Format.substr(edate1, 2, 6);
            if(d2!="")
            	 {
           	 		if(d1 == d2)
            	 	{
           	 			values.endDate = currdatetime;
             	 	}
            	 }
            //alert(values.startDate+"    "+values.endDate);
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
Ext.reg("subscribersearchform", mFino.widget.SubscriberSearchForm);