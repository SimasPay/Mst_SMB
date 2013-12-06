//FindBugs Final Html Report Creator

package com.mfino.findbugs;
import org.apache.commons.io.FileUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
/**
 * 
 * @author Sreenath
 *
 */
public class FindBugsReportCreator {
	static String htmlWriter;
	static int totalSize;
	static int totalBugs;
	static int totalP1Bugs;
	static int totalP2Bugs;
	//Count the total size and total number of bugs from all modules(i.e all links of html added together)
	public static void totalBugCount(String filePath) throws NumberFormatException, SAXException, IOException, ParserConfigurationException{
		Element findBugsElement = getTagValue(filePath,"FindBugsSummary");
		totalSize=totalSize+Integer.parseInt(findBugsElement.getAttribute("total_size"));
		totalBugs=totalBugs+Integer.parseInt(findBugsElement.getAttribute("total_bugs"));
		//When no bugs are present P1 bugs and P2 Bugs are returned "" .So assigning that case to zero below
		String P1Bugs=(findBugsElement.getAttribute("priority_1").isEmpty()?"0":findBugsElement.getAttribute("priority_1"));
		String P2Bugs=(findBugsElement.getAttribute("priority_2").isEmpty()?"0":findBugsElement.getAttribute("priority_2"));
		totalP1Bugs=totalP1Bugs+Integer.parseInt(P1Bugs);
		totalP2Bugs=totalP2Bugs+Integer.parseInt(P2Bugs);
	}
	//getTagValue returns the required Element with the Tag name.It takes The file to be searched for the tag(filepath) and the \
	//tagname to be searched(tagName) as inputs
	public static Element getTagValue(String filePath,String tagName) throws SAXException, IOException, ParserConfigurationException
	{
		 	File xmlInput = new File(filePath);
		    DocumentBuilderFactory docbuilderfactory= DocumentBuilderFactory.newInstance();
		    DocumentBuilder docbuilder = docbuilderfactory.newDocumentBuilder();
		    Document doc = docbuilder.parse(xmlInput);
		    NodeList requiredNodeList = doc.getElementsByTagName(tagName);
		    //here only the title and the and FindBugsSummary tags are extarcted.Only 1 tag of each exist.So we use the 1st data element from the 
		    //requiredNodesList array
		    Node firstRequiredNode = requiredNodeList.item(0);
		    Element element = (Element) firstRequiredNode;
            return element;
	}
	 //Write data and links to the final Html file
	public static void fillHtml(String filePath) throws SAXException, IOException, ParserConfigurationException{
		 String htmlPath = filePath.replace(".xml", ".html");
		 //System.out.println("path="+htmlPath);
		 Element findBugsElement=getTagValue(filePath,"FindBugsSummary");
		 String codeSize=findBugsElement.getAttribute("total_size");
		 String bugs=findBugsElement.getAttribute("total_bugs");
		//When no bugs are present P1 bugs and P2 Bugs are returned "" .So assigning that case to zero below
		 String P1Bugs=(findBugsElement.getAttribute("priority_1").isEmpty()?"0":findBugsElement.getAttribute("priority_1"));
		 String P2Bugs=(findBugsElement.getAttribute("priority_2").isEmpty()?"0":findBugsElement.getAttribute("priority_2"));

		 htmlWriter+="<tr><td><a href="+"\\"+htmlPath+">"+getTagValue(filePath, "Project").getAttribute("projectName")+"</a></td>" +
		    		"<td >"+codeSize+"</td><td >"+bugs+"</td><td class=\"summary-priority-1\">"+
		    		P1Bugs+"</td><td class=\"summary-priority-2\">"+
		    		P2Bugs+"</td><td class=\"summary-priority-3\" /><td class=\"summary-priority-4\" /></tr>";
		 totalBugCount(filePath);
	 }
	//creating the reporting folder where all the bug html and xml files are copied to from the code base modules.
	//A Html is created with link to these files in the fillHtml method
	 public static File reportFolderGeneration(ArrayList<String> xmlDirectoryPath, String reportDirectory) throws IOException  {
		 //System.out.println("called");
		 String defaultDirectory=reportDirectory+"\\FindBugsReports\\FindBugsReport"+TransactionDate.getTransactionDate();
		 String[] copyExtensions={".xml",".html"};
		 for(Iterator<String> iterator=xmlDirectoryPath.iterator();iterator.hasNext();){
			 //System.out.println("entered");
			 String source = (String) iterator.next();
			 String destination;
			 if(source.contains(":")){
				 destination = defaultDirectory + source.split(":")[1];
				 //System.out.println("entered if");
			 }else{
				 destination = defaultDirectory + source.split(":")[0];
				 //System.out.println("entered else");
			 }
			 for(String ext:copyExtensions){
			 File sourceFile= new File(source,"findbugsXml"+ext);
			 File destinationDirectory = new File(destination);
			 boolean create = destinationDirectory.mkdirs();
			 File destinationFile= new File(destination,"findbugsXml"+ext);
			 
			 InputStream copyFrom = new FileInputStream(sourceFile);
			 OutputStream copyTo = new FileOutputStream(destinationFile);
			 byte[] buffer = new byte[1024];
			 
 	        int length;
 	        //copy the file content in bytes 
 	        while ((length = copyFrom.read(buffer)) > 0){
 	    	   copyTo.write(buffer, 0, length);
 	        }

 	        copyFrom.close();
 	        copyTo.close();
			 }
 	        //System.out.println("File copied from " + source + " to " + destination);
		 }
		 return new File(defaultDirectory);
		 
	 }
	public static void main(String[] args) throws Exception {
		if(args.length<=1){
			System.err.println("NOT ENOUGH ARGUMENTS ENTERED." +
					"PATH OF THE CODEBASE  AND REPORT OUTPUT FOLDER REQUIRED" +
					" IN THE SAME ORDER");
			System.exit(1);
		}
		File directoryCheck = new File(args[1]);
		if(!directoryCheck.exists()){
			System.err.println("Entered report directory doesnt exist");
		}
		File root = new File(args[0]);
		//System.out.println(root.getAbsolutePath());

		 String[] extensions = { "xml"};
		 ArrayList<String> xmlFilePath = new ArrayList<String>();
		 //xmlDirectoryPath
		 ArrayList<String> xmlDirectoryPath = new ArrayList<String>();
		 boolean recursive = true;
		 Collection xmlFiles = FileUtils.listFiles(root, extensions, recursive);
		 for (Iterator iterator = xmlFiles.iterator(); iterator.hasNext();) {
			 File xmlFile = (File) iterator.next();
			 if(!(xmlFile.getParent().contains("FindBugsReports"))){
			 if(xmlFile.getAbsolutePath().contains("findbugsXml")){
				 xmlDirectoryPath.add(xmlFile.getParent());
				 //System.out.println(xmlFile.getAbsolutePath());
				 //System.out.println("parent="+xmlFile.getParent());
			 }
			 }
		 }
		
		 if(xmlDirectoryPath.size()==0){
			 System.err.println("no findbugs folder found.Might be a build error or findbugs running is skipped manually" +
			 		".Report creator exiting.....");
			 System.exit(1);
			 
		 }
		 
		 File finalReportDirectory = reportFolderGeneration(xmlDirectoryPath,args[1]);
		 File finalReportFile =  new File(finalReportDirectory,"report.html");
		 //System.out.println("final="+finalReportFile.getAbsolutePath());
		 xmlFiles.clear();
		 xmlFiles = FileUtils.listFiles(finalReportDirectory, extensions, recursive);
		 for (Iterator iterator = xmlFiles.iterator(); iterator.hasNext();) {
			 File xmlFile = (File) iterator.next();
			 if(xmlFile.getAbsolutePath().contains("findbugs")){
				 xmlFilePath.add(xmlFile.getAbsolutePath());
			 }
		 }
		 System.out.println("report file path="+finalReportFile.getAbsolutePath());

		 BufferedWriter output = new BufferedWriter(new FileWriter(finalReportFile));
		 //FileWriter output = new FileWriter(finalReportFile);
		 htmlWriter="<html><head>";
		 htmlWriter+="<title>Final FindBugs Report</title>";
		 htmlWriter+="</head><body><h3>FindBugs Summary Report</h3>";
		 htmlWriter+="<table border=\"1\">" +
				 "<tr><th>Module</th><th>Code Size</th><th>Bugs</th><th>Bugs p1</th><th>Bugs p2</th><th>Bugs p3</th><th>Bugs Exp.</th>" +
				 "</tr>";
    
		 for(int i=0;i<xmlFilePath.size();i++){
			 FindBugsReportCreator.fillHtml(xmlFilePath.get(i));
		 }
		 htmlWriter+="<tr><td>TOTAL</td><td>"+totalSize+"</td><td>"+totalBugs+"</td><td>"+totalP1Bugs+"</td><td>"+totalP2Bugs+
    					"</td><tr></table>";
		 output.write(htmlWriter);
		 output.write("</body><html>");
		 output.close();
		 //hudsonCheckFile is the file whose data is to be sent in the mail each time a build occurs.
		 File hudsonCheckFile = new File(finalReportDirectory.getParent(),"HudsonCheckFile.txt");
		 System.out.println("HudsonCheckFile location="+hudsonCheckFile.getAbsolutePath());
		 BufferedWriter hudsonFileWriter = new BufferedWriter(new FileWriter(hudsonCheckFile));
		 
		 hudsonFileWriter.write("BUGS FOUND IN LATEST BUILD.");
		 hudsonFileWriter.newLine();
		 hudsonFileWriter.write("PRIORITY-1 BUGS="+totalP1Bugs);
		 hudsonFileWriter.newLine();
		 hudsonFileWriter.write("PRIORITY-2 BUGS="+totalP2Bugs);
		 hudsonFileWriter.close();
    
  }

}