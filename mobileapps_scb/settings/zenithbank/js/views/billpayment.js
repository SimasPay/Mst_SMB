window.BillPaymentMenu= Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"eaZymoney",text:"eaZymoney"})+
        					   addMenuItem({id:"Bank",text:"Bank"}));   
        $('#logout', this.el).remove();
        this.events=getEvents({
     	   'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'click #Bank':   'showBankPayment',
            'click #eaZymoney':   'showEazymoneyPayment'
        },{
     	   'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #Bank':   'showBankPayment',
            'touchstart #eaZymoney':   'showEazymoneyPayment'
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
    
    showBankPayment:function(){
   	 var bankPayment = new BillPaymentInquiryForm({sourcePocketCode:POCKET_CODE_BANK});
        app.changePage(bankPayment);     	
   },
   
   showEazymoneyPayment:function(){
   	 var eazymoneyPayment = new BillPaymentInquiryForm({sourcePocketCode:POCKET_CODE_SVA});
        app.changePage(eazymoneyPayment);     	
   }
   
});

window.BillPaymentInquiryForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        var service = SERVICE_PAYMENT;
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_BILL_PAY_INQUIRY})+
        							   addHiddenField({name:PARAMETER_SERVICE_NAME,value:service})+
        							   addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.user.get(PARAMETER_SOURCE_MDN)})+
        							   addHiddenField({name:PARAMETER_SRC_POCKET_CODE,value:config.sourcePocketCode})+
        							   addField({label:"Enter Biller Code",name:PARAMETER_BILLER_CODE,type:"text",required:true,validations:true})+
        							   addField({label:"Amount",name:PARAMETER_AMOUNT,type:"text",required:true,validations:true})+
        							   addField({label:"Bill No",name:PARAMETER_BILL_NO,type:"text",required:true,validations:true})+
        							   addField({label:"PIN",name:PARAMETER_SOURCE_PIN,type:"password",required:true,validations:true})+
        							   addButton({id:"submit",value:"submit",type:"submit",inline:true}));
        $('#logout', this.el).remove();
        this.events=getEvents({
     	   'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'submit form':   'submit'
        },{
     	   'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
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
    
    back:function(e){
    	var menu=new BillPaymentMenu();
    	app.changePage(menu);
    },
    submit:function(){
    	if($('#baseForm').valid()){
    		var self = this;
    		var data = $('#baseForm').serialize();
    		sendRequest(data,self);
    	}
         return false;  
      },
    onSuccess:function(response){       
 	   if(response.data.code==CODE_TRANSFERINQUIRY_SUCCESS){
 	          app.response.set(response.data);
 	          var billpaymentConfirmation = new BillPaymentConfirmationForm();
 	          app.changePage(billpaymentConfirmation );
 	        }else{
 	        	var error = new ResponseView(response);
	   	        app.changePage(error);
 	      }
    }    
    
   
});

window.BillPaymentConfirmationForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_BILL_PAY})+
        						addHiddenField({name:PARAMETER_SERVICE_NAME,value:app.request.get(PARAMETER_SERVICE_NAME)})+
        						addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.request.get(PARAMETER_SOURCE_MDN)})+
        						addHiddenField({name:PARAMETER_TRANSFER_ID,value:app.response.get(PARAMETER_TRANSFER_ID)})+
        						addHiddenField({name:PARAMETER_PARENTTXN_ID,value:app.response.get(PARAMETER_PARENTTXN_ID)})+
        						addHiddenField({name:PARAMETER_BILLER_CODE,value:app.request.get(PARAMETER_BILLER_CODE)})+
        						addHiddenField({name:PARAMETER_BILL_NO,value:app.request.get(PARAMETER_BILL_NO)})+
        						"<center><b> Do u want to pay bill \n </b>"+
        								"<b>Biller code\n</b>"+	app.request.get(PARAMETER_BILLER_CODE)+
        								"<br><b>Bill number\n</b>"+	app.request.get(PARAMETER_BILL_NO)+
        								"<br><b> Amount: </b>"+app.request.get(PARAMETER_AMOUNT)+"<center>"+
        					   addButton({id:"cancel",value:"Cancel",inline:true})+
        					   addButton({id:"confirm",value:"Confirm",type:"submit",inline:true}));
        $('#logout', this.el).remove(); 
        $('#back', this.el).remove(); 
        this.events=getEvents({
     	   'click #logout':   'logout',
            'click #home':   'home',
            'click #cancel':   'cancel',
            'submit form':   'submit'
        },{
     	   'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #cancel':   'cancel',
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
    
    cancel:function(e){
   	 var self = this;
        var data = $('#baseForm').serialize();
        data = data+"&confirmed="+CONSTANT_VALUE_FALSE;
        sendRequest(data,self); 
   },
   
   submit:function(){
       var self = this;
        var data = $('#baseForm').serialize();
        data = data+"&confirmed="+CONSTANT_VALUE_TRUE;
        sendRequest(data,self);
        return false;  
     },
     
      onSuccess:function(response){       
   	         var error = new ResponseView(response);
     	         app.changePage(error);
      }    
    
});

