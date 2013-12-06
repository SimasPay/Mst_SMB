window.AgentCashOutInquiryForm = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        
        $(this.el).html(this.template());
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_CASHOUT_UNREGISTERED_INQUIRY})+
                                       addHiddenField({name:PARAMETER_SERVICE_NAME,value:SERVICE_AGENT})+
                                       addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.user.get(PARAMETER_SOURCE_MDN)})+
                                       addField({label:"MDN",name:PARAMETER_DEST_MDN,type:"text",required:true,validations:true})+
                                       addField({label:"TransferId",name:PARAMETER_TRANSFER_ID,type:"text",required:true,validations:true})+
                                       addField({label:"Fund Access Code",name:PARAMETER_SECRETE_CODE,type:"text",required:true,validations:true})+
                                       addField({label:"Enter PIN",name:PARAMETER_SOURCE_PIN,type:"password",required:true,validations:true})+
                                       addButton({id:"submit",value:"CashOut",type:"submit",inline:true}));
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
 	   if(response.data.code==CODE_CASHOUT_UNREGISTERED_INQUIRY_SUCCESS){
 	          app.response.set(response.data);
 	          var agentcashoutConfirmation = new AgentCashOutconfirmationForm();
 	          app.changePage(agentcashoutConfirmation);
 	        }else{
 	       var error = new ResponseView(response);
   	         app.changePage(error);
 	      }
    }    
    
   
});
window.AgentCashOutconfirmationForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        

        $('#baseForm', this.el).append("<b> Kindly Confirm: Mobile  no  </b>"+app.request.get(PARAMETER_DEST_MDN)+"<br><b> Amount: </b>"+app.response.get(PARAMETER_CREDIT_AMT)+
                                      "<br><b>FAC:</b>"+app.request.get(PARAMETER_SECRETE_CODE)+
                                       addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_CASHOUT_UNREGISTERED})+
                                       addHiddenField({name:PARAMETER_SERVICE_NAME,value:app.request.get(PARAMETER_SERVICE_NAME)})+
                                       addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.request.get(PARAMETER_SOURCE_MDN)})+
                                       addHiddenField({name:PARAMETER_DEST_MDN,value:app.request.get(PARAMETER_DEST_MDN)})+
                                       addHiddenField({name:PARAMETER_TRANSFER_ID,value:app.response.get(PARAMETER_TRANSFER_ID)})+
                                       addHiddenField({name:PARAMETER_PARENTTXN_ID,value:app.response.get(PARAMETER_PARENTTXN_ID)})+
                                       addButton({id:"cancel",value:"Cancel",inline:true})+
                                       addButton({id:"confirm",value:"confirm",type:"submit",inline:true}));
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
