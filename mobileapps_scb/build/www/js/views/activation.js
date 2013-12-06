window.ActivationPage = Backbone.View.extend({ 
  
      initialize:function () {
          this.template = _.template(tpl.get('basepage'));
          $(this.el).html(this.template()); 
         
          $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_SERVICE_NAME,type:"hidden",value:SERVICE_ACCOUNT})+
        		  						addField({label:"MDN",name:PARAMETER_SOURCE_MDN,type:"text",required:true,validations:true})+
        		  						addField({label:"OneTimePin",name:PARAMETER_OTP,type:"password",required:true,validations:true})+
        		  						addField({label:"NewPin",name:PARAMETER_ACTIVATION_NEWPIN,type:"password",required:true,validations:true})+
        		  						addField({label:"ConfirmPin",name:PARAMETER_ACTIVATION_CONFIRMPIN,type:"password",required:true,validations:true})+
        		  						"<br><fieldset data-role='controlgroup' data-type='horizontal' align='center'>" +
        		  							"<input type='radio' name='"+PARAMETER_TRANSACTIONNAME+"' id='subscriberOption' value='"+TRANSACTION_ACTIVATION+"' checked='checked' />" +
        		  							"<label for='subscriberOption'>Subscriber</label>" +
        		  							"<input type='radio' name='"+PARAMETER_TRANSACTIONNAME+"' id='agentOption' value='"+TRANSACTION_AGENTACTIVATION+"'  />" +
        		  							"<label for='agentOption'>Agent</label>" +
        		  							"</fieldset>"+
        		  						/*"<br><select name='"+PARAMETER_TRANSACTIONNAME+"' id='txnName' data-overlay-theme='a' data-theme='c'  data-native-menu='false'>" +
        		 								"<option value='"+TRANSACTION_ACTIVATION+"'>Subscriber</option>" +
        		 								"<option value='"+TRANSACTION_AGENTACTIVATION+"'>Agent</option></select>"+*/
        		 								addButton({id:"activate",value:"Activate",type:"submit",inline:true}));   
          $('#home', this.el).remove();
          $('#logout', this.el).remove();
          this.events=getEvents({
         	 'click #back' : 'cancel',
              'submit form':   'submit'
          },{
         	 'touchstart #back' : 'cancel',
              'touchstart form':   'submit'
          });
         },
         
         
  
      render:function (eventName) {
        return this;
      },
      
      
      cancel:function(){
        app.main();
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
    	   var responseview= new ResponseView(response);
    	      app.changePage(responseview);
       }    
  });

 
