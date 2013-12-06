/*jslint adsafe: false, bitwise: false, browser: true, cap: false, css: false, debug: false, eqeqeq: false, evil: true, forin: true, fragment: false, immed: false, laxbreak: true, newcap: false, nomen: false, on: false, onevar: false, passfail: false, plusplus: false, regexp: false, rhino: false, safe: false, sidebar: false, strict: false, sub: true, undef: false, white: false, widget: false */
/*global Ext: true */

Ext.ns("mFino.util");

mFino.util.fix = function(){

    return {
        //Send a single fix message
        //msgToSend: is the fix message going to be send
        //params: are the parameters
        //params.success: is the success handler with the return fix message as parameter
        //params.failure: is the failure handler without any parameter
        send : function(msgToSend, params){
            var BufToSend = new FIX.CMultiXBuffer();
            msgToSend.ToFIX(BufToSend);

            Ext.Ajax.request({
                url: mFino.DATA_URL,
                method: 'POST',
                xmlData: BufToSend.DataPtr(),
                success: function(response, options) {
                    if(params.success){
                        if (!(response.responseText)) {
                            throw {
                                message: "FIXReader.read: FIX Message not available"
                            };
                        }

                        var Buf = new FIX.CMultiXBuffer();
                        var ResponseMsg = new FIX.CFIXMsg();
                        Buf.Append(response.responseText);

                        ResponseMsg = ResponseMsg.FromFIX(Buf);

                        if (!ResponseMsg) {
                            throw {
                                message: "FIXReader.read: Invalid FIX Message"
                            };
                        }
                        if(params.store) {
                        	if(params.store.lastOptions){
                        		params.store.lastOptions.params[CmFinoFIX.message.JSBase.mfinoaction._name] = CmFinoFIX.JSmFinoAction.Update;
                        		params.store.load(params.store.lastOptions);
                        	}else{
                        		params.store.reload();
                        	}
                        }
                        if(params.CCApproveRejectStore) {
                        	params.CCApproveRejectStore.reload();
                        }

                        params.success(ResponseMsg);
                        if(params.formWindow) {
                            params.formWindow.hide();
                            params.formWindow.form.getForm().reset();
                        }
                    }
                },
                failure: function(response, options) {
                    if(params.failure){
                        params.failure(response, options);
                        if(params.formWindow) {
                            params.formWindow.hide();
                            params.formWindow.form.getForm().reset();
                        }
                    }
                }
            });
        },

        getResetPinMsgFromRecord : function(record){
            var msg= new CmFinoFIX.message.JSResetPin();
            msg.m_pAuthenticationPhrase = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.AuthenticationPhrase._name);
            msg.m_pSourceMDN = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name);
            msg.m_pMSPID = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.MSPID._name);
            msg.m_pServiceName = CmFinoFIX.ServiceName.RESET_PIN;
            msg.m_pNewPin = "111111";
            return msg;
        }, 
        getMerchantResetPinFromRecord: function(record){
            var msg= new CmFinoFIX.message.JSMerchantResetPin();
            msg.m_pAuthenticationPhrase = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.AuthenticationPhrase._name);
            msg.m_pSourceMDN = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.MDN._name);
            msg.m_pMSPID = record.get(CmFinoFIX.message.JSSubscriberMDN.Entries.MSPID._name);
            msg.m_pServiceName = CmFinoFIX.ServiceName.RESET_PIN;
            msg.m_pServletPath = CmFinoFIX.ServletPath.WebAppFEForMerchants;
            msg.m_pNewPin = "111111";
            msg.m_pConfPin = "111111";
            return msg;
        },
        markAsRetry : function(record, comment){
            var msg= mFino.util.fix.fillPendingCommodityTransfer(record);
            msg.m_pCSRAction = 0;
            msg.m_pCSRComment = comment;
            return msg;
        },
        markAsCancel : function(record, comment){
            var msg= mFino.util.fix.fillPendingCommodityTransfer(record);
            msg.m_pCSRAction = CmFinoFIX.CSRAction.Cancel;
            msg.m_pCSRComment = comment;
            return msg;
        },
        markAsComplete : function(record, comment){
            var msg= mFino.util.fix.fillPendingCommodityTransfer(record);
            msg.m_pCSRAction = CmFinoFIX.CSRAction.Complete;
            msg.m_pCSRComment = comment;
            return msg;
        },
        fillPendingCommodityTransfer : function(record){
            var msg= new CmFinoFIX.message.JSPendingCommodityTransferRequest();
            msg.m_pTransferID = record.get(CmFinoFIX.message.JSCommodityTransfer.Entries.ID._name);
            msg.m_pSourceMDN = record.get(CmFinoFIX.message.JSCommodityTransfer.Entries.SourceMDN._name);
            msg.m_pMSPID = record.get(CmFinoFIX.message.JSCommodityTransfer.Entries.MSPID._name);
            msg.m_pTransactionID = record.get(CmFinoFIX.message.JSCommodityTransfer.Entries.TransactionID._name);

            return msg;
        },
        checkGroupIDParent : function(groupIdField, parentId){
            var groupId= groupIdField.getValue();
            var isGroupIdDisabled = groupIdField.disabled;
            if(!isGroupIdDisabled){
                if(!(parentId===null || parentId==="" || parentId===undefined)){
                    var message= new CmFinoFIX.message.JSParentGroupIdCheck();
                    message.m_pID = parentId;
                    var params = mFino.util.showResponse.getMerchantDisplayParam();
                    if((groupId===null || groupId==="")){
                        params.groupId="";
                    }else{
                        params.groupId= groupId;
                    }
                    params.parentId=parentId;
                    mFino.util.fix.send(message, params);
                }else {
                    if(!(groupId===null || groupId==="")){
                        Ext.ux.Toast.msg(_("Error"), "GroupId Should be null, For given ParentID",5);
                        //Ext.getCmp("merchantGroupID").setRawValue("");
                        //groupIdField.setValue("");
                    }else{
                        Ext.getCmp("merchantGroupID").isInvalid_mFino = false;
                    }
                }
            }
        },
        checkNameInDB:function(field, msg,checkForExists){
            var params = mFino.util.showResponse.handleIfFieldExistsInDB(field,checkForExists);
            mFino.util.fix.send(msg, params);
        },
        checkMDNInDB:function(field, msg){
            var params = mFino.util.showResponse.handleIfFieldExistsInDB(field);
            mFino.util.fix.send(msg, params);
        },
        
        // Sorts the given 2D array on the first column
    	sort : function(lst) {
    		var size = lst.length;
    		for (var i=0; i<size; i++) {
    			for (var j=1; j<(size-i); j++) {
    				if (lst[j-1][0] > lst[j][0]) {
    					var tmin = lst[j-1][0];
    					var tmax = lst[j-1][1];
    					lst[j-1][0] = lst[j][0];
    					lst[j-1][1] = lst[j][1];
    					lst[j][0] = tmin;
    					lst[j][1] = tmax;
    				}
    			}
    		}
    		return lst;
    	}
        
    };
}();