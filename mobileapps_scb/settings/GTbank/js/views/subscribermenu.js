window.SubscriberMenu = Backbone.View.extend({ 
	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"TransferMenu",text:"Send Money"})+
        							  addMenuItem({id:"Airtime",text:"Airtime"})+
        							  addMenuItem({id:"MyAccount",text:"My Account"}));
        $('#back', this.el).remove();
        $('#home', this.el).remove();
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #TransferMenu':   'showTransferMenu',
            'click #Shop':   'showshopMenu',
            'click #Airtime'  : 'showAirtimeMenu',
            'click #payBill'  : 'showPayBillMenu',
            'click #MyAccount'  : 'showAccountMenu'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #TransferMenu':   'showTransferMenu',
            'touchstart #Shop':   'showshopMenu',
            'touchstart #Airtime'  : 'showAirtimeMenu',
            'touchstart #payBill'  : 'showPayBillMenu',
            'touchstart #MyAccount'  : 'showAccountMenu'
        });  
     },

    render:function (eventName) {    	
      return this;
    },
    
    
     
    logout:function (e) {
      app.logout();    	  
    },
    
    showTransferMenu:function(e){
  	  var transferMenu = new TransferMenu();
        app.changePage(transferMenu);   
    },
    showshopMenu:function(e){
    	  var shopMenu = new shopMenu();
          app.changePage(shopMenu);   
      },
    showAirtimeMenu:function(e){
    	  var Menu = new AirtimeMenu();
          app.changePage(Menu);   
      },
      showPayBillMenu:function(e){
    	  var PayBillMenu = new BillPaymentMenu();
          app.changePage(PayBillMenu);   
      },
      showAccountMenu:function(e){
    	  var accountMenu = new AccountMenu();
          app.changePage(accountMenu);   
      }
      
      
    
});

