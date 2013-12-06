 /*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

mFino.widget.DHTree = function (config) {

	var localConfig = Ext.apply({rootVisible: false}, config);
	
	var DctTreeNode = function(config){
		var treeNodeConfig = Ext.apply({}, config);
		DctTreeNode.superclass.constructor.call(this, treeNodeConfig);		
	};
	
	Ext.extend(DctTreeNode, Ext.tree.AsyncTreeNode, {
		initComponent : function () {
			DctTreeNode.superclass.initComponent.call(this);
		}
	});
	
	localConfig.root = new DctTreeNode(config);
	
	var treeLoader = new Ext.tree.TreeLoader({
		dataUrl: "distributionTree.htm"
    });
    
   treeLoader.on("beforeload", function(t,n) {	   
        if(this.isParentSearch) {
            t.baseParams.getParents = true;
            t.baseParams.dctNodeType = this.dctNodeType;
        } else {
            t.baseParams.getParents = false;
			t.baseParams.dctNodeType = n.attributes.dctNodeType;
			t.baseParams.objectId = n.attributes.objectId;
			t.baseParams.dctId = n.attributes.dctId;
			t.baseParams.partnerId = n.attributes.partnerId;
			t.baseParams.srchDctName = this.srchDctName;
			t.baseParams.srchServiceId = this.srchServiceId;			
        }
        if(this.isPopUpWindow) {
        	t.baseParams.addCheckBox = true;
        	this.root.id = n.attributes.objectId;        	
 	   	} else {
 	   		t.baseParams.addCheckBox = false;
 	   	}
    }, this);  
   
   
   	localConfig = Ext.applyIf(localConfig, {
        dataUrl  : "distributionTree.htm",
        loadMask : true,
        height : 412,
        useArrows: true,
        autoScroll: true,
        animate: true,
        enableDD: false,
        containerScroll: true,
        border: false,
        draggable: false,
        loader: treeLoader,
        listeners: {            
            click: function(n) {
                this.fireEvent("nodeClickEvent", n.attributes.id, n.attributes.text, n.attributes.objectId, n.attributes.dctNodeType, n.attributes.PermissionType, n.attributes.serviceId, n.attributes.dctId, n.attributes.mdn, n.attributes.subscriberId, n.attributes.businessPartnerType, n.attributes.levels, n.attributes);
            },
            append: function(t,pn, cn, index) {
                if(this.isParentSearch) {
                    this.node=cn;
                }
            },
            render : function(){
                this.loadTree({});
            }
        }
    });
 
    mFino.widget.DHTree.superclass.constructor.call(this, localConfig);
}

Ext.extend(mFino.widget.DHTree, Ext.tree.TreePanel, {
    initComponent : function () {
        this.addEvents("nodeClickEvent");
        this.on('render', this.removeDisabled, this);
        mFino.widget.DHTree.superclass.initComponent.call(this);
    },
    
    removeDisabled: function(){
    	if(!mFino.auth.isEnabledItem('dct.distribute.amount')){
    		var tb = this.getTopToolbar();
    		var item = tb.getComponent('dct.distribute.amount');
    		tb.remove(item);
    	}
    },   
    
    loadTree: function(values){
		this.isParentSearch = false;
		this.srchDctName = values.NameSearch;
		this.srchServiceId = values.ServiceIDSearch;
        this.getRootNode().reload();
    }
});

