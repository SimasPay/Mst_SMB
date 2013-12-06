window.TransferInquiryForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        this.config=config;
        var txnName = config.txnName?config.txnName:TRANSACTION_TRANSFER_INQUIRY;
        var service = config.service?config.service:config.srcPocketCode==POCKET_CODE_SVA?SERVICE_WALLET:SERVICE_BANK;
        var buttonText = txnName==TRANSACTION_CASHIN_INQUIRY?"CashIn":"Transfer";
        	
        $(this.el).html(this.template());
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:txnName})+
        							   addHiddenField({name:PARAMETER_SERVICE_NAME,value:service})+
        							   addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.user.get(PARAMETER_SOURCE_MDN)})+
        							   addHiddenField({name:PARAMETER_SRC_POCKET_CODE,value:config.srcPocketCode})+
        							   addHiddenField({name:PARAMETER_DEST_POCKET_CODE,value:config.destPocketCode}));
        
        if(config.isSelfTransfer)  {
	        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_DEST_MDN,value:app.user.get(PARAMETER_SOURCE_MDN),validations:true}));
	    } else{
	    	if(config.isAgent){
	    		$('#baseForm', this.el).append(addField({label:"AgentCode",name:PARAMETER_AGENT_CODE,type:"text",required:true,validations:true}));
	    	}else{
		    	if(config.destPocketCode==POCKET_CODE_BANK){
		    		if(!config.interbank){		    		
		    			$('#baseForm', this.el).append(addField({label:"DestBankAccount",name:PARAMETER_DESTINATION_BANK_ACCOUNT_NO,required:true,type:"text",validations:true}));
		    		}
		    	}else{
		    		$('#baseForm', this.el).append(addField({label:"DestMDN",name:PARAMETER_DEST_MDN,type:"text",required:true,validations:true}));
			    	
		    	}	        
	        }
	    }
        if(config.interbank){
        	$('#baseForm', this.el).append(addField({label:"DestBankAccount",name:PARAMETER_DEST_ACCOUNT_NO,required:true,type:"text",validations:true})+
        								   addField({label:"DestinationBankCode",name:PARAMETER_DEST_BANK_CODE,type:"text",required:true,validations:true}));
        }
        $('#baseForm', this.el).append(addField({label:"Amount",name:PARAMETER_AMOUNT,type:"text",required:true,validations:true})+
        							   addField({label:"PIN",name:PARAMETER_SOURCE_PIN,type:"password",required:true,validations:true})+
        							   addButton({id:"transfer",value:buttonText,type:"submit",inline:true}));  
        $('#logout', this.el).remove();
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'submit form':   'submit'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
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
    
    back:function(e){
    	app.changePage(this.config.back);
    },
    
    submit:function(){
        if($('#baseForm').valid()){
        	 var self = this;
	         var data = $('#baseForm').serialize();
	         sendRequest(data,self);
        }
         return false;  
      },
      
       onSuccess:function(response){   
    	   var self=this;
    	   var code=response.data.code;
    	   if(code==CODE_TRANSFERINQUIRY_SUCCESS||code==CODE_TRANSFERINQUIRY_UNREGISTERED_SUCCESS){
    	          app.response.set(response.data);
    	          var transferConfirmation = new TransferConfirmationForm(self.config);
    	          app.changePage(transferConfirmation);
    	        }else if(code==CODE_TRANSFER_UNREGISTERED_PROMPT){
    	        	var unregTransfer = new UnregisteredTransferForm();
    	        	app.changePage(unregTransfer);
    	        }else{
	    	       var error = new ResponseView(response);
	      	       app.changePage(error);
    	      }
       }    
    
});
