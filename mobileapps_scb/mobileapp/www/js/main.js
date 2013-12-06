var AppRouter = Backbone.Router.extend({
    routes:{
        "":"main"
    },

    initialize:function () {
        $('.back').live('click', function(event) {
           // window.history.back();
            return false;
        });
       
        this.request = new Request();
        this.response = new Response();
        this.user = new User();        
    },
    
    main:function () {
        var menu =  new IndexMenuPage();
        this.changePage(menu);        
    },
    
    menu :function(){
     if(this.user.has(PARAMETER_SOURCE_MDN)){
	      if(this.user.get("type")==USER_SUBSCRIBER){
	        this.subscriberMenu();
	     }else if(this.user.get("type")==USER_AGENT){
	        this.agentMenu();
	     }
    	}else{
    		this.main();
    	}
    },
    
    subscriberMenu:function () {
     var menu =  new SubscriberMenu();
     this.changePage(menu);
    },
    
    agentMenu:function(){
    	 var menu =  new AgentMenu();
         this.changePage(menu);
    },
    
    logout:function(){
    	sendLogoutRequest();
    },
    
    changePage:function (page) {
    	disablePage();
        $(page.el).attr('data-role', 'page');
        $('body').append($(page.el));
        $.mobile.changePage($(page.el), {changeHash:false, transition: 'none'});
        setLayout();      
    }

});

$(document).ready(function() {
	tpl.loadConfig('MANIFEST.MF',function() {
		tpl.loadTemplates([ 'basepage' ], function() {
			app = new AppRouter();
			Backbone.history.start();
		});
	});
	isTouchDevice = 'ontouchstart' in document.documentElement;
});