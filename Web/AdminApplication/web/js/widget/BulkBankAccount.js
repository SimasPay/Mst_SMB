/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkBankAccount = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        modal:true,
        title : _("Bulk Link MDN to Card"),
        layout:'fit',
        floating: true,
        width:400,
        height:160,
        plain:true
    });

    mFino.widget.BulkBankAccount.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkBankAccount, Ext.Window, {
    initComponent : function () {
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
        
        this.form = new Ext.FormPanel({
            defaults: {
                anchor: '90%',
                allowBlank: false,
                msgTarget: 'side'
            },
            fileUpload:true,
            frame : true,
            bodyStyle: 'padding: 10px 10px 0 10px;',
            labelWidth: 50,
            labelSeparator : '',
            items : [
            {
                xtype: 'fileuploadfield',
                fieldLabel: _('File'),
                emptyText:_('eg: bulkupload.csv'),
                buttonText: _('Upload')
            }
            ]
        });

        this.items = [this.form];
        mFino.widget.BulkBankAccount.superclass.initComponent.call(this);
    },

    ok : function(){
        if(this.form.getForm().isValid()){
            this.form.getForm().submit({
                url: 'uploadbankaccount.htm',
                waitMsg: _('Uploading your file...'),
                success : function(fp, action){
                    Ext.Msg.show({
                        title: _('Info'),
                        minProgressWidth:250,
                        msg: _('Successfully uploaded the file to the server. You will receive a report by email very soon.'),
                        buttons: Ext.MessageBox.OK,
                        multiline: false
                    });
                    this.close();
                }.createDelegate(this),
                failure : function(fp, o){
                    Ext.Msg.show({
                        title: _('Error'),
                        minProgressWidth:250,
                        msg: _('Failed to upload the file to the server'),
                        buttons: Ext.MessageBox.OK,
                        multiline: false
                    });
                    this.close();
                }.createDelegate(this)
            });
        }
    },

    cancel : function(){
        this.close();
    }
});

Ext.reg("bulkbankaccount", mFino.widget.BulkBankAccount);

