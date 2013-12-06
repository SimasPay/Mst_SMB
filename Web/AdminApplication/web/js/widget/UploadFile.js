/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.UploadFile = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        id: "uploadFile",
        defaultType: 'textfield',
        fileUpload:true,
        width: 500,
        frame : true,
        bodyStyle: 'padding: 10px 10px 0 10px;',
        labelWidth: 5,
        labelSeparator : ''
    });
    mFino.widget.UploadFile.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.UploadFile, Ext.FormPanel, {
    initComponent : function () {
        this.labelWidth = 140;
        this.labelPad = 5;
        this.defaults = {
            anchor: '85%',
            allowBlank: false,
            msgTarget: 'side',
            labelSeparator : ''
        };
        this.items = [
        {
            xtype : "enumdropdown",
            fieldLabel  : _('Customer Type'),
            labelSeparator : '',
            mode: 'local',
            triggerAction: 'all',
            emptyText : _('<Select one..>'),
            enumId : CmFinoFIX.TagID.RecordType,
            name: CmFinoFIX.message.JSBulkUploadFile.Entries.RecordType._name
        },
        {
            xtype: 'textfield',
            fieldLabel: _('Description'),
            emptyText: _('eg: Enter description about file'),
            name: CmFinoFIX.message.JSBulkUploadFile.Entries.Description._name
        },
        /*{
            xtype: 'numberfield',
            allowDecimals:false,
            fieldLabel: _('No. of Records'),
            emptyText: _('eg: Enter number of records'),
            name: CmFinoFIX.message.JSBulkUploadFile.Entries.RecordCount._name
        },*/
        {
            xtype: 'fileuploadfield',
            id: 'form-file1',
            buttonText: _('Browse'),
            fieldLabel: _('Upload File'),
            emptyText:_('eg: bulkupload.csv'),
            name: CmFinoFIX.message.JSBulkUploadFile.Entries.FileName._name
        }
        ];

        mFino.widget.UploadFile.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
        if(this.record){
            this.getForm().loadRecord(this.record);
        }
    }    
});

Ext.reg("UploadFile", mFino.widget.UploadFile);

