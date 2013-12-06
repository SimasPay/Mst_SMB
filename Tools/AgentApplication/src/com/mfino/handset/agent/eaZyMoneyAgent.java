package com.mfino.handset.agent;

import com.mfino.handset.agent.services.listeners.IResponseListener;
import com.mfino.handset.agent.services.ServicesForAgent;
import com.mfino.handset.agent.constants.Constants;
import com.mfino.handset.agent.security.CryptoService;
import com.mfino.handset.agent.services.account.activation.AgentActivationForm1;
import java.io.IOException;
import javax.microedition.midlet.*;
import com.sun.lwuit.*;
import com.sun.lwuit.events.*;
import com.mfino.handset.agent.util.StringUtil;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.mfino.handset.agent.http.HttpExecutorThread;
import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
import com.mfino.handset.agent.widgets.CustomLabel;
import com.mfino.handset.agent.widgets.CustomTextField;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.geom.Dimension;
import com.sun.lwuit.layouts.BoxLayout;

/**
 * @author karthik
 */
public class eaZyMoneyAgent extends MIDlet implements ActionListener, IResponseListener {

    private AgentDataContainer agentDataContainer;
    private CustomTextField usernameTextField;
    private CustomTextField passwordTextField;
    CustomizedForm loginForm;

    public void startApp() {

        Display.init(this);
        agentDataContainer = null;

        loginForm = new CustomizedForm();
        loginForm.getStyle().setBgColor(Constants.COLOUR_GRAY);

        Image img = null;
        try {
            img = Image.createImage("/com/mfino/handset/agent/resources/easymoney.png");
        } catch (IOException ex) {
        }
        int h = Display.getInstance().getDisplayHeight();
        int w = Display.getInstance().getDisplayWidth();
        img = img.scaled(w*9/10, h/3);
//        Dimension d = new Dimension(w/2, h/3);
        CustomLabel imgLbl = new CustomLabel(img);
//        imgLbl.setPreferredSize(d);
        BoxLayout layout = new BoxLayout(BoxLayout.Y_AXIS);
        loginForm.setLayout(layout);
        loginForm.addComponent(imgLbl);

        CustomLabel lblMobileNo = new CustomLabel("Mobile Number");
        usernameTextField = new CustomTextField(CustomTextField.PHONENUMBER);
        usernameTextField.setHint("mobile no");

        CustomLabel passwordLabel = new CustomLabel("Pin");
        passwordTextField = new CustomTextField(TextField.PASSWORD);
        passwordTextField.setHint("pin");

        Command loginCommand = new Command("login", 1);
        Command activate = new Command("activation", 2);

        loginForm.addComponent(lblMobileNo);
        loginForm.addComponent(usernameTextField);

        loginForm.addComponent(passwordLabel);
        loginForm.addComponent(passwordTextField);

        loginForm.addCommand(activate);
        loginForm.addCommand(loginCommand);
        loginForm.addCommandListener(this);
        loginForm.show();
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getCommand().getId() == 1) {
            String mdn = usernameTextField.getText();
            String pin = passwordTextField.getText();

            if (StringUtil.isBlank(mdn) || StringUtil.isBlank(pin)) {
                Dialog d = CustomDialogs.createInvalidValueEnteredDialog(this.loginForm);
                d.show();
                return;
            }

            byte[] salt = CryptoService.generateSalt();

            char[] password = null;
            byte[] zeroesBytes = null;
            byte[] encryptedZeroes = null;
            try {
                mdn = StringUtil.normalizeMDN(mdn);
                password = CryptoService.generateHash(mdn, pin);
                zeroesBytes = Constants.ZEROES_STRING.getBytes(Constants.UTF_8);
                encryptedZeroes = CryptoService.encryptWithPBE(zeroesBytes, password, salt, Constants.PBE_ITERATION_COUNT);

            } catch (Exception ex) {
            }
            String hexEncoded4Salt = new String(CryptoService.binToHex(salt));
            String hexEncodedencredZeroes = new String(CryptoService.binToHex(encryptedZeroes));

            agentDataContainer = new AgentDataContainer();
            try {
                agentDataContainer.setSourceMdn(mdn);
                agentDataContainer.setSourcePin(pin);
            } catch (Exception ex) {
            }
            try {
                agentDataContainer.setSalt(hexEncoded4Salt);
                agentDataContainer.setAuthenticationString(hexEncodedencredZeroes);
                agentDataContainer.setServiceName(Constants.SERVICE_ACCOUNT);
                agentDataContainer.setTransactionName(Constants.TRANSACTION_LOGIN);

                HttpExecutorThread thread = new HttpExecutorThread(agentDataContainer, this);
                thread.start();
            } catch (Exception ex) {
            }
            return;
        } else if (ae.getCommand().getId() == 2) {
            agentDataContainer = new AgentDataContainer();
            agentDataContainer.setMobileBankingMidlet(this);
            AgentActivationForm1 form = new AgentActivationForm1(agentDataContainer);
            form.show();
        }
    }

    public void responseReceived(ResponseDataContainer rd) {

        System.out.println("Response received");
        if (rd == null) {
            this.usernameTextField.clear();
            this.passwordTextField.clear();
            this.agentDataContainer = null;
            Dialog d = CustomDialogs.createLoginErrorDialog();
            d.show();
        } else {
            if (parseResponse(rd)) {
                agentDataContainer.setMobileBankingMidlet(this);
                agentDataContainer.setMobileBankingMenuDisplay(Display.getInstance());
                ServicesForAgent form = new ServicesForAgent(agentDataContainer);
                try {
                    agentDataContainer.setSourceMdn(StringUtil.normalizeMDN(usernameTextField.getText()));
                    agentDataContainer.setHomePage(form);
                } catch (Exception ex) {
                }
                agentDataContainer.setLoginResult(true);
                form.show();
            } else {
                agentDataContainer = null;
                if (rd.getMsgCode().equals(Constants.NOTIFICATIONCODE_WRONGPINSPECIFIED)) {
                    this.usernameTextField.clear();
                    this.passwordTextField.clear();
                    final Dialog d = CustomDialogs.createWrongPinEnteredDialog(loginForm);
                    d.show();
                    return;
                } else {
                    this.usernameTextField.clear();
                    this.passwordTextField.clear();
                    String msg = null;
                    if (rd.getErrorMsg() != null) {
                        msg = rd.getErrorMsg();
                    } else {
                        msg = rd.getMessage();
                    }
                    msg = rd.getMsgCode() + " : " + msg;
                    final Dialog d = CustomDialogs.createDialog(msg, loginForm);
                    d.show();
                }
            }
        }
    }

    private boolean parseResponse(ResponseDataContainer rs) {

        try {
            byte[] salt = CryptoService.hexToBin(rs.getSalt().toCharArray());
            byte[] encryptedZeroes = CryptoService.hexToBin(rs.getAuthenticationString().toCharArray());
            byte[] encryptedKey = CryptoService.hexToBin(rs.getEncryptedAESKey().toCharArray());
            String pinHash = new String(CryptoService.generateHash(agentDataContainer.getSourceMdn(), agentDataContainer.getSourcePin()));
            byte[] aesKey = CryptoService.decryptWithPBE(encryptedKey, pinHash.toCharArray(), salt, Constants.PBE_ITERATION_COUNT);
            byte[] decryptedZeroes = CryptoService.decryptWithAES(aesKey, encryptedZeroes);
            String str = new String(decryptedZeroes, Constants.UTF_8);
            if (Constants.ZEROES_STRING.equals(str)) {
                agentDataContainer.setAESKey(aesKey);
                return true;
            }
        } catch (Exception ex) {
        }
        agentDataContainer = null;
        return false;
    }
}