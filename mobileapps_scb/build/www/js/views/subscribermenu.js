window.SubscriberMenu = Backbone.View.extend({ 
	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"TransferMenu",text:"Transfer"})+
        							  addMenuItem({id:"BuyMe",text:"Buy"})+
        							  addMenuItem({id:"Bill",text:"Bill Payment"})+
        							  addMenuItem({id:"Account",text:"Account"}));
        $('#back', this.el).remove();
        $('#home', this.el).remove();
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #TransferMenu':   'showTransferMenu',
            'click #BuyMe'  : 'showBuyMenu',
            'click #Bill'  : 'showBillPaymentMenu',
            'click #Account'  : 'showAccountMenu'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #TransferMenu':   'showTransferMenu',
            'touchstart #BuyMe'  : 'showBuyMenu',
            'touchstart #Bill'  : 'showBillPaymentMenu',
            'touchstart #Account'  : 'showAccountMenu'
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
    showBuyMenu:function(e){
    	  var buyMenu = new BuyMenu();
          app.changePage(buyMenu);   
      },
      showBillPaymentMenu:function(e){
    	  var billPaymentMenu = new BillPaymentMenu();
          app.changePage(billPaymentMenu);   
      },
      showAccountMenu:function(e){
    	  var accountMenu = new AccountMenu();
          app.changePage(accountMenu);   
      }
      
      
    
});

