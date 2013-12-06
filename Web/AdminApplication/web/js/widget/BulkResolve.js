mFino.widget.BulkResolve = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        bodyStyle:'padding:5px 5px 0',
        frame : true,
        selectOnFocus: true,
        msgTarget: 'side',
        fileUpload: true
    });

    mFino.widget.BulkResolve.superclass.constructor.call(this, localConfig);
};
Ext.extend(mFino.widget.BulkResolve, Ext.form.FormPanel, {
    initComponent : function ()
    {
        this.labelWidth = 140;
        this.labelPad = 5;
        // this.renderTo= 'fi-form',
        this.defaults = {
            anchor: '85%',
            allowBlank: false,
            msgTarget: 'side',
            labelSeparator : ''
        };
        this.items = [
        {
            xtype: 'textfield',
            maxLength : 255,
            fieldLabel: _('Description'),
            name:'Description',
            emptyText: _('message')
        },
        {
            xtype: 'fileuploadfield',
            //id: 'form-file-bulkresolve',
            buttonText: _('Browse'),
            fieldLabel: _('File'),
            emptyText:_('eg: bulkupload.csv'),
            name: 'file'
        },
        {
            xtype:'numberfield',
            allowDecimals:false,
            fieldLabel: _('No. of Transactions'),
            vtype:'number16',
            emptyText:_('eg: 1124'),
            name:'filenooftransactions'
        },
        {
                xtype: 'fieldset',
                title : _('Select One'),
                layout : 'column',
                autoHeight: true,
                anchor : '90%',
                columns: 1,
                items: [
                {
                    columnWidth: 1,
                    xtype : 'radio',
                    itemId : 'complete',
                    name: 'selectone',
                    anchor : '90%',
                    checked : true,
                    boxLabel: _('  Mark the Transaction as complete ')
                },
                {
                    columnWidth: 1,
                    xtype : 'radio',
                    itemId : 'cancel',
                    anchor : '90%',
                    name: 'selectone',
                    boxLabel: _('  Mark the Transaction as cancel ')
                }]
            }
        ];
        mFino.widget.BulkResolve.superclass.initComponent.call(this);
        markMandatoryFields(this.form);
    }
});
Ext.reg("bulkresolve", mFino.widget.BulkResolve);