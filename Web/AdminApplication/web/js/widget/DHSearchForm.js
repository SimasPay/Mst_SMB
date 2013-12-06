/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DHSearchForm = function (config) {

    var localConfig = Ext.apply({}, config);

	localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        labelWidth : 70,
        frame:true,
        title: _('Distribution Template Search'),
        bodyStyle:'padding:5px 5px 0',
        items : [
					{
						xtype : "textfield",
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
						anchor : '90%',
						allowBlank: true,
						itemId : 'dhsearch.form.service',
						id : 'dhsearch.form.id.service',
						fieldLabel :"Service",
						//emptyText:_('<select one..>'),
						RPCObject : CmFinoFIX.message.JSService,
						displayField: CmFinoFIX.message.JSService.Entries.ServiceName._name,
						valueField : CmFinoFIX.message.JSService.Entries.ID._name,
						name: CmFinoFIX.message.JSDistributionChainTemplate.ServiceIDSearch._name,
						pageSize : 10
					}
              ]
        
    });

	try{
		mFino.widget.DHSearchForm.superclass.constructor.call(this, localConfig);
	}
	catch(e){
		alert('constructor '+e);
	}

};

Ext.extend(mFino.widget.DHSearchForm, Ext.FormPanel, {

    initComponent : function () {

        this. buttons = [{
            text: _('Search'),
            handler : this.searchhandler.createDelegate(this)
        },
        {
            text: _('Reset'),
            handler : this.resetHandler.createDelegate(this)
        }];

		try{		
			mFino.widget.DHSearchForm.superclass.initComponent.call(this);
		}
		catch(e){
			alert('initComponent '+e);
		}

		this.addEvents("DHSearchEvent");
		this.form.items.get("dhsearch.form.service").getStore().reload();
    },

    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },

    searchhandler : function(){
		if(this.getForm()){
			if(this.getForm().isValid()){
				//this.form.items.get("dhsearch.form.service").getStore().reload();
				var values = this.getForm().getValues();
				var srchServiceIDSearch =  this.form.items.get("dhsearch.form.service").getValue()
				var val = {NameSearch : values.NameSearch, ServiceIDSearch: srchServiceIDSearch};
				this.fireEvent("DHSearchEvent", val);
			} else{
				Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
			}
	    }
    },

    resetHandler : function(){
		this.getForm().reset();
    }
});


Ext.reg("DHSearchForm", mFino.widget.DHSearchForm);