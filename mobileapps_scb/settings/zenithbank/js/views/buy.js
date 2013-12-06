window.BuyMenu= Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"Purchase",text:"Purchase"})+
        							  addMenuItem({id:"Airtime",text:"Airtime"}));
        $('#logout', this.el).remove();  
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'click #Purchase':   'showPurchaseMenu',
            'click #Airtime':   'showAirtimeMenu'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #Purchase':   'showPurchaseMenu',
            'touchstart #Airtime':   'showAirtimeMenu'
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
    
    showPurchaseMenu:function(){
   	 var Menu = new PurchaseInquiryForm();
        app.changePage(Menu);     	
   },
   
   showAirtimeMenu:function(){
   	 var Menu = new AirtimeMenu();
        app.changePage(Menu);     	
   }
   
});

window.PurchaseInquiryForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_PURCHASE_INQUIRY})+
        							   addHiddenField({name:PARAMETER_SERVICE_NAME,value:SERVICE_SHOPPING})+
        							   addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.user.get(PARAMETER_SOURCE_MDN)})+
        							   addHiddenField({name:PARAMETER_SRC_POCKET_CODE,value:POCKET_CODE_SVA})+
        							   addField({label:"Enter Code",name:PARAMETER_PARTNER_CODE,type:"text",required:true,validations:true})+
        							   addField({label:"Amount",name:PARAMETER_AMOUNT,type:"text",required:true,validations:true})+
        							   addField({label:"Bill No",name:PARAMETER_BILL_NO,type:"text",required:true,validations:true})+
        							   addField({label:"PIN",name:PARAMETER_SOURCE_PIN,type:"password",required:true,validations:true})+
        							   addButton({id:"Buy",value:"buy",type:"submit",inline:true}));
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
    	var menu=new BuyMenu();
    	app.changePage(menu);
    },
    submit:function(){
        var self = this;
         var data = $('#baseForm').serialize();
         sendRequest(data,self);
         return false;  
      },
    onSuccess:function(response){       
 	   if(response.data.code==72){
 	          app.response.set(response.data);
 	          var buyConfirmation = new PurchaseConfirmationForm();
 	          app.changePage(buyConfirmation);
 	        }else{
 	       var error = new ResponseView(response);
   	         app.changePage(error);
 	      }
    }    
    
   
});

window.PurchaseConfirmationForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_PURCHASE})+
        							   addHiddenField({name:PARAMETER_SERVICE_NAME,value:app.request.get(PARAMETER_SERVICE_NAME)})+
        							   addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.request.get(PARAMETER_SOURCE_MDN)})+
        							   addHiddenField({name:PARAMETER_PARTNER_CODE,value:app.request.get(PARAMETER_PARTNER_CODE)})+
        							   addHiddenField({name:PARAMETER_TRANSFER_ID,value:app.response.get(PARAMETER_TRANSFER_ID)})+
        							   addHiddenField({name:PARAMETER_PARENTTXN_ID,value:app.response.get(PARAMETER_PARENTTXN_ID)})+
        							   "<center><b> Do u want to purchase from partner\n </b>"+app.request.get(PARAMETER_PARTNER_CODE)+
        								"<br><b> Amount\n </b>"+app.response.get(PARAMETER_CREDIT_AMT)+
        								"<br><b> Charge\n </b>"+app.response.get(PARAMETER_CHARGES)+"</center>"+
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

window.AirtimeMenu = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"visafone",text:"Visafone"})+
                               addMenuItem({id:"Other",text:"Other"}));  
        $('#logout', this.el).remove();
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'click #visafone':   'showVisafoneForm',
            'click #Other':   'showSelectOperatorMenu',
        },{
        	'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart #visafone':   'showVisafoneForm',
            'touchstart #Other':   'showSelectOperatorMenu',
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
    	 var buyMenu = new BuyMenu();
         app.changePage(buyMenu);  
    },
    showVisafoneForm:function(){
   	 var visafoneForm = new VisafoneMenu();
        app.changePage(visafoneForm); 
   },
   
   showSelectOperatorMenu:function(){
   	 var operatorForm = new SelectorOperatorMenu();
        app.changePage(operatorForm);     	
   }
   
});

window.VisafoneMenu = Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"Self",text:"For Self"})+
        					   addMenuItem({id:"Others",text:"For Others"}));
        $('#logout', this.el).remove(); 
        this.events=getEvents({
        	 'click #logout':   'logout',
             'click #home':   'home',
             'click #back':   'back',
             'click #Self':   'showSelfForm',
             'click #Others':   'showOtherForm'
        },{
        	 'touchstart #logout':   'logout',
             'touchstart #home':   'home',
             'touchstart #back':   'back',
             'touchstart #Self':   'showSelfForm',
             'touchstart #Others':   'showOtherForm'
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
    	 var transferMenu = new AirtimeMenu();
         app.changePage(transferMenu);  
    },
    
    showSelfForm:function(){
    	 var selfForm = new AirtimePurchaseInquiryForm({isSelfBuy:true,visafone:true,cmpid:1});
         app.changePage(selfForm);  	
    },
    
    showOtherForm:function(){
    	 var otherForm = new AirtimePurchaseInquiryForm({isSelfBuy:false,visafone:true,cmpid:1});
         app.changePage(otherForm); 
    }
    
});

