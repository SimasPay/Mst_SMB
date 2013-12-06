window.CheckBalanceMenu = Backbone.View.extend({
	initialize : function() {
		this.template = _.template(tpl.get('basepage'));
		$(this.el).html(this.template());
		
		$('#content', this.el).append(addMenuItem({id : "Emoney",text : "Emoney"})+
									  addMenuItem({id : "bank",text : "Bank"}));
		$('#logout', this.el).remove();
		 this.events=getEvents({
			 'click #logout' : 'logout',
				'click #home' : 'home',
				'click #back' : 'back',
				'click #bank' : 'showBankBalanceForm',
				'click #Emoney' : 'showEmoneyBalanceForm'
         },{
        	 'touchstart #logout' : 'logout',
     		'touchstart #home' : 'home',
     		'touchstart #back' : 'back',
     		'touchstart #bank' : 'showBankBalanceForm',
     		'touchstart #Emoney' : 'showEmoneyBalanceForm'
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
		var accMenu = new AccountMenu();
		app.changePage(accMenu);
	},

	showBankBalanceForm : function() {
		var bankbalanceInquiryForm = new CheckBalanceForm({service : SERVICE_BANK,sourcePocketCode:POCKET_CODE_BANK});
		app.changePage(bankbalanceInquiryForm);
	},

	showEmoneyBalanceForm : function() {
		var emoneybalanceInquiryForm = new CheckBalanceForm({service : SERVICE_WALLET,sourcePocketCode:POCKET_CODE_SVA});
		app.changePage(emoneybalanceInquiryForm);
	}

});

window.CheckBalanceForm = Backbone.View.extend({
	initialize : function(config) {
		this.template = _.template(tpl.get('basepage'));
		$(this.el).html(this.template());
		
		$('#baseForm', this.el).append(addHiddenField({name : PARAMETER_TRANSACTIONNAME,value : TRANSACTION_CHECKBALANCE})+
									   addHiddenField({name : PARAMETER_SERVICE_NAME,value : config.service})+
		                               addHiddenField({name : PARAMETER_SRC_POCKET_CODE,value : config.sourcePocketCode})+
		                               addHiddenField({name : PARAMETER_SOURCE_MDN,value : app.user.get(PARAMETER_SOURCE_MDN)})+
		                               addField({label : "PIN",name : PARAMETER_SOURCE_PIN,type : "password",required : true,validations:true})+
		                               addButton({id : "Submit",value : "Get Balance",type : "submit",inline : true}));
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
		var menu = new CheckBalanceMenu();
		app.changePage(menu);
	},

	submit : function() {
		if($('#baseForm').valid()){
			var self = this;
			var data = $('#baseForm').serialize();
			sendRequest(data, self);
		}
		return false;
	},

	onSuccess : function(response) {
		var responseView = new ResponseView(response);
        app.changePage(responseView);
	}

});