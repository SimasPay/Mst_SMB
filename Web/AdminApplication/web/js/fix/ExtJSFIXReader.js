/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("FIX");

FIX.FIXReader = function(meta, recordType) {
    meta = Ext.applyIf(meta || {},  {
        totalProperty : "total",
        successProperty : "success",
        idProperty : "ID",
        root : "Entries"
    });
    FIX.FIXReader.superclass.constructor.call(this, meta, recordType || meta.fields);
};

Ext.extend(FIX.FIXReader, Ext.data.DataReader, {
	getId : function(data){
		if(this.meta.idProperty){
			return data[this.meta.idProperty];
		}else{
			return data["ID"];
		}
	},
	
    read: function(response) {
        this.decodeFix(response);
        return this.readRecords(response);
    },
    
    readResponse: function(action, response) {
        this.decodeFix(response);
        var Result = this.readRecords(response);

        if (action != 'read' && Result.success === true) {
            var RetVal = {};

            if (this.meta.totalProperty) {
                RetVal[this.meta.totalProperty] = Result.totalRecords;
            } else {
                RetVal['total'] = Result.totalRecords;
            }

            if (this.meta.successProperty) {
                RetVal[this.meta.successProperty] = Result.success;
            } else {
                RetVal['success'] = Result.success;
            }

            var Data = [];
            for (var I = 0; I < Result.records.length; I++) {
                Data[I] = Result.records[I].data;
            }

            if (this.meta.root) {
                RetVal[this.meta.root] = Data.length == 1 ? Data[0] : Data;
                //NOTE: work around ExtJS bug
                RetVal.data = RetVal[this.meta.root];
            } else {
                RetVal['Entries'] = Data.length == 1 ? Data[0] : Data;
                //NOTE: work around ExtJS bug
                RetVal.data = RetVal['Entries'];
            }

            return RetVal;
        } else {
            return Result;
        }
    },
    
    extractValues: function(data, items, len) {
        var I;

        var values = {};
        for (I in data) {
            values[I] = data[I];
        }

        return values;
    },

    decodeFix : function(response){
        if (!(response.responseText)) {
            throw {
                message: "FIXReader.read: FIX Message not available"
            };
        }

        var Buf = new FIX.CMultiXBuffer();
        this.ResponseMsg = new FIX.CFIXMsg();
        Buf.Append(response.responseText);

        this.ResponseMsg = this.ResponseMsg.FromFIX(Buf);

        if (!this.ResponseMsg) {
            throw {
                message: "FIXReader.read: Invalid FIX Message"
            };
        }
        return this.ResponseMsg;
    },
    
    readRecords: function(response) {
        if(this.ResponseMsg.Header().m_pMsgType === CmFinoFIX.MessageType.JSError){
            if(this.lastTId != response.tId){
               if(this.ResponseMsg.m_pErrorCode !=CmFinoFIX.ErrorCode.NoError){
                    Ext.ux.Toast.msg(_("Error"), this.ResponseMsg.m_pErrorDescription);
                    this.lastTId = response.tId;
                    //mark form invalid
                    if(this.store.form){
                        var errors = {};
                        Ext.each(this.ResponseMsg.Get_Entries(), function(item){
                            errors[item.m_pErrorName] = item.m_pErrorDescription;
                        }, this);
                        this.store.form.getForm().markInvalid(errors);
                    }
               }
               else{
                   Ext.ux.Toast.msg(_("Info"), this.ResponseMsg.m_pErrorDescription);
                   this.lastTId = response.tId;
               }
            }
            return {
                success : false
            };
        }
        if(this.ResponseMsg.m_pErrorDescription && this.ResponseMsg.m_pErrorDescription.length > 0)
        {
        	Ext.ux.Toast.msg(_("Error"), this.ResponseMsg.m_pErrorDescription);
        }

        var recordType = this.recordType, fields = recordType.prototype.fields;
        var totalRecords = 0, success = true;
        var Results = [];
        var ID = null;

        if (this.meta.totalProperty) {
            totalRecords = this.ResponseMsg['m_p' + this.meta.totalProperty];
        } else {
            totalRecords = this.ResponseMsg['m_p' + 'total'];
        }

        if (this.meta.successProperty) {
            success = this.ResponseMsg['m_p' + this.meta.successProperty];
        } else {
            success = this.ResponseMsg['m_p' + 'success'];
        }

        if (this.meta.root) {
            Results = this.ResponseMsg['m_p' + this.meta.root];
        } else {
            Results = this.ResponseMsg['m_p' + 'Entries'];
        }

        var records = [];
        if (Results !== null) {
            for (var I = 0; I < Results.length; I++) {
                var Values = {};
                if (this.meta.idProperty) {
                    ID = Results[I]['m_p' + this.meta.idProperty];
                } else {
                    ID = Results[I]['m_p' + 'ID'];
                }

                for (var J = 0; J < fields.length; J++) {
                    var Field = fields.items[J];
                    if (Results[I]['m_p' + Field.name] !== null && Results[I]['m_p' + Field.name] !== undefined)
                    {
                        Values[Field.name] = Results[I]['m_p' + Field.name].valueOf();
                    }
                    else
                    {
                        Values[Field.name] = null;
                    }
                }
                var record = new recordType(Values, ID);
                records[records.length] = record;
            }
        }
        var RetVal = {};
        RetVal.success = success === 0 ? false : true;
        RetVal.records = records;
        RetVal.totalRecords = totalRecords || records.length;

        return RetVal;
    }
});

