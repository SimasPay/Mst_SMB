package com.mfino.provision.tools.propertymanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ObjectFromXml
{
	ArrayList<String> propertyarray;
	ArrayList<String> fileLocationActual;
	String xmlName;
	String SearchInFolder;
	String Home;

	public String getXmlName()
	{
		return this.xmlName;
	}

	public void setXmlName(String xmlName)
	{
		this.xmlName = xmlName;
	}

	public ArrayList<String> getPropertyarray()
	{
		return this.propertyarray;
	}

	public void setPropertyarray(ArrayList<String> propertyarray)
	{
		this.propertyarray = propertyarray;
	}

	public String getSearchInFolder()
	{
		return this.SearchInFolder;
	}

	public void setSearchInFolder(String searchInFolder)
	{
		this.SearchInFolder = searchInFolder;
	}

	public String getHome()
	{
		return this.Home;
	}

	public void setHome(String home)
	{
		this.Home = home;
	}

	public ArrayList<String> getFileLocationActual()
	{
		return this.fileLocationActual;
	}

	public void setFileLocationActual(ArrayList<String> fileLocationActual)
	{
		this.fileLocationActual = fileLocationActual;
	}
}

public class XmlParse
{

	@SuppressWarnings("unused")
	private static BufferedWriter outfile;

	private static String getTagValue(String sTag, Element eElement)
	{
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

		Node nValue = nlList.item(0);

		return nValue.getNodeValue();
	}

	public static void searchXmlFiles(String dataChecklist, String requiredFile, HashMap<String, ArrayList<String>> failedFilesMap, Logger logger,
			PMObject ConsoleObject) throws IOException, InvocationTargetException
	{
		PropertiesObject propertyobject = new PropertiesObject();
		String searchInFolder = dataChecklist + requiredFile;
		File root = new File(searchInFolder);
		ArrayList<String> AllProperties2 = new ArrayList<String>();
		String[] extensions = { "xml" };
		boolean recursive = true;
		@SuppressWarnings("rawtypes")
		Collection xmlFiles = FileUtils.listFiles(root, extensions, recursive);
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = xmlFiles.iterator(); iterator.hasNext();) {
			File xmlFile = (File) iterator.next();
			String XmlFileName = xmlFile.getAbsolutePath();
			PMSmxParser cfp = new PMSmxParser(XmlFileName);
			cfp.parseDocument();
			ArrayList<String> AllProperties3 = new ArrayList<String>();
			ArrayList<String> folderNameAll = new ArrayList<String>();
			ArrayList<String> AllProperties = cfp.propertynames;

			for (int i = 0; i < AllProperties.size(); i++) {
				AllProperties2 = CommonFunctions.extract(AllProperties.get(i), "{", "}");
				for (int j = 0; j < AllProperties2.size(); j++) {
					AllProperties3.add(AllProperties2.get(j));
				}
			}
			for (int g = 0; g < cfp.fileLocationActual.size(); g++) {
				if (cfp.fileLocationActual.get(g).startsWith("file:") || cfp.fileLocationActual.get(g).startsWith("classpath:")) {
					folderNameAll.add(cfp.fileLocationActual.get(g));
				}
			}
			if ((AllProperties3.size() != 0) && cfp.fileLocationActual.size() != 0) {
				ObjectFromXml newXmlObject = new ObjectFromXml();
				newXmlObject.setFileLocationActual(folderNameAll);
				newXmlObject.setHome(dataChecklist);
				newXmlObject.setPropertyarray(AllProperties3);
				newXmlObject.setSearchInFolder(searchInFolder);
				newXmlObject.setXmlName(XmlFileName);
				logger.log(Level.SEVERE, "Verifying " + XmlFileName);
				failedFilesMap = PropertiesUtil.checkforproperties(logger, newXmlObject, failedFilesMap, ConsoleObject, propertyobject);
			}
		}

	}

	public static PropertiesObject checkConfigFile(PMObject consoleArgs, String requiredFile, Logger logger, String dataChecklist, String CoDate,
			PropertiesObject propertiesobject)
	{
		Node nNode = null;
		String foldPlusFileNAme = null;
		logger.log(Level.INFO, "---------------------------------------------------------------------------------------");

		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			File fXmlFile = new File(consoleArgs.getConfigFileLocation());
			if (!fXmlFile.exists()) {
				logger.log(Level.SEVERE, "configFile.xml not found !");
				System.exit(0);
			}
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName(dataChecklist);
			logger.log(Level.INFO, "----------------------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++)

			{
				nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					foldPlusFileNAme = getTagValue(PropertyManagerConstants.folderLocationInXml, eElement) + requiredFile;
					// login Credentials
					LoginDetails IntialDetails = new LoginDetails();
					IntialDetails.setSYSTEMNAME(getTagValue(PropertyManagerConstants.SystemNameInXml, eElement));
					IntialDetails.setIPADDRESS(getTagValue(PropertyManagerConstants.IPInXml, eElement));
					logger.log(Level.SEVERE, "accessing files in " + IntialDetails.getSYSTEMNAME());
					if (propertiesobject.getSystemCred().containsKey(IntialDetails.SYSTEMNAME) == false) {
						IntialDetails = JschUtil.getLoginDetails(IntialDetails, logger);
						propertiesobject.getSystemCred().put(IntialDetails.SYSTEMNAME, IntialDetails);
					} else {
						IntialDetails = propertiesobject.getSystemCred().get(IntialDetails.SYSTEMNAME);
					}

					logger.log(Level.SEVERE, "reading " + foldPlusFileNAme + ". . . . . ");
					if (consoleArgs.isMakeChangesAtProduction() == false)

					{
						// copy from remote system
						String orginalFileAtLocal = JschUtil.fileCopyFromSource(IntialDetails, foldPlusFileNAme, logger);
						// read all properties and store changes into temp file
						propertiesobject = TextFileUtil.propertyFilesReading(consoleArgs, logger, orginalFileAtLocal, propertiesobject);
						// save changes at remote server
						String tempFileAtRemote = CommonFunctions.GetFolderName(foldPlusFileNAme, "new", CoDate);
						JschUtil.filePasteAtSource(IntialDetails, propertiesobject.getTempFileAtLocal(), tempFileAtRemote, logger);
						// del local files
						File fileToDelete = new File(orginalFileAtLocal);
						fileToDelete.deleteOnExit();
						File fileToDelete2 = new File(propertiesobject.getTempFileAtLocal());
						fileToDelete2.deleteOnExit();
					}
					//
					else {

						String tempNewFIle = CommonFunctions.GetFolderName(foldPlusFileNAme, "old", CoDate);
						String tempNewFIle2 = CommonFunctions.GetFolderName(foldPlusFileNAme, "new", CoDate);

						JschUtil.fileRename(IntialDetails, foldPlusFileNAme, tempNewFIle, logger);
						JschUtil.fileRename(IntialDetails, tempNewFIle2, foldPlusFileNAme, logger);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return propertiesobject;

	}
}
