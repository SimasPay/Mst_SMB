/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.appUploader = function(config) {
	var my_form = new Ext.FormPanel(
	{
 	   url : document.URL.split('AdminApplication')[0] + 'AppSelector/uploadFile',
	   title : 'App Uploader',
	   method : 'POST',
	   fileUpload : true,
       bodyPadding : 5,
	   buttonAlign : 'center',
	   width : 500,
	   height : '50%',
       frame : true,
       items : [
 	         {
	        	 xtype : 'combo',
	        	 fieldLabel : 'App Type',
	        	 hiddenName : 'appType',
	        	 autoload:true,
	        	 allowBlank : false,
	        	 id:'appTypeID',
	        	 itemId: 'upload.appType',
	        	 width : 127,
	        	 mode : 'local',
	        	 store : new Ext.data.SimpleStore({
	        		 data : [['subscriberapp'],['agentapp']],
	                 id : 0,
	                 fields : ['text']
	        	 }),
	         	 valueField : 'text',
	        	 displayField : 'text',
	        	 triggerAction : 'all',
	        	 editable : false,
	        	 listeners: {
	        		 select: function(field){
 	        			 my_form.find("itemId", "upload.platform")[0].enable();  
                    	 my_form.find("itemId", "upload.platform")[0].clearValue(); 

                    	 my_form.find("itemId","upload.newVersion")[0].reset();
						 my_form.find("itemId","upload.newVersion")[0].disable();
						 my_form.find("itemId","upload.presentVersion")[0].reset();
                    	 my_form.find("itemId","upload.presentVersion")[0].disable();
                    	 my_form.find("itemId", "upload.filePath")[0].reset();
                    	 my_form.find("itemId", "upload.filePath")[0].disable();
	        		 }
	        	}
		     },	         
		     {
	        	 xtype : 'combo',
	        	 fieldLabel : 'Platform',
	        	 hiddenName : 'platform',
	        	 autoload:true,
	        	 allowBlank : false,
	        	 id:'platformID',
	        	 itemId: 'upload.platform',
	        	 width : 127,
	        	 mode : 'local',
	        	 store : new Ext.data.SimpleStore(
	        	 {
	        		 data : [ [ 'android'], [ 'blackberry' ],[ 'j2me' ] ],
	        		 id : 0,
	        		 fields : [ 'text' ]
	        	 }),
	        	 valueField : 'text',
	        	 displayField : 'text',
	        	 triggerAction : 'all',
	        	 editable : false,
	        	 disabled: true,
	        	 listeners: 
	        	 {
	        		 select: function(field) {
	        			 my_form.find("itemId", "upload.presentVersion")[0].reset();
 	        			 my_form.find("itemId", "upload.newVersion")[0].reset();
 	        			 my_form.find("itemId", "upload.newVersion")[0].enable();
 	        			 my_form.find("itemId", "upload.filePath")[0].reset();
	        			 my_form.find("itemId", "upload.filePath")[0].enable();
	        			 var appType = my_form.find("itemId", "upload.appType")[0].value;
	        			 var platform = my_form.find("itemId", "upload.platform")[0].value;  
	        			 Ext.Ajax.request({     
	    	        	     url: document.URL.split('AdminApplication')[0] + 'AppSelector/uploadversion',
	    	        	     params: {'appType': appType, 'platform': platform},
	    	        	     success: function(response, options) {
	    	        	    	 var data = Ext.util.JSON.decode(response.responseText);
	    	        	    	 var oldVersion = data.version;
	    	        	    	 my_form.find("itemId", "upload.presentVersion")[0].setValue(oldVersion);
	    	        	     }
	        			 });
	        		 }
	        	 }
	         },
	         {
	        	 xtype : 'textfield',
	        	 allowBlank : false,
	        	 fieldLabel : 'App Present Version',
	        	 id : 'presentVersionID',
	        	 itemId: 'upload.presentVersion',
	        	 name : 'presentVersion',
	        	 disabled: true
	         },
	         {
	        	 xtype : 'numberfield',
	        	 allowBlank : false,
	        	 fieldLabel : 'App New Version',
	        	 id : 'newVersionID',
	        	 itemId: 'upload.newVersion',
	        	 name : 'newVersion',
	        	 disabled: true,
	        	 listeners:{

	        		 change: function(field) {
	        			 var presentVersion = my_form.find("itemId", "upload.presentVersion")[0].value;
	        			 var newVersion = field.getValue();
	        			 if(newVersion < presentVersion || newVersion==presentVersion )
	        				 {
	        				 	Ext.Msg.alert('Error','New Version should be greater than Previous Version !! ');
	        				 	 my_form.find("itemId", "upload.newVersion")[0].reset();
	     	        			 my_form.find("itemId", "upload.newVersion")[0].enable();
	     	        			 my_form.find("itemId", "upload.filePath")[0].reset();
							 }
	        	 	 }
	        	  }
	         },
	         {
	        	 xtype : 'fileuploadfield',
	        	 allowBlank : false,
	        	 emptyText : 'Select a file for uploading..',
	        	 labelWidth : 50,
	        	 msgTarget : 'side',
	        	 anchor : '38%',
	        	 fieldLabel : 'File path',
	        	 buttonText : 'Select a File',
	        	 name : 'sourceFilePath',
	        	 id : 'filepath',
	        	 itemId: 'upload.filePath',
	        	 disabled: true
	         },
	         {
	        	 xtype : 'container',
	        	 layout : 'absolute',
	        	 items : 
	        	 { 	 
	        		 xtype : 'button',
	        		 align : 'middle',
	        		 x : 104,
	        		 y : 30,
	        		 text : 'Submit',
	        		 margin : 150,
	        		 centered : true,
	        		 width : 70,
	        		 left : 300,
	        		 listeners: 
	        		 {
	        			 render: function()
	        			 {
	        				 this.getEl().on('click', function(e){
	        					 if(my_form.getForm().isValid())
								 {
		        					 Ext.MessageBox.confirm('Confirm', 'Do you want to proceed with the file upload',
		        					 function(btn){ 
		        				     if (btn == 'yes'){ 			
							 			my_form.getForm().submit({
						 				 clientValidation : true,
										 waitMsg : 'Sending Data',
										 waitTitle : 'Wait',
										 success : function(form, action){
											 Ext.Msg.alert('Success','Processed file on the server');
											 form.reset();
										 },
										 failure : function(form, action) {
											 Ext.Msg.alert('Error','Please select \'.apk\' file for android, \'.jar\' for j2me and \'.zip\' file for blackberry. Please select the files with filename as mentioned in configuration file.');
										 }
	        							 });
		        					 } 
		        				   }); 
	        				 } 
	        					 else
	        						 {
	        						 	Ext.Msg.alert('Error','Incomplete Data');

	        						 }
	        					 });    
	        			 }
	        		 }
	        	 }
		    },
		    {
	        	 xtype : 'container',
	        	 layout : 'absolute',
	        	 items : 
	        	 {
	        		 xtype: 'button',
	        		 id: 'reset-buttonID',
	        		 align : 'middle',
	        		 x : 190,
	        		 y : 30,
	        		 margin : 150,
	        		 centered : true,
	        		 width : 70,
	        		 left : 300,
	        		 type: 'reset',
	        		 text: 'Reset',
	        		 handler: function(btn, eventObj) 
	        		 {
						btn.findParentByType('form').getForm().reset();
					 }
	        	 }
		       }]
			});
return my_form;
 };
