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
 */
public class TransferForm implements CommandListener {

    private MFSClientMidlet parent;
    private Display display;
    private Form f, parentForm;
    private TextField amount, bankID, destMDN, destPocketCode, sourceMDN, sourcePIN, sourcePocketCode;
    private StringItem si;
    
    private Command sendCommand = new Command("Send", Command.ITEM, 1);
    private Command backCommand = new Command("Back", Command.BACK, 2);
    private Command exitCommand = new Command("Exit", Command.EXIT, 1);
    
    private Alert alert = null;

    public TransferForm(MFSClientMidlet s, Form pf) {
        parent = s;
        parentForm = pf;
        display = Display.getDisplay(parent);
        f = new Form("Transfer Funds");

        sourceMDN = new TextField("MDN", "", 14, TextField.PHONENUMBER);
        sourcePIN = new TextField("Pin", "", 6, TextField.PASSWORD);
        destMDN = new TextField("Dest MDN", "", 14, TextField.PHONENUMBER);
        amount = new TextField("Amount", "", 10, TextField.ANY);
        bankID = new TextField("Bank ID", "", 14, TextField.PHONENUMBER);
        sourcePocketCode = new TextField("Source Pocket Code", "", 14, TextField.PHONENUMBER);
        destPocketCode = new TextField("Destination Pocket Code", "", 14, TextField.PHONENUMBER);

        f.append(sourceMDN);
        f.append(sourcePIN);
        f.append(destMDN);
        f.append(amount);
        f.append(bankID);
        f.append(sourcePocketCode);
        f.append(destPocketCode);

        f.addCommand(sendCommand);
        f.addCommand(backCommand);
        f.setCommandListener(this);
        display.setCurrent(f);
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
                f = new Form("Subscriber Activation");
                si = new StringItem("", "");
                f.append(si);

                f.addCommand(backCommand);
//                f.addCommand(exitCommand);

                si.setText(str);
                
                // Added for Money Transfer - start
                XMLParser parser = new XMLParser();
                ResponseData res = parser.parse(str);
                
                String transferID = res.getTransferId();
                String parentTID = res.getParentTxnId();

//				  res.getMsg is a html string...
//                parser = new XMLParser();
//                res = parser.parse(res.getMsg()); 
// 				  System.out.println("again parsed output");
//                System.out.println("ParentTransactionID=" + parentTID);
                
                
                System.out.println("TransferId=" + transferID);
                System.out.println("Amount=" + amount.getString().trim());
                
                if (parentTID == null && transferID == null) {
                    si.setText("Code = " + res.getMsgCode() + ": " + res.getMsg());
                } else {
                    url = ConfigurationUtil.getURL() + "?serviceName=transfer" +
                            "&sourceMDN=" + sourceMDN.getString().trim() +
                            "&sourcePIN=" + sourcePIN.getString().trim() +
                            "&destMDN=" + destMDN.getString().trim() +
                            "&amount=" + amount.getString().trim() +
                            "&confirmed=true&mode=3&channelId=7&bankID=" + bankID.getString().trim() + 
                            "&transferId=" + transferID.trim() + "&parentTxnID=" + parentTID.trim() +
                            "&sourcePocketCode=" + sourcePocketCode.getString().trim() +
                            "&destPocketCode="+destPocketCode.getString().trim();

                    System.out.println("Actual transfer URL=" + url);
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
                        res = parser.parse(str);
                        si.setText("Code = " + res.getMsgCode() + ": " + res.getMsg());
                    }
                }
                // Added for Money Transfer - end
                
                f.setCommandListener(this);
                display.setCurrent(f);
                while (str.indexOf(" ") != -1) {
                    str = ConfigurationUtil.replace(" ", "%20", str);
                }
                url = ConfigurationUtil.getSmsUrl() + sourceMDN.getString().substring(2).trim() + "&Message=" + str.trim();

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
                
                /*
                 * TODO: inquiry is done, write code for the actual transfer
                 * 
                 */
                
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
			if (sourceMDN.getString().equals("")
					|| sourcePIN.getString().equals("")
					|| destMDN.getString().equals("")
					|| amount.getString().equals("")
					|| bankID.getString().equals("")
					|| sourcePocketCode.getString().equals("")
					|| destPocketCode.getString().equals("")) {
				
                alert = new Alert("Error", "You should enter all the required fields", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                display.setCurrent(alert);
            } else {
				String url = ConfigurationUtil.getURL() + "?sourceMDN="
						+ sourceMDN.getString().trim() + "&sourcePIN="
						+ sourcePIN.getString().trim()
						+ "&serviceName=transferInquiry&mode=3&channelId=7"
						+ "&sourcePocketCode=" + sourcePocketCode.getString().trim()
						+ "&destMDN=" + destMDN.getString().trim() 
						+ "&bankID=" + bankID.getString().trim() 
						+ "&amount=" + amount.getString().trim()
						+ "&destPocketCode="+destPocketCode.getString().trim();
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
