/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.UploadTransferFile = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        id: "uploadTransferFile",
        defaultType: 'textfield',
        fileUpload:true,
        width: 550,
        frame : true,
        bodyStyle: 'padding: 10px 10px 0 10px;',
        labelWidth: 5,
        labelSeparator : ''
    });
    mFino.widget.UploadTransferFile.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.UploadTransferFile, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 220;
        this.labelPad = 5;
        this.defaults = {
            anchor: '90%',
            allowBlank: false,
            msgTarget: 'side',
            labelSeparator : ':'
        };
        this.items = [
        {
           	xtype: 'textfield',
           	fieldLabel: _('Name'),
           	itemId : 'transfer.form.name',
           	emptyText: _(''),
           	maxLength : 50,
           	name: CmFinoFIX.message.JSBulkUpload.Entries.Name._name
        },        
        {
            xtype: 'fileuploadfield',
            id: 'transfer.form.file',
            buttonText: _('Browse'),
            fieldLabel: _('Upload CSV File'),
            emptyText:_(''),
            name: CmFinoFIX.message.JSBulkUpload.Entries.FileName._name
        },
        {
          	xtype : 'datefield',
          	editable: false,
          	fieldLabel: _('Execution Date Time'),
          	emptyText: _(''),
          	itemId : 'transfer.form.paymentdate',
          	name: CmFinoFIX.message.JSBulkUpload.Entries.PaymentDate._name,
            minValue:new Date().add('d', -2).clearTime()
            //disabledDates: [new Date()],
            //minText:'Date must be future date'
       	},
        {
            xtype: 'textfield',
            fieldLabel: _('Description'),
            itemId : 'transfer.form.description',
            emptyText: _(''),
            name: CmFinoFIX.message.JSBulkUpload.Entries.Description._name
        }
        ];

        mFino.widget.UploadTransferFile.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        if(this.record){
            this.getForm().loadRecord(this.record);
        }
    },
    
    onUploadTransfer: function(formWindow){
        
        if(this.getForm().isValid()){

            var status;

            this.getForm().submit({
                url: 'bulkTransferInquiry.htm',
                waitMsg: _('Uploading Bulk Transfer file...'),
                reset: false,
                success : function(fp, action){
                	formWindow.hide();
					var ConfirmTransfer = new mFino.widget.FormWindowLOP(Ext.apply({
	                    form : new mFino.widget.ConfirmTransferFile(),
	                    title : _(action.result.name + " - BULK TRANSFER SUMMARY"),
	                    height : 270,
	                    width:450,
	                    mode:"confirmTransfer"
	                	},null));
	                
						ConfirmTransfer.form.setDetails(action.result.id, action.result.disPalyId, action.result.name, action.result.count, 
								action.result.totalAmount, action.result.paymentDate, action.result.description);
	                	ConfirmTransfer.show();	
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
    }
    
});

Ext.reg("uploadtransferfile", mFino.widget.UploadTransferFile);

