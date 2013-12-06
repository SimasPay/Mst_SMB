/*
 * EsmeInterface.java
 *
 * Created on September 15, 2006, 10:27 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package test;

/**
 *
 * @author HIBRIDZI
 */
import java.io.*;
import java.net.*;
import java.util.*;
import com.wolvex.Iso8583.*;
import com.wolvex.Common.*;

public class IsoZTEClient extends Thread {
    private Socket ccbs                 = null;
    private DataInputStream in          = null;
    private DataOutputStream out        = null;
    private String address              = null;
    private ISOProfile profile          = null;
    private mFinoParser parser          = null;
    private Hashtable requests          = null;

    private boolean run                 = true;
    private boolean bound               = false;
    private long lastPacket             = 0;
    private int timeout                 = 30000;
    private int port                    = 0;

    /** Creates a new instance of EsmeInterface */
    public IsoZTEClient(String address, int port, int timeout) {
        this.address    = address;
        this.port       = port;
        this.timeout    = timeout;
    }

    /**
    public static byte[] readISO(InputStream input) throws SocketTimeoutException, SocketException, IOException, Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = Functions.read(input, 4); bos.write(b); 
        if (bos.size() == 4) {
            String len = Functions.bytesToString(b);
            bos.write(Functions.read(input, Integer.parseInt(len)));
            return bos.toByteArray();
        } else throw new Exception("Invalid ISO-8583 packet length header");
    }

    //@Override
    public static String parseISO(byte[] data) throws ParseException, Exception {
        byte[] length = new byte[4];
        //copy array data dari posisi 0 ke array length posisi 0 sebanyak 2 byte
        System.arraycopy(data, 0, length, 0, 4);
        int len = Integer.parseInt(Functions.bytesToString(length));
        //jika panjang data - 2 (byte length) tidak sama dengan nilai decimal array length, maka tidak valid
        if ((data.length - 4) != len) throw new ParseException("Invalid data length", -1);
        //jika valid, convert byte data menjadi string dengan table US-ASCII,
        //dimulai dari posisi array ke 2 sepanjang data dikurangi byte length (2)
        return new String(data, 4, data.length - 4, "US-ASCII");
    }*/

    @Override
    public void run() {
        System.out.println(toString() + " Starts ...");

        try {
            profile  = new ISOProfile("mFino.xml", null, null);
            parser   = new mFinoParser(profile);
            requests = new Hashtable();

            connect();

            while ( run ) {
                try {
                    byte[] b        = parser.read(in);
                    String iso      = parser.parse(b);
                    ISOData data    = parser.parse(iso);

                    lastPacket = System.currentTimeMillis();

                    System.out.println(toString() + " receives {\n" + iso + "\n" + data.print() + "}");

                    switch (data.getMessageType()) {
                        case mFinoParser.TR_NETWORK:
                            break;
                        case mFinoParser.TR_NETWORK_RESP:
                            bound = (Integer.parseInt(data.getBit(39)) == mFinoParser.RC_APPROVED);
                            break;
                        default:
                            response(data);
                            break;
                    }

                    //parser = null;

                } catch (SocketTimeoutException e) {

                }

                if (System.currentTimeMillis() - lastPacket > 120000) echo();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //unbind();

        disconnect();

        System.out.println(toString() + " ends ...");
    }

    public synchronized boolean isBound() { return bound; }

    public synchronized void close() { bound = false; run = false; }

    public synchronized void request(Thread thread, ISOData data) throws IOException, Exception {
        try {
            requests.put(thread, data); String iso = parser.build(data);
            System.out.println(toString() + " sends {\n" + iso + "\n" + data.print() + "}");
            send(iso);
        } catch (Exception e) {
            requests.remove(thread);
            throw e;
        }
    }

    public synchronized ISOData getResponse(Thread thread) {
        return (ISOData)requests.remove(thread);
    }

    private void response(ISOData data) {
        Enumeration keys = requests.keys();
        while (keys.hasMoreElements()) {
            Thread thread = (Thread)keys.nextElement();
            ISOData d        = (ISOData)requests.get(thread);
            if (data.getBit(3).equals(d.getBit(3)) &&
                data.getBit(11).equals(d.getBit(11)) &&
                data.getBit(7).equals(d.getBit(7))) {
                requests.put(thread, data);
                thread.interrupt();
                return;
            }
        }
    }

    private synchronized void connect() throws IOException, Exception {
        System.out.println(toString() + " connecting to " + address + ":" + port);
        ccbs = new Socket(address, port);
        in  = new DataInputStream(new BufferedInputStream(ccbs.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(ccbs.getOutputStream()));
        ccbs.setSoTimeout(timeout);
        bind();
    }

    private synchronized void disconnect() {
        try { out.close();  } catch (Exception e) { e.printStackTrace(); } out  = null;
        try { in.close();   } catch (Exception e) { e.printStackTrace(); } in   = null;
        try { ccbs.close(); } catch (Exception e) { e.printStackTrace(); } ccbs = null;
    }

    private synchronized void bind() throws IOException, Exception {
        System.out.println(toString() + " sending bind command ...");

        ISOData data = new ISOData();
        data.setMessageType(mFinoParser.TR_NETWORK);
        data.setBit(7, Functions.now("MMddHHmmss"));
        data.setBit(11, 1);
        data.setBit(70, mFinoParser.NW_SIGN_ON);

        send(parser.build(data));
    }

    private synchronized void unbind() throws IOException, Exception {
        System.out.println(toString() + " sending unbind command ...");

        ISOData data = new ISOData();
        data.setMessageType(mFinoParser.TR_NETWORK);
        data.setBit(7, Functions.now("MMddHHmmss"));
        data.setBit(11, 1);
        data.setBit(70, mFinoParser.NW_SIGN_OFF);

        send(parser.build(data));
    }

    private synchronized void echo() throws IOException, Exception {
        System.out.println(toString() + " sending echo command ...");

        ISOData data = new ISOData();
        data.setMessageType(mFinoParser.TR_NETWORK);
        data.setBit(7, Functions.now("MMddHHmmss"));
        data.setBit(11, 1);
        data.setBit(70, mFinoParser.NW_ECHO_TEST);

        send(parser.build(data));
    }

    private synchronized void send(String data) throws IOException {
        data = Functions.padZero(String.valueOf(data.length()), 4) + data.toUpperCase();
//        System.out.println(toString() + " sends [" + data + "]");
        byte[] msg = data.getBytes();
//        System.out.println(toString() + " sends [" + Functions.bytesToHex(msg) + "]");
        //lastPacket = System.currentTimeMillis();
        out.write(msg, 0, msg.length); out.flush();
    }

}
