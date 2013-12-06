/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.subscriber.ui;

import com.mfino.handset.subscriber.constants.Constants;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.datacontainers.ResponseDataContainer;
import com.mfino.handset.subscriber.http.WebAPIHTTPWrapper;
/**
 *
 * @author sasidhar
 */
public class BuyForm extends AbstractMfinoConfig implements CommandListener {
    private Displayable parent;
    private Display display;
    
    private Form detailsForm = new Form("Details");
    private TextField code = new TextField("Enter Code", null, 5, TextField.ANY);
    private TextField amount = new TextField("Amount", null, 8, TextField.NUMERIC);
    private TextField message = new TextField("Message", "", 16, TextField.ANY);
    
    private Form pinForm = new Form("Enter Pin");
    private TextField pin = new TextField("PIN", "", 4, TextField.PASSWORD);
    
    private Form confirmForm = new Form("Confirm");
    private StringItem confirmMessage = new StringItem("",""); 
    
    private Form receiptForm = new Form("Confirmation");
    private StringItem receiptMessage = new StringItem("", "");
    
    private String confirmString = "";
    private String receiptString = "";
    private String source = "";
    
    public BuyForm(UserDataContainer mFinoConfigData, Displayable parent){
        super(mFinoConfigData);
	this.parent = parent;
	display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());
        
        detailsForm.append(code);
        detailsForm.append(amount);
        detailsForm.append(message);
        detailsForm.addCommand(mFinoConfigData.nextCommand);
        detailsForm.addCommand(mFinoConfigData.backCommand);
        detailsForm.setCommandListener(this);
        
        pinForm.append(pin);
        pinForm.addCommand(mFinoConfigData.nextCommand);
        pinForm.addCommand(mFinoConfigData.backCommand);
        pinForm.setCommandListener(this);
        
        confirmForm.append(confirmMessage);
        confirmForm.addCommand(mFinoConfigData.continueCommand);
        confirmForm.addCommand(mFinoConfigData.cancelCommand);
        confirmForm.setCommandListener(this);
        
        receiptForm.append(receiptMessage);
        receiptForm.addCommand(mFinoConfigData.homeCommand);
        receiptForm.addCommand(mFinoConfigData.logoutCommand);
        receiptForm.setCommandListener(this);
        
        display.setCurrent(detailsForm);
    }

    public void commandAction(Command command, Displayable displayable) {
    
        if (displayable == detailsForm) {
            if (command == mFinoConfigData.nextCommand) {
                display.setCurrent(pinForm);
            } else if (command == mFinoConfigData.backCommand) {
                display.setCurrent(mFinoConfigData.getMobileBankingMenuDisplay());
            }
        }
        else if (displayable == pinForm) {
            if (command == mFinoConfigData.nextCommand) {
                mFinoConfigData.setMerchantCode(code.getString());
                mFinoConfigData.setAmount(amount.getString());
                mFinoConfigData.setSourcePin(pin.getString());
                
                confirmString = "Do you want to purchase from " + "\n" + "code:" + mFinoConfigData.getMerchantCode() + "\n" + "Amount:" + mFinoConfigData.getAmount();
                confirmMessage.setText(confirmString);
                
                display.setCurrent(confirmForm);
            } else if (command == mFinoConfigData.backCommand) {
                display.setCurrent(detailsForm);
            }
        }
        else if (displayable == confirmForm) {
            if (command == mFinoConfigData.continueCommand) {
                
                //logic for sending http request
                mFinoConfigData.setSourcePin(pin.getString());
                mFinoConfigData.setServiceName(Constants.SERVICE_WALLET);
                mFinoConfigData.setTransactionName(Constants.TRANSACTION_TRANSFER_INQUIRY);
                //logic for sending http request
                WebAPIHTTPWrapper webApiHttpWrapper = new WebAPIHTTPWrapper(mFinoConfigData);
                ResponseDataContainer responseData = webApiHttpWrapper.getResponseData();

                if (responseData.getParentTxnId() == null || responseData.getTransferId() == null) {
                    receiptString = responseData.getMsgCode() + " : " + responseData.getMsg();
                } else {
                    mFinoConfigData.setParentTxnId(responseData.getParentTxnId());
                    mFinoConfigData.setTransferId(responseData.getTransferId());
                    
                    mFinoConfigData.setServiceName(Constants.SERVICE_WALLET);
                    mFinoConfigData.setTransactionName(Constants.TRANSACTION_TRANSFER);
                    mFinoConfigData.setConfirmed(Constants.CONSTANT_VALUE_TRUE);
                    
                    responseData = webApiHttpWrapper.getResponseData();
                
                    mFinoConfigData.setParentTxnId(responseData.getParentTxnId());
                    mFinoConfigData.setTransferId(responseData.getTransferId());

                    receiptString = responseData.getMsgCode() + " : " + responseData.getMsg();

                receiptMessage.setText(receiptString);
                display.setCurrent(receiptForm);
                }
            } 
            else if (command == mFinoConfigData.cancelCommand) {
                display.setCurrent(mFinoConfigData.getMobileBankingMenuDisplay());
            }
        }
        else if (displayable == receiptForm) {
            if (command == mFinoConfigData.homeCommand) {
                display.setCurrent(mFinoConfigData.getMobileBankingMenuDisplay());
            } else if (command == mFinoConfigData.logoutCommand) {
                mFinoConfigData.getMobileBankingMidlet().startApp();
            }
        }        
    }
}
