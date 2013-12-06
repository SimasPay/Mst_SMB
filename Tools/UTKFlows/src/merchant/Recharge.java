/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package merchant;

import Util.ConfigurationUtil;
import banking.MobileBankingMidlet;
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

/**
 *
 * @author ADMIN
 */
public class Recharge implements CommandListener {

    private MobileBankingMidlet parent;
    private Display display;
    private Form f, parentForm;
    private TextField sourceMdn, destMdn, pin, amount;
    private StringItem si;
    private Command sendCommand = new Command("Send", Command.ITEM, 1);
    private Command backCommand = new Command("Back", Command.BACK, 2);
    private Command exitCommand = new Command("Exit", Command.EXIT, 1);
    private Alert alert = null;

    public Recharge(MobileBankingMidlet m, Form pf) {
        parent = m;
        parentForm = pf;
        display = Display.getDisplay(parent);
        f = new Form("Mobile Agent Recharge");

        sourceMdn = new TextField("Source MDN", "", 14, TextField.PHONENUMBER);
        destMdn = new TextField("Dest MDN", "", 14, TextField.PHONENUMBER);
        pin = new TextField("Pin", "", 6, TextField.PASSWORD);
        amount = new TextField("Amount", "", 9, TextField.NUMERIC);

        f.append(sourceMdn);
        f.append(destMdn);
        f.append(amount);
        f.append(pin);

        f.addCommand(sendCommand);
        f.addCommand(backCommand);
        f.setCommandListener(this);
        display.setCurrent(f);
    }

    private void processRequest(String url) throws IOException {
        HttpConnection http = null;
        InputStream iStrm = null;

        try {
            // Create the connection
            http = (HttpConnection) Connector.open(url);
            http.setRequestMethod(HttpConnection.GET);

            // 2) Send header information (this header is optional)
            http.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Configuration/CLDC-1.0");
//      http.setRequestProperty("If-Modified-Since", "Mon, 16 Jul 2001 22:54:26 GMT");

            // If you experience IO problems, try
            // removing the comment from the following line
            //http.setRequestProperty("Connection", "close");

            // 3) Send body/data - No data for this request
            // Server Response
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
                f = new Form("MobileAgent Recharge");
                si = new StringItem("", "");
                f.append(si);

                f.addCommand(backCommand);
//                f.addCommand(exitCommand);

                si.setText(str);
                f.setCommandListener(this);
                display.setCurrent(f);
                // Show connection information
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
            if (sourceMdn.getString().equals("") || pin.getString().equals("")) {
                alert = new Alert("Error", "You should enter Source Mdn, Dest Mdn, Amount and Pin", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                String url = ConfigurationUtil.getURL() + "SmsServicesServletMA?SMS_sourceMsisdn=" + sourceMdn.getString().trim() + 
                        "&SMS_destMsisdn=" + destMdn.getString().trim() + "&SMS_pin=" + pin.getString().trim() +
                        "&SMS_bucketType=Reg&SMS_rechargeAmount=" + amount.getString().trim() + "&SMS_serviceName=mobileAgentRecharge";
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
