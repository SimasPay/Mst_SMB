/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkTransferEntrySearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px',
        frame:true,
        border:true,
        items: [{
        	 layout:'column',
             border:false,
             items: [
        			{
                        columnWidth:0.25,
                        layout: 'form',
						labelWidth: 50,
                        items: [{                    
				                    xtype : "enumdropdown",
				                    fieldLabel: _('Status'),
				                    itemId: "transferstatus",
				                    emptyText : _('<select one..>'),
									anchor: '90%',
				                    enumId : CmFinoFIX.TagID.TransactionsTransferStatus,
				                    name: CmFinoFIX.message.JSBulkUploadEntry.TransactionsTransferStatus._name,
				                    listeners   : {
				                        specialkey: this.enterKeyHandler.createDelegate(this)
				                    }
                        	   }]
        			},
        			{
                        columnWidth:0.15,
                        layout: 'form',
                        items: [{
		                            xtype:'button',
		                            text:'Search',
									anchor: '80%',
		                            handler : this.searchHandler.createDelegate(this)
				                }]
        			},
        			{
                        columnWidth:0.15,
                        layout: 'form',
                        items: [{
		                            xtype:'button',
		                            text:'Reset',
									anchor: '80%',
		                            handler : this.resetHandler.createDelegate(this)
				               }]
        			}
        			]
        }]
    });

    mFino.widget.BulkTransferEntrySearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkTransferEntrySearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.BulkTransferEntrySearchForm.superclass.initComponent.call(this);
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },
    searchHandler : function(){
        if(this.getForm().isValid()){
        	 var values = this.getForm().getValues();
        	 if(values.TransactionsTransferStatus === "undefined"){
                 values.TransactionsTransferStatus =null;
             }
        	 if (values.IsUnRegistered ===  "on"){
                 values.IsUnRegistered = true;
             }
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
