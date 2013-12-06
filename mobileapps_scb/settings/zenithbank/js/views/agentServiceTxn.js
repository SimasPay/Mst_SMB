window.ServiceTxnMenu= Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"Cashin",text:"Cash In"})+
        					   addMenuItem({id:"Billpayment",text:"Bill Payment"})+
        					   addMenuItem({id:"sendreceipt",text:"Send Receipt"})+
        					   addMenuItem({id:"cashout",text:"Cash Out"}));
        $('#logout', this.el).remove();
        this.events=getEvents({
      	  'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'click #Cashin':   'showCashInForm',
            'click #Billpayment':   'showBillPaymentForm',
            'click #sendreceipt':   'showSendReceiptForm',
            'click #cashout':   'showCashOutForm'
        },{
      	  'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #Cashin':   'showCashInForm',
            'touchstart #Billpayment':   'showBillPaymentForm',
            'touchstart #sendreceipt':   'showSendReceiptForm',
            'touchstart #cashout':   'showCashOutForm'
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
    
    back:function(e){
      app.menu();
    },
    
    showCashInForm:function(){
    	var back = new ServiceTxnMenu(); 
    	var cashinform = new TransferInquiryForm({srcPocketCode:POCKET_CODE_SVA,destPocketCode:POCKET_CODE_SVA,service:SERVICE_AGENT,txnName:TRANSACTION_CASHIN_INQUIRY,confirmTxnName:TRANSACTION_CASHIN,back:back});
        app.changePage(cashinform);     	
   },
   
   showBillPaymentForm:function(){
   	 var billpayform = new AgentBillPaymentInquiryForm();
        app.changePage(billpayform);     	
   },
   showSendReceiptForm:function(){
	   	 var sendreceiptform = new SendReceiptInquiryForm();
	        app.changePage(sendreceiptform);     	
	   },
	   showCashOutForm:function(){
		   	 var cashoutform = new AgentCashOutInquiryForm();
		        app.changePage(cashoutform);     	
		   }
   
});


