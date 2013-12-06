package com.mfino.integration.cashin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.integration.xml.RequestResponseTransformation;
import com.mfino.integration.xml.TransformationFailedException;

public class XSLTTransformation implements RequestResponseTransformation {

	Logger	       log	= LoggerFactory.getLogger(XSLTTransformation.class);

	private String	requestXSLTFile;

	private String	responseXSLTFile;

	private String detailsRequestXSLTFile;

	public String getDetailsRequestXSLTFile() {
		return detailsRequestXSLTFile;
	}

	public void setDetailsRequestXSLTFile(String detailsRequestXSLTFile) {
		this.detailsRequestXSLTFile = detailsRequestXSLTFile;
	}

	public String getDetailsResponseXSLTFile() {
		return detailsResponseXSLTFile;
	}

	public void setDetailsResponseXSLTFile(String detailsResponseXSLTFile) {
		this.detailsResponseXSLTFile = detailsResponseXSLTFile;
	}

	private String detailsResponseXSLTFile;

	@Override
	public String requestTransform(String inXML) throws TransformationFailedException {

		try {
			log.info("inXML --> " + inXML);

			String str = transform(inXML, requestXSLTFile);
			log.info("xml after transformation-->" + str);
			return str;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log.error("request trasformation failed." + ex.getMessage(),ex);
			TransformationFailedException e = new TransformationFailedException(ex.getMessage());
			e.fillInStackTrace();
			throw e;
		}

	}

	@Override
	public String responseTransform(String inXML) throws TransformationFailedException {
		try {
			return transform(inXML, responseXSLTFile);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log.error("response trasformation failed.", ex);
			TransformationFailedException e = new TransformationFailedException(ex.getMessage());
			e.fillInStackTrace();
			throw e;
		}
	}

	@Override
	public void setRequestXSLTFile(String requestXSLTFile) {
		this.requestXSLTFile = requestXSLTFile;

	}

	@Override
	public void setResponseXSLTFile(String responseXSLTFile) {
		this.responseXSLTFile = responseXSLTFile;
	}

	public void transform(InputStream inXML, String inXSL, OutputStream outXML) throws TransformerConfigurationException, TransformerException {

		TransformerFactory factory = TransformerFactory.newInstance();

		StreamSource xslStream = new StreamSource(inXSL);
		Transformer transformer = factory.newTransformer(xslStream);
		transformer.setErrorListener(new MyErrorListener());

		StreamSource in = new StreamSource(inXML);
		StreamResult out = new StreamResult(outXML);

		transformer.transform(in, out);

	}

	public String transform(String inXML, String inXSL) throws TransformerConfigurationException, TransformerException {

		ByteArrayInputStream iStream = new ByteArrayInputStream(inXML.getBytes());
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();

		transform(iStream, inXSL, oStream);

		return oStream.toString();
	}

	class MyErrorListener implements ErrorListener {
		public void warning(TransformerException e) throws TransformerException {
			show("Warning", e);
			throw (e);
		}

		public void error(TransformerException e) throws TransformerException {
			show("Error", e);
			throw (e);
		}

		public void fatalError(TransformerException e) throws TransformerException {
			show("Fatal Error", e);
			throw (e);
		}

		private void show(String type, TransformerException e) {
			log.info(type + ": " + e.getMessage());
			if (e.getLocationAsString() != null)
				log.info(e.getLocationAsString());
		}
	}

	@Override
	public String getDetailsRequestTransform(String inXML) throws TransformationFailedException {
		
		try {
			log.info("inXML --> " + inXML);

			String str = transform(inXML, detailsRequestXSLTFile);
			log.info("xml after transformation-->" + str);
			return str;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log.error("request trasformation failed." + ex.getMessage(),ex);
			TransformationFailedException e = new TransformationFailedException(ex.getMessage());
			e.fillInStackTrace();
			throw e;
		}
	}

	@Override
	public String getDetailsResponseTransform(String inXML) throws TransformationFailedException {
		try {
			return transform(inXML, detailsResponseXSLTFile);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log.error("response trasformation failed.", ex);
			TransformationFailedException e = new TransformationFailedException(ex.getMessage());
			e.fillInStackTrace();
			throw e;
		}
	}

}
