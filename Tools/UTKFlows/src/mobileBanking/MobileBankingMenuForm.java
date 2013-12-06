/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobileBanking;

import banking.MobileBankingMidlet;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;

/**
 *
 * @author Srinu..
 */
public class MobileBankingMenuForm implements CommandListener {

    private static Display display;
    private MobileBankingMidlet parent;
    private Form f;
    private List list;
    private ChoiceGroup cg;
    private Command backCommand = new Command("Back", Command.EXIT, 1);
    private Command startCommand = new Command("Start", Command.ITEM, 1);
    private boolean isPaused;

    public boolean isPaused() {
        return isPaused;
    }
    private static final String FUNDS_TRANSFER = "Funds Transfer";
    private static final String TOPUP = "TopUp";
    private static final String CHANGE_PIN = "Change Pin";
    private static final String RESET_PIN = "Reset Pin";
    private static final String BALANCE_INQUIRY = "Balance Inquiry";
    private static final String GET_LAST_TRASACTIONS = "Get Last Transactions";
    private static final String[] menu = {FUNDS_TRANSFER, TOPUP, CHANGE_PIN, RESET_PIN, BALANCE_INQUIRY, GET_LAST_TRASACTIONS};

    public MobileBankingMenuForm(MobileBankingMidlet m, List pl) {
        parent = m;
        list = pl;
        display = Display.getDisplay(parent);
        f = new Form("Mobile Banking Menu");

        cg = new ChoiceGroup("Select one", Choice.EXCLUSIVE, menu, null);
        f.append(cg);

        f.addCommand(backCommand);
        f.addCommand(startCommand);
        f.setCommandListener(this);

        display.setCurrent(f);
    }

    public void startApp() {
        isPaused = false;
    }

    public void pauseApp() {
        isPaused = true;
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable s) {
        if (c == backCommand) {
            display.setCurrent(list);
        } else if (c == startCommand) {
            String name = cg.getString(cg.getSelectedIndex());

            if (name.equals(FUNDS_TRANSFER)) {
                new FundsTransfer(parent, f);
            } else if (name.equals(TOPUP)) {
                new TopUp(parent, f);
            } else if (name.equals(CHANGE_PIN)) {
                new ChangePin(parent, f);
            } else if (name.equals(BALANCE_INQUIRY)) {
                new CheckBalance(parent, f);
            } else if (name.equals(GET_LAST_TRASACTIONS)) {
                new TransactionHistory(parent, f);
            }
        }
    }
}
