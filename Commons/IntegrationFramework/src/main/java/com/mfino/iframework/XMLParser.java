package com.mfino.iframework;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.mfino.iframework.domain.Integration;

public class XMLParser {

	public Integration validateXML(File file) {

		Integration integration = null;
		try {
			JAXBContext context = JAXBContext.newInstance("com.mfino.iframework.domain");
			Unmarshaller unmarshaller = context.createUnmarshaller();

			integration = (Integration) unmarshaller.unmarshal(file);

		}
		catch (JAXBException ex) {
		}

		return integration;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}