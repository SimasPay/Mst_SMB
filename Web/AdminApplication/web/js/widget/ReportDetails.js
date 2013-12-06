/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.ReportDetails = function (config)
{
    var localConfig = Ext.apply({}, config);

    localConfig = Ext.applyIf(localConfig, {
    	bodyStyle:'padding:5px 5px 0',
        frame : true
//        width:643,
       
    });

    mFino.widget.ReportDetails.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.ReportDetails , Ext.form.FormPanel, {
    initComponent : function () {
        this.labelWidth = 220;
        this.labelPad = 20;
		this.items = [{
			
	              layout:'column',
	              items : [{
	                    	   columnWidth: 0.5,
	                    	   items : [{
	                    	            	items: [firstColumn]
	                    	            }]
	                       },{
	                    	    columnWidth: 0.5,
	                    	    items : [{
							                 items: [secondColumn]
	                    	              }]
	                       }]
				}];
       
        mFino.widget.ReportDetails.superclass.initComponent.call(this);
    },
    
   
    setItems : function(response,reportName){
    	this.clearItems();
    	this.addHeaderInfo(response,reportName);
    	
    	for (i = 0; i < response.m_ptotal; i++) {
    		if(response.Get_Entries()[i].m_pIndex ==0){
    			Ext.getCmp('first').add({
    		   		xtype: "displayfield",
    		   		anchor : '100%',
    		   		fieldLabel: response.Get_Entries()[i].m_pLable,
    		   		value: response.Get_Entries()[i].m_pValue
    		   		});
    			if(i+1<response.m_ptotal &&response.Get_Entries()[i+1].m_pIndex ==0&&!response.m_pIsActive ){
        			Ext.getCmp('second').add({
        		   		xtype: "displayfield",
        		   		anchor : '100%',
        		   		fieldLabel: "",
        		   		value: ''
        		   		});
        		}
    		}else{ 
    			Ext.getCmp('second').add({
    		   		xtype: "displayfield",
    		   		anchor : '100%',
    		   		fieldLabel: response.Get_Entries()[i].m_pLable,
    		   		value: response.Get_Entries()[i].m_pValue
    		   		});
    		}
   		 
   	}
    },
    
    addHeaderInfo : function(response,reportName){
    	Ext.getCmp('first').add({
	   		xtype: "displayfield",
	   		anchor : '100%',
	   		fieldLabel: reportName
	   		});
		Ext.getCmp('second').add({
	   		xtype: "displayfield",
	   		anchor : '100%',
	   		fieldLabel: "",
	   		value: ""
	   		});
		
		Ext.getCmp('first').add({
	   		xtype: "displayfield",
	   		fieldLabel: 'From',
	   		value: response.m_pReportStartDate
	   		});
		Ext.getCmp('second').add({
	   		xtype: "displayfield",
	   		fieldLabel: 'To',
	   		value:  response.m_pReportEndDate
	   		});
		for( i=0;i<5;i++){
		Ext.getCmp('second').add({
	   		xtype: "displayfield",
	   		anchor : '100%',
	   		fieldLabel: "",
	   		value: ""
	   		});
		}
		Ext.getCmp('first').add({
	   		xtype: "displayfield",
	   		anchor : '100%',
	   		fieldLabel: "",
	   		value: ""
	   		});
    },
    
    clearItems : function(){
    	 Ext.getCmp('first').removeAll();
    	 Ext.getCmp('second').removeAll();
    }
});

var firstColumn = {
	    title: '',
	    id:'first',
	    autoHeight: true,
	    width: 280,
	    layout: 'form',
	    items: []};
var secondColumn = {
	    title: '',
	    id:'second',
	    autoHeight: true,
	    width: 280,
	    layout: 'form',
	    items: []};
Ext.reg("reportdetails", mFino.widget.ReportDetails);

