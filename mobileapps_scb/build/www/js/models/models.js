window.Request = Backbone.Model.extend({

    urlRoot:url,

    initialize:function () {
       // this.collection = new ResponseCollection();
    }

});

window.Response = Backbone.Model.extend({

    urlRoot:url,

    initialize:function () {
       // this.collection = new ResponseCollection();
    }

});


window.User = Backbone.Model.extend({

    urlRoot:url,

    initialize:function () {
       // this.collection = new ResponseCollection();
    }

});

window.Labels = Backbone.Model.extend({

    urlRoot:url,

    initialize:function () {
    	var labels= this;
    	$.ajax({
    	    type: "GET",
    	    url: "labels.xml",
    	    dataType: "xml",
    	    success: function(xml){
    	    	$(xml).find("Labels").each(function(){
    	    	    $(this).children().each(function(){
    	    	        labels.set(this.tagName,$(this).text());
    	    	    });
    	    	});
    		},
    	    error:this.onError
    	  });
    },
	
	getLabel:function(name,label){
		if(this.get(name)){
			return this.get(name);
		}else{
			return label;
		}
	},
	
	onError:function(){
    	console.log("failed to load xml");
    }

});
window.ButtonLabel = Backbone.Model.extend({

    urlRoot:url,

    initialize:function () {
    	var ButtonLabel= this;
    	$.ajax({
    	    type: "GET",
    	    url: "ButtonLabel.xml",
    	    dataType: "xml",
    	    success: function(xml){
    	    	console.log("but");
    	    	$(xml).find("ButtonLabel").each(function(){
    	    	    $(this).children().each(function(){
    	    	    	ButtonLabel.set(this.tagName,$(this).text());
    	    	    });
    	    	});
    		},
    	    error:this.onError
    	  });
    },
	
	
  getButtonLabel:function(id,text){
		if(this.get(id)){
			return this.get(id);
		}else{
			return text;
		}
	},
	
    
    onError:function(){
    	console.log("failed to load xml");
    }

});

/*window.AppConfig = Backbone.Model.extend({

    urlRoot:url,

    initialize:function () {
       // this.collection = new ResponseCollection();
    }

});*/