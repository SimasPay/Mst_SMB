/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mfino.dao.query.MerchantQuery;
import com.mfino.domain.Merchant;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.hibernate.Timestamp;

/**
 *
 * @author sunil
 */
public class MerchantDAOTest {

    private MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();
    private SubscriberMDNDAO mdndao = new SubscriberMDNDAO();
    private SubscriberDAO subDao = new SubscriberDAO();
    private MfinoServiceProvider msp = new MfinoServiceProvider();
    private Subscriber sub = new Subscriber();
    private SubscriberMdn mdn1 = new SubscriberMdn();
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
        System.out.println(msp.getCreatedby());

        sub.setFirstname("xxx");
        sub.setCurrency("dollar");
        sub.setActivationtime(new Timestamp());
        sub.setCreatetime(new Timestamp());
        sub.setCreatedby("sas");
        sub.setEmail("sasa");
        sub.setLanguage(Integer.MAX_VALUE);
        sub.setLastname("sdas");
        sub.setLastupdatetime(new Timestamp());
        sub.setNotificationmethod(Integer.MAX_VALUE);
        //sub.setParentID(Long.MIN_VALUE);
        sub.setRestrictions(new Integer(0));
        sub.setStatus(new Integer(0));
        sub.setStatustime(new Timestamp());
        sub.setTimezone("sas");
        sub.setType(Integer.MAX_VALUE);
        sub.setUpdatedby("sasa");
        CompanyDAO comp = new CompanyDAO();
        sub.setCompany(comp.getById(1L));
        sub.setMfinoServiceProvider(msp);

        subDao.save(sub);


        mdn1.setMdn(getRandomMDN());
        mdn1.setSubscriber(sub);
        mdn1.setCreatetime(new Timestamp());
        mdn1.setRestrictions(new Integer(10));
        mdn1.setStatus(new Integer(10));
        mdn1.setActivationtime(new Timestamp());
        mdn1.setAuthenticationphonenumber("sunny");
        mdn1.setAuthenticationphrase("sunny");
        mdn1.setCreatedby("sunny");
        mdn1.setDigestedpin("sds");
        mdn1.setLasttransactionid(new java.math.BigDecimal(Long.MIN_VALUE));
        mdn1.setLasttransactiontime(new Timestamp());
        mdn1.setLastupdatetime(new Timestamp());
        mdn1.setStatustime(new Timestamp());
        mdn1.setUpdatedby("sunny");
        mdn1.setWrongpincount(Integer.MAX_VALUE);
        mdndao.save(mdn1);


        merchant.setAddressByFranchiseoutletaddressid(null);
        merchant.setAddressByMerchantaddressid(null);
        merchant.setAuthorizedrepresentative("saas");
        merchant.setClassification("saas");
        merchant.setDesignation("sasa");
        merchant.setFaxnumber("sas");
        merchant.setFranchisephonenumber("sasas");
        merchant.setIndustryclassification("sasas");
        merchant.setNumberofoutlets(Long.MAX_VALUE);
        merchant.setRepresentativename("sasas");
        merchant.setSubscriber(sub);
        merchant.setTradename("sasa");
        merchant.setTypeoforganization("sasa");
        merchant.setWebsite("sasas");
        merchant.setStatustime(new Timestamp());


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

