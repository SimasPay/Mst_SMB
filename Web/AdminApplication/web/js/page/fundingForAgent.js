Ext.ns("mFino.page");

mFino.page.fundingForAgent = function(config){
	var detailsForm = new mFino.widget.FundingForAgentDetails(Ext.apply({
		height: 200
	},config));
	
	 var grid = new mFino.widget.FundingForAgentGrid(Ext.apply({
		 height: 500,
		 title : _('Agent CashIn Transactions')
	 },config));
	
     var approveWindow = new mFino.widget.ApproveRejectAgentCashInWindow(config);
	
	
	 var panelCenter = new Ext.Panel({
		 layout: 'anchor',
		 items:[
		 {
			 anchor : "100%",
			 height : 205,
			 autoscroll : true,
			 tbar : [
             '<b class= x-form-tbar>' + _('Agent cashIn Transaction Details') + '</b>',
             '->',
             {
                 iconCls : "mfino-button-resolve",
                 tooltip : _('Approve/Reject Agent CashIn Request'),
                 itemId: 'fundingForAgent.approve',
                 id: 'fundingForAgent.approve',
                 handler : function(){
                     if(detailsForm.record) {
                         approveWindow.show();
                         approveWindow.setRecord(detailsForm.record);
                     }
                     else {
                    	 Ext.MessageBox.alert(_("Alert"), _("No Transaction is selected"));
                     }
              	 }
             }
			 ],
			 items: [ detailsForm]
		 } 
		       
		 ]
	 });
	
	 grid.on("defaultSearch", function() {
			 grid.store.lastOptions = {
					 params : {
						 start : 0,
						 limit : CmFinoFIX.PageSize.Default
					 }
			 };
			 grid.store.load(grid.store.lastOptions);
	 });
	
	 grid.selModel.on("rowselect", function(sm, rowIndex, record){
		 detailsForm.setRecord(record);        
		 detailsForm.setStore(grid.store);

		 if ((Ext.getCmp('fundingForAgent.approve') && (mFino.auth.isEnabledItem('fundingForAgent.approve'))) &&
        		 (record.data[CmFinoFIX.message.AgentCashIn.Entries.AgentCashInTrxnStatus._name] === CmFinoFIX.AgentCashInTrxnStatus.Initialized)) {
        	 Ext.getCmp('fundingForAgent.approve').show();
         } 
         else if (Ext.getCmp('fundingForAgent.approve')) {
        	 Ext.getCmp('fundingForAgent.approve').hide();
         }
	 });
	
	 var panel = new Ext.Panel({
		 broder: false,
		 width : 1020,
		 items: [ panelCenter, grid]
	 });
	 
	 return panel;
	
}