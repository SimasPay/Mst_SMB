window.custRegistrationForm = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        
        $(this.el).html(this.template());
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_SUBSCRIBERREGISTRATION})+
        						addHiddenField({name:PARAMETER_SERVICE_NAME,value:SERVICE_AGENT})+
        						addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.user.get(PARAMETER_SOURCE_MDN)})+
        						addHiddenField({name:PARAMETER_ACCOUNT_TYPE,value:1})+
        						addField({label:"Enter Mobile Number",name:PARAMETER_SUB_MDN,type:"text",required:true,validations:true})+
        						addField({label:"First Name",name:PARAMETER_SUB_FIRSTNAME,type:"text",required:true,validations:true})+
        						addField({label:"Last Name",name:PARAMETER_SUB_LASTNAME,type:"text",required:true,validations:true})+
        						addField({label:"dob",name:PARAMETER_DOB,type:"text",required:true,validations:true})+
        						addField({label:"Application No:",name:PARAMETER_APPLICATION_ID,type:"text",required:true,validations:true})+
        						addField({label:"PIN",name:PARAMETER_SOURCE_PIN,type:"password",required:true,validations:true})+
        						addButton({id:"submit",value:"submit",type:"submit",inline:true}));
        $('#logout', this.el).remove();
        this.events=getEvents({
     	   'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'home',
            'submit form':   'submit'
        },{
     	   'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'home',
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
    	if($('#baseForm').valid()){
    		
	    		var self = this;
	    		var data = $('#baseForm').serialize();
		        var dataarray = data.split("dob=");
	    		var data = dataarray[0]+"dob="+$('#'+PARAMETER_DOB).val().replace('/','').replace('/','')+dataarray[1].substr(dataarray[1].indexOf('&'),dataarray[1].length);
	    		sendRequest(data,self);
    		
    	}
         return false;  
      },
      
    onSuccess:function(response){       
 	       	var responseView = new ResponseView(response);
   	         app.changePage(responseView);
    }    
    
   
});