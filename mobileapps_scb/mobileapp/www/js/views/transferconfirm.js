window.TransferConfirmationForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        var txnName = config.confirmTxnName?config.confirmTxnName:TRANSACTION_TRANSFER;
               
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_SERVICE_NAME,value:app.request.get(PARAMETER_SERVICE_NAME)})+
        						addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.request.get(PARAMETER_SOURCE_MDN)})+
        						addHiddenField({name:PARAMETER_SRC_POCKET_CODE,value:app.request.get(PARAMETER_SRC_POCKET_CODE)})+
        						addHiddenField({name:PARAMETER_DEST_POCKET_CODE,value:app.request.get(PARAMETER_DEST_POCKET_CODE)})+
        						addHiddenField({name:PARAMETER_TRANSFER_ID,value:app.response.get(PARAMETER_TRANSFER_ID)})+
        						addHiddenField({name:PARAMETER_PARENTTXN_ID,value:app.response.get(PARAMETER_PARENTTXN_ID)})+
        						addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:txnName}));	
        if(config.isAgent){
    		$('#baseForm', this.el).append(addHiddenField({name:PARAMETER_AGENT_CODE,value:app.request.get(PARAMETER_AGENT_CODE)}));
    	}
        var transfer = txnName==TRANSACTION_CASHIN?"cashin":"transfer";
        var confirm ="<center><b> Your requested to "+transfer+"<br>"; 
        if(app.request.has(PARAMETER_DEST_MDN)){ 
        	$('#baseForm', this.el).append(addHiddenField({name:PARAMETER_DEST_MDN,value:app.request.get(PARAMETER_DEST_MDN)}));
        	confirm=confirm+(config.isSelfTransfer?"":"<br><b>Receiver phone number\n</b>"+app.request.get(PARAMETER_DEST_MDN));
        }
        if(app.request.get(PARAMETER_DESTINATION_BANK_ACCOUNT_NO)){
        	$('#baseForm', this.el).append(addHiddenField({name:PARAMETER_DESTINATION_BANK_ACCOUNT_NO,value:app.request.get(PARAMETER_DESTINATION_BANK_ACCOUNT_NO)}));
        	confirm=confirm+(config.isSelfTransfer?"":"<br><b>Receiver account number\n </b>"+app.request.get(PARAMETER_DESTINATION_BANK_ACCOUNT_NO));
        }
        if(app.request.get(PARAMETER_AGENT_CODE)){
        	confirm=confirm+(config.isSelfTransfer?"":"<br><b>Receiver agent code\n </b>"+app.request.get(PARAMETER_AGENT_CODE));
    	}
        
        confirm =confirm+"<br><b> Amount\n </b>"+app.response.get(PARAMETER_CREDIT_AMT)+
        			"<br><b> Charge\n </b>"+app.response.get(PARAMETER_CHARGES)+"</center>";

        $('#baseForm', this.el).append(confirm);       
                 
        $('#baseForm', this.el).append(addButton({id:"confirm",value:"Confirm",type:"submit",inline:true})+
        							   addButton({id:"cancel",value:"Cancel",inline:true}));
        
        $('#logout', this.el).remove();
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