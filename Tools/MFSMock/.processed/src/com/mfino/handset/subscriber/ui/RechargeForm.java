package com.mfino.handset.subscriber.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import com.mfino.handset.subscriber.midlets.MFSClientMidlet;
import com.mfino.handset.subscriber.util.ConfigurationUtil;
import com.mfino.handset.subscriber.util.ResponseData;
import com.mfino.handset.subscriber.util.XMLParser;



/**
 * @author sasidhar
 *
 */
public class RechargeForm implements CommandListener {

    private MFSClientMidlet parent;
    private Display display;
    private Form rechargeForm, parentForm;
    private TextField sourceMdn, sourcePin, destinationMdn, amount, bucketType, bankId, sourcePocketCode;
    private StringItem si;
    
    private Command sendCommand = new Command("Send", Command.ITEM, 1);
    private Command backCommand = new Command("Back", Command.BACK, 2);
    private Command exitCommand = new Command("Exit", Command.EXIT, 1);
    
    private Alert alert = null;

    public RechargeForm(MFSClientMidlet s, Form pf) {
        parent = s;
        parentForm = pf;
        display = Display.getDisplay(parent);
        rechargeForm = new Form("Recharge");

        sourceMdn = new TextField("MDN", "", 14, TextField.PHONENUMBER);
        sourcePin = new TextField("Pin", "", 6, TextField.PASSWORD);
        destinationMdn = new TextField("Dest MDN", "", 14, TextField.PHONENUMBER);
        amount = new TextField("Amount", "", 10, TextField.DECIMAL);
        bucketType = new TextField("Bucket Type", "", 10, TextField.NUMERIC);
        bankId = new TextField("BankId", "", 10, TextField.DECIMAL);
        sourcePocketCode = new TextField("Pocket Code", "", 10, TextField.NUMERIC);

        rechargeForm.append(sourceMdn);
        rechargeForm.append(sourcePin);
        rechargeForm.append(destinationMdn);
        rechargeForm.append(amount);
        rechargeForm.append(bucketType);
        rechargeForm.append(bankId);
        rechargeForm.append(sourcePocketCode);

        rechargeForm.addCommand(sendCommand);
        rechargeForm.addCommand(backCommand);
        rechargeForm.setCommandListener(this);
        display.setCurrent(rechargeForm);
    }

