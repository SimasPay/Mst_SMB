/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.distributionHierarchy = function(config){

	var dhSearchBox;
	var dhTreeBox;
	
	var dhDctDetails;
	var dhPartnerDetails;

	var dhDctTabPanel;
	var dhPartnerTabPanel;
	
	var dhDctPanelCenter;
	var dhPartnerPanelCenter;
	var panelCenter;
	
	var nodeRecord;
	var parentTree;
	
	var dctFixStore = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSDistributionChainTemplate);
	var partnerFixStore = new FIX.FIXStore(config.dataUrl, CmFinoFIX.message.JSPartner);
	
	dhDctDetails = new mFino.widget.DHDCTDetails(Ext.applyIf({
        height : 200
    }, config));
	
	dhPartnerDetails = new mFino.widget.DHPartnerDetails(Ext.applyIf({
        height : 200,
		width:665
    }, config));

    dhSearchBox = new mFino.widget.DHSearchForm(Ext.apply({
        height : 170,
        region: 'north',
		dataUrl: "distributionTree.htm"
    }, config));
	
    dhTreeBox = new mFino.widget.DHTree({
    	text: "root",
		objectId: -10,
		dctNodeType: CmFinoFIX.NodeType.root,
		dctId: -10,
		partnerId: -10,
		PermissionType: CmFinoFIX.PermissionType.Read_Write,
		serviceId: -1,
		mdn: -1,
		subscriberId: -1,
		businessPartnerType: -1,
		srchDctName: '',
		srchServiceId: -1,
		levels: 0,
        height: 672,
        region: 'center',
        isPopUpWindow: false,
        tbar : [
                '<b class= x-form-tbar>' +_('Distribution Hierarchy') + '</b>',
                '->',
                {
                    iconCls: 'mfino-button-pocket-add',
                    tooltip : _('Distribute Amount'),
                    text : _('Distribute Amount'),
                    itemId: 'dct.distribute.amount',
                    handler: function(){ 
                    	if(nodeRecord){
                    		if(nodeRecord.partnerId != mFino.auth.getPartnerId()){
                    			Ext.ux.Toast.msg(_("Message"),_("Can distribute only to partners under "+mFino.auth.getUsername()));
                    			return;
                    		}
                    		var popup = new mFino.widget.DistributionChargesWindow({nodeRecord: nodeRecord, parentTree: parentTree});
                       	 	popup.show();
                    	} else {
                    		Ext.ux.Toast.msg(_("Message"),_("Select partner to distribute amount"));
                    	}                   	 
                    }
                }
                ]
    });
	
	dhSearchBox.on("DHSearchEvent", function(values){
		dhTreeBox.loadTree(values);
	});

    dhTreeBox.on("nodeClickEvent", function(id, text, objectId, nodeType, PermissionType, serviceId, dctId, mdn, subscriberId, businessPartnerType, levels, record) {

		if(CmFinoFIX.NodeType.dct == nodeType){
			nodeRecord = null;
			dhDctDetails.getForm().reset();
			dhDctDetails.setStore(dctFixStore);
			dctFixStore.lastOptions = {
				params : {
					IDSearch: objectId
				}
			};
			dctFixStore.load(dctFixStore.lastOptions);
			
			var values = {TemplateID : objectId, ServiceID : serviceId, Levels : levels, PermissionType: PermissionType};

			dhDctTabPanel.setValues(values);
			

			dhPartnerDetails.hide();
			dhPartnerTabPanel.hide();
			dhDctDetails.show();
			dhDctTabPanel.show();
		}
		else if(CmFinoFIX.NodeType.partner == nodeType){
			dhPartnerDetails.getForm().reset();
			dhPartnerDetails.setStore(partnerFixStore);
			partnerFixStore.lastOptions = {
				params : {
					PartnerIDSearch: objectId,
					IsHierarchyTabPartnerIDSearch: true
				}
			};
			partnerFixStore.load(partnerFixStore.lastOptions);
			
			var values = {PartnerID: objectId,PermissionType: PermissionType, TemplateID : dctId, ServiceID : serviceId, MDN : mdn, SubscriberID: subscriberId, BusinessPartnerType: businessPartnerType, Levels : levels};
			dhPartnerTabPanel.setValues(values);

			dhDctDetails.hide();
			dhDctTabPanel.hide();
			dhPartnerDetails.show();
			dhPartnerTabPanel.show();
			nodeRecord = record;
		}
    });
    
    parentTree = dhTreeBox;
	
	dhDctTabPanel = new mFino.widget.DHDCTTabPanel(config);
	dhPartnerTabPanel = new mFino.widget.DHPartnerTabPanel(config);

    panelCenter = new Ext.Panel({
        layout: 'anchor',
        items : [
        {
            anchor : "100%",
            tbar : [
          '<b class= x-form-tbar>' + _('Details ') + '</b>',
            '->'

            ],
            items: [  dhPartnerDetails, dhDctDetails ]
        },
		{
            anchor : "100%, -200",
            layout: "fit",
            items: [dhDctTabPanel, dhPartnerTabPanel ]				
		}
        ]
    });
	
	dhPartnerDetails.hide();
	dhPartnerTabPanel.hide();

    var panel = new Ext.Panel({
        layout: "border",
		id: 'distributionHierarchyPanel',
        broder: false,
        width : 1020,
        items: [
        {
            region: 'west',
            width : 266,
            layout : "border",
            items:[ dhSearchBox , dhTreeBox ]
        },
        {
            region: 'center',
			width:700,
			height: 300,
            itemId: 'distributionHierarhy.itemId.panelCenter',
            id: 'distributionHierarhy.id.panelCenter',
            layout : "fit",
            items: [panelCenter]
        }
        ]
    });
    
    return panel;
};

