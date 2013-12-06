/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("FIX");

FIX.FIXProxy = function(conn) {

    this.defaultHeaders = this.defaultHeaders || {};

    Ext.apply(this.defaultHeaders, {
        'Content-Type': 'text/fix'
    });
    
    // Adding the Timeout parameter to the connection object.
    // Currently setting the timeout to 1 and a half minute. 90 seconds.
    conn.timeout=120000;

    FIX.FIXProxy.superclass.constructor.call(this, conn);
};

Ext.extend(FIX.FIXProxy, Ext.data.HttpProxy, {
    doRequest: function(action, rs, params, reader, cb, scope, arg) {
        params.format = 'FIX';
        var MsgToSend = null;
        var BufToSend = new FIX.CMultiXBuffer();
        var Root;
        var ID;
        var RecordVersion;
        var p;
        var Records;
        var Entries;
        var I;
        var Rec;

        if(scope.reader	&& scope.reader.meta.root){
            Root = scope.reader.meta.root;
        }
        else
        if (scope.root)
        {
            Root = scope.root;
        }
        else
        {
            Root = 'Entries';
        }
        if (scope.reader && scope.reader.meta.idProperty)
        {
            ID = scope.reader.meta.idProperty;
        }
        else
        if (scope.idProperty)
        {
            ID = scope.idProperty;
        }
        else
        {
            ID = 'ID';
        }
        if (scope.reader && scope.reader.meta.recordVersionProperty)
        {
            RecordVersion = scope.reader.meta.recordVersionProperty;
        }
        else
        if (scope.recordVersionProperty)
        {
            RecordVersion = scope.recordVersionProperty;
        }
        else
        {
            RecordVersion = 'RecordVersion';
        }
        switch (action) {
            case 'read':
                if (scope.reader && scope.reader.meta.LoadRequest)
                {
                    MsgToSend = new scope.reader.meta.LoadRequest();
                }
                else
                {
                    MsgToSend = new scope.RPCObject();
                }
                for (p in params) {
                    if (typeof p != "object") {
                        MsgToSend['m_p' + p]= params[p];
                    }
                }
                break;
            case 'create':
                if (scope.writer && scope.writer.meta.SaveRequest)
                {
                    MsgToSend = new scope.writer.meta.SaveRequest();
                }
                else
                {
                    MsgToSend = new scope.RPCObject();
                }

                Records = [];
                if (!Ext.isArray(rs))
                {
                    Records[0] = rs;
                }
                else
                {
                    Records = rs;
                }
                Entries = MsgToSend["Allocate_" + Root](Records.length, true);
                for (I = 0; I < Records.length; I++) {
                    Rec = Records[I];
                    for (p in Rec.data) {
                        Entries[I]['m_p' + p] = Rec.data[p];
                    }
                }
                break;

            case 'update':
                if (scope.writer && scope.writer.meta.SaveRequest)
                {
                    MsgToSend = new scope.writer.meta.SaveRequest();
                }
                else
                {
                    MsgToSend = new scope.RPCObject();
                }

                Records = [];
                if (!Ext.isArray(rs))
                {
                    Records[0] = rs;
                }
                else
                {
                    Records = rs;
                }
                Entries = MsgToSend["Allocate_" + Root](Records.length, true);
                for (I = 0; I < Records.length; I++) {
                    Rec = Records[I];
                    for (p in Rec.modified) {
                        Entries[I]['m_p' + p] = Rec.data[p];
                        Entries[I]['m_p' + p+'Modified'] = true;
                    }
                    Entries[I]['m_p' + ID] = Rec.data[ID];
                    Entries[I]['m_p' + RecordVersion] = Rec.data[RecordVersion];
                }
                break;
            case 'destroy':
                if (scope.writer && scope.writer.meta.SaveRequest)
                {
                    MsgToSend = new scope.writer.meta.SaveRequest();
                }
                else
                {
                    MsgToSend = new scope.RPCObject();
                }

                Records = [];
                if (!Ext.isArray(rs))
                {
                    Records[0] = rs;
                }
                else
                {
                    Records = rs;
                }
                Entries = MsgToSend["Allocate_" + Root](Records.length, true);
                for (I = 0; I < Records.length; I++) {
                    Entries[I]['m_p' + ID] = Records[I].data[ID];
                }
                break;
        }

        if (MsgToSend === null)
        {
            return;
        }
        if (scope.writer && scope.writer.meta.xactionProperty)
        {
            MsgToSend["m_p" + scope.writer.meta.xactionProperty][0] = params.xaction;
        }
        else if (scope.xactionProperty)
        {
            MsgToSend["m_p" + scope.xactionProperty] = params.xaction;
        }
        else if(params.xaction)
        {
            MsgToSend["m_p" + 'xaction'] = params.xaction;
        }

        if (scope.writer && scope.writer.meta.actionProperty)
        {
            MsgToSend["m_p" + scope.writer.meta.actionProperty][0] = action;
        }
        else if (scope.actionProperty)
        {
            MsgToSend["m_p" + scope.actionProperty] = action;
        }
        else
        {
            MsgToSend["m_p" + 'action'] = action;
        }
        
        MsgToSend.ToFIX(BufToSend);

        this.BufToSend = BufToSend;
        //params are being sent as key value pair instead of as a string, so that we can get param value in controller using getParameter() method
        
        /*var NewParams = function(Options) {
            Options.method = "POST";
            return Options.scope.BufToSend.DataPtr();
        };*/

        FIX.FIXProxy.superclass.doRequest.call(this, action, rs, {'data': BufToSend.DataPtr()}, reader, /*writer,*/cb, scope, arg);
    }
});
