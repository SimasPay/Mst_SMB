/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");

mFino.page.promos = function(config) {
	var my_form = new Ext.FormPanel(
			{
				title : 'Promos',
				method : 'POST',
				fileUpload : true,
				bodyPadding : 5,
				buttonAlign : 'center',
				// width : 500,
				//height : '50%',
				frame : true,
				labelWidth : 120,
				items : [
				         {
				        	 xtype : 'fileuploadfield',
				        	 allowBlank : false,
				        	 emptyText : 'Select a jpg/png/gif file for uploading..',
				        	 msgTarget : 'side',
				        	 anchor : '50%',
				        	 fieldLabel : 'Promo Image File path',
				        	 buttonText : 'Browse',
				        	 name : 'PromoImage',
				        	 id : 'promoImagefilepath',
				        	 itemId : 'promoimage.upload.filePath',
				        	 disabled : true
				         },
				         {
						  xtype : 'container',
						  layout : 'absolute',
						  items : 
						  { 							 
				        	 xtype : 'button',
				        	 text : _('Submit'),
							 x : 120,
							 y : 30,
							 margin : 150,
							 centered : true,
						     width : 70,
							 left : 300,							 
				        	 handler : function() {

				        		 if (this.findParentByType('form').getForm()
				        				 .isValid()) {
				        			 var title = this.findParentByType('form').title;
				        			 if (title.search('Upload File') >= 0
				        					 || title.search('Promos') >= 0) {
				        				 var urlpattern = 'uploadpromoimage.htm';
				        				 this
				        				 .findParentByType('form')
				        				 .getForm()
				        				 .submit(
				        						 {
				        							 url : urlpattern,
				        							 waitMsg : _('Uploading your file...'),
				        							 reset : false,
				        							 success : function(
				        									 fp, action) {
				        								 var msg = 'Successfully uploaded '
				        									 + action.result.file
				        									 + ' to the server ';
				        								 Ext.Msg
				        								 .show({
				        									 title : _('Info'),
				        									 minProgressWidth : 250,
				        									 msg : _(msg),
				        									 buttons : Ext.MessageBox.OK,
				        									 multiline : false
				        								 });
				        							 },
				        							 failure : function(
				        									 fp, action) {
				        								 Ext.Msg
				        								 .show({
				        									 title : _('Error'),
				        									 minProgressWidth : 250,
				        									 msg : action.result.Error,
				        									 buttons : Ext.MessageBox.OK,
				        									 multiline : false
				        								 });
				        							 }
				        						 });
				        				 return;
				        			 }
				        		 }
								}
				        	 }
				         }, 
						 {
						  xtype : 'container',
						  layout : 'absolute',
						  items : 
						  { 	
				        	 xtype : 'button',
				        	 text : _('Reset'),
							 x : 200,
					         y : 30,
	        		         margin : 150,
							 centered : true,
							 width : 70,
							 left : 300,
				        	 handler : function() {
				        		 this.findParentByType('form').getForm().reset()
				        	 }
						  }
				         } ]
			}

	);

	return my_form;
};
