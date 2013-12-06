package com.mfino.integration.xml;

public interface RequestResponseTransformation {

	public String requestTransform(String inXML) throws TransformationFailedException;

	public String responseTransform(String outXML) throws TransformationFailedException;

	public void setRequestXSLTFile(String filepath);

	public void setResponseXSLTFile(String filepath);

	public String getDetailsRequestTransform(String inXML) throws TransformationFailedException;

	public String getDetailsResponseTransform(String transformedResponse) throws TransformationFailedException;

}
