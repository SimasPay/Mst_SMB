package com.mfino.handset.subscriber.ui;

import com.mfino.handset.subscriber.constants.Constants;
import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import com.mfino.handset.subscriber.ui.youraccount.YourAccountMenu;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.ui.buy.PartnerCodeForm1;
import com.mfino.handset.subscriber.ui.manageyourmoney.transfers.Transfers;

/**
 * @author sasidhar
 *
 */
public class MobileBankingMenu extends AbstractMfinoConfig implements CommandListener {

    public Display display;
    private List list;
    private Image imgRightArrow;

    public MobileBankingMenu(UserDataContainer mfinoConfigData, Displayable displayable) {
        super();
        this.mFinoConfigData = mfinoConfigData;
        display = Display.getDisplay(mFinoConfigData.getMobileBankingMidlet());

        list = new List("Select One", List.IMPLICIT);

        try {
            imgRightArrow = Image.createImage("/com/mfino/handset/resources/rightarrow.png");

            list.append(Constants.FEATURE_TRANSFER, imgRightArrow);
            list.append(Constants.FEATURE_BUY, imgRightArrow);
            list.append(Constants.FEATURE_MYACCOUNT, imgRightArrow);

        } catch (IOException ex) {
            Form eform = new Form("Error");
            eform.append("Failed to load images");
        }

        list.addCommand(mfinoConfigData.exitCommand);
        list.setCommandListener(this);
        display.setCurrent(list);
    }

    public void commandAction(Command command, Displayable displayable) {

        mFinoConfigData.setMobileBankingMenuDisplay(displayable);

        if (command == mFinoConfigData.exitCommand) {
            mFinoConfigData.setAESKey(null);
            mFinoConfigData.setSourcePin(null);
            mFinoConfigData.setSourceMdn(null);
            mFinoConfigData.getMobileBankingMidlet().startApp();
        } else if (displayable == list) {
            List down = (List) display.getCurrent();
            switch (down.getSelectedIndex()) {
                case 0:
                    new Transfers(mFinoConfigData, displayable);
                    break;
                case 1:
                    new PartnerCodeForm1(mFinoConfigData, displayable);
                    break;
                case 2:
                    new YourAccountMenu(mFinoConfigData, displayable);
                    break;
            }
        }
    }
}
