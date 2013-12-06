package com.mfino.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.BulkUploadDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.BulkUpload;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author sandeepjs
 */
public class BulkRemittanceImportTool {

    public static final String DOT = "\\.";
    public static final String FILENAMESUFFIX = "RPT";
    public static final int FOUR = 4;
    public static final String HYPHEN = "-";
    public static final int ONE = 1;
    public static final int THREE = 3;
    public static final int TWO = 2;
    public static final String UNDERSCORE = "_";
    private static FTPClient client = null;

    private static Logger log = LoggerFactory.getLogger(BulkRemittanceImportTool.class);

    public static void main(String[] args) {
        BulkRemittanceImportTool.Import();
    }

    public static void Import() {
    	log.info("Start BulkRemittance Import");
    	
        if(!getFTPClient()){
        	return;
        }
        try {
            FTPFile[] files = client.listFiles();

            for (int i = 0; i < files.length; i++) {
                FTPFile fTPFile = files[i];
                if (fTPFile.isFile()) {
                    processFile(fTPFile);
                }
            }
        } catch (Exception ex) {
            log.error("Exception", ex);
        }
    }

    public static boolean getFTPClient() {
        URL ftpUrl = null;
        try {
            ftpUrl = new URL(ConfigurationUtil.getBulkRemittanceImportFtpServer());
        } catch (MalformedURLException urlEx) {
            log.error("Ftp url is in a wrong format. e.g. ftp://somehost:21/dirtofile", urlEx);
            return false;
        }
        try {
            client = new FTPClient();
            client.connect(ftpUrl.getHost());
            if (ConfigurationUtil.getBulkRemittanceImportFtpDoLogin()) {
                client.login(ConfigurationUtil.getBulkRemittanceImportFtpUsername(), 
                		ConfigurationUtil.getBulkRemittanceImportFtpPassword());
            }
            if(client.changeWorkingDirectory("~/" + ftpUrl.getPath())){
            	return true;
            }else{
            	log.error("Cannot change to the target ftp directory");
            	return false;
            }
        } catch (Exception exp) {
            log.error("Cannot Connect to Client", exp);
            return false;
        }
    }

    public static void processFile(FTPFile fTPFile) {
        try {
            String fileName = fTPFile.getName();
            log.info("filename = "+fileName);
            String IDStr = checkFileName(fileName);
            log.info("ID = "+IDStr);
            
            if (IDStr != null) {
                Long ID = Long.parseLong(IDStr);
                File output = new File(ConfigurationUtil.getTempDir(), fTPFile.getName());
                OutputStream outputStream = new FileOutputStream(output);
                boolean success = client.retrieveFile(fTPFile.getName(), outputStream);
                if (success) {
                    outputStream.close();
                    String fileData = getFileData(output);
                    BulkUploadDAO dao = DAOFactory.getInstance().getBulkUploadDAO();

                    HibernateUtil.getCurrentSession().beginTransaction();

                    BulkUpload entry = dao.getById(ID);
                    entry.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Complete);
                    entry.setReportFileData(fileData);
                    entry.setReportFileName(fTPFile.getName());
                    dao.save(entry);

                    HibernateUtil.getCurrentSession().getTransaction().commit();
                } else {
                    log.error("Failed to download file from ftp");
                }
            }
        } catch (Exception exp) {
            log.error("Unable to process file " + fTPFile.getName(), exp);
        }
    }

    // Checking format of the fileName.
    public static String checkFileName(String fileName) {
        String firstTokens[] = fileName.split(DOT);

        log.info("tokens lenght= "+firstTokens.length);
        if (firstTokens.length != THREE || !firstTokens[TWO].equals(FILENAMESUFFIX)) {
            return null;
        }
        String middleToken = firstTokens[ONE];

        log.info("middle token = "+middleToken);

        String secondTokens[] = middleToken.split(HYPHEN);

        log.info("secondtokens len "+secondTokens.length);

        if (secondTokens.length != FOUR) {
            return null;
        }
        String description = secondTokens[TWO];

        log.info("description "+description);
        
        //description is a string with delimiter "_" separating a string and ID.
        //For Example : "SMART_100"
        String[] lastTokens = description.split(UNDERSCORE);

        log.info("lasttokens len"+lastTokens.length);
        
        if (lastTokens.length != TWO) {
            return null;
        }
        String ID = lastTokens[ONE];
        
        log.info("ID = "+ ID);
        return ID;
    }

    private static String getFileData(File output) {
        String retValue = GeneralConstants.EMPTY_STRING;
//        FileInputStream fis = null;
        FileReader fr = null;
        try {
//            fis = new FileInputStream(output);
            fr = new FileReader(output);
//            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer data = new StringBuffer();
            String line = br.readLine();
            while (line != null) {
                data.append(line);
                line = br.readLine();
            }
            retValue = data.toString();
            return retValue;
        } catch (Exception ex) {
            log.error("Cannot read File.",ex);
        } finally {
            try {
//                fis.close();
                fr.close();
            } catch (IOException ex) {
                log.error("Cannot close file",ex);
            }
        }

        return retValue;
    }
    }
