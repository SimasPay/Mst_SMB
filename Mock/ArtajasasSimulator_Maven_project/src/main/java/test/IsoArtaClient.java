package test;

/*
 * EsmeInterface.java
 *
 * Created on September 15, 2006, 10:27 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */



/**
 *
 * @author HIBRIDZI
 */

import com.mfino.util.logging.ILogger;
import com.mfino.util.logging.LogFactory;
import java.util.Properties;
import java.io.*;
import java.net.*;
import java.util.*;
import com.wolvex.Iso8583.*;
import com.wolvex.Common.*;

public class IsoArtaClient extends Thread {
    private ServerSocket server         = null;
    private Socket ccbs                 = null;
    private DataInputStream in          = null;
    private DataOutputStream out        = null;
    private String address              = null;
    private InetAddress inetAddress     = null;
    private ISOProfile profile          = null;
    private ISOParser parser            = null;
    private Hashtable requests          = null;

    private boolean run                 = true;
    private boolean bound               = false;
    private long lastPacket             = 0;
    private int port                    = 0;
    private int timeout                 = 30000;
    private static Properties _configurationProperties = new Properties();
    private Logger log = LoggerFactory.getLogger(this.getClass());tClass());tClass());

    /** Creates a new instance of EsmeInterface */
    public IsoArtaClient(String address, int port, int timeout) {
        this.address    = address;
        this.port       = port;
        this.timeout    = timeout;
    }
     public IsoArtaClient() {
        String path =
            IsoArtaClient.class.getProtectionDomain().getCodeSource()
                .getLocation().toString();
        path = path.substring(6,path.length()-15).concat("multix.properties");
        try
        {
          FileInputStream fis = new FileInputStream(path);
          _configurationProperties.load(fis);
          this.inetAddress = InetAddress.getLocalHost();
          fis.close();
            this.address    = _configurationProperties.getProperty("address")!=null ? _configurationProperties.getProperty("address"):"localhost";
            try
            {
                this.inetAddress = InetAddress.getByName(this.address);
            }
            catch(Exception e)
            {
                this.inetAddress = InetAddress.getLocalHost();
            }
            this.port       = _configurationProperties.getProperty("port")!=null? Integer.parseInt(_configurationProperties.getProperty("port")):9999;
            this.timeout    = _configurationProperties.getProperty("timeout")!=null? Integer.parseInt(_configurationProperties.getProperty("timeout")):30000;
        }
        catch(Exception e)
        {
            log.error("error in reading propeties", e);
        }
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
        	ClassLoader loader = this.getClass().getClassLoader();
        	URL url = loader.getResource("mFino.xml");
        	String path = url.getPath();
            profile  = new ISOProfile(path, null, null);
            parser   = new ISOParser(profile, null, null);
            requests = new Hashtable();

            while ( run ) {
                try {
                    if (ccbs == null) connect();

                    byte[] b        = parser.read(in);
                    String iso      = parser.parse(b);
                    ISOData data    = parser.parse(iso);

                    lastPacket = System.currentTimeMillis();

                    System.out.println(toString() + " receives {\n" + iso + "\n" + data.print() + "}");
                    log.info(toString() + " receives {\n" + iso + "\n" + data.print() + "}");

                    switch (data.getMessageType()) {
                        case ISOParser.TR_NETWORK:
                            data.setMessageType(ISOParser.TR_NETWORK_RESP);
                            data.setBit(39, 0);
                            send(parser.build(data));
                            bound = true;
                            break;
                        case ISOParser.TR_NETWORK_RESP:
                            bound = (Integer.parseInt(data.getBit(39)) == ISOParser.RC_APPROVED);
                            break;
                        default:
                            response(data);
                            break;
                    }

                    //parser = null;

                } catch (SocketTimeoutException e) {
                    
                } catch (IOException e) {
                    log.error("errorr : ", e);
                    e.printStackTrace(); disconnect();
                }

                if (System.currentTimeMillis() - lastPacket > 120000 && ccbs != null) echo();
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error("error ", e);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error ", e);
        }

        //unbind();

        disconnect();

        System.out.println(toString() + " ends ...");
    }

    public synchronized boolean isBound() { return bound; }

    public synchronized void close() { bound = false; run = false; }

    public synchronized void request(ISOData data) throws IOException, Exception {
        try {
            String iso = parser.build(data);
            System.out.println(toString() + " sends {\n" + iso + "\n" + data.print() + "}");
            log.info(toString() + " sends {\n" + iso + "\n" + data.print() + "}");
            send(iso);
        } catch (Exception e) {
            log.error("error in request", e);
            throw e;
        }
    }

   /* public synchronized ISOData getResponse(Thread thread) {
        return (ISOData)requests.remove(thread);
    }*/

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
        System.out.println(toString() + " listening on port " + port);
        log.info("address  "+address + "   port   "+port);
        if (server == null)
        {server = new ServerSocket(port,50,inetAddress);
        ccbs = server.accept(); ccbs.setSoTimeout(timeout);
        in  = new DataInputStream(new BufferedInputStream(ccbs.getInputStream()));
        out = new DataOutputStream(new BufferedOutputStream(ccbs.getOutputStream()));
        }
        //bind();
    }

    private synchronized void disconnect() {
        try { out.close();  } catch (Exception e) {
            log.error("error ", e);e.printStackTrace(); } out  = null;
        try { in.close();   } catch (Exception e) {
            log.error("error ", e); e.printStackTrace(); } in   = null;
        try { ccbs.close(); } catch (Exception e) {
            log.error("error ", e); e.printStackTrace(); } ccbs = null;
    }

    private synchronized void bind() throws IOException, Exception {
        System.out.println(toString() + " sending bind command ...");

        ISOData data = new ISOData();
        data.setMessageType(ISOParser.TR_NETWORK);
        data.setBit(7, Functions.now("MMddHHmmss"));
        data.setBit(11, 1);
        data.setBit(70, ISOParser.NW_SIGN_ON);

        send(parser.build(data));
    }

    private synchronized void unbind() throws IOException, Exception {
        System.out.println(toString() + " sending unbind command ...");

        ISOData data = new ISOData();
        data.setMessageType(ISOParser.TR_NETWORK);
        data.setBit(7, Functions.now("MMddHHmmss"));
        data.setBit(11, 1);
        data.setBit(70, ISOParser.NW_SIGN_OFF);

        send(parser.build(data));
    }

    private synchronized void echo() throws IOException, Exception {
        System.out.println(toString() + " sending echo command ...");
        log.info(toString() + " sending echo command ...");

        ISOData data = new ISOData();
        data.setMessageType(ISOParser.TR_NETWORK);
        data.setBit(7, Functions.now("MMddHHmmss"));
        data.setBit(11, 1);
        data.setBit(70, ISOParser.NW_ECHO_TEST);

        send(parser.build(data));
    }

    private synchronized void send(String data) throws IOException {
        //data = Functions.padZero(String.valueOf(data.length()), 4) + data.toUpperCase();
        //System.out.println(toString() + " sends [" + data + "]");
        byte[] msg = parser.build(data.toUpperCase());
        System.out.println(toString() + " sends [" + Functions.bytesToHex(msg) + "]");
        log.info(toString() + " sends [" + Functions.bytesToHex(msg) + "]");
        //lastPacket = System.currentTimeMillis();
        out.write(msg); out.flush(); //, 0, msg.length); out.flush();
    }

    public static void main(String[] args) {
    	IsoArtaClient client = new IsoArtaClient();
    	client.run();
//    	ClassLoader loader = client.getClass().getClassLoader();
//    	URL url = loader.getSystemResource("mFino.xml");
//    	System.out.println(url);
	}
}
