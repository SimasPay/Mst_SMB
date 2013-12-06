/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import com.mfino.dao.query.SubscriberQuery;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Test;

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
    SubscriberMDN mdn1 = new SubscriberMDN();
    SubscriberMDNDAO mdndao = new SubscriberMDNDAO();

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    private void insertData() {
        sub.setFirstName("x");
        sub.setLastName("chen");
        sub.setEmail("xiaohui@mfino.com");
        sub.setType(CmFinoFIX.SubscriberType_Subscriber);
        sub.setStatus(CmFinoFIX.SubscriberStatus_Active);
        sub.setStatusTime(new Timestamp());
        CompanyDAO comp = new CompanyDAO();
        sub.setCompany(comp.getById(1L));
        mdn1.setMDN("9871124086");
        mdn1.setSubscriber(sub);
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

        pocket.setPocketTemplate(pocktTemplateDAO.getById(3L));
        pocket.setSubscriberMDNByMDNID(mdn1);
        pocket.setStatusTime(new Timestamp());

        Set pocketSet = new HashSet();
        pocketSet.add(pocket);

        service.save(sub);
        mdndao.save(mdn1);
        pocketDAO.save(pocket);
    }

    @Test
    public void testGet() {

        insertData();
        assertNotNull(sub.getID());
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