window.SelectorOperatorMenu= Backbone.View.extend({ 	  
    initialize:function () {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        $('#content', this.el).append(addMenuItem({id:"MTN",text:"MTN",name:1})+
        					   addMenuItem({id:"Glo",text:"Glo",name:1})+
        					   addMenuItem({id:"Airtel",text:"Airtel",name:1}));
        $('#logout', this.el).remove(); 
        this.events=getEvents({
        	'click #logout':   'logout',
            'click #home':   'home',
            'click #back':   'back',
            'click #MTN':   'showAirtimeInquiryForm',
            'click #Glo':   'showAirtimeInquiryForm',
            'click #Airtel':   'showAirtimeInquiryForm'
        },{
        	'touchstart #logout':   'logout',
            'touchstart #home':   'home',
            'touchstart #back':   'back',
            'touchstart button':   'showAirtimeInquiryForm',
            'touchstart #MTN':   'showAirtimeInquiryForm',
            'touchstart #Glo':   'showAirtimeInquiryForm',
            'touchstart #Airtel':   'showAirtimeInquiryForm'
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
      var menu=new AirtimeMenu();
      app.changePage(menu);
    },
    
    showAirtimeInquiryForm:function(e){
    	var clickeMenu = $(e.currentTarget);
    	var cmpid = clickeMenu.attr("name");
   	 var Menu = new AirtimePurchaseInquiryForm({isSelfBuy:false,cmpid:1});
        app.changePage(Menu);     	
   }
   
});

window.AirtimePurchaseInquiryForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
       
        this.config = config;
        
        var service = app.user.get('type')==USER_SUBSCRIBER?SERVICE_BUY:SERVICE_AGENT;
       
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_AIRTIME_PURCHASE_INQUIRY})+
        							   addHiddenField({name:PARAMETER_SERVICE_NAME,value:service })+
        							   addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.user.get(PARAMETER_SOURCE_MDN)})+
        							   addHiddenField({name:PARAMETER_SRC_POCKET_CODE,value:POCKET_CODE_SVA})+
        							   addHiddenField({name:COMPANY_ID,value:config.cmpid}));
        
        if(config.isSelfBuy)  {
	        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_DEST_MDN,value:app.user.get(PARAMETER_SOURCE_MDN)}));	        
	    }else{
	    	  $('#baseForm', this.el).append(addField({label:"destMDN",name:PARAMETER_DEST_MDN,type:"text",required:true,validations:true}));
	    }
        $('#baseForm', this.el).append(addField({label:"Amount",name:PARAMETER_AMOUNT,type:"text",required:true,validations:true})+
                                addField({label:"PIN",name:PARAMETER_SOURCE_PIN,type:"password",required:true,validations:true})+
                                addButton({id:"Buy",value:"BuyAirtime",type:"submit",inline:true}));
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
    	var menu;
    	if(this.config.visafone){
      	  menu = new VisafoneMenu(); 
      	}else{
      		 menu = new SelectorOperatorMenu();
      	}
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
 	   if(response.data.code==CODE_AIRTIME_INQUIRY_SUCCESS){
 	          app.response.set(response.data);
 	          var buyConfirmation = new AirtimePurchaseConfirmationForm(this.config);
 	          app.changePage(buyConfirmation);
 	        }else{
 	        	var error = new ResponseView(response);
 	        	app.changePage(error);
 	      }
    }    
    
   
});

window.AirtimePurchaseConfirmationForm = Backbone.View.extend({ 	  
    initialize:function (config) {
        this.template = _.template(tpl.get('basepage'));
        $(this.el).html(this.template());
        
        $('#baseForm', this.el).append(addHiddenField({name:PARAMETER_TRANSACTIONNAME,value:TRANSACTION_AIRTIME_PURCHASE})+
        						addHiddenField({name:PARAMETER_SERVICE_NAME,value:app.request.get(PARAMETER_SERVICE_NAME)})+
        						addHiddenField({name:PARAMETER_SOURCE_MDN,value:app.request.get(PARAMETER_SOURCE_MDN)})+
        						addHiddenField({name:PARAMETER_DEST_MDN,value:app.request.get(PARAMETER_DEST_MDN)})+
        						addHiddenField({name:PARAMETER_SRC_POCKET_CODE,value:app.request.get(PARAMETER_SRC_POCKET_CODE)})+
        						addHiddenField({name:COMPANY_ID,value:app.request.get(COMPANY_ID)})+
        						addHiddenField({name:PARAMETER_TRANSFER_ID,value:app.response.get(PARAMETER_TRANSFER_ID)})+
        						addHiddenField({name:PARAMETER_PARENTTXN_ID,value:app.response.get(PARAMETER_PARENTTXN_ID)})+
        						addHiddenField({name:PARAMETER_AMOUNT,value:app.request.get(PARAMETER_AMOUNT)})+
                                "<center><b>Do you want to buy airtime of NGN </b>"+app.request.get(PARAMETER_AMOUNT)+
                                addButton({id:"cancel",value:"Cancel",inline:true})+
                                addButton({id:"confirm",value:"Confirm",type:"submit",inline:true}));
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


  