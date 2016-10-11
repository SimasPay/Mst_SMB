/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mfino.dao.query.Person2PersonQuery;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Person2Person;
import com.mfino.domain.Subscriber;
import com.mfino.hibernate.Timestamp;

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

        MfinoServiceProvider msp = mspDao.getById(1L);


        Subscriber sub = new Subscriber();
        sub.setFirstname("xxx");
        sub.setCurrency("dollar");
        sub.setActivationtime(new Timestamp());
        sub.setCreatetime(new Timestamp());
        sub.setCreatedby("sas");
        sub.setEmail("sasa");
        sub.setLanguage(Integer.MAX_VALUE);
        sub.setLastname("sdas");
        sub.setLastupdatetime(new Timestamp());
        sub.setNotificationmethod(Long.MAX_VALUE);
        //sub.setParentID(Long.MIN_VALUE);
        sub.setRestrictions(Integer.MAX_VALUE);
        sub.setStatus(Integer.MAX_VALUE);
        sub.setStatustime(new Timestamp());
        sub.setTimezone("sas");
        sub.setType(Integer.MAX_VALUE);
        sub.setUpdatedby("sasa");


        sub.setMfinoServiceProvider(msp);

        subDao.save(sub);

        Person2Person p2p1 = new Person2Person();
        Person2Person p2p2 = new Person2Person();
        Person2Person p2p3 = new Person2Person();

        p2p1.setMdn("9346222461");
        p2p1.setPeername("Sunny");
        p2p2.setMdn("9346222462");
        p2p2.setPeername("Bunny");
        p2p3.setMdn("9346222463");
        p2p3.setPeername("Funny");


        p2p1.setMfinoServiceProvider(msp);
        p2p3.setMfinoServiceProvider(msp);
        p2p2.setMfinoServiceProvider(msp);
        p2p1.setSubscriber(sub);
        p2p2.setSubscriber(sub);
        p2p3.setSubscriber(sub);

        p2p1.setCreatetime(new Timestamp());
        p2p1.setActivationtime(new Timestamp());
        p2p1.setCreatedby("dsd");
        p2p1.setLastupdatetime(new Timestamp());
        p2p1.setUpdatedby("dsd");

        p2p2.setCreatetime(new Timestamp());
        p2p2.setActivationtime(new Timestamp());
        p2p2.setCreatedby("dsd");
        p2p2.setLastupdatetime(new Timestamp());
        p2p2.setUpdatedby("dsd");


        p2p3.setCreatetime(new Timestamp());
        p2p3.setActivationtime(new Timestamp());
        p2p3.setCreatedby("dsd");
        p2p3.setLastupdatetime(new Timestamp());
        p2p3.setUpdatedby("dsd");

        dao.save(p2p1);
        dao.save(p2p2);
        dao.save(p2p3);

        assertTrue(sub.getId().longValue() > 0);
        assertTrue(p2p1.getId().longValue() > 0);
        assertTrue(p2p2.getId().longValue() > 0);
        assertTrue(p2p3.getId().longValue() > 0);
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
            System.out.println("Phone Number::" + s.getMdn() + "Peer Name::" + s.getPeername() +
                    "Subscriber ID ::" + s.getSubscriber().getId());
        }

        assertTrue(results.size() > 0);
    }
}

