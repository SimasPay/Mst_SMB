
/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.schedulerConfigSearchForm = function (config) {
	var localConfig = Ext.apply({}, config);

	localConfig = Ext.applyIf(localConfig, {
		labelPad : 10,
		layout:'column',
		labelWidth : 70,
		frame:true,
		title: _('schedulerConfigs'),
		bodyStyle:'padding:5px 5px 0',

		items: [
		        {
		            columnWidth:0.2,
		            layout:'form',
		            labelWidth:80,
		            items:[
		            {
		                xtype:'textfield',
		                fieldLabel:'ScheduleTemplate Name',
		                anchor:'95%',
		                name : CmFinoFIX.message.JSScheduleTemplate.NameSearch._name,
		                listeners : {
		                    specialkey: this.enterKeyHandler.createDelegate(this)
		                }
		            }
		            ]
		        },
		        /*
		        {
		            columnWidth:0.2,
		            layout:'form',
		            labelWidth:80,
		            items:[
		            {
		                xtype:'textfield',
		                fieldLabel:'ScheduleTemplate Mode',
		                anchor:'95%',
		                name : CmFinoFIX.message.JSScheduleTemplate.ModeSearch._name,
		                listeners : {
		                    specialkey: this.enterKeyHandler.createDelegate(this)
		                }
		            }
		            ]
		        },
		        */
		        {
		            columnWidth:0.05,
		            layout:'form',
		            labelWidth:50,
		            items:[
		            {
		                xtype:'displayfield',
		                anchor:'90%'
		            }
		            ]
		        },{
		            columnWidth:0.2,
		            layout:'form',
		            items:[
		            {
		                xtype:'button',
		                text:'Search',
		                anchor:'60%',
		                handler : this.searchHandler.createDelegate(this)
		            }
		            ]
		        }
		        ]
	});

	mFino.widget.schedulerConfigSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.schedulerConfigSearchForm, Ext.FormPanel, {

	initComponent : function () {
		mFino.widget.schedulerConfigSearchForm.superclass.initComponent.call(this);
		this.addEvents("search");
	},

	enterKeyHandler : function (f, e) {
		if (e.getKey() === e.ENTER) {
			this.searchHandler();
		}
	},

	searchHandler : function(){
		if(this.getForm().isValid()){
			var values = this.getForm().getValues();
			this.fireEvent("search", values);
		}else{
			Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
		}
	},
	resetHandler : function(){
		this.getForm().reset();
	}
});

