package banking;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.*;
import merchant.MerchantMenuForm;
import mobileBanking.MobileBankingMenuForm;
import subscriber.SubscriberMenuForm;

/**
 * @author Srinu..
 */
public class MobileBankingMidlet extends MIDlet implements CommandListener {

    public Display display;
    private List list;
    private Image subscriberImage, merchantImage, mobileImage;
    static final Command backCommand = new Command("Back", Command.BACK, 0);
    static final Command exitCommand = new Command("Exit", Command.STOP, 1);

    public void startApp() {
        display = Display.getDisplay(this);

        list = new List("Select One", List.IMPLICIT);

        try {
            subscriberImage = Image.createImage("/resources/user.png");
            list.append("Subscriber Menu", subscriberImage);
            merchantImage = Image.createImage("/resources/user-black.png");
            list.append("Merchant Menu", merchantImage);
            mobileImage = Image.createImage("/resources/mobile-phone-off.png");
            list.append("Mobile Banking", mobileImage);
        } catch (IOException ex) {
            Form eform = new Form("Error");
            eform.append("Failed to load images");
        }

        list.addCommand(exitCommand);
        list.setCommandListener(this);
        display.setCurrent(list);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        notifyDestroyed();
    }

    public void commandAction(Command c, Displayable d) {

        String label = c.getLabel();
        if (label.equals("Exit")) {
            destroyApp(true);
        } else if (d == list) {
            List down = (List) display.getCurrent();
            switch (down.getSelectedIndex()) {
                case 0:
                    new SubscriberMenuForm(this, list);
                    break;
                case 1:
                    new MerchantMenuForm(this, list);
                    break;
                case 2:
                    new MobileBankingMenuForm(this, list);
                    break;
            }
        }
    }
}
