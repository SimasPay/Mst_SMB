/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mfino.domain.MerchantSyncRecord;

/**
 *
 * @author Raju
 */
public class GenerateMerchants {

    public static void fillMerchant(MerchantSyncRecord m, int k) {
        int mdn = 628915130 + k;
        String language[] = {"en_US", "id_ID"};
        String timeZone[] = {"Central_Indonesia_Time", "East_Indonesia_Time", "UTC", "West_Indonesia_Time"};
        String currency[] = {"IDR", "USD"};
        m.setMdn(mdn + "");
        m.setUserName("MerchanBt1" + k);
        m.setLanguage(language[k % 2]);
        m.setTimezone(timeZone[k % 4]);
        m.setGroupId(StringUtils.EMPTY);
        m.setCurrency(currency[k % 2]);
        m.setDistChainTemplate(StringUtils.EMPTY);
        m.setStatus("ACTIVE");
        m.setParentId("341");
        m.setPartnerType("Merchant E-Load");
        m.setRegion("Hyderabad");
        m.setFirstName("Merchant" + k);
        m.setLastName("Last" + k);
        m.setTradeName("");
        m.setEmail("gen@mfino.com");
        m.setAdminComment("TEST");
        //	private String notificationMethod = StringUtils.EMPTY;
        m.setNotificationMethod("");
        m.setLine1("ABC");
        m.setLine2("Line 2");
        m.setCity("Hyderabad");
        m.setState("Texas");
        m.setCountry("U.S.");
        //	private String state = StringUtils.EMPTY;
        //	private String country = StringUtils.EMPTY;
        m.setZip("500032");
        m.setOutletType("");
//	private String outletType = StringUtils.EMPTY;
       m.setContactNumber(mdn + "");
       m.setFaxNumber("Fax");
//	private String faxNumber = StringUtils.EMPTY;
        m.setOrgType("1");
        m.setIndustryClassification("1");//
        m.setWebsiteURL("www.mfino.com");
        m.setOutletCnt("");
//	private String outletCnt = StringUtils.EMPTY;
        m.setYearEstablished("2010");//
        m.setOutletLine1("");
        m.setOutletLine2("");
        m.setOutletCity("");
        m.setOutletState("");
        m.setOutletCountry("");
//	private String outletAddress = StringUtils.EMPTY;
//	private String outletCity = StringUtils.EMPTY;
//	private String outletState = StringUtils.EMPTY;
//	private String outletCountry = StringUtils.EMPTY;
        m.setOutletZip("1");
        m.setRepresentativeName("Rep" + k);
        m.setPosition("");
        m.setRepContactNumber("");
        m.setOutletFaxNumber("");
        m.setOutletEmail("");
        m.setSrcIP("");
//	private String representativeName = StringUtils.EMPTY; // Mandatory
//	private String position = StringUtils.EMPTY;
//	private String repContactNumber = StringUtils.EMPTY;
//	private String outletFaxNumber = StringUtils.EMPTY;
//	private String outletEmail = StringUtils.EMPTY;
//	private String srcIP = StringUtils.EMPTY;
    }

    public static void main(String[] args) throws IOException {
        int k = 1; // String filePath = System.getProperty("user.dir") // +
        // "\\Subscribers\\GenerateSubscribers" + k + ".csv";
        String filePath = "E:\\BulkUploadMerchant" + ".csv";
        File MerchantFile = new File(filePath);
        if (!MerchantFile.exists()) {
            MerchantFile.createNewFile();
        }
        PrintWriter output = new PrintWriter(new FileWriter(MerchantFile));
        List<MerchantSyncRecord> results = new ArrayList<MerchantSyncRecord>();
        String formatspecifier = new String();
        for (int i = 0; i < 43; i++) {
            formatspecifier += "%s|";
        }
        formatspecifier += "%s";
        for (k = 0; k < 5000; k++) {
            MerchantSyncRecord m = new MerchantSyncRecord();
            fillMerchant(m, k);
            results.add(m);
			output.println(String.format(formatspecifier,
									 k,
                                                                         m.getMdn(),
                                                                         m.getUserName(),
                                                                         m.getLanguage(),
                                                                         m.getTimezone(),
                                                                         m.getGroupId(),
                                                                         m.getCurrency(),
                                                                         m.getDistChainTemplate(),
                                                                         m.getStatus(),                                                                         
                                                                         m.getParentId(),
                                                                         m.getPartnerType(),
                                                                         m.getRegion(),
                                                                         m.getFirstName(),
                                                                         m.getLastName(),
                                                                         m.getTradeName(),
                                                                         m.getEmail(),
                                                                         m.getAdminComment(),
                                                                         m.getNotificationMethod(),
                                                                         m.getLine1(),
                                                                         m.getLine2(),
                                                                         m.getCity(),
                                                                         m.getState(),
                                                                         m.getCountry(),
                                                                         m.getZip(),
                                                                         m.getOutletType(),
                                                                         m.getContactNumber(),
                                                                         m.getFaxNumber(),
                                                                         m.getOrgType(),
                                                                         m.getIndustryClassification(),
                                                                         m.getWebsiteURL(),
                                                                         m.getOutletCnt(),
                                                                         m.getYearEstablished(),
                                                                         m.getOutletLine1(),
                                                                         m.getOutletLine2(),
                                                                         m.getOutletCity(),
                                                                         m.getOutletState(),
                                                                         m.getOutletCountry(),
                                                                         m.getOutletZip(),
                                                                         m.getRepresentativeName(),
                                                                         m.getPosition(),
                                                                         m.getRepContactNumber(),
                                                                         m.getOutletFaxNumber(),
                                                                         m.getOutletEmail(),
                                                                         m.getSrcIP()
                                                                        ));
        }
        output.close();
        // this completes the insertion of the Subscribers

        System.out.println("TestRaju");

    }
}
