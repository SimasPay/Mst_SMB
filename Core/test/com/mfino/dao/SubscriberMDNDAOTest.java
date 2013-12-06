/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.hibernate.Timestamp;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sandeepjs
 */
public class SubscriberMDNDAOTest {

    private SubscriberMDNDAO dao = new SubscriberMDNDAO();
    private SubscriberDAO subDao = new SubscriberDAO();
    private MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();
    private static Random random = new Random();

    public SubscriberMDNDAOTest() {
        random.setSeed(System.currentTimeMillis());
    }

    @Before
    public void setUp() {
        insertTestData();
    }

    @After
    public void tearDown() {
    }

    public void insertTestData() {

        mFinoServiceProvider msp = mspDao.getById(1L);

        SubscriberMDN mdn1 = new SubscriberMDN();
        SubscriberMDN mdn2 = new SubscriberMDN();
        SubscriberMDN mdn3 = new SubscriberMDN();

        Subscriber sub = new Subscriber();
        sub.setFirstName("sandeep");
        sub.setCurrency("dollar");
        sub.setActivationTime(new Timestamp());
        sub.setCreateTime(new Timestamp());
        sub.setCreatedBy("sas");
        sub.setEmail("sasa");
        sub.setLanguage(new Integer(0));
        sub.setLastName("sdas");
        sub.setLastUpdateTime(new Timestamp());
        sub.setNotificationMethod(new Integer(0));
        sub.setRestrictions(new Integer(0));
        sub.setStatus(new Integer(0));
        sub.setStatusTime(new Timestamp());
        sub.setTimezone("sas");
        sub.setType(new Integer(0));
        sub.setUpdatedBy("sasa");
        CompanyDAO comp = new CompanyDAO();
        sub.setCompany(comp.getById(1L));
        sub.setmFinoServiceProviderByMSPID(msp);

        subDao.save(sub);

        mdn1.setMDN(getRandomMDN());
        mdn2.setMDN(getRandomMDN());
        mdn3.setMDN(getRandomMDN());

        mdn1.setSubscriber(sub);
        mdn2.setSubscriber(sub);
        mdn3.setSubscriber(sub);

        mdn1.setCreateTime(new Timestamp());
        mdn1.setRestrictions(new Integer(15));
        mdn1.setStatus(new Integer(0));
        mdn1.setActivationTime(new Timestamp());
        mdn1.setAuthenticationPhoneNumber("dsd");
        mdn1.setAuthenticationPhrase("dsd");
        mdn1.setCreatedBy("dsd");
        mdn1.setDigestedPIN("sds");
        mdn1.setLastTransactionID(Long.MIN_VALUE);
        mdn1.setLastTransactionTime(new Timestamp());
        mdn1.setLastUpdateTime(new Timestamp());
        mdn1.setStatusTime(new Timestamp());
        mdn1.setUpdatedBy("dsd");
        mdn1.setWrongPINCount(Integer.MAX_VALUE);

        mdn2.setCreateTime(new Timestamp());
        mdn2.setRestrictions(new Integer(9));
        mdn2.setStatus(new Integer(0));
        mdn2.setActivationTime(new Timestamp());
        mdn2.setAuthenticationPhoneNumber("dsd");
        mdn2.setAuthenticationPhrase("dsd");
        mdn2.setCreatedBy("dsd");
        mdn2.setDigestedPIN("sds");
        mdn2.setLastTransactionID(Long.MIN_VALUE);
        mdn2.setLastTransactionTime(new Timestamp());
        mdn2.setLastUpdateTime(new Timestamp());
        mdn2.setStatusTime(new Timestamp());
        mdn2.setUpdatedBy("dsd");
        mdn2.setWrongPINCount(Integer.MAX_VALUE);

        mdn3.setCreateTime(new Timestamp());
        mdn3.setRestrictions(new Integer(12));
        mdn3.setStatus(new Integer(0));
        mdn3.setActivationTime(new Timestamp());
        mdn3.setAuthenticationPhoneNumber("dsd");
        mdn3.setAuthenticationPhrase("dsd");
        mdn3.setCreatedBy("dsd");
        mdn3.setDigestedPIN("sds");
        mdn3.setLastTransactionID(Long.MIN_VALUE);
        mdn3.setLastTransactionTime(new Timestamp());
        mdn3.setLastUpdateTime(new Timestamp());
        mdn3.setStatusTime(new Timestamp());
        mdn3.setUpdatedBy("dsd");
        mdn3.setWrongPINCount(Integer.MAX_VALUE);

        dao.save(mdn1);
        dao.save(mdn2);
        dao.save(mdn3);

        assertTrue(sub.getID() > 0);
        assertTrue(mdn1.getID() > 0);
        assertTrue(mdn2.getID() > 0);
        assertTrue(mdn3.getID() > 0);
    }

    @Test
    public void searchGetAllTest() {

        SubscriberMdnQuery query = new SubscriberMdnQuery();

        query.setLimit(10);
        query.setStart(0);

        List list = dao.get(query);

        assertTrue(list.size() > 0);
    }

    @Ignore
    @Test
    public void searchTest() {

        SubscriberMdnQuery query = new SubscriberMdnQuery();

        query.setFirstName("sa");
        query.setMdn("934");
        query.setEndRegistrationDate(new Timestamp());

        query.setLimit(10);
        query.setStart(0);

        List list = dao.get(query);

        assertTrue(list.size() > 0);
    }

    @Test
    public void lazyLoadingTest() {

        SubscriberMdnQuery query = new SubscriberMdnQuery();
        List<SubscriberMDN> list = dao.get(query);

        assertTrue(list.size() > 0);

        SubscriberMDN mdn = list.get(0);
        Subscriber sub = mdn.getSubscriber();
        System.out.println(sub.getID());
    }

    private static String getRandomMDN() {
        String mdn = "1111";
        return mdn + (long) (random.nextDouble() * 10000L);
    }
}
