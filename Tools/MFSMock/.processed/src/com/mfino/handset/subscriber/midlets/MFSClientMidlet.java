package com.mfino.handset.subscriber.midlets;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.mfino.handset.subscriber.ui.ActivationForm;
import com.mfino.handset.subscriber.ui.RechargeForm;
import com.mfino.handset.subscriber.ui.TransferForm;


/**
 * 
 * @author sasidhar
 */
public class MFSClientMidlet extends MIDlet implements CommandListener{

    public Display display;
    private Form mfsClientMidletForm;
    private ChoiceGroup choiceGroup;
    
    static final Command exitCommand = new Command("Exit", Command.STOP, 1);
    private Command startCommand = new Command("Start", Command.ITEM, 1);
    
    private static final String ACTIVATION = "Activation";
    private static final String RECHARGE = "Recharge";
    private static final String TRANSFER = "Transfer";
    
    private static final String[] menu = {ACTIVATION, RECHARGE, TRANSFER};
    
	public MFSClientMidlet() {

	}

    public void destroyApp(boolean unconditional) {
        notifyDestroyed();
    }

	public void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		
		mfsClientMidletForm = new Form("mFino");

		choiceGroup = new ChoiceGroup("Select one", Choice.EXCLUSIVE, menu, null);
		mfsClientMidletForm.append(choiceGroup);

        mfsClientMidletForm.addCommand(exitCommand);
        mfsClientMidletForm.addCommand(startCommand);
        mfsClientMidletForm.setCommandListener(this);

        display.setCurrent(mfsClientMidletForm);
	}

    public void commandAction(Command c, Displayable s) {
    	if (c == startCommand) {
            String name = choiceGroup.getString(choiceGroup.getSelectedIndex());

            if (name.equals(ACTIVATION)) {
                new ActivationForm(this, mfsClientMidletForm);
            } else if (name.equals(RECHARGE)) {
                new RechargeForm(this, mfsClientMidletForm);
            } else if (name.equals(TRANSFER)) {
                new TransferForm(this, mfsClientMidletForm);
            } 
        }
    }

}
