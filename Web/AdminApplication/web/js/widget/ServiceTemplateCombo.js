    // vim: sw=4:ts=4:nu:nospell:fdc=4
    /*global Ext, Example */
    /**
    * Combo with Remote Store Example
    *
    * @author Ing. Jozef Sakáloš
    * @copyright (c) 2009, by Ing. Jozef Sakáloš
    * @date 3. April 2008
    * @version $Id: combo.js 144 2009-04-07 13:04:52Z jozo $
    *
    * @license combo.js is licensed under the terms of the Open Source
    * LGPL 3.0 license. Commercial use is permitted to the extent that the
    * code/component(s) do NOT become part of another Open Source or Commercially
    * licensed development library or toolkit without explicit permission.
    *
    * License details: http://www.gnu.org/licenses/lgpl.html
    */
     
    Ext.ns('Example');
     
    Example.comboConfig = {
    xtype:'combo'
     
    // we need id to focus this field. See window::defaultButton
    ,id:'combo'
     
    // we want to submit id, not text
    ,valueField:'persID'
    ,hiddenName:'persID'
     
    // could be undefined as we use custom template
    ,displayField:'persLastName'
     
    // query all records on trigger click
    ,triggerAction:'all'
     
    // minimum characters to start the search
    ,minChars:2
     
    // do not allow arbitrary values
    ,forceSelection:true
     
    // otherwise we will not receive key events
    ,enableKeyEvents:true
     
    // let's use paging combo
    ,pageSize:5
     
    // make the drop down list resizable
    ,resizable:true
     
    // we need wider list for paging toolbar
    ,minListWidth:220
     
    // force user to fill something
    ,allowBlank:false
     
    // store getting items from server
    ,store:new Ext.data.JsonStore({
    id:'persID'
    ,root:'rows'
    ,totalProperty:'totalCount'
    ,fields:[
    {name:'persID', type:'int'}
    ,{name:'persLastName', type:'string'}
    ,{name:'persFirstName', type:'string'}
    ]
    ,url:'PartnerProcessor'
    ,baseParams:{
    cmd:'getData'
    ,objName:'person2'
    ,fields:'["persLastName","persFirstName"]'
    }
    })
     
    // concatenate last and first names
    ,tpl:'<tpl for="."><div class="x-combo-list-item">{persLastName}, {persFirstName}</div></tpl>'
     
    // listeners
    ,listeners:{
    // sets raw value to concatenated last and first names
    select:function(combo, record, index) {
    this.setRawValue(record.get('persLastName') + ', ' + record.get('persFirstName'));
    }
     
    // repair raw value after blur
    ,blur:function() {
    var val = this.getRawValue();
    this.setRawValue.defer(1, this, [val]);
    }
     
    // set tooltip and validate
    ,render:function() {
    this.el.set(
    {qtip:'Type at least ' + this.minChars + ' characters to search in last name'}
    );
    this.validate();
    }
     
    // requery if field is cleared by typing
    // ,keypress:{buffer:100, fn:function() {
    // if(!this.getRawValue()) {
    // this.doQuery('', true);
    // }
    // }}
    }
     
    // label
    ,fieldLabel:'Combo'
    };
     
    // main entry point
    Ext.onReady(function() {
    Ext.QuickTips.init();
     
    // invalid markers to sides
    Ext.form.Field.prototype.msgTarget = 'side';
     
    // create and show window
    var win = new Ext.Window({
    id:'combo-win'
    ,title:Ext.fly('page-title').dom.innerHTML
    ,layout:'fit'
    ,width:300
    ,height:150
    ,closable:false
    ,border:false
     
    // let window code to focus the combo on show
    ,defaultButton:'combo'
    ,items:{
    xtype:'form'
    ,frame:true
    ,defaults:{anchor:'-20'}
    ,items:[{
    xtype:'textfield'
    ,fieldLabel:'Dummy Field'
    }
    ,Example.comboConfig
    ]
    }
    });
     
    win.show();
     
    }); // eo onReady
     
    // eof
