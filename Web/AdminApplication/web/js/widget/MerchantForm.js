/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.MerchantForm = function (config) {
    this.LOPPermissionEnabled = false;

    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side'
    });

    mFino.widget.MerchantForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantForm, Ext.form.FormPanel, {
    initComponent : function ()
    {
        this.rangecheck = false;
        this.mdnRangeGrid = new mFino.widget.MDNRange();
        this.labelWidth = 120;
        this.labelPad = 20;
        this.autoScroll = true;
        this.frame = true;
        this.items = [
        {
            xtype: 'fieldset',
            title : _('Merchant Details'),
            layout : 'column',
            autoHeight: true,
            width:860,
            items : [
            {
                columnWidth: 0.5,
                xtype: 'panel',
                layout: 'form',
                items:[
                {
                    xtype:'textfield',
                    fieldLabel: _('MDN'),
                    allowBlank: false,
                    blankText : _('MDN is required'),
                    itemId : 'mer.form.mdn',
                    maxLength : 16,
                    anchor : '90%',
                    vtype: 'smarttelcophoneAddMore',
                    name: CmFinoFIX.message.JSMerchant.Entries.MDN._name,
                    listeners: {
                        change: function(field) {/*
                        	field.isValid(true);
                        	var valmdn=/^[2]{1}[3]{1}[4]{1}[0-9]{10}$/;
                        	var valmdn1=/^[2]{1}[3]{1}[4]{1}[0-9]{7}$/;
                        	var mdn = field.getValue();
                        	if(mdn.length==13)
                    		{
                        		if(!valmdn.test(mdn))
                        		{
                        			field.markInvalid("MDN start with 234");
                        		}
                    		}else if(mdn.length>10){
                    			field.markInvalid("MDN starting with 234 should be 13 digits or 10 digits");
                    		}else if(valmdn1.test(mdn)){
                        		
                    			field.markInvalid("MDN should be 13 digits");
                    		
                 		}
                    */}
                    }
                },
                {
                    xtype : "textfield",
                    anchor : '90%',
                    fieldLabel :_("User Name"),
                    itemId  : 'mer.form.username',
                    maxLength : 255,
                    async   : false,
                    name    : CmFinoFIX.message.JSMerchant.Entries.Username._name,
                    allowBlank: false,
                    vtype:'usernamechk',
                    listeners: {
                        blur: function(field) {
                            var msg = new CmFinoFIX.message.JSUsernameCheck();
                            msg.m_pUsername = field.getValue();
                            msg.m_pCheckIfExists = true;
                            var checkForExists=true;
                            mFino.util.fix.checkNameInDB(field,msg, checkForExists);
                        }
                    },
                    emptyText: _('david_andrew'),
                    blankText : _('User name is required')
                },
                {
                    xtype : "enumdropdown",
                    anchor : '90%',
                    fieldLabel :_('Language'),
                    itemId : 'mer.form.lang',
                    allowBlank: false,
                    emptyText : _('<select one..>'),
                    blankText : _('Language is required'),
                    enumId : CmFinoFIX.TagID.Language,
                    name : CmFinoFIX.message.JSMerchant.Entries.Language._name
                },
                {
                    xtype : "enumdropdown",
                    anchor : '90%',
                    fieldLabel :_('Time zone'),
                    allowBlank: false,
                    emptyText : _('<select one..>'),
                    itemId : 'mer.form.time',
                    blankText : _('Time zone is required'),
                    enumId: CmFinoFIX.TagID.Timezone,
                    name : CmFinoFIX.message.JSMerchant.Entries.Timezone._name
                },
                {
                    xtype : "enumdropdown",
                    anchor : '90%',
                    allowBlank: false,
                    blankText : _('Partner Type'),
                    emptyText : _('<select one..>'),
                    itemId : 'mer.form.partnerType',
                    fieldLabel :_('Partner Type'),
                    enumId : CmFinoFIX.TagID.PartnerType,
                    name : CmFinoFIX.message.JSMerchant.Entries.PartnerType._name                    
                }
                ]
            },
            {
                columnWidth: 0.5,
                xtype: 'panel',
                layout: 'form',
                labelWidth:180,
                items:[
                {
                    xtype : "enumdropdown",
                    anchor : '90%',
                    fieldLabel :_('Currency'),
                    allowBlank: false,
                    emptyText : _('<select o>'),
                    itemId : 'mer.form.curr',
                    blankText : _('Currency is required'),
                    enumId : CmFinoFIX.TagID.Currency,
                    name : CmFinoFIX.message.JSMerchant.Entries.Currency._name
                },
                {
                    xtype : "enumdropdown",
                    anchor : '90%',
                    fieldLabel :_('Status'),
                    itemId: 'mer.form.status',
                    allowBlank: false,
                    emptyText : _('<select one..>'),
                    blankText : _('Status is required'),
                    enumId: CmFinoFIX.TagID.SubscriberStatus,
                    name : CmFinoFIX.message.JSMerchant.Entries.SubscriberStatus._name,
                    listeners : {
                        select :  function(status){
                            var s1= status.getValue();
                            this.findParentByType('merchantform').onStatusDropdown(s1);
                        }
                    }
                },
                {
                    xtype : "remotedropdown",
                    anchor : '90%',
                    itemId : 'mer.form.DCT',
                    fieldLabel :_("Distribution Chain Template"),
                    RPCObject : CmFinoFIX.message.JSDistributionChainTemplate,
                    displayField: CmFinoFIX.message.JSDistributionChainTemplate.Entries.DistributionChainName._name,
                    valueField : CmFinoFIX.message.JSDistributionChainTemplate.Entries.ID._name,
                    name: CmFinoFIX.message.JSMerchant.Entries.DistributionChainTemplateID._name,
                    listeners: {
                        select: function(field) {
                            this.findParentByType('merchantform').onDCT(field.getValue());
                        }
                    }
                },                
                {
                    xtype:'combo',
                    fieldLabel: _('Parent ID'),
                    itemId : 'mer.form.parentId',
                    id     : "merchantfromparentid",
                    anchor : '90%',
                    triggerAction: "all",
                    minChars : 2,
                    forceSelection : true,
                    pageSize : 10,
                    store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSMerchant),
                    displayField: CmFinoFIX.message.JSMerchant.Entries.Username._name,
                    valueField : CmFinoFIX.message.JSMerchant.Entries.ID._name,
                    hiddenName : CmFinoFIX.message.JSMerchant.Entries.ParentID._name,
                    name: CmFinoFIX.message.JSMerchant.Entries.ParentID._name,
                    listeners: {
                        select: function(field) {
                            var groupIdField= Ext.getCmp("merchantGroupID");
                            mFino.util.fix.checkGroupIDParent(groupIdField, field.getValue());
                        },
                        blur : function(field){
                            this.findParentByType('merchantform').onParent(field.getValue());
                        }
                    }
                },
                {
                    xtype:'remotedropdown',
                    itemId : 'mer.form.regionname',
                    fieldLabel :_("Region Name"),
                    anchor : '90%',
                    allowBlank : false,
                    pageSize : 10,
                    RPCObject : CmFinoFIX.message.JSRegion,
                    displayField: CmFinoFIX.message.JSRegion.Entries.DisplayText._name,
                    valueField : CmFinoFIX.message.JSRegion.Entries.ID._name,
                    name: CmFinoFIX.message.JSMerchant.Entries.RegionID._name,
                    listeners: {
                        focus: function(){
                            this.reload();
                        }
                    //                        beforerender: function(cmp) {
                    //                            cmp.store.load({
                    //                                callback : function( records, options, success){
                    //                                    if(success){
                    //                                        var emptyData = {};
                    //                                        emptyData[this.displayField] = "";
                    //                                        var r = new this.store.recordType(emptyData, -1); // create new record
                    //                                        this.store.insert(0, r); // insert a new record into the store (also see add)
                    //                                        this.store.commitChanges();
                    //                                        this.setValue(this.value);
                    //                                    }
                    //                                },
                    //                                scope : this,
                    //                                params : {}
                    //                            });
                    //}
                    }
                }
                ]
            },
            {
                columnWidth: 1,
                layout: 'form',
                labelWidth : 120,
                labelPad : 5,
                items : [
                {
                    xtype:'combo',
                    id : "merchantGroupID",
                    itemId: "mer.form.groupID",   //cannot get this to work :(
                    fieldLabel: _('Group ID'),
                    anchor : '70%',
                    triggerAction: "all",
                    minChars : 2,
                    forceSelection : true,
                    pageSize : 10,
                    store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSSAPGroupID),
                    displayField: CmFinoFIX.message.JSSAPGroupID.Entries.DisplayText._name,
                    valueField : CmFinoFIX.message.JSSAPGroupID.Entries.GroupID._name,
                    hiddenName : CmFinoFIX.message.JSSAPGroupID.Entries.GroupID._name,
                    name: CmFinoFIX.message.JSMerchant.Entries.GroupID._name,
                    listeners: {
                        blur: function(field) {
                            var parentIdField= Ext.getCmp("merchantfromparentid");
                            mFino.util.fix.checkGroupIDParent(field, parentIdField.getValue());
                        }
                
                    }
                }
                ]
            }
            ]
        },
        {
            xtype:'tabpanel',
            frame:true,
            activeTab: 0,
            border : false,
            deferredRender:false,
            itemId:'tabpanelmerchant',
            defaults:{
                bodyStyle:'padding:10px'
            },
            items:[
            {
                title: _('Contact Details'),
                layout:'column',
                frame:true,
                autoHeight: true,
                width:840,
                items:[
                {
                    columnWidth: 0.5,
                    xtype: 'panel',
                    layout: 'form',
                    items :[
                    {
                        xtype: 'textfield',
                        fieldLabel: _('First Name'),
                        itemId :'firstName',
                        anchor : '90%',
                        maxLength : 255,
                        name: CmFinoFIX.message.JSMerchant.Entries.FirstName._name
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: _('Last Name'),
                        itemId :'lastName',
                        anchor : '90%',
                        maxLength : 255,
                        name: CmFinoFIX.message.JSMerchant.Entries.LastName._name
                    },
                    {
                        xtype:'textfield',
                        fieldLabel: _('Trade Name'),
                        itemId :'tradeName',
                        anchor : '90%',
                        maxLength : 255,
                        blankText : _('Trade name is required'),
                        name: CmFinoFIX.message.JSMerchant.Entries.TradeName._name
                    },
                    {
                        xtype:'textfield',
                        fieldLabel: _('Email Address'),
                        itemId : 'form.email',
                        anchor : '90%',
                        maxLength : 255,
                        vtype: 'email',
                        name: CmFinoFIX.message.JSMerchant.Entries.Email._name
                    },
                    {
                        xtype:'textfield',
                        fieldLabel: _('Administrator Comment'),
                        itemId :'adminComment',
                        maxLength : 255,
                        anchor : '90%',
                        name: CmFinoFIX.message.JSMerchant.Entries.AdminComment._name
                    },
                    {
                        xtype: 'fieldset',
                        title : _('Notification Method'),
                        layout : 'column',
                        autoHeight: true,
                        anchor : '90%',
                        columns: 2,
                        items: [
                        {
                            columnWidth: 0.5,
                            xtype : 'checkbox',
                            itemId : 'SMS',
                            boxLabel: _(' SMS')
                        },
                        {
                            columnWidth: 0.5,
                            xtype : 'checkbox',
                            itemId : 'Email1',
                            boxLabel: _(' Email')
                        }
                        ]
                    }
                    ]
                },
                {
                    columnWidth:0.5,
                    xtype: 'panel',
                    layout: 'form',
                    items:[
                    {
                        xtype:'fieldset',
                        title: _('Address'),
                        autoHeight:true,
                        defaults: {
                            width: 210
                        },
                        defaultType: 'textfield',
                        columns: 2,
                        items :[
                        {
                            fieldLabel: _('Line1'),
                            anchor : '90%',
                            itemId :'line1',
                            allowBlank : false,
                            maxLength : 255,
                            name: CmFinoFIX.message.JSMerchant.Entries.MerchantAddressLine1._name
                        },
                        {
                            fieldLabel: _('Line2'),
                            anchor : '90%',
                            itemId :'line2',
                            maxLength : 255,
                            name: CmFinoFIX.message.JSMerchant.Entries.MerchantAddressLine2._name
                        },
                        {
                            fieldLabel: _('City'),
                            anchor : '90%',
                            itemId :'city',
                            allowBlank : false,
                            maxLength : 255,
                            name: CmFinoFIX.message.JSMerchant.Entries.MerchantAddressCity._name
                        },
                        {
                            fieldLabel: _('State'),
                            anchor : '90%',
                            itemId :'state',
                            maxLength : 255,
                            name: CmFinoFIX.message.JSMerchant.Entries.MerchantAddressState._name
                        },
                        {
                            fieldLabel: _('Country'),
                            anchor : '90%',
                            itemId :'country',
                            maxLength : 255,
                            name: CmFinoFIX.message.JSMerchant.Entries.MerchantAddressCountry._name
                        },
                        {
                            fieldLabel: _('Postal Code'),
                            allowBlank: false,
                            itemId :'postalCode',
                            blankText : _('Postal Code is required'),
                            anchor : '90%',
                            maxLength : 16,
                            name: CmFinoFIX.message.JSMerchant.Entries.MerchantAddressZipcode._name
                        }]
                    }]
                }]
            },
            {
                title : _('Outlet Details'),
                layout : 'column',
                frame:true,
                autoHeight: true,
                width:840,
                items :
                [
                {
                    columnWidth: 0.5,
                    xtype: 'panel',
                    layout: 'form',
                    items:
                    [
                    {
                        xtype : "enumdropdown",
                        anchor : '90%',
                        fieldLabel :_('Outlet Classification'),
                        itemId : 'mer.form.outlet',
                        enumId : CmFinoFIX.TagID.Classification,
                        name : CmFinoFIX.message.JSMerchant.Entries.Classification._name
                    },
                    {
                        xtype : 'numberfield',
                        allowDecimals:false,
                        fieldLabel : _('Contact Number'),
                        allowBlank: false,
                        itemId : 'mer.form.contactNumber',
                        blankText : _('Contact Number is required'),
                        anchor : '90%',
                        maxLength : 16,
                        name: CmFinoFIX.message.JSMerchant.Entries.FranchisePhoneNumber._name
                    },
                    {
                        xtype : 'numberfield',
                        allowDecimals:false,
                        fieldLabel : _('Fax Number'),
                        anchor : '90%',
                        maxLength : 16,
                        itemId : 'mer.form.faxNumber',
                        name: CmFinoFIX.message.JSMerchant.Entries.FaxNumber._name
                    },
                    {
                        xtype : "enumdropdown",
                        anchor : '90%',
                        fieldLabel :_('Type of Organization'),
                        allowBlank: false,
                        emptyText : _('<select one..>'),
                        itemId : 'mer.form.org',
                        blankText : _('Type Of Organization is required'),
                        maxLength:255,
                        enumId : CmFinoFIX.TagID.TypeOfOrganization, 
                        name : CmFinoFIX.message.JSMerchant.Entries.TypeOfOrganization._name
                    },
                    {
                        xtype : 'textfield',
                        fieldLabel : _('Line Of Businesses / Industries'), 
                        allowBlank: false,
                        blankText : _('Line Of Business is required'),
                        anchor : '90%',
                        itemId : 'mer.form.business',
                        maxLength:255,
                        name : CmFinoFIX.message.JSMerchant.Entries.IndustryClassification._name
                    },
                    {
                        xtype : 'textfield',
                        fieldLabel : _('WebSite URL'),
                        anchor : '90%',
                        itemId : 'mer.form.url',
                        maxLength:255,
                        name: CmFinoFIX.message.JSMerchant.Entries.WebSite._name
                    },
                    {
                        xtype : "numberfield",
                        fieldLabel: _('Number of Outlet locations'),
                        anchor : '90%',
                        itemId : 'mer.form.locations',
                        maxValue:2000000000,
                        minValue:0,
                        name: CmFinoFIX.message.JSMerchant.Entries.NumberOfOutlets._name
                    }
                    ]
                },
                {
                    columnWidth: 0.5,
                    xtype: 'panel',
                    layout: 'form',
                    items:
                    [
                    {
                        xtype : 'numberfield',
                        allowDecimals:false,
                        fieldLabel : _('Year established'),
                        allowBlank: false,
                        blankText : _('Year established is required'),
                        itemId : 'mer.form.year',
                        anchor : '88%',
                        vtype:'yearCheck',
                        minLength : 4,
                        maxLength : 4,
                        name : CmFinoFIX.message.JSMerchant.Entries.YearEstablished._name
                    },
                    {
                        xtype:'fieldset',
                        title: _('Address'),
                        autoHeight:true,
                        defaults: {
                            width: 260
                        },
                        defaultType: 'textfield',
                        columns: 2,
                        items :[
                        {
                            fieldLabel: _('Line1'),
                            anchor : '90%',
                            maxLength:255,
                            itemId : 'mer.form.line1',
                            name : CmFinoFIX.message.JSMerchant.Entries.OutletAddressLine1._name
                        },
                        {
                            fieldLabel: _('Line2'),
                            anchor : '90%',
                            itemId : 'mer.form.line2',
                            maxLength:255,
                            name : CmFinoFIX.message.JSMerchant.Entries.OutletAddressLine2._name
                        },
                        {
                            fieldLabel: _('City'),
                            anchor : '90%',
                            maxLength:255,
                            itemId : 'mer.form.city',
                            name : CmFinoFIX.message.JSMerchant.Entries.OutletAddressCity._name
                        },
                        {
                            fieldLabel: _('State'),
                            anchor : '90%',
                            maxLength:255,
                            itemId : 'mer.form.state',
                            name : CmFinoFIX.message.JSMerchant.Entries.OutletAddressState._name
                        },
                        {
                            fieldLabel: _('Country'),
                            anchor : '90%',
                            maxLength:255,
                            itemId : 'mer.form.country',
                            name : CmFinoFIX.message.JSMerchant.Entries.OutletAddressCountry._name
                        },
                        {
                            fieldLabel: _('Postal Code'),
                            allowBlank: false,
                            blankText : _('Postal Code is required'),
                            anchor : '90%',
                            maxLength : 16,
                            itemId : 'mer.form.postalCode',
                            name : CmFinoFIX.message.JSMerchant.Entries.OutletAddressZipcode._name
                        }
                        ]
                    }
                    ]
                }]
            },
            {
                title: _('Authorization'),
                layout:'column',
                frame:true,
                autoHeight: true,
                width : 840,
                items:[
                {
                    xtype: 'panel',
                    columnWidth:0.5,
                    layout: 'form',
                    labelWidth : 150,
                    items :[
                    {
                        xtype: 'textfield',
                        fieldLabel: _('Representative Name'),
                        allowBlank: false,
                        blankText : _('Representative Name is required'),
                        anchor : '85%',
                        maxLength:255,
                        itemId : 'mer.form.repName',
                        name: CmFinoFIX.message.JSMerchant.Entries.RepresentativeName._name
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: _('Position'),
                        anchor : '85%',
                        itemId : 'mer.form.position',
                        maxLength:255,
                        name: CmFinoFIX.message.JSMerchant.Entries.Designation._name
                    },
                    {
                        xtype:'numberfield',
                        fieldLabel: _('Contact Number'),
                        allowBlank: false,
                        itemId : 'mer.form.contact',
                        blankText : _('Contact Number is required'),
                        anchor : '85%',
                        maxLength : 16,
                        name: CmFinoFIX.message.JSMerchant.Entries.AuthenticationPhoneNumber._name
                    },
                    {
                        xtype : 'numberfield',
                        allowDecimals:false,
                        fieldLabel : _('Fax Number'),
                        anchor : '85%',
                        maxLength : 16,
                        itemId : 'mer.form.fax',
                        name: CmFinoFIX.message.JSMerchant.Entries.AuthorizedFaxNumber._name
                    },
                    {
                        xtype:'textfield',
                        fieldLabel: _('Email Address'),
                        anchor : '85%',
                        vtype: 'email',
                        maxLength : 255,
                        itemId : 'mer.form.email',
                        name: CmFinoFIX.message.JSMerchant.Entries.AuthorizedEmail._name
                    },
                    {
                        xtype:'textfield',
                        fieldLabel: _('Source IP Address'),
                        itemId : 'mer.form.sourceIp',
                        anchor : '85%',
                        vtype: 'ipAddress',
                        name: CmFinoFIX.message.JSMerchant.Entries.H2HAllowedIP._name
                    },
                    {
                        xtype:'displayfield',
                        fieldLabel: _('Merchant Code'),
                        anchor : '85%',
                        name: CmFinoFIX.message.JSMerchant.Entries.MerchantCode._name
                    }
                    ]
                }
                ]
            },
            {
                title: _('MDN Range'),
                autoHeight: true,
                padding: '0 0 0 0',
                itemId : 'mer.form.mdnRangeTab',
                items:[ this.mdnRangeGrid ]
            }
            ]
        }
        ];
        
        this.mdnRangeGrid.action.on({
            action:function(grid, record, action, row, col) {
                if(action === 'mfino-button-remove'){
                    if(!(mFino.auth.isEnabledItem('mer.form.mdnRange') != "undefined" && mFino.auth.isEnabledItem('mer.form.mdnRange'))){
                        Ext.ux.Toast.msg(_("Message"), _("Permission denied"));
                        return;
                    }
                    var size = grid.store.getCount();
                    if(size > 1)
                    {
                        Ext.MessageBox.confirm(
                            _('Delete Range'),
                            _('Are you sure you want to delete this range?'),
                            function(btn){
                                if (btn == 'yes') {
                                    if(grid.store) {
                                        grid.store.remove(record);
                                        if(!record.phantom){
                                            grid.store.save();
                                        }
                                    }
                                }
                            });
                    }else if(size===1 & grid.LOPPermissionEnabled ){
                        Ext.ux.Toast.msg(_('Info'), _("Can't Delete, Atleast one MDN Range should be there"));
                    }
                    else
                    {
                        Ext.MessageBox.confirm(
                            _('Delete Range'),
                            _('Are you sure you want to delete this range?'),
                            function(btn){
                                if (btn == 'yes') {
                                    if(grid.store) {
                                        grid.store.remove(record);
                                        if(!record.phantom){
                                            grid.store.save();
                                        }
                                    }
                                }
                            });
                    }
                }
            }
        });
        this.mdnRangeGrid.getStore().on("load", function(store, records, options){
            if((mFino.auth.isEnabledItem('mer.form.mdnRange') != "undefined" && mFino.auth.isEnabledItem('mer.form.mdnRange')) && this.LOPPermissionEnabled){
                this.mdnRangeGrid.addBtn.setDisabled(false);
            }
            else {
                this.mdnRangeGrid.addBtn.setDisabled(true);
            }            
        //            store.each(function(record){
        //                record.set(CmFinoFIX.message.JSMDNRange.Entries.GridHideIndex._name, checkDisable);
        //            });
        },this);
       
        mFino.widget.MerchantForm.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    },
    
    disableNotPermittedItems: function(){
        var checkAbleItems = ['mer.form.status', 'mer.form.DCT', 'mer.form.parentId','mer.form.mdn','mer.form.sourceIp'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            if(!mFino.auth.isEnabledItem(itemIdStr)){
                checkItem.disable();
            }
        }
    },
    enablePermittedItems: function(){
        var checkAbleItems = ['mer.form.status', 'mer.form.DCT', 'mer.form.parentId'];
        for(var i = 0; i < checkAbleItems.length; i++){
            var itemIdStr = checkAbleItems[i];
            var checkItem = this.find("itemId", itemIdStr)[0];
            // The above items are disabled only for Edit operation. They should be available for Add.
            checkItem.enable();
        }
    },
    onStatusDropdown : function(status){
        if(status == CmFinoFIX.SubscriberStatus.PendingRetirement){

            var items = ['mer.form.DCT','mer.form.parentId','mer.form.lang','mer.form.curr','mer.form.time','mer.form.mdn','mer.form.status','tabpanelmerchant','mer.form.partnerType','mer.form.regionname'];
            for(var i=0;i<items.length;i++){
                this.find('itemId',items[i])[0].disable();
            }           
        }else if(status == CmFinoFIX.SubscriberStatus.Initialized || status == CmFinoFIX.SubscriberStatus.Active){

            items = ['mer.form.parentId','mer.form.lang','mer.form.curr','mer.form.time','mer.form.status','tabpanelmerchant','mer.form.partnerType'];
            for(i=0;i<items.length;i++){
                this.find('itemId',items[i])[0].enable();
            }

        }else if(status==CmFinoFIX.SubscriberStatus.Retired){
            this.getForm().items.get('mer.form.parentId').enable();
            
            items = ['mer.form.DCT','mer.form.lang','mer.form.curr','mer.form.time','mer.form.mdn','mer.form.status','tabpanelmerchant','mer.form.partnerType','mer.form.regionname'];
            for(i=0;i<items.length;i++){
                this.find('itemId',items[i])[0].disable();
            }
        }
    },
    onDCT : function(value){
        if(value){
            this.getForm().items.get('mer.form.parentId').setValue("");
            this.getForm().items.get('mer.form.parentId').disable();
        }else{
            this.getForm().items.get('mer.form.DCT').setValue("");
            this.getForm().items.get('mer.form.parentId').enable();
        }
    },
    onParent : function(value){
        if(value){
            this.getForm().items.get('mer.form.DCT').setValue("");
            this.getForm().items.get('mer.form.DCT').disable();
        }else{
            this.getForm().items.get('mer.form.parentId').setValue("");
            this.getForm().items.get('mer.form.DCT').enable();
        }
    },
    save : function(){
        
    	if(this.getForm().isValid()){
            this.getForm().updateRecord(this.record);
            var notiValue = 0;
            if(this.form.items.get("SMS").checked){
                notiValue = notiValue + CmFinoFIX.NotificationMethod.SMS;
            }
            if(this.form.items.get("Email1").checked){
                notiValue = notiValue + CmFinoFIX.NotificationMethod.Email;
            } 
                    
            this.record.beginEdit();
            this.record.set(CmFinoFIX.message.JSMerchant.Entries.NotificationMethod._name, notiValue);
                             
            if(!this.getForm().items.get('mer.form.parentId').getValue()){
                this.record.set(CmFinoFIX.message.JSMerchant.Entries.ParentID._name, "");
                this.record.set(CmFinoFIX.message.JSMerchant.Entries.ParentName._name, "");
            }
            this.record.endEdit();

            if(this.store){
                if(this.record.phantom){
                    this.store.insert(0, this.record);
                }
                this.store.save();
            }
        }
    },   
    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
        this.getForm().loadRecord(record);

        var merchantId = this.record.get(CmFinoFIX.message.JSMerchant.Entries.ID._name);
        this.mdnRangeGrid.setMdnId(merchantId);
        if(this.mdnRangeGrid.store && merchantId){
            this.mdnRangeGrid.store.baseParams[CmFinoFIX.message.JSMDNRange.limit._name]= CmFinoFIX.PageSize.Default;
            this.mdnRangeGrid.store.baseParams[CmFinoFIX.message.JSMDNRange.MerchantIDSearch._name]=merchantId;
            this.mdnRangeGrid.store.load();
        }
        
        var notiValue = record.get(CmFinoFIX.message.JSMerchant.Entries.NotificationMethod._name);
        this.form.items.get("SMS").setValue( (notiValue & CmFinoFIX.NotificationMethod.SMS) > 0);
        this.form.items.get("Email1").setValue( ( notiValue & CmFinoFIX.NotificationMethod.Email) > 0);
        var groupID = this.find('itemId', 'mer.form.groupID')[0];
        groupID.setDisabled(record.data["ID"]);
        groupID.setRawValue(record.get(CmFinoFIX.message.JSMerchant.Entries.GroupIDDisplayText._name));
        var parentID = this.find('itemId', 'mer.form.parentId')[0];
        if(parentID) {
            parentID.setRawValue(record.data["ParentName"]);
        }
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
    },
    isRangeCheckChanged: function(isRangeCheckUpdated)
   {
    	if(isRangeCheckUpdated)
    	{
    	this.record.beginEdit();
    	this.record.set(CmFinoFIX.message.JSMerchant.Entries.IsRangeCheckUpdated._name, isRangeCheckUpdated);
    	this.record.endEdit();
    	}
   },
    setLOPPermission:function(checkPermission){   
        this.LOPPermissionEnabled = checkPermission;
        this.mdnRangeGrid.setLOPPermission(checkPermission);
        if(checkPermission){
            this.mdnRangeGrid.addBtn.setDisabled(false);
        }
    }
});

Ext.reg("merchantform", mFino.widget.MerchantForm);