    private void processRequest(String url) throws IOException {
        HttpConnection http = null;
        InputStream iStrm = null;

        try {
            http = (HttpConnection) Connector.open(url);
            http.setRequestMethod(HttpConnection.GET);
            http.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Configuration/CLDC-1.0");

            System.out.println("url: " + url);
            System.out.println("-------------------------");

            // 1) Get status Line
            System.out.println("Msg: " + http.getResponseMessage());
            System.out.println("Code: " + http.getResponseCode());

            // 2) Get header information
            if (http.getResponseCode() == HttpConnection.HTTP_OK) {
                System.out.println("field 0: " + http.getHeaderField(0));
                System.out.println("field 1: " + http.getHeaderField(1));
                System.out.println("field 2: " + http.getHeaderField(2));
                System.out.println("-------------------------");

                System.out.println("key 0: " + http.getHeaderFieldKey(0));
                System.out.println("key 1 : " + http.getHeaderFieldKey(1));
                System.out.println("key 2: " + http.getHeaderFieldKey(2));
                System.out.println("-------------------------");

                System.out.println("content: " + http.getHeaderField("content-type"));
                System.out.println("content length: " + http.getHeaderField("content-length"));
                System.out.println("date: " + http.getHeaderField("date"));
                System.out.println("last-modified: " + http.getHeaderField("last-modified"));

                System.out.println("-------------------------");

                // 3) Get data (show the file contents)
                String str;
                iStrm = http.openInputStream();  // open and return an input stream for connection
                int length = (int) http.getLength();
                System.out.println(length);
                if (length != -1) {
                    // Read data in one chunk
                    byte serverData[] = new byte[length];
                    iStrm.read(serverData);
                    str = new String(serverData);
                } else // Length not available...
                {
                    ByteArrayOutputStream bStrm = new ByteArrayOutputStream();

                    // Read data one character at a time
                    int ch;
                    while ((ch = iStrm.read()) != -1) {
                        bStrm.write(ch);
                    }

                    str = new String(bStrm.toByteArray());
                    bStrm.close();
                }

                System.out.println("File Contents: " + str);
                if (str.indexOf("<body>") != -1 && str.indexOf("</body>") != -1) {
                    str = str.substring(str.indexOf("<body>") + "<body>".length(), str.indexOf("</body>"));
                }
                rechargeForm = new Form("Subscriber Activation");
                si = new StringItem("", "");
                rechargeForm.append(si);

                rechargeForm.addCommand(backCommand);
//                f.addCommand(exitCommand);

                //Added for XML Parsing
                XMLParser parser = new XMLParser();
                ResponseData res = parser.parse(str);
                str = "Code = " + res.getMsgCode() + ": " + res.getMsg();
                
                si.setText(str);
                rechargeForm.setCommandListener(this);
                display.setCurrent(rechargeForm);
                while (str.indexOf(" ") != -1) {
                    str = ConfigurationUtil.replace(" ", "%20", str);
                }
                url = ConfigurationUtil.getSmsUrl() + sourceMdn.getString().substring(2).trim() + "&Message=" + str.trim();

                System.out.println("URL=" + url);
                http = (HttpConnection) Connector.open(url);

                http.setRequestMethod(HttpConnection.GET);
                http.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Configuration/CLDC-1.0");
                if (http.getResponseCode() == HttpConnection.HTTP_OK) {
                    iStrm = http.openInputStream();  // open and return an input stream for connection
                    length = (int) http.getLength();
                    System.out.println(length);
                    if (length != -1) {
                        // Read data in one chunk
                        byte serverData[] = new byte[length];
                        iStrm.read(serverData);
                        str = new String(serverData);
                    } else // Length not available...
                    {
                        ByteArrayOutputStream bStrm = new ByteArrayOutputStream();

                        // Read data one character at a time
                        int ch;
                        while ((ch = iStrm.read()) != -1) {
                            bStrm.write(ch);
                        }

                        str = new String(bStrm.toByteArray());
                        bStrm.close();
                    }
                }
                System.out.println("SMS Response: " + str);
                //-----------------------------
                // Show connection information
                //-----------------------------
                System.out.println("Host: " + http.getHost());
                System.out.println("Port: " + http.getPort());
                System.out.println("Type: " + http.getType());

                System.out.println("File: " + http.getFile());
                System.out.println("Protocol: " + http.getProtocol());
                System.out.println("URL: " + http.getURL());
                System.out.println("Query: " + http.getQuery());
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            // Clean up
            if (iStrm != null) {
                iStrm.close();
            }
            if (http != null) {
                http.close();
            }
        }
    }

    public void commandAction(Command c, Displayable d) {
        if ((c == sendCommand)) {
            if (sourceMdn.getString().equals("") || sourcePin.getString().equals("") || destinationMdn.getString().equals("") || amount.getString().equals("") || bucketType.getString().equals("") || bankId.getString().equals("") || sourcePocketCode.getString().equals("")) {
                alert = new Alert("Error", "You should enter all the required fields", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
				String url = ConfigurationUtil.getURL() 
						+ "?sourceMDN=" + sourceMdn.getString().trim() 
						+ "&sourcePIN=" + sourcePin.getString().trim()
						+ "&serviceName=recharge&mode=3&channelId=7"
						+ "&sourcePocketCode=" + sourcePocketCode.getString().trim() 
						+ "&destMDN=" + destinationMdn.getString().trim() 
						+ "&bucketType=" + bucketType.getString().trim()
						+ "&bankID=" + bankId.getString().trim() 
						+ "&amount=" + amount.getString().trim();
                try {
                    processRequest(url);
                } catch (Exception e) {
                    System.err.println("Msg: " + e.toString());
                }
            }
        }
        if (c == exitCommand) {
            parent.destroyApp(true);
            parent.notifyDestroyed();
        }
        if (c == backCommand) {
            display.setCurrent(parentForm);
        }
    }
}
