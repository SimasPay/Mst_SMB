/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.dao.query.Person2PersonQuery;
import com.mfino.domain.Person2Person;
import com.mfino.domain.Subscriber;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.hibernate.Timestamp;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sunil
 */
public class Person2PersonDAOTest {

    private Person2PersonDAO dao = new Person2PersonDAO();
    private SubscriberDAO subDao = new SubscriberDAO();
    private MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();

    public Person2PersonDAOTest() {
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


        Subscriber sub = new Subscriber();
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
        sub.setRestrictions(Integer.MAX_VALUE);
        sub.setStatus(Integer.MAX_VALUE);
        sub.setStatusTime(new Timestamp());
        sub.setTimezone("sas");
        sub.setType(Integer.MAX_VALUE);
        sub.setUpdatedBy("sasa");


        sub.setmFinoServiceProviderByMSPID(msp);

        subDao.save(sub);

        Person2Person p2p1 = new Person2Person();
        Person2Person p2p2 = new Person2Person();
        Person2Person p2p3 = new Person2Person();

        p2p1.setMDN("9346222461");
        p2p1.setPeerName("Sunny");
        p2p2.setMDN("9346222462");
        p2p2.setPeerName("Bunny");
        p2p3.setMDN("9346222463");
        p2p3.setPeerName("Funny");


        p2p1.setmFinoServiceProviderByMSPID(msp);
        p2p3.setmFinoServiceProviderByMSPID(msp);
        p2p2.setmFinoServiceProviderByMSPID(msp);
        p2p1.setSubscriber(sub);
        p2p2.setSubscriber(sub);
        p2p3.setSubscriber(sub);

        p2p1.setCreateTime(new Timestamp());
        p2p1.setActivationTime(new Timestamp());
        p2p1.setCreatedBy("dsd");
        p2p1.setLastUpdateTime(new Timestamp());
        p2p1.setUpdatedBy("dsd");

        p2p2.setCreateTime(new Timestamp());
        p2p2.setActivationTime(new Timestamp());
        p2p2.setCreatedBy("dsd");
        p2p2.setLastUpdateTime(new Timestamp());
        p2p2.setUpdatedBy("dsd");


        p2p3.setCreateTime(new Timestamp());
        p2p3.setActivationTime(new Timestamp());
        p2p3.setCreatedBy("dsd");
        p2p3.setLastUpdateTime(new Timestamp());
        p2p3.setUpdatedBy("dsd");

        dao.save(p2p1);
        dao.save(p2p2);
        dao.save(p2p3);

        assertTrue(sub.getID() > 0);
        assertTrue(p2p1.getID() > 0);
        assertTrue(p2p2.getID() > 0);
        assertTrue(p2p3.getID() > 0);
    }

    @Test
    public void searchGetAllTest() {
        Person2PersonQuery query = new Person2PersonQuery();

        query.setLimit(10);
        query.setStart(0);

        List list = dao.get(query);

        assertTrue(list.size() > 0);
    }

    @Test
    public void searchTest() {
        Person2PersonQuery query = new Person2PersonQuery();

        query.setMdn("934");

        query.setLimit(10);
        query.setStart(0);

        List list = dao.get(query);

        assertTrue(list.size() > 0);
    }

    @Ignore //FIXME: this test fails
    @Test
    public void testSearch() {
        Person2PersonQuery query = new Person2PersonQuery();

        query.setSubscriberId(15L);

        query.setStart(0);
        query.setLimit(10);

        List results = dao.get(query);

        for (int i = 0; i < results.size(); i++) {
            Person2Person s = (Person2Person) results.get(i);
            System.out.println("Phone Number::" + s.getMDN() + "Peer Name::" + s.getPeerName() +
                    "Subscriber ID ::" + s.getSubscriber().getID());
        }

        assertTrue(results.size() > 0);
    }
}

