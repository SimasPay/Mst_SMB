/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.ns("mFino.subcriberGrid");

mFino.subcriberGrid = function(dataUrl){

    var proxy = new Ext.data.HttpProxy({
        prettyUrls: false,
        api: {
            read: dataUrl + "/get.htm",
            create: dataUrl + "/create.htm",
            update: dataUrl + "/update.htm",
            destroy: dataUrl + "/destroy.htm"
        }
    });

    var record = Ext.data.Record.create(
        [
        { name: 'id'},
        { name: 'MSPID', allowBlank: false },
        { name: 'ParentID',allowBlank: false},
        { name: 'FirstName', type: 'date',dateFormat: 'n/j/Y'},
        { name: 'LastName'},
        { name: 'Email'},
        { name: 'NotificationMethod'},
        { name: 'Language'},
        { name: 'Currency'},
        { name: 'Timezone'},
        { name: 'SubscriberRestrictions'},
        { name: 'SubscriberType'},
        { name: 'SubscriberStatus'},
        { name: 'StatusTime'},
        { name: 'CreateTime'},
        { name: 'ActivationTime'},
        { name: 'LastUpdateTime'},
        { name: 'UpdatedBy'}
        ]);

    var reader = new Ext.data.JsonReader(
    {
        totalProperty: 'total',   //   the property which contains the total dataset size (optional)
        idProperty: 'id',
        root: 'rows',
        successProperty: 'success'
    },
    record
    );

    var writer = new Ext.data.JsonWriter({
        returnJson: true,
        writeAllFields: true
    });

    var store = new Ext.data.Store({
        id: 'subscriber',
        proxy: proxy,
        reader: reader,
        writer: writer,
        paramsAsHash: true,
        batchSave: false,
        autoLoad: false

    });
    
    store.loadData();

    var grid = new Ext.grid.EditorGridPanel({
        renderTo: 'main',
        store: store,
        height: 400,
        width: 500,
        loadMask : true,
        sm: new Ext.grid.RowSelectionModel({ singleSelect: true }),
        columns: [{ header:'id',dataIndex: "id"},
        { header:'MSPID', dataIndex: "MSPID" },
        { header:'ParentID',dataIndex: "ParentID"},
        { header:'FirstName',dataIndex: "FirstName"},
        { header:'LastName',dataIndex: "LastName"},
        { header:'Email',dataIndex: "Email"},
        { header:'NotificationMethod',dataIndex: "NotificationMethod"},
        { header:'Language',dataIndex: "Language"},
        { header:'Currency',dataIndex: "Currency"},
        { header:'Timezone',dataIndex: "Timezone"},
        { header:'SubscriberRestrictions',dataIndex: "SubscriberRestrictions"},
        { header:'SubscriberType',dataIndex: "SubscriberType"},
        { header:'SubscriberStatus',dataIndex: "SubscriberStatus"},
        { header:'StatusTime',dataIndex: "StatusTime"},
        { header:'CreateTime',dataIndex: "CreateTime"},
        { header:'ActivationTime',dataIndex: "ActivationTime"},
        { header:'LastUpdateTime',dataIndex: "LastUpdateTime"},
        { header:'UpdatedBy',dataIndex: "UpdatedBy"}]
    });


};



