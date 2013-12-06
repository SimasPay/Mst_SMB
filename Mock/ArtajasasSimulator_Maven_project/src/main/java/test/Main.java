/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.*;
import org.w3c.dom.*;
//import org.apache.commons.httpclient.*;
//import org.apache.commons.httpclient.methods.*;
import com.wolvex.Common.*;
import com.wolvex.Iso8583.*;

/**
 *
 * @author Administrator
 */
public class Main extends Thread {

/**
     * @param args the command line arguments
     */

    IsoArtaClient client    = null;
    InputStreamReader isr   = null;
    BufferedReader br       = null;
    String ipAddr           = "10.17.85.14";
    int port                = 9999;
    int timeout             = 30000;

    public static void main(String[] args) {
        // TODO code application logic here
//        System.out.println(sendCharging(initSmppPool()));
        Main main = new Main(args);
        main.start();
    }


    public Main(String[] args) {
        if (args.length > 0) ipAddr  = args[0];
        if (args.length > 1) port    = Integer.parseInt(args[1]);
        if (args.length > 2) timeout = Integer.parseInt(args[2]);
    }
     public Main() {
        }
    @Override
    public void run() {
        try {
            isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);

            client = new IsoArtaClient(ipAddr, port, timeout);
            client.start();

            ISOData data = new ISOData();
            while (true) {
                if (!client.isBound()) {
                    try { this.sleep(1000); } catch (Exception ex) { }                    
                } else {
                    /**
                    System.out.print("===================================================\n1. Topup\n2. Inquiry\n3. Payment\nChoose : ");
                    String s = br.readLine();
                    switch (Integer.parseInt(s)) {
                        case 1:
                            topup(client, data); break;
                        case 2:
                            inquiry(client, data); break;
                        case 3:
                            payment(client, data); break;
                    }*/
                    System.out.print("===================================================\nInput File : ");
                    String s = br.readLine();
                    executeFile(client, s, 200); //break;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.close();
        
    }

    private void assignBit(ISOData data, int bit, String def) throws Exception {
        if (def == null) def = data.getBit(bit);
        System.out.print("DE-" + bit + " [" + def + "] : ");
        String s = br.readLine(); //System.out.print(s);
        if (s.length() > 0) {
            data.setBit(bit, s);
        } else if (def.length() > 0) data.setBit(bit, def);
    }

    public void executeFile(IsoArtaClient client, String xml, int mesagesType) throws Exception {
        System.out.println("Executing file ...");
        ISOData data = new ISOData();
        data.setMessageType(mesagesType);
        data.setBit(7, Functions.now("MMddHHmmss"));
        data.setBit(12, Functions.now("HHmmss"));
        data.setBit(13, Functions.now("MMdd"));
        data.setBit(14, Functions.now("yyMM"));
        data.setBit(15, Functions.now("MMdd"));
        DataDocument  dom = new DataDocument(new ByteArrayInputStream(xml.getBytes()));
        NodeList bits = dom.getDocument().getElementsByTagName("bit");
        for (int i=0;i<bits.getLength();i++) {
            Node node = bits.item(i);
            System.out.println(node.getAttributes().getNamedItem("pos").getNodeValue() + "   " + node.getFirstChild().getNodeValue());
            data.setBit(Integer.parseInt(node.getAttributes().getNamedItem("pos").getNodeValue()),node.getFirstChild().getNodeValue());
            /*data.setBit(Integer.parseInt(node.getAttributes().getNamedItem("pos").getTextContent()),
                    node.getTextContent());            */
        }
        try {
            client.request(data);
        } catch (InterruptedException e) { }
        //ISOData res = client.getResponse(Thread.currentThread());
    /*
        if (res == data) System.out.println("Timeout detected !!!!!!!!!!!!!!!!!!!!!!!");
        while ( true ) {
            System.out.print("Reverse transaction ? (y/n) "); String s = br.readLine();
            if (s.equalsIgnoreCase("y")) reversal(client, res); else break;
        }*/
    }

    private void topup(IsoArtaClient client, ISOData data) throws Exception {
        System.out.println("Topup Request");
        data.setMessageType(200);
        assignBit(data, 2, null);
        data.setBit(3, mFinoParser.PC_VOUCHER_TOPUP);
        assignBit(data, 4, null);
        assignBit(data, 7, Functions.now("MMddHHmmss"));
        assignBit(data, 11, Functions.now("HHmmss"));
        assignBit(data, 12, Functions.now("HHmmss"));
        assignBit(data, 13, Functions.now("MMdd"));
        //assignBit(data, 14, Functions.now("yyMM"));
        assignBit(data, 15, Functions.now("MMdd"));
        assignBit(data, 18, null);
        //assignBit(data, 22, null);
        assignBit(data, 32, null);
        assignBit(data, 37, String.valueOf(System.currentTimeMillis()));
        //assignBit(data, 41, null);
        assignBit(data, 42, null);
        //assignBit(data, 43, null);
        assignBit(data, 48, null);
        assignBit(data, 49, null);
        //assignBit(data, 61, null);
        assignBit(data, 63, null);
      /*  try {
           // client.request(Thread.currentThread(), data);
           // Thread.currentThread().sleep(timeout);
        } catch (InterruptedException e) { }
       // ISOData res = client.getResponse(Thread.currentThread());

        if (res == data) System.out.println("Timeout detected !!!!!!!!!!!!!!!!!!!!!!!");
        while ( true ) {
            System.out.print("Reverse topup ? (y/n) "); String s = br.readLine();
            if (s.equalsIgnoreCase("y")) reversal(client, res); else break;
        }*/
    }

    private void inquiry(IsoArtaClient client, ISOData data) throws Exception {
        System.out.println("Inquiry Request");
        data.setMessageType(200);
        assignBit(data, 2, null);
        data.setBit(3, mFinoParser.PC_BILL_INQUIRY);
        assignBit(data, 4, null);
        assignBit(data, 7, Functions.now("MMddHHmmss"));
        assignBit(data, 11, Functions.now("HHmmss"));
        assignBit(data, 12, Functions.now("HHmmss"));
        assignBit(data, 13, Functions.now("MMdd"));
        assignBit(data, 14, Functions.now("yyMM"));
        assignBit(data, 15, Functions.now("MMdd"));
        assignBit(data, 18, null);
        assignBit(data, 32, null);
        assignBit(data, 37, String.valueOf(System.currentTimeMillis()));
        assignBit(data, 41, null);
        assignBit(data, 43, null);
        assignBit(data, 48, null);
        assignBit(data, 49, null);
        assignBit(data, 61, null);
        assignBit(data, 63, null);
        try {
//            client.request(Thread.currentThread(), data);
            Thread.currentThread().sleep(timeout);
        } catch (InterruptedException e) { }
       // ISOData res = client.getResponse(Thread.currentThread());
    }

    private void payment(IsoArtaClient client, ISOData data) throws Exception {
        System.out.println("Payment Request");
        data.setMessageType(200);
        assignBit(data, 2, null);
        data.setBit(3, mFinoParser.PC_BILL_PAY_CASH);
        assignBit(data, 4, null);
        assignBit(data, 7, Functions.now("MMddHHmmss"));
        assignBit(data, 11, Functions.now("HHmmss"));
        assignBit(data, 12, Functions.now("HHmmss"));
        assignBit(data, 13, Functions.now("MMdd"));
        assignBit(data, 14, Functions.now("yyMM"));
        assignBit(data, 15, Functions.now("MMdd"));
        assignBit(data, 18, null);
        assignBit(data, 32, null);
        assignBit(data, 37, String.valueOf(System.currentTimeMillis()));
        assignBit(data, 41, null);
        assignBit(data, 43, null);
        assignBit(data, 48, null);
        assignBit(data, 49, null);
        assignBit(data, 61, null);
        assignBit(data, 63, null);
        /*try {
            client.request(Thread.currentThread(), data);
            Thread.currentThread().sleep(timeout);
        } catch (InterruptedException e) { }
        ISOData res = client.getResponse(Thread.currentThread());

        if (res == data) System.out.println("Timeout detected !!!!!!!!!!!!!!!!!!!!!!!");
        while ( true ) {
            System.out.print("Reverse payment ? (y/n) "); String s = br.readLine();
            if (s.equalsIgnoreCase("y")) reversal(client, res); else break;
        }*/
    }

    private void reversal(IsoArtaClient client, ISOData request) throws Exception {
        System.out.println("Reversal Request"); ISOData data = new ISOData();
        data.setMessageType(400);
        data.setBit(2, request.getBit(2));
        data.setBit(3, request.getBit(3));
        data.setBit(4, request.getBit(4));
        data.setBit(7, Functions.now("MMddHHmmss"));
        data.setBit(11, request.getBit(11));
        data.setBit(12, Functions.now("HHmmss"));
        data.setBit(13, Functions.now("MMdd"));
        //data.setBit(14, Functions.now("yyMM"));
        data.setBit(15, Functions.now("MMdd"));
        data.setBit(18, request.getBit(18));
        data.setBit(32, request.getBit(32));
        data.setBit(37, request.getBit(37));
        //data.setBit(41, request.getBit(41));
        data.setBit(48, request.getBit(48));
        data.setBit(49, request.getBit(49));
        //data.setBit(61, request.getBit(61));
        data.setBit(63, request.getBit(63));
        data.setBit(90, "0200" + request.getBit(11) + request.getBit(7) + Functions.padZero(request.getBit(32), 11) +
                "00000000000");
        try {
           // client.request(Thread.currentThread(), data);
            Thread.currentThread().sleep(timeout);
        } catch (InterruptedException e) { }
       // ISOData res = client.getResponse(Thread.currentThread());
    }

    /**
    private static final int sendSms(HttpClient smppvas, String src, String dest, String msg) {
        PostMethod post = null; String tgl = null; int status = -255;
        try {
            int h = Integer.parseInt(Functions.now("HH"));
            if (h < 8) {
                tgl = Functions.now("yyyy-MM-dd") + " 06:00:00";
            } else if (h > 22) {
                tgl = Functions.tomorrow("yyyy-MM-dd") + " 06:00:00";
            } else tgl = Functions.now("yyyy-MM-dd HH:mm:ss");

            post = new PostMethod("/submitSM");
            post.setRequestHeader("Content-Type", "text/xml");

            String data = "<smppvas><sms id='" + System.currentTimeMillis() + "' src='6477' dest='88211100120' cback='6477' options='svc_type=CMT;data_coding=0'>test sms 1</sms></smppva>"; //<sms id='125785526' src='ADZAN' dest='088912345675' cback='2324' options='svc_type=CMT;data_coding=0'>test sms 2</sms></smppvas>";
            //    "<smppvas><sms id='125785525' tgl='" + tgl + "' src='" + src + "' dest='" + dest + "' cback='2324' options='svc_type=CMT;data_coding=0'>" + msg + "</sms></smppvas>";
                //"<smppvas><sms src='" + src + "' dest='" + dest + "' tgl='" + tgl + "'>" + msg + "</sms></smppvas>";

            StringRequestEntity body = new StringRequestEntity(data, "text/xml", null);
            post.setRequestEntity(body);

            status = smppvas.executeMethod(post);

        } catch (Exception e) {

        } finally {
            try { post.releaseConnection(); } catch (Exception e) { }
        } return status;
    }*/

    /**
    private static final int sendCharging(HttpClient smppvas) {
        PostMethod post = null; String tgl = null; int status = -255;
        try {
            int h = Integer.parseInt(Functions.now("HH"));
            if (h < 8) {
                tgl = Functions.now("yyyy-MM-dd") + " 06:00:00";
            } else if (h > 22) {
                tgl = Functions.tomorrow("yyyy-MM-dd") + " 06:00:00";
            } else tgl = Functions.now("yyyy-MM-dd HH:mm:ss");

            post = new PostMethod("/PMTInquiryService");
            post.setRequestHeader("Content-Type", "text/xml");

            String data = "<?xml version=\"1.0\"?><pmt><seqid>" + System.currentTimeMillis() + "</seqid>"
                    + "<mdn>8816172752</mdn><provider>00000027</provider><service>000000000040</service>"
                    + "</pmt>";

            StringRequestEntity body = new StringRequestEntity(data, "text/xml", null);
            post.setRequestEntity(body); status = smppvas.executeMethod(post);

            DataDocument doc = new DataDocument(Functions.parseXML(post.getResponseBodyAsStream()));
            System.out.println(doc.convertXML("\t"));

//            sendAck(smppvas,
//                    Functions.findTagValue(doc.getFirstChild(), "ackid"),
//                    Functions.findTagValue(doc.getFirstChild(), "transid"),
//                    null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { post.releaseConnection(); } catch (Exception e) { }
        } return status;
    }*/

    /**
    private static final int sendAck(HttpClient smppvas, String ack, String trx, String pmt) {
        PostMethod post = null; String tgl = null; int status = -255;
        try {
            int h = Integer.parseInt(Functions.now("HH"));
            if (h < 8) {
                tgl = Functions.now("yyyy-MM-dd") + " 06:00:00";
            } else if (h > 22) {
                tgl = Functions.tomorrow("yyyy-MM-dd") + " 06:00:00";
            } else tgl = Functions.now("yyyy-MM-dd HH:mm:ss");

            post = new PostMethod("/PMTAcknowledge");
            post.setRequestHeader("Content-Type", "text/xml");

            String data = "<?xml version=\"1.0\"?><pmt><seqid>2</seqid><ackid>" + ack + "</ackid>" +
                    "<transid>" + trx + "</transid><pmtid>0</pmtid><commit>true</commit>" +
                    "</pmt>";

            StringRequestEntity body = new StringRequestEntity(data, "text/xml", null);
            post.setRequestEntity(body);

            status = smppvas.executeMethod(post);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { post.releaseConnection(); } catch (Exception e) { }
        } return status;
    }*/

    /**
    public static final HttpClient initSmppPool() {
        HttpClient svPool = null;
        try {
            MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
            //manager.getParams().setConnectionTimeout(properties.getInt("smpp.connect.timeout"));
            //manager.getParams().setSoTimeout(properties.getInt("smpp.read.timeout"));
            //manager.getParams().setDefaultMaxConnectionsPerHost(properties.getInt("smpp.max.actv"));
            svPool = new HttpClient(manager);
            svPool.getHostConfiguration().setHost("10.17.87.21",7002);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return svPool;
    }*/
}
