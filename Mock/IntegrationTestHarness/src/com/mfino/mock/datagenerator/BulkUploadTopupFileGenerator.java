///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.mfino.mock.datagenerator;
//
//import com.mfino.dao.MerchantDAO;
//import com.mfino.dao.MfinoServiceProviderDAO;
//import com.mfino.dao.SubscriberDAO;
//import com.mfino.dao.SubscriberMDNDAO;
//import com.mfino.dao.UserDAO;
//import com.mfino.dao.query.SubscriberMdnQuery;
//import com.mfino.domain.Merchant;
//import com.mfino.domain.Subscriber;
//import com.mfino.domain.SubscriberMDN;
//import com.mfino.domain.User;
//import com.mfino.domain.mFinoServiceProvider;
//import com.mfino.fix.CmFinoFIX;
//import com.mfino.hibernate.Timestamp;
//import com.mfino.util.PasswordGenUtil;
//import com.mfino.util.logging.DefaultLogger;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
///**
// *
// * @author sunil
// */
//public class BulkUploadTopupFileGenerator {
//
//    final int noOfTransactions = 10;
//    final int startingAmount = 6000;
//
//    public String createTopupUploadFile() throws IOException {
//        String filePath = System.getProperty("user.dir") + "/topupFile.csv";
//
//        File topupFile = new File(filePath);
//
//
//        if (!topupFile.exists()) {
//            topupFile.createNewFile();
//        }
//        PrintWriter output = new PrintWriter(new FileWriter(topupFile));
//
//        String str = new String();
//        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
//        Date dt = new Date();
//        output.println(String.format("%s,%s,%s",
//                df.format(dt).toString(),
//                noOfTransactions,
//                "Topup"));
//        String sourceNumber = "62" + PasswordGenUtil.generate("0123456789", 8);
//        long destNumberSeries = Long.parseLong(sourceNumber) % 10000;
//        insertMDNToDB(sourceNumber);
//        str = sourceNumber;
//        str += ",";
//        long totalAmount = 0;
//        long hashTotal = 0;
//
//        long mdnLstFourDigits = 0;
//        long amountFistFourDigits = 0;
//
//        for (int i = 0; i < noOfTransactions; i++) {
//            totalAmount += startingAmount + i;
//            destNumberSeries++;
//            mdnLstFourDigits = destNumberSeries % 10000;
//            String temp = Integer.toString(startingAmount + i);
//            amountFistFourDigits = Long.parseLong(temp.substring(0, 4));
//            hashTotal += (mdnLstFourDigits * amountFistFourDigits);
//
//        }
//        destNumberSeries = Long.parseLong(sourceNumber);
//
//        output.println(String.format("%s,%s,%s,%s",
//                sourceNumber,
//                totalAmount,
//                hashTotal,
//                "TopUp for stress test ver.1"));
//
//
//        for (int i = 0;i < noOfTransactions;i++) {
//            mdnLstFourDigits = 0;
//            amountFistFourDigits = 0;
//            hashTotal = 0;
//            destNumberSeries++;
//            mdnLstFourDigits = destNumberSeries % 10000;
//            String temp = "" + (startingAmount + i);
//            amountFistFourDigits = Long.parseLong(temp.substring(0, 4));
//            hashTotal = (mdnLstFourDigits * amountFistFourDigits);
//            output.println(String.format("%s,%s,%s,%s",
//                    destNumberSeries,
//                    (startingAmount + i),
//                    hashTotal,
//                    "TopUp for stress test ver.1 and Row Count" + i));
//        }
//
//        output.close();
//        return sourceNumber;
//    }
//
//    private void insertMDNToDB(String mdn) {
//
//        SubscriberMdnQuery mdnQuery = new SubscriberMdnQuery();
//        mdnQuery.setExactMDN(mdn);
//        SubscriberMDNDAO dao = new SubscriberMDNDAO();
//        List<SubscriberMDN> results = dao.get(mdnQuery);
//        //We don't Insert a MDN into DB if it already exists
//        if (results.size() > 0) {
//            return;
//        }
//
//        SubscriberDAO subDao = new SubscriberDAO();
//        MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();
//        mFinoServiceProvider msp = mspDao.getById(1L);
//
//        SubscriberMDN mdn1 = new SubscriberMDN();
//        Subscriber sub = new Subscriber();
//        sub.setFirstName("Sunny");
//        sub.setCurrency(CmFinoFIX.Currency_USD);
//        sub.setActivationTime(new Timestamp());
//        sub.setCreateTime(new Timestamp());
//        sub.setCreatedBy("user");
//        sub.setEmail("sunil@mfino.com");
//        sub.setLanguage(CmFinoFIX.Language_English);
//        sub.setLastName("Sunny");
//        sub.setLastUpdateTime(new Timestamp());
//        sub.setNotificationMethod((CmFinoFIX.NotificationMethod_Email | CmFinoFIX.NotificationMethod_SMS));
//        sub.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
//        sub.setStatus(CmFinoFIX.SubscriberStatus_Active);
//        sub.setStatusTime(new Timestamp());
//        sub.setTimezone(CmFinoFIX.Timezone_East_Indonesia_Time);
//        sub.setType(CmFinoFIX.SubscriberType_Merchant_E_Load);
//        sub.setUpdatedBy("user");
//
//        UserDAO usrDao =
//                new UserDAO();
//        User userObj =
//                usrDao.getByUserName("mfino");
//        sub.setUser(userObj);
//
//        sub.setmFinoServiceProviderByMSPID(msp);
//
//        subDao.save(sub);
//
//        mdn1.setMDN(mdn);
//        mdn1.setSubscriber(sub);
//
//        mdn1.setCreateTime(new Timestamp());
//        mdn1.setRestrictions(new Integer(15));
//        mdn1.setStatus(new Integer(0));
//        mdn1.setActivationTime(new Timestamp());
//        mdn1.setAuthenticationPhoneNumber("629000000");
//        mdn1.setAuthenticationPhrase("629000000");
//        mdn1.setCreatedBy("user");
//        mdn1.setDigestedPIN("123456");
//        mdn1.setLastTransactionID(Long.MIN_VALUE);
//        mdn1.setLastTransactionTime(new Timestamp());
//        mdn1.setLastUpdateTime(new Timestamp());
//        mdn1.setStatusTime(new Timestamp());
//        mdn1.setUpdatedBy("user");
//        mdn1.setWrongPINCount(0);
//        dao.save(mdn1);
//
//
//        Merchant merchant = new Merchant();
//        MerchantDAO merchantDAO = new MerchantDAO();
//        merchant.setAddressByFranchiseOutletAddressID(null);
//        merchant.setAddressByMerchantAddressID(null);
//        merchant.setAuthorizedRepresentative("sunny");
//        merchant.setClassification(CmFinoFIX.Classification_Hypermarket);
//        merchant.setDesignation("fdsfds");
//        merchant.setFaxNumber("80989090");
//        merchant.setFranchisePhoneNumber("7987987");
//        merchant.setIndustryClassification(CmFinoFIX.Classification_Hypermarket);
//        merchant.setNumberOfOutlets(Integer.MAX_VALUE);
//        merchant.setRepresentativeName("ipoipopiopoi");
//        merchant.setSubscriber(sub);
//        merchant.setTradeName("Sunny");
//        merchant.setTypeOfOrganization(CmFinoFIX.TypeOfOrganization_Cooperative);
//        merchant.setWebSite("www.mfino.com");
//        merchant.setStatusTime(new Timestamp());
//        merchantDAO.save(merchant);
//    }
//}
//
