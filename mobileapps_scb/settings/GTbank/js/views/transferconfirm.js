window.TransferConfirmationForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        var txnName = config.confirmTxnName?config.confirmTxnName:TRANSACTION_TRANSFER;
         var confirm="<b>";
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_SERVICE_NAME,value:app.request.get(PARAMETER_SERVICE_NAME)})+
        							   addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.request.get(PARAMETER_SOURCE_MDN)})+
        							   addHiddenField({name:PARAMETER_SRC_POCKET_CODE,value:app.request.get(PARAMETER_SRC_POCKET_CODE)})+
        							   addHiddenField({name:PARAMETER_DEST_POCKET_CODE,value:app.request.get(PARAMETER_DEST_POCKET_CODE)})+
        							   addHiddenField({name:PARAMETER_TRANSFER_ID,value:app.response.get(PARAMETER_TRANSFER_ID)})+
        							   addHiddenField({name:PARAMETER_PARENTTXN_ID,value:app.response.get(PARAMETER_PARENTTXN_ID)})+
        							   addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:txnName}));	
        var transfer = txnName==TRANSACTION_CASHIN?"cashin":"transfer";
         
        if(app.request.has(PARAMETER_DEST_MDN)){ 
        	$('#baseForm', this.el).append(addHiddenField({name:PARAMETER_DEST_MDN,value:app.request.get(PARAMETER_DEST_MDN)}));
        	
        }
        if(app.request.get(PARAMETER_DESTINATION_BANK_ACCOUNT_NO)){
        	$('#baseForm', this.el).append(addHiddenField({name:PARAMETER_DESTINATION_BANK_ACCOUNT_NO,value:app.request.get(PARAMETER_DESTINATION_BANK_ACCOUNT_NO)}));
        	
        }
        
       	var transfer = txnName==TRANSACTION_CASHIN?"cashin":"transfer";
        if(config.isSelfTransfer)
        	{
        	confirm =confirm+"<center> <b>Transfer N\n</b>"+app.response.get(PARAMETER_CREDIT_AMT)+"<b>&nbsp;from\n</b>"; 
        	if(config.srcPocketCode==POCKET_CODE_SVA)
        		{
        		  confirm=confirm+"<b>mKuza to Bank</b>";
        		}
        	else
        		{
        		confirm=confirm+"<b>Bank to mKuza</b>";
        		}
        	}
        else
        	{
        	confirm =confirm+"<center> <b>Transfer N\n</b>"+app.response.get(PARAMETER_CREDIT_AMT); 
        	   if(app.request.get(PARAMETER_DEST_POCKET_CODE)==POCKET_CODE_SVA)
        		   {
        		   confirm=confirm+"<b> to &nbsp;"+app.request.get(PARAMETER_DEST_MDN)+"&nbsp;From mKuza";
        		   }
        	   else
        		   {
        		   confirm=confirm+"<b>to GT Bank A/C #&nbsp;"+app.request.get(PARAMETER_DESTINATION_BANK_ACCOUNT_NO)+"&nbsp; From mKuza";
        		   }
        	}
        	
        
        
        var debtamt=parseInt(app.response.get(PARAMETER_CREDIT_AMT))+ parseInt(app.response.get(PARAMETER_CHARGES));
        
        confirm =confirm+"<br><center><b> Fees:\n </b>"+app.response.get(PARAMETER_CHARGES)+
        			"<br><b> Amount to be debited:\n </b>"+debtamt+"</center>";
        $('#baseForm', this.el).append(confirm);       
                 
        $('#baseForm', this.el).append(addButton({id:"confirm",value:"Confirm",type:"submit",inline:true})+
        							   addButton({id:"cancel",value:"Cancel",inline:true}));
        
         
        $('#back', this.el).remove(); 
        this.events=getEvents({
        	 'click #logout':   'logout',
             'click #home':   'home',
             'click #cancel':   'cancel',
             'submit form':   'submit'
        },{
        	 'touchstart #logout':   'logout',
             'touchstart #home':   'home',
             'touchstart #cancel':   'cancel',
             'submit form':   'submit'
        }); 
       },

    render:function (eventName) {    	
      return this;
    },
    
   
    logout:function (e) {
      app.logout();    	  
    },
    
    home:function(e){
  	  app.menu();  
    },
    
    cancel:function(e){
    	 var self = this;
         var data = $('#baseForm').serialize();
         data = data+"&confirmed="+CONSTANT_VALUE_FALSE;
         sendRequest(data,self); 
    },
    
    submit:function(){
        var self = this;
         var data = $('#baseForm').serialize();
         data = data+"&confirmed="+CONSTANT_VALUE_TRUE;
         sendRequest(data,self);
         return false;  
      },
      
       onSuccess:function(response){       
    	         var error = new ResponseView(response);
      	         app.changePage(error);
       }    
    
});