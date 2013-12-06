/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SystemParemterDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
        autoScroll : true,
        layout:'column',
        frame : true,
        items: [        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 100,
            items : [
             {
            	 xtype : "displayfield",
            	 fieldLabel :_("Paramter ID"),
            	 name: CmFinoFIX.message.JSSystemParameters.Entries.ID._name
             },
            {
            	xtype : "displayfield",
            	fieldLabel :_("Parameter Name"),
            	name: CmFinoFIX.message.JSSystemParameters.Entries.ParameterName._name
            },
        	{
            	xtype : "displayfield",
            	fieldLabel :_("Parameter Value"),
            	name: CmFinoFIX.message.JSSystemParameters.Entries.ParameterValue._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Desciption"),
        		name: CmFinoFIX.message.JSSystemParameters.Entries.Description._name
        	}
            ]
        },
        {
            columnWidth: 0.5,
            layout: 'form',
            labelWidth : 100,
            items : [
            {
            	xtype : "displayfield",
            	fieldLabel :_("Create Time"),
            	name: CmFinoFIX.message.JSSystemParameters.Entries.CreateTime._name
            },
        	{
            	xtype : "displayfield",
            	fieldLabel :_("Created By"),
            	name: CmFinoFIX.message.JSSystemParameters.Entries.CreatedBy._name
        	},
        	{
        		xtype : "displayfield",
        		fieldLabel :_("Last Update Time"),
        		name: CmFinoFIX.message.JSSystemParameters.Entries.LastUpdateTime._name
        	},
        	{
    			xtype : "displayfield",
    			fieldLabel :_("Updated By"),
    			name: CmFinoFIX.message.JSSystemParameters.Entries.UpdatedBy._name
    		}
            ]
        }
    ]
    });

    mFino.widget.SystemParemterDetails.superclass.constructor.call(this, localConfig);
};


Ext.extend(mFino.widget.SystemParemterDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 120;
        this.labelPad = 20;
        mFino.widget.SystemParemterDetails.superclass.initComponent.call(this);
    },

    setRecord : function(record){
        this.getForm().reset();
        this.record = record;
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

Ext.reg("systemparemterdetails", mFino.widget.SystemParemterDetails);

