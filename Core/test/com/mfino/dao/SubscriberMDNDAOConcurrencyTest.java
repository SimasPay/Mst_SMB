/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.mFinoServiceProvider;
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


        mFinoServiceProvider msp = new mFinoServiceProvider();

        msp.setCreateTime(new Timestamp());
        msp.setCreatedBy("sas");
        msp.setDescription("sas");
        msp.setLastUpdateTime(new Timestamp());
        msp.setName("saa");
        msp.setUpdatedBy("sas");


        mspDao.save(msp);

        SubscriberMDN mdn = new SubscriberMDN();


        Subscriber sub = new Subscriber();
        sub.setFirstName("sandeep");
        sub.setCurrency("USD");
        sub.setActivationTime(new Timestamp());
        sub.setCreateTime(new Timestamp());
        sub.setCreatedBy("sas");
        sub.setEmail("sasa");
        sub.setLanguage(new Integer(0));
        sub.setLastName("sdas");
        sub.setLastUpdateTime(new Timestamp());
        sub.setNotificationMethod(new Integer(0));
        // sub.setParentID(Long.MIN_VALUE);
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



        mdn.setMDN(getRandomMdn());

        mdn.setSubscriber(sub);

        mdn.setCreateTime(new Timestamp());
        mdn.setRestrictions(new Integer(15));
        mdn.setStatus(new Integer(0));
        mdn.setActivationTime(new Timestamp());
        mdn.setAuthenticationPhoneNumber("dsd");
        mdn.setAuthenticationPhrase("dsd");
        mdn.setCreatedBy("dsd");
        mdn.setDigestedPIN("sds");
        mdn.setLastTransactionID(Long.MIN_VALUE);
        mdn.setLastTransactionTime(new Timestamp());
        mdn.setLastUpdateTime(new Timestamp());
        mdn.setStatusTime(new Timestamp());
        mdn.setUpdatedBy("dsd");
        mdn.setWrongPINCount(Integer.MAX_VALUE);

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

    private boolean isStaleData(SubscriberMDN subMdn) {

        SubscriberMdnQuery query = new SubscriberMdnQuery();

        query.setVersion(subMdn.getVersion());
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

        private SubscriberMDN subMdn;

        public UpdaterThread(SubscriberMDN subMdn, int status) {
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
