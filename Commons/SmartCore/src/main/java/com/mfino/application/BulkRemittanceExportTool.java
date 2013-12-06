/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.BulkUploadDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.BulkUploadQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;

/**
 *
 * @author sandeepjs
 */
public class BulkRemittanceExportTool {

    private static FTPClient client = null;

    private static Logger log = LoggerFactory.getLogger(BulkRemittanceExportTool.class);

    public static void doUpload(BulkUpload entry) throws IOException, FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(entry.getOutFileName());
        fos.write(entry.getOutFileData().getBytes());
        fos.close();
        FileInputStream fis = new FileInputStream(entry.getOutFileName());
        boolean success = client.storeFile(entry.getOutFileName(), fis);
        if (success) {
            entry.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Uploaded);
        } else {
            log.error("Failed !!! the file could not be uploaded !!");
        }
    }

    public static boolean getFTPClient() {
        URL ftpUrl = null;
        try {
            ftpUrl = new URL(ConfigurationUtil.getBulkRemittanceExportFtpServer());
        } catch (MalformedURLException urlEx) {
            log.error("Ftp url is in a wrong format. e.g. ftp://somehost:21/dirtofile", urlEx);
            return false;
        }
        
        try {
            client = new FTPClient();
            client.connect(ftpUrl.getHost());
            if (ConfigurationUtil.getBulkRemittanceExportFtpDoLogin()) {
                client.login(ConfigurationUtil.getBulkRemittanceExportFtpUsername(), 
                		ConfigurationUtil.getBulkRemittanceExportFtpPassword());
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

    public static void main(String[] args) {
        BulkRemittanceExportTool.startExport();
    }

    public static void startExport() {
    	log.info("Start Bulk Remittance Export");
    	
        if(!getFTPClient()){
        	return;
        }
        if (client != null) {
            List<BulkUpload> batch = getBatch();

            while (batch.size() > 0) {
                for (int i = 0; i < batch.size(); i++) {
                    BulkUpload entry = (BulkUpload) batch.get(i);
                    processExport(entry);
                }
                // get new batch
                batch = getBatch();
            }
        }
    }

    public static List<BulkUpload> getBatch() {
        BulkUploadDAO dao = DAOFactory.getInstance().getBulkUploadDAO();

        int batchSize = ConfigurationUtil.getBulkRemittanceExportBatchSize();

        BulkUploadQuery query = new BulkUploadQuery();

        query.setStart(0);
        query.setLimit(batchSize);
        query.setFileStatus(CmFinoFIX.BulkUploadDeliveryStatus_ReadyForBankUpload);
        query.setDeliveryDate(new Date());

        HibernateUtil.getCurrentSession().beginTransaction();

        List<BulkUpload> batch = dao.get(query);

        HibernateUtil.getCurrentSession().getTransaction().rollback();

        return batch;
    }

    public static void processExport(BulkUpload entry) {
        try {
            doUpload(entry);
        } catch (Exception exp) {
			log.error(exp.getMessage(), exp);
        } finally {
            entry.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_UploadedToBank);
            entry.setBankUploadTryCounter(entry.getBankUploadTryCounter() + 1);
            entry.setBankUploadLastTryDate(new Timestamp());

            BulkUploadDAO dao = DAOFactory.getInstance().getBulkUploadDAO();
            HibernateUtil.getCurrentSession().beginTransaction();
            dao.save(entry);
            HibernateUtil.getCurrentSession().getTransaction().commit();
        }
    }
}
