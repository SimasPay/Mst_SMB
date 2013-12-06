/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.page");


mFino.page.report = function(config){
	    var detailsForm = new mFino.widget.ReportDetails(Ext.apply({height : 572
        },config));
	    
	    var search = new mFino.widget.ReportSearchForm(config);
	    
	    var reportName ="" ;
	    search.on("getReport", function(values){
	    	var msg= new CmFinoFIX.message.JSReport();
	    	msg.m_pReportName = values.ReportName;
	    	msg.m_pReportStartDate = values.ReportStartDate;
	    	msg.m_pReportEndDate = values.ReportEndDate;
            var params = {
                    success : function(response){
                    	if(response.m_psuccess){
                        	detailsForm.setItems(response,values.ReportName);
                        	detailsForm.doLayout();
                        	reportName = response.m_pReportName;
                        	if(Ext.getCmp('downloadexcel')){
                        	Ext.getCmp('downloadexcel').show();
                        	}
                        	if(Ext.getCmp('downloadpdf')){
                        	Ext.getCmp('downloadpdf').show();
                        	}
                        }else{
                        	reportName = "";
                        	if(Ext.getCmp('downloadexcel')){
                            	Ext.getCmp('downloadexcel').hide();
                            	}
                            	if(Ext.getCmp('downloadpdf')){
                            	Ext.getCmp('downloadpdf').hide();
                            	}
                        	detailsForm.clearItems();
                            Ext.Msg.show({
                                title: 'Info',
                                minProgressWidth:600,
                                msg: "Your request for report is still processing please try again after sometime to get Details",
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                        }
                    },
                    failure : function(response){
					   if(response.m_psuccess){
                        	detailsForm.setItems(response,values.ReportName);
                        	detailsForm.doLayout();
                        	reportName = response.m_pReportName;
                        	if(Ext.getCmp('downloadexcel')){
                        	Ext.getCmp('downloadexcel').show();
                        	}
                        	if(Ext.getCmp('downloadpdf')){
                        	Ext.getCmp('downloadpdf').show();
                        	}
                        }else{
                        	reportName = "";
                        	if(Ext.getCmp('downloadexcel')){
                            	Ext.getCmp('downloadexcel').hide();
                            	}
                            	if(Ext.getCmp('downloadpdf')){
                            	Ext.getCmp('downloadpdf').hide();
                            	}
                        	detailsForm.clearItems();
                            Ext.Msg.show({
                                title: 'Info',
                                minProgressWidth:600,
                                msg: "Your request for report is still processing please try again after sometime to get Details",
                                buttons: Ext.MessageBox.OK,
                                multiline: false
                            });
                        }
                    }
                };
             mFino.util.fix.send(msg, params);
        });  
	    
	    
	    
	    
	    var panel = new Ext.Panel({
	         broder: false,
	        width : 1020,
	        items: [search,
	                {
//	        	height : 200,
	            anchor : "100%",
	            autoScroll : true,
	            tbar : [
	            '<b class= x-form-tbar>' + _('Report Details') + '</b>',
	            '->',
	            {
	            	 iconCls: 'mfino-button-excel',
	                 tooltip : _('Download Excel Sheet'),
	                 id : 'downloadexcel',
	                 itemId: 'downloadexcel',
	                 handler : function(){
	                	 if(reportName!=""){
	                	 queryString = "dType=report";
	                	 queryString += "&reportName="+reportName;
	                	 queryString += "&format="+".xls";
	                	 var URL = "reportdownload.htm?" + queryString;
	                     window.open(URL,'mywindow','width=400,height=200');
	                	 }else{
	                		 Ext.ux.Toast.msg(_(""),"Get Report Deatails first ");
	                	 }
	                }
	            },
	            {
	            	 iconCls: 'mfino-button-pdf',
	                 tooltip : _('Download PDF'),
	                 id : 'downloadpdf',
	                 itemId:'downloadpdf',
	                 handler : function(){
	                	 if(reportName!=""){
	                	 queryString = "dType=report";
	                	 queryString += "&reportName="+reportName;
	                	 queryString += "&format="+".pdf";
	                	 var URL = "reportdownload.htm?" + queryString;
	                     window.open(URL,'mywindow','width=400,height=200');
	                	 }else{
	                		 Ext.ux.Toast.msg(_(""),"Get Report Deatails first ");
	                	 }
	                }
	            }
	            ],
	            items: [ detailsForm ]
	                }]
	    });
	    
	    var mainItem = panel.items.get(1);
	    mainItem.on('render', function(){
	        var tb = mainItem.getTopToolbar();
	        var itemIDs = [];
	        for(var i = 0; i < tb.items.length; i++){
	            itemIDs.push(tb.items.get(i).itemId);
	        }
	        for(i = 0; i < itemIDs.length; i++){
	            var itemID = itemIDs[i];
	            if(!mFino.auth.isEnabledItem(itemID)){
	                var item = tb.getComponent(itemID);
	                tb.remove(item);
	            }
	        }
	    });

	    
	    return panel;
};

