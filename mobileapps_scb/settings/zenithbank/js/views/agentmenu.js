window.AgentMenu = Backbone.View.extend({ 
	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"TransferMenu",text:"Transfer"})+
        					   addMenuItem({id:"custRegistration",text:"Customer Registration"})+
        					   addMenuItem({id:"servicetxn",text:"Service Transactions"})+
        					   addMenuItem({id:"BuyMe",text:"Buy"})+
        					   addMenuItem({id:"Account",text:"Account"}));
        
        $('#back', this.el).remove();
        $('#home', this.el).remove(); 
        this.events=getEvents({
     	   'click #logout':   'logout',
            'click #TransferMenu':   'showTransferMenu',
            'click #custRegistration':   'showCustRegistrationForm',
            'click #servicetxn':   'showServiceTxnMenu',
            'click #buy':   'showBuyMenu',
            'click #Account':   'showAccountMenu',
        },{
     	   'touchstart #logout':   'logout',
            'touchstart #TransferMenu':   'showTransferMenu',
            'touchstart #custRegistration':   'showCustRegistrationForm',
            'touchstart #servicetxn':   'showServiceTxnMenu',
            'touchstart #buy':   'showBuyMenu',
            'touchstart #Account':   'showAccountMenu',
        });  
        
       },
       
    render:function (eventName) {    	
      return this;
    },
    
  logout:function (e) {
      app.logout();    	  
    },
    
    showTransferMenu:function(e){
  	  var transferMenu = new AgentTransferMenu();
        app.changePage(transferMenu);   
    },
    showCustRegistrationForm:function(e){
    	  var custregistrationForm = new custRegistrationForm();
          app.changePage(custregistrationForm);   
      },
      showServiceTxnMenu:function(e){
    	  var servicetxnMenu = new ServiceTxnMenu();
          app.changePage(servicetxnMenu);   
      },
      showBuyMenu:function(e){
    	  var agentbuyMenu = new BuyMenu({isAgent:true});
          app.changePage(agentbuyMenu);   
      },
      showAccountMenu:function(e){
    	  var agentaccountMenu = new AccountMenu({isAgent:true});
          app.changePage(agentaccountMenu);   
      }
});

