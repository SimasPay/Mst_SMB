window.ResponseView = Backbone.View.extend({ 
  
      initialize:function (response) {
          this.template = _.template(tpl.get('basepage'));
          $(this.el).html(this.template());
          if(response.data){
        	  $('#content', this.el).append("<center>("+response.data.code+") "+response.data.message);
          }else{
        	  $('#content', this.el).append("<center>"+response.message);
          }
          $('#content', this.el).append(addButton({id:"ok",value:"OK",inline:true}));
          if(!app.user.has(PARAMETER_SOURCE_MDN)){
        	  $('#home', this.el).remove();
          }
          $('#logout', this.el).remove();
          $('#back', this.el).remove();
          this.events=getEvents({
        	  'click #logout' : 'logout',
              'click #home' : 'home',
              'click #ok' : 'back'
          },{
        	  'touchstart #logout' : 'logout',
              'touchstart #home' : 'home',
              'touchstart #ok' : 'back'
          });         
         },
  
      render:function (eventName) {
        return this;
      },
      
     logout:function(){
        app.logout();
      },
      
      home:function(){
    	  app.menu();
      },
      back:function(){
    	  app.menu();
      }
      
  });