window.ChangePinForm = Backbone.View.extend({
	initialize : function() {
		this.template = _.template(tpl.get('basepage'));
		$(this.el).html(this.template());
		
		 $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_SERVICE_NAME,value:SERVICE_ACCOUNT})+
				 						addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_CHANGEPIN})+
				 						addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.user.get(PARAMETER_SOURCE_MDN)})+
				 						addField({label : "Old PIN",name : PARAMETER_SOURCE_PIN,type : "password",required : true,validations:true})+
				 						addField({label : "New PIN",name : PARAMETER_NEW_PIN,type : "password",required : true,validations:true})+
				 						addField({label : "Confirm PIN",name : PARAMETER_CONFIRM_PIN,type : "password",required : true,validations:true})+
				 						addButton({id : "submit",value : "Change",type : "submit",inline : true}));
		$('#logout', this.el).remove(); 
		 this.events=getEvents({
			 'click #logout' : 'logout',
				'click #home' : 'home',
				'click #back' : 'back',
				'submit form' : 'submit'
         },{
        	 'touchstart #logout' : 'logout',
     		'touchstart #home' : 'home',
     		'touchstart #back' : 'back',
     		'submit form' : 'submit'
         }); 
	},

	render : function(eventName) {
		return this;
	},

	
	logout : function(e) {
		app.logout();
	},

	home : function(e) {
		app.menu();
	},

	back : function(e) {
		var menu = new AccountMenu();
		app.changePage(menu);
	},

	submit : function() {
		if ($('#baseForm').valid()) {
			var answer = confirm("Are you sure you want to change your PIN ?");
				if (answer){
					var self = this;
					var data = $('#baseForm').serialize();
					sendRequest(data, self);
				}
				else{
					this.back();
				}			
		}
		return false;
	},

	onSuccess : function(response) {
		var responseView = new ResponseView(response);
	         app.changePage(responseView);
	}

});