window.UnregisteredTransferForm = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
               
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_SERVICE_NAME,value:app.request.get(PARAMETER_SERVICE_NAME)})+
        							   addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:app.request.get(PARAMETER_TRANSACTIONNAME)})+
        							   addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.request.get(PARAMETER_SOURCE_MDN)})+
        							   addHiddenField({name:PARAMETER_SRC_POCKET_CODE,value:app.request.get(PARAMETER_SRC_POCKET_CODE)})+
        							   addHiddenField({name:PARAMETER_DEST_POCKET_CODE,value:app.request.get(PARAMETER_DEST_POCKET_CODE)})+
        							   addHiddenField({name:PARAMETER_DEST_MDN,value:app.request.get(PARAMETER_DEST_MDN)})+
        							   addHiddenField({name:PARAMETER_AMOUNT,value:app.request.get(PARAMETER_AMOUNT)})+
        							   addHiddenField({name:PARAMETER_SOURCE_PIN,value:app.request.get(PARAMETER_SOURCE_PIN)})+
        							   addField({label:"DestFirstName",name:PARAMETER_SUB_FIRSTNAME,type:"text",required:true,validations:true})+
        							   addField({label:"DestLastName",name:PARAMETER_SUB_LASTNAME,type:"text",required:true,validations:true})+
        							   addButton({id:"transfer",value:"Transfer",type:"submit",inline:true})+
        							   addButton({id:"cancel",value:"Cancel",inline:true}));
        $('#logout', this.el).remove();
        $('#back', this.el).remove();
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #home':   'home',
            'click #cancel':   'home',
            'submit form':   'submit'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #cancel':   'home',
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
    
    submit:function(){
        var self = this;
        if($('#baseForm').valid()){
	         var data = $('#baseForm').serialize();
	         sendRequest(data,self);
        }
         return false;  
      },
      
       onSuccess:function(response){
    	   var code=response.data.code;
    	   if(code==CODE_TRANSFERINQUIRY_UNREGISTERED_SUCCESS){
 	          app.response.set(response.data);
 	          var transferConfirmation = new TransferConfirmationForm({isSelfTransfer:false});
 	          app.changePage(transferConfirmation);
 	        }else{
	    	       var error = new ResponseView(response);
	      	       app.changePage(error);
 	      }
       }    
    
});

