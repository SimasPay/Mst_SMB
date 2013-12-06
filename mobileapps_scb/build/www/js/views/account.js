window.AccountMenu = Backbone.View.extend({
	initialize : function() {
		this.template = _.template(tpl.get('basepage'));
		$(this.el).html(this.template());
		$('#content', this.el).append(addMenuItem({id : "ChangePIN",text : "Change PIN"})
				                     +addMenuItem({id : "CheckBalance",text : "Check Balance"})
				                     +addMenuItem({id : "History",text : "History"}));
		if(app.user.get("type")!=USER_AGENT){
			$('#content', this.el).append(addMenuItem({id : "CashOut",text : "Cash Out"}));
		}
		 $('#logout', this.el).remove();
		 this.events=getEvents({
				'click #logout' : 'logout',
				'click #home' : 'home',
				'click #back' : 'home',
				'click #ChangePIN' : 'showChangePinForm',
				'click #CheckBalance' : 'showCheckBalanceMenu',
				'click #History' : 'showHistoryMenu',
				'click #CashOut' : 'showCashOutMenu'
		        
		    },{
		    	'touchstart #logout' : 'logout',
		    	'touchstart #home' : 'home',
		    	'touchstart #back' : 'home',
		    	'touchstart #ChangePIN' : 'showChangePinForm',
		    	'touchstart #CheckBalance' : 'showCheckBalanceMenu',
		    	'touchstart #History' : 'showHistoryMenu',
		    	'touchstart #CashOut' : 'showCashOutMenu'
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

	showChangePinForm : function() {
		var Menu = new ChangePinForm();
		app.changePage(Menu);
	},

	showCheckBalanceMenu : function() {
		var Menu = new CheckBalanceMenu();
		app.changePage(Menu);
	},
	showHistoryMenu : function() {
		var Menu = new HistoryMenu();
		app.changePage(Menu);
	},
	showCashOutMenu : function() {
		var Menu = new CashOutInquiryForm({sourcePocketCode : POCKET_CODE_SVA});
		app.changePage(Menu);
	}

});

