/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.BulkTransfer = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        id: "BulkTransfer",
        defaultType: 'textfield',
        fileUpload:true,
        width: 500,
        frame : true,
        bodyStyle: 'padding: 10px 10px 0 10px;',
        labelWidth: 5,
        labelSeparator : ''
    });

    mFino.widget.BulkTransfer.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.BulkTransfer, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 140;
        this.labelPad = 5;
        // this.renderTo= 'fi-form',
        this.defaults = {
            anchor: '85%',
            allowBlank: false,
            msgTarget: 'side',
            labelSeparator : '',
            allowDecimals:false,
            xtype: 'numberfield'
        };
        this.items = [
        {
            xtype : "enumdropdown",
            fieldLabel  : _('File Type'),
            labelSeparator : '',
            mode: 'local',
            itemId:'bulkfiletype',
            triggerAction: 'all',
            emptyText : _('<Select one..>'),
            enumId : CmFinoFIX.TagID.BulkUploadFileType,
            name: CmFinoFIX.message.JSBulkUpload.Entries.BulkUploadFileType._name,
            listeners:
            {
                select: function(field){
                    this.findParentByType("bulktransfer").enableCheckSum(field.getValue());
                }
            }
        },
        {
            xtype: 'textfield',
            id:'filedescription',
             maxLength : 255,
            fieldLabel: _('Description'),
            emptyText: _('eg: For the Month of November')
        },
        {
            xtype: 'fileuploadfield',
            id: 'form-file',
            buttonText: _('Upload'),
            fieldLabel: _('File'),
            emptyText:_('eg: bulkupload.csv'),
            name: 'file-path'
        },
        {
            xtype: 'displayfield',
            fieldLabel: _('Delivery Date')
        },
        {
            xtype : 'panel',
            bodyStyle: 'padding:5px 5px 0',
            layout : 'column',
            items : [
            {
                columnWidth: 0.5,
                xtype : "radio",
                boxLabel : _('Future Date'),
                name : 'groupone',
                itemId:'futuredatechecked',
                checked : true,
                hideLabel: false,
                labelSeparator: '',
                style:{
                    display:'inline'
                },
                value : 0,
                listeners:{
                    check: function()
                    {
                        if(this.checked){
                            this.findParentByType("bulktransfer").onFutureClick();
                        }
                    }
                }
            },
            {
                columnWidth: 0.5,
                xtype : "datefield",
                itemId:'futuredate',
                allowBlank : false
            }
            ]
        },
        {
            xtype : 'panel',
            bodyStyle: 'padding:5px 5px 0',
            layout : 'column',
            items : [
            {
                columnWidth: 0.5,
                xtype : "radio",
                name : 'groupone',
                boxLabel : _('Immediately'),
                hideLabel: true,
                itemId:'immediatechecked',
                labelSeparator: '',
                value : 1,
                listeners:{
                    check: function()
                    {
                        if(this.checked){
                            this.findParentByType("bulktransfer").onImmediateClick();
                        }
                    }
                }
            },
            {
                columnWidth: 0.5,
                xtype : 'textfield',
                editable:false,
                itemId:'immediate',
                disabled:true
            }
            ]
        },
        {
            xtype: 'displayfield',
            fieldLabel: _('Validation')
        },
        {
            fieldLabel: _('No. of Transactions'),
            vtype:'number16',
            emptyText:_('eg: 1124'),
            id:'filenooftransactions'
        },
        {
            fieldLabel: _('Total Amount'),
            vtype:'number16',
            emptyText:_('eg: 1124'),
            id:'filetotalamount'
        },
        {
            xtype : 'textfield',
            fieldLabel: _('Check Num'),
            maxLength : 16,
            id:'checkNumber',
            disabled: true
        },
        {
            xtype : 'textfield',
            fieldLabel: _('Bank Branch Code'),
            id:'bankBranchCode',
            maxLength : 16,
            disabled: true
        }
        ];

        mFino.widget.BulkTransfer.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        if(this.record){
            this.getForm().loadRecord(this.record);
        }
    },
    enableCheckSum: function(value)
    {
       if(value == CmFinoFIX.BulkUploadFileType.BankAccountTransfer)
        {
            this.form.items.get('checkNumber').enable();
            this.form.items.get('bankBranchCode').enable();
        }
        else
        {
            this.form.items.get('checkNumber').reset();
            this.form.items.get('bankBranchCode').reset();
            this.form.items.get('checkNumber').disable();
            this.form.items.get('bankBranchCode').disable();
       }
    },
    onImmediateClick: function()
    {
        this.form.items.get('futuredate').reset();
        this.form.items.get('futuredate').disable();
        var date =new Date();
        this.form.items.get('immediate').setValue(date.format('m/d/Y'));
    },
    onFutureClick: function()
    {
        this.form.items.get('immediate').reset();
        this.form.items.get('futuredate').enable();
        this.form.items.get('futuredate').reset();
        this.form.items.get('immediate').disable();

    }
});

Ext.reg("bulktransfer", mFino.widget.BulkTransfer);

