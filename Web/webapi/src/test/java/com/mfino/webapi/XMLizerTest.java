/**
 * 
 */
package com.mfino.webapi;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;


/**
 * @author Deva
 *
 */
public class XMLizerTest {
	public static void main(String[] args) throws Exception{
//		createXMLUsingXerces();
		createXML();
	}
	
	public static void createXML() throws Exception {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
	    XMLStreamWriter writer = factory.createXMLStreamWriter(System.out);
	    writer.writeStartDocument("1.0");
	    writer.writeStartElement("catalog");
	    writer.writeStartElement("book");
	    writer.writeAttribute("id", "1");
	    writer.writeStartElement("code");
	    writer.writeCharacters("I01");
	    writer.writeEndElement();
	    writer.writeStartElement("title");
	    writer.writeCharacters("This is the title");
	    writer.writeEndElement();
	    writer.writeStartElement("price");
	    writer.writeCharacters("$2.95");
	    writer.writeEndElement();
	    writer.writeEndDocument();
	    writer.flush();
	    writer.close();
	    System.out.println();
	}
	
	public static void createXMLUsingXerces() throws Exception {
		/*DOMImplementation implementation= DOMImplementationRegistry.newInstance().getDOMImplementation("XML 3.0");
		Document doc = implementation.createDocument(null,null,null);
        Element e1 = doc.createElement("howto");
        doc.appendChild(e1);
        Element e2 = doc.createElement("java");
        e1.appendChild(e2);
		DOMImplementationLS feature = (DOMImplementationLS) implementation.getFeature("LS","3.0");
		LSSerializer serializer = feature.createLSSerializer();
		LSOutput output = feature.createLSOutput();
		output.setByteStream(System.out);
		serializer.write(doc, output);
		System.out.println();*/
	}
}
