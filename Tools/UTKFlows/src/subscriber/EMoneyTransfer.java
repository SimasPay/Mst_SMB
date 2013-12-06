/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package subscriber;

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
 * @author Srinu
 */
public class EMoneyTransfer implements CommandListener {

    private MobileBankingMidlet parent;
    private Display display;
    private Form f, parentForm;
    private TextField sourceMdn, destMdn, pin, amount, message;
    private StringItem si;
    private Command sendCommand = new Command("Send", Command.ITEM, 1);
    private Command backCommand = new Command("Back", Command.BACK, 2);
    private Command exitCommand = new Command("Exit", Command.EXIT, 1);
    private Alert alert = null;

    public EMoneyTransfer(MobileBankingMidlet s, Form pf) {
        parent = s;
        parentForm = pf;
        display = Display.getDisplay(parent);
        f = new Form("Subscriber E-Money Transfer");

        sourceMdn = new TextField("Source MDN", "", 14, TextField.PHONENUMBER);
        destMdn = new TextField("Dest MDN", "", 14, TextField.PHONENUMBER);
        pin = new TextField("Pin", "", 6, TextField.PASSWORD);
        amount = new TextField("Amount", "", 9, TextField.NUMERIC);
        message = new TextField("Message", "", 20, TextField.ANY);

        f.append(sourceMdn);
        f.append(destMdn);
        f.append(message);
        f.append(amount);
        f.append(pin);

        f.addCommand(backCommand);
        f.addCommand(sendCommand);
        f.setCommandListener(this);
        display.setCurrent(f);
    }

    public void commandAction(Command c, Displayable d) {
        if ((c == sendCommand)) {
            if (sourceMdn.getString().equals("") || pin.getString().equals("") || destMdn.getString().equals("") || 
                    amount.getString().equals("") || message.getString().equals("")) {
                alert = new Alert("Error", "You should enter Source Mdn, Dest Mdn, Amount, Message and Pin", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
                String url = ConfigurationUtil.getURL() + "SmsMCashServlet?SMS_sourceMsisdn=" + sourceMdn.getString().trim() +
                        "&SMS_destMsisdn=" + destMdn.getString().trim() + "&SMS_mCashPin=" + pin.getString().trim() +
                        "&SMS_amount=" + amount.getString().trim() + "&SMS_mCashMessage=" + message.getString().trim() +
                        "&SMS_serviceName=mcash_to_mcash";
                try {
                    processRequest(url);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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

    private void processRequest(String url) throws IOException {
        HttpConnection http = null;
        InputStream iStrm = null;

        try {
            // Create the connection
            http = (HttpConnection) Connector.open(url);

            //----------------
            // Client Request
            //----------------
            // 1) Send request method
            http.setRequestMethod(HttpConnection.GET);

            // 2) Send header information (this header is optional)
            http.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Configuration/CLDC-1.0");
//      http.setRequestProperty("If-Modified-Since", "Mon, 16 Jul 2001 22:54:26 GMT");

            // If you experience IO problems, try
            // removing the comment from the following line
            //http.setRequestProperty("Connection", "close");

            // 3) Send body/data - No data for this request


            //----------------
            // Server Response
            //----------------
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
                String str = "";
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

                f = new Form("E-Money Transfer");
                si = new StringItem("", "");
                f.append(si);

                f.addCommand(backCommand);
//                f.addCommand(exitCommand);

                String transferID = null;
                String parentTID = null;
                if (str.indexOf("ParentTransactionID=") != -1) {
                    parentTID = str.substring(str.indexOf("ParentTransactionID=") + "ParentTransactionID=".length());
                    System.out.println("ParentTransactionID=" + parentTID);
                }

                if (str.indexOf("TransferID=") != -1 && str.indexOf("ParentTransactionID") != -1) {
                    transferID = str.substring(str.indexOf("TransferID=") + "TransferID=".length(), str.indexOf("ParentTransactionID"));
                    System.out.println("TransactionID=" + transferID);
                }

                if (parentTID == null && transferID == null) {
                    si.setText(str);
                } else {
                    url = ConfigurationUtil.getURL() + "SmsMCashServlet?SMS_serviceName=BankAccountToBankAccountConfirmation" +
                            "&SMS_sourceMsisdn=" + sourceMdn.getString().trim() +
                            "&Confirmed=1&SMS_destMsisdn=" + destMdn.getString().trim() + "&TransferID=" + transferID.trim() + "&ParentTransactionID=" + parentTID.trim();

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

                        System.out.println("File Contents: " + str);
                        if (str.indexOf("<body>") != -1 && str.indexOf("</body>") != -1) {
                            str = str.substring(str.indexOf("<body>") + "<body>".length(), str.indexOf("</body>"));
                        }
                        si.setText(str);
                    }
                }

                f.setCommandListener(this);
                display.setCurrent(f);
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
}
