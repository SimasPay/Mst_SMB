/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ReportSearchForm = function (config) {
  
    var localConfig = Ext.apply({}, config);
    localConfig = Ext.applyIf(localConfig, {
        layout:'column',
        frame : true,
        title: _('Report '),
        height:100,
        items:[
        {
            columnWidth:0.3,
            layout:'form',
            labelWidth:80,
            items:[          {
                xtype : "combo",
                fieldLabel: _('Report Name'),
                labelSeparator : '',
//                itemId: "report.name",
                anchor : '98%',
                triggerAction: "all",
                forceSelection : true,
                pageSize : 20,
                addEmpty : true,
                emptyText : _('<select one..>'),
                store: new FIX.FIXStore(mFino.DATA_URL, CmFinoFIX.message.JSOfflineReport),
                displayField: CmFinoFIX.message.JSOfflineReport.Entries.Name._name,
                valueField : CmFinoFIX.message.JSOfflineReport.Entries.Name._name,
                name: CmFinoFIX.message.JSReport.ReportName._name,
                listeners : {
                   specialkey: this.enterKeyHandler.createDelegate(this)
                	}
            }
            	
            ]
        },
        {
            columnWidth:0.3,
            layout:'form',
            labelWidth:60,
            items:[{
					 xtype : 'datefield',
					 fieldLabel: 'FromDate',
					 editable: false,
					 allowBlank: false,
					 itemId : 'start',
					 anchor : '80%',
					 format : 'd/m/Y',
					 maxValue:new Date().add('d',-1),
				     maxText:'Date of birth should not be future date',
					 name: CmFinoFIX.message.JSReport.ReportStartDate._name
            } 
            ]
        },
        {
            columnWidth:0.3,
            layout:'form',
            labelWidth:60,
            items:[
					{
						 xtype : 'datefield',
						 fieldLabel: 'ToDate',
						 editable: false,
						 itemId : 'end',
						 anchor : '80%',
						 format : 'd/m/Y',
						 allowBlank: false,
						 maxValue:new Date(),
						 maxText:'Date should not be future date',
						 name: CmFinoFIX.message.JSReport.ReportEndDate._name
					}]
        },
        {
	        columnWidth:0.1,
	        layout:'form',
	        labelWidth:60,
	        items:[      
					 {
		                xtype:'button',
		                fieldLabel: '',
		                text:'GetReport',
		                anchor:'60%',
		                handler: this.searchHandler.createDelegate(this)
		            }]
        } ]
    });

    mFino.widget.ReportSearchForm.superclass.constructor.call(this, localConfig);
    
};

Ext.extend(mFino.widget.ReportSearchForm, Ext.FormPanel, {

    initComponent : function () {
        mFino.widget.ReportSearchForm.superclass.initComponent.call(this);
        this.addEvents("getReport");
        },
    
    enterKeyHandler : function (f, e) {
        if (e.getKey() === e.ENTER) {
            this.searchHandler();
        }
    },   
    searchHandler : function(){
    	if(this.getForm().isValid()){
        var values = this.getForm().getValues();  
        
//        if(values.ReportStartDate){
//        	values.ReportStartDate = this.find('itemId','start')[0].getValue().format('Ymd-H:i:s:u');
//        }
//        if(values.ReportEndDate){
//        	values.ReportEndDate =this.find('itemId','end')[0].getValue().format('Ymd-H:i:s:u');
//        }
        if(!values.ReportStartDate){
        	this.fireEvent("getReport", values);
        }else if(this.find('itemId','start')[0].getValue()<this.find('itemId','end')[0].getValue()){
        	this.fireEvent("getReport", values);
        }else{
        	 Ext.ux.Toast.msg(_("Error"), _("FromDate must be less than ToDate"),5);
        }
    	}  else{
            Ext.ux.Toast.msg(_("Error"), _("Some fields have invalid information. <br/> Please fix the errors before search"),5);
        }
    }
});
Ext.reg('reportsearchform',mFino.widget.ReportSearchForm);