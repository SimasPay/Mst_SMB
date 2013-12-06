/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.widget");

/*
 * @param config dataUrl is the base url to get data
 */

var isParentSearch = false;
var merchantIdSet = null;
var node= null;
mFino.widget.MerchantTree = function (config) {

    var localConfig = Ext.apply({}, config);
    var treeroottext = mFino.auth.getTreeRootText();
    if(!mFino.auth.isMerchant()) {
        localConfig.root = new Ext.tree.AsyncTreeNode({
            text: treeroottext,
            id : '0',
            icon: 'resources/images/customer.png'
        });
    } else {
        var rootText = mFino.auth.getTreeRootText();
        var rootId = mFino.auth.getTreeRootId();
        localConfig.root = new Ext.tree.AsyncTreeNode({
            text: rootText,
            id : rootId,
            icon: 'resources/images/customer.png'
        });
    }
    
    var treeLoader = new Ext.tree.TreeLoader({
        dataUrl: "tree.htm"
    });

    treeLoader.on("beforeload", function(t,n) {
        if(this.isParentSearch) {
            t.baseParams.search = CmFinoFIX.TreeSearch.AllParents;
            t.baseParams.merchantid = this.merchantIdSet;
        } else {
            t.baseParams.search = CmFinoFIX.TreeSearch.ImmediateChildren;
            t.baseParams.merchantid = n.attributes.id;
        }
    }, this);
    treeLoader.on("load", function(t, n, r) {
        this.isParentSearch = false;
        if(this.node) {
            this.node.select();
            this.node = null;
        }
        var json = r.responseText;

        // Here we can use this method to load new children or reload the children.
        if(null === json || "" === json) {
            // If we reach here then we have null response.
            Ext.ux.Toast.msg("Error" , "No data to render. Please check", 5);
            return;
        }

        if(json.match("INFO*")) {
            // Ext.ux.Toast.msg("INFO" , "No children for this merchant.", 1);
            n.attributes.leaf = true;
            n.select();
            return;
        }

        if(json.match("ERROR*")) {
            // If we reach here then we need to show the toast message.
            Ext.ux.Toast.msg("Error" , r.responseText, 5);
            return;
        }
    }, this);

    
    localConfig = Ext.applyIf(localConfig, {
        dataUrl  : "tree.htm",
        loadMask : true,
        height : 412,
        title: _('Merchant Distribution Chain'),
        useArrows: true,
        autoScroll: true,
        animate: true,
        enableDD: false,
        containerScroll: true,
        border: false,
        draggable: false,
        loader: treeLoader,
        listeners: {
            beforeexpandnode: function(n) {
                if(this.isParentSearch) {
                    return;
                }
                
                if(n.hasChildNodes()) {
                    while (n.hasChildNodes()) {
                        n.item(0).remove();
                    }
                    n.select();
                    this.getLoader().requestData(n);
                } 
            }, 
            click: function(n) {
                if(n.attributes.text === 'Smart') {
                    return;
                }
                this.fireEvent("nodeClickEvent", n.attributes.id, n.attributes.text);
            },
            append: function(t,pn, cn, index) {
                if(this.isParentSearch) {
                    this.node=cn;
                }
            }
        }
    });
 
    mFino.widget.MerchantTree.superclass.constructor.call(this, localConfig);
};

Ext.extend(mFino.widget.MerchantTree, Ext.tree.TreePanel, {
    initComponent : function () {
        this.addEvents("nodeClickEvent");
        mFino.widget.MerchantTree.superclass.initComponent.call(this);
    },
    loadThisMerchant: function(merchantId) {
        if(!merchantId) {
            this.getRootNode().collapse();

            return;
        }

        if(merchantId === this.getRootNode().attributes.id) {
            this.getRootNode().collapse();
            if(mFino.auth.isMerchant()) {
                this.getRootNode().expand();
            }
            return;
        }

        this.isParentSearch = true;
        this.merchantIdSet = merchantId;
        this.getLoader().baseParams.search = CmFinoFIX.TreeSearch.AllParents;
        this.getLoader().baseParams.merchantid = this.merchantIdSet;
        this.getRootNode().attributes.children = false;
        this.getRootNode().reload();
    }
});


