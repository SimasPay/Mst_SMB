window.CashOutMenu = Backbone.View.extend({ 
	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"Frommkuza",text:"From mKuza"})+
                                      addMenuItem({id:"Frombank",text:"From Bank"}));
        $('#home', this.el).remove();
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #back':   'back',
            'click #Frommkuza':   'showmkuzamenu',
            'click #Frombank'  : 'showbankMenu'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #back':   'back',
            'touchstart #Frommkuza':   'showmkuzamenu',
            'touchstart #Frombank'  : 'showbankMenu'
        });  
     },

    render:function (eventName) {    	
      return this;
    },
    
    
    logout:function (e) {
      app.logout();    	  
    },
    back:function(e){
        var menu=new AccountMenu();
        app.changePage(menu);
    },
    
    showmkuzamenu:function(e){
  	  var mkuzaMenu = new cashoutmkuzaMenu();
        app.changePage(mkuzaMenu);   
    },
    showbankMenu:function(e){
    	  var bankMenu = new cashoutbankMenu();
          app.changePage(bankMenu);   
      }
     
});
window.cashoutmkuzaMenu = Backbone.View.extend({ 
	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"agent",text:"From agent"})+
        							  addMenuItem({id:"atm",text:"From ATM"}));
        $('#home', this.el).remove();
        this.events=getEvents({
        	 'click #logout':   'logout',
             'click #agent':   'showAgentForm',
             'click #atm'  : 'showATMform',
              'click #back'  : 'back'
             
        },{
        	 'touchstart #logout':   'logout',
             'touchstart #agent':   'showAgentForm',
             'touchstart #atm'  : 'showATMform',
              'touchstart #back'  : 'back'
             
        }); 
        
       },

    render:function (eventName) {    	
      return this;
    },
    
    
    logout:function (e) {
      app.logout();    	  
    },
    back:function(e){
        var menu=new CashOutMenu();
        app.changePage(menu);
    },
    
    showAgentForm:function(e){
  	  var AgentForm = new CashOutInquiryForm({fromAgent:true,isATM:false});
        app.changePage(AgentForm);   
    },
    showATMform:function(e){
    	  var ATMform = new CashOutInquiryForm({fromAgent:false,isATM:true});
          app.changePage(ATMform);   
      }
     
});


window.CashOutInquiryForm = Backbone.View.extend({
	initialize : function(config) {
		this.template = _.template(tpl.get('basepage'));
		$(this.el).html(this.template());
		if(config.isATM)
		{
			$('#baseForm', this.el).append(addHiddenField({name : PARAMETER_TRANSACTIONNAME,value : TRANSACTION_ATM_CASHOUT})+
										   addHiddenField({name : PARAMETER_SERVICE_NAME,value : SERVICE_WALLET}));
		}
		else
		{
	    $('#baseForm', this.el).append(addHiddenField({name : PARAMETER_TRANSACTIONNAME,value : TRANSACTION_CASHOUT_INQUIRY})+
	    							   addHiddenField({name : PARAMETER_SERVICE_NAME,value : SERVICE_WALLET}));
		}
		$('#baseForm', this.el).append(addHiddenField({name : PARAMETER_SRC_POCKET_CODE,value : config.sourcePocketCode})+
									   addHiddenField({name : PARAMETER_SOURCE_MDN,value : app.user.get(PARAMETER_SOURCE_MDN)}));
		if(config.fromAgent)
	    {
		$('#baseForm', this.el).append(addField({label : "Agent Code",name : PARAMETER_AGENT_CODE,type : "text",required : true,validations:true}));
	    }
		$('#baseForm', this.el).append(addField({label : "Amount",name : PARAMETER_AMOUNT,type : "text",required : true,validations:true})+
									   addField({label : "PIN",name : PARAMETER_SOURCE_PIN,type : "password",required : true,validations:true})+
									   addButton({id : "Submit",value : "CashOut",type : "submit",inline : true}));
		
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
		var menu = new cashoutmkuzaMenu();
		app.changePage(menu);
	},

	submit : function() {
		if( $('#baseForm').valid()){
			var self = this;
			var data = $('#baseForm').serialize();
			sendRequest(data, self);
		}
		return false;
	},

	onSuccess : function(response) {
		if (response.data.code == CODE_TRANSFERINQUIRY_SUCCESS) {
			app.response.set(response.data);
			var cashoutConfirmation = new CashOutConfirmationForm();
			app.changePage(cashoutConfirmation);
		} else {
			var responseView = new ResponseView(response);
	        app.changePage(responseView);
		}
	}

});

window.CashOutConfirmationForm = Backbone.View.extend({
	initialize : function() {
		this.template = _.template(tpl.get('basepage'));
		$(this.el).html(this.template());
		
		$('#baseForm', this.el).append(addHiddenField({name : PARAMETER_TRANSACTIONNAME,value : TRANSACTION_CASHOUT})+
									   addHiddenField({name : PARAMETER_SERVICE_NAME,value : app.request.get(PARAMETER_SERVICE_NAME)})+
									   addHiddenField({name : PARAMETER_SOURCE_MDN,value : app.user.get(PARAMETER_SOURCE_MDN)})+
									   addHiddenField({name:PARAMETER_TRANSFER_ID,value:app.response.get(PARAMETER_TRANSFER_ID)})+
									   addHiddenField({name:PARAMETER_PARENTTXN_ID,value:app.response.get(PARAMETER_PARENTTXN_ID)})+
									   addHiddenField({name : PARAMETER_AGENT_CODE,value : app.request.get(PARAMETER_AGENT_CODE)}));
	    
	    var confirm ="<center><br><b> cash out of"+app.response.get(PARAMETER_CREDIT_AMT)+" at agent" +app.request.get(PARAMETER_AGENT_CODE)+
	    				"</center>";

		$('#baseForm', this.el).append(confirm);       
	    $('#baseForm', this.el).append(addButton({id:"confirm",value:"Confirm",type:"submit",inline:true})+
	    							   addButton({id:"cancel",value:"Cancel",inline:true}));
		$('#logout', this.el).remove(); 
		this.events=getEvents({
			'click #logout' : 'logout',
			'click #home' : 'home',
			'click #cancel' : 'cancel',
			'submit form' : 'submit'
        },{
        	'touchstart #logout' : 'logout',
    		'touchstart #home' : 'home',
    		'touchstart #cancel' : 'cancel',
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

	cancel:function(e){
   	 var self = this;
        var data = $('#baseForm').serialize();
        data = data+"&confirmed="+CONSTANT_VALUE_FALSE;
        sendRequest(data,self); 
   },

	submit : function() {
		var self = this;
		var data = $('#baseForm').serialize();
		data = data + "&confirmed="+CONSTANT_VALUE_TRUE;
		sendRequest(data, self);
		return false;
	},

	onSuccess : function(response) {
		var responseView = new ResponseView(response);
        app.changePage(responseView);
	}

});
