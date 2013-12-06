/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SystemParameterViewForm = function (config) {
	var localConfig = Ext.apply({}, config);
	localConfig = Ext.applyIf(localConfig, {
		bodyStyle:'padding:5px 5px 0',
		frame : true
	});

	mFino.widget.SystemParameterViewForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SystemParameterViewForm, Ext.FormPanel, {
	initComponent : function () {
		this.labelWidth = 100;
		this.labelPad = 20;
		this.items = [
		              {
		            	  xtype : "displayfield",
		            	  fieldLabel :_("Parameter Name"),
		            	  anchor : '95%',
		            	  name: CmFinoFIX.message.JSSystemParameters.Entries.ParameterName._name
		              },
		              {
		            	  xtype : "displayfield",
		            	  fieldLabel :_("Parameter Value"),
		            	  anchor : '95%',
		            	  name: CmFinoFIX.message.JSSystemParameters.Entries.ParameterValue._name
		              },
		              {
		            	  xtype : "displayfield",
		            	  fieldLabel :_("Desciption"),
		            	  anchor : '95%',
		            	  name: CmFinoFIX.message.JSSystemParameters.Entries.Description._name
		              }
		              ];

		mFino.widget.SystemParameterViewForm.superclass.initComponent.call(this);
	},
	setRecord : function(record){
		this.getForm().reset();
		this.record = record;
		this.getForm().loadRecord(record);
		this.getForm().clearInvalid();  
	}

});

Ext.reg("systemparameterviewform", mFino.widget.SystemParameterViewForm);
