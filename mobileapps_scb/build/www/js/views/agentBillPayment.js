window.AgentBillPaymentInquiryForm = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        
        $(this.el).html(this.template());
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_AGENT_BILL_PAY_INQUIRY})+
                                       addHiddenField({name:PARAMETER_SERVICE_NAME,value:SERVICE_AGENT})+
                                       addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.user.get(PARAMETER_SOURCE_MDN)})+
                                       addHiddenField({name:PARAMETER_SRC_POCKET_CODE,value:POCKET_CODE_SVA})+
                                       addField({label:"EnterBillerCode",name:PARAMETER_BILLER_CODE,type:"text",required:true,validations:true})+
                                       addField({label:"BillNo",name:PARAMETER_BILL_NO,type:"text",required:true,validations:true})+
                                       addField({label:"Amount",name:PARAMETER_AMOUNT,type:"text",required:true,validations:true})+
                                       addField({label:"Destination Mobile",name:PARAMETER_DEST_MDN,type:"text",required:true,validations:true})+
                                       addField({label:"PIN",name:PARAMETER_SOURCE_PIN,type:"password",required:true,validations:true})+
                                       addButton({id:"submit",value:"Pay",type:"submit",inline:true}));
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
    	var menu=new ServiceTxnMenu();
    	app.changePage(menu);
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
 	   if(response.data.code==CODE_TRANSFERINQUIRY_SUCCESS){
 	          app.response.set(response.data);
 	          var billpaymentConfirmation = new AgentBillPaymentConfirmationForm();
 	          app.changePage(billpaymentConfirmation );
 	        }else{
 	       var error = new ResponseView(response);
   	         app.changePage(error);
 	      }
    }    
    
   
});


window.AgentBillPaymentConfirmationForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        

        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_AGENT_BILL_PAY})+
                                       addHiddenField({name:PARAMETER_SERVICE_NAME,value:app.request.get(CONSTANTS.PARAMETER_SERVICE_NAME)})+
                                       addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.request.get(PARAMETER_SOURCE_MDN)})+
                                       addHiddenField({name:PARAMETER_DEST_MDN,value:app.request.get(PARAMETER_DEST_MDN)})+
                                       addHiddenField({name:PARAMETER_TRANSFER_ID,value:app.response.get(PARAMETER_TRANSFER_ID)})+
                                       addHiddenField({name:PARAMETER_PARENTTXN_ID,value:app.response.get(PARAMETER_PARENTTXN_ID)})+
                                       addHiddenField({name:PARAMETER_BILLER_CODE,value:app.request.get(PARAMETER_BILLER_CODE)})+
                                       addHiddenField({name:PARAMETER_BILL_NO,value:app.request.get(PARAMETER_BILL_NO)})+
                                       "<center><b> Do u want to pay bill for \n </b>"+
        		                       "<b>Mobile number\n</b>"+	app.request.get(PARAMETER_DEST_MDN)+
				                       "<b>Biller code\n</b>"+	app.request.get(PARAMETER_BILLER_CODE)+
				                       "<br><b>Bill number\n</b>"+	app.request.get(PARAMETER_BILL_NO)+
				                       "<br><b> Amount: </b>"+app.request.get(PARAMETER_AMOUNT)+
				                       "<br><b>Charge\n</b>"+	app.response.get(PARAMETER_CHARGES)+"<center>"+
                                       addButton({id:"cancel",value:"Cancel",inline:true})+
                                       addButton({id:"confirm",value:"comfirm",type:"submit",inline:true}));
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

