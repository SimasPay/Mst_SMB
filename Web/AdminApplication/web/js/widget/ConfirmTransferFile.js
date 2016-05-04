/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ConfirmTransferFile = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        id: "ConfirmTransferFile",
        defaultType: 'displayfield',
        width: 550,
        frame : true,
        bodyStyle: 'padding: 10px 10px 0 10px;',
        labelWidth: 5,
        labelSeparator : ''
    });
    mFino.widget.ConfirmTransferFile.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ConfirmTransferFile, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 220;
        this.labelPad = 5;
        this.defaults = {
            anchor: '90%',
            msgTarget: 'side',
            labelSeparator : ':'
        };
        this.items = [
        {
           	xtype: 'displayfield',
           	fieldLabel: _('Name'),
           	itemId : 'confirm.form.name'
        },
        {
           	xtype: 'displayfield',
           	fieldLabel: _('Batch #'),
           	itemId : 'confirm.form.displayId'
        },        
        {
           	xtype: 'displayfield',
           	fieldLabel: _('Total MDN'),
           	itemId : 'confirm.form.count'
        },        
        {
           	xtype: 'displayfield',
           	fieldLabel: _('Total Amount to be disbursed'),
           	itemId : 'confirm.form.totalAmount'
        },
        {
           	xtype: 'displayfield',
           	fieldLabel: _('Execution Date Time'),
           	itemId : 'confirm.form.time'
        },
        {
           	xtype: 'displayfield',
           	fieldLabel: _('Description'),
           	itemId : 'confirm.form.description'
        },
        {
            xtype : 'hidden',
            itemId : 'confirm.form.id',
            name: CmFinoFIX.message.JSBulkUpload.Entries.ID._name
        },
        {
            xtype : 'hidden',
            itemId : 'confirm.form.status',
            name: 'confirmStatus'
        }        
        ];

        mFino.widget.ConfirmTransferFile.superclass.initComponent.call(this);
    },
    
    setDetails : function(id, displayId, name, count, totalAmount, time, description){
        this.getForm().reset();
        this.form.items.get("confirm.form.id").setValue(id);
    	this.form.items.get("confirm.form.displayId").setValue(displayId);
    	this.form.items.get("confirm.form.name").setValue(name);
    	this.form.items.get("confirm.form.count").setValue(count);
    	this.form.items.get("confirm.form.totalAmount").setValue(totalAmount);
    	this.form.items.get("confirm.form.time").setValue(time);
    	this.form.items.get("confirm.form.description").setValue(description);
    },
    
    onCancelConfirm: function(formWindow) {
        var status;
        this.form.items.get("confirm.form.status").setValue(false);
        this.getForm().submit({
            url: 'bulkTransferConfirm.htm',
            waitMsg: _('Processing the Bulk Transfer ...'),
            reset: false,
            success : function(fp, action){
            	formWindow.hide();
            },
            failure : function(fp, action){
            	formWindow.hide();
                Ext.Msg.show({
                    title: _('Error'),
                    minProgressWidth:250,
                    msg: action.result.Error,
                    buttons: Ext.MessageBox.OK,
                    multiline: false
                });
            }
            ,
            params: {
                markAs:status
            }
        });
    },
    
    onConfirmTransfer: function(formWindow) {
        var status;
        this.form.items.get("confirm.form.status").setValue(true);
        this.getForm().submit({
            url: 'bulkTransferConfirm.htm',
            waitMsg: _('Processing the Bulk Transfer ...'),
            reset: false,
            success : function(fp, action){
            	formWindow.hide();
            },
            failure : function(fp, action){
            	formWindow.hide();
                Ext.Msg.show({
                    title: _('Error'),
                    minProgressWidth:250,
                    msg: action.result.Error,
                    buttons: Ext.MessageBox.OK,
                    multiline: false
                });
            }
            ,
            params: {
                markAs:status
            }
        });
    }
});

Ext.reg("ConfirmTransferFile", mFino.widget.ConfirmTransferFile);

