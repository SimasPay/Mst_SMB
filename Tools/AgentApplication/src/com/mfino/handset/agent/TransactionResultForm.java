/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent;

import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
import com.mfino.handset.agent.datacontainers.UserDataContainer;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.Command;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.plaf.Border;
import java.util.Vector;

/**
 *
 * @author karthik
 */
public class TransactionResultForm extends CustomizedForm implements ActionListener {

    private UserDataContainer mfinoConfigdata;

    public TransactionResultForm(UserDataContainer mcd, ResponseDataContainer rd) {
        this.mfinoConfigdata = mcd;
        this.previousForm = mfinoConfigdata.getPreviousFormHolder();
        this.getStyle().setBgColor(Constants.COLOUR_GRAY);
        
        TextArea ta = new TextArea();
        ta.setEditable(false);        
        ta.getStyle().setBorder(Border.createEmpty());
        ta.getStyle().setBgColor(Constants.COLOUR_GRAY);        
        ta.getUnselectedStyle().setBorder(Border.createEmpty());
        ta.getUnselectedStyle().setBgColor(Constants.COLOUR_GRAY);
        
        this.addComponent(ta);

//        Command com1 = new Command("Back", 1);
//        this.addCommand(com1);
        Command com2 = new Command("Home", 2);
        this.addCommand(com2);
        this.addCommandListener(this);

        String text = rd.getMsgCode()+" : "+rd.getMessage();
        ta.setText(text);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand().getId() == 2) {
//            try{
//                String sourceMDN = mfinoConfigdata.getSourceMdn();
//                KeyParameter kp = mfinoConfigdata.getEncryptedAESKey();
//                mfinoConfigdata = new UserDataContainer();
//                mfinoConfigdata.setSourceMdn(sourceMDN);
////                mfinoConfigdata.setAESKey(kp.getKey());
//            if(this.mfinoConfigdata.getHomePage() == null)
//                this.mfinoConfigdata.setHomePage( new ServicesForAgent(mfinoConfigdata));
//            }
//            catch(Exception  ex){
//                
//            }

            try {
                if (this.mfinoConfigdata.getHomePage() == null) {
                    this.mfinoConfigdata.setHomePage(new ServicesForAgent(mfinoConfigdata));
                }
            } catch (Exception ex) {
//                ex.printStackTrace();
            }
            this.mfinoConfigdata.getHomePage().show();
        }
    }
}
