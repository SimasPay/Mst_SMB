package corp.dimo.test;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import corp.dimo.common.transaction.MessageBuilder;
import iso8583.jpos.ism.Client_ASCIIChannelAdapter;

public class ISOClientSimulator {

	public static void main(String[] args) throws ISOException{

		String hostname = "localhost";
	    int portNumber = 12345;
	    String packagerFile = "WebContent/Resources/isoMsg/packager/iso87ascii-bsm.xml";
	    String connName = "atmConn";
	    
	    new Client_ASCIIChannelAdapter().createConnection(connName, hostname, portNumber, packagerFile);
	    
        ISOMsg request = new ISOMsg();
        request = MessageBuilder.atmMBankingRegistration("1310923840", "93847923ishdiu98", "081329524389");
        System.out.println("["+new String(request.pack())+"]");
        String response = new Client_ASCIIChannelAdapter().sendreceiveRequest(connName, request, 30);
        System.out.println(response);
	}
}
