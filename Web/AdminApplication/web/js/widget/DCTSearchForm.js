/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DCTSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Search Distribution Templates'),
        bodyStyle:'padding:5px 5px 0',
        defaultType: 'textfield',

        items: [{
            fieldLabel: _('Dist Chain Template'),
            labelSeparator : '',
            maxLength:255,
            name: CmFinoFIX.message.JSDistributionChainTemplate.NameSearch._name,
            listeners   : {
                specialkey: this.enterKeyHandler.createDelegate(this)
            }
        },
		{
			xtype : "remotedropdown",
			anchor : '95%',
			allowBlank: true,
			itemId : 'dctsearch.form.service',
			id : 'dctsearch.form.id.service',
			fieldLabel :"Service",
			emptyText:_('<select one..>'),			
			RPCObject : CmFinoFIX.message.JSService,
			displayField: CmFinoFIX.message.JSService.Entries.ServiceName._name,
			valueField : CmFinoFIX.message.JSService.Entries.ID._name,
			name: CmFinoFIX.message.JSDistributionChainTemplate.ServiceIDSearch._name
		}
//		{
//			xtype : "combo",
//			anchor : '95%',
//			fieldLabel :_("Service"),
//			//allowBlank: false,
//			//emptyText : _('<select one..>'),
//			itemId : 'dctsearch.form.service',
//			triggerAction: "all",
//			forceSelection : true,
//			lastQuery: '',
//			store : new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSService),
//			displayField: CmFinoFIX.message.JSService.Entries.ServiceName._name,
//			valueField : CmFinoFIX.message.JSService.Entries.ID._name,
//			name: CmFinoFIX.message.JSDistributionChainTemplate.ServiceIDSearch._name
//		}
        ]
    });

    mFino.widget.DCTSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.DCTSearchForm, Ext.FormPanel, {

    initComponent : function () {
        this. buttons = [{
            text: _('Search'),
            handler : this.searchHandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];

		

        mFino.widget.DCTSearchForm.superclass.initComponent.call(this);
        this.addEvents("DCTSearchEvent");
		this.form.items.get("dctsearch.form.service").getStore().reload();
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },

    searchHandler : function(){
        if(this.getForm().isValid()){
            var values = this.getForm().getValues();
			var sValues = {NameSearch: values.NameSearch, ServiceIDSearch: Ext.getCmp("dctsearch.form.id.service").getValue()};
            this.fireEvent("DCTSearchEvent", sValues);
        } else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    },
    resetHandler : function(){
        this.getForm().reset();
    }
});
