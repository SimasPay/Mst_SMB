/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.hibernate.Timestamp;

/**
 *
 * @author sandeepjs
 */
@Ignore
public class SubscriberMDNDAOConcurrencyTest {

    private SubscriberMDNDAO dao = new SubscriberMDNDAO();
    private SubscriberDAO subDao = new SubscriberDAO();
    private MfinoServiceProviderDAO mspDao = new MfinoServiceProviderDAO();

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public SubscriberMDNDAOConcurrencyTest() {
    }

    public static void main(String args[]) throws Exception {
        SubscriberMDNDAOConcurrencyTest sTest = new SubscriberMDNDAOConcurrencyTest();
        sTest.dotest();
    }

    public void dotest() throws InterruptedException {


        MfinoServiceProvider msp = new MfinoServiceProvider();

        msp.setCreatetime(new Timestamp());
        msp.setCreatedby("sas");
        msp.setDescription("sas");
        msp.setLastupdatetime(new Timestamp());
        msp.setName("saa");
        msp.setUpdatedby("sas");


        mspDao.save(msp);

        SubscriberMdn mdn = new SubscriberMdn();


        Subscriber sub = new Subscriber();
        sub.setFirstname("sandeep");
        sub.setCurrency("USD");
        sub.setActivationtime(new Timestamp());
        sub.setCreatetime(new Timestamp());
        sub.setCreatedby("sas");
        sub.setEmail("sasa");
        sub.setLanguage(new Integer(0));
        sub.setLastname("sdas");
        sub.setLastupdatetime(new Timestamp());
        sub.setNotificationmethod(new Integer(0));
        // sub.setParentID(Long.MIN_VALUE);
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



        mdn.setMdn(getRandomMdn());

        mdn.setSubscriber(sub);

        mdn.setCreatetime(new Timestamp());
        mdn.setRestrictions(new Integer(15));
        mdn.setStatus(new Integer(0));
        mdn.setActivationtime(new Timestamp());
        mdn.setAuthenticationphonenumber("dsd");
        mdn.setAuthenticationphrase("dsd");
        mdn.setCreatedby("dsd");
        mdn.setDigestedpin("sds");
        mdn.setLasttransactionid(Long.MIN_VALUE);
        mdn.setLasttransactiontime(new Timestamp());
        mdn.setLastupdatetime(new Timestamp());
        mdn.setStatustime(new Timestamp());
        mdn.setUpdatedby("dsd");
        mdn.setWrongpincount(Integer.MAX_VALUE);

        dao.save(mdn);

        Thread.sleep(3000);

        log.info("Version before =" + mdn.getVersion());
        
        UpdaterThread t1 = new UpdaterThread(mdn, 1);
        UpdaterThread t2 = new UpdaterThread(mdn, 2);

        t1.start();

        Thread.sleep(2000);

        t2.start();

        Thread.sleep(2000);


    }

    private boolean isStaleData(SubscriberMdn subMdn) {

        SubscriberMdnQuery query = new SubscriberMdnQuery();

        query.setVersion(((Long)subMdn.getVersion()).intValue());
        query.setLimit(10);
        query.setStart(0);

        List list = dao.get(query);

        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    class UpdaterThread extends Thread {

        private SubscriberMdn subMdn;

        public UpdaterThread(SubscriberMdn subMdn, int status) {
            this.subMdn = subMdn;
            this.subMdn.setStatus(new Integer(status));
        }

        public void run() {

            try {

                log.info("Version in " + this.getName() + "  =" + subMdn.getVersion());

                if (!isStaleData(subMdn)) {
                    dao.save(subMdn);
                    log.info(this.getName() + " -> Wins !!");
                } else {
                    log.info(this.getName() + " -> Looses !!");
                }

            } catch (Exception e) {
                log.info(this.getName() + " -> Looses !!");
            }
        }
    }

    public String getRandomMdn() {
        String mdn = "";

        Random rand = new Random();

        rand.setSeed(System.currentTimeMillis());

        double mdnNum = rand.nextDouble() * 10000000000L;

        mdn = mdn + (long) mdnNum;

        return mdn;

    }
}
