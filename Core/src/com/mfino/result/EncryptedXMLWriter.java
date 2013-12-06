package com.mfino.result;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.bouncycastle.crypto.params.KeyParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.GeneralConstants;
import com.mfino.crypto.CryptographyService;

public class EncryptedXMLWriter {

	private XMLStreamWriter	writer;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private EncryptedXMLWriter(XMLStreamWriter writer) {
		this.writer = writer;
	}

	private KeyParameter	keyParameter;

	public static EncryptedXMLWriter createEncryptedXMLWriter(XMLStreamWriter writer) {
		EncryptedXMLWriter xmlwriter = new EncryptedXMLWriter(writer);
		return xmlwriter;
	}

	public void writeAttribute(String name, String value, boolean encrypt) throws Exception {
		if (keyParameter==null||!encrypt ) {
			writer.writeAttribute(name, value);
			return;
		}
		String encedMsg = new String(CryptographyService.binToHex(CryptographyService.encryptWithAES(keyParameter, value.getBytes(GeneralConstants.UTF_8))));
		writer.writeAttribute(name, encedMsg);
	}

	public void writeCharacters( String message,boolean encrypt) throws Exception {
		if (keyParameter==null|| !encrypt ) {
			writer.writeCharacters(message);
			return;
		}
		
		byte[] plainText = message.getBytes(GeneralConstants.UTF_8);
		byte[] cipherText = CryptographyService.encryptWithAES(keyParameter, plainText);
		String encedMsg = new String(CryptographyService.binToHex(cipherText));
		writer.writeCharacters(encedMsg);
		log.info(message);
		log.info(encedMsg);
	}

	public KeyParameter getKeyParameter() {
		return keyParameter;
	}
	public void setKeyParameter(KeyParameter keyParameter) {
		this.keyParameter = keyParameter;
	}
    public void writeStartElement(String eleName) throws Exception {
    	writer.writeStartElement(eleName);
    }
    public void writeEndElement() throws Exception {
    	writer.writeEndElement();
    }
    public void writeStartDocument(String msg) throws Exception {
    	writer.writeStartDocument(msg);
    }
    public void writeEndDocument() throws Exception {
    	writer.writeEndDocument();
    }
    public void flush() throws XMLStreamException {
    	writer.flush();
    }
    public void close() throws XMLStreamException {
    	writer.close();
    }
    
}
