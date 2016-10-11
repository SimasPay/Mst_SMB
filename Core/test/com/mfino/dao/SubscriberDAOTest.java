/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import com.mfino.dao.query.SubscriberQuery;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;

/**
 *
 * @author xchen
 */
public class SubscriberDAOTest extends TestCase {

    Subscriber sub = new Subscriber();
    SubscriberDAO service = new SubscriberDAO();
    Pocket pocket = new Pocket();
    PocketDAO pocketDAO = new PocketDAO();
    PocketTemplateDAO pocktTemplateDAO = new PocketTemplateDAO();
    PocketTemplate pocketTemplate = new PocketTemplate();
    SubscriberMdn mdn1 = new SubscriberMdn();
    SubscriberMDNDAO mdndao = new SubscriberMDNDAO();

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    private void insertData() {
        sub.setFirstname("x");
        sub.setLastname("chen");
        sub.setEmail("xiaohui@mfino.com");
        sub.setType(CmFinoFIX.SubscriberType_Subscriber);
        sub.setStatus(CmFinoFIX.SubscriberStatus_Active);
        sub.setStatustime(new Timestamp());
        CompanyDAO comp = new CompanyDAO();
        sub.setCompany(comp.getById(1L));
        mdn1.setMdn("9871124086");
        mdn1.setSubscriber(sub);
        mdn1.setCreatetime(new Timestamp());
        mdn1.setRestrictions(new Integer(15));
        mdn1.setStatus(new Integer(0));
        mdn1.setActivationtime(new Timestamp());
        mdn1.setAuthenticationphonenumber("dsd");
        mdn1.setAuthenticationphrase("dsd");
        mdn1.setCreatedby("dsd");
        mdn1.setDigestedpin("sds");
        mdn1.setLasttransactionid(new BigDecimal(Long.MIN_VALUE));
        mdn1.setLasttransactiontime(new Timestamp());
        mdn1.setLastupdatetime(new Timestamp());
        mdn1.setStatustime(new Timestamp());
        mdn1.setUpdatedby("dsd");
        mdn1.setWrongpincount(Integer.MAX_VALUE);

        pocket.setPocketTemplate(pocktTemplateDAO.getById(3L));
        pocket.setSubscriberMdn(mdn1);
        pocket.setStatustime(new Timestamp());

        Set pocketSet = new HashSet();
        pocketSet.add(pocket);

        service.save(sub);
        mdndao.save(mdn1);
        pocketDAO.save(pocket);
    }

    @Test
    public void testGet() {

        insertData();
        assertNotNull(sub.getId());
        SubscriberDAO service = new SubscriberDAO();

        List<Subscriber> results = service.getAll();
        assertTrue(results.size() > 0);
    }

    @Test
    public void testPaging() {

        insertData();
        SubscriberDAO service = new SubscriberDAO();

        SubscriberQuery query = new SubscriberQuery();
        query.setStart(0);
        query.setLimit(1);
        List<Subscriber> results = service.get(query);
        assertTrue(results.size() == 1);
    }
}
