/**
 * 
 */
package com.mfino.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Deva
 *
 */
public class NotificationXMLGenerator {

	public static void main(String[] args) throws Exception {
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream("E:/Temp/NewNotif.xml"));
		NodeList valueNodes = document.getElementsByTagName("value");
		for (int i =0; i< valueNodes.getLength(); ++i) {
			Node valueNode = valueNodes.item(i);
			NodeList childNodes = valueNode.getChildNodes();
			
			Node smartNode = document.createElement("C1");
			int k = childNodes.getLength();
			for (int j=0; j<k;++j) {
				smartNode.appendChild(childNodes.item(0));
			}
			valueNode.appendChild(smartNode);
			Node mobile8Node = document.createElement("C2");
			childNodes = smartNode.getChildNodes();
			for (int j=0; j<childNodes.getLength();++j) {
				mobile8Node.appendChild(childNodes.item(j).cloneNode(true));
			}
			valueNode.appendChild(mobile8Node);
			
		}
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		FileOutputStream fos = new FileOutputStream("E:/deva/test/output.xml");
		serialize(document, fos);
//		String s = new String(outputStream.toByteArray());
//		System.out.println(s);
		
	}
	public static void serialize(Document doc, OutputStream out) throws Exception {
        
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        try {
            serializer = tfactory.newTransformer();
            //Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            serializer.transform(new DOMSource(doc), new StreamResult(out));
        } catch (TransformerException e) {
            // this is fatal, just dump the stack and throw a runtime exception
            e.printStackTrace();
            
            throw new RuntimeException(e);
        }
    }
}
