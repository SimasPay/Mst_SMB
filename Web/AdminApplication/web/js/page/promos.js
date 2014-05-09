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
				height : '50%',
				frame : true,
				items : [
				         {
				        	 xtype : 'fileuploadfield',
				        	 allowBlank : false,
				        	 emptyText : 'Select a file for uploading..',
				        	 labelWidth : 50,
				        	 msgTarget : 'side',
				        	 anchor : '60%',
				        	 fieldLabel : 'Promo Image File path',
				        	 buttonText : 'Browse',
				        	 name : 'PromoImage',
				        	 id : 'filepath',
				        	 itemId : 'upload.filePath',
				        	 disabled : true
				         },

				         {
				        	 xtype : 'button',
				        	 text : _('submit'),
				        	 anchor : '60%',
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
				        								 var msg = 'Success, Uploaded '
				        									 + action.result.file
				        									 + ' the file to the server ';
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
				        							 },
				        						 });
				        				 return;
				        			 }
				        		 }

				        	 }
				         }, {
				        	 xtype : 'button',
				        	 text : _('Reset'),
				        	 anchor : '60%',
				        	 handler : function() {
				        		 this.findParentByType('form').getForm().reset()
				        	 }
				         } ]
			}

	);

	return my_form;
};
