package com.mfino.web.admin.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.sql.Clob;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialClob;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.View;

import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.BulkBankAccount;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.BulkBankAccountService;
import com.mfino.service.PocketService;
import com.mfino.service.PocketTemplateService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.SubscriberMdnProcessor;
import com.mfino.uicore.web.JSONView;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.ValidationUtil;

@Controller
public class FileBankAccountUploadController {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("BulkBankAccountServiceImpl")
	private BulkBankAccountService bulkBankAccountService;

	@Autowired
	@Qualifier("PocketTemplateServiceImpl")
	private PocketTemplateService pocketTemplateService;	

	@Autowired
	@Qualifier("SubscriberMdnProcessorImpl")
	private SubscriberMdnProcessor subscriberMdnProcessor;

	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@RequestMapping("/uploadbankaccount.htm")
	protected View handleUpload(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			//5 is Default Bank Account-Debit
			PocketTemplate bankAccountTemplate = pocketTemplateService.getById(new Long(5));


			if (request instanceof MultipartHttpServletRequest) {
				// this is what we need
				MultipartHttpServletRequest realRequest = (MultipartHttpServletRequest) request;
				@SuppressWarnings("unchecked")
				Iterator<String> filenames = realRequest.getFileNames();
				while (filenames.hasNext()) {
					BulkBankAccount bu = new BulkBankAccount();
					String filename = (String) filenames.next();
					MultipartFile file = realRequest.getFile(filename);
					bu.setFilename(file.getOriginalFilename());
					byte[] bytes = file.getBytes();
					bu.setFiledata(new String(bytes));
					int fileSzMB = Integer.parseInt(ConfigurationUtil.getUploadFileSizeLimit());
					long maxFileSizeAllowed = fileSzMB * 1048576;
					if (bytes.length > maxFileSizeAllowed) {
						HashMap map = new HashMap();
						map.put("Error", String.format(MessageText._("Sorry, file size exceeds the max limit") + " %s" + MessageText._("MB."), fileSzMB));
						map.put("success", false);
						return new JSONView(map);
					}
					bu.setUploadfilestatus(CmFinoFIX.UploadFileStatus_Uploaded);
					bulkBankAccountService.save(bu);

					// Parsing the file for validations
					BufferedReader reader = new BufferedReader(new StringReader(bu.getFiledata()));

					String currentDir = System.getProperty("user.dir");
					File reportFile = new File(currentDir + "/reportTemp." + bu.getId() + ".txt");
					FileOutputStream fstream = new FileOutputStream(reportFile);
					PrintStream out = new PrintStream(fstream);

					String strLine = null;
					int linecount = 0;
					int errorLineCount = 0;
					while ((strLine = reader.readLine()) != null) {
						if (strLine.length() == 0) {
							// skip empty lines
							continue;
						}
						linecount++;
						log.info("Parsing Line is" + strLine);    
						String result[] = strLine.split(",");
						if (!(result.length == 3 || result.length == 4)) {
							// The line format is not right
							log.error(String.format("Bad line format: %s", strLine));
							out.println(strLine + ",1,Bad line format");
							errorLineCount++;
							continue;
						}
						if(result.length==4){
							//we need to use other than default bank pocket template based on the user input.
							Long templateid;
							log.info("Setting the cardpan to other than the default bank account" );
							log.info("Setting the cardpan to the following pocket template id " + result[3]);
							try{
								templateid = Long.parseLong(result[3]);
								bankAccountTemplate = pocketTemplateService.getById(templateid);
								if(bankAccountTemplate == null){
									log.info("Invalid Pocket template id" + result[3]);
									out.println(strLine + ",1,Invalid pocket template id");
									errorLineCount++;
									continue;
								}
								//checking the template is bank account pocket template or not and commodity type is Money or not
								if(!(CmFinoFIX.PocketType_BankAccount.equals(bankAccountTemplate.getType()) && CmFinoFIX.Commodity_Money.equals(bankAccountTemplate.getCommodity()))){
									log.info("Pocket Template ID is not bank account template" +  result[3]);
									out.println(strLine + ",1,Invalid bank account pocket template id");
									errorLineCount++;
									continue;
								}
							}catch (Exception exp) {
								log.warn("Failed to Parse the new template id" + result[3], exp);
								errorLineCount++;
								continue;
							}            	
						}
						String mdn = result[0];
						String bankAccount = result[1];
						String recordType = result[2];
						if (ValidationUtil.ValidateMDN(mdn) == false) {
							log.error(String.format("Bad MDN format: %s", strLine));
							out.println(strLine + ",1,Wrong MDN");
							errorLineCount++;
							continue;
						}
						SubscriberMdnQuery mdnQuery = new SubscriberMdnQuery();
						mdnQuery.setExactMDN(mdn);
						if(userService.getUserCompany()!=null){
							mdnQuery.setCompany(userService.getUserCompany());            	
						}
						else{
							log.error(String.format("Failed creating a pocket. Invalid CompanyID for loggined in user: %s", strLine));
							out.println(strLine + ",1,Invalid CompanyID for loggined in user");
							errorLineCount++;
							continue;
						}
						
						List<SubscriberMdn> mdnResults = subscriberMdnProcessor.get(mdnQuery);
						if (mdnResults.size() < 1) {
							log.error(String.format("MDN does not exist : %s", strLine));
							out.println(strLine + ",1,MDN does not exist");
							errorLineCount++;
							continue;
						}
						try{
							if (bankAccountTemplate.getTypeofcheck()==(CmFinoFIX.TypeOfCheck_LuhnCheck) && ValidationUtil.ValidateBankAccount(bankAccount) == false) {
								log.error(String.format("Bad bank account format: %s", strLine));
								out.println(strLine + ",1,Wrong ATM card number or Parity check error");
								errorLineCount++;
								continue;
							}
							else if (bankAccountTemplate.getTypeofcheck()==(CmFinoFIX.TypeOfCheck_RegularExpressionCheck) && ValidationUtil.validateRegularExpression(bankAccount,bankAccountTemplate.getRegularexpression()) == false){
								log.error(String.format("Bad bank account failed due to regular expression check %s", strLine));
								out.println(strLine + ",1,Wrong ATM card number or Parity check error or Regular Expression check");
								errorLineCount++;
								continue;
							}
						}catch(PatternSyntaxException patternExp){
							log.error(String.format("Bad bank account failed due to regular expression Expection Issue", patternExp));
							out.println(strLine + ",1,Wrong ATM card number or Parity check error or Regular Expression Parsing Failed and  exception occured");
							errorLineCount++;
							continue;
						}
						// the bank account number should be unique,
						// there should not exists an existing record with the same number
						PocketQuery query = new PocketQuery();
						List<Pocket> pockets = null;
						query.setCardPan(bankAccount);
						pockets = pocketService.get(query);
						if (pockets.size() > 0) {
							log.error(String.format("Bank account already exists: %s", strLine));
							out.println(strLine + ",1,ATM Card number already exists");
							errorLineCount++;
							continue;
						}
						query = new PocketQuery();
						query.setMdnIDSearch(mdnResults.get(0).getId().longValue());
						query.setPocketType(CmFinoFIX.PocketType_BankAccount);

						query.setPocketStatus(CmFinoFIX.PocketStatus_Active);
						pockets = pocketService.get(query);
						query.setPocketStatus(CmFinoFIX.PocketStatus_Initialized);
						pockets.addAll(pocketService.get(query));
						log.info("Number of Bank Account pockets "+ pockets.size()); 
						if (recordType.equals("0")) {
							// new record
							// there should not be an active bank pocket for the mdn
							if (pockets.size() > 0) {
								log.error(String.format("Bank account pocket already exists while trying to create a new pocket: %s", strLine));
								out.println(strLine + ",1,Bank account link already exists");
								errorLineCount++;
								continue;
							} else {
								Pocket newPocket = new Pocket();
								newPocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
								newPocket.setStatustime(new Timestamp());
								newPocket.setPocketTemplateByPockettemplateid(bankAccountTemplate);
								newPocket.setCardpan(bankAccount);
								newPocket.setIsdefault(CmFinoFIX.Boolean_True);
								newPocket.setSubscriberMdn(mdnResults.get(0));
								newPocket.setCompany(userService.getUserCompany());
								pocketService.save(newPocket);                
								log.info("creation of the pocket is successful: " +  newPocket.getId());
							}
						} else if (recordType.equals("1")) {
							// update record
							// there should be an active bank pocket for the mdn
							if (pockets.size() < 1) {
								log.error(String.format("Bank account pocket does not exist while updating pocket: %s", strLine));
								out.println(strLine + ",1,Bank account link does not exist");
								errorLineCount++;
								continue;
							}
							Pocket oldPocket = null;
							for(Pocket pocket: pockets){
								PocketTemplate template = pocket.getPocketTemplateByPockettemplateid();
								// *FindbugsChange*
					        	// Previous -- if(template!=null && (bankAccountTemplate.getID() == (template.getID()))){
								if(bankAccountTemplate.getId().equals(template.getId())){
									oldPocket = pocket;
									//REFACTOR: Limitation to get just the first pocket for the template
									break;
								}
							}
							if(oldPocket==null){
								log.error(String.format("Bank account pocket does not exist for pocket template: "+bankAccountTemplate.getId()+" while updating pocket: %s", strLine));
								out.println(strLine + ",1,Bank account pocket does not exist for pocket template");
								errorLineCount++;
								continue;
							}

							oldPocket.setStatus(CmFinoFIX.PocketStatus_Initialized);
							oldPocket.setCardpan(bankAccount);
							pocketService.save(oldPocket);
							log.info("updation of the pocket is successful: " +  oldPocket.getId());
						} else {
							log.error(String.format("Bad record type format: %s", strLine));
							out.println(strLine + ",1,Unknown record type");
							errorLineCount++;
							continue;
						}

						out.println(strLine + ",0,0");
					}
					out.close();

					bu.setTotallinecount((long)linecount);
					bu.setErrorlinecount((long)errorLineCount);
					bu.setUploadfilestatus(CmFinoFIX.UploadFileStatus_Processed);
					String reportString = IOUtils.toString(new FileReader(reportFile));
					bu.setUploadreport(reportString);
					
					bulkBankAccountService.save(bu);

					// send the email report          
					userService.sendEmail(String.format(MessageText._("Bulk Link MDN to Card (Ref#%s)"), bu.getId()), reportString);
					log.info("sending and email to the user about the bulkbankupload: " + bu.getId());
				}
			}

			HashMap map = new HashMap();
			map.put("success", true);
			return new JSONView(map);
		} catch (Throwable throwable) {
			log.error(throwable.getMessage(), throwable);

			HashMap map = new HashMap();
			map.put("success", false);
			map.put("Error", "Sorry," + throwable.toString());
			return new JSONView(map);
		} 
	}
}
