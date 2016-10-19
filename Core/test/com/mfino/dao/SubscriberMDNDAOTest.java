/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.hibernate.Timestamp;

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

        MfinoServiceProvider msp = mspDao.getById(1L);

        SubscriberMdn mdn1 = new SubscriberMdn();
        SubscriberMdn mdn2 = new SubscriberMdn();
        SubscriberMdn mdn3 = new SubscriberMdn();

        Subscriber sub = new Subscriber();
        sub.setFirstname("sandeep");
        sub.setCurrency("dollar");
        sub.setActivationtime(new Timestamp());
        sub.setCreatetime(new Timestamp());
        sub.setCreatedby("sas");
        sub.setEmail("sasa");
        sub.setLanguage(new Integer(0));
        sub.setLastname("sdas");
        sub.setLastupdatetime(new Timestamp());
        sub.setNotificationmethod(0);
        sub.setRestrictions(new Integer(0));
        sub.setStatus(new Integer(0));
        sub.setStatustime(new Timestamp());
        sub.setTimezone("sas");
        sub.setType(new Integer(0));
        sub.setUpdatedby("sasa");
        CompanyDAO comp = new CompanyDAO();
        sub.setCompany(comp.getById(1L));
        sub.setMfinoServiceProvider(msp);

        subDao.save(sub);

        mdn1.setMdn(getRandomMDN());
        mdn2.setMdn(getRandomMDN());
        mdn3.setMdn(getRandomMDN());

        mdn1.setSubscriber(sub);
        mdn2.setSubscriber(sub);
        mdn3.setSubscriber(sub);

        mdn1.setCreatetime(new Timestamp());
        mdn1.setRestrictions(new Integer(15));
        mdn1.setStatus(new Integer(0));
        mdn1.setActivationtime(new Timestamp());
        mdn1.setAuthenticationphonenumber("dsd");
        mdn1.setAuthenticationphrase("dsd");
        mdn1.setCreatedby("dsd");
        mdn1.setDigestedpin("sds");
        mdn1.setLasttransactionid(Long.MIN_VALUE);
        mdn1.setLasttransactiontime(new Timestamp());
        mdn1.setLastupdatetime(new Timestamp());
        mdn1.setStatustime(new Timestamp());
        mdn1.setUpdatedby("dsd");
        mdn1.setWrongpincount(Integer.MAX_VALUE);

        mdn2.setCreatetime(new Timestamp());
        mdn2.setRestrictions(new Integer(9));
        mdn2.setStatus(new Integer(0));
        mdn2.setActivationtime(new Timestamp());
        mdn2.setAuthenticationphonenumber("dsd");
        mdn2.setAuthenticationphrase("dsd");
        mdn2.setCreatedby("dsd");
        mdn2.setDigestedpin("sds");
        mdn2.setLasttransactionid(Long.MIN_VALUE);
        mdn2.setLasttransactiontime(new Timestamp());
        mdn2.setLastupdatetime(new Timestamp());
        mdn2.setStatustime(new Timestamp());
        mdn2.setUpdatedby("dsd");
        mdn2.setWrongpincount(Integer.MAX_VALUE);

        mdn3.setCreatetime(new Timestamp());
        mdn3.setRestrictions(new Integer(12));
        mdn3.setStatus(new Integer(0));
        mdn3.setActivationtime(new Timestamp());
        mdn3.setAuthenticationphonenumber("dsd");
        mdn3.setAuthenticationphrase("dsd");
        mdn3.setCreatedby("dsd");
        mdn3.setDigestedpin("sds");
        mdn3.setLasttransactionid(Long.MIN_VALUE);
        mdn3.setLasttransactiontime(new Timestamp());
        mdn3.setLastupdatetime(new Timestamp());
        mdn3.setStatustime(new Timestamp());
        mdn3.setUpdatedby("dsd");
        mdn3.setWrongpincount(Integer.MAX_VALUE);

        dao.save(mdn1);
        dao.save(mdn2);
        dao.save(mdn3);

        assertTrue(sub.getId().longValue() > 0);
        assertTrue(mdn1.getId().longValue() > 0);
        assertTrue(mdn2.getId().longValue() > 0);
        assertTrue(mdn3.getId().longValue() > 0);
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
        List<SubscriberMdn> list = dao.get(query);

        assertTrue(list.size() > 0);

        SubscriberMdn mdn = list.get(0);
        Subscriber sub = mdn.getSubscriber();
        System.out.println(sub.getId());
    }

    private static String getRandomMDN() {
        String mdn = "1111";
        return mdn + (long) (random.nextDouble() * 10000L);
    }
}
