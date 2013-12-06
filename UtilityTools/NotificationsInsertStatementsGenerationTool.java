package com.mfino.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NotificationsInsertStatementsGenerationTool{

	/**
	 * An xml file is expected as input with field as the root node and value nodes as its children
	 * Remove all the notifications that do not follow the below format <br>
	 * from the input xmlfile.
	 *<br>&lt;field&gt;
	 *<br> &lt;value description="BillPaymentTopupInquiryDetails" enum="605"&gt;
     *<br>   &lt;C1&gt;
     *<br>     &lt;English&gt;
     *<br>       &lt;SMS&gt;
     *<br>         &lt;Text&gt;$(BankName) Your requested to topup an Amount $(BillAmountValue) on $(CurrentDateTime) REF: $(TransactionID)&lt;/Text&gt;
     *<br>       &lt;/SMS&gt;
     *<br>     &lt;/English&gt;
     *<br>     &lt;Bahasa&gt;
     *<br>       &lt;SMS&gt;
     *<br>        &lt;Text&gt;$(BankName) Your requested to topup an Amount $(BillAmountValue) on $(CurrentDateTime) REF: $(TransactionID)&lt;/Text&gt;
     *<br>       &lt;/SMS&gt;       
     *<br>        &lt;/Bahasa&gt;
     *<br>   &lt;/C1&gt;
     *<br>   &lt;C2&gt;
     *<br>     &lt;English&gt;
     *<br>       &lt;SMS&gt;
     *<br>        &lt;Text&gt;$(BankName) Your requested to topup an Amount $(BillAmountValue) on $(CurrentDateTime) REF: $(TransactionID)&lt;/Text&gt;
     *<br>       &lt;/SMS&gt;
     *<br>     &lt;/English&gt;
     *<br>     &lt;Bahasa&gt;
     *<br>       &lt;SMS&gt;
     *<br>         &lt;Text&gt;$(BankName) Your requested to topup an Amount $(BillAmountValue) on $(CurrentDateTime) REF: $(TransactionID)&lt;/Text&gt;
     *<br>       &lt;/SMS&gt;
     *<br>     &lt;/Bahasa&gt;
     *<br>   &lt;/C2&gt;
     *<br>&lt;/value&gt; 
	 *<br>&lt;/field&gt;
	 * @param args
	 **/
	public static void main(String[] args) {
		 if (args.length < 1)
		 {	 
			System.out.println("Usage:");
		        System.out.println("java NotificationInsertStatementsGenerationTool <AbsoluteFilePath>");
			return;
		 }
//		String str = "C:\\codebase\\branches\\phase-2\\UtilityTools\\abcd.xml";
		try {
			File xmlFile = new File(args[0]);
			String name = xmlFile.getName();
			String[] spl = name.split("\\.");
			name = spl[0];
			FileWriter fstream = new FileWriter(xmlFile.getParent()+File.separator+name+"_INSERTstmts.sql");
			BufferedWriter bw = new BufferedWriter(fstream);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList valueList = doc.getElementsByTagName("value");
			int totalCnt=0;
			for (int i = 0; i < valueList.getLength(); i++) {
				Node value = valueList.item(i);
				if (value.getNodeType() == Node.ELEMENT_NODE) {
					String attrDescription = value.getAttributes().getNamedItem("description").getTextContent();
					String attrEnum = value.getAttributes().getNamedItem("enum").getTextContent();
					int companyCnt=0;
					NodeList companyList = value.getChildNodes();
					for (int j = 0; j < companyList.getLength(); j++) {
						Node company = companyList.item(j);
						if (company.getNodeType() == Node.ELEMENT_NODE) {
							companyCnt++;
							int languageCnt=-1;
							NodeList languageList = company.getChildNodes();
							for (int k = 0; k < languageList.getLength(); k++) {
								Node language = languageList.item(k);
								if (language.getNodeType() == Node.ELEMENT_NODE) {
									languageCnt++;
									NodeList smsList = language.getChildNodes();
									for (int l = 0; l < smsList.getLength(); l++) {
										Node sms = smsList.item(l);
										if (sms.getNodeType() == Node.ELEMENT_NODE) {
											NodeList textList = sms.getChildNodes();
											for (int m = 0; m < textList.getLength(); m++) {
												Node text = textList.item(m);
												if (text.getNodeType() == Node.ELEMENT_NODE) {
													
													String notifText = text.getTextContent();
													
													String insertStmt = "INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`," +
															"`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`," +
															"`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES ";
													
													int[] notifTypes = {1,2,4,8,16};
													for(int nT:notifTypes) {
																String vstmt = "(now(),\"System\",now(),\"System\",0,1,"+attrEnum+",\""+attrDescription+"\","+nT+
																",\""+notifText+"\",null,"+languageCnt+",0,now(),null,null,"+companyCnt+");";
																System.out.println(insertStmt+vstmt);
																System.out.println();
																System.out.println();
																bw.write(insertStmt+vstmt);
																bw.newLine();
																bw.newLine();
																totalCnt++;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			System.out.println(totalCnt);
			bw.close();
		}
		catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}
