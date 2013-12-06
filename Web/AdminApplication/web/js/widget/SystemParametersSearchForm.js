/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.SystemParametersSearchForm = function (config) {
    var localConfig = Ext.apply({}, config);
   
    localConfig = Ext.applyIf(localConfig, {
        labelPad : 10,
        layout:'column',
        labelWidth : 70,
        frame:true,
        title: _('System Parameters'),
        bodyStyle:'padding:5px 5px 0',

        items: [

		{
		  columnWidth:0.28,
          layout:'form',
          labelWidth:100,
          items:[
          {
              xtype : "remotedropdown", 
	                fieldLabel: _('Parameter Name'), 
	                labelSeparator : '',
	                id: 'parameterName',
	                anchor : '98%', 
	                emptyText : _('<select  >'),
	                pageSize: 10,
	                params: {start: 0, limit: 10},
	                store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSSystemParameters), 
	                displayField: CmFinoFIX.message.JSSystemParameters.Entries.ParameterName._name, 
	                valueField : CmFinoFIX.message.JSSystemParameters.Entries.ParameterName._name, 
	                hiddenName : CmFinoFIX.message.JSSystemParameters.ParameterNameSearch._name, 
	                name: CmFinoFIX.message.JSSystemParameters.ParameterNameSearch._name, 
	                listeners   : { 
	                    specialkey: this.enterKeyHandler.createDelegate(this) 
	                } 
          }
          ]
      },
      
        {
    	  columnWidth:0.20,
          layout:'form',
          labelWidth:100,
          items:[
          {
              xtype : 'textfield',
              allowDecimals:false,
              fieldLabel: _("Parameter Value"),
              labelSeparator : '',
              maxLength:50000,
              minValue:0,
              name: CmFinoFIX.message.JSSystemParameters.ParameterValueSearch._name,
              anchor : '95%',
              listeners   : {
                  specialkey: this.enterKeyHandler.createDelegate(this)
              }
          }
          ]
      },
      
      {
          columnWidth:0.25,
          layout:'form',
          labelWidth:60,
          items:[
          {
              xtype : 'textfield',
              allowDecimals:false,
              fieldLabel: _("Description"),
              labelSeparator : '',
              maxLength:50000,
              minValue:0,
              name: CmFinoFIX.message.JSSystemParameters.DescriptionSearch._name,
              anchor : '95%',
              listeners   : {
                  specialkey: this.enterKeyHandler.createDelegate(this)
              }
          }
          ]
      },
        
        {
            columnWidth:0.05,
            layout:'form',
            items:[
            {
                xtype:'displayfield',
                anchor:'50%'
            }
            ]
        },
		{
            columnWidth:0.10,
            layout:'form',
            items:[
            {
                xtype:'button',
                text:'Search',
                anchor:'60%',
                handler : this.searchHandler.createDelegate(this)
            }
            
            ]
        },
		{
            columnWidth:0.10,
            layout:'form',
            items:[
            {
                xtype: 'button',
                text: _('Reset'),
                anchor:'60%',
                handler : this.resetHandler.createDelegate(this)
            }
            
            ]
        }
        ]
    });

    mFino.widget.SystemParametersSearchForm.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.SystemParametersSearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.SystemParametersSearchForm.superclass.initComponent.call(this);
        this.addEvents("search");
        this.form.items.get("parameterName").getStore().reload();
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