//------------------------------------------------------------------------------------------------------------------------------------

/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DistributionChargesWindow = function (config) {
	var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
    	id: 'distributionChargesWindow',
        modal:true,
        title : _("Distribute Amount"),
        layout:'fit',
        floating: true,
        width:300,
        height:400,
        plain:true
    });
    mFino.widget.DistributionChargesWindow.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.DistributionChargesWindow, Ext.Window, {
    initComponent : function () {
    	var noOfPartnersSelected = 0;
    	this.dhTreeBox = new mFino.widget.DHTree(Ext.apply({
    		text: "root",
			objectId: this.nodeRecord.objectId,
			dctNodeType: this.nodeRecord.dctNodeType,
			dctId: this.nodeRecord.dctId,
			partnerId: this.nodeRecord.partnerId,
			PermissionType: CmFinoFIX.PermissionType.Read_Write,
			serviceId: this.nodeRecord.serviceId,
			mdn: this.nodeRecord.mdn,
			subscriberId: this.nodeRecord.subscriberId,
			businessPartnerType: this.nodeRecord.businessPartnerType,
			srchDctName: this.nodeRecord.srchDctName,
			srchServiceId: this.nodeRecord.srchServiceId,
			levels: 0,
            isPopUpWindow: true,
            listeners: {
            	checkchange: function(node, checked) {
            		if(checked) {
            			noOfPartnersSelected++; 
            		} else {
            			noOfPartnersSelected--;            			
            		}            		
            		Ext.get('totalSelectedPartners').dom.value = noOfPartnersSelected;
            		calculateTotalAmount();
            	}
            },
            tbar : [
                    '<b class= x-form-tbar>' +_('Distribution Hierarchy') + '</b>',
                    '->'
                    ]
        },{}));  
    	
    	var calculateTotalAmount = function(){
    		var amt = Ext.get('amountPerPartner').dom.value;
    		if(amt){    			
    			Ext.get('totalAmountToDistribute').dom.value = noOfPartnersSelected * amt;
    		}
    	};
    	
    	this.balance = this.nodeRecord.balance;
    	
    	this.detailsForm = new Ext.form.FormPanel({
    		bodyStyle:'padding:5px 5px 0',
    		id: 'dthDetailsForm',
    		items : [
	                  {
	                      xtype : "numberfield",
	                      id: 'amountPerPartner',
	                      name : CmFinoFIX.message.JSDistributeChargesForm.AmountPerPartner._name,
	                      anchor : '90%',
	                      fieldLabel : _("Amount Per Each Partner"),
	                      allowBlank : false,
	                      blankText : _('Field is required'),
	                      listeners: {
	                    	  change: function(){
	                    		  calculateTotalAmount();
	                    	  }
	                      }	                      
	                  },
	                  {
	                      xtype : "numberfield",
	                      anchor : '90%',
	                      fieldLabel : _("No. of Selected Partners"),
	                      disabled: true,
	                      id: 'totalSelectedPartners',
	                      name : CmFinoFIX.message.JSDistributeChargesForm.TotalSelectedPartners._name,
	                      value: 0
	                  },
	                  {
	                      xtype : "numberfield",
	                      anchor : '90%',
	                      disabled: true, 			                      
	                      fieldLabel : _("Available Balance"),
	                      id: 'availableBalance',
	                      name : CmFinoFIX.message.JSDistributeChargesForm.AvailableBalance._name,
	                      value : this.balance
	                  },
	                  {
	                      xtype : "numberfield",
	                      anchor : '90%',
	                      disabled: true,  			                      
	                      fieldLabel : _("Total Amount"),
	                      id: 'totalAmountToDistribute',
	                      name : CmFinoFIX.message.JSDistributeChargesForm.TotalAmountToDistribute._name
	                  },
	                  {
	                      xtype : "numberfield",
	                      anchor : '90%',
	                      disabled: false,  			                      
	                      fieldLabel : _("Pin"),
	                      id: 'pin',
	                      inputType   : 'password',
	                      allowBlank : false,
	                      width:140,
	                      blankText : _('Field is required'),
	                      name : CmFinoFIX.message.JSDistributeChargesForm.Pin._name
	                  },
	                  this.dhTreeBox
	                  ]    	
    	});
		this.buttons = [
              {
               text: _('OK'),
               handler: this.ok.createDelegate(this)
              },
              {
               text: _('Cancel'),
               handler: this.cancel.createDelegate(this)
              }
            ]; 
    	this.items = [this.detailsForm];
        mFino.widget.DistributionChargesWindow.superclass.initComponent.call(this);
    }, 
    
    closeAction: 'destroy',
        
    ok : function(){
    	if(!this.detailsForm.form.isValid()){
    		return;
    	}
    	var formValues = this.detailsForm.form.getFieldValues();
    	if(formValues.TotalSelectedPartners == 0){
    		Ext.ux.Toast.msg(_("Message"),_("No partners selected"));
    		return;
    	}
    	if(formValues.AvailableBalance < formValues.TotalAmountToDistribute) {
    		Ext.ux.Toast.msg(_("Message"),_("Insufficient balance to distribute"));
    		return;
    	}
    	var selectedNodes = this.dhTreeBox.getChecked();
    	var listOfSelectedPartners = "";
        Ext.each(selectedNodes, function(node) {
        	listOfSelectedPartners += node.attributes.subscriberId+","; 
                });
        
        var msg= new CmFinoFIX.message.JSDistributeChargesForm();
        msg.m_pAmountPerPartner = Ext.get('amountPerPartner').dom.value;
        msg.m_pListOfSelectedPartners = listOfSelectedPartners;
        msg.m_pSourceSubscriberID = this.dhTreeBox.root.attributes.subscriberId;
        msg.m_pDCTID = this.dhTreeBox.root.attributes.dctId;
        msg.m_pPin = Ext.get('pin').dom.value;
        Ext.each(this.buttons, function(button) {
        	button.disable();
        });
        var params = mFino.util.showResponse.getDisplayParam();
        mFino.util.fix.send(msg, params);
        Ext.apply(params, {        	
            success :  function(response){
            	if(response.m_pErrorCode === CmFinoFIX.ErrorCode.NoError){
                    Ext.ux.Toast.msg(_('Success'),_("Amount distributed successfully"));                    
                    this.scope.close();
                    this.scope.parentTree.loadTree({});                   
                } else{
                	Ext.ux.Toast.msg(_('Failure'),_(response.m_pErrorDescription));
                	Ext.each(this.scope.buttons, function(button) {
                    	button.enable();
                    });
                }
            },
            scope:this
        });   
         	   	
    },

    cancel : function(){
    	this.close();
    }
});

Ext.reg("distributionChargesWindow", mFino.widget.DistributionChargesWindow);

