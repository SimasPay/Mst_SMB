/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.ns("mFino.util");

mFino.util.showResponse = function(){

    return {
        getDisplayParam : function(){
            var params = {
                success : function(response){
                	if(response.m_pErrorCode === CmFinoFIX.ErrorCode.NoError){
                        Ext.Msg.show({
                            title: 'Info',
                            minProgressWidth:600,
                            msg: response.m_pErrorDescription,
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });

                	}else if(response.m_pErrorCode < 0){
                        Ext.Msg.show({
                            title: 'Notification',
                            minProgressWidth:600,
                            msg: 'Unsupported Operation',
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });

                    }else{
                        Ext.Msg.show({
                            title: 'Error',
                            minProgressWidth:600,
                            msg: response.m_pErrorDescription,
                            buttons: Ext.MessageBox.OK,
                            multiline: false
                        });
                    }
                },
                failure : function(response){
                    Ext.Msg.show({
                        title: 'Error',
                        minProgressWidth:250,
                        msg: "Your transaction is having a problem. Please check your recent transaction on pending transaction list or contact Customer Care :881",
                        buttons: Ext.MessageBox.OK,
                        multiline: false
                    });
                }
            };
            return params;
        },
        handleReverseTransaction : function(){
            var params = {
                success : function(response){
                	if(response.m_pErrorCode!== CmFinoFIX.ErrorCode.NoError){
                		Ext.ux.Toast.msg(_("Error"), response.m_pErrorDescription,5);
                        return false;
                    }
                    return true;
                },
                failure : function(response){
                    return true;
                }
            };
            return params;
        },
        handleIfFieldExistsInDB : function(field, checkForExists){
            var params = {
                success : function(response){
                	if(response.m_pErrorCode!== CmFinoFIX.ErrorCode.NoError){
                        if(checkForExists){
                        	Ext.ux.Toast.msg(_("Error"), response.m_pErrorDescription,5);
                            field.setRawValue("");
                            return false;
                        }
                        else{
                        	Ext.ux.Toast.msg(_("Error"), response.m_pErrorDescription,5);
                            field.setRawValue("");
                            return false;
                        }
                    }
                    return true;
                },
                failure : function(response){
                    //We don't care if for some reason we are not able to check the response
                    //Server will throw a error if the username already exists
                    return true;
                }
            };
            return params;
        },
        getMerchantDisplayParam : function(){
            var params = {
                success :  function(response){
            		Ext.getCmp("merchantGroupID").isInvalid_mFino = false;            	
            		if(response.m_pAllowedForLOP){
                        if(this.groupId===null || this.groupId===""){
                            Ext.getCmp("merchantGroupID").markInvalid("Field cannot be null, For given Parent ID");
                            Ext.getCmp("merchantGroupID").isInvalid_mFino = true;
                            Ext.ux.Toast.msg(_("Error"), "GroupId cannot be null",5);
                        }
                    }else {
                        if(!(this.groupId===null || this.groupId==="")){
                            Ext.getCmp("merchantGroupID").markInvalid("GroupId Should be null, For given ParentID");
                            Ext.ux.Toast.msg(_("Error"), "GroupId Should be null, For given ParentID",5);
                            Ext.getCmp("merchantGroupID").isInvalid_mFino = true;
                            //Ext.getCmp("merchantGroupID").setValue("");
                            //Ext.getCmp
                        }
                    }

                }
            };
            return params;
        }
    };
}();
