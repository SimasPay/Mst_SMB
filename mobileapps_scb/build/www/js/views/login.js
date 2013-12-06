
window.LoginPage = Backbone.View.extend({ 
  
      initialize:function () {
          this.template = _.template(tpl.get('basepage'));
          $(this.el).html(this.template());
          $('#baseForm', this.el).append(addField({label:"MobileNumber",name:PARAMETER_SOURCE_MDN,type:"text",required:true,validations:true})+
        		  						 addField({label:"PIN",name:PARAMETER_AUTHENTICATION_STRING,type:"password",required:true,validations:true})+
        		  						 addHiddenField({name:PARAMETER_TRANSACTIONNAME,type:"hidden",value:TRANSACTION_LOGIN})+
        		  						 addHiddenField({name:PARAMETER_SERVICE_NAME,type:"hidden",value:SERVICE_ACCOUNT})+
        		  						 addButton({value:"Login",type:"submit",inline:true}));  
          $('#home', this.el).remove();
          $('#logout', this.el).remove();
          this.events=getEvents({
        	  'click #back' : 'cancel',
              'submit form':   'submit'
          },{
              'touchstart #back':   'cancel',
              'submit form':   'submit'
          });  
         },
  
      render:function (eventName) {
        return this;
      },
      
      
      cancel:function(){
        app.main();
      },
      

	 submit : function() {
		 if($('#baseForm').valid()){
			var self = this;
			var data = $('#baseForm').serialize();
			sendRequest(data, self);
		 }		
		return false;
	 },
      
       onSuccess:function(response){       
    	   if(response.data.code==CODE_LOGIN_SUCCESS){
    	         app.user.set(response.data);
    	         app.user.set(PARAMETER_SOURCE_MDN,app.request.get(PARAMETER_SOURCE_MDN));
    	          app.user.set(PARAMETER_AUTHENTICATION_STRING,app.request.get(PARAMETER_AUTHENTICATION_STRING));
    	          if(response.data.url!="-1"){
    	        	  var update = new UpdateApp(response.data.url);
    	        	  app.changePage(update);
    	          }else{
    	        	  app.menu();
    	          }
    	        }else{
    	         alert(response.data.message);
    	         app.user.clear();
    	      }
       }    
  });

window.UpdateApp = Backbone.View.extend({ 
	  
    initialize:function (appurl) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#baseForm', this.el).append("<b>A new updated version available <br>"+
        								"<br><a href='"+appurl+"' data-role='button' data-mini='true'>Update</a>"+
        							addButton({value:"Cancel",id:'Cancel',type:"button",inline:true}));  
        $('#home', this.el).remove();
        $('#logout', this.el).remove();
        this.events=getEvents({
      	  'click #Cancel' : 'cancel'
        },{
            'touchstart #Cancel':   'cancel'
        });  
       },

    render:function (eventName) {
      return this;
    },
    cancel:function(){
      app.menu();
    }
});