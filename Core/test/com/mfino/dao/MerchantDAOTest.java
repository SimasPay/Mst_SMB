/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.dao.query.MerchantQuery;
import com.mfino.domain.Merchant;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.hibernate.Timestamp;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sunil
 */
public class MerchantDAOTest {

    private MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();
    private SubscriberMDNDAO mdndao = new SubscriberMDNDAO();
    private SubscriberDAO subDao = new SubscriberDAO();
    private mFinoServiceProvider msp = new mFinoServiceProvider();
    private Subscriber sub = new Subscriber();
    private SubscriberMDN mdn1 = new SubscriberMDN();
    private static Random random = new Random();
    private MerchantDAO merchantDAO = new MerchantDAO();
    private Merchant merchant = new Merchant();

    public MerchantDAOTest() {
        random.setSeed(System.currentTimeMillis());
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void insertTestData() {
        msp = mspDao.getById(1L);
        System.out.println(msp.getCreatedBy());

        sub.setFirstName("xxx");
        sub.setCurrency("dollar");
        sub.setActivationTime(new Timestamp());
        sub.setCreateTime(new Timestamp());
        sub.setCreatedBy("sas");
        sub.setEmail("sasa");
        sub.setLanguage(Integer.MAX_VALUE);
        sub.setLastName("sdas");
        sub.setLastUpdateTime(new Timestamp());
        sub.setNotificationMethod(Integer.MAX_VALUE);
        //sub.setParentID(Long.MIN_VALUE);
        sub.setRestrictions(new Integer(0));
        sub.setStatus(new Integer(0));
        sub.setStatusTime(new Timestamp());
        sub.setTimezone("sas");
        sub.setType(Integer.MAX_VALUE);
        sub.setUpdatedBy("sasa");
        CompanyDAO comp = new CompanyDAO();
        sub.setCompany(comp.getById(1L));
        sub.setmFinoServiceProviderByMSPID(msp);

        subDao.save(sub);


        mdn1.setMDN(getRandomMDN());
        mdn1.setSubscriber(sub);
        mdn1.setCreateTime(new Timestamp());
        mdn1.setRestrictions(new Integer(10));
        mdn1.setStatus(new Integer(10));
        mdn1.setActivationTime(new Timestamp());
        mdn1.setAuthenticationPhoneNumber("sunny");
        mdn1.setAuthenticationPhrase("sunny");
        mdn1.setCreatedBy("sunny");
        mdn1.setDigestedPIN("sds");
        mdn1.setLastTransactionID(Long.MIN_VALUE);
        mdn1.setLastTransactionTime(new Timestamp());
        mdn1.setLastUpdateTime(new Timestamp());
        mdn1.setStatusTime(new Timestamp());
        mdn1.setUpdatedBy("sunny");
        mdn1.setWrongPINCount(Integer.MAX_VALUE);
        mdndao.save(mdn1);


        merchant.setAddressByFranchiseOutletAddressID(null);
        merchant.setAddressByMerchantAddressID(null);
        merchant.setAuthorizedRepresentative("saas");
        merchant.setClassification("saas");
        merchant.setDesignation("sasa");
        merchant.setFaxNumber("sas");
        merchant.setFranchisePhoneNumber("sasas");
        merchant.setIndustryClassification("sasas");
        merchant.setNumberOfOutlets(Integer.MAX_VALUE);
        merchant.setRepresentativeName("sasas");
        merchant.setSubscriber(sub);
        merchant.setTradeName("sasa");
        merchant.setTypeOfOrganization("sasa");
        merchant.setWebSite("sasas");
        merchant.setStatusTime(new Timestamp());


        merchantDAO.save(merchant);


    }

    @Test
    public void testGetByHQL() {
        MerchantQuery query = new MerchantQuery();
        //query.setMdn("9871");
        //query.setUserName("mfi");

        query.setLimit(10);
        query.setStart(0);
        List<Merchant> results = merchantDAO.getByHQL(query);
        System.out.println("size =" + results.size());
        assertTrue(results.size() > 0);


    }

    private static String getRandomMDN() {
        String mdn = "1111";
        return mdn + (long) (random.nextDouble() * 10000L);
    }

    private static Long getRandomID() {
        return (long) (random.nextDouble() * 10000L);
    }
}

