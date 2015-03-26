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
	    	msg.m_pFromUpdatedTime = values.FromUpdatedTime;
	    	msg.m_pToUpdatedTime = values.ToUpdatedTime;
	    	
	    	msg.m_pSubscriberStatus = values.SubscriberStatus;
	    	//msg.m_pPocketTemplateDescription = values.PocketTemplateDescription;
	    	msg.m_pPocketTemplateID = values.PocketTemplateID;
	    	msg.m_pSubscriberRestrictions = values.SubscriberRestrictions;
	    	msg.m_pSourceMDN = values.SourceMDN;
	    	//msg.m_pTransactionType = values.TransactionType;
	    	msg.m_pTransactionTypeID = values.TransactionTypeID;
	    	msg.m_pTransactionStatus = values.TransactionStatus;
	    	msg.m_pDestinationPocketStatus = values.DestinationPocketStatus;
	    	msg.m_pDestMDN = values.DestMDN;
	    	msg.m_pPartnerCode = values.PartnerCode;
	    	msg.m_pBillerCode = values.BillerCode;
	    	msg.m_pPartnerType = values.PartnerType;
	    	msg.m_pSettlementStatus = values.SettlementStatus;
	    	msg.m_pCSRUserName = values.CSRUserName;
	    	msg.m_pIDNumber = values.IDNumber;
	    	msg.m_pMDN = values.MDN;
	    	msg.m_pMerchantID = values.MerchantID;
	    	msg.m_pMerchantAccount = values.MerchantAccount;
	    	msg.m_pReferenceNumber = values.ReferenceNumber;
	    	msg.m_pEmail = values.Email;
	    	msg.m_pBankRetrievalReferenceNumber = values.BankRetrievalReferenceNumber;
	    	msg.m_pSourcePartnerCode = values.SourcePartnerCode;
	    	msg.m_pDestPartnerCode = values.DestPartnerCode;
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

