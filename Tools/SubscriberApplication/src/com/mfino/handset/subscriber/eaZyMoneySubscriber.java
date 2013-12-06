package com.mfino.handset.subscriber;

import com.mfino.handset.subscriber.security.CryptoService;
import com.mfino.handset.subscriber.constants.Constants;
import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import com.mfino.handset.subscriber.ui.MfinoConfig;
import com.mfino.handset.subscriber.ui.MobileBankingMenu;
import com.mfino.handset.subscriber.ui.youraccount.activation.ActivationMdnForm1;
import com.mfino.handset.subscriber.util.ConfigurationUtil;
import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
import com.mfino.handset.subscriber.datacontainers.ResponseDataContainer;
import com.mfino.handset.subscriber.http.WebAPIHTTPWrapper;

/**
 * @author sasidhar
 */
public class eaZyMoneySubscriber extends MIDlet implements CommandListener, MfinoConfig {

    private UserDataContainer userDataContainer;
    public Display display;
    private Form loginForm;
    private StringItem stringItem;
    private TextField tfPhoneNumber;
    private TextField tfMpin;
    private Image mFinoLogo;
    private Alert alert;

    public void startApp() {


        loginForm = new Form("Login");
        userDataContainer = new UserDataContainer();
        userDataContainer.setMobileBankingMidlet(this);


        display = Display.getDisplay(this);

        stringItem = new StringItem("", "");

        try {
//            mFinoLogo = Image.createImage("/com/mfino/handset/resources/zenith.png");
//            mFinoLogo = Image.createImage("/com/mfino/handset/resources/ezmoney.jpg");
            mFinoLogo = Image.createImage("/com/mfino/handset/resources/ezm1.jpg");
        } catch (IOException e) {
//            e.printStackTrace();
        }
        tfPhoneNumber = new TextField("Mobile Number", "", 13, TextField.PHONENUMBER);
        tfMpin = new TextField("Pin", null, 4, TextField.PASSWORD | TextField.NUMERIC);
        loginForm.append(mFinoLogo);
        loginForm.append(tfPhoneNumber);
        loginForm.append(tfMpin);

        loginForm.addCommand(userDataContainer.loginCommand);
        loginForm.addCommand(userDataContainer.activationCommand);
        loginForm.addCommand(userDataContainer.exitCommand);
        loginForm.setCommandListener(this);
        display.setCurrent(loginForm);
    }

    public void pauseApp() {
        pauseApp();
    }

    public void destroyApp(boolean unconditional) {
        notifyDestroyed();
    }

    public void commandAction(Command command, Displayable displayable) {

        if (command == userDataContainer.loginCommand) {
            if (ConfigurationUtil.isBlank(tfPhoneNumber.getString()) || ConfigurationUtil.isBlank(tfMpin.getString())) {
                alert = new Alert("Error", "Please enter required fields", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {

                byte[] salt = CryptoService.generateSalt();
                char[] password = null;
                byte[] zeroesBytes = null;
                byte[] encryptedZeroes = null;
                String normalizedMDN  = null;
                try {
                    normalizedMDN = ConfigurationUtil.normalizeMDN(tfPhoneNumber.getString());
                    password = CryptoService.generateHash(normalizedMDN, tfMpin.getString());
                    zeroesBytes = Constants.ZEROES_STRING.getBytes(Constants.UTF_8);
                    encryptedZeroes = CryptoService.encryptWithPBE(zeroesBytes, password, salt, Constants.PBE_ITERATION_COUNT);
                } catch (Exception ex) {
                }
                String hexEncoded4Salt = new String(CryptoService.binToHex(salt));
                String hexEncodedencredZeroes = new String(CryptoService.binToHex(encryptedZeroes));

//                System.out.println(hexEncoded4Salt);
//                System.out.println(hexEncodedencredZeroes);

                try {
                    userDataContainer.setSourceMdn(normalizedMDN);
                    userDataContainer.setSourcePin(tfMpin.getString().trim());
                } catch (Exception ex) {
                }
                userDataContainer.setSalt(hexEncoded4Salt);
                userDataContainer.setAuthenticationString(hexEncodedencredZeroes);
                userDataContainer.setServiceName(Constants.SERVICE_ACCOUNT);
                userDataContainer.setTransactionName(Constants.TRANSACTION_LOGIN);

                WebAPIHTTPWrapper wrapper = new WebAPIHTTPWrapper(userDataContainer);
                ResponseDataContainer rd = wrapper.getResponseData();

                String[] result = parseResponse(rd);
                if ("yes".equals(result[0])) {
                    new MobileBankingMenu(userDataContainer, displayable);
                } else if (Constants.NOTIFICATIONCODE_WRONGPINSPECIFIED.equals(result[0])) {
                    tfMpin.setString("");
                    tfPhoneNumber.setString("");
                    alert = new Alert("Error", "You have entered a wrong pin", null, AlertType.ERROR);
                    alert.setTimeout(Alert.FOREVER);
                    display.setCurrent(alert);
                } else {
                    tfMpin.setString("");
                    tfPhoneNumber.setString("");
                    alert = new Alert("Error", result[1], null, AlertType.ERROR);
                    alert.setTimeout(Alert.FOREVER);
                    display.setCurrent(alert);
                }
            }
        } else if (command == userDataContainer.activationCommand) {
            new ActivationMdnForm1(userDataContainer, displayable);
        } else if (command == userDataContainer.exitCommand) {

            userDataContainer.setSourceMdn(null);
            userDataContainer.setSourcePin(null);
            userDataContainer.setAESKey(null);

            userDataContainer.getMobileBankingMidlet().destroyApp(true);
            userDataContainer.getMobileBankingMidlet().notifyDestroyed();
        }
    }

    public void setConfigData(UserDataContainer mFinoConfigData) {
        this.userDataContainer = mFinoConfigData;
    }

    public UserDataContainer getMfinoConfigData() {
        return this.userDataContainer;
    }

    private String[] parseResponse(ResponseDataContainer rs) {

        try {
            String normalizedMDN = ConfigurationUtil.normalizeMDN(tfPhoneNumber.getString());
            byte[] salt = CryptoService.hexToBin(rs.getSalt().toCharArray());
            byte[] encryptedZeroes = CryptoService.hexToBin(rs.getAuthenticationString().toCharArray());
            byte[] encryptedKey = CryptoService.hexToBin(rs.getAESKey().toCharArray());
            String pinHash = new String(CryptoService.generateHash(normalizedMDN, tfMpin.getString()));
            byte[] aesKey = CryptoService.decryptWithPBE(encryptedKey, pinHash.toCharArray(), salt, Constants.PBE_ITERATION_COUNT);
            byte[] decryptedZeroes = CryptoService.decryptWithAES(aesKey, encryptedZeroes);
            String str = new String(decryptedZeroes, Constants.UTF_8);
            if (Constants.ZEROES_STRING.equals(str)) {
                userDataContainer.setAESKey(aesKey);
                userDataContainer.setSourceMdn(normalizedMDN);
                userDataContainer.setSourcePin(tfMpin.getString());
                String[] ret = {"yes",rs.getMsg()};
                return ret;
            }
        } catch (Exception ex) {
        }
        userDataContainer.setSourceMdn(null);
        userDataContainer.setSourcePin(null);
        userDataContainer.setSalt(null);
        userDataContainer.setAuthenticationString(null);
        userDataContainer.setServiceName(null);
        userDataContainer.setTransactionName(null);
        String[] ret1 = {rs.getMsgCode(),rs.getMsg()};
        return ret1;
    }
}
