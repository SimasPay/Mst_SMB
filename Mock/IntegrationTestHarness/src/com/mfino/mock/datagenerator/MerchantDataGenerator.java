///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.mfino.application;
//
//
//import com.mfino.dao.MerchantDAO;
//import com.mfino.dao.MfinoServiceProviderDAO;
//import com.mfino.dao.PocketDAO;
//import com.mfino.dao.PocketTemplateDAO;
//import com.mfino.dao.SubscriberDAO;
//import com.mfino.dao.SubscriberMDNDAO;
//import com.mfino.dao.UserDAO;
//import com.mfino.dao.query.SubscriberMdnQuery;
//import com.mfino.domain.Merchant;
//import com.mfino.domain.Pocket;
//import com.mfino.domain.PocketTemplate;
//import com.mfino.domain.Subscriber;
//import com.mfino.domain.SubscriberMDN;
//import com.mfino.domain.User;
//import com.mfino.domain.mFinoServiceProvider;
//import com.mfino.fix.CmFinoFIX;
//import com.mfino.hibernate.Timestamp;
//import com.mfino.service.MerchantService;
//import com.mfino.util.HibernateUtil;
//import com.mfino.util.PasswordGenUtil;
//import com.mfino.util.logging.DefaultLogger;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import org.junit.After;
//import org.junit.Ignore;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.springframework.security.providers.encoding.PasswordEncoder;
//import org.springframework.security.providers.encoding.ShaPasswordEncoder;
//
///**
// *
// * @author sunil
// */
////@Ignore
//public class MerchantDataGenerator {
//
//    final int noOfMerchants = 500;
//    final String bucketType[] = {"reg", "Cal", "Dat", "SPL"};
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
//
//    @Before
//    public void setUp() {
//        HibernateUtil.getCurrentSession().beginTransaction();
//       }
//
//    @After
//    public void tearDown() {
//        if(HibernateUtil.getCurrentSession().getTransaction().isActive())
//            HibernateUtil.getCurrentSession().getTransaction().commit();
//    }
//
//
//
//    public void createVerticalHierarchy() throws IOException {
//        String filePath = System.getProperty("user.dir");
//        File resetPinFile = new File(filePath + "/"+ITHConstants.MERCHANT_FILE+".csv");
//        if (!resetPinFile.exists()) {
//            resetPinFile.createNewFile();
//        }
//
//        PrintWriter resetPinWriter = new PrintWriter(new FileWriter(resetPinFile));
//
//        if (!HibernateUtil.getCurrentSession().getTransaction().isActive()) {
//            HibernateUtil.getCurrentSession().beginTransaction();
//        }
//
//        UserDAO usrdao = new UserDAO();
//        User usr = usrdao.getByUserName("user");
//        String mdn = new String();
//        String destnMdn = new String();
//        String contactNumber = new String();
//        String authenticationPhrase = "";
//        resetPinWriter.println(String.format("%s", noOfMerchants));
//        Merchant oldMerchant=MerchantService.getMerchantFromMDN("6299999999999");
//        if(oldMerchant==null){
//            DefaultLogger.error("Please create a Merchant with MDN 6299999999999 and re run the test again ");
//            return;
//        }
//        SubscriberMdnQuery mdnQuery = new SubscriberMdnQuery();
//        SubscriberMDNDAO dao = new SubscriberMDNDAO();
//        Set<SubscriberMDN> submdn = oldMerchant.getSubscriber().getSubscriberMDNFromSubscriberID();
//        SubscriberMDN subcrMdn = (SubscriberMDN) submdn.toArray()[0];
//        List<SubscriberMDN> results;
//        int amount = 5000;
//        int rechargeAmount = 6000;
//        int distrubuteAmount = 4000;
//        int bucketValue = 0;
//        String message = new String();
//        String mcashMessage = new String();
//        try {
//            for (int i = 0; i < noOfMerchants; i++) {
//                mdn = "62" + PasswordGenUtil.generate("0123456789", 8);
//                destnMdn = "62" + PasswordGenUtil.generate("0123456789", 8);
//                contactNumber = "62" + PasswordGenUtil.generate("0123456789", 8);
//                if (!HibernateUtil.getCurrentSession().getTransaction().isActive()) {
//                    HibernateUtil.getCurrentSession().beginTransaction();
//                }
//                mdnQuery.setExactMDN(mdn);
//                results = dao.get(mdnQuery);
//
//                //We don't Insert a MDN into DB if it already exists
//                if (results.size() > 0) {
//                    i--;
//                } else {
//                    authenticationPhrase = "MerchantTestData" + i;
//                    amount = 5000 + i;
//                    rechargeAmount = rechargeAmount + i;
//                    distrubuteAmount = distrubuteAmount + i;
//                    bucketValue = i % 4;
//                    message = "Message:" + i;
//                    mcashMessage = "MCashMessage" + i;
//                    oldMerchant=insertAndGetMerchant(mdn, i, usr, oldMerchant);
//                    resetPinWriter.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
//                            "123456", "123456", "123456", amount, "123456",
//                            contactNumber, mdn, authenticationPhrase, destnMdn, bucketType[bucketValue],
//                            rechargeAmount, distrubuteAmount, message, mcashMessage));
//                }
//                results = null;
//            }
//        } finally {
//
//            resetPinWriter.close();
//        }
//
//    }
//    @Test
//    public void prepareMerchantFiles() throws IOException {
//        String filePath = System.getProperty("user.dir");
//        File resetPinFile = new File(filePath + "/"+ITHConstants.MERCHANT_FILE+".csv");
//        if (!resetPinFile.exists()) {
//            resetPinFile.createNewFile();
//        }
//
//        PrintWriter resetPinWriter = new PrintWriter(new FileWriter(resetPinFile));
//
//        if (!HibernateUtil.getCurrentSession().getTransaction().isActive()) {
//            HibernateUtil.getCurrentSession().beginTransaction();
//        }
//
//        UserDAO usrdao = new UserDAO();
//        User usr = usrdao.getByUserName("user");
//        String mdn = new String();
//        String destnMdn = new String();
//        String contactNumber = new String();
//        String authenticationPhrase = "";
//        resetPinWriter.println(String.format("%s", noOfMerchants));
//        Merchant parentMerchant=MerchantService.getMerchantFromMDN("6299999999999");
//        if(parentMerchant==null){
//            DefaultLogger.error("Please create a Merchant with MDN 6299999999999 and re run the test again ");
//            return;
//        }
//        SubscriberMdnQuery mdnQuery = new SubscriberMdnQuery();
//        SubscriberMDNDAO dao = new SubscriberMDNDAO();
//        List<SubscriberMDN> results;
//        int amount = 5000;
//        int rechargeAmount = 6000;
//        int distrubuteAmount = 4000;
//        int bucketValue = 0;
//        String message = new String();
//        String mcashMessage = new String();
//        try {
//            for (int i = 0; i < noOfMerchants; i++) {
//                mdn = "62" + PasswordGenUtil.generate("0123456789", 8);
//                destnMdn = "62" + PasswordGenUtil.generate("0123456789", 8);
//                contactNumber = "62" + PasswordGenUtil.generate("0123456789", 8);
//                if (!HibernateUtil.getCurrentSession().getTransaction().isActive()) {
//                    HibernateUtil.getCurrentSession().beginTransaction();
//                }
//                mdnQuery.setExactMDN(mdn);
//                results = dao.get(mdnQuery);
//
//                //We don't Insert a MDN into DB if it already exists
//                if (results.size() > 0) {
//                    i--;
//                } else {
//                    authenticationPhrase = "MerchantTestData" + i;
//                    amount = 5000 + i;
//                    rechargeAmount = rechargeAmount + i;
//                    distrubuteAmount = distrubuteAmount + i;
//                    bucketValue = i % 4;
//                    message = "Message:" + i;
//                    mcashMessage = "MCashMessage" + i;
//                    insertMerchant(mdn, i, usr, parentMerchant);
//                    resetPinWriter.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
//                            "123456", "123456", "123456", amount, "123456",
//                            contactNumber, mdn, authenticationPhrase, destnMdn, bucketType[bucketValue],
//                            rechargeAmount, distrubuteAmount, message, mcashMessage));
//                }
//                mdn="";
//                results = null;
//            }
//        } finally {
//
//            resetPinWriter.close();
//        }
//    }
//
//    private Pocket insertSubscriberPocket(SubscriberMDN submdn, int pocketno, List<PocketTemplate> results) {
//        if(submdn== null){
//            return null;
//        }
//
//        PocketDAO pdao = new PocketDAO();
//        Pocket p = new Pocket();
//        int size= results.size();
//        if (size > 0) {
//            p.setPocketTemplate(results.get(pocketno%size));
//            p.setSubscriberMDNByMDNID(submdn);
//            p.setSubscriber(submdn.getSubscriber());
//            if(p.getPocketTemplate().getType().equals(CmFinoFIX.PocketType_BankAccount)){
//                p.setCardPAN("5555555555");
//            }
//            p.setStatus(CmFinoFIX.PocketStatus_Active);
//        } else {
//            String randomVal= PasswordGenUtil.generate("0123456789", 3);
//            PocketTemplate pt = new PocketTemplate();
//            pt.setDescription("Test");
//            pt.setType(pocketno + 1);
//            pt.setCommodity(CmFinoFIX.Commodity_Airtime);
//            pt.setBillingType(CmFinoFIX.BillingType_PrePaid);
//            pt.setBankAccountCardType(CmFinoFIX.BankAccountCardType_DebitCard);
//            pt.setBankCode(CmFinoFIX.BankCodeForRouting_Sinarmas);
//            pt.setOperatorCode(CmFinoFIX.OperatorCodeForRouting_CBOSS);
//            pt.setMaximumStoredValue(Long.MAX_VALUE);
//            pt.setMinimumStoredValue(Long.parseLong(randomVal)+1000);
//            pt.setMaxAmountPerTransaction(Long.parseLong(randomVal)+100000);
//            pt.setMinAmountPerTransaction(Long.parseLong(randomVal)+1000);
//            pt.setMaxAmountPerDay(Long.parseLong(randomVal)+1000000);
//            pt.setMaxAmountPerWeek(Long.parseLong(randomVal)+10000000);
//            pt.setMaxAmountPerMonth(Long.parseLong(randomVal)+100000000);
//            pt.setMinTimeBetweenTransactions(0);
//            pt.setMaxTransactionsPerDay(Integer.parseInt(randomVal)+ 10000);
//            pt.setMaxTransactionsPerWeek(Integer.parseInt(randomVal)+ 10000);
//            pt.setMaxTransactionsPerMonth(Integer.parseInt(randomVal)+ 100000);
//            pt.setCreateTime(new Timestamp());
//            pt.setCreatedBy("user");
//            pt.setUpdatedBy("user");
//            pt.setLastUpdateTime(new Timestamp());
//            //dao.save(pt);
//            p.setPocketTemplate(pt);
//            p.setSubscriberMDNByMDNID(submdn);
//            p.setStatus(CmFinoFIX.PocketStatus_Active);
//        }
//        p.setCreateTime(new Timestamp());
//        p.setCreatedBy("user");
//        p.setUpdatedBy("user");
//        p.setLastUpdateTime(new Timestamp());
//        p.setStatusTime(new Timestamp());
//        p.setIsDefault(Boolean.TRUE);
//        //pdao.save(p);
//        return p;
//    }
//
//    private void insertMerchant(String mdn, int count, User usr, Merchant parentMerchant) {
//        if (!HibernateUtil.getCurrentSession().getTransaction().isActive()) {
//            HibernateUtil.getCurrentSession().beginTransaction();
//        }
//        MerchantDAO merchantDAO = new MerchantDAO();
//        UserDAO usrDao = new UserDAO();
//        Merchant merchant = new Merchant();
//        SubscriberMDNDAO dao = new SubscriberMDNDAO();
//        SubscriberDAO subDao = new SubscriberDAO();
//        MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();
//        mFinoServiceProvider msp = mspDao.getById(1L);
//
//        SubscriberMDN mdn1 = new SubscriberMDN();
//        Subscriber sub = new Subscriber();
//        String randomStr = "MerchantTestData:" + count;
//        sub.setFirstName(randomStr);
//        sub.setSecurityAnswer(randomStr);
//        sub.setLastName(randomStr);
//
//        sub.setCurrency(CmFinoFIX.Currency_USD);
//        sub.setActivationTime(new Timestamp());
//        sub.setCreateTime(new Timestamp());
//        sub.setCreatedBy(usr.getUsername());
//        sub.setEmail("sunil@mfino.com");
//        sub.setLanguage(CmFinoFIX.Language_English);
//
//        sub.setLastUpdateTime(new Timestamp());
//        sub.setNotificationMethod((CmFinoFIX.NotificationMethod_Email | CmFinoFIX.NotificationMethod_SMS));
//        sub.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
//        sub.setStatus(CmFinoFIX.SubscriberStatus_Active);
//        sub.setStatusTime(new Timestamp());
//        sub.setTimezone(CmFinoFIX.Timezone_East_Indonesia_Time);
//        sub.setType(CmFinoFIX.SubscriberType_Merchant_E_Load);
//        sub.setUpdatedBy(usr.getUsername());
//        sub.setParentID(parentMerchant.getID());
//
//        User usr1 = new User();
//        String usrName = "MerchantTestData" + mdn;
//        usr1.setUsername(usrName);
//        usr1.setAdminComment("Created using Test Generator");
//        usr1.setCreateTime(new Timestamp());
//        usr1.setFirstTimeLogin(Boolean.FALSE);
//        usr1.setFailedLoginCount(0);
//        usr1.setEmail("sunil@mfino.com");
//        usr1.setLanguage(CmFinoFIX.Language_English);
//        usr1.setRole(14);
//
//        PasswordEncoder encoder = new ShaPasswordEncoder(1);
//        String encPassword = encoder.encodePassword("user123*", usrName);
//        usr1.setPassword(encPassword);
//        usr1.setRestrictions(0);
//        usr1.setStatus(CmFinoFIX.UserStatus_Active);
//        usrDao.save(usr1);
//        sub.setUser(usr1);
//        sub.setmFinoServiceProviderByMSPID(msp);
//
//        subDao.save(sub);
//
//        mdn1.setMDN(mdn);
//        mdn1.setSubscriber(sub);
//
//        mdn1.setCreateTime(new Timestamp());
//        mdn1.setRestrictions(new Integer(15));
//        mdn1.setStatus(CmFinoFIX.SubscriberStatus_Active);
//        mdn1.setActivationTime(new Timestamp());
//        mdn1.setAuthenticationPhoneNumber("629000000");
//        mdn1.setAuthenticationPhrase(randomStr);
//        mdn1.setCreatedBy(usr.getUsername());
//        mdn1.setDigestedPIN("D47FCFF3F709369790141743CB7A308A9D9FD6A9");
//        mdn1.setMerchantDigestedPIN("D47FCFF3F709369790141743CB7A308A9D9FD6A9");
//        mdn1.setLastTransactionID(Long.MIN_VALUE);
//        mdn1.setLastTransactionTime(new Timestamp());
//        mdn1.setLastUpdateTime(new Timestamp());
//        mdn1.setStatusTime(new Timestamp());
//        mdn1.setUpdatedBy("user");
//        mdn1.setWrongPINCount(0);
//        mdn1.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
//
//        merchant.setAddressByFranchiseOutletAddressID(null);
//        merchant.setAddressByMerchantAddressID(null);
//        merchant.setAuthorizedRepresentative(randomStr);
//        merchant.setClassification(CmFinoFIX.Classification_Hypermarket);
//        merchant.setDesignation(randomStr);
//        merchant.setFaxNumber("9999999999");
//        merchant.setFranchisePhoneNumber("9999999999");
//        merchant.setIndustryClassification(randomStr);
//        merchant.setNumberOfOutlets(90);
//        merchant.setRepresentativeName(randomStr);
//        merchant.setSubscriber(sub);
//        merchant.setTradeName(randomStr);
//        merchant.setTypeOfOrganization(CmFinoFIX.TypeOfOrganization_Association);
//        merchant.setWebSite("sasas");
//        merchant.setStatusTime(new Timestamp());
//        merchant.setMerchantByParentID(parentMerchant);
//        merchant.setStatus(CmFinoFIX.SubscriberStatus_Active);
//
//        PocketTemplateDAO ptdao = new PocketTemplateDAO();
//        List<PocketTemplate> ptResults = ptdao.getAll();
//        Set<Pocket> pocketSet= new HashSet<Pocket>(3);
//        Pocket p1;
//        for (int p = 0; p < 3; p++) {
//            p1 = insertSubscriberPocket(mdn1, p, ptResults);
//            if (p1 != null) {
//                pocketSet.add(p1);
//            }
//        }
//        if (pocketSet != null) {
//            mdn1.setPocketFromMDNID(pocketSet);
//        }
//
//        dao.save(mdn1);
//        merchantDAO.save(merchant);
//
//
//        if (HibernateUtil.getCurrentSession().getTransaction().isActive()) {
//            HibernateUtil.getCurrentSession().getTransaction().commit();
//        }
//        //Clean Up
//        pocketSet.clear();
//        ptdao=null;
//        ptResults=null;
//        merchantDAO = null;
//        usrDao = null;
//        dao = null;
//        subDao =null;
//        mspDao = null;
//        msp = null;
//        //return merchant;
//    }
//    private Merchant insertAndGetMerchant(String mdn, int count, User usr, Merchant parentMerchant) {
//        if (!HibernateUtil.getCurrentSession().getTransaction().isActive()) {
//            HibernateUtil.getCurrentSession().beginTransaction();
//        }
//        MerchantDAO merchantDAO = new MerchantDAO();
//        UserDAO usrDao = new UserDAO();
//        Merchant merchant = new Merchant();
//        SubscriberMDNDAO dao = new SubscriberMDNDAO();
//        SubscriberDAO subDao = new SubscriberDAO();
//        MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();
//        mFinoServiceProvider msp = mspDao.getById(1L);
//
//        SubscriberMDN mdn1 = new SubscriberMDN();
//        Subscriber sub = new Subscriber();
//        String randomStr = "MerchantTestData:" + count;
//        sub.setFirstName(randomStr);
//        sub.setSecurityAnswer(randomStr);
//        sub.setLastName(randomStr);
//
//        sub.setCurrency(CmFinoFIX.Currency_USD);
//        sub.setActivationTime(new Timestamp());
//        sub.setCreateTime(new Timestamp());
//        sub.setCreatedBy(usr.getUsername());
//        sub.setEmail("sunil@mfino.com");
//        sub.setLanguage(CmFinoFIX.Language_English);
//
//        sub.setLastUpdateTime(new Timestamp());
//        sub.setNotificationMethod((CmFinoFIX.NotificationMethod_Email | CmFinoFIX.NotificationMethod_SMS));
//        sub.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
//        sub.setStatus(CmFinoFIX.SubscriberStatus_Active);
//        sub.setStatusTime(new Timestamp());
//        sub.setTimezone(CmFinoFIX.Timezone_East_Indonesia_Time);
//        sub.setType(CmFinoFIX.SubscriberType_Merchant_E_Load);
//        sub.setUpdatedBy(usr.getUsername());
//        sub.setParentID(parentMerchant.getID());
//
//        User usr1 = new User();
//        String usrName = "MerchantTestData" + mdn;
//        usr1.setUsername(usrName);
//        usr1.setAdminComment("Created using Test Generator");
//        usr1.setCreateTime(new Timestamp());
//        usr1.setFirstTimeLogin(Boolean.FALSE);
//        usr1.setFailedLoginCount(0);
//        usr1.setEmail("sunil@mfino.com");
//        usr1.setLanguage(CmFinoFIX.Language_English);
//        usr1.setRole(14);
//
//        PasswordEncoder encoder = new ShaPasswordEncoder(1);
//        String encPassword = encoder.encodePassword("user123*", usrName);
//        usr1.setPassword(encPassword);
//        usr1.setRestrictions(0);
//        usr1.setStatus(CmFinoFIX.UserStatus_Active);
//        usrDao.save(usr1);
//        sub.setUser(usr1);
//        sub.setmFinoServiceProviderByMSPID(msp);
//
//        subDao.save(sub);
//
//        mdn1.setMDN(mdn);
//        mdn1.setSubscriber(sub);
//
//        mdn1.setCreateTime(new Timestamp());
//        mdn1.setRestrictions(new Integer(15));
//        mdn1.setStatus(CmFinoFIX.SubscriberStatus_Active);
//        mdn1.setActivationTime(new Timestamp());
//        mdn1.setAuthenticationPhoneNumber("629000000");
//        mdn1.setAuthenticationPhrase(randomStr);
//        mdn1.setCreatedBy(usr.getUsername());
//        mdn1.setDigestedPIN("D47FCFF3F709369790141743CB7A308A9D9FD6A9");
//        mdn1.setMerchantDigestedPIN("D47FCFF3F709369790141743CB7A308A9D9FD6A9");
//        mdn1.setLastTransactionID(Long.MIN_VALUE);
//        mdn1.setLastTransactionTime(new Timestamp());
//        mdn1.setLastUpdateTime(new Timestamp());
//        mdn1.setStatusTime(new Timestamp());
//        mdn1.setUpdatedBy("user");
//        mdn1.setWrongPINCount(0);
//        mdn1.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
//
//        merchant.setAddressByFranchiseOutletAddressID(null);
//        merchant.setAddressByMerchantAddressID(null);
//        merchant.setAuthorizedRepresentative(randomStr);
//        merchant.setClassification(CmFinoFIX.Classification_Hypermarket);
//        merchant.setDesignation(randomStr);
//        merchant.setFaxNumber("9999999999");
//        merchant.setFranchisePhoneNumber("9999999999");
//        merchant.setIndustryClassification(randomStr);
//        merchant.setNumberOfOutlets(90);
//        merchant.setRepresentativeName(randomStr);
//        merchant.setSubscriber(sub);
//        merchant.setTradeName(randomStr);
//        merchant.setTypeOfOrganization(CmFinoFIX.TypeOfOrganization_Association);
//        merchant.setWebSite("sasas");
//        merchant.setStatusTime(new Timestamp());
//        merchant.setMerchantByParentID(parentMerchant);
//        merchant.setStatus(CmFinoFIX.SubscriberStatus_Active);
//
//        PocketTemplateDAO ptdao = new PocketTemplateDAO();
//        List<PocketTemplate> ptResults = ptdao.getAll();
//        Set<Pocket> pocketSet= new HashSet<Pocket>(3);
//        Pocket p1;
//        for (int p = 0; p < 3; p++) {
//            p1 = insertSubscriberPocket(mdn1, p, ptResults);
//            if (p1 != null) {
//                pocketSet.add(p1);
//            }
//        }
//        if (pocketSet != null) {
//            mdn1.setPocketFromMDNID(pocketSet);
//        }
//
//        dao.save(mdn1);
//        merchantDAO.save(merchant);
//
//
//        if (HibernateUtil.getCurrentSession().getTransaction().isActive()) {
//            HibernateUtil.getCurrentSession().getTransaction().commit();
//        }
//        //Clean Up
//        pocketSet.clear();
//        ptdao=null;
//        ptResults=null;
//        merchantDAO = null;
//        usrDao = null;
//        dao = null;
//        subDao =null;
//        mspDao = null;
//        msp = null;
//        return merchant;
//    }
//    public class ITHConstants {
//
//    //Type of List
//    public static final int SUBSCRIBER_LIST = 1;
//    public static final int MERCHANT_LIST = 2;
//    public static final int MCASH_LIST = 3;
//
//    public static final String SUBSCRIBER_FILE ="subscriber_report" ;
//    public static final String MERCHANT_FILE = "merchant_report";
//    public static final String  MCASH_FILE = "mcash_report";
//}
//
//}
