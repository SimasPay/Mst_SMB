/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ActorChannelMappingSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Actor Channel Mapping Search'),
        bodyStyle:'padding:5px 5px 0',
        items : [
              {
                xtype : "enumdropdown",
                fieldLabel: _('Actor'),
                labelSeparator : '',
                itemId: "actor",
                anchor : '98%',
                editable: true,
                emptyText : _('<select one..>'),
                enumId : CmFinoFIX.TagID.SubscriberType,
                name: CmFinoFIX.message.JSActorChannelMapping.SubscriberType._name,
                hiddenName: CmFinoFIX.message.JSActorChannelMapping.SubscriberType._name,
                listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this),
                    select : function(field) {
			        	var partnerTypeField = this.findParentByType('actorChannelMappingSearchForm').find('itemId','actorchannel.search.partnerType')[0];
			        	var kycField = this.findParentByType('actorChannelMappingSearchForm').find('itemId','actorchannel.search.kyclevel')[0];
			        	if(field.value == CmFinoFIX.SubscriberType.Subscriber){
			        		partnerTypeField.reset();
			        		partnerTypeField.disable();
			        		kycField.enable();
			        	} else {
			        		partnerTypeField.enable();
			        		kycField.reset();
			        		kycField.disable();
			        	}
			        }
                }
              },
              {
			    xtype : "enumdropdown",
			    fieldLabel: _('Partner Type'),				    
			    labelSeparator : '',
			    itemId: "actorchannel.search.partnerType",
			    anchor : '98%',
			    editable: true,
			    emptyText : _('<select one..>'),
			    enumId : CmFinoFIX.TagID.BusinessPartnerType,
			    name: CmFinoFIX.message.JSActorChannelMapping.BusinessPartnerType._name,
			    hiddenName: CmFinoFIX.message.JSActorChannelMapping.BusinessPartnerType._name,
			    listeners   : {
                    specialkey: this.enterKeyHandler.createDelegate(this)
                }
			  }, 
              {
                  xtype: "remotedropdown",
                  fieldLabel: _('Service Type'),
				  emptyText : _('<select one..>'),
                  labelSeparator : '',
                  anchor:'98%',
                  editable: true,
                  RPCObject : CmFinoFIX.message.JSService,
                  displayField: CmFinoFIX.message.JSService.Entries.ServiceName._name,
                  valueField : CmFinoFIX.message.JSService.Entries.ID._name,                  
                  name: CmFinoFIX.message.JSActorChannelMapping.ServiceID._name,
                  hiddenName: CmFinoFIX.message.JSActorChannelMapping.ServiceID._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this),                      
                      select: function(field) {
              			this.findParentByType('actorChannelMappingSearchForm').getTransactions(field.getValue());
                      }
                  }
              },
              {
                  xtype: "remotedropdown",
                  labelWidth : 100,
                  fieldLabel: _('Transaction Type'),
                  labelSeparator : '',
				  emptyText : _('<select one..>'),
                  anchor:'98%',
                  editable: true,
                  itemId : 'actorchannel.search.transactiontype',                  
                  lastQuery: '',
                  store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSTransactionsForService),
                  displayField: CmFinoFIX.message.JSTransactionsForService.Entries.TransactionName._name,
                  valueField : CmFinoFIX.message.JSTransactionsForService.Entries.TransactionTypeID._name,
                  name: CmFinoFIX.message.JSActorChannelMapping.TransactionTypeID._name,
                  hiddenName: CmFinoFIX.message.JSActorChannelMapping.TransactionTypeID._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },
              {
                  xtype: 'remotedropdown',
                  labelWidth : 100,
                  fieldLabel: _('Channel'),
				  emptyText : _('<select one..>'),
                  labelSeparator : '',
                  anchor:'98%',
                  editable: true,
                  RPCObject : CmFinoFIX.message.JSChannelCode,
                  displayField: CmFinoFIX.message.JSChannelCode.Entries.ChannelName._name,
                  valueField : CmFinoFIX.message.JSChannelCode.Entries.ID._name,
                  name: CmFinoFIX.message.JSActorChannelMapping.ChannelCodeID._name,
                  hiddenName: CmFinoFIX.message.JSActorChannelMapping.ChannelCodeID._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },    
              {
                  xtype : "remotedropdown",
                  anchor : '98%',                  
                  itemId : 'actorchannel.search.kyclevel',
                  fieldLabel : kyc,
                  editable: true,
                  emptyText : _('<select one..>'),
                  RPCObject : CmFinoFIX.message.JSKYCCheck,
                  displayField: CmFinoFIX.message.JSKYCCheck.Entries.KYCLevelName._name,
                  valueField : CmFinoFIX.message.JSKYCCheck.Entries.KYCLevel._name,
                  name: CmFinoFIX.message.JSActorChannelMapping.KYCLevel._name,
                  hiddenName: CmFinoFIX.message.JSActorChannelMapping.KYCLevel._name,
                  listeners   : {
                      specialkey: this.enterKeyHandler.createDelegate(this)
                  }
              },
              {
					xtype : "remotedropdown",
					anchor : '98%',
					emptyText : _('<select one..>'),
					itemId : 'actorchannel.search.group',
					fieldLabel : "Group",
					editable: true,
					emptyText: _('<select one..>'),
					RPCObject : CmFinoFIX.message.JSGroup,
					displayField: CmFinoFIX.message.JSGroup.Entries.GroupName._name,
					valueField : CmFinoFIX.message.JSGroup.Entries.ID._name,
					name: CmFinoFIX.message.JSActorChannelMapping.GroupID._name,
					hiddenName: CmFinoFIX.message.JSActorChannelMapping.GroupID._name,
					pageSize : 50,
					params: {start: 0, limit: 50},
					listeners   : {
	                      specialkey: this.enterKeyHandler.createDelegate(this)
	                }
              }
         ]
        
    });

    mFino.widget.ActorChannelMappingSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ActorChannelMappingSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];
        
        mFino.widget.ActorChannelMappingSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
        
        this.on("render", function(){
            this.reloadRemoteDropDown();
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
            if(values.SubscriberType === ""){
                values.SubscriberType = null;
            }
            if(values.BusinessPartnerType === ""){
                values.BusinessPartnerType = null;
            } 
            if(values.ServiceID === ""){
                values.ServiceID = null;
            }
            if(values.TransactionTypeID === ""){
                values.TransactionTypeID = null;
            }
            if(values.KYCLevel === ""){
                values.KYCLevel = null;
            }
            if(values.ChannelCodeID === ""){
                values.ChannelCodeID = null;
            }
            if(values.GroupID === ""){
                values.GroupID = null;
            }
            this.fireEvent("search", values);
        } else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
        this.getForm().items.each(function(item) {
        	item.enable();
        });
    },
    
    reloadRemoteDropDown : function(){
    	this.getForm().items.each(function(item) {
            if(item.getXType() == 'remotedropdown') {
                item.reload();
            }
        });
    },
    
    getTransactions : function(field) {
    	var tr_combo = this.find('itemId','actorchannel.search.transactiontype')[0];
    	tr_combo.clearValue();
    	tr_combo.store.reload({
    		params: {ServiceID : field}
    	});
    }    
});

Ext.reg("actorChannelMappingSearchForm", mFino.widget.ActorChannelMappingSearchForm);