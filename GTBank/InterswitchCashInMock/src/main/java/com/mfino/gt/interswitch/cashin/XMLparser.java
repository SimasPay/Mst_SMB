package com.mfino.gt.interswitch.cashin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLparser {

	public static void main(String[] args) throws Exception {

		
		
		BufferedReader br = new BufferedReader(new FileReader(new File(
		        "A:\\MFS_V2_6\\GTBank\\InterswitchCashInMock\\src\\main\\java\\com\\mfino\\gt\\interswitch\\cashin\\Test2.xml")));

		String str = "", str1 = "";

		while ((str = br.readLine()) != null) {
			str1 = str1 + str;
		}
		//*FindbugsChange*
		//Previous -- Commented out next line
		//File file = new File("");
		
		System.out.println(str1);

		// str1="<data><employee><name>A</name>"
		// + "<title>Manager</title></employee></data>";

		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(str1));

		Document doc = db.parse(is);
		NodeList nodes = doc.getElementsByTagName("CashInRequest");

		for (int i = 0; i < nodes.getLength(); i++) {
			Element element = (Element) nodes.item(i);

			System.out.println( getElementContent(element,"PaymentLogId"));

			// NodeList title = element.getElementsByTagName("title");
			// line = (Element) title.item(0);
			// System.out.println("Title: " +
			// getCharacterDataFromElement(line));
		}

	}

	private static String getElementContent(Element element,String name) {
		NodeList name3 = element.getElementsByTagName(name);
		Element line3 = (Element) name3.item(0);
		return getCharacterDataFromElement(line3);
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

}
