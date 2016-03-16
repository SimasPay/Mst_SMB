/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SubscriberDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        // id : "subscriberdetails",
        layout:'column',
        frame : true,
        width:643,
        items: [        {
            columnWidth: 0.55,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype : 'displayfield',
                fieldLabel:kyc,
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.KYCLevelText._name,
                anchor : '75%'
            },{
                xtype : 'displayfield',
                fieldLabel: _("MDN"),
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name,
                anchor : '75%'
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('First Name'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.FirstName._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Nick Name'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.Nickname._name
            },
//            {
//                xtype : 'displayfield',
//                fieldLabel: _("Dompet Merchant"),
//                anchor : '75%',
//                name: CmFinoFIX.message.JSSubscriberMDN.Entries.DompetMerchant._name,
//                renderer : function(value){
//                    if(value){
//                        return "True";
//                    }else{
//                        return "False";
//                    }
//                }
//            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Email'),
                anchor : '100%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.Email._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Status'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberStatusText._name
            },
            {
                xtype : "displayfield",
                anchor : '100%',
                fieldLabel : _('Restrictions'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.MDNRestrictionsText._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Language'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.LanguageText._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Time zone'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.Timezone._name
            },
            {
                xtype : "displayfield",
                anchor : '60%',
                fieldLabel :_('Currency'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.Currency._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer: "date",
                fieldLabel : _('Activation Time'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.ActivationTime._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('Status Time'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.StatusTime._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                renderer: "date",
                fieldLabel: _('Creation Time'),
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.CreateTime._name
            },
            {
                xtype: "displayfield",
                renderer : "date",
                fieldLabel: _('Last Update Time'),
                anchor : '100%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.LastUpdateTime._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Approval ID Number'),
                anchor : '100%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.ApprovalIdNumber._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('KTPDocument'),
                anchor : '100%',
				style: {
					color: '#0000ff' ,
					//text-decoration:'underline',
					//text-decoration: 'underline',
					cursor:'pointer'
				},
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name,
                listeners:{
                	 afterrender: function(component) {
						 
                	      component.getEl().on('click', function() { 
						  
							
						    mFino.widget.SubscriberDetails.prototype.showImage(component.getValue())
                	        
                	      });  
                	      
                	    }
                             	
                }
            },
			{
                xtype: "displayfield",
                fieldLabel: _('SubscriberForm'),
                anchor : '100%',
				style: {
					color: '#0000ff' ,
					//text-decoration:'underline',
					//text-decoration: 'underline',
					cursor:'pointer'
				},
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberFormPath._name,
                listeners:{
                	 afterrender: function(component) {
						 
                	      component.getEl().on('click', function() { 
						  
							
						    mFino.widget.SubscriberDetails.prototype.showImage(component.getValue())
                	        
                	      });  
                	      
                	    }
                             	
                }
            }  
            ]
        },
        {
            columnWidth: 0.45,
            layout: 'form',
            labelWidth : 130,
            items : [
            {
                xtype: "displayfield",
                fieldLabel: _('Subscriber ID'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberID._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Group'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.GroupName._name
            },
            {
                xtype : 'displayfield',
                fieldLabel: _('Last Name'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.LastName._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Applied KYC'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradableKYCLevelText._name
            },
           /* {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Authentication MDN'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.AuthenticationPhoneNumber._name
            },    */        
            {
                xtype: "displayfield",
                fieldLabel: _('Approval Status'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeStateText._name
            },
//            {
//                xtype : 'displayfield',
//                anchor : '100%',
//                fieldLabel : _('Partner Type'),
//                name : CmFinoFIX.message.JSSubscriberMDN.Entries.PartnerTypeText._name
//            },
           /* {
                xtype : 'displayfield',
                anchor : '100%',
                fieldLabel : _('Secret Question'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.SecurityQuestion._name
            },
            {
                xtype : 'displayfield',
                anchor : '90%',
                fieldLabel : _('Secret Answer'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.AuthenticationPhrase._name
            },*/
            
            {
                xtype : 'displayfield',
                anchor : '90%',
                fieldLabel : _('Other MDN'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.OtherMDN._name
            },
            /*{
                xtype : 'displayfield',
                anchor : '90%',
                fieldLabel : _('Last Transaction ID'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.LastTransactionID._name
            },
            {
                xtype : 'displayfield',
                anchor : '100%',
                renderer : "date",
                fieldLabel : _('Last Transaction Time'),
                name : CmFinoFIX.message.JSSubscriberMDN.Entries.LastTransactionTime._name
            },*/
            {
                xtype: "displayfield",
                fieldLabel: _('Created By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.CreatedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Updated By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.UpdatedBy._name
            },
            
            {
                xtype: "displayfield",
                fieldLabel: _('Applied By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.AppliedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Approved/Rejected By'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.ApprovedOrRejectedBy._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('Approve/Reject Comment'),
                anchor : '75%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.ApproveOrRejectComment._name
            },
            {
                xtype: "displayfield",
                renderer : "date",
                fieldLabel: _('Applied Time'),
                anchor : '100%',
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.AppliedTime._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                renderer: "date",
                fieldLabel: _('Approve/Reject Time'),
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.ApproveOrRejectTime._name
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Upgrade Account Status'),
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeAcctStatusText._name
                
            },
            {
                xtype: "displayfield",
                anchor : '100%',
                fieldLabel: _('Upgrade Account Comments'),
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.UpgradeAcctComments._name
            },
            {
                xtype: "displayfield",
                fieldLabel: _('SupportingDocument'),
                anchor : '100%',
				style: {
					color: '#0000ff' ,
					//text-decoration:'underline',
					//text-decoration: 'underline',
					cursor:'pointer'
				},
                name: CmFinoFIX.message.JSSubscriberMDN.Entries.SupportingDocumentPath._name,
                listeners:{
                	 afterrender: function(component) {
						 
                	      component.getEl().on('click', function() { 
						  
							
						    mFino.widget.SubscriberDetails.prototype.showImage(component.getValue())
                	        
                	      });  
                	      
                	    }
                             	
                }
            }
            
            ]
        }]
    });

    mFino.widget.SubscriberDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SubscriberDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.SubscriberDetails.superclass.initComponent.call(this);
    },
    showImage:function(imageName){
    	var imagePath=mFino.widget.SubscriberDetails.path+imageName
		if(imagePath.indexOf('.')!=-1){
			
		
			var window=new Ext.Window({
				layout:'anchor',
				width:500,
				height:500,
				autoScroll:true,
				bodyStyle:'backgroundColor:white',
				title:imageName,
				items:[{
					anchor : '100%',
					html: "<div display=\"block\" style=''>" + 
						
					"<div style=\"text-align:left;line-height:3px;padding:5px 3px 4px;\">" +
						"<span>" + 
								"<img height=200 width=200 alt=\"image\" src=\""+imagePath+"\" />" +
						"</span>" + 
					"</div>" +
				   "</div>"
				}]
			});
			window.show();
		}else{
			
			Ext.Msg.alert('Info', 'Document Not Available!');
		}
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;

		if(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name)!=null){

			var docFullPath=record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name)
			docFullPath=docFullPath.replace("\\","/");
			docFullPath=docFullPath.replace("\\","/");
			if(mFino.widget.SubscriberDetails.path == null || mFino.widget.SubscriberDetails.path == '' || mFino.widget.SubscriberDetails.path == undefined){
					mFino.widget.SubscriberDetails.path=docFullPath.substring(0,docFullPath.lastIndexOf('/')+1);
			}
			
			var docName=docFullPath.substring(docFullPath.lastIndexOf('/')+1,docFullPath.length);
			
			record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.KTPDocumentPath._name,docName);
		}
		
		
			if(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberFormPath._name)!=null){

			var docFullPath=record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberFormPath._name)
			docFullPath=docFullPath.replace("\\","/");
			docFullPath=docFullPath.replace("\\","/");
			if(mFino.widget.SubscriberDetails.path == null || mFino.widget.SubscriberDetails.path == '' || mFino.widget.SubscriberDetails.path == undefined){
					mFino.widget.SubscriberDetails.path=docFullPath.substring(0,docFullPath.lastIndexOf('/')+1);
			}
			
			var docName=docFullPath.substring(docFullPath.lastIndexOf('/')+1,docFullPath.length);
			
			record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.SubscriberFormPath._name,docName);
		}
		

		
			if(record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SupportingDocumentPath._name)!=null){
				
		
			var docFullPath=record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.SupportingDocumentPath._name)
			
					docFullPath=docFullPath.replace("\\","/");
			docFullPath=docFullPath.replace("\\","/");
			if(mFino.widget.SubscriberDetails.path == null || mFino.widget.SubscriberDetails.path == '' || mFino.widget.SubscriberDetails.path == undefined){
					mFino.widget.SubscriberDetails.path=docFullPath.substring(0,docFullPath.lastIndexOf('/')+1);
			}
			
			var docName=docFullPath.substring(docFullPath.lastIndexOf('/')+1,docFullPath.length);
			
			record.set(CmFinoFIX.message.JSSubscriberMDN.Entries.SupportingDocumentPath._name,docName);
		}

        this.getForm().loadRecord(record);
        this.getForm().clearInvalid();
    },

    setStore : function(store){
        if(this.store){
            this.store.un("update", this.onStoreUpdate, this);
        }
        this.store = store;
        this.store.on("update", this.onStoreUpdate, this);
    },

    onStoreUpdate: function(){
        this.setRecord(this.record);
    }
});

Ext.reg("subscriberdetails", mFino.widget.SubscriberDetails);

