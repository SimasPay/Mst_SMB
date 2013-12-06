/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.widgets;

import com.mfino.handset.agent.constants.Constants;
import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Font;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.plaf.Border;

/**
 *
 * @author karthik
 */
public class CustomDialogs {

    private CustomDialogs() {
    }

    public static Dialog createInvalidValueEnteredDialog(CustomizedForm presentForm) {
        return createDialog("Please enter valid values",presentForm);
    }
    public static Dialog createInvalidDateOfBirthDialog(CustomizedForm presentForm) {
        return createDialog("Please enter valid date of birth",presentForm);
    }
    
    public static Dialog createWaitingForHTTPResponseDialog() {
        Dialog d = createDialog("Waiting for response",null);
        d.removeAllCommands();
        d.setTimeout(60000);
        d.setAutoDispose(true);
        return d;
    }

    public static Dialog createWrongPinEnteredDialog(CustomizedForm form) {
        return createDialog("You have entered wrong pin",form);
    }

    public static Dialog createInvalidResponseDailog(CustomizedForm form) {
        return createDialog("Error.Invalid response received.",form);
    }
    
    public static Dialog createLoginErrorDialog() {
        return createDialog("An Error occured.Please try after sometime.",null);
    }

    
    public static Dialog createDialog(String text,final CustomizedForm previousForm) {
        final Dialog d = createStyledDialog(text);
        d.setTimeout(4000l);
        d.setAutoDispose(true);
        
        Command cmd = new Command("OK");
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
//                System.out.println("disposing dialog");
                d.dispose();
    
                previousForm.show();
            }
        };
        d.addCommand(cmd);
        d.addCommandListener(al);
//        DialogDisposerThread thread = new DialogDisposerThread(d);
//        thread.start();
//        Timer timer = new Timer();
//        timer.schedule(new DialogDisposer(d), 5000l);
        return d;
    }
    
    private static Dialog createStyledDialog(String text) {
        Dialog waitingDialog = new Dialog();
        waitingDialog.setFocusable(false);
        waitingDialog.getDialogStyle().setBgColor(Constants.COLOUR_SLATEGRAY);
        waitingDialog.getDialogStyle().setBorder(Border.createRoundBorder(29, 20, true));
        waitingDialog.getDialogStyle().setFont(Font.createSystemFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        TextArea ta = new TextArea();
        ta.setSize(new Dimension(50, 100));
        ta.setEditable(false);
        ta.setFocusable(false);
        ta.getStyle().setBorder(Border.createEmpty());
        ta.setText(text);
//        ta.getStyle().setBorder(Border.createRoundBorder(10, 10));
        ta.getStyle().setBgColor(Constants.COLOUR_SLATEGRAY);
        waitingDialog.addComponent(ta);
        waitingDialog.getTitleComponent().getStyle().setBgColor(Constants.COLOUR_SLATEGRAY);
        return waitingDialog;
    }
}
